package com.example.spring_ecom.service.address;

import com.example.spring_ecom.domain.address.Address;
import com.example.spring_ecom.domain.address.model.AddressRequest;
import com.example.spring_ecom.domain.address.model.LocationSuggestionResponse;

import java.util.List;

public interface AddressUseCase {
    
    List<Address> findByUserId(Long userId);
    
    Address findByIdAndUserId(Long addressId, Long userId);
    
    Address findDefaultByUserId(Long userId);
    
    Address create(AddressRequest request, Long userId);
    
    Address update(Long addressId, AddressRequest request, Long userId);
    
    void delete(Long addressId, Long userId);
    
    Address setDefault(Long addressId, Long userId);
    
    LocationSuggestionResponse getLocationSuggestion(String ipAddress);
}
