package com.example.spring_ecom.controller.api.admin.supplier;

import com.example.spring_ecom.controller.api.admin.supplier.model.CreateSupplierRequest;
import com.example.spring_ecom.controller.api.admin.supplier.model.SupplierResponse;
import com.example.spring_ecom.controller.api.admin.supplier.model.SupplierResponseMapper;
import com.example.spring_ecom.controller.api.admin.supplier.model.UpdateSupplierRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.supplier.Supplier;
import com.example.spring_ecom.service.supplier.SupplierUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminSupplierController implements AdminSupplierAPI {

    private final SupplierUseCase supplierUseCase;
    private final SupplierResponseMapper responseMapper;

    @Override
    public ResponseEntity<ApiResponse<Page<SupplierResponse>>> getAllSuppliers(
            Pageable pageable, String keyword, Boolean isActive) {
        log.info("Admin getting suppliers, keyword={}, isActive={}", keyword, isActive);
        Page<SupplierResponse> response = supplierUseCase.findAll(keyword, isActive, pageable)
                .map(responseMapper::toResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }

    @Override
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(Long id) {
        log.info("Admin getting supplier by ID: {}", id);
        return supplierUseCase.findById(id)
                .map(s -> ResponseEntity.ok(ApiResponse.Success.of(responseMapper.toResponse(s))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(CreateSupplierRequest request) {
        log.info("Admin creating supplier: {}", request.getName());
        Supplier supplier = new Supplier(
                null, request.getName(), request.getContactName(),
                request.getPhone(), request.getEmail(), request.getAddress(),
                request.getNote(),
                Objects.nonNull(request.getIsActive()) ? request.getIsActive() : true,
                null, null, null
        );
        return supplierUseCase.create(supplier)
                .map(created -> ResponseEntity.ok(
                        ApiResponse.Success.of(ResponseCode.CREATED, "Supplier created successfully",
                                responseMapper.toResponse(created))))
                .orElse(ResponseEntity.internalServerError()
                        .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create supplier")));
    }

    @Override
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(Long id, UpdateSupplierRequest request) {
        log.info("Admin updating supplier: {}", id);
        Supplier existing = supplierUseCase.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        Supplier updated = new Supplier(
                existing.id(),
                Objects.nonNull(request.getName()) ? request.getName() : existing.name(),
                Objects.nonNull(request.getContactName()) ? request.getContactName() : existing.contactName(),
                Objects.nonNull(request.getPhone()) ? request.getPhone() : existing.phone(),
                Objects.nonNull(request.getEmail()) ? request.getEmail() : existing.email(),
                Objects.nonNull(request.getAddress()) ? request.getAddress() : existing.address(),
                Objects.nonNull(request.getNote()) ? request.getNote() : existing.note(),
                Objects.nonNull(request.getIsActive()) ? request.getIsActive() : existing.isActive(),
                existing.createdAt(), null, existing.deletedAt()
        );

        return supplierUseCase.update(id, updated)
                .map(result -> ResponseEntity.ok(
                        ApiResponse.Success.of(ResponseCode.OK, "Supplier updated successfully",
                                responseMapper.toResponse(result))))
                .orElse(ResponseEntity.internalServerError()
                        .body(ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to update supplier")));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(Long id) {
        log.info("Admin deleting supplier: {}", id);
        supplierUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Supplier deleted successfully", null));
    }
}

