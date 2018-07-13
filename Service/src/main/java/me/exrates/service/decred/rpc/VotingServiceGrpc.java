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
public final class VotingServiceGrpc {

  private VotingServiceGrpc() {}

  public static final String SERVICE_NAME = "walletrpc.VotingService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getVoteChoicesMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VoteChoicesRequest,
      me.exrates.service.decred.rpc.Api.VoteChoicesResponse> METHOD_VOTE_CHOICES = getVoteChoicesMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VoteChoicesRequest,
      me.exrates.service.decred.rpc.Api.VoteChoicesResponse> getVoteChoicesMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VoteChoicesRequest,
      me.exrates.service.decred.rpc.Api.VoteChoicesResponse> getVoteChoicesMethod() {
    return getVoteChoicesMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VoteChoicesRequest,
      me.exrates.service.decred.rpc.Api.VoteChoicesResponse> getVoteChoicesMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.VoteChoicesRequest, me.exrates.service.decred.rpc.Api.VoteChoicesResponse> getVoteChoicesMethod;
    if ((getVoteChoicesMethod = VotingServiceGrpc.getVoteChoicesMethod) == null) {
      synchronized (VotingServiceGrpc.class) {
        if ((getVoteChoicesMethod = VotingServiceGrpc.getVoteChoicesMethod) == null) {
          VotingServiceGrpc.getVoteChoicesMethod = getVoteChoicesMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.VoteChoicesRequest, me.exrates.service.decred.rpc.Api.VoteChoicesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.VotingService", "VoteChoices"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.VoteChoicesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.VoteChoicesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new VotingServiceMethodDescriptorSupplier("VoteChoices"))
                  .build();
          }
        }
     }
     return getVoteChoicesMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetVoteChoicesMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest,
      me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse> METHOD_SET_VOTE_CHOICES = getSetVoteChoicesMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest,
      me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse> getSetVoteChoicesMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest,
      me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse> getSetVoteChoicesMethod() {
    return getSetVoteChoicesMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest,
      me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse> getSetVoteChoicesMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest, me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse> getSetVoteChoicesMethod;
    if ((getSetVoteChoicesMethod = VotingServiceGrpc.getSetVoteChoicesMethod) == null) {
      synchronized (VotingServiceGrpc.class) {
        if ((getSetVoteChoicesMethod = VotingServiceGrpc.getSetVoteChoicesMethod) == null) {
          VotingServiceGrpc.getSetVoteChoicesMethod = getSetVoteChoicesMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest, me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.VotingService", "SetVoteChoices"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new VotingServiceMethodDescriptorSupplier("SetVoteChoices"))
                  .build();
          }
        }
     }
     return getSetVoteChoicesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VotingServiceStub newStub(io.grpc.Channel channel) {
    return new VotingServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VotingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new VotingServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VotingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new VotingServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class VotingServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void voteChoices(me.exrates.service.decred.rpc.Api.VoteChoicesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.VoteChoicesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getVoteChoicesMethodHelper(), responseObserver);
    }

    /**
     */
    public void setVoteChoices(me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetVoteChoicesMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getVoteChoicesMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.VoteChoicesRequest,
                me.exrates.service.decred.rpc.Api.VoteChoicesResponse>(
                  this, METHODID_VOTE_CHOICES)))
          .addMethod(
            getSetVoteChoicesMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest,
                me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse>(
                  this, METHODID_SET_VOTE_CHOICES)))
          .build();
    }
  }

  /**
   */
  public static final class VotingServiceStub extends io.grpc.stub.AbstractStub<VotingServiceStub> {
    private VotingServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private VotingServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VotingServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VotingServiceStub(channel, callOptions);
    }

    /**
     */
    public void voteChoices(me.exrates.service.decred.rpc.Api.VoteChoicesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.VoteChoicesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getVoteChoicesMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setVoteChoices(me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetVoteChoicesMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class VotingServiceBlockingStub extends io.grpc.stub.AbstractStub<VotingServiceBlockingStub> {
    private VotingServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private VotingServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VotingServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VotingServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.VoteChoicesResponse voteChoices(me.exrates.service.decred.rpc.Api.VoteChoicesRequest request) {
      return blockingUnaryCall(
          getChannel(), getVoteChoicesMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse setVoteChoices(me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetVoteChoicesMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class VotingServiceFutureStub extends io.grpc.stub.AbstractStub<VotingServiceFutureStub> {
    private VotingServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private VotingServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VotingServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VotingServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.VoteChoicesResponse> voteChoices(
        me.exrates.service.decred.rpc.Api.VoteChoicesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getVoteChoicesMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse> setVoteChoices(
        me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetVoteChoicesMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VOTE_CHOICES = 0;
  private static final int METHODID_SET_VOTE_CHOICES = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final VotingServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(VotingServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VOTE_CHOICES:
          serviceImpl.voteChoices((me.exrates.service.decred.rpc.Api.VoteChoicesRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.VoteChoicesResponse>) responseObserver);
          break;
        case METHODID_SET_VOTE_CHOICES:
          serviceImpl.setVoteChoices((me.exrates.service.decred.rpc.Api.SetVoteChoicesRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetVoteChoicesResponse>) responseObserver);
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

  private static abstract class VotingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    VotingServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.rpc.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("VotingService");
    }
  }

  private static final class VotingServiceFileDescriptorSupplier
      extends VotingServiceBaseDescriptorSupplier {
    VotingServiceFileDescriptorSupplier() {}
  }

  private static final class VotingServiceMethodDescriptorSupplier
      extends VotingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    VotingServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (VotingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VotingServiceFileDescriptorSupplier())
              .addMethod(getVoteChoicesMethodHelper())
              .addMethod(getSetVoteChoicesMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
