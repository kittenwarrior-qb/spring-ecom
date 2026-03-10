package com.example.spring_ecom.service.address;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.address.Address;
import com.example.spring_ecom.domain.address.dto.AddressRequest;
import com.example.spring_ecom.domain.address.dto.AddressResponse;
import com.example.spring_ecom.domain.address.dto.LocationSuggestionResponse;
import com.example.spring_ecom.repository.database.address.AddressEntity;
import com.example.spring_ecom.repository.database.address.AddressEntityMapper;
import com.example.spring_ecom.repository.database.address.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final AddressEntityMapper addressMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream()
                .map(addressMapper::toDomain)
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long addressId, Long userId) {
        AddressEntity entity = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Address not found"));
        return toResponse(addressMapper.toDomain(entity));
    }
    
    @Transactional
    public AddressResponse createAddress(AddressRequest request, Long userId) {
        Address address = new Address(
                null,
                userId,
                request.fullName(),
                request.phoneNumber(),
                request.addressLine(),
                request.ward(),
                request.district(),
                request.city(),
                request.postalCode(),
                false,
                null,
                null,
                null
        );
        
        AddressEntity entity = addressMapper.toEntity(address);
        
        // If this is set as default or user has no addresses, make it default
        if (Boolean.TRUE.equals(request.isDefault()) || addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId).isEmpty()) {
            addressRepository.unsetAllDefaultForUser(userId);
            entity.setIsDefault(true);
        }
        
        AddressEntity saved = addressRepository.save(entity);
        return toResponse(addressMapper.toDomain(saved));
    }
    
    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressRequest request, Long userId) {
        AddressEntity entity = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Address not found"));
        
        entity.setFullName(request.fullName());
        entity.setPhoneNumber(request.phoneNumber());
        entity.setAddressLine(request.addressLine());
        entity.setWard(request.ward());
        entity.setDistrict(request.district());
        entity.setCity(request.city());
        entity.setPostalCode(request.postalCode());
        
        // Handle default address logic
        if (Boolean.TRUE.equals(request.isDefault())) {
            addressRepository.unsetDefaultForUser(userId, addressId);
            entity.setIsDefault(true);
        }
        
        AddressEntity updated = addressRepository.save(entity);
        return toResponse(addressMapper.toDomain(updated));
    }
    
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        AddressEntity entity = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Address not found"));
        
        entity.setDeletedAt(java.time.LocalDateTime.now());
        addressRepository.save(entity);
        
        // If deleted address was default, set another one as default
        if (Boolean.TRUE.equals(entity.getIsDefault())) {
            List<AddressEntity> remainingAddresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
            if (!remainingAddresses.isEmpty()) {
                AddressEntity newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }
    
    @Transactional
    public AddressResponse setDefaultAddress(Long addressId, Long userId) {
        AddressEntity entity = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Address not found"));
        
        addressRepository.unsetDefaultForUser(userId, addressId);
        entity.setIsDefault(true);
        
        AddressEntity updated = addressRepository.save(entity);
        return toResponse(addressMapper.toDomain(updated));
    }
    
    public LocationSuggestionResponse getLocationSuggestion(String ipAddress) {
        try {
            String url = "http://ip-api.com/json/" + (ipAddress != null ? ipAddress : "");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "success".equals(response.get("status"))) {
                return new LocationSuggestionResponse(
                        (String) response.get("city"),
                        (String) response.get("regionName"),
                        (String) response.get("country"),
                        (String) response.get("countryCode"),
                        (String) response.get("timezone")
                );
            }
        } catch (Exception e) {
            log.error("Failed to get location suggestion: {}", e.getMessage());
        }
        
        // Return default Vietnam location if API fails
        return new LocationSuggestionResponse("Hồ Chí Minh", "Hồ Chí Minh", "Vietnam", "VN", "Asia/Ho_Chi_Minh");
    }
    
    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.id(),
                address.fullName(),
                address.phoneNumber(),
                address.addressLine(),
                address.ward(),
                address.district(),
                address.city(),
                address.postalCode(),
                address.isDefault(),
                address.createdAt(),
                address.updatedAt()
        );
    }
}
