package com.example.spring_ecom.controller.grpc.user;

import com.example.spring_ecom.controller.grpc.order.OrderGrpcMapper;
import com.example.spring_ecom.grpc.services.UserServiceGrpc;
import com.example.spring_ecom.grpc.services.UserServiceProto.*;
import com.example.spring_ecom.grpc.domain.UserProto;
import com.example.spring_ecom.grpc.domain.OrderProto;
import com.example.spring_ecom.service.user.UserUseCase;
import com.example.spring_ecom.service.order.OrderUseCase;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.example.spring_ecom.domain.order.Order;
import com.example.spring_ecom.domain.order.OrderStatus;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    
    private final UserUseCase userUseCase;
    private final UserGrpcMapper userGrpcMapper;
    private final OrderUseCase orderUseCase;
    private final OrderGrpcMapper orderGrpcMapper;
    
    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> responseObserver) {
        try {
            log.info("gRPC GetUserById called for userId: {}", request.getUserId());
            
            User user = userUseCase.findByUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            UserProto.User userProto = userGrpcMapper.toProto(user);
            
            GetUserByIdResponse response = GetUserByIdResponse.newBuilder()
                    .setUser(userProto)
                    .setSuccess(true)
                    .setMessage("User found")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception ex) {
            log.error("Error in GetUserById gRPC call", ex);
            
            GetUserByIdResponse errorResponse = GetUserByIdResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found: " + ex.getMessage())
                    .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUserProfile(GetUserProfileRequest request, StreamObserver<GetUserProfileResponse> responseObserver) {
        try {
            log.info("gRPC GetUserProfile called for userId: {}", request.getUserId());
            
            UserInfo userInfo = userUseCase.getUserInfo(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Profile not found"));
            
            UserProto.UserInfo userInfoProto = userGrpcMapper.toProto(userInfo);
            
            GetUserProfileResponse response = GetUserProfileResponse.newBuilder()
                    .setProfile(userInfoProto)
                    .setSuccess(true)
                    .setMessage("Profile found")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in GetUserProfile gRPC call", ex);
            GetUserProfileResponse errorResponse = GetUserProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Profile not found: " + ex.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateProfile(UpdateProfileRequest request, StreamObserver<UpdateProfileResponse> responseObserver) {
        try {
            log.info("gRPC UpdateProfile called for userId: {}", request.getUserId());
            
            UserInfo updatedInfo = new UserInfo(
                    null, // id
                    request.getUserId(),
                    request.getFirstName().isEmpty() ? null : request.getFirstName(),
                    request.getLastName().isEmpty() ? null : request.getLastName(),
                    request.getPhone().isEmpty() ? null : request.getPhone(),
                    request.getDateOfBirth().isEmpty() ? null : LocalDate.parse(request.getDateOfBirth()),
                    request.getAvatarUrl().isEmpty() ? null : request.getAvatarUrl(),
                    null, null, null, null, null, null, null, null
            );
            
            userUseCase.updateUserInfo(updatedInfo);
            
            User user = userUseCase.findByUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found after update"));
            
            UserProto.User userProto = userGrpcMapper.toProto(user);
            
            UpdateProfileResponse response = UpdateProfileResponse.newBuilder()
                    .setUser(userProto)
                    .setSuccess(true)
                    .setMessage("Profile updated successfully")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in UpdateProfile gRPC call", ex);
            UpdateProfileResponse errorResponse = UpdateProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Update profile failed: " + ex.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {
        try {
            log.info("gRPC ChangePassword called for userId: {}", request.getUserId());
            userUseCase.changePassword(request.getUserId(), request.getCurrentPassword(), request.getNewPassword(), request.getConfirmPassword());
            
            ChangePasswordResponse response = ChangePasswordResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Password changed successfully")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in ChangePassword gRPC call", ex);
            ChangePasswordResponse errorResponse = ChangePasswordResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Change password failed: " + ex.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUserOrders(GetUserOrdersRequest request, StreamObserver<GetUserOrdersResponse> responseObserver) {
        try {
            log.info("gRPC GetUserOrders called for userId: {}", request.getUserId());
            
            Pageable pageable = PageRequest.of(
                    request.getPageRequest().getPage(),
                    request.getPageRequest().getSize()
            );
            
            Page<Order> orderPage;
            if (request.getStatusFilter().isEmpty()) {
                orderPage = orderUseCase.findByUserId(request.getUserId(), pageable);
            } else {
                OrderStatus status = OrderStatus.valueOf(request.getStatusFilter().toUpperCase());
                orderPage = orderUseCase.findByUserIdAndStatus(request.getUserId(), status, pageable);
            }
            
            GetUserOrdersResponse.Builder responseBuilder = GetUserOrdersResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Orders retrieved");
            
            orderPage.getContent().forEach(order -> {
                OrderProto.Order orderProto = orderGrpcMapper.toProto(order);
                responseBuilder.addOrders(orderProto);
            });
            
            responseBuilder.setPageResponse(
                    com.example.spring_ecom.grpc.common.CommonProto.PageResponse.newBuilder()
                            .setPage(orderPage.getNumber())
                            .setSize(orderPage.getSize())
                            .setTotalElements(orderPage.getTotalElements())
                            .setTotalPages(orderPage.getTotalPages())
                            .setFirst(orderPage.isFirst())
                            .setLast(orderPage.isLast())
                            .build()
            );
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in GetUserOrders gRPC call", ex);
            GetUserOrdersResponse errorResponse = GetUserOrdersResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to retrieve orders: " + ex.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
        try {
            log.info("gRPC DeleteAccount called for userId: {}", request.getUserId());
            boolean success = userUseCase.deleteUser(request.getUserId());
            
            DeleteAccountResponse response = DeleteAccountResponse.newBuilder()
                    .setSuccess(success)
                    .setMessage(success ? "Account deleted successfully" : "Failed to delete account")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in DeleteAccount gRPC call", ex);
            DeleteAccountResponse errorResponse = DeleteAccountResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to delete account: " + ex.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUserPreferences(GetUserPreferencesRequest request, StreamObserver<GetUserPreferencesResponse> responseObserver) {
        GetUserPreferencesResponse response = GetUserPreferencesResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Stub: GetUserPreferences")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUserPreferences(UpdateUserPreferencesRequest request, StreamObserver<UpdateUserPreferencesResponse> responseObserver) {
        UpdateUserPreferencesResponse response = UpdateUserPreferencesResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Stub: UpdateUserPreferences")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserAddresses(GetUserAddressesRequest request, StreamObserver<GetUserAddressesResponse> responseObserver) {
        GetUserAddressesResponse response = GetUserAddressesResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Stub: GetUserAddresses")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addUserAddress(AddUserAddressRequest request, StreamObserver<AddUserAddressResponse> responseObserver) {
        AddUserAddressResponse response = AddUserAddressResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Stub: AddUserAddress")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUserAddress(UpdateUserAddressRequest request, StreamObserver<UpdateUserAddressResponse> responseObserver) {
        UpdateUserAddressResponse response = UpdateUserAddressResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Stub: UpdateUserAddress")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUserAddress(DeleteUserAddressRequest request, StreamObserver<DeleteUserAddressResponse> responseObserver) {
        DeleteUserAddressResponse response = DeleteUserAddressResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Stub: DeleteUserAddress")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void setDefaultAddress(SetDefaultAddressRequest request, StreamObserver<SetDefaultAddressResponse> responseObserver) {
        SetDefaultAddressResponse response = SetDefaultAddressResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Stub: SetDefaultAddress")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}