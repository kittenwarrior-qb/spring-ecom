package com.example.spring_ecom.service.address;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.address.Address;
import com.example.spring_ecom.repository.database.address.AddressEntity;
import com.example.spring_ecom.repository.database.address.AddressEntityMapper;
import com.example.spring_ecom.repository.database.address.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressCommandService {
    
    private final AddressRepository repository;
    private final AddressEntityMapper mapper;
    
    protected Address save(Address address) {
        AddressEntity entity = mapper.toEntity(address);
        AddressEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    protected Address update(Long addressId, Long userId, Address updatedAddress) {
        AddressEntity entity = repository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Address not found"));
        
        entity.setFullName(updatedAddress.fullName());
        entity.setPhoneNumber(updatedAddress.phoneNumber());
        entity.setAddressLine(updatedAddress.addressLine());
        entity.setWard(updatedAddress.ward());
        entity.setDistrict(updatedAddress.district());
        entity.setCity(updatedAddress.city());
        entity.setPostalCode(updatedAddress.postalCode());
        
        if (updatedAddress.isDefault() != null) {
            entity.setIsDefault(updatedAddress.isDefault());
        }
        
        AddressEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected void softDelete(Long addressId, Long userId) {
        AddressEntity entity = repository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Address not found"));
        
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
        
        // If deleted address was default, set another one as default
        if (Boolean.TRUE.equals(entity.getIsDefault())) {
            List<AddressEntity> remainingAddresses = repository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
            if (!remainingAddresses.isEmpty()) {
                AddressEntity newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                repository.save(newDefault);
            }
        }
    }
    
    protected void unsetDefaultForUser(Long userId, Long exceptAddressId) {
        repository.unsetDefaultForUser(userId, exceptAddressId);
    }
    
    protected void unsetAllDefaultForUser(Long userId) {
        repository.unsetAllDefaultForUser(userId);
    }
}
