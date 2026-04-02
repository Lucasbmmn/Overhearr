package fr.lucasbmmn.overhearrserver.metadata.converter;

import fr.lucasbmmn.overhearrserver.metadata.domain.SearchType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSearchTypeConverter implements Converter<String, SearchType> {

    @Override
    public SearchType convert(String source) {
        try {
            return SearchType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid SearchType: " + source);
        }
    }
}
