package com.example.spring_ecom.service.supplier;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.supplier.Supplier;
import com.example.spring_ecom.repository.database.supplier.SupplierEntity;
import com.example.spring_ecom.repository.database.supplier.SupplierEntityMapper;
import com.example.spring_ecom.repository.database.supplier.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierCommandService {

    private final SupplierRepository supplierRepository;
    private final SupplierEntityMapper mapper;

    public Optional<Supplier> create(Supplier supplier) {
        if (supplierRepository.existsByNameAndDeletedAtIsNull(supplier.name())) {
            throw new BaseException(ResponseCode.CONFLICT, "Supplier name already exists");
        }

        SupplierEntity entity = mapper.toEntity(supplier);
        supplierRepository.save(entity);
        log.info("[SUPPLIER] Created supplier: {}", entity.getName());
        return Optional.of(mapper.toDomain(entity));
    }

    public Optional<Supplier> update(Long id, Supplier supplier) {
        SupplierEntity entity = findActiveById(id);
        mapper.update(entity, supplier);
        supplierRepository.save(entity);
        log.info("[SUPPLIER] Updated supplier: {}", entity.getName());
        return Optional.of(mapper.toDomain(entity));
    }

    public void delete(Long id) {
        SupplierEntity entity = findActiveById(id);
        mapper.markAsDeleted(entity, null);
        supplierRepository.save(entity);
        log.info("[SUPPLIER] Deleted supplier: {}", entity.getName());
    }

    private SupplierEntity findActiveById(Long id) {
        return supplierRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Supplier not found"));
    }
}

