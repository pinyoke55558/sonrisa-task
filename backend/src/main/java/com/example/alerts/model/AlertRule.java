package com.example.alerts.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/** A user's definition of what they want to be notified about. */
@Getter
@Setter
@Entity
@Table(name = "alert_rule")
public class AlertRule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean enabled = true;

    /** At least one keyword; matched against an event's title and summary. */
    @ElementCollection
    @CollectionTable(name = "alert_rule_keyword", joinColumns = @JoinColumn(name = "rule_id"))
    @Column(name = "keyword", nullable = false, length = 100)
    private Set<String> keywords = new HashSet<>();

    /** Optional filter; empty means any non-deleted category. */
    @ManyToMany
    @JoinTable(
            name = "alert_rule_category",
            joinColumns = @JoinColumn(name = "rule_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<EventCategory> categories = new HashSet<>();

    /**
     * Targets belong to the rule. No orphan removal: targets are deleted logically together with
     * the rule, because stored notifications still refer to them.
     */
    @OneToMany(mappedBy = "rule", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<NotificationTarget> targets = new ArrayList<>();

    public void addTarget(NotificationTarget target) {
        target.setRule(this);
        targets.add(target);
    }
}
