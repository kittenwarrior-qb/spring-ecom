package com.example.spring_ecom.service.supplier;

import com.example.spring_ecom.domain.supplier.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierUseCaseService implements SupplierUseCase {

    private final SupplierQueryService queryService;
    private final SupplierCommandService commandService;

    @Override
    @Transactional(readOnly = true)
    public Page<Supplier> findAll(String keyword, Boolean isActive, Pageable pageable) {
        return queryService.findAll(keyword, isActive, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Supplier> findById(Long id) {
        return queryService.findById(id);
    }

    @Override
    @Transactional
    public Optional<Supplier> create(Supplier supplier) {
        return commandService.create(supplier);
    }

    @Override
    @Transactional
    public Optional<Supplier> update(Long id, Supplier supplier) {
        return commandService.update(id, supplier);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        commandService.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveSuppliers() {
        return queryService.countActiveSuppliers();
    }
}

