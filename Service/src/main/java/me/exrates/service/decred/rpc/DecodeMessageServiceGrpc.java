package me.exrates.service.decred.rpc;

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
    value = "by gRPC proto compiler (version 1.10.1)",
    comments = "Source: api.proto")
public final class DecodeMessageServiceGrpc {

  private DecodeMessageServiceGrpc() {}

  public static final String SERVICE_NAME = "walletrpc.DecodeMessageService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getDecodeRawTransactionMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest,
      me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse> METHOD_DECODE_RAW_TRANSACTION = getDecodeRawTransactionMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest,
      me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse> getDecodeRawTransactionMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest,
      me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse> getDecodeRawTransactionMethod() {
    return getDecodeRawTransactionMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest,
      me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse> getDecodeRawTransactionMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest, me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse> getDecodeRawTransactionMethod;
    if ((getDecodeRawTransactionMethod = DecodeMessageServiceGrpc.getDecodeRawTransactionMethod) == null) {
      synchronized (DecodeMessageServiceGrpc.class) {
        if ((getDecodeRawTransactionMethod = DecodeMessageServiceGrpc.getDecodeRawTransactionMethod) == null) {
          DecodeMessageServiceGrpc.getDecodeRawTransactionMethod = getDecodeRawTransactionMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest, me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.DecodeMessageService", "DecodeRawTransaction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DecodeMessageServiceMethodDescriptorSupplier("DecodeRawTransaction"))
                  .build();
          }
        }
     }
     return getDecodeRawTransactionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DecodeMessageServiceStub newStub(io.grpc.Channel channel) {
    return new DecodeMessageServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DecodeMessageServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DecodeMessageServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DecodeMessageServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DecodeMessageServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class DecodeMessageServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void decodeRawTransaction(me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDecodeRawTransactionMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getDecodeRawTransactionMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest,
                me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse>(
                  this, METHODID_DECODE_RAW_TRANSACTION)))
          .build();
    }
  }

  /**
   */
  public static final class DecodeMessageServiceStub extends io.grpc.stub.AbstractStub<DecodeMessageServiceStub> {
    private DecodeMessageServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DecodeMessageServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DecodeMessageServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DecodeMessageServiceStub(channel, callOptions);
    }

    /**
     */
    public void decodeRawTransaction(me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDecodeRawTransactionMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DecodeMessageServiceBlockingStub extends io.grpc.stub.AbstractStub<DecodeMessageServiceBlockingStub> {
    private DecodeMessageServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DecodeMessageServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DecodeMessageServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DecodeMessageServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse decodeRawTransaction(me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), getDecodeRawTransactionMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DecodeMessageServiceFutureStub extends io.grpc.stub.AbstractStub<DecodeMessageServiceFutureStub> {
    private DecodeMessageServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DecodeMessageServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DecodeMessageServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DecodeMessageServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse> decodeRawTransaction(
        me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDecodeRawTransactionMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DECODE_RAW_TRANSACTION = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DecodeMessageServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DecodeMessageServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DECODE_RAW_TRANSACTION:
          serviceImpl.decodeRawTransaction((me.exrates.service.decred.rpc.Api.DecodeRawTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DecodeRawTransactionResponse>) responseObserver);
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

  private static abstract class DecodeMessageServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DecodeMessageServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.rpc.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DecodeMessageService");
    }
  }

  private static final class DecodeMessageServiceFileDescriptorSupplier
      extends DecodeMessageServiceBaseDescriptorSupplier {
    DecodeMessageServiceFileDescriptorSupplier() {}
  }

  private static final class DecodeMessageServiceMethodDescriptorSupplier
      extends DecodeMessageServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DecodeMessageServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (DecodeMessageServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DecodeMessageServiceFileDescriptorSupplier())
              .addMethod(getDecodeRawTransactionMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
