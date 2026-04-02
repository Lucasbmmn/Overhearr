package fr.lucasbmmn.overhearrserver.metadata.controller;

import fr.lucasbmmn.overhearrserver.metadata.domain.SearchType;
import fr.lucasbmmn.overhearrserver.metadata.dto.SearchResponse;
import fr.lucasbmmn.overhearrserver.metadata.service.MetadataSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final MetadataSearchService metadataSearchService;

    @GetMapping
    public ResponseEntity<SearchResponse> search(@RequestParam("q") @NotEmpty @Length(min = 2) String query,
                                                 @RequestParam(defaultValue = "album,artist,track") List<SearchType> types,
                                                 @RequestParam(defaultValue = "0") @Min(0) int pageNumber,
                                                 @RequestParam(defaultValue = "30") @Min(1) @Max(100) int pageSize) {
        return ResponseEntity.ok(this.metadataSearchService.search(query, types, pageNumber, pageSize));
    }
}
