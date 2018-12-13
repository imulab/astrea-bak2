package io.imulab.astrea.sdk.discovery;

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
    comments = "Source: discovery_service.proto")
public final class DiscoveryGrpc {

  private DiscoveryGrpc() {}

  public static final String SERVICE_NAME = "discovery.Discovery";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.imulab.astrea.sdk.discovery.DiscoveryRequest,
      io.imulab.astrea.sdk.discovery.DiscoveryResponse> getGetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Get",
      requestType = io.imulab.astrea.sdk.discovery.DiscoveryRequest.class,
      responseType = io.imulab.astrea.sdk.discovery.DiscoveryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.imulab.astrea.sdk.discovery.DiscoveryRequest,
      io.imulab.astrea.sdk.discovery.DiscoveryResponse> getGetMethod() {
    io.grpc.MethodDescriptor<io.imulab.astrea.sdk.discovery.DiscoveryRequest, io.imulab.astrea.sdk.discovery.DiscoveryResponse> getGetMethod;
    if ((getGetMethod = DiscoveryGrpc.getGetMethod) == null) {
      synchronized (DiscoveryGrpc.class) {
        if ((getGetMethod = DiscoveryGrpc.getGetMethod) == null) {
          DiscoveryGrpc.getGetMethod = getGetMethod = 
              io.grpc.MethodDescriptor.<io.imulab.astrea.sdk.discovery.DiscoveryRequest, io.imulab.astrea.sdk.discovery.DiscoveryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "discovery.Discovery", "Get"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.discovery.DiscoveryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.discovery.DiscoveryResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DiscoveryMethodDescriptorSupplier("Get"))
                  .build();
          }
        }
     }
     return getGetMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DiscoveryStub newStub(io.grpc.Channel channel) {
    return new DiscoveryStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DiscoveryBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DiscoveryBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DiscoveryFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DiscoveryFutureStub(channel);
  }

  /**
   */
  public static abstract class DiscoveryImplBase implements io.grpc.BindableService {

    /**
     */
    public void get(io.imulab.astrea.sdk.discovery.DiscoveryRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.discovery.DiscoveryResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.imulab.astrea.sdk.discovery.DiscoveryRequest,
                io.imulab.astrea.sdk.discovery.DiscoveryResponse>(
                  this, METHODID_GET)))
          .build();
    }
  }

  /**
   */
  public static final class DiscoveryStub extends io.grpc.stub.AbstractStub<DiscoveryStub> {
    private DiscoveryStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DiscoveryStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DiscoveryStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DiscoveryStub(channel, callOptions);
    }

    /**
     */
    public void get(io.imulab.astrea.sdk.discovery.DiscoveryRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.discovery.DiscoveryResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DiscoveryBlockingStub extends io.grpc.stub.AbstractStub<DiscoveryBlockingStub> {
    private DiscoveryBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DiscoveryBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DiscoveryBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DiscoveryBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.imulab.astrea.sdk.discovery.DiscoveryResponse get(io.imulab.astrea.sdk.discovery.DiscoveryRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DiscoveryFutureStub extends io.grpc.stub.AbstractStub<DiscoveryFutureStub> {
    private DiscoveryFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DiscoveryFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DiscoveryFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DiscoveryFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.imulab.astrea.sdk.discovery.DiscoveryResponse> get(
        io.imulab.astrea.sdk.discovery.DiscoveryRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DiscoveryImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DiscoveryImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET:
          serviceImpl.get((io.imulab.astrea.sdk.discovery.DiscoveryRequest) request,
              (io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.discovery.DiscoveryResponse>) responseObserver);
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

  private static abstract class DiscoveryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DiscoveryBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.imulab.astrea.sdk.discovery.DiscoveryProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Discovery");
    }
  }

  private static final class DiscoveryFileDescriptorSupplier
      extends DiscoveryBaseDescriptorSupplier {
    DiscoveryFileDescriptorSupplier() {}
  }

  private static final class DiscoveryMethodDescriptorSupplier
      extends DiscoveryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DiscoveryMethodDescriptorSupplier(String methodName) {
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
      synchronized (DiscoveryGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DiscoveryFileDescriptorSupplier())
              .addMethod(getGetMethod())
              .build();
        }
      }
    }
    return result;
  }
}
