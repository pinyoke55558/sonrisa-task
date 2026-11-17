package com.example.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.alerts.model.AlertRule;
import com.example.alerts.model.Event;
import com.example.alerts.model.EventCategory;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RuleMatcherTest {

    private final RuleMatcher ruleMatcher = new RuleMatcher();

    @Test
    void matchesKeywordInTitleIgnoringCase() {
        assertThat(ruleMatcher.listMatchedKeywords(rule(Set.of("Earthquake")), event("Strong EARTHQUAKE hits coast", null)))
                .containsExactly("Earthquake");
    }

    @Test
    void matchesKeywordInSummary() {
        assertThat(ruleMatcher.listMatchedKeywords(rule(Set.of("tsunami")), event("Breaking news", "A tsunami warning was issued.")))
                .containsExactly("tsunami");
    }

    @Test
    void matchesWholeWordsOnly() {
        assertThat(ruleMatcher.listMatchedKeywords(rule(Set.of("quake")), event("Strong earthquake", null))).isEmpty();
        assertThat(ruleMatcher.listMatchedKeywords(rule(Set.of("quake")), event("Quake, then aftershocks", null)))
                .containsExactly("quake");
    }

    @Test
    void blankKeywordsNeverMatch() {
        assertThat(ruleMatcher.listMatchedKeywords(rule(Set.of(" ")), event("Anything", null))).isEmpty();
    }

    @Test
    void matchesMultiWordKeywordAndReturnsAllMatches() {
        assertThat(ruleMatcher.listMatchedKeywords(
                        rule(Set.of("stock market", "crash", "storm")), event("Stock market crash today", null)))
                .containsExactly("crash", "stock market");
    }

    @Test
    void noKeywordInTextMeansNoMatch() {
        assertThat(ruleMatcher.listMatchedKeywords(rule(Set.of("hurricane")), event("Local football results", "3-1"))).isEmpty();
    }

    @Test
    void ruleWithoutCategoriesMatchesAnyCategory() {
        assertThat(ruleMatcher.listMatchedKeywords(rule(Set.of("quake")), event("Quake", null))).isNotEmpty();
    }

    @Test
    void ruleWithCategoriesMatchesOnlyThoseCategories() {
        AlertRule matchingCategory = rule(Set.of("quake"), category(1L, false));
        AlertRule otherCategory = rule(Set.of("quake"), category(2L, false));

        assertThat(ruleMatcher.listMatchedKeywords(matchingCategory, event("Quake", null))).isNotEmpty();
        assertThat(ruleMatcher.listMatchedKeywords(otherCategory, event("Quake", null))).isEmpty();
    }

    @Test
    void ruleWhoseOnlyCategoryIsDeletedMatchesNothing() {
        assertThat(ruleMatcher.listMatchedKeywords(rule(Set.of("quake"), category(1L, true)), event("Quake", null))).isEmpty();
    }

    private static AlertRule rule(Set<String> keywords, EventCategory... categories) {
        AlertRule rule = new AlertRule();
        rule.setKeywords(new HashSet<>(keywords));
        rule.setCategories(new HashSet<>(Set.of(categories)));
        return rule;
    }

    private static EventCategory category(Long id, boolean deleted) {
        EventCategory category = new EventCategory();
        category.setId(id);
        category.setDeleted(deleted);
        return category;
    }

    private static Event event(String title, String summary) {
        Event event = new Event();
        event.setTitle(title);
        event.setSummary(summary);
        event.setCategory(category(1L, false));
        return event;
    }
}
