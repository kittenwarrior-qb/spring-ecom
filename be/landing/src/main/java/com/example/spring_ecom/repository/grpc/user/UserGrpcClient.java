package com.example.spring_ecom.repository.grpc.user;

import com.example.spring_ecom.grpc.services.UserServiceGrpc;
import com.example.spring_ecom.grpc.services.UserServiceProto.*;
import com.example.spring_ecom.grpc.domain.UserProto;
import com.example.spring_ecom.grpc.domain.OrderProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGrpcClient {
    
    @GrpcClient("core-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    
    public Optional<UserProto.User> getUser(Long userId) {
        try {
            GetUserByIdRequest request = GetUserByIdRequest.newBuilder().setUserId(userId).build();
            GetUserByIdResponse response = userServiceStub.getUserById(request);
            return response.getSuccess() ? Optional.of(response.getUser()) : Optional.empty();
        } catch (Exception ex) {
            log.error("Error calling gRPC GetUserById", ex);
            return Optional.empty();
        }
    }
    
    public boolean validateUser(Long userId) {
        try {
            Optional<UserProto.User> user = getUser(userId);
            return user.isPresent() && user.get().getIsActive();
        } catch (Exception ex) {
            log.error("Error validating user via gRPC", ex);
            return false;
        }
    }

    public Optional<UserProto.UserInfo> getUserProfile(Long userId) {
        try {
            log.info("Calling gRPC GetUserProfile for userId: {}", userId);
            GetUserProfileRequest request = GetUserProfileRequest.newBuilder().setUserId(userId).build();
            GetUserProfileResponse response = userServiceStub.getUserProfile(request);
            return response.getSuccess() ? Optional.of(response.getProfile()) : Optional.empty();
        } catch (Exception ex) {
            log.error("Error calling gRPC GetUserProfile", ex);
            return Optional.empty();
        }
    }

    public Optional<UserProto.User> updateProfile(Long userId, String firstName, String lastName, String phone, String dateOfBirth, String avatarUrl) {
        try {
            log.info("Calling gRPC UpdateProfile for userId: {}", userId);
            UpdateProfileRequest request = UpdateProfileRequest.newBuilder()
                    .setUserId(userId)
                    .setFirstName(firstName != null ? firstName : "")
                    .setLastName(lastName != null ? lastName : "")
                    .setPhone(phone != null ? phone : "")
                    .setDateOfBirth(dateOfBirth != null ? dateOfBirth : "")
                    .setAvatarUrl(avatarUrl != null ? avatarUrl : "")
                    .build();
            UpdateProfileResponse response = userServiceStub.updateProfile(request);
            return response.getSuccess() ? Optional.of(response.getUser()) : Optional.empty();
        } catch (Exception ex) {
            log.error("Error calling gRPC UpdateProfile", ex);
            return Optional.empty();
        }
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        try {
            log.info("Calling gRPC ChangePassword for userId: {}", userId);
            ChangePasswordRequest request = ChangePasswordRequest.newBuilder()
                    .setUserId(userId)
                    .setCurrentPassword(currentPassword != null ? currentPassword : "")
                    .setNewPassword(newPassword != null ? newPassword : "")
                    .setConfirmPassword(confirmPassword != null ? confirmPassword : "")
                    .build();
            ChangePasswordResponse response = userServiceStub.changePassword(request);
            return response.getSuccess();
        } catch (Exception ex) {
            log.error("Error calling gRPC ChangePassword", ex);
            return false;
        }
    }

    public List<OrderProto.Order> getUserOrders(Long userId, int page, int size, String statusFilter) {
        try {
            log.info("Calling gRPC GetUserOrders for userId: {}, page: {}", userId, page);
            GetUserOrdersRequest request = GetUserOrdersRequest.newBuilder()
                    .setUserId(userId)
                    .setPageRequest(com.example.spring_ecom.grpc.common.CommonProto.PageRequest.newBuilder().setPage(page).setSize(size).build())
                    .setStatusFilter(statusFilter != null ? statusFilter : "")
                    .build();
            GetUserOrdersResponse response = userServiceStub.getUserOrders(request);
            return response.getOrdersList();
        } catch (Exception ex) {
            log.error("Error calling gRPC GetUserOrders", ex);
            return List.of();
        }
    }

    public boolean deleteAccount(Long userId, String password, String reason) {
        try {
            log.info("Calling gRPC DeleteAccount for userId: {}", userId);
            DeleteAccountRequest request = DeleteAccountRequest.newBuilder()
                    .setUserId(userId)
                    .setPassword(password != null ? password : "")
                    .setReason(reason != null ? reason : "")
                    .build();
            DeleteAccountResponse response = userServiceStub.deleteAccount(request);
            return response.getSuccess();
        } catch (Exception ex) {
            log.error("Error calling gRPC DeleteAccount", ex);
            return false;
        }
    }
}