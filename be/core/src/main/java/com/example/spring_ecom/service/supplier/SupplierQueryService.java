package com.example.spring_ecom.service.supplier;

import com.example.spring_ecom.domain.supplier.Supplier;
import com.example.spring_ecom.repository.database.supplier.SupplierEntityMapper;
import com.example.spring_ecom.repository.database.supplier.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierQueryService {

    private final SupplierRepository supplierRepository;
    private final SupplierEntityMapper mapper;

    public Page<Supplier> findAll(String keyword, Boolean isActive, Pageable pageable) {
        return supplierRepository.findSuppliersWithFilters(keyword, isActive, pageable)
                .map(mapper::toDomain);
    }

    public Optional<Supplier> findById(Long id) {
        return supplierRepository.findByIdAndDeletedAtIsNull(id)
                .map(mapper::toDomain);
    }
}

