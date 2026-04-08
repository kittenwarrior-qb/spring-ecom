package com.example.spring_ecom.service.supplier;

import com.example.spring_ecom.domain.supplier.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SupplierUseCase {

    Page<Supplier> findAll(String keyword, Boolean isActive, Pageable pageable);

    Optional<Supplier> findById(Long id);

    Optional<Supplier> create(Supplier supplier);

    Optional<Supplier> update(Long id, Supplier supplier);

    void delete(Long id);
}

