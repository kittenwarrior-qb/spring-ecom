package com.example.spring_ecom.service.address;

import com.example.spring_ecom.domain.address.Address;
import com.example.spring_ecom.repository.database.address.AddressEntityMapper;
import com.example.spring_ecom.repository.database.address.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressQueryService {
    
    private final AddressRepository repository;
    private final AddressEntityMapper mapper;
    
    protected List<Address> findByUserId(Long userId) {
        return repository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    protected Optional<Address> findByIdAndUserId(Long addressId, Long userId) {
        return repository.findByIdAndUserId(addressId, userId)
                .map(mapper::toDomain);
    }
    
    protected Optional<Address> findDefaultByUserId(Long userId) {
        return repository.findByUserIdAndIsDefaultTrue(userId)
                .map(mapper::toDomain);
    }
}
