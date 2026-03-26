package com.example.spring_ecom.repository.grpc;

import com.example.spring_ecom.grpc.domain.UserProto;
import com.example.spring_ecom.grpc.domain.OrderProto;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User gRPC operations
 * CLIENT side - calls to SERVER
 */
public interface UserGrpcRepository {
    
    Optional<UserProto.User> getUser(Long userId);
    
    boolean validateUser(Long userId);

    Optional<UserProto.UserInfo> getUserProfile(Long userId);

    Optional<UserProto.User> updateProfile(Long userId, String firstName, String lastName, String phone, String dateOfBirth, String avatarUrl);

    boolean changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword);

    List<OrderProto.Order> getUserOrders(Long userId, int page, int size, String statusFilter);

    boolean deleteAccount(Long userId, String password, String reason);
}