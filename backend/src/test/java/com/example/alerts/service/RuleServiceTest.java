package com.example.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.alerts.dto.RuleResponse;
import com.example.alerts.dto.RuleSaveRequest;
import com.example.alerts.dto.RuleUpdateRequest;
import com.example.alerts.dto.TargetRequest;
import com.example.alerts.exception.BadRequestException;
import com.example.alerts.exception.NotFoundException;
import com.example.alerts.mapper.RuleMapper;
import com.example.alerts.model.AlertRule;
import com.example.alerts.model.ChannelType;
import com.example.alerts.model.EventCategory;
import com.example.alerts.model.NotificationTarget;
import com.example.alerts.model.User;
import com.example.alerts.repository.AlertRuleRepository;
import com.example.alerts.repository.EventCategoryRepository;
import com.example.alerts.repository.NotificationTargetRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleServiceTest {

    private static final Long USER_ID = 10L;
    private static final Long OTHER_USER_ID = 20L;
    private static final String WEBHOOK = "https://hooks.slack.com/services/T000/B000/secret";

    @Mock
    private AlertRuleRepository ruleRepository;
    @Mock
    private EventCategoryRepository categoryRepository;
    @Mock
    private NotificationTargetRepository targetRepository;
    @Mock
    private UserService userService;

    private RuleService ruleService;

    @BeforeEach
    void setUp() {
        RuleMapper mapper = Mappers.getMapper(RuleMapper.class);
        ruleService = new RuleService(mapper, userService, ruleRepository, categoryRepository, targetRepository);
    }

    @Test
    void listReturnsOnlyOwnRules() {
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(ruleRepository.findAllByOwnerIdAndDeletedFalseOrderByNameAsc(USER_ID))
                .thenReturn(List.of(rule(1L, USER_ID, "Earthquakes")));

        List<RuleResponse> rules = ruleService.list();

        assertThat(rules).extracting(RuleResponse::name).containsExactly("Earthquakes");
        assertThat(rules.getFirst().targets().getFirst().destination()).isEqualTo(WEBHOOK);
    }

    @Test
    void listAllMasksSlackWebhooksOfOtherUsers() {
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(ruleRepository.findAllByDeletedFalseOrderByNameAsc())
                .thenReturn(List.of(rule(1L, USER_ID, "Mine"), rule(2L, OTHER_USER_ID, "Theirs")));

        List<RuleResponse> rules = ruleService.listAll();

        assertThat(rules.get(0).targets().getFirst().destination()).isEqualTo(WEBHOOK);
        assertThat(rules.get(1).targets().getFirst().destination()).isEqualTo("https://hooks.slack.com/****");
    }

    @Test
    void loadOwnRule() {
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(userService.isAdmin()).thenReturn(false);
        when(ruleRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(rule(1L, USER_ID, "Mine")));

        assertThat(ruleService.load(1L).name()).isEqualTo("Mine");
    }

    @Test
    void loadOtherUsersRuleAsUserIsNotFound() {
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(userService.isAdmin()).thenReturn(false);
        when(ruleRepository.findByIdAndOwnerIdAndDeletedFalse(2L, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ruleService.load(2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void loadOtherUsersRuleAsAdminIsMasked() {
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(userService.isAdmin()).thenReturn(true);
        when(ruleRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.of(rule(2L, OTHER_USER_ID, "Theirs")));

        RuleResponse response = ruleService.load(2L);

        assertThat(response.targets().getFirst().destination()).isEqualTo("https://hooks.slack.com/****");
    }

    @Test
    void saveCreatesRuleForCurrentUser() {
        EventCategory category = category(5L, "Dummy A");
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(userService.getReference(USER_ID)).thenReturn(user(USER_ID));
        when(categoryRepository.findAllByIdInAndDeletedFalse(Set.of(5L))).thenReturn(List.of(category));
        when(ruleRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RuleResponse response = ruleService.save(new RuleSaveRequest("Quakes", null, Set.of("earthquake"), Set.of(5L),
                List.of(new TargetRequest(ChannelType.EMAIL, "anna@example.com"))));

        ArgumentCaptor<AlertRule> saved = ArgumentCaptor.forClass(AlertRule.class);
        verify(ruleRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getOwner().getId()).isEqualTo(USER_ID);
        assertThat(saved.getValue().isEnabled()).isTrue();
        assertThat(saved.getValue().getTargets()).allMatch(target -> target.getRule() == saved.getValue());
        assertThat(response.categories()).extracting("name").containsExactly("Dummy A");
    }

    @Test
    void saveRejectsUnknownCategory() {
        when(categoryRepository.findAllByIdInAndDeletedFalse(Set.of(99L))).thenReturn(List.of());
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);

        assertThatThrownBy(() -> ruleService.save(new RuleSaveRequest("Quakes", true, Set.of("earthquake"), Set.of(99L),
                List.of(new TargetRequest(ChannelType.EMAIL, "anna@example.com")))))
                .isInstanceOf(BadRequestException.class);
        verify(ruleRepository, never()).saveAndFlush(any());
    }

    @Test
    void saveRejectsInvalidTargets() {
        assertThatThrownBy(() -> ruleService.save(new RuleSaveRequest("Quakes", true, Set.of("earthquake"), null,
                List.of(new TargetRequest(ChannelType.SLACK, "http://internal.example/hook")))))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> ruleService.save(new RuleSaveRequest("Quakes", true, Set.of("earthquake"), null,
                List.of(new TargetRequest(ChannelType.EMAIL, "not-an-email")))))
                .isInstanceOf(BadRequestException.class);
        verify(ruleRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateReplacesTargetsAndPassesClientVersionToJpa() {
        NotificationTarget oldTarget = target(ChannelType.EMAIL, "old@example.com");
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(ruleRepository.existsByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(true);
        when(targetRepository.findAllByRuleIdAndDeletedFalse(1L)).thenReturn(List.of(oldTarget));
        when(userService.getReference(USER_ID)).thenReturn(user(USER_ID));
        when(ruleRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ruleService.update(1L, new RuleUpdateRequest("Renamed", false, Set.of("storm"), null,
                List.of(new TargetRequest(ChannelType.SLACK, WEBHOOK)), 3L));

        ArgumentCaptor<AlertRule> saved = ArgumentCaptor.forClass(AlertRule.class);
        verify(ruleRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getId()).isEqualTo(1L);
        assertThat(saved.getValue().getVersion()).isEqualTo(3L);
        assertThat(saved.getValue().isEnabled()).isFalse();
        assertThat(saved.getValue().getTargets()).extracting(NotificationTarget::getDestination).containsExactly(WEBHOOK);
        assertThat(oldTarget.isDeleted()).isTrue();
    }

    @Test
    void updateOtherUsersRuleIsNotFound() {
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(ruleRepository.existsByIdAndOwnerIdAndDeletedFalse(2L, USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> ruleService.update(2L, new RuleUpdateRequest("x", true, Set.of("x"), null,
                List.of(new TargetRequest(ChannelType.EMAIL, "a@example.com")), 0L)))
                .isInstanceOf(NotFoundException.class);
        verify(ruleRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteMarksRuleAndTargetsAsDeleted() {
        AlertRule rule = rule(1L, USER_ID, "Mine");
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(ruleRepository.findByIdAndOwnerIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(rule));

        ruleService.delete(1L);

        assertThat(rule.isDeleted()).isTrue();
        assertThat(rule.getTargets()).allMatch(NotificationTarget::isDeleted);
        verify(ruleRepository).saveAndFlush(rule);
        verify(ruleRepository, never()).delete(any());
    }

    @Test
    void deleteOtherUsersRuleIsNotFound() {
        when(userService.loadCurrentUserId()).thenReturn(USER_ID);
        when(ruleRepository.findByIdAndOwnerIdAndDeletedFalse(2L, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ruleService.delete(2L)).isInstanceOf(NotFoundException.class);
        verify(ruleRepository, never()).saveAndFlush(any());
    }

    private static AlertRule rule(Long id, Long ownerId, String name) {
        AlertRule rule = new AlertRule();
        rule.setId(id);
        rule.setVersion(0L);
        rule.setName(name);
        rule.setOwner(user(ownerId));
        rule.setKeywords(Set.of("earthquake"));
        rule.addTarget(target(ChannelType.SLACK, WEBHOOK));
        return rule;
    }

    private static User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setDisplayName("User " + id);
        return user;
    }

    private static EventCategory category(Long id, String name) {
        EventCategory category = new EventCategory();
        category.setId(id);
        category.setCode("C" + id);
        category.setName(name);
        return category;
    }

    private static NotificationTarget target(ChannelType channel, String destination) {
        NotificationTarget target = new NotificationTarget();
        target.setChannel(channel);
        target.setDestination(destination);
        return target;
    }
}
