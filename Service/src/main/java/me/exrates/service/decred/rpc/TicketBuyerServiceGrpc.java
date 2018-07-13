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
public final class TicketBuyerServiceGrpc {

  private TicketBuyerServiceGrpc() {}

  public static final String SERVICE_NAME = "walletrpc.TicketBuyerService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getStartAutoBuyerMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest,
      me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse> METHOD_START_AUTO_BUYER = getStartAutoBuyerMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest,
      me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse> getStartAutoBuyerMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest,
      me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse> getStartAutoBuyerMethod() {
    return getStartAutoBuyerMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest,
      me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse> getStartAutoBuyerMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest, me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse> getStartAutoBuyerMethod;
    if ((getStartAutoBuyerMethod = TicketBuyerServiceGrpc.getStartAutoBuyerMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getStartAutoBuyerMethod = TicketBuyerServiceGrpc.getStartAutoBuyerMethod) == null) {
          TicketBuyerServiceGrpc.getStartAutoBuyerMethod = getStartAutoBuyerMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest, me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "StartAutoBuyer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("StartAutoBuyer"))
                  .build();
          }
        }
     }
     return getStartAutoBuyerMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getStopAutoBuyerMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest,
      me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse> METHOD_STOP_AUTO_BUYER = getStopAutoBuyerMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest,
      me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse> getStopAutoBuyerMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest,
      me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse> getStopAutoBuyerMethod() {
    return getStopAutoBuyerMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest,
      me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse> getStopAutoBuyerMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest, me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse> getStopAutoBuyerMethod;
    if ((getStopAutoBuyerMethod = TicketBuyerServiceGrpc.getStopAutoBuyerMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getStopAutoBuyerMethod = TicketBuyerServiceGrpc.getStopAutoBuyerMethod) == null) {
          TicketBuyerServiceGrpc.getStopAutoBuyerMethod = getStopAutoBuyerMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest, me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "StopAutoBuyer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("StopAutoBuyer"))
                  .build();
          }
        }
     }
     return getStopAutoBuyerMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getTicketBuyerConfigMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest,
      me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse> METHOD_TICKET_BUYER_CONFIG = getTicketBuyerConfigMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest,
      me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse> getTicketBuyerConfigMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest,
      me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse> getTicketBuyerConfigMethod() {
    return getTicketBuyerConfigMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest,
      me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse> getTicketBuyerConfigMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest, me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse> getTicketBuyerConfigMethod;
    if ((getTicketBuyerConfigMethod = TicketBuyerServiceGrpc.getTicketBuyerConfigMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getTicketBuyerConfigMethod = TicketBuyerServiceGrpc.getTicketBuyerConfigMethod) == null) {
          TicketBuyerServiceGrpc.getTicketBuyerConfigMethod = getTicketBuyerConfigMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest, me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "TicketBuyerConfig"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("TicketBuyerConfig"))
                  .build();
          }
        }
     }
     return getTicketBuyerConfigMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetAccountMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetAccountRequest,
      me.exrates.service.decred.rpc.Api.SetAccountResponse> METHOD_SET_ACCOUNT = getSetAccountMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetAccountRequest,
      me.exrates.service.decred.rpc.Api.SetAccountResponse> getSetAccountMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetAccountRequest,
      me.exrates.service.decred.rpc.Api.SetAccountResponse> getSetAccountMethod() {
    return getSetAccountMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetAccountRequest,
      me.exrates.service.decred.rpc.Api.SetAccountResponse> getSetAccountMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetAccountRequest, me.exrates.service.decred.rpc.Api.SetAccountResponse> getSetAccountMethod;
    if ((getSetAccountMethod = TicketBuyerServiceGrpc.getSetAccountMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetAccountMethod = TicketBuyerServiceGrpc.getSetAccountMethod) == null) {
          TicketBuyerServiceGrpc.getSetAccountMethod = getSetAccountMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetAccountRequest, me.exrates.service.decred.rpc.Api.SetAccountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetAccountResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetAccount"))
                  .build();
          }
        }
     }
     return getSetAccountMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetBalanceToMaintainMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest,
      me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse> METHOD_SET_BALANCE_TO_MAINTAIN = getSetBalanceToMaintainMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest,
      me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse> getSetBalanceToMaintainMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest,
      me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse> getSetBalanceToMaintainMethod() {
    return getSetBalanceToMaintainMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest,
      me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse> getSetBalanceToMaintainMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest, me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse> getSetBalanceToMaintainMethod;
    if ((getSetBalanceToMaintainMethod = TicketBuyerServiceGrpc.getSetBalanceToMaintainMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetBalanceToMaintainMethod = TicketBuyerServiceGrpc.getSetBalanceToMaintainMethod) == null) {
          TicketBuyerServiceGrpc.getSetBalanceToMaintainMethod = getSetBalanceToMaintainMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest, me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetBalanceToMaintain"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetBalanceToMaintain"))
                  .build();
          }
        }
     }
     return getSetBalanceToMaintainMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetMaxFeeMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxFeeRequest,
      me.exrates.service.decred.rpc.Api.SetMaxFeeResponse> METHOD_SET_MAX_FEE = getSetMaxFeeMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxFeeRequest,
      me.exrates.service.decred.rpc.Api.SetMaxFeeResponse> getSetMaxFeeMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxFeeRequest,
      me.exrates.service.decred.rpc.Api.SetMaxFeeResponse> getSetMaxFeeMethod() {
    return getSetMaxFeeMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxFeeRequest,
      me.exrates.service.decred.rpc.Api.SetMaxFeeResponse> getSetMaxFeeMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxFeeRequest, me.exrates.service.decred.rpc.Api.SetMaxFeeResponse> getSetMaxFeeMethod;
    if ((getSetMaxFeeMethod = TicketBuyerServiceGrpc.getSetMaxFeeMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetMaxFeeMethod = TicketBuyerServiceGrpc.getSetMaxFeeMethod) == null) {
          TicketBuyerServiceGrpc.getSetMaxFeeMethod = getSetMaxFeeMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetMaxFeeRequest, me.exrates.service.decred.rpc.Api.SetMaxFeeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetMaxFee"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetMaxFeeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetMaxFeeResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetMaxFee"))
                  .build();
          }
        }
     }
     return getSetMaxFeeMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetMaxPriceRelativeMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse> METHOD_SET_MAX_PRICE_RELATIVE = getSetMaxPriceRelativeMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse> getSetMaxPriceRelativeMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse> getSetMaxPriceRelativeMethod() {
    return getSetMaxPriceRelativeMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse> getSetMaxPriceRelativeMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest, me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse> getSetMaxPriceRelativeMethod;
    if ((getSetMaxPriceRelativeMethod = TicketBuyerServiceGrpc.getSetMaxPriceRelativeMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetMaxPriceRelativeMethod = TicketBuyerServiceGrpc.getSetMaxPriceRelativeMethod) == null) {
          TicketBuyerServiceGrpc.getSetMaxPriceRelativeMethod = getSetMaxPriceRelativeMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest, me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetMaxPriceRelative"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetMaxPriceRelative"))
                  .build();
          }
        }
     }
     return getSetMaxPriceRelativeMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetMaxPriceAbsoluteMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse> METHOD_SET_MAX_PRICE_ABSOLUTE = getSetMaxPriceAbsoluteMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse> getSetMaxPriceAbsoluteMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse> getSetMaxPriceAbsoluteMethod() {
    return getSetMaxPriceAbsoluteMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse> getSetMaxPriceAbsoluteMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest, me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse> getSetMaxPriceAbsoluteMethod;
    if ((getSetMaxPriceAbsoluteMethod = TicketBuyerServiceGrpc.getSetMaxPriceAbsoluteMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetMaxPriceAbsoluteMethod = TicketBuyerServiceGrpc.getSetMaxPriceAbsoluteMethod) == null) {
          TicketBuyerServiceGrpc.getSetMaxPriceAbsoluteMethod = getSetMaxPriceAbsoluteMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest, me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetMaxPriceAbsolute"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetMaxPriceAbsolute"))
                  .build();
          }
        }
     }
     return getSetMaxPriceAbsoluteMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetVotingAddressMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVotingAddressRequest,
      me.exrates.service.decred.rpc.Api.SetVotingAddressResponse> METHOD_SET_VOTING_ADDRESS = getSetVotingAddressMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVotingAddressRequest,
      me.exrates.service.decred.rpc.Api.SetVotingAddressResponse> getSetVotingAddressMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVotingAddressRequest,
      me.exrates.service.decred.rpc.Api.SetVotingAddressResponse> getSetVotingAddressMethod() {
    return getSetVotingAddressMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVotingAddressRequest,
      me.exrates.service.decred.rpc.Api.SetVotingAddressResponse> getSetVotingAddressMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetVotingAddressRequest, me.exrates.service.decred.rpc.Api.SetVotingAddressResponse> getSetVotingAddressMethod;
    if ((getSetVotingAddressMethod = TicketBuyerServiceGrpc.getSetVotingAddressMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetVotingAddressMethod = TicketBuyerServiceGrpc.getSetVotingAddressMethod) == null) {
          TicketBuyerServiceGrpc.getSetVotingAddressMethod = getSetVotingAddressMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetVotingAddressRequest, me.exrates.service.decred.rpc.Api.SetVotingAddressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetVotingAddress"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetVotingAddressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetVotingAddressResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetVotingAddress"))
                  .build();
          }
        }
     }
     return getSetVotingAddressMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetPoolAddressMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolAddressRequest,
      me.exrates.service.decred.rpc.Api.SetPoolAddressResponse> METHOD_SET_POOL_ADDRESS = getSetPoolAddressMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolAddressRequest,
      me.exrates.service.decred.rpc.Api.SetPoolAddressResponse> getSetPoolAddressMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolAddressRequest,
      me.exrates.service.decred.rpc.Api.SetPoolAddressResponse> getSetPoolAddressMethod() {
    return getSetPoolAddressMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolAddressRequest,
      me.exrates.service.decred.rpc.Api.SetPoolAddressResponse> getSetPoolAddressMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolAddressRequest, me.exrates.service.decred.rpc.Api.SetPoolAddressResponse> getSetPoolAddressMethod;
    if ((getSetPoolAddressMethod = TicketBuyerServiceGrpc.getSetPoolAddressMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetPoolAddressMethod = TicketBuyerServiceGrpc.getSetPoolAddressMethod) == null) {
          TicketBuyerServiceGrpc.getSetPoolAddressMethod = getSetPoolAddressMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetPoolAddressRequest, me.exrates.service.decred.rpc.Api.SetPoolAddressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetPoolAddress"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetPoolAddressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetPoolAddressResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetPoolAddress"))
                  .build();
          }
        }
     }
     return getSetPoolAddressMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetPoolFeesMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolFeesRequest,
      me.exrates.service.decred.rpc.Api.SetPoolFeesResponse> METHOD_SET_POOL_FEES = getSetPoolFeesMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolFeesRequest,
      me.exrates.service.decred.rpc.Api.SetPoolFeesResponse> getSetPoolFeesMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolFeesRequest,
      me.exrates.service.decred.rpc.Api.SetPoolFeesResponse> getSetPoolFeesMethod() {
    return getSetPoolFeesMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolFeesRequest,
      me.exrates.service.decred.rpc.Api.SetPoolFeesResponse> getSetPoolFeesMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetPoolFeesRequest, me.exrates.service.decred.rpc.Api.SetPoolFeesResponse> getSetPoolFeesMethod;
    if ((getSetPoolFeesMethod = TicketBuyerServiceGrpc.getSetPoolFeesMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetPoolFeesMethod = TicketBuyerServiceGrpc.getSetPoolFeesMethod) == null) {
          TicketBuyerServiceGrpc.getSetPoolFeesMethod = getSetPoolFeesMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetPoolFeesRequest, me.exrates.service.decred.rpc.Api.SetPoolFeesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetPoolFees"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetPoolFeesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetPoolFeesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetPoolFees"))
                  .build();
          }
        }
     }
     return getSetPoolFeesMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSetMaxPerBlockMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse> METHOD_SET_MAX_PER_BLOCK = getSetMaxPerBlockMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse> getSetMaxPerBlockMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse> getSetMaxPerBlockMethod() {
    return getSetMaxPerBlockMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest,
      me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse> getSetMaxPerBlockMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest, me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse> getSetMaxPerBlockMethod;
    if ((getSetMaxPerBlockMethod = TicketBuyerServiceGrpc.getSetMaxPerBlockMethod) == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        if ((getSetMaxPerBlockMethod = TicketBuyerServiceGrpc.getSetMaxPerBlockMethod) == null) {
          TicketBuyerServiceGrpc.getSetMaxPerBlockMethod = getSetMaxPerBlockMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest, me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.TicketBuyerService", "SetMaxPerBlock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TicketBuyerServiceMethodDescriptorSupplier("SetMaxPerBlock"))
                  .build();
          }
        }
     }
     return getSetMaxPerBlockMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TicketBuyerServiceStub newStub(io.grpc.Channel channel) {
    return new TicketBuyerServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TicketBuyerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TicketBuyerServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TicketBuyerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TicketBuyerServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class TicketBuyerServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void startAutoBuyer(me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getStartAutoBuyerMethodHelper(), responseObserver);
    }

    /**
     */
    public void stopAutoBuyer(me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getStopAutoBuyerMethodHelper(), responseObserver);
    }

    /**
     */
    public void ticketBuyerConfig(me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTicketBuyerConfigMethodHelper(), responseObserver);
    }

    /**
     */
    public void setAccount(me.exrates.service.decred.rpc.Api.SetAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetAccountResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetAccountMethodHelper(), responseObserver);
    }

    /**
     */
    public void setBalanceToMaintain(me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetBalanceToMaintainMethodHelper(), responseObserver);
    }

    /**
     */
    public void setMaxFee(me.exrates.service.decred.rpc.Api.SetMaxFeeRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxFeeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetMaxFeeMethodHelper(), responseObserver);
    }

    /**
     */
    public void setMaxPriceRelative(me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetMaxPriceRelativeMethodHelper(), responseObserver);
    }

    /**
     */
    public void setMaxPriceAbsolute(me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetMaxPriceAbsoluteMethodHelper(), responseObserver);
    }

    /**
     */
    public void setVotingAddress(me.exrates.service.decred.rpc.Api.SetVotingAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetVotingAddressResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetVotingAddressMethodHelper(), responseObserver);
    }

    /**
     */
    public void setPoolAddress(me.exrates.service.decred.rpc.Api.SetPoolAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetPoolAddressResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetPoolAddressMethodHelper(), responseObserver);
    }

    /**
     */
    public void setPoolFees(me.exrates.service.decred.rpc.Api.SetPoolFeesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetPoolFeesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetPoolFeesMethodHelper(), responseObserver);
    }

    /**
     */
    public void setMaxPerBlock(me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSetMaxPerBlockMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getStartAutoBuyerMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest,
                me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse>(
                  this, METHODID_START_AUTO_BUYER)))
          .addMethod(
            getStopAutoBuyerMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest,
                me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse>(
                  this, METHODID_STOP_AUTO_BUYER)))
          .addMethod(
            getTicketBuyerConfigMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest,
                me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse>(
                  this, METHODID_TICKET_BUYER_CONFIG)))
          .addMethod(
            getSetAccountMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetAccountRequest,
                me.exrates.service.decred.rpc.Api.SetAccountResponse>(
                  this, METHODID_SET_ACCOUNT)))
          .addMethod(
            getSetBalanceToMaintainMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest,
                me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse>(
                  this, METHODID_SET_BALANCE_TO_MAINTAIN)))
          .addMethod(
            getSetMaxFeeMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetMaxFeeRequest,
                me.exrates.service.decred.rpc.Api.SetMaxFeeResponse>(
                  this, METHODID_SET_MAX_FEE)))
          .addMethod(
            getSetMaxPriceRelativeMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest,
                me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse>(
                  this, METHODID_SET_MAX_PRICE_RELATIVE)))
          .addMethod(
            getSetMaxPriceAbsoluteMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest,
                me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse>(
                  this, METHODID_SET_MAX_PRICE_ABSOLUTE)))
          .addMethod(
            getSetVotingAddressMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetVotingAddressRequest,
                me.exrates.service.decred.rpc.Api.SetVotingAddressResponse>(
                  this, METHODID_SET_VOTING_ADDRESS)))
          .addMethod(
            getSetPoolAddressMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetPoolAddressRequest,
                me.exrates.service.decred.rpc.Api.SetPoolAddressResponse>(
                  this, METHODID_SET_POOL_ADDRESS)))
          .addMethod(
            getSetPoolFeesMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetPoolFeesRequest,
                me.exrates.service.decred.rpc.Api.SetPoolFeesResponse>(
                  this, METHODID_SET_POOL_FEES)))
          .addMethod(
            getSetMaxPerBlockMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest,
                me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse>(
                  this, METHODID_SET_MAX_PER_BLOCK)))
          .build();
    }
  }

  /**
   */
  public static final class TicketBuyerServiceStub extends io.grpc.stub.AbstractStub<TicketBuyerServiceStub> {
    private TicketBuyerServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TicketBuyerServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TicketBuyerServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TicketBuyerServiceStub(channel, callOptions);
    }

    /**
     */
    public void startAutoBuyer(me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStartAutoBuyerMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stopAutoBuyer(me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStopAutoBuyerMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ticketBuyerConfig(me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTicketBuyerConfigMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setAccount(me.exrates.service.decred.rpc.Api.SetAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetAccountResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetAccountMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setBalanceToMaintain(me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetBalanceToMaintainMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMaxFee(me.exrates.service.decred.rpc.Api.SetMaxFeeRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxFeeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetMaxFeeMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMaxPriceRelative(me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetMaxPriceRelativeMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMaxPriceAbsolute(me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetMaxPriceAbsoluteMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setVotingAddress(me.exrates.service.decred.rpc.Api.SetVotingAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetVotingAddressResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetVotingAddressMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setPoolAddress(me.exrates.service.decred.rpc.Api.SetPoolAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetPoolAddressResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetPoolAddressMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setPoolFees(me.exrates.service.decred.rpc.Api.SetPoolFeesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetPoolFeesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetPoolFeesMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMaxPerBlock(me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetMaxPerBlockMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TicketBuyerServiceBlockingStub extends io.grpc.stub.AbstractStub<TicketBuyerServiceBlockingStub> {
    private TicketBuyerServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TicketBuyerServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TicketBuyerServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TicketBuyerServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse startAutoBuyer(me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest request) {
      return blockingUnaryCall(
          getChannel(), getStartAutoBuyerMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse stopAutoBuyer(me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest request) {
      return blockingUnaryCall(
          getChannel(), getStopAutoBuyerMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse ticketBuyerConfig(me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest request) {
      return blockingUnaryCall(
          getChannel(), getTicketBuyerConfigMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetAccountResponse setAccount(me.exrates.service.decred.rpc.Api.SetAccountRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetAccountMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse setBalanceToMaintain(me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetBalanceToMaintainMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetMaxFeeResponse setMaxFee(me.exrates.service.decred.rpc.Api.SetMaxFeeRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetMaxFeeMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse setMaxPriceRelative(me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetMaxPriceRelativeMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse setMaxPriceAbsolute(me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetMaxPriceAbsoluteMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetVotingAddressResponse setVotingAddress(me.exrates.service.decred.rpc.Api.SetVotingAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetVotingAddressMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetPoolAddressResponse setPoolAddress(me.exrates.service.decred.rpc.Api.SetPoolAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetPoolAddressMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetPoolFeesResponse setPoolFees(me.exrates.service.decred.rpc.Api.SetPoolFeesRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetPoolFeesMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse setMaxPerBlock(me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetMaxPerBlockMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TicketBuyerServiceFutureStub extends io.grpc.stub.AbstractStub<TicketBuyerServiceFutureStub> {
    private TicketBuyerServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TicketBuyerServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TicketBuyerServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TicketBuyerServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse> startAutoBuyer(
        me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStartAutoBuyerMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse> stopAutoBuyer(
        me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStopAutoBuyerMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse> ticketBuyerConfig(
        me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTicketBuyerConfigMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetAccountResponse> setAccount(
        me.exrates.service.decred.rpc.Api.SetAccountRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetAccountMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse> setBalanceToMaintain(
        me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetBalanceToMaintainMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetMaxFeeResponse> setMaxFee(
        me.exrates.service.decred.rpc.Api.SetMaxFeeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetMaxFeeMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse> setMaxPriceRelative(
        me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetMaxPriceRelativeMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse> setMaxPriceAbsolute(
        me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetMaxPriceAbsoluteMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetVotingAddressResponse> setVotingAddress(
        me.exrates.service.decred.rpc.Api.SetVotingAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetVotingAddressMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetPoolAddressResponse> setPoolAddress(
        me.exrates.service.decred.rpc.Api.SetPoolAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetPoolAddressMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetPoolFeesResponse> setPoolFees(
        me.exrates.service.decred.rpc.Api.SetPoolFeesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetPoolFeesMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse> setMaxPerBlock(
        me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetMaxPerBlockMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_START_AUTO_BUYER = 0;
  private static final int METHODID_STOP_AUTO_BUYER = 1;
  private static final int METHODID_TICKET_BUYER_CONFIG = 2;
  private static final int METHODID_SET_ACCOUNT = 3;
  private static final int METHODID_SET_BALANCE_TO_MAINTAIN = 4;
  private static final int METHODID_SET_MAX_FEE = 5;
  private static final int METHODID_SET_MAX_PRICE_RELATIVE = 6;
  private static final int METHODID_SET_MAX_PRICE_ABSOLUTE = 7;
  private static final int METHODID_SET_VOTING_ADDRESS = 8;
  private static final int METHODID_SET_POOL_ADDRESS = 9;
  private static final int METHODID_SET_POOL_FEES = 10;
  private static final int METHODID_SET_MAX_PER_BLOCK = 11;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TicketBuyerServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TicketBuyerServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_START_AUTO_BUYER:
          serviceImpl.startAutoBuyer((me.exrates.service.decred.rpc.Api.StartAutoBuyerRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StartAutoBuyerResponse>) responseObserver);
          break;
        case METHODID_STOP_AUTO_BUYER:
          serviceImpl.stopAutoBuyer((me.exrates.service.decred.rpc.Api.StopAutoBuyerRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StopAutoBuyerResponse>) responseObserver);
          break;
        case METHODID_TICKET_BUYER_CONFIG:
          serviceImpl.ticketBuyerConfig((me.exrates.service.decred.rpc.Api.TicketBuyerConfigRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TicketBuyerConfigResponse>) responseObserver);
          break;
        case METHODID_SET_ACCOUNT:
          serviceImpl.setAccount((me.exrates.service.decred.rpc.Api.SetAccountRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetAccountResponse>) responseObserver);
          break;
        case METHODID_SET_BALANCE_TO_MAINTAIN:
          serviceImpl.setBalanceToMaintain((me.exrates.service.decred.rpc.Api.SetBalanceToMaintainRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetBalanceToMaintainResponse>) responseObserver);
          break;
        case METHODID_SET_MAX_FEE:
          serviceImpl.setMaxFee((me.exrates.service.decred.rpc.Api.SetMaxFeeRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxFeeResponse>) responseObserver);
          break;
        case METHODID_SET_MAX_PRICE_RELATIVE:
          serviceImpl.setMaxPriceRelative((me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPriceRelativeResponse>) responseObserver);
          break;
        case METHODID_SET_MAX_PRICE_ABSOLUTE:
          serviceImpl.setMaxPriceAbsolute((me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPriceAbsoluteResponse>) responseObserver);
          break;
        case METHODID_SET_VOTING_ADDRESS:
          serviceImpl.setVotingAddress((me.exrates.service.decred.rpc.Api.SetVotingAddressRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetVotingAddressResponse>) responseObserver);
          break;
        case METHODID_SET_POOL_ADDRESS:
          serviceImpl.setPoolAddress((me.exrates.service.decred.rpc.Api.SetPoolAddressRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetPoolAddressResponse>) responseObserver);
          break;
        case METHODID_SET_POOL_FEES:
          serviceImpl.setPoolFees((me.exrates.service.decred.rpc.Api.SetPoolFeesRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetPoolFeesResponse>) responseObserver);
          break;
        case METHODID_SET_MAX_PER_BLOCK:
          serviceImpl.setMaxPerBlock((me.exrates.service.decred.rpc.Api.SetMaxPerBlockRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SetMaxPerBlockResponse>) responseObserver);
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

  private static abstract class TicketBuyerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TicketBuyerServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.rpc.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TicketBuyerService");
    }
  }

  private static final class TicketBuyerServiceFileDescriptorSupplier
      extends TicketBuyerServiceBaseDescriptorSupplier {
    TicketBuyerServiceFileDescriptorSupplier() {}
  }

  private static final class TicketBuyerServiceMethodDescriptorSupplier
      extends TicketBuyerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TicketBuyerServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (TicketBuyerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TicketBuyerServiceFileDescriptorSupplier())
              .addMethod(getStartAutoBuyerMethodHelper())
              .addMethod(getStopAutoBuyerMethodHelper())
              .addMethod(getTicketBuyerConfigMethodHelper())
              .addMethod(getSetAccountMethodHelper())
              .addMethod(getSetBalanceToMaintainMethodHelper())
              .addMethod(getSetMaxFeeMethodHelper())
              .addMethod(getSetMaxPriceRelativeMethodHelper())
              .addMethod(getSetMaxPriceAbsoluteMethodHelper())
              .addMethod(getSetVotingAddressMethodHelper())
              .addMethod(getSetPoolAddressMethodHelper())
              .addMethod(getSetPoolFeesMethodHelper())
              .addMethod(getSetMaxPerBlockMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
