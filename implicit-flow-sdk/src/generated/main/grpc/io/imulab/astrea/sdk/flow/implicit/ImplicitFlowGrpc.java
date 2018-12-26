package io.imulab.astrea.sdk.flow.implicit;

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
    comments = "Source: implicit_service.proto")
public final class ImplicitFlowGrpc {

  private ImplicitFlowGrpc() {}

  public static final String SERVICE_NAME = "client.ImplicitFlow";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest,
      io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse> getAuthorizeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Authorize",
      requestType = io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest.class,
      responseType = io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest,
      io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse> getAuthorizeMethod() {
    io.grpc.MethodDescriptor<io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest, io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse> getAuthorizeMethod;
    if ((getAuthorizeMethod = ImplicitFlowGrpc.getAuthorizeMethod) == null) {
      synchronized (ImplicitFlowGrpc.class) {
        if ((getAuthorizeMethod = ImplicitFlowGrpc.getAuthorizeMethod) == null) {
          ImplicitFlowGrpc.getAuthorizeMethod = getAuthorizeMethod = 
              io.grpc.MethodDescriptor.<io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest, io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "client.ImplicitFlow", "Authorize"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ImplicitFlowMethodDescriptorSupplier("Authorize"))
                  .build();
          }
        }
     }
     return getAuthorizeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ImplicitFlowStub newStub(io.grpc.Channel channel) {
    return new ImplicitFlowStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ImplicitFlowBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ImplicitFlowBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ImplicitFlowFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ImplicitFlowFutureStub(channel);
  }

  /**
   */
  public static abstract class ImplicitFlowImplBase implements io.grpc.BindableService {

    /**
     */
    public void authorize(io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAuthorizeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAuthorizeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest,
                io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse>(
                  this, METHODID_AUTHORIZE)))
          .build();
    }
  }

  /**
   */
  public static final class ImplicitFlowStub extends io.grpc.stub.AbstractStub<ImplicitFlowStub> {
    private ImplicitFlowStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ImplicitFlowStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImplicitFlowStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ImplicitFlowStub(channel, callOptions);
    }

    /**
     */
    public void authorize(io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAuthorizeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ImplicitFlowBlockingStub extends io.grpc.stub.AbstractStub<ImplicitFlowBlockingStub> {
    private ImplicitFlowBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ImplicitFlowBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImplicitFlowBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ImplicitFlowBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse authorize(io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest request) {
      return blockingUnaryCall(
          getChannel(), getAuthorizeMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ImplicitFlowFutureStub extends io.grpc.stub.AbstractStub<ImplicitFlowFutureStub> {
    private ImplicitFlowFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ImplicitFlowFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ImplicitFlowFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ImplicitFlowFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse> authorize(
        io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAuthorizeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_AUTHORIZE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ImplicitFlowImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ImplicitFlowImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_AUTHORIZE:
          serviceImpl.authorize((io.imulab.astrea.sdk.flow.implicit.ImplicitTokenRequest) request,
              (io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse>) responseObserver);
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

  private static abstract class ImplicitFlowBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ImplicitFlowBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.imulab.astrea.sdk.flow.implicit.ImplicitFlowProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ImplicitFlow");
    }
  }

  private static final class ImplicitFlowFileDescriptorSupplier
      extends ImplicitFlowBaseDescriptorSupplier {
    ImplicitFlowFileDescriptorSupplier() {}
  }

  private static final class ImplicitFlowMethodDescriptorSupplier
      extends ImplicitFlowBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ImplicitFlowMethodDescriptorSupplier(String methodName) {
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
      synchronized (ImplicitFlowGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ImplicitFlowFileDescriptorSupplier())
              .addMethod(getAuthorizeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
