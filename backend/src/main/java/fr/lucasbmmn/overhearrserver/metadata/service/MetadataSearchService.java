package fr.lucasbmmn.overhearrserver.metadata.service;

import fr.lucasbmmn.overhearrserver.metadata.domain.SearchType;
import fr.lucasbmmn.overhearrserver.metadata.dto.SearchResponse;

import java.util.List;

public interface MetadataSearchService {
    SearchResponse search(String query, List<SearchType> searchTypes, int pageNumber, int pageSize);
}
