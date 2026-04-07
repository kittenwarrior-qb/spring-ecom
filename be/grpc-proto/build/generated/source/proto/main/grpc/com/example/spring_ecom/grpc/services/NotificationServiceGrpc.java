package com.example.spring_ecom.grpc.services;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: services/notification_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class NotificationServiceGrpc {

  private NotificationServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "services.NotificationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest,
      com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse> getSendNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendNotification",
      requestType = com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest.class,
      responseType = com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest,
      com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse> getSendNotificationMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest, com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse> getSendNotificationMethod;
    if ((getSendNotificationMethod = NotificationServiceGrpc.getSendNotificationMethod) == null) {
      synchronized (NotificationServiceGrpc.class) {
        if ((getSendNotificationMethod = NotificationServiceGrpc.getSendNotificationMethod) == null) {
          NotificationServiceGrpc.getSendNotificationMethod = getSendNotificationMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest, com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendNotification"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NotificationServiceMethodDescriptorSupplier("SendNotification"))
              .build();
        }
      }
    }
    return getSendNotificationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest,
      com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse> getBroadcastNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BroadcastNotification",
      requestType = com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest.class,
      responseType = com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest,
      com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse> getBroadcastNotificationMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest, com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse> getBroadcastNotificationMethod;
    if ((getBroadcastNotificationMethod = NotificationServiceGrpc.getBroadcastNotificationMethod) == null) {
      synchronized (NotificationServiceGrpc.class) {
        if ((getBroadcastNotificationMethod = NotificationServiceGrpc.getBroadcastNotificationMethod) == null) {
          NotificationServiceGrpc.getBroadcastNotificationMethod = getBroadcastNotificationMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest, com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "BroadcastNotification"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NotificationServiceMethodDescriptorSupplier("BroadcastNotification"))
              .build();
        }
      }
    }
    return getBroadcastNotificationMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NotificationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceStub>() {
        @java.lang.Override
        public NotificationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceStub(channel, callOptions);
        }
      };
    return NotificationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NotificationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceBlockingStub>() {
        @java.lang.Override
        public NotificationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceBlockingStub(channel, callOptions);
        }
      };
    return NotificationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NotificationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceFutureStub>() {
        @java.lang.Override
        public NotificationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceFutureStub(channel, callOptions);
        }
      };
    return NotificationServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Send notification to specific user via MQTT
     * </pre>
     */
    default void sendNotification(com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendNotificationMethod(), responseObserver);
    }

    /**
     * <pre>
     * Broadcast notification to all users via MQTT
     * </pre>
     */
    default void broadcastNotification(com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBroadcastNotificationMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service NotificationService.
   */
  public static abstract class NotificationServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return NotificationServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service NotificationService.
   */
  public static final class NotificationServiceStub
      extends io.grpc.stub.AbstractAsyncStub<NotificationServiceStub> {
    private NotificationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Send notification to specific user via MQTT
     * </pre>
     */
    public void sendNotification(com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendNotificationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Broadcast notification to all users via MQTT
     * </pre>
     */
    public void broadcastNotification(com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBroadcastNotificationMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service NotificationService.
   */
  public static final class NotificationServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<NotificationServiceBlockingStub> {
    private NotificationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Send notification to specific user via MQTT
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse sendNotification(com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendNotificationMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Broadcast notification to all users via MQTT
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse broadcastNotification(com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBroadcastNotificationMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service NotificationService.
   */
  public static final class NotificationServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<NotificationServiceFutureStub> {
    private NotificationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Send notification to specific user via MQTT
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse> sendNotification(
        com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendNotificationMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Broadcast notification to all users via MQTT
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse> broadcastNotification(
        com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBroadcastNotificationMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_NOTIFICATION = 0;
  private static final int METHODID_BROADCAST_NOTIFICATION = 1;

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
        case METHODID_SEND_NOTIFICATION:
          serviceImpl.sendNotification((com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse>) responseObserver);
          break;
        case METHODID_BROADCAST_NOTIFICATION:
          serviceImpl.broadcastNotification((com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse>) responseObserver);
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
          getSendNotificationMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationRequest,
              com.example.spring_ecom.grpc.services.NotificationServiceProto.SendNotificationResponse>(
                service, METHODID_SEND_NOTIFICATION)))
        .addMethod(
          getBroadcastNotificationMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationRequest,
              com.example.spring_ecom.grpc.services.NotificationServiceProto.BroadcastNotificationResponse>(
                service, METHODID_BROADCAST_NOTIFICATION)))
        .build();
  }

  private static abstract class NotificationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NotificationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.spring_ecom.grpc.services.NotificationServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NotificationService");
    }
  }

  private static final class NotificationServiceFileDescriptorSupplier
      extends NotificationServiceBaseDescriptorSupplier {
    NotificationServiceFileDescriptorSupplier() {}
  }

  private static final class NotificationServiceMethodDescriptorSupplier
      extends NotificationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    NotificationServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (NotificationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NotificationServiceFileDescriptorSupplier())
              .addMethod(getSendNotificationMethod())
              .addMethod(getBroadcastNotificationMethod())
              .build();
        }
      }
    }
    return result;
  }
}
