package com.example.spring_ecom.repository.grpc;

import com.example.spring_ecom.grpc.UserGrpcClient;
import com.example.spring_ecom.grpc.domain.UserProto;
import com.example.spring_ecom.grpc.domain.OrderProto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserGrpcRepositoryImpl implements UserGrpcRepository {
    
    private final UserGrpcClient userGrpcClient;
    
    @Override
    public Optional<UserProto.User> getUser(Long userId) {
        return userGrpcClient.getUser(userId);
    }
    
    @Override
    public boolean validateUser(Long userId) {
        return userGrpcClient.validateUser(userId);
    }

    @Override
    public Optional<UserProto.UserInfo> getUserProfile(Long userId) {
        return userGrpcClient.getUserProfile(userId);
    }

    @Override
    public Optional<UserProto.User> updateProfile(Long userId, String firstName, String lastName, String phone, String dateOfBirth, String avatarUrl) {
        return userGrpcClient.updateProfile(userId, firstName, lastName, phone, dateOfBirth, avatarUrl);
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        return userGrpcClient.changePassword(userId, currentPassword, newPassword, confirmPassword);
    }

    @Override
    public List<OrderProto.Order> getUserOrders(Long userId, int page, int size, String statusFilter) {
        return userGrpcClient.getUserOrders(userId, page, size, statusFilter);
    }

    @Override
    public boolean deleteAccount(Long userId, String password, String reason) {
        return userGrpcClient.deleteAccount(userId, password, reason);
    }
}