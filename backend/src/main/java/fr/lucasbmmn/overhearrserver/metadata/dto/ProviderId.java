package fr.lucasbmmn.overhearrserver.metadata.dto;

import fr.lucasbmmn.overhearrserver.metadata.domain.Provider;
import lombok.NonNull;

public record ProviderId(
        @NonNull
        Provider provider,
        @NonNull
        String id) {
}
