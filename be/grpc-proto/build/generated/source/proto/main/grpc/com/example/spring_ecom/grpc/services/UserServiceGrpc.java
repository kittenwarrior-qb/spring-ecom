package com.example.spring_ecom.grpc.services;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: services/user_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserServiceGrpc {

  private UserServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "services.UserService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse> getGetUserByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserById",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse> getGetUserByIdMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse> getGetUserByIdMethod;
    if ((getGetUserByIdMethod = UserServiceGrpc.getGetUserByIdMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserByIdMethod = UserServiceGrpc.getGetUserByIdMethod) == null) {
          UserServiceGrpc.getGetUserByIdMethod = getGetUserByIdMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetUserById"))
              .build();
        }
      }
    }
    return getGetUserByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse> getGetUserProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserProfile",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse> getGetUserProfileMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse> getGetUserProfileMethod;
    if ((getGetUserProfileMethod = UserServiceGrpc.getGetUserProfileMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserProfileMethod = UserServiceGrpc.getGetUserProfileMethod) == null) {
          UserServiceGrpc.getGetUserProfileMethod = getGetUserProfileMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetUserProfile"))
              .build();
        }
      }
    }
    return getGetUserProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse> getUpdateProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateProfile",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse> getUpdateProfileMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest, com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse> getUpdateProfileMethod;
    if ((getUpdateProfileMethod = UserServiceGrpc.getUpdateProfileMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getUpdateProfileMethod = UserServiceGrpc.getUpdateProfileMethod) == null) {
          UserServiceGrpc.getUpdateProfileMethod = getUpdateProfileMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest, com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("UpdateProfile"))
              .build();
        }
      }
    }
    return getUpdateProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse> getChangePasswordMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ChangePassword",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse> getChangePasswordMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest, com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse> getChangePasswordMethod;
    if ((getChangePasswordMethod = UserServiceGrpc.getChangePasswordMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getChangePasswordMethod = UserServiceGrpc.getChangePasswordMethod) == null) {
          UserServiceGrpc.getChangePasswordMethod = getChangePasswordMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest, com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ChangePassword"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("ChangePassword"))
              .build();
        }
      }
    }
    return getChangePasswordMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse> getGetUserOrdersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserOrders",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse> getGetUserOrdersMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse> getGetUserOrdersMethod;
    if ((getGetUserOrdersMethod = UserServiceGrpc.getGetUserOrdersMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserOrdersMethod = UserServiceGrpc.getGetUserOrdersMethod) == null) {
          UserServiceGrpc.getGetUserOrdersMethod = getGetUserOrdersMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserOrders"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetUserOrders"))
              .build();
        }
      }
    }
    return getGetUserOrdersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse> getDeleteAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteAccount",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse> getDeleteAccountMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest, com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse> getDeleteAccountMethod;
    if ((getDeleteAccountMethod = UserServiceGrpc.getDeleteAccountMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getDeleteAccountMethod = UserServiceGrpc.getDeleteAccountMethod) == null) {
          UserServiceGrpc.getDeleteAccountMethod = getDeleteAccountMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest, com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("DeleteAccount"))
              .build();
        }
      }
    }
    return getDeleteAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse> getGetUserPreferencesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserPreferences",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse> getGetUserPreferencesMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse> getGetUserPreferencesMethod;
    if ((getGetUserPreferencesMethod = UserServiceGrpc.getGetUserPreferencesMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserPreferencesMethod = UserServiceGrpc.getGetUserPreferencesMethod) == null) {
          UserServiceGrpc.getGetUserPreferencesMethod = getGetUserPreferencesMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserPreferences"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetUserPreferences"))
              .build();
        }
      }
    }
    return getGetUserPreferencesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse> getUpdateUserPreferencesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateUserPreferences",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse> getUpdateUserPreferencesMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest, com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse> getUpdateUserPreferencesMethod;
    if ((getUpdateUserPreferencesMethod = UserServiceGrpc.getUpdateUserPreferencesMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getUpdateUserPreferencesMethod = UserServiceGrpc.getUpdateUserPreferencesMethod) == null) {
          UserServiceGrpc.getUpdateUserPreferencesMethod = getUpdateUserPreferencesMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest, com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateUserPreferences"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("UpdateUserPreferences"))
              .build();
        }
      }
    }
    return getUpdateUserPreferencesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse> getGetUserAddressesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserAddresses",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse> getGetUserAddressesMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse> getGetUserAddressesMethod;
    if ((getGetUserAddressesMethod = UserServiceGrpc.getGetUserAddressesMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserAddressesMethod = UserServiceGrpc.getGetUserAddressesMethod) == null) {
          UserServiceGrpc.getGetUserAddressesMethod = getGetUserAddressesMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest, com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserAddresses"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetUserAddresses"))
              .build();
        }
      }
    }
    return getGetUserAddressesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse> getAddUserAddressMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddUserAddress",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse> getAddUserAddressMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest, com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse> getAddUserAddressMethod;
    if ((getAddUserAddressMethod = UserServiceGrpc.getAddUserAddressMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getAddUserAddressMethod = UserServiceGrpc.getAddUserAddressMethod) == null) {
          UserServiceGrpc.getAddUserAddressMethod = getAddUserAddressMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest, com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddUserAddress"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("AddUserAddress"))
              .build();
        }
      }
    }
    return getAddUserAddressMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse> getUpdateUserAddressMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateUserAddress",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse> getUpdateUserAddressMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest, com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse> getUpdateUserAddressMethod;
    if ((getUpdateUserAddressMethod = UserServiceGrpc.getUpdateUserAddressMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getUpdateUserAddressMethod = UserServiceGrpc.getUpdateUserAddressMethod) == null) {
          UserServiceGrpc.getUpdateUserAddressMethod = getUpdateUserAddressMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest, com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateUserAddress"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("UpdateUserAddress"))
              .build();
        }
      }
    }
    return getUpdateUserAddressMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse> getDeleteUserAddressMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteUserAddress",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse> getDeleteUserAddressMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest, com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse> getDeleteUserAddressMethod;
    if ((getDeleteUserAddressMethod = UserServiceGrpc.getDeleteUserAddressMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getDeleteUserAddressMethod = UserServiceGrpc.getDeleteUserAddressMethod) == null) {
          UserServiceGrpc.getDeleteUserAddressMethod = getDeleteUserAddressMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest, com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteUserAddress"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("DeleteUserAddress"))
              .build();
        }
      }
    }
    return getDeleteUserAddressMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse> getSetDefaultAddressMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetDefaultAddress",
      requestType = com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest.class,
      responseType = com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest,
      com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse> getSetDefaultAddressMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest, com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse> getSetDefaultAddressMethod;
    if ((getSetDefaultAddressMethod = UserServiceGrpc.getSetDefaultAddressMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getSetDefaultAddressMethod = UserServiceGrpc.getSetDefaultAddressMethod) == null) {
          UserServiceGrpc.getSetDefaultAddressMethod = getSetDefaultAddressMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest, com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetDefaultAddress"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("SetDefaultAddress"))
              .build();
        }
      }
    }
    return getSetDefaultAddressMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceStub>() {
        @java.lang.Override
        public UserServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceStub(channel, callOptions);
        }
      };
    return UserServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub>() {
        @java.lang.Override
        public UserServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceBlockingStub(channel, callOptions);
        }
      };
    return UserServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub>() {
        @java.lang.Override
        public UserServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceFutureStub(channel, callOptions);
        }
      };
    return UserServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Basic user operations
     * </pre>
     */
    default void getUserById(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserByIdMethod(), responseObserver);
    }

    /**
     */
    default void getUserProfile(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserProfileMethod(), responseObserver);
    }

    /**
     */
    default void updateProfile(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateProfileMethod(), responseObserver);
    }

    /**
     */
    default void changePassword(com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getChangePasswordMethod(), responseObserver);
    }

    /**
     */
    default void getUserOrders(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserOrdersMethod(), responseObserver);
    }

    /**
     */
    default void deleteAccount(com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteAccountMethod(), responseObserver);
    }

    /**
     * <pre>
     * User preferences
     * </pre>
     */
    default void getUserPreferences(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserPreferencesMethod(), responseObserver);
    }

    /**
     */
    default void updateUserPreferences(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateUserPreferencesMethod(), responseObserver);
    }

    /**
     * <pre>
     * User addresses
     * </pre>
     */
    default void getUserAddresses(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserAddressesMethod(), responseObserver);
    }

    /**
     */
    default void addUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddUserAddressMethod(), responseObserver);
    }

    /**
     */
    default void updateUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateUserAddressMethod(), responseObserver);
    }

    /**
     */
    default void deleteUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteUserAddressMethod(), responseObserver);
    }

    /**
     */
    default void setDefaultAddress(com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSetDefaultAddressMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service UserService.
   */
  public static abstract class UserServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return UserServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service UserService.
   */
  public static final class UserServiceStub
      extends io.grpc.stub.AbstractAsyncStub<UserServiceStub> {
    private UserServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Basic user operations
     * </pre>
     */
    public void getUserById(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getUserProfile(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateProfile(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void changePassword(com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getChangePasswordMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getUserOrders(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserOrdersMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteAccount(com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * User preferences
     * </pre>
     */
    public void getUserPreferences(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserPreferencesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateUserPreferences(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateUserPreferencesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * User addresses
     * </pre>
     */
    public void getUserAddresses(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserAddressesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddUserAddressMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateUserAddressMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteUserAddressMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setDefaultAddress(com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSetDefaultAddressMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service UserService.
   */
  public static final class UserServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<UserServiceBlockingStub> {
    private UserServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Basic user operations
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse getUserById(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserByIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse getUserProfile(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserProfileMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse updateProfile(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateProfileMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse changePassword(com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getChangePasswordMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse getUserOrders(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserOrdersMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse deleteAccount(com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteAccountMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * User preferences
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse getUserPreferences(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserPreferencesMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse updateUserPreferences(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateUserPreferencesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * User addresses
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse getUserAddresses(com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserAddressesMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse addUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddUserAddressMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse updateUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateUserAddressMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse deleteUserAddress(com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteUserAddressMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse setDefaultAddress(com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSetDefaultAddressMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service UserService.
   */
  public static final class UserServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<UserServiceFutureStub> {
    private UserServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Basic user operations
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse> getUserById(
        com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse> getUserProfile(
        com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserProfileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse> updateProfile(
        com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateProfileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse> changePassword(
        com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getChangePasswordMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse> getUserOrders(
        com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserOrdersMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse> deleteAccount(
        com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteAccountMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * User preferences
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse> getUserPreferences(
        com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserPreferencesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse> updateUserPreferences(
        com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateUserPreferencesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * User addresses
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse> getUserAddresses(
        com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserAddressesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse> addUserAddress(
        com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddUserAddressMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse> updateUserAddress(
        com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateUserAddressMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse> deleteUserAddress(
        com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteUserAddressMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse> setDefaultAddress(
        com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSetDefaultAddressMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_USER_BY_ID = 0;
  private static final int METHODID_GET_USER_PROFILE = 1;
  private static final int METHODID_UPDATE_PROFILE = 2;
  private static final int METHODID_CHANGE_PASSWORD = 3;
  private static final int METHODID_GET_USER_ORDERS = 4;
  private static final int METHODID_DELETE_ACCOUNT = 5;
  private static final int METHODID_GET_USER_PREFERENCES = 6;
  private static final int METHODID_UPDATE_USER_PREFERENCES = 7;
  private static final int METHODID_GET_USER_ADDRESSES = 8;
  private static final int METHODID_ADD_USER_ADDRESS = 9;
  private static final int METHODID_UPDATE_USER_ADDRESS = 10;
  private static final int METHODID_DELETE_USER_ADDRESS = 11;
  private static final int METHODID_SET_DEFAULT_ADDRESS = 12;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_USER_BY_ID:
          serviceImpl.getUserById((com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse>) responseObserver);
          break;
        case METHODID_GET_USER_PROFILE:
          serviceImpl.getUserProfile((com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse>) responseObserver);
          break;
        case METHODID_UPDATE_PROFILE:
          serviceImpl.updateProfile((com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse>) responseObserver);
          break;
        case METHODID_CHANGE_PASSWORD:
          serviceImpl.changePassword((com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse>) responseObserver);
          break;
        case METHODID_GET_USER_ORDERS:
          serviceImpl.getUserOrders((com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse>) responseObserver);
          break;
        case METHODID_DELETE_ACCOUNT:
          serviceImpl.deleteAccount((com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse>) responseObserver);
          break;
        case METHODID_GET_USER_PREFERENCES:
          serviceImpl.getUserPreferences((com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse>) responseObserver);
          break;
        case METHODID_UPDATE_USER_PREFERENCES:
          serviceImpl.updateUserPreferences((com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse>) responseObserver);
          break;
        case METHODID_GET_USER_ADDRESSES:
          serviceImpl.getUserAddresses((com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse>) responseObserver);
          break;
        case METHODID_ADD_USER_ADDRESS:
          serviceImpl.addUserAddress((com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse>) responseObserver);
          break;
        case METHODID_UPDATE_USER_ADDRESS:
          serviceImpl.updateUserAddress((com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse>) responseObserver);
          break;
        case METHODID_DELETE_USER_ADDRESS:
          serviceImpl.deleteUserAddress((com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse>) responseObserver);
          break;
        case METHODID_SET_DEFAULT_ADDRESS:
          serviceImpl.setDefaultAddress((com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetUserByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserByIdResponse>(
                service, METHODID_GET_USER_BY_ID)))
        .addMethod(
          getGetUserProfileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserProfileResponse>(
                service, METHODID_GET_USER_PROFILE)))
        .addMethod(
          getUpdateProfileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.UpdateProfileResponse>(
                service, METHODID_UPDATE_PROFILE)))
        .addMethod(
          getChangePasswordMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.ChangePasswordResponse>(
                service, METHODID_CHANGE_PASSWORD)))
        .addMethod(
          getGetUserOrdersMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserOrdersResponse>(
                service, METHODID_GET_USER_ORDERS)))
        .addMethod(
          getDeleteAccountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.DeleteAccountResponse>(
                service, METHODID_DELETE_ACCOUNT)))
        .addMethod(
          getGetUserPreferencesMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserPreferencesResponse>(
                service, METHODID_GET_USER_PREFERENCES)))
        .addMethod(
          getUpdateUserPreferencesMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserPreferencesResponse>(
                service, METHODID_UPDATE_USER_PREFERENCES)))
        .addMethod(
          getGetUserAddressesMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.GetUserAddressesResponse>(
                service, METHODID_GET_USER_ADDRESSES)))
        .addMethod(
          getAddUserAddressMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.AddUserAddressResponse>(
                service, METHODID_ADD_USER_ADDRESS)))
        .addMethod(
          getUpdateUserAddressMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.UpdateUserAddressResponse>(
                service, METHODID_UPDATE_USER_ADDRESS)))
        .addMethod(
          getDeleteUserAddressMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.DeleteUserAddressResponse>(
                service, METHODID_DELETE_USER_ADDRESS)))
        .addMethod(
          getSetDefaultAddressMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressRequest,
              com.example.spring_ecom.grpc.services.UserServiceProto.SetDefaultAddressResponse>(
                service, METHODID_SET_DEFAULT_ADDRESS)))
        .build();
  }

  private static abstract class UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.spring_ecom.grpc.services.UserServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserService");
    }
  }

  private static final class UserServiceFileDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier {
    UserServiceFileDescriptorSupplier() {}
  }

  private static final class UserServiceMethodDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    UserServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (UserServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserServiceFileDescriptorSupplier())
              .addMethod(getGetUserByIdMethod())
              .addMethod(getGetUserProfileMethod())
              .addMethod(getUpdateProfileMethod())
              .addMethod(getChangePasswordMethod())
              .addMethod(getGetUserOrdersMethod())
              .addMethod(getDeleteAccountMethod())
              .addMethod(getGetUserPreferencesMethod())
              .addMethod(getUpdateUserPreferencesMethod())
              .addMethod(getGetUserAddressesMethod())
              .addMethod(getAddUserAddressMethod())
              .addMethod(getUpdateUserAddressMethod())
              .addMethod(getDeleteUserAddressMethod())
              .addMethod(getSetDefaultAddressMethod())
              .build();
        }
      }
    }
    return result;
  }
}
