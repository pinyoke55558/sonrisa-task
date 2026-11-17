package com.example.alerts.service;

import com.example.alerts.model.AlertRule;
import com.example.alerts.model.Event;
import com.example.alerts.model.EventCategory;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class RuleMatcher {

    public Set<String> listMatchedKeywords(AlertRule rule, Event event) {
        if (!matchesCategory(rule, event)) {
            return Set.of();
        }

        String text = event.getTitle() + "\n" + (event.getSummary() == null ? "" : event.getSummary());
        Set<String> matched = new TreeSet<>();
        rule.getKeywords().stream()
                .filter(keyword -> containsWholeWord(text, keyword))
                .forEach(matched::add);
        return matched;
    }

    private static boolean matchesCategory(AlertRule rule, Event event) {
        if (rule.getCategories().isEmpty()) {
            return true;
        }

        Long eventCategoryId = event.getCategory().getId();
        return rule.getCategories().stream()
                .filter(category -> !category.isDeleted())
                .map(EventCategory::getId)
                .anyMatch(eventCategoryId::equals);
    }

    private static boolean containsWholeWord(String text, String keyword) {
        String word = keyword.trim();
        if (word.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(
                "(?<![\\p{L}\\p{N}])" + Pattern.quote(word) + "(?![\\p{L}\\p{N}])",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        return pattern.matcher(text).find();
    }
}
