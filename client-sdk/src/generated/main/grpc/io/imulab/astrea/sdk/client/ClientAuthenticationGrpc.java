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
public final class ClientAuthenticationGrpc {

  private ClientAuthenticationGrpc() {}

  public static final String SERVICE_NAME = "client.ClientAuthentication";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.imulab.astrea.sdk.client.ClientAuthenticateRequest,
      io.imulab.astrea.sdk.client.ClientLookupResponse> getAuthenticateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Authenticate",
      requestType = io.imulab.astrea.sdk.client.ClientAuthenticateRequest.class,
      responseType = io.imulab.astrea.sdk.client.ClientLookupResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.imulab.astrea.sdk.client.ClientAuthenticateRequest,
      io.imulab.astrea.sdk.client.ClientLookupResponse> getAuthenticateMethod() {
    io.grpc.MethodDescriptor<io.imulab.astrea.sdk.client.ClientAuthenticateRequest, io.imulab.astrea.sdk.client.ClientLookupResponse> getAuthenticateMethod;
    if ((getAuthenticateMethod = ClientAuthenticationGrpc.getAuthenticateMethod) == null) {
      synchronized (ClientAuthenticationGrpc.class) {
        if ((getAuthenticateMethod = ClientAuthenticationGrpc.getAuthenticateMethod) == null) {
          ClientAuthenticationGrpc.getAuthenticateMethod = getAuthenticateMethod = 
              io.grpc.MethodDescriptor.<io.imulab.astrea.sdk.client.ClientAuthenticateRequest, io.imulab.astrea.sdk.client.ClientLookupResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "client.ClientAuthentication", "Authenticate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.client.ClientAuthenticateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.imulab.astrea.sdk.client.ClientLookupResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ClientAuthenticationMethodDescriptorSupplier("Authenticate"))
                  .build();
          }
        }
     }
     return getAuthenticateMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ClientAuthenticationStub newStub(io.grpc.Channel channel) {
    return new ClientAuthenticationStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ClientAuthenticationBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ClientAuthenticationBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ClientAuthenticationFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ClientAuthenticationFutureStub(channel);
  }

  /**
   */
  public static abstract class ClientAuthenticationImplBase implements io.grpc.BindableService {

    /**
     */
    public void authenticate(io.imulab.astrea.sdk.client.ClientAuthenticateRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.client.ClientLookupResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAuthenticateMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAuthenticateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.imulab.astrea.sdk.client.ClientAuthenticateRequest,
                io.imulab.astrea.sdk.client.ClientLookupResponse>(
                  this, METHODID_AUTHENTICATE)))
          .build();
    }
  }

  /**
   */
  public static final class ClientAuthenticationStub extends io.grpc.stub.AbstractStub<ClientAuthenticationStub> {
    private ClientAuthenticationStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientAuthenticationStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientAuthenticationStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientAuthenticationStub(channel, callOptions);
    }

    /**
     */
    public void authenticate(io.imulab.astrea.sdk.client.ClientAuthenticateRequest request,
        io.grpc.stub.StreamObserver<io.imulab.astrea.sdk.client.ClientLookupResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAuthenticateMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ClientAuthenticationBlockingStub extends io.grpc.stub.AbstractStub<ClientAuthenticationBlockingStub> {
    private ClientAuthenticationBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientAuthenticationBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientAuthenticationBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientAuthenticationBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.imulab.astrea.sdk.client.ClientLookupResponse authenticate(io.imulab.astrea.sdk.client.ClientAuthenticateRequest request) {
      return blockingUnaryCall(
          getChannel(), getAuthenticateMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ClientAuthenticationFutureStub extends io.grpc.stub.AbstractStub<ClientAuthenticationFutureStub> {
    private ClientAuthenticationFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ClientAuthenticationFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ClientAuthenticationFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ClientAuthenticationFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.imulab.astrea.sdk.client.ClientLookupResponse> authenticate(
        io.imulab.astrea.sdk.client.ClientAuthenticateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAuthenticateMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_AUTHENTICATE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ClientAuthenticationImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ClientAuthenticationImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_AUTHENTICATE:
          serviceImpl.authenticate((io.imulab.astrea.sdk.client.ClientAuthenticateRequest) request,
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

  private static abstract class ClientAuthenticationBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ClientAuthenticationBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.imulab.astrea.sdk.client.ClientProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ClientAuthentication");
    }
  }

  private static final class ClientAuthenticationFileDescriptorSupplier
      extends ClientAuthenticationBaseDescriptorSupplier {
    ClientAuthenticationFileDescriptorSupplier() {}
  }

  private static final class ClientAuthenticationMethodDescriptorSupplier
      extends ClientAuthenticationBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ClientAuthenticationMethodDescriptorSupplier(String methodName) {
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
      synchronized (ClientAuthenticationGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ClientAuthenticationFileDescriptorSupplier())
              .addMethod(getAuthenticateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
