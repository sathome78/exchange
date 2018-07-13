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
public final class MessageVerificationServiceGrpc {

  private MessageVerificationServiceGrpc() {}

  public static final String SERVICE_NAME = "walletrpc.MessageVerificationService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getVerifyMessageMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VerifyMessageRequest,
      me.exrates.service.decred.rpc.Api.VerifyMessageResponse> METHOD_VERIFY_MESSAGE = getVerifyMessageMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VerifyMessageRequest,
      me.exrates.service.decred.rpc.Api.VerifyMessageResponse> getVerifyMessageMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VerifyMessageRequest,
      me.exrates.service.decred.rpc.Api.VerifyMessageResponse> getVerifyMessageMethod() {
    return getVerifyMessageMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VerifyMessageRequest,
      me.exrates.service.decred.rpc.Api.VerifyMessageResponse> getVerifyMessageMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VerifyMessageRequest, me.exrates.service.decred.rpc.Api.VerifyMessageResponse> getVerifyMessageMethod;
    if ((getVerifyMessageMethod = MessageVerificationServiceGrpc.getVerifyMessageMethod) == null) {
      synchronized (MessageVerificationServiceGrpc.class) {
        if ((getVerifyMessageMethod = MessageVerificationServiceGrpc.getVerifyMessageMethod) == null) {
          MessageVerificationServiceGrpc.getVerifyMessageMethod = getVerifyMessageMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.VerifyMessageRequest, me.exrates.service.decred.rpc.Api.VerifyMessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.MessageVerificationService", "VerifyMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.VerifyMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.VerifyMessageResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new MessageVerificationServiceMethodDescriptorSupplier("VerifyMessage"))
                  .build();
          }
        }
     }
     return getVerifyMessageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MessageVerificationServiceStub newStub(io.grpc.Channel channel) {
    return new MessageVerificationServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MessageVerificationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new MessageVerificationServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MessageVerificationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new MessageVerificationServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class MessageVerificationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void verifyMessage(me.exrates.service.decred.rpc.Api.VerifyMessageRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.VerifyMessageResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getVerifyMessageMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getVerifyMessageMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.VerifyMessageRequest,
                me.exrates.service.decred.rpc.Api.VerifyMessageResponse>(
                  this, METHODID_VERIFY_MESSAGE)))
          .build();
    }
  }

  /**
   */
  public static final class MessageVerificationServiceStub extends io.grpc.stub.AbstractStub<MessageVerificationServiceStub> {
    private MessageVerificationServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MessageVerificationServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MessageVerificationServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MessageVerificationServiceStub(channel, callOptions);
    }

    /**
     */
    public void verifyMessage(me.exrates.service.decred.rpc.Api.VerifyMessageRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.VerifyMessageResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getVerifyMessageMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MessageVerificationServiceBlockingStub extends io.grpc.stub.AbstractStub<MessageVerificationServiceBlockingStub> {
    private MessageVerificationServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MessageVerificationServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MessageVerificationServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MessageVerificationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.VerifyMessageResponse verifyMessage(me.exrates.service.decred.rpc.Api.VerifyMessageRequest request) {
      return blockingUnaryCall(
          getChannel(), getVerifyMessageMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MessageVerificationServiceFutureStub extends io.grpc.stub.AbstractStub<MessageVerificationServiceFutureStub> {
    private MessageVerificationServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MessageVerificationServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MessageVerificationServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MessageVerificationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.VerifyMessageResponse> verifyMessage(
        me.exrates.service.decred.rpc.Api.VerifyMessageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getVerifyMessageMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VERIFY_MESSAGE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MessageVerificationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MessageVerificationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VERIFY_MESSAGE:
          serviceImpl.verifyMessage((me.exrates.service.decred.rpc.Api.VerifyMessageRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.VerifyMessageResponse>) responseObserver);
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

  private static abstract class MessageVerificationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MessageVerificationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.rpc.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MessageVerificationService");
    }
  }

  private static final class MessageVerificationServiceFileDescriptorSupplier
      extends MessageVerificationServiceBaseDescriptorSupplier {
    MessageVerificationServiceFileDescriptorSupplier() {}
  }

  private static final class MessageVerificationServiceMethodDescriptorSupplier
      extends MessageVerificationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    MessageVerificationServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (MessageVerificationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MessageVerificationServiceFileDescriptorSupplier())
              .addMethod(getVerifyMessageMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
