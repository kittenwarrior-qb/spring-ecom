package com.example.spring_ecom.controller.grpc;

import com.example.spring_ecom.grpc.domain.UserProto;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserGrpcMapper {
    
    public UserProto.User toProto(User user) {
        UserProto.User.Builder builder = UserProto.User.newBuilder()
                .setId(user.id())
                .setUsername(user.username())
                .setEmail(user.email())
                .setIsActive(user.isActive());
        
        // Optional fields with defaults
        builder.setFirstName("");
        builder.setLastName("");
        builder.setPhone("");
        builder.setDateOfBirth("");
        builder.setAvatarUrl("");
        builder.setEmailVerified(false);
        builder.setCreatedAt("");
        builder.setUpdatedAt("");
        
        return builder.build();
    }
    
    public User toDomain(UserProto.User userProto) {
        // Implementation for converting proto to domain if needed
        return null; // TODO: Implement if needed
    }
    
    public UserProto.UserInfo toProto(UserInfo userInfo) {
        UserProto.UserInfo.Builder builder = UserProto.UserInfo.newBuilder()
                .setId(userInfo.id())
                .setUserId(userInfo.userId())
                .setFirstName(userInfo.firstName() != null ? userInfo.firstName() : "")
                .setLastName(userInfo.lastName() != null ? userInfo.lastName() : "")
                .setPhoneNumber(userInfo.phoneNumber() != null ? userInfo.phoneNumber() : "")
                .setDateOfBirth(userInfo.dateOfBirth() != null ? userInfo.dateOfBirth().toString() : "")
                .setAvatarUrl(userInfo.avatarUrl() != null ? userInfo.avatarUrl() : "")
                .setAddress(userInfo.address() != null ? userInfo.address() : "")
                .setWard(userInfo.ward() != null ? userInfo.ward() : "")
                .setDistrict(userInfo.district() != null ? userInfo.district() : "")
                .setCity(userInfo.city() != null ? userInfo.city() : "")
                .setPostalCode(userInfo.postalCode() != null ? userInfo.postalCode() : "");
        
        return builder.build();
    }
    
    public UserInfo toDomain(UserProto.UserInfo userInfoProto) {
        return new UserInfo(
                userInfoProto.getId(),
                userInfoProto.getUserId(),
                userInfoProto.getFirstName().isEmpty() ? null : userInfoProto.getFirstName(),
                userInfoProto.getLastName().isEmpty() ? null : userInfoProto.getLastName(),
                userInfoProto.getPhoneNumber().isEmpty() ? null : userInfoProto.getPhoneNumber(),
                userInfoProto.getDateOfBirth().isEmpty() ? null : java.time.LocalDate.parse(userInfoProto.getDateOfBirth()),
                userInfoProto.getAvatarUrl().isEmpty() ? null : userInfoProto.getAvatarUrl(),
                userInfoProto.getAddress().isEmpty() ? null : userInfoProto.getAddress(),
                userInfoProto.getWard().isEmpty() ? null : userInfoProto.getWard(),
                userInfoProto.getDistrict().isEmpty() ? null : userInfoProto.getDistrict(),
                userInfoProto.getCity().isEmpty() ? null : userInfoProto.getCity(),
                userInfoProto.getPostalCode().isEmpty() ? null : userInfoProto.getPostalCode(),
                null, // createdAt
                null, // updatedAt
                null  // deletedAt
        );
    }
}