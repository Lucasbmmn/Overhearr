package fr.lucasbmmn.overhearrserver.auth.util;

import fr.lucasbmmn.overhearrserver.auth.constant.AuthConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenExtractorTest {

    @Mock
    private HttpServletRequest request;

    private final TokenExtractor tokenExtractor = new TokenExtractor();

    @Test
    void extract_FromHeader_Success() {
        when(request.getHeader(AuthConstants.AUTHORIZATION_HEADER))
                .thenReturn("Bearer header.token");

        String token = tokenExtractor.extract(request);
        assertEquals("header.token", token);
    }

    @Test
    void extract_FromCookie_Success() {
        when(request.getHeader(AuthConstants.AUTHORIZATION_HEADER)).thenReturn(null);

        Cookie cookie = new Cookie(AuthConstants.ACCESS_TOKEN_COOKIE_NAME, "cookie.token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String token = tokenExtractor.extract(request);
        assertEquals("cookie.token", token);
    }

    @Test
    void extract_NoToken_ReturnsNull() {
        when(request.getHeader(AuthConstants.AUTHORIZATION_HEADER)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        assertNull(tokenExtractor.extract(request));
    }
}