package com.example.spring_ecom.controller.api.address;

import com.example.spring_ecom.controller.api.address.model.AddressResponse;
import com.example.spring_ecom.controller.api.address.model.AddressResponseMapper;
import com.example.spring_ecom.controller.api.address.model.CreateAddressRequest;
import com.example.spring_ecom.controller.api.address.model.LocationSuggestionResponse;
import com.example.spring_ecom.controller.api.address.model.UpdateAddressRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.address.Address;
import com.example.spring_ecom.domain.address.model.AddressRequest;
import com.example.spring_ecom.service.address.AddressUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressController implements AddressAPI {
    
    private final AddressUseCase addressUseCase;
    private final AddressResponseMapper responseMapper;
    
    @Override
    public ApiResponse<List<AddressResponse>> getUserAddresses(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Address> addresses = addressUseCase.findByUserId(userId);
        List<AddressResponse> responses = addresses.stream()
                .map(responseMapper::toResDto)
                .toList();
        return ApiResponse.Success.of(responses);
    }
    
    @Override
    public ApiResponse<AddressResponse> getDefaultAddress(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        Address address = addressUseCase.findDefaultByUserId(userId);
        return ApiResponse.Success.of(responseMapper.toResDto(address));
    }
    
    @Override
    public ApiResponse<AddressResponse> getAddressById(Long id, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        Address address = addressUseCase.findByIdAndUserId(id, userId);
        return ApiResponse.Success.of(responseMapper.toResDto(address));
    }
    
    @Override
    public ApiResponse<AddressResponse> createAddress(@Valid CreateAddressRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressRequest addressRequest = new AddressRequest(
                request.fullName(),
                request.phoneNumber(),
                request.addressLine(),
                request.ward(),
                request.district(),
                request.city(),
                request.postalCode(),
                request.isDefault()
        );
        Address address = addressUseCase.create(addressRequest, userId);
        return ApiResponse.Success.of(ResponseCode.CREATED, "Address created successfully", responseMapper.toResDto(address));
    }
    
    @Override
    public ApiResponse<AddressResponse> updateAddress(Long id, @Valid UpdateAddressRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressRequest addressRequest = new AddressRequest(
                request.fullName(),
                request.phoneNumber(),
                request.addressLine(),
                request.ward(),
                request.district(),
                request.city(),
                request.postalCode(),
                request.isDefault()
        );
        Address address = addressUseCase.update(id, addressRequest, userId);
        return ApiResponse.Success.of(ResponseCode.OK, "Address updated successfully", responseMapper.toResDto(address));
    }
    
    @Override
    public ApiResponse<Void> deleteAddress(Long id, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        addressUseCase.delete(id, userId);
        return ApiResponse.Success.of(ResponseCode.OK, "Address deleted successfully");
    }
    
    @Override
    public ApiResponse<AddressResponse> setDefaultAddress(Long id, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        Address address = addressUseCase.setDefault(id, userId);
        return ApiResponse.Success.of(ResponseCode.OK, "Default address updated successfully", responseMapper.toResDto(address));
    }
    
    @Override
    public ApiResponse<LocationSuggestionResponse> getLocationSuggestion(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        com.example.spring_ecom.domain.address.model.LocationSuggestionResponse suggestion = 
                addressUseCase.getLocationSuggestion(ipAddress);
        
        LocationSuggestionResponse response = new LocationSuggestionResponse(
                suggestion.city(),
                suggestion.region(),
                suggestion.country(),
                suggestion.countryCode(),
                suggestion.timezone()
        );
        return ApiResponse.Success.of(response);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
