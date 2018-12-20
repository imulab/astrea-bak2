package io.imulab.astrea.sdk.flow;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.1)",
    comments = "Source: authorize_code_service.proto")
public final class AuthorizeCodeFlowGrpc {

  private AuthorizeCodeFlowGrpc() {}

  public static final String SERVICE_NAME = "client.AuthorizeCodeFlow";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.CodeRequest,
      io.imulab.astrea.sdk.flow.CodeResponse> getAuthorizeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Authorize",
      requestType = io.imulab.astrea.sdk.flow.CodeRequest.class,
      responseType = io.imulab.astrea.sdk.flow.CodeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.CodeRequest,
      io.imulab.astrea.sdk.flow.CodeResponse> getAuthorizeMethod() {
    io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.CodeRequest, io.imulab.astrea.sdk.flow.CodeResponse> getAuthorizeMethod;
    if ((getAuthorizeMethod = AuthorizeCodeFlowGrpc.getAuthorizeMethod) == null) {
      synchronized (AuthorizeCodeFlowGrpc.class) {
        if ((getAuthorizeMethod = AuthorizeCodeFlowGrpc.getAuthorizeMethod) == null) {
          AuthorizeCodeFlowGrpc.getAuthorizeMethod = getAuthorizeMethod = 
              io.grpc.MethodDescriptor.<io.imulab.astrea.sdk.flow.CodeRequest, io.imulab.astrea.sdk.flow.CodeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "client.AuthorizeCodeFlow", "Authorize"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.flow.CodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.flow.CodeResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new AuthorizeCodeFlowMethodDescriptorSupplier("Authorize"))
                  .build();
          }
        }
     }
     return getAuthorizeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.TokenRequest,
      io.imulab.astrea.sdk.flow.TokenResponse> getExchangeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Exchange",
      requestType = io.imulab.astrea.sdk.flow.TokenRequest.class,
      responseType = io.imulab.astrea.sdk.flow.TokenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.TokenRequest,
      io.imulab.astrea.sdk.flow.TokenResponse> getExchangeMethod() {
    io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.TokenRequest, io.imulab.astrea.sdk.flow.TokenResponse> getExchangeMethod;
    if ((getExchangeMethod = AuthorizeCodeFlowGrpc.getExchangeMethod) == null) {
      synchronized (AuthorizeCodeFlowGrpc.class) {
        if ((getExchangeMethod = AuthorizeCodeFlowGrpc.getExchangeMethod) == null) {
          AuthorizeCodeFlowGrpc.getExchangeMethod = getExchangeMethod = 
              io.grpc.MethodDescriptor.<io.imulab.astrea.sdk.flow.TokenRequest, io.imulab.astrea.sdk.flow.TokenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "client.AuthorizeCodeFlow", "Exchange"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.flow.TokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.flow.TokenResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new AuthorizeCodeFlowMethodDescriptorSupplier("Exchange"))
                  .build();
          }
        }
     }
     return getExchangeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AuthorizeCodeFlowStub newStub(io.grpc.Channel channel) {
    return new AuthorizeCodeFlowStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AuthorizeCodeFlowBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new AuthorizeCodeFlowBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AuthorizeCodeFlowFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new AuthorizeCodeFlowFutureStub(channel);
  }

  /**
   */
  public static abstract class AuthorizeCodeFlowImplBase implements io.grpc.BindableService {

    /**
     */
    public void authorize(io.imulab.astrea.sdk.flow.CodeRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.CodeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAuthorizeMethod(), responseObserver);
    }

    /**
     */
    public void exchange(io.imulab.astrea.sdk.flow.TokenRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.TokenResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getExchangeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAuthorizeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.imulab.astrea.sdk.flow.CodeRequest,
                io.imulab.astrea.sdk.flow.CodeResponse>(
                  this, METHODID_AUTHORIZE)))
          .addMethod(
            getExchangeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.imulab.astrea.sdk.flow.TokenRequest,
                io.imulab.astrea.sdk.flow.TokenResponse>(
                  this, METHODID_EXCHANGE)))
          .build();
    }
  }

  /**
   */
  public static final class AuthorizeCodeFlowStub extends io.grpc.stub.AbstractStub<AuthorizeCodeFlowStub> {
    private AuthorizeCodeFlowStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AuthorizeCodeFlowStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthorizeCodeFlowStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AuthorizeCodeFlowStub(channel, callOptions);
    }

    /**
     */
    public void authorize(io.imulab.astrea.sdk.flow.CodeRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.CodeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAuthorizeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void exchange(io.imulab.astrea.sdk.flow.TokenRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.TokenResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getExchangeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class AuthorizeCodeFlowBlockingStub extends io.grpc.stub.AbstractStub<AuthorizeCodeFlowBlockingStub> {
    private AuthorizeCodeFlowBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AuthorizeCodeFlowBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthorizeCodeFlowBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AuthorizeCodeFlowBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.imulab.astrea.sdk.flow.CodeResponse authorize(io.imulab.astrea.sdk.flow.CodeRequest request) {
      return blockingUnaryCall(
          getChannel(), getAuthorizeMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.imulab.astrea.sdk.flow.TokenResponse exchange(io.imulab.astrea.sdk.flow.TokenRequest request) {
      return blockingUnaryCall(
          getChannel(), getExchangeMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AuthorizeCodeFlowFutureStub extends io.grpc.stub.AbstractStub<AuthorizeCodeFlowFutureStub> {
    private AuthorizeCodeFlowFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AuthorizeCodeFlowFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthorizeCodeFlowFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AuthorizeCodeFlowFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.imulab.astrea.sdk.flow.CodeResponse> authorize(
        io.imulab.astrea.sdk.flow.CodeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAuthorizeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.imulab.astrea.sdk.flow.TokenResponse> exchange(
        io.imulab.astrea.sdk.flow.TokenRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getExchangeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_AUTHORIZE = 0;
  private static final int METHODID_EXCHANGE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AuthorizeCodeFlowImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AuthorizeCodeFlowImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_AUTHORIZE:
          serviceImpl.authorize((io.imulab.astrea.sdk.flow.CodeRequest) request,
              (io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.CodeResponse>) responseObserver);
          break;
        case METHODID_EXCHANGE:
          serviceImpl.exchange((io.imulab.astrea.sdk.flow.TokenRequest) request,
              (io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.TokenResponse>) responseObserver);
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

  private static abstract class AuthorizeCodeFlowBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AuthorizeCodeFlowBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.imulab.astrea.sdk.flow.AuthorizeCodeFlowProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AuthorizeCodeFlow");
    }
  }

  private static final class AuthorizeCodeFlowFileDescriptorSupplier
      extends AuthorizeCodeFlowBaseDescriptorSupplier {
    AuthorizeCodeFlowFileDescriptorSupplier() {}
  }

  private static final class AuthorizeCodeFlowMethodDescriptorSupplier
      extends AuthorizeCodeFlowBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AuthorizeCodeFlowMethodDescriptorSupplier(String methodName) {
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
      synchronized (AuthorizeCodeFlowGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AuthorizeCodeFlowFileDescriptorSupplier())
              .addMethod(getAuthorizeMethod())
              .addMethod(getExchangeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
