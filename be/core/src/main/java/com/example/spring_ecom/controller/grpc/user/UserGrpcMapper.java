package com.example.spring_ecom.controller.grpc.user;

import com.example.spring_ecom.config.MapStructGlobalConfig;
import com.example.spring_ecom.grpc.domain.UserProto;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.Objects;

@Mapper(config = MapStructGlobalConfig.class, imports = Objects.class)
public interface UserGrpcMapper {

    // ========== Domain -> Proto ==========

    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "id", expression = "java(Objects.nonNull(user.id()) ? user.id() : 0L)")
    @Mapping(target = "username", expression = "java(nullToEmpty(user.username()))")
    @Mapping(target = "email", expression = "java(nullToEmpty(user.email()))")
    @Mapping(target = "firstName", constant = "")
    @Mapping(target = "lastName", constant = "")
    @Mapping(target = "phone", constant = "")
    @Mapping(target = "dateOfBirth", constant = "")
    @Mapping(target = "avatarUrl", constant = "")
    @Mapping(target = "isActive", expression = "java(Objects.nonNull(user.isActive()) ? user.isActive() : false)")
    @Mapping(target = "emailVerified", expression = "java(Objects.nonNull(user.isEmailVerified()) ? user.isEmailVerified() : false)")
    @Mapping(target = "createdAt", constant = "")
    @Mapping(target = "updatedAt", constant = "")
    UserProto.User toProto(User user);
    
    @BeanMapping(builder = @Builder(buildMethod = "build"))
    @Mapping(target = "id", expression = "java(Objects.nonNull(userInfo.id()) ? userInfo.id() : 0L)")
    @Mapping(target = "userId", expression = "java(Objects.nonNull(userInfo.userId()) ? userInfo.userId() : 0L)")
    @Mapping(target = "firstName", expression = "java(nullToEmpty(userInfo.firstName()))")
    @Mapping(target = "lastName", expression = "java(nullToEmpty(userInfo.lastName()))")
    @Mapping(target = "phoneNumber", expression = "java(nullToEmpty(userInfo.phoneNumber()))")
    @Mapping(target = "dateOfBirth", expression = "java(Objects.nonNull(userInfo.dateOfBirth()) ? userInfo.dateOfBirth().toString() : \"\")")
    @Mapping(target = "avatarUrl", expression = "java(nullToEmpty(userInfo.avatarUrl()))")
    @Mapping(target = "address", expression = "java(nullToEmpty(userInfo.address()))")
    @Mapping(target = "ward", expression = "java(nullToEmpty(userInfo.ward()))")
    @Mapping(target = "district", expression = "java(nullToEmpty(userInfo.district()))")
    @Mapping(target = "city", expression = "java(nullToEmpty(userInfo.city()))")
    @Mapping(target = "postalCode", expression = "java(nullToEmpty(userInfo.postalCode()))")
    UserProto.UserInfo toProto(UserInfo userInfo);

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
    
    @Mapping(target = "id", expression = "java(zeroToNullLong(proto.getId()))")
    @Mapping(target = "userId", expression = "java(zeroToNullLong(proto.getUserId()))")
    @Mapping(target = "firstName", expression = "java(emptyToNull(proto.getFirstName()))")
    @Mapping(target = "lastName", expression = "java(emptyToNull(proto.getLastName()))")
    @Mapping(target = "phoneNumber", expression = "java(emptyToNull(proto.getPhoneNumber()))")
    @Mapping(target = "dateOfBirth", expression = "java(parseLocalDate(proto.getDateOfBirth()))")
    @Mapping(target = "avatarUrl", expression = "java(emptyToNull(proto.getAvatarUrl()))")
    @Mapping(target = "address", expression = "java(emptyToNull(proto.getAddress()))")
    @Mapping(target = "ward", expression = "java(emptyToNull(proto.getWard()))")
    @Mapping(target = "district", expression = "java(emptyToNull(proto.getDistrict()))")
    @Mapping(target = "city", expression = "java(emptyToNull(proto.getCity()))")
    @Mapping(target = "postalCode", expression = "java(emptyToNull(proto.getPostalCode()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    UserInfo toDomain(UserProto.UserInfo proto);

    // ========== Helper methods ==========
    
    @Named("zeroToNullLong")
    default Long zeroToNullLong(long value) {
        return value == 0 ? null : value;
    }
    
    @Named("emptyToNull")
    default String emptyToNull(String value) {
        return Objects.isNull(value) || value.isEmpty() ? null : value;
    }
    
    @Named("nullToEmpty")
    default String nullToEmpty(String value) {
        return Objects.isNull(value) ? "" : value;
    }
    
    @Named("parseLocalDate")
    default LocalDate parseLocalDate(String value) {
        if (Objects.isNull(value) || value.isEmpty()) return null;
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }
}