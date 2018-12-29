package io.imulab.astrea.sdk.client;

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
    comments = "Source: client_service.proto")
public final class ClientLookupGrpc {

  private ClientLookupGrpc() {}

  public static final String SERVICE_NAME = "client.ClientLookup";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.imulab.astrea.sdk.client.ClientLookupRequest,
      io.imulab.astrea.sdk.client.ClientLookupResponse> getFindMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Find",
      requestType = io.imulab.astrea.sdk.client.ClientLookupRequest.class,
      responseType = io.imulab.astrea.sdk.client.ClientLookupResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.imulab.astrea.sdk.client.ClientLookupRequest,
      io.imulab.astrea.sdk.client.ClientLookupResponse> getFindMethod() {
    io.grpc.MethodDescriptor<io.imulab.astrea.sdk.client.ClientLookupRequest, io.imulab.astrea.sdk.client.ClientLookupResponse> getFindMethod;
    if ((getFindMethod = ClientLookupGrpc.getFindMethod) == null) {
      synchronized (ClientLookupGrpc.class) {
        if ((getFindMethod = ClientLookupGrpc.getFindMethod) == null) {
          ClientLookupGrpc.getFindMethod = getFindMethod = 
              io.grpc.MethodDescriptor.<io.imulab.astrea.sdk.client.ClientLookupRequest, io.imulab.astrea.sdk.client.ClientLookupResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "client.ClientLookup", "Find"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.client.ClientLookupRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.client.ClientLookupResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ClientLookupMethodDescriptorSupplier("Find"))
                  .build();
          }
        }
     }
     return getFindMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ClientLookupStub newStub(io.grpc.Channel channel) {
    return new ClientLookupStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ClientLookupBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ClientLookupBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ClientLookupFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ClientLookupFutureStub(channel);
  }

  /**
   */
  public static abstract class ClientLookupImplBase implements io.grpc.BindableService {

    /**
     */
    public void find(io.imulab.astrea.sdk.client.ClientLookupRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.client.ClientLookupResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFindMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getFindMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.imulab.astrea.sdk.client.ClientLookupRequest,
                io.imulab.astrea.sdk.client.ClientLookupResponse>(
                  this, METHODID_FIND)))
          .build();
    }
  }

  /**
   */
  public static final class ClientLookupStub extends io.grpc.stub.AbstractStub<ClientLookupStub> {
    private ClientLookupStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientLookupStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientLookupStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientLookupStub(channel, callOptions);
    }

    /**
     */
    public void find(io.imulab.astrea.sdk.client.ClientLookupRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.client.ClientLookupResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFindMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ClientLookupBlockingStub extends io.grpc.stub.AbstractStub<ClientLookupBlockingStub> {
    private ClientLookupBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientLookupBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientLookupBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientLookupBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.imulab.astrea.sdk.client.ClientLookupResponse find(io.imulab.astrea.sdk.client.ClientLookupRequest request) {
      return blockingUnaryCall(
          getChannel(), getFindMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ClientLookupFutureStub extends io.grpc.stub.AbstractStub<ClientLookupFutureStub> {
    private ClientLookupFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientLookupFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientLookupFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientLookupFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.imulab.astrea.sdk.client.ClientLookupResponse> find(
        io.imulab.astrea.sdk.client.ClientLookupRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFindMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_FIND = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ClientLookupImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ClientLookupImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FIND:
          serviceImpl.find((io.imulab.astrea.sdk.client.ClientLookupRequest) request,
              (io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.client.ClientLookupResponse>) responseObserver);
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

  private static abstract class ClientLookupBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ClientLookupBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.imulab.astrea.sdk.client.ClientProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ClientLookup");
    }
  }

  private static final class ClientLookupFileDescriptorSupplier
      extends ClientLookupBaseDescriptorSupplier {
    ClientLookupFileDescriptorSupplier() {}
  }

  private static final class ClientLookupMethodDescriptorSupplier
      extends ClientLookupBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ClientLookupMethodDescriptorSupplier(String methodName) {
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
      synchronized (ClientLookupGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ClientLookupFileDescriptorSupplier())
              .addMethod(getFindMethod())
              .build();
        }
      }
    }
    return result;
  }
}
