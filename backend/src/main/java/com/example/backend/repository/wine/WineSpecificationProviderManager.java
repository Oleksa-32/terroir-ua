package com.example.backend.repository.wine;

import com.example.backend.model.Wine;
import com.example.backend.repository.SpecificationProvider;
import com.example.backend.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WineSpecificationProviderManager implements SpecificationProviderManager {
    private final List<SpecificationProvider<Wine>> providers;

    @Override
    public SpecificationProvider getSpecificationProvider(String key) {
        return providers.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No provider for key=" + key));
    }
}
