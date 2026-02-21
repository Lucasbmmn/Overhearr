package fr.lucasbmmn.overhearrserver.auth.constant;

/**
 * Holds constant values used throughout the authentication module.
 */
public class AuthConstants {
    /** The name of the HTTP header used for token-based authorization. */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** The name of the HTTP cookie used to store the access token. */
    public static final String ACCESS_TOKEN_COOKIE_NAME = "overhearr_access_token";

    /** The prefix preceding the token in the Authorization header value. */
    public static final String BEARER_PREFIX = "Bearer ";

    /** The character length of the {@link #BEARER_PREFIX}. */
    public static final int BEARER_PREFIX_LENGTH = 7;
}