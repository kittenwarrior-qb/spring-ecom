package com.example.spring_ecom.grpc.services;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: services/product_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ProductServiceGrpc {

  private ProductServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "services.ProductService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse> getCreateProductMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateProduct",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse> getCreateProductMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse> getCreateProductMethod;
    if ((getCreateProductMethod = ProductServiceGrpc.getCreateProductMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getCreateProductMethod = ProductServiceGrpc.getCreateProductMethod) == null) {
          ProductServiceGrpc.getCreateProductMethod = getCreateProductMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateProduct"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("CreateProduct"))
              .build();
        }
      }
    }
    return getCreateProductMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse> getUpdateProductMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateProduct",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse> getUpdateProductMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse> getUpdateProductMethod;
    if ((getUpdateProductMethod = ProductServiceGrpc.getUpdateProductMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getUpdateProductMethod = ProductServiceGrpc.getUpdateProductMethod) == null) {
          ProductServiceGrpc.getUpdateProductMethod = getUpdateProductMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateProduct"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("UpdateProduct"))
              .build();
        }
      }
    }
    return getUpdateProductMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse> getDeleteProductMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteProduct",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse> getDeleteProductMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse> getDeleteProductMethod;
    if ((getDeleteProductMethod = ProductServiceGrpc.getDeleteProductMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getDeleteProductMethod = ProductServiceGrpc.getDeleteProductMethod) == null) {
          ProductServiceGrpc.getDeleteProductMethod = getDeleteProductMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteProduct"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("DeleteProduct"))
              .build();
        }
      }
    }
    return getDeleteProductMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse> getGetProductsAdminMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetProductsAdmin",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse> getGetProductsAdminMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse> getGetProductsAdminMethod;
    if ((getGetProductsAdminMethod = ProductServiceGrpc.getGetProductsAdminMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getGetProductsAdminMethod = ProductServiceGrpc.getGetProductsAdminMethod) == null) {
          ProductServiceGrpc.getGetProductsAdminMethod = getGetProductsAdminMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetProductsAdmin"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("GetProductsAdmin"))
              .build();
        }
      }
    }
    return getGetProductsAdminMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse> getIncrementProductViewsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "IncrementProductViews",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse> getIncrementProductViewsMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse> getIncrementProductViewsMethod;
    if ((getIncrementProductViewsMethod = ProductServiceGrpc.getIncrementProductViewsMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getIncrementProductViewsMethod = ProductServiceGrpc.getIncrementProductViewsMethod) == null) {
          ProductServiceGrpc.getIncrementProductViewsMethod = getIncrementProductViewsMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "IncrementProductViews"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("IncrementProductViews"))
              .build();
        }
      }
    }
    return getIncrementProductViewsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse> getValidateProductAvailabilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateProductAvailability",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse> getValidateProductAvailabilityMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse> getValidateProductAvailabilityMethod;
    if ((getValidateProductAvailabilityMethod = ProductServiceGrpc.getValidateProductAvailabilityMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getValidateProductAvailabilityMethod = ProductServiceGrpc.getValidateProductAvailabilityMethod) == null) {
          ProductServiceGrpc.getValidateProductAvailabilityMethod = getValidateProductAvailabilityMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateProductAvailability"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("ValidateProductAvailability"))
              .build();
        }
      }
    }
    return getValidateProductAvailabilityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse> getGetProductByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetProductById",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse> getGetProductByIdMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse> getGetProductByIdMethod;
    if ((getGetProductByIdMethod = ProductServiceGrpc.getGetProductByIdMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getGetProductByIdMethod = ProductServiceGrpc.getGetProductByIdMethod) == null) {
          ProductServiceGrpc.getGetProductByIdMethod = getGetProductByIdMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetProductById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("GetProductById"))
              .build();
        }
      }
    }
    return getGetProductByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse> getUpdateProductStockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateProductStock",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse> getUpdateProductStockMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse> getUpdateProductStockMethod;
    if ((getUpdateProductStockMethod = ProductServiceGrpc.getUpdateProductStockMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getUpdateProductStockMethod = ProductServiceGrpc.getUpdateProductStockMethod) == null) {
          ProductServiceGrpc.getUpdateProductStockMethod = getUpdateProductStockMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateProductStock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("UpdateProductStock"))
              .build();
        }
      }
    }
    return getUpdateProductStockMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse> getUpdateProductsSoldCountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateProductsSoldCount",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse> getUpdateProductsSoldCountMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse> getUpdateProductsSoldCountMethod;
    if ((getUpdateProductsSoldCountMethod = ProductServiceGrpc.getUpdateProductsSoldCountMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getUpdateProductsSoldCountMethod = ProductServiceGrpc.getUpdateProductsSoldCountMethod) == null) {
          ProductServiceGrpc.getUpdateProductsSoldCountMethod = getUpdateProductsSoldCountMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateProductsSoldCount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("UpdateProductsSoldCount"))
              .build();
        }
      }
    }
    return getUpdateProductsSoldCountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse> getCreateCategoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateCategory",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse> getCreateCategoryMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse> getCreateCategoryMethod;
    if ((getCreateCategoryMethod = ProductServiceGrpc.getCreateCategoryMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getCreateCategoryMethod = ProductServiceGrpc.getCreateCategoryMethod) == null) {
          ProductServiceGrpc.getCreateCategoryMethod = getCreateCategoryMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateCategory"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("CreateCategory"))
              .build();
        }
      }
    }
    return getCreateCategoryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse> getUpdateCategoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateCategory",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse> getUpdateCategoryMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse> getUpdateCategoryMethod;
    if ((getUpdateCategoryMethod = ProductServiceGrpc.getUpdateCategoryMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getUpdateCategoryMethod = ProductServiceGrpc.getUpdateCategoryMethod) == null) {
          ProductServiceGrpc.getUpdateCategoryMethod = getUpdateCategoryMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateCategory"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("UpdateCategory"))
              .build();
        }
      }
    }
    return getUpdateCategoryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse> getDeleteCategoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteCategory",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse> getDeleteCategoryMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse> getDeleteCategoryMethod;
    if ((getDeleteCategoryMethod = ProductServiceGrpc.getDeleteCategoryMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getDeleteCategoryMethod = ProductServiceGrpc.getDeleteCategoryMethod) == null) {
          ProductServiceGrpc.getDeleteCategoryMethod = getDeleteCategoryMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteCategory"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("DeleteCategory"))
              .build();
        }
      }
    }
    return getDeleteCategoryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse> getGetCategoriesAdminMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetCategoriesAdmin",
      requestType = com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest.class,
      responseType = com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest,
      com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse> getGetCategoriesAdminMethod() {
    io.grpc.MethodDescriptor<com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse> getGetCategoriesAdminMethod;
    if ((getGetCategoriesAdminMethod = ProductServiceGrpc.getGetCategoriesAdminMethod) == null) {
      synchronized (ProductServiceGrpc.class) {
        if ((getGetCategoriesAdminMethod = ProductServiceGrpc.getGetCategoriesAdminMethod) == null) {
          ProductServiceGrpc.getGetCategoriesAdminMethod = getGetCategoriesAdminMethod =
              io.grpc.MethodDescriptor.<com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest, com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetCategoriesAdmin"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProductServiceMethodDescriptorSupplier("GetCategoriesAdmin"))
              .build();
        }
      }
    }
    return getGetCategoriesAdminMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ProductServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProductServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProductServiceStub>() {
        @java.lang.Override
        public ProductServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProductServiceStub(channel, callOptions);
        }
      };
    return ProductServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ProductServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProductServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProductServiceBlockingStub>() {
        @java.lang.Override
        public ProductServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProductServiceBlockingStub(channel, callOptions);
        }
      };
    return ProductServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ProductServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProductServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProductServiceFutureStub>() {
        @java.lang.Override
        public ProductServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProductServiceFutureStub(channel, callOptions);
        }
      };
    return ProductServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Product management (Admin)
     * </pre>
     */
    default void createProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateProductMethod(), responseObserver);
    }

    /**
     */
    default void updateProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateProductMethod(), responseObserver);
    }

    /**
     */
    default void deleteProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteProductMethod(), responseObserver);
    }

    /**
     */
    default void getProductsAdmin(com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetProductsAdminMethod(), responseObserver);
    }

    /**
     */
    default void incrementProductViews(com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getIncrementProductViewsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Stock management (inter-service)
     * </pre>
     */
    default void validateProductAvailability(com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateProductAvailabilityMethod(), responseObserver);
    }

    /**
     */
    default void getProductById(com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetProductByIdMethod(), responseObserver);
    }

    /**
     */
    default void updateProductStock(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateProductStockMethod(), responseObserver);
    }

    /**
     */
    default void updateProductsSoldCount(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateProductsSoldCountMethod(), responseObserver);
    }

    /**
     * <pre>
     * Category management (Admin)
     * </pre>
     */
    default void createCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateCategoryMethod(), responseObserver);
    }

    /**
     */
    default void updateCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateCategoryMethod(), responseObserver);
    }

    /**
     */
    default void deleteCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteCategoryMethod(), responseObserver);
    }

    /**
     */
    default void getCategoriesAdmin(com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetCategoriesAdminMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ProductService.
   */
  public static abstract class ProductServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ProductServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ProductService.
   */
  public static final class ProductServiceStub
      extends io.grpc.stub.AbstractAsyncStub<ProductServiceStub> {
    private ProductServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProductServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProductServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Product management (Admin)
     * </pre>
     */
    public void createProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateProductMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateProductMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteProductMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getProductsAdmin(com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetProductsAdminMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void incrementProductViews(com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getIncrementProductViewsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Stock management (inter-service)
     * </pre>
     */
    public void validateProductAvailability(com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateProductAvailabilityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getProductById(com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetProductByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateProductStock(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateProductStockMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateProductsSoldCount(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateProductsSoldCountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Category management (Admin)
     * </pre>
     */
    public void createCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateCategoryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateCategoryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteCategoryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getCategoriesAdmin(com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest request,
        io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetCategoriesAdminMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ProductService.
   */
  public static final class ProductServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ProductServiceBlockingStub> {
    private ProductServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProductServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProductServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Product management (Admin)
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse createProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateProductMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse updateProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateProductMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse deleteProduct(com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteProductMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse getProductsAdmin(com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetProductsAdminMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse incrementProductViews(com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getIncrementProductViewsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Stock management (inter-service)
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse validateProductAvailability(com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateProductAvailabilityMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse getProductById(com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetProductByIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse updateProductStock(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateProductStockMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse updateProductsSoldCount(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateProductsSoldCountMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Category management (Admin)
     * </pre>
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse createCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateCategoryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse updateCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateCategoryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse deleteCategory(com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteCategoryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse getCategoriesAdmin(com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetCategoriesAdminMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ProductService.
   */
  public static final class ProductServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<ProductServiceFutureStub> {
    private ProductServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProductServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProductServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Product management (Admin)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse> createProduct(
        com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateProductMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse> updateProduct(
        com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateProductMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse> deleteProduct(
        com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteProductMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse> getProductsAdmin(
        com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetProductsAdminMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse> incrementProductViews(
        com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getIncrementProductViewsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Stock management (inter-service)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse> validateProductAvailability(
        com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateProductAvailabilityMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse> getProductById(
        com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetProductByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse> updateProductStock(
        com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateProductStockMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse> updateProductsSoldCount(
        com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateProductsSoldCountMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Category management (Admin)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse> createCategory(
        com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateCategoryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse> updateCategory(
        com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateCategoryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse> deleteCategory(
        com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteCategoryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse> getCategoriesAdmin(
        com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetCategoriesAdminMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_PRODUCT = 0;
  private static final int METHODID_UPDATE_PRODUCT = 1;
  private static final int METHODID_DELETE_PRODUCT = 2;
  private static final int METHODID_GET_PRODUCTS_ADMIN = 3;
  private static final int METHODID_INCREMENT_PRODUCT_VIEWS = 4;
  private static final int METHODID_VALIDATE_PRODUCT_AVAILABILITY = 5;
  private static final int METHODID_GET_PRODUCT_BY_ID = 6;
  private static final int METHODID_UPDATE_PRODUCT_STOCK = 7;
  private static final int METHODID_UPDATE_PRODUCTS_SOLD_COUNT = 8;
  private static final int METHODID_CREATE_CATEGORY = 9;
  private static final int METHODID_UPDATE_CATEGORY = 10;
  private static final int METHODID_DELETE_CATEGORY = 11;
  private static final int METHODID_GET_CATEGORIES_ADMIN = 12;

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
        case METHODID_CREATE_PRODUCT:
          serviceImpl.createProduct((com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse>) responseObserver);
          break;
        case METHODID_UPDATE_PRODUCT:
          serviceImpl.updateProduct((com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse>) responseObserver);
          break;
        case METHODID_DELETE_PRODUCT:
          serviceImpl.deleteProduct((com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse>) responseObserver);
          break;
        case METHODID_GET_PRODUCTS_ADMIN:
          serviceImpl.getProductsAdmin((com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse>) responseObserver);
          break;
        case METHODID_INCREMENT_PRODUCT_VIEWS:
          serviceImpl.incrementProductViews((com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_PRODUCT_AVAILABILITY:
          serviceImpl.validateProductAvailability((com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse>) responseObserver);
          break;
        case METHODID_GET_PRODUCT_BY_ID:
          serviceImpl.getProductById((com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse>) responseObserver);
          break;
        case METHODID_UPDATE_PRODUCT_STOCK:
          serviceImpl.updateProductStock((com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse>) responseObserver);
          break;
        case METHODID_UPDATE_PRODUCTS_SOLD_COUNT:
          serviceImpl.updateProductsSoldCount((com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse>) responseObserver);
          break;
        case METHODID_CREATE_CATEGORY:
          serviceImpl.createCategory((com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse>) responseObserver);
          break;
        case METHODID_UPDATE_CATEGORY:
          serviceImpl.updateCategory((com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse>) responseObserver);
          break;
        case METHODID_DELETE_CATEGORY:
          serviceImpl.deleteCategory((com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse>) responseObserver);
          break;
        case METHODID_GET_CATEGORIES_ADMIN:
          serviceImpl.getCategoriesAdmin((com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest) request,
              (io.grpc.stub.StreamObserver<com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse>) responseObserver);
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
          getCreateProductMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.CreateProductResponse>(
                service, METHODID_CREATE_PRODUCT)))
        .addMethod(
          getUpdateProductMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductResponse>(
                service, METHODID_UPDATE_PRODUCT)))
        .addMethod(
          getDeleteProductMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteProductResponse>(
                service, METHODID_DELETE_PRODUCT)))
        .addMethod(
          getGetProductsAdminMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductsAdminResponse>(
                service, METHODID_GET_PRODUCTS_ADMIN)))
        .addMethod(
          getIncrementProductViewsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.IncrementProductViewsResponse>(
                service, METHODID_INCREMENT_PRODUCT_VIEWS)))
        .addMethod(
          getValidateProductAvailabilityMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.ValidateProductAvailabilityResponse>(
                service, METHODID_VALIDATE_PRODUCT_AVAILABILITY)))
        .addMethod(
          getGetProductByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.GetProductByIdResponse>(
                service, METHODID_GET_PRODUCT_BY_ID)))
        .addMethod(
          getUpdateProductStockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductStockResponse>(
                service, METHODID_UPDATE_PRODUCT_STOCK)))
        .addMethod(
          getUpdateProductsSoldCountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateProductsSoldCountResponse>(
                service, METHODID_UPDATE_PRODUCTS_SOLD_COUNT)))
        .addMethod(
          getCreateCategoryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.CreateCategoryResponse>(
                service, METHODID_CREATE_CATEGORY)))
        .addMethod(
          getUpdateCategoryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.UpdateCategoryResponse>(
                service, METHODID_UPDATE_CATEGORY)))
        .addMethod(
          getDeleteCategoryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.DeleteCategoryResponse>(
                service, METHODID_DELETE_CATEGORY)))
        .addMethod(
          getGetCategoriesAdminMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminRequest,
              com.example.spring_ecom.grpc.services.ProductServiceProto.GetCategoriesAdminResponse>(
                service, METHODID_GET_CATEGORIES_ADMIN)))
        .build();
  }

  private static abstract class ProductServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ProductServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.spring_ecom.grpc.services.ProductServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ProductService");
    }
  }

  private static final class ProductServiceFileDescriptorSupplier
      extends ProductServiceBaseDescriptorSupplier {
    ProductServiceFileDescriptorSupplier() {}
  }

  private static final class ProductServiceMethodDescriptorSupplier
      extends ProductServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ProductServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (ProductServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ProductServiceFileDescriptorSupplier())
              .addMethod(getCreateProductMethod())
              .addMethod(getUpdateProductMethod())
              .addMethod(getDeleteProductMethod())
              .addMethod(getGetProductsAdminMethod())
              .addMethod(getIncrementProductViewsMethod())
              .addMethod(getValidateProductAvailabilityMethod())
              .addMethod(getGetProductByIdMethod())
              .addMethod(getUpdateProductStockMethod())
              .addMethod(getUpdateProductsSoldCountMethod())
              .addMethod(getCreateCategoryMethod())
              .addMethod(getUpdateCategoryMethod())
              .addMethod(getDeleteCategoryMethod())
              .addMethod(getGetCategoriesAdminMethod())
              .build();
        }
      }
    }
    return result;
  }
}
