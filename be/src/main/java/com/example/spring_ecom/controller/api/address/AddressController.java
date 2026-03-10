package com.example.spring_ecom.controller.api.address;

import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.address.dto.AddressRequest;
import com.example.spring_ecom.domain.address.dto.AddressResponse;
import com.example.spring_ecom.domain.address.dto.LocationSuggestionResponse;
import com.example.spring_ecom.service.address.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "Address management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {
    
    private final AddressService addressService;
    
    @GetMapping
    @Operation(summary = "Get all addresses of current user")
    public ApiResponse<List<AddressResponse>> getUserAddresses(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);
        return ApiResponse.Success.of(addresses);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID")
    public ApiResponse<AddressResponse> getAddressById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        AddressResponse address = addressService.getAddressById(id, userId);
        return ApiResponse.Success.of(address);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new address")
    public ApiResponse<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        AddressResponse address = addressService.createAddress(request, userId);
        return ApiResponse.Success.of(ResponseCode.CREATED, "Address created successfully", address);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update address")
    public ApiResponse<AddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        AddressResponse address = addressService.updateAddress(id, request, userId);
        return ApiResponse.Success.of(ResponseCode.OK, "Address updated successfully", address);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete address")
    public ApiResponse<Void> deleteAddress(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        addressService.deleteAddress(id, userId);
        return ApiResponse.Success.of(ResponseCode.OK, "Address deleted successfully");
    }
    
    @PatchMapping("/{id}/set-default")
    @Operation(summary = "Set address as default")
    public ApiResponse<AddressResponse> setDefaultAddress(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        AddressResponse address = addressService.setDefaultAddress(id, userId);
        return ApiResponse.Success.of(ResponseCode.OK, "Default address updated successfully", address);
    }
    
    @GetMapping("/location-suggestion")
    @Operation(summary = "Get location suggestion based on IP address")
    public ApiResponse<LocationSuggestionResponse> getLocationSuggestion(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        LocationSuggestionResponse suggestion = addressService.getLocationSuggestion(ipAddress);
        return ApiResponse.Success.of(suggestion);
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
