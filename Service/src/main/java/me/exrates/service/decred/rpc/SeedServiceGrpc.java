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
public final class SeedServiceGrpc {

  private SeedServiceGrpc() {}

  public static final String SERVICE_NAME = "walletrpc.SeedService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getGenerateRandomSeedMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest,
      me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse> METHOD_GENERATE_RANDOM_SEED = getGenerateRandomSeedMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest,
      me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse> getGenerateRandomSeedMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest,
      me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse> getGenerateRandomSeedMethod() {
    return getGenerateRandomSeedMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest,
      me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse> getGenerateRandomSeedMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest, me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse> getGenerateRandomSeedMethod;
    if ((getGenerateRandomSeedMethod = SeedServiceGrpc.getGenerateRandomSeedMethod) == null) {
      synchronized (SeedServiceGrpc.class) {
        if ((getGenerateRandomSeedMethod = SeedServiceGrpc.getGenerateRandomSeedMethod) == null) {
          SeedServiceGrpc.getGenerateRandomSeedMethod = getGenerateRandomSeedMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest, me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.SeedService", "GenerateRandomSeed"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new SeedServiceMethodDescriptorSupplier("GenerateRandomSeed"))
                  .build();
          }
        }
     }
     return getGenerateRandomSeedMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getDecodeSeedMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeSeedRequest,
      me.exrates.service.decred.rpc.Api.DecodeSeedResponse> METHOD_DECODE_SEED = getDecodeSeedMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeSeedRequest,
      me.exrates.service.decred.rpc.Api.DecodeSeedResponse> getDecodeSeedMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeSeedRequest,
      me.exrates.service.decred.rpc.Api.DecodeSeedResponse> getDecodeSeedMethod() {
    return getDecodeSeedMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeSeedRequest,
      me.exrates.service.decred.rpc.Api.DecodeSeedResponse> getDecodeSeedMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DecodeSeedRequest, me.exrates.service.decred.rpc.Api.DecodeSeedResponse> getDecodeSeedMethod;
    if ((getDecodeSeedMethod = SeedServiceGrpc.getDecodeSeedMethod) == null) {
      synchronized (SeedServiceGrpc.class) {
        if ((getDecodeSeedMethod = SeedServiceGrpc.getDecodeSeedMethod) == null) {
          SeedServiceGrpc.getDecodeSeedMethod = getDecodeSeedMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.DecodeSeedRequest, me.exrates.service.decred.rpc.Api.DecodeSeedResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.SeedService", "DecodeSeed"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.DecodeSeedRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.DecodeSeedResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new SeedServiceMethodDescriptorSupplier("DecodeSeed"))
                  .build();
          }
        }
     }
     return getDecodeSeedMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SeedServiceStub newStub(io.grpc.Channel channel) {
    return new SeedServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SeedServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SeedServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SeedServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SeedServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class SeedServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void generateRandomSeed(me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGenerateRandomSeedMethodHelper(), responseObserver);
    }

    /**
     */
    public void decodeSeed(me.exrates.service.decred.rpc.Api.DecodeSeedRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DecodeSeedResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDecodeSeedMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGenerateRandomSeedMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest,
                me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse>(
                  this, METHODID_GENERATE_RANDOM_SEED)))
          .addMethod(
            getDecodeSeedMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.DecodeSeedRequest,
                me.exrates.service.decred.rpc.Api.DecodeSeedResponse>(
                  this, METHODID_DECODE_SEED)))
          .build();
    }
  }

  /**
   */
  public static final class SeedServiceStub extends io.grpc.stub.AbstractStub<SeedServiceStub> {
    private SeedServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SeedServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SeedServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SeedServiceStub(channel, callOptions);
    }

    /**
     */
    public void generateRandomSeed(me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGenerateRandomSeedMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void decodeSeed(me.exrates.service.decred.rpc.Api.DecodeSeedRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DecodeSeedResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDecodeSeedMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SeedServiceBlockingStub extends io.grpc.stub.AbstractStub<SeedServiceBlockingStub> {
    private SeedServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SeedServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SeedServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SeedServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse generateRandomSeed(me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest request) {
      return blockingUnaryCall(
          getChannel(), getGenerateRandomSeedMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.DecodeSeedResponse decodeSeed(me.exrates.service.decred.rpc.Api.DecodeSeedRequest request) {
      return blockingUnaryCall(
          getChannel(), getDecodeSeedMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SeedServiceFutureStub extends io.grpc.stub.AbstractStub<SeedServiceFutureStub> {
    private SeedServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SeedServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SeedServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SeedServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse> generateRandomSeed(
        me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGenerateRandomSeedMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.DecodeSeedResponse> decodeSeed(
        me.exrates.service.decred.rpc.Api.DecodeSeedRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDecodeSeedMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GENERATE_RANDOM_SEED = 0;
  private static final int METHODID_DECODE_SEED = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SeedServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SeedServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GENERATE_RANDOM_SEED:
          serviceImpl.generateRandomSeed((me.exrates.service.decred.rpc.Api.GenerateRandomSeedRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GenerateRandomSeedResponse>) responseObserver);
          break;
        case METHODID_DECODE_SEED:
          serviceImpl.decodeSeed((me.exrates.service.decred.rpc.Api.DecodeSeedRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DecodeSeedResponse>) responseObserver);
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

  private static abstract class SeedServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SeedServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.rpc.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SeedService");
    }
  }

  private static final class SeedServiceFileDescriptorSupplier
      extends SeedServiceBaseDescriptorSupplier {
    SeedServiceFileDescriptorSupplier() {}
  }

  private static final class SeedServiceMethodDescriptorSupplier
      extends SeedServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SeedServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (SeedServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SeedServiceFileDescriptorSupplier())
              .addMethod(getGenerateRandomSeedMethodHelper())
              .addMethod(getDecodeSeedMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
