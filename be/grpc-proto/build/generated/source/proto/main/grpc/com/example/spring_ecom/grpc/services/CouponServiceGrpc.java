package com.example.spring_ecom.grpc.services;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: services/coupon_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CouponServiceGrpc {

  private CouponServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "services.CouponService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest,
      com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse> getValidateCouponMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateCoupon",
      requestType = com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest.class,
      responseType = com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest,
      com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse> getValidateCouponMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest, com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse> getValidateCouponMethod;
    if ((getValidateCouponMethod = CouponServiceGrpc.getValidateCouponMethod) == null) {
      synchronized (CouponServiceGrpc.class) {
        if ((getValidateCouponMethod = CouponServiceGrpc.getValidateCouponMethod) == null) {
          CouponServiceGrpc.getValidateCouponMethod = getValidateCouponMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest, com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateCoupon"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CouponServiceMethodDescriptorSupplier("ValidateCoupon"))
              .build();
        }
      }
    }
    return getValidateCouponMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest,
      com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse> getIncrementUsageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "IncrementUsage",
      requestType = com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest.class,
      responseType = com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest,
      com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse> getIncrementUsageMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest, com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse> getIncrementUsageMethod;
    if ((getIncrementUsageMethod = CouponServiceGrpc.getIncrementUsageMethod) == null) {
      synchronized (CouponServiceGrpc.class) {
        if ((getIncrementUsageMethod = CouponServiceGrpc.getIncrementUsageMethod) == null) {
          CouponServiceGrpc.getIncrementUsageMethod = getIncrementUsageMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest, com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "IncrementUsage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CouponServiceMethodDescriptorSupplier("IncrementUsage"))
              .build();
        }
      }
    }
    return getIncrementUsageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest,
      com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse> getGetCouponByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetCouponById",
      requestType = com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest.class,
      responseType = com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest,
      com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse> getGetCouponByIdMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest, com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse> getGetCouponByIdMethod;
    if ((getGetCouponByIdMethod = CouponServiceGrpc.getGetCouponByIdMethod) == null) {
      synchronized (CouponServiceGrpc.class) {
        if ((getGetCouponByIdMethod = CouponServiceGrpc.getGetCouponByIdMethod) == null) {
          CouponServiceGrpc.getGetCouponByIdMethod = getGetCouponByIdMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest, com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetCouponById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CouponServiceMethodDescriptorSupplier("GetCouponById"))
              .build();
        }
      }
    }
    return getGetCouponByIdMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CouponServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CouponServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CouponServiceStub>() {
        @java.lang.Override
        public CouponServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CouponServiceStub(channel, callOptions);
        }
      };
    return CouponServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CouponServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CouponServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CouponServiceBlockingStub>() {
        @java.lang.Override
        public CouponServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CouponServiceBlockingStub(channel, callOptions);
        }
      };
    return CouponServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CouponServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CouponServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CouponServiceFutureStub>() {
        @java.lang.Override
        public CouponServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CouponServiceFutureStub(channel, callOptions);
        }
      };
    return CouponServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Validate coupon for order (inter-service)
     * </pre>
     */
    default void validateCoupon(com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateCouponMethod(), responseObserver);
    }

    /**
     * <pre>
     * Increment usage count (inter-service)
     * </pre>
     */
    default void incrementUsage(com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getIncrementUsageMethod(), responseObserver);
    }

    /**
     * <pre>
     * Get coupon by ID (inter-service)
     * </pre>
     */
    default void getCouponById(com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetCouponByIdMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CouponService.
   */
  public static abstract class CouponServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CouponServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CouponService.
   */
  public static final class CouponServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CouponServiceStub> {
    private CouponServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CouponServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CouponServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Validate coupon for order (inter-service)
     * </pre>
     */
    public void validateCoupon(com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateCouponMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Increment usage count (inter-service)
     * </pre>
     */
    public void incrementUsage(com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getIncrementUsageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get coupon by ID (inter-service)
     * </pre>
     */
    public void getCouponById(com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetCouponByIdMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CouponService.
   */
  public static final class CouponServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CouponServiceBlockingStub> {
    private CouponServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CouponServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CouponServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Validate coupon for order (inter-service)
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse validateCoupon(com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateCouponMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Increment usage count (inter-service)
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse incrementUsage(com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getIncrementUsageMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get coupon by ID (inter-service)
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse getCouponById(com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetCouponByIdMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CouponService.
   */
  public static final class CouponServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CouponServiceFutureStub> {
    private CouponServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CouponServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CouponServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Validate coupon for order (inter-service)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse> validateCoupon(
        com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateCouponMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Increment usage count (inter-service)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse> incrementUsage(
        com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getIncrementUsageMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Get coupon by ID (inter-service)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse> getCouponById(
        com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetCouponByIdMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VALIDATE_COUPON = 0;
  private static final int METHODID_INCREMENT_USAGE = 1;
  private static final int METHODID_GET_COUPON_BY_ID = 2;

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
        case METHODID_VALIDATE_COUPON:
          serviceImpl.validateCoupon((com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse>) responseObserver);
          break;
        case METHODID_INCREMENT_USAGE:
          serviceImpl.incrementUsage((com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse>) responseObserver);
          break;
        case METHODID_GET_COUPON_BY_ID:
          serviceImpl.getCouponById((com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse>) responseObserver);
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
          getValidateCouponMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponRequest,
              com.example.spring_ecom.grpc.services.CouponServiceProto.ValidateCouponResponse>(
                service, METHODID_VALIDATE_COUPON)))
        .addMethod(
          getIncrementUsageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageRequest,
              com.example.spring_ecom.grpc.services.CouponServiceProto.IncrementUsageResponse>(
                service, METHODID_INCREMENT_USAGE)))
        .addMethod(
          getGetCouponByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdRequest,
              com.example.spring_ecom.grpc.services.CouponServiceProto.GetCouponByIdResponse>(
                service, METHODID_GET_COUPON_BY_ID)))
        .build();
  }

  private static abstract class CouponServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CouponServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.spring_ecom.grpc.services.CouponServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CouponService");
    }
  }

  private static final class CouponServiceFileDescriptorSupplier
      extends CouponServiceBaseDescriptorSupplier {
    CouponServiceFileDescriptorSupplier() {}
  }

  private static final class CouponServiceMethodDescriptorSupplier
      extends CouponServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    CouponServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (CouponServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CouponServiceFileDescriptorSupplier())
              .addMethod(getValidateCouponMethod())
              .addMethod(getIncrementUsageMethod())
              .addMethod(getGetCouponByIdMethod())
              .build();
        }
      }
    }
    return result;
  }
}
