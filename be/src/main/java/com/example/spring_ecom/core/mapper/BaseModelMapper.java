package com.example.spring_ecom.core.mapper;

import org.mapstruct.MappingTarget;
import java.util.List;

// D = request, T = dto domain
public interface BaseModelMapper<D, T> {

    // chuyển dto từ request thành dto domain
    T toDomain(D resDto);

    // ngược lại
    D toResDto(T domain);

    // dùng cho read/get query
    List<T> toDomain(List<D> resDtoList);
    List<D> toResDto(List<T> domainList);

    // Update data từ dto
    void update(@MappingTarget T domain, D resDto);
}