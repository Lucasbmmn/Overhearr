package fr.lucasbmmn.overhearrserver.metadata.controller;

import fr.lucasbmmn.overhearrserver.auth.service.JwtService;
import fr.lucasbmmn.overhearrserver.auth.util.TokenExtractor;
import fr.lucasbmmn.overhearrserver.common.dto.PageResponse;
import fr.lucasbmmn.overhearrserver.config.SecurityConfig;
import fr.lucasbmmn.overhearrserver.metadata.dto.SearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.service.MetadataSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.lucasbmmn.overhearrserver.exception.GlobalExceptionHandler;

@WebMvcTest(SearchController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetadataSearchService metadataSearchService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenExtractor tokenExtractor;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void search_Successful() throws Exception {
        SearchResponse searchResponse = new SearchResponse(
                new PageResponse<>(Collections.emptyList(), new PageResponse.PageMetadata(0, 30, 0, 0)),
                new PageResponse<>(Collections.emptyList(), new PageResponse.PageMetadata(0, 30, 0, 0)),
                new PageResponse<>(Collections.emptyList(), new PageResponse.PageMetadata(0, 30, 0, 0))
        );

        when(this.metadataSearchService.search(anyString(), anyList(), anyInt(), anyInt())).thenReturn(searchResponse);

        this.mockMvc.perform(get("/search")
                        .param("q", "test")
                        .param("types", "album", "artist", "track")
                        .param("pageNumber", "0")
                        .param("pageSize", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albums.data").isArray())
                .andExpect(jsonPath("$.artists.data").isArray())
                .andExpect(jsonPath("$.tracks.data").isArray());
    }

    @Test
    @WithMockUser
    void search_QueryTooShort_ReturnsBadRequest() throws Exception {
        this.mockMvc.perform(get("/search")
                        .param("q", "a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void search_MissingQuery_ReturnsBadRequest() throws Exception {
        this.mockMvc.perform(get("/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_Unauthorized_ReturnsUnauthorized() throws Exception {
        this.mockMvc.perform(get("/search")
                        .param("q", "test"))
                .andExpect(status().isUnauthorized());
    }
}
