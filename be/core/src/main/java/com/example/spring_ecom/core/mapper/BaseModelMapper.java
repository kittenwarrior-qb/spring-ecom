package com.example.spring_ecom.core.mapper;

import org.mapstruct.MappingTarget;
import java.util.List;

// D = request, T = dto domain
public interface BaseModelMapper<D, T> {

    T toDomain(D response);

    D toResponse(T domain);

    List<T> toDomain(List<D> responseList);
    List<D> toResponse(List<T> domainList);

    // Update data từ domain
    void update(@MappingTarget T domain, D response);
}