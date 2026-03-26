package com.example.spring_ecom.repository.grpc.user;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.grpc.domain.UserProto;
import com.example.spring_ecom.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapStructGlobalConfig.class)
public interface UserGrpcMapper {

    // ========== Proto -> Domain ==========
    
    @Mapping(target = "id", expression = "java(zeroToNullLong(proto.getId()))")
    @Mapping(target = "username", expression = "java(emptyToNull(proto.getUsername()))")
    @Mapping(target = "email", expression = "java(emptyToNull(proto.getEmail()))")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isEmailVerified", expression = "java(proto.getEmailVerified())")
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "emailVerificationTokenExpiry", ignore = true)
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetTokenExpiry", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "isActive", expression = "java(proto.getIsActive())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    User toDomain(UserProto.User proto);

    // ========== Domain -> Proto ==========
    // Proto uses builder pattern, use default method with manual builder
    
    default UserProto.User toProto(User user) {
        if (user == null) return null;
        
        return UserProto.User.newBuilder()
                .setId(user.id() != null ? user.id() : 0L)
                .setUsername(nullToEmpty(user.username()))
                .setEmail(nullToEmpty(user.email()))
                .setFirstName("")
                .setLastName("")
                .setPhone("")
                .setDateOfBirth("")
                .setAvatarUrl("")
                .setIsActive(user.isActive() != null ? user.isActive() : false)
                .setEmailVerified(user.isEmailVerified() != null ? user.isEmailVerified() : false)
                .build();
    }

    // ========== Helper methods ==========
    
    @Named("zeroToNullLong")
    default Long zeroToNullLong(long value) {
        return value == 0 ? null : value;
    }
    
    @Named("emptyToNull")
    default String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
    
    @Named("nullToEmpty")
    default String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
