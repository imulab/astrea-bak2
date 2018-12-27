package io.imulab.astrea.sdk.flow.cc;

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
    comments = "Source: client_credentials_service.proto")
public final class ClientCredentialsFlowGrpc {

  private ClientCredentialsFlowGrpc() {}

  public static final String SERVICE_NAME = "flow.ClientCredentialsFlow";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest,
      io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse> getExchangeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "exchange",
      requestType = io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest.class,
      responseType = io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest,
      io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse> getExchangeMethod() {
    io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest, io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse> getExchangeMethod;
    if ((getExchangeMethod = ClientCredentialsFlowGrpc.getExchangeMethod) == null) {
      synchronized (ClientCredentialsFlowGrpc.class) {
        if ((getExchangeMethod = ClientCredentialsFlowGrpc.getExchangeMethod) == null) {
          ClientCredentialsFlowGrpc.getExchangeMethod = getExchangeMethod = 
              io.grpc.MethodDescriptor.<io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest, io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "flow.ClientCredentialsFlow", "exchange"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ClientCredentialsFlowMethodDescriptorSupplier("exchange"))
                  .build();
          }
        }
     }
     return getExchangeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ClientCredentialsFlowStub newStub(io.grpc.Channel channel) {
    return new ClientCredentialsFlowStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ClientCredentialsFlowBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ClientCredentialsFlowBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ClientCredentialsFlowFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ClientCredentialsFlowFutureStub(channel);
  }

  /**
   */
  public static abstract class ClientCredentialsFlowImplBase implements io.grpc.BindableService {

    /**
     */
    public void exchange(io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getExchangeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getExchangeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest,
                io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse>(
                  this, METHODID_EXCHANGE)))
          .build();
    }
  }

  /**
   */
  public static final class ClientCredentialsFlowStub extends io.grpc.stub.AbstractStub<ClientCredentialsFlowStub> {
    private ClientCredentialsFlowStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientCredentialsFlowStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientCredentialsFlowStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientCredentialsFlowStub(channel, callOptions);
    }

    /**
     */
    public void exchange(io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getExchangeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ClientCredentialsFlowBlockingStub extends io.grpc.stub.AbstractStub<ClientCredentialsFlowBlockingStub> {
    private ClientCredentialsFlowBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientCredentialsFlowBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientCredentialsFlowBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientCredentialsFlowBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse exchange(io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest request) {
      return blockingUnaryCall(
          getChannel(), getExchangeMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ClientCredentialsFlowFutureStub extends io.grpc.stub.AbstractStub<ClientCredentialsFlowFutureStub> {
    private ClientCredentialsFlowFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientCredentialsFlowFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientCredentialsFlowFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientCredentialsFlowFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse> exchange(
        io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getExchangeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXCHANGE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ClientCredentialsFlowImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ClientCredentialsFlowImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXCHANGE:
          serviceImpl.exchange((io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest) request,
              (io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse>) responseObserver);
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

  private static abstract class ClientCredentialsFlowBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ClientCredentialsFlowBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.imulab.astrea.sdk.flow.cc.ClientCredentialsFlowProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ClientCredentialsFlow");
    }
  }

  private static final class ClientCredentialsFlowFileDescriptorSupplier
      extends ClientCredentialsFlowBaseDescriptorSupplier {
    ClientCredentialsFlowFileDescriptorSupplier() {}
  }

  private static final class ClientCredentialsFlowMethodDescriptorSupplier
      extends ClientCredentialsFlowBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ClientCredentialsFlowMethodDescriptorSupplier(String methodName) {
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
      synchronized (ClientCredentialsFlowGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ClientCredentialsFlowFileDescriptorSupplier())
              .addMethod(getExchangeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
