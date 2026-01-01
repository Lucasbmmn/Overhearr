package fr.lucasbmmn.overhearrserver.user.model;

/**
 * Enumeration defining the available roles within the application.
 * Used for authorization and access control logic.
 */
public enum UserRole {
    /**
     * Standard user with basic access privileges.
     */
    USER,

    /**
     * Administrator with elevated privileges (e.g., user management, configuration).
     */
    ADMIN,
}
