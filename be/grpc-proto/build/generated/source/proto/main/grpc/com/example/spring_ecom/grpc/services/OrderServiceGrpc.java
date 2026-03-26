package com.example.spring_ecom.grpc.services;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: services/order_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class OrderServiceGrpc {

  private OrderServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "services.OrderService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest,
      com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse> getGetOrdersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetOrders",
      requestType = com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest.class,
      responseType = com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest,
      com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse> getGetOrdersMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest, com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse> getGetOrdersMethod;
    if ((getGetOrdersMethod = OrderServiceGrpc.getGetOrdersMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getGetOrdersMethod = OrderServiceGrpc.getGetOrdersMethod) == null) {
          OrderServiceGrpc.getGetOrdersMethod = getGetOrdersMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest, com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetOrders"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("GetOrders"))
              .build();
        }
      }
    }
    return getGetOrdersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest,
      com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse> getGetOrderDetailMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetOrderDetail",
      requestType = com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest.class,
      responseType = com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest,
      com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse> getGetOrderDetailMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest, com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse> getGetOrderDetailMethod;
    if ((getGetOrderDetailMethod = OrderServiceGrpc.getGetOrderDetailMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getGetOrderDetailMethod = OrderServiceGrpc.getGetOrderDetailMethod) == null) {
          OrderServiceGrpc.getGetOrderDetailMethod = getGetOrderDetailMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest, com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetOrderDetail"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("GetOrderDetail"))
              .build();
        }
      }
    }
    return getGetOrderDetailMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest,
      com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse> getUpdateOrderStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateOrderStatus",
      requestType = com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest.class,
      responseType = com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest,
      com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse> getUpdateOrderStatusMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest, com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse> getUpdateOrderStatusMethod;
    if ((getUpdateOrderStatusMethod = OrderServiceGrpc.getUpdateOrderStatusMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getUpdateOrderStatusMethod = OrderServiceGrpc.getUpdateOrderStatusMethod) == null) {
          OrderServiceGrpc.getUpdateOrderStatusMethod = getUpdateOrderStatusMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest, com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateOrderStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("UpdateOrderStatus"))
              .build();
        }
      }
    }
    return getUpdateOrderStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest,
      com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse> getGetOrderStatisticsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetOrderStatistics",
      requestType = com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest.class,
      responseType = com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest,
      com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse> getGetOrderStatisticsMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest, com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse> getGetOrderStatisticsMethod;
    if ((getGetOrderStatisticsMethod = OrderServiceGrpc.getGetOrderStatisticsMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getGetOrderStatisticsMethod = OrderServiceGrpc.getGetOrderStatisticsMethod) == null) {
          OrderServiceGrpc.getGetOrderStatisticsMethod = getGetOrderStatisticsMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest, com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetOrderStatistics"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("GetOrderStatistics"))
              .build();
        }
      }
    }
    return getGetOrderStatisticsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OrderServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceStub>() {
        @java.lang.Override
        public OrderServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceStub(channel, callOptions);
        }
      };
    return OrderServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OrderServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceBlockingStub>() {
        @java.lang.Override
        public OrderServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceBlockingStub(channel, callOptions);
        }
      };
    return OrderServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OrderServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceFutureStub>() {
        @java.lang.Override
        public OrderServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceFutureStub(channel, callOptions);
        }
      };
    return OrderServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Order management (Admin)
     * </pre>
     */
    default void getOrders(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetOrdersMethod(), responseObserver);
    }

    /**
     */
    default void getOrderDetail(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetOrderDetailMethod(), responseObserver);
    }

    /**
     */
    default void updateOrderStatus(com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateOrderStatusMethod(), responseObserver);
    }

    /**
     */
    default void getOrderStatistics(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetOrderStatisticsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service OrderService.
   */
  public static abstract class OrderServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return OrderServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service OrderService.
   */
  public static final class OrderServiceStub
      extends io.grpc.stub.AbstractAsyncStub<OrderServiceStub> {
    private OrderServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Order management (Admin)
     * </pre>
     */
    public void getOrders(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetOrdersMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getOrderDetail(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetOrderDetailMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateOrderStatus(com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateOrderStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getOrderStatistics(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetOrderStatisticsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service OrderService.
   */
  public static final class OrderServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<OrderServiceBlockingStub> {
    private OrderServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Order management (Admin)
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse getOrders(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetOrdersMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse getOrderDetail(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetOrderDetailMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse updateOrderStatus(com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateOrderStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse getOrderStatistics(com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetOrderStatisticsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service OrderService.
   */
  public static final class OrderServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<OrderServiceFutureStub> {
    private OrderServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Order management (Admin)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse> getOrders(
        com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetOrdersMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse> getOrderDetail(
        com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetOrderDetailMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse> updateOrderStatus(
        com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateOrderStatusMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse> getOrderStatistics(
        com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetOrderStatisticsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ORDERS = 0;
  private static final int METHODID_GET_ORDER_DETAIL = 1;
  private static final int METHODID_UPDATE_ORDER_STATUS = 2;
  private static final int METHODID_GET_ORDER_STATISTICS = 3;

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
        case METHODID_GET_ORDERS:
          serviceImpl.getOrders((com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse>) responseObserver);
          break;
        case METHODID_GET_ORDER_DETAIL:
          serviceImpl.getOrderDetail((com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse>) responseObserver);
          break;
        case METHODID_UPDATE_ORDER_STATUS:
          serviceImpl.updateOrderStatus((com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse>) responseObserver);
          break;
        case METHODID_GET_ORDER_STATISTICS:
          serviceImpl.getOrderStatistics((com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse>) responseObserver);
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
          getGetOrdersMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersRequest,
              com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrdersResponse>(
                service, METHODID_GET_ORDERS)))
        .addMethod(
          getGetOrderDetailMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailRequest,
              com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderDetailResponse>(
                service, METHODID_GET_ORDER_DETAIL)))
        .addMethod(
          getUpdateOrderStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusRequest,
              com.example.spring_ecom.grpc.services.OrderServiceProto.UpdateOrderStatusResponse>(
                service, METHODID_UPDATE_ORDER_STATUS)))
        .addMethod(
          getGetOrderStatisticsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsRequest,
              com.example.spring_ecom.grpc.services.OrderServiceProto.GetOrderStatisticsResponse>(
                service, METHODID_GET_ORDER_STATISTICS)))
        .build();
  }

  private static abstract class OrderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    OrderServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.spring_ecom.grpc.services.OrderServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("OrderService");
    }
  }

  private static final class OrderServiceFileDescriptorSupplier
      extends OrderServiceBaseDescriptorSupplier {
    OrderServiceFileDescriptorSupplier() {}
  }

  private static final class OrderServiceMethodDescriptorSupplier
      extends OrderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    OrderServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (OrderServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new OrderServiceFileDescriptorSupplier())
              .addMethod(getGetOrdersMethod())
              .addMethod(getGetOrderDetailMethod())
              .addMethod(getUpdateOrderStatusMethod())
              .addMethod(getGetOrderStatisticsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
