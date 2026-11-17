package com.example.alerts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Local reference to a user signed on through Keycloak. Created on first sign-on; this is not
 * user management.
 */
@Getter
@Setter
@Entity
@Table(name = "app_user")
public class User extends BaseEntity {

    /** Keycloak user ID (the token's {@code sub} claim). */
    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(length = 320)
    private String email;

    @Column(name = "display_name")
    private String displayName;
}
