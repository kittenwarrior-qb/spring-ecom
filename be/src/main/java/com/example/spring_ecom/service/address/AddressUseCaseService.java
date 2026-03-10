package com.example.spring_ecom.service.address;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.address.Address;
import com.example.spring_ecom.domain.address.dto.AddressRequest;
import com.example.spring_ecom.domain.address.dto.LocationSuggestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressUseCaseService implements AddressUseCase {
    
    private final AddressQueryService queryService;
    private final AddressCommandService commandService;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    @Transactional(readOnly = true)
    public List<Address> findByUserId(Long userId) {
        return queryService.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Address findByIdAndUserId(Long addressId, Long userId) {
        return queryService.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Address not found"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Address findDefaultByUserId(Long userId) {
        return queryService.findDefaultByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "No default address found"));
    }
    
    @Override
    @Transactional
    public Address create(AddressRequest request, Long userId) {
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
        boolean shouldBeDefault = Boolean.TRUE.equals(request.isDefault()) || 
                                  queryService.findByUserId(userId).isEmpty();
        
        if (shouldBeDefault) {
            commandService.unsetAllDefaultForUser(userId);
            address = new Address(
                    address.id(),
                    address.userId(),
                    address.fullName(),
                    address.phoneNumber(),
                    address.addressLine(),
                    address.ward(),
                    address.district(),
                    address.city(),
                    address.postalCode(),
                    true,
                    address.createdAt(),
                    address.updatedAt(),
                    address.deletedAt()
            );
        }
        return commandService.save(address);
    }
    
    @Override
    @Transactional
    public Address update(Long addressId, AddressRequest request, Long userId) {
        Address existing = findByIdAndUserId(addressId, userId);
        
        Address updated = new Address(
                existing.id(),
                existing.userId(),
                request.fullName(),
                request.phoneNumber(),
                request.addressLine(),
                request.ward(),
                request.district(),
                request.city(),
                request.postalCode(),
                existing.isDefault(),
                existing.createdAt(),
                existing.updatedAt(),
                existing.deletedAt()
        );
        
        // Handle default address logic
        if (Boolean.TRUE.equals(request.isDefault()) && !Boolean.TRUE.equals(existing.isDefault())) {
            commandService.unsetDefaultForUser(userId, addressId);
            updated = new Address(
                    updated.id(),
                    updated.userId(),
                    updated.fullName(),
                    updated.phoneNumber(),
                    updated.addressLine(),
                    updated.ward(),
                    updated.district(),
                    updated.city(),
                    updated.postalCode(),
                    true,
                    updated.createdAt(),
                    updated.updatedAt(),
                    updated.deletedAt()
            );
        }
        
        return commandService.update(addressId, userId, updated);
    }
    
    @Override
    @Transactional
    public void delete(Long addressId, Long userId) {
        commandService.softDelete(addressId, userId);
    }
    
    @Override
    @Transactional
    public Address setDefault(Long addressId, Long userId) {
        Address address = findByIdAndUserId(addressId, userId);
        
        commandService.unsetDefaultForUser(userId, addressId);
        
        Address updated = new Address(
                address.id(),
                address.userId(),
                address.fullName(),
                address.phoneNumber(),
                address.addressLine(),
                address.ward(),
                address.district(),
                address.city(),
                address.postalCode(),
                true,
                address.createdAt(),
                address.updatedAt(),
                address.deletedAt()
        );
        
        return commandService.update(addressId, userId, updated);
    }
    
    @Override
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
}
