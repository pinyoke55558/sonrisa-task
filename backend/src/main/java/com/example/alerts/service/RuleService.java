package com.example.alerts.service;

import com.example.alerts.dto.RuleResponse;
import com.example.alerts.dto.RuleSaveRequest;
import com.example.alerts.dto.RuleUpdateRequest;
import com.example.alerts.dto.TargetRequest;
import com.example.alerts.dto.TargetResponse;
import com.example.alerts.exception.BadRequestException;
import com.example.alerts.exception.NotFoundException;
import com.example.alerts.mapper.RuleMapper;
import com.example.alerts.model.AlertRule;
import com.example.alerts.model.ChannelType;
import com.example.alerts.model.EventCategory;
import com.example.alerts.repository.AlertRuleRepository;
import com.example.alerts.repository.EventCategoryRepository;
import com.example.alerts.repository.NotificationTargetRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RuleService {

    private static final String RULE = "Rule";
    private static final String SLACK_WEBHOOK_PREFIX = "https://hooks.slack.com/";
    private static final String MASKED_SLACK_WEBHOOK = SLACK_WEBHOOK_PREFIX + "****";
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final RuleMapper ruleMapper;
    private final UserService userService;
    private final AlertRuleRepository ruleRepository;
    private final EventCategoryRepository categoryRepository;
    private final NotificationTargetRepository targetRepository;

    public List<RuleResponse> list() {
        return ruleRepository.findAllByOwnerIdAndDeletedFalseOrderByNameAsc(userService.loadCurrentUserId()).stream()
                .map(ruleMapper::toResponse)
                .toList();
    }

    public List<RuleResponse> listAll() {
        Long userId = userService.loadCurrentUserId();
        return ruleRepository.findAllByDeletedFalseOrderByNameAsc().stream()
                .map(rule -> toResponse(rule, userId))
                .toList();
    }

    public List<AlertRule> listMatchable() {
        return ruleRepository.findAllByEnabledTrueAndDeletedFalse();
    }

    public RuleResponse load(Long id) {
        Long userId = userService.loadCurrentUserId();
        return (userService.isAdmin()
                        ? ruleRepository.findByIdAndDeletedFalse(id)
                        : ruleRepository.findByIdAndOwnerIdAndDeletedFalse(id, userId))
                .map(rule -> toResponse(rule, userId))
                .orElseThrow(() -> new NotFoundException(RULE, id));
    }

    @Transactional
    public RuleResponse save(RuleSaveRequest request) {
        validateTargets(request.targets());
        AlertRule rule = ruleMapper.toEntity(request);
        rule.setOwner(userService.getReference(userService.loadCurrentUserId()));
        rule.setCategories(resolveCategories(request.categoryIds()));
        request.targets().stream().map(ruleMapper::toTarget).forEach(rule::addTarget);

        return ruleMapper.toResponse(ruleRepository.saveAndFlush(rule));
    }

    @Transactional
    public RuleResponse update(Long id, RuleUpdateRequest request) {
        Long userId = userService.loadCurrentUserId();
        if (!ruleRepository.existsByIdAndOwnerIdAndDeletedFalse(id, userId)) {
            throw new NotFoundException(RULE, id);
        }

        validateTargets(request.targets());
        Set<EventCategory> categories = resolveCategories(request.categoryIds());
        targetRepository.findAllByRuleIdAndDeletedFalse(id).forEach(target -> target.setDeleted(true));

        AlertRule rule = ruleMapper.toEntity(id, request);
        rule.setOwner(userService.getReference(userId));
        rule.setCategories(categories);
        request.targets().stream().map(ruleMapper::toTarget).forEach(rule::addTarget);

        return ruleMapper.toResponse(ruleRepository.saveAndFlush(rule));
    }

    @Transactional
    public void delete(Long id) {
        AlertRule rule = ruleRepository.findByIdAndOwnerIdAndDeletedFalse(id, userService.loadCurrentUserId())
                .orElseThrow(() -> new NotFoundException(RULE, id));

        rule.setDeleted(true);
        rule.getTargets().forEach(target -> target.setDeleted(true));
        ruleRepository.saveAndFlush(rule);
    }

    private RuleResponse toResponse(AlertRule rule, Long userId) {
        RuleResponse response = ruleMapper.toResponse(rule);
        if (rule.getOwner().getId().equals(userId)) {
            return response;
        }

        List<TargetResponse> maskedTargets = response.targets().stream()
                .map(target -> target.channel() == ChannelType.SLACK
                        ? new TargetResponse(target.id(), target.channel(), MASKED_SLACK_WEBHOOK)
                        : target)
                .toList();
        return new RuleResponse(response.id(), response.name(), response.enabled(), response.keywords(),
                response.categories(), maskedTargets, response.owner(), response.version());
    }

    private Set<EventCategory> resolveCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }

        List<EventCategory> categories = categoryRepository.findAllByIdInAndDeletedFalse(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new BadRequestException("Unknown or deleted category in " + categoryIds + ".");
        }
        return new HashSet<>(categories);
    }

    private static void validateTargets(List<TargetRequest> targets) {
        targets.forEach(target -> {
            boolean valid = switch (target.channel()) {
                case EMAIL -> EMAIL.matcher(target.destination()).matches();
                case SLACK -> target.destination().startsWith(SLACK_WEBHOOK_PREFIX);
            };
            if (!valid) {
                throw new BadRequestException("Invalid " + target.channel() + " target destination.");
            }
        });
    }
}
