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
public final class WalletLoaderServiceGrpc {

  private WalletLoaderServiceGrpc() {}

  public static final String SERVICE_NAME = "walletrpc.WalletLoaderService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getWalletExistsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.WalletExistsRequest,
      me.exrates.service.decred.rpc.Api.WalletExistsResponse> METHOD_WALLET_EXISTS = getWalletExistsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.WalletExistsRequest,
      me.exrates.service.decred.rpc.Api.WalletExistsResponse> getWalletExistsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.WalletExistsRequest,
      me.exrates.service.decred.rpc.Api.WalletExistsResponse> getWalletExistsMethod() {
    return getWalletExistsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.WalletExistsRequest,
      me.exrates.service.decred.rpc.Api.WalletExistsResponse> getWalletExistsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.WalletExistsRequest, me.exrates.service.decred.rpc.Api.WalletExistsResponse> getWalletExistsMethod;
    if ((getWalletExistsMethod = WalletLoaderServiceGrpc.getWalletExistsMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getWalletExistsMethod = WalletLoaderServiceGrpc.getWalletExistsMethod) == null) {
          WalletLoaderServiceGrpc.getWalletExistsMethod = getWalletExistsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.WalletExistsRequest, me.exrates.service.decred.rpc.Api.WalletExistsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "WalletExists"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.WalletExistsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.WalletExistsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("WalletExists"))
                  .build();
          }
        }
     }
     return getWalletExistsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getCreateWalletMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWalletRequest,
      me.exrates.service.decred.rpc.Api.CreateWalletResponse> METHOD_CREATE_WALLET = getCreateWalletMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWalletRequest,
      me.exrates.service.decred.rpc.Api.CreateWalletResponse> getCreateWalletMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWalletRequest,
      me.exrates.service.decred.rpc.Api.CreateWalletResponse> getCreateWalletMethod() {
    return getCreateWalletMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWalletRequest,
      me.exrates.service.decred.rpc.Api.CreateWalletResponse> getCreateWalletMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWalletRequest, me.exrates.service.decred.rpc.Api.CreateWalletResponse> getCreateWalletMethod;
    if ((getCreateWalletMethod = WalletLoaderServiceGrpc.getCreateWalletMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getCreateWalletMethod = WalletLoaderServiceGrpc.getCreateWalletMethod) == null) {
          WalletLoaderServiceGrpc.getCreateWalletMethod = getCreateWalletMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.CreateWalletRequest, me.exrates.service.decred.rpc.Api.CreateWalletResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "CreateWallet"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CreateWalletRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CreateWalletResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("CreateWallet"))
                  .build();
          }
        }
     }
     return getCreateWalletMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getCreateWatchingOnlyWalletMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest,
      me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse> METHOD_CREATE_WATCHING_ONLY_WALLET = getCreateWatchingOnlyWalletMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest,
      me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse> getCreateWatchingOnlyWalletMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest,
      me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse> getCreateWatchingOnlyWalletMethod() {
    return getCreateWatchingOnlyWalletMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest,
      me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse> getCreateWatchingOnlyWalletMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest, me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse> getCreateWatchingOnlyWalletMethod;
    if ((getCreateWatchingOnlyWalletMethod = WalletLoaderServiceGrpc.getCreateWatchingOnlyWalletMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getCreateWatchingOnlyWalletMethod = WalletLoaderServiceGrpc.getCreateWatchingOnlyWalletMethod) == null) {
          WalletLoaderServiceGrpc.getCreateWatchingOnlyWalletMethod = getCreateWatchingOnlyWalletMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest, me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "CreateWatchingOnlyWallet"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("CreateWatchingOnlyWallet"))
                  .build();
          }
        }
     }
     return getCreateWatchingOnlyWalletMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getOpenWalletMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.OpenWalletRequest,
      me.exrates.service.decred.rpc.Api.OpenWalletResponse> METHOD_OPEN_WALLET = getOpenWalletMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.OpenWalletRequest,
      me.exrates.service.decred.rpc.Api.OpenWalletResponse> getOpenWalletMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.OpenWalletRequest,
      me.exrates.service.decred.rpc.Api.OpenWalletResponse> getOpenWalletMethod() {
    return getOpenWalletMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.OpenWalletRequest,
      me.exrates.service.decred.rpc.Api.OpenWalletResponse> getOpenWalletMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.OpenWalletRequest, me.exrates.service.decred.rpc.Api.OpenWalletResponse> getOpenWalletMethod;
    if ((getOpenWalletMethod = WalletLoaderServiceGrpc.getOpenWalletMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getOpenWalletMethod = WalletLoaderServiceGrpc.getOpenWalletMethod) == null) {
          WalletLoaderServiceGrpc.getOpenWalletMethod = getOpenWalletMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.OpenWalletRequest, me.exrates.service.decred.rpc.Api.OpenWalletResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "OpenWallet"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.OpenWalletRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.OpenWalletResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("OpenWallet"))
                  .build();
          }
        }
     }
     return getOpenWalletMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getCloseWalletMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CloseWalletRequest,
      me.exrates.service.decred.rpc.Api.CloseWalletResponse> METHOD_CLOSE_WALLET = getCloseWalletMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CloseWalletRequest,
      me.exrates.service.decred.rpc.Api.CloseWalletResponse> getCloseWalletMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CloseWalletRequest,
      me.exrates.service.decred.rpc.Api.CloseWalletResponse> getCloseWalletMethod() {
    return getCloseWalletMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CloseWalletRequest,
      me.exrates.service.decred.rpc.Api.CloseWalletResponse> getCloseWalletMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CloseWalletRequest, me.exrates.service.decred.rpc.Api.CloseWalletResponse> getCloseWalletMethod;
    if ((getCloseWalletMethod = WalletLoaderServiceGrpc.getCloseWalletMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getCloseWalletMethod = WalletLoaderServiceGrpc.getCloseWalletMethod) == null) {
          WalletLoaderServiceGrpc.getCloseWalletMethod = getCloseWalletMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.CloseWalletRequest, me.exrates.service.decred.rpc.Api.CloseWalletResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "CloseWallet"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CloseWalletRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CloseWalletResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("CloseWallet"))
                  .build();
          }
        }
     }
     return getCloseWalletMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getStartConsensusRpcMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest,
      me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse> METHOD_START_CONSENSUS_RPC = getStartConsensusRpcMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest,
      me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse> getStartConsensusRpcMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest,
      me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse> getStartConsensusRpcMethod() {
    return getStartConsensusRpcMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest,
      me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse> getStartConsensusRpcMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest, me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse> getStartConsensusRpcMethod;
    if ((getStartConsensusRpcMethod = WalletLoaderServiceGrpc.getStartConsensusRpcMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getStartConsensusRpcMethod = WalletLoaderServiceGrpc.getStartConsensusRpcMethod) == null) {
          WalletLoaderServiceGrpc.getStartConsensusRpcMethod = getStartConsensusRpcMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest, me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "StartConsensusRpc"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("StartConsensusRpc"))
                  .build();
          }
        }
     }
     return getStartConsensusRpcMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getDiscoverAddressesMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest,
      me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse> METHOD_DISCOVER_ADDRESSES = getDiscoverAddressesMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest,
      me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse> getDiscoverAddressesMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest,
      me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse> getDiscoverAddressesMethod() {
    return getDiscoverAddressesMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest,
      me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse> getDiscoverAddressesMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest, me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse> getDiscoverAddressesMethod;
    if ((getDiscoverAddressesMethod = WalletLoaderServiceGrpc.getDiscoverAddressesMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getDiscoverAddressesMethod = WalletLoaderServiceGrpc.getDiscoverAddressesMethod) == null) {
          WalletLoaderServiceGrpc.getDiscoverAddressesMethod = getDiscoverAddressesMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest, me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "DiscoverAddresses"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("DiscoverAddresses"))
                  .build();
          }
        }
     }
     return getDiscoverAddressesMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSubscribeToBlockNotificationsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest,
      me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse> METHOD_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS = getSubscribeToBlockNotificationsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest,
      me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse> getSubscribeToBlockNotificationsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest,
      me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse> getSubscribeToBlockNotificationsMethod() {
    return getSubscribeToBlockNotificationsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest,
      me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse> getSubscribeToBlockNotificationsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest, me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse> getSubscribeToBlockNotificationsMethod;
    if ((getSubscribeToBlockNotificationsMethod = WalletLoaderServiceGrpc.getSubscribeToBlockNotificationsMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getSubscribeToBlockNotificationsMethod = WalletLoaderServiceGrpc.getSubscribeToBlockNotificationsMethod) == null) {
          WalletLoaderServiceGrpc.getSubscribeToBlockNotificationsMethod = getSubscribeToBlockNotificationsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest, me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "SubscribeToBlockNotifications"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("SubscribeToBlockNotifications"))
                  .build();
          }
        }
     }
     return getSubscribeToBlockNotificationsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getFetchHeadersMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FetchHeadersRequest,
      me.exrates.service.decred.rpc.Api.FetchHeadersResponse> METHOD_FETCH_HEADERS = getFetchHeadersMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FetchHeadersRequest,
      me.exrates.service.decred.rpc.Api.FetchHeadersResponse> getFetchHeadersMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FetchHeadersRequest,
      me.exrates.service.decred.rpc.Api.FetchHeadersResponse> getFetchHeadersMethod() {
    return getFetchHeadersMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FetchHeadersRequest,
      me.exrates.service.decred.rpc.Api.FetchHeadersResponse> getFetchHeadersMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FetchHeadersRequest, me.exrates.service.decred.rpc.Api.FetchHeadersResponse> getFetchHeadersMethod;
    if ((getFetchHeadersMethod = WalletLoaderServiceGrpc.getFetchHeadersMethod) == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        if ((getFetchHeadersMethod = WalletLoaderServiceGrpc.getFetchHeadersMethod) == null) {
          WalletLoaderServiceGrpc.getFetchHeadersMethod = getFetchHeadersMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.FetchHeadersRequest, me.exrates.service.decred.rpc.Api.FetchHeadersResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletLoaderService", "FetchHeaders"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.FetchHeadersRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.FetchHeadersResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletLoaderServiceMethodDescriptorSupplier("FetchHeaders"))
                  .build();
          }
        }
     }
     return getFetchHeadersMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WalletLoaderServiceStub newStub(io.grpc.Channel channel) {
    return new WalletLoaderServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WalletLoaderServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new WalletLoaderServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WalletLoaderServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new WalletLoaderServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class WalletLoaderServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void walletExists(me.exrates.service.decred.rpc.Api.WalletExistsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.WalletExistsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getWalletExistsMethodHelper(), responseObserver);
    }

    /**
     */
    public void createWallet(me.exrates.service.decred.rpc.Api.CreateWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateWalletResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateWalletMethodHelper(), responseObserver);
    }

    /**
     */
    public void createWatchingOnlyWallet(me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateWatchingOnlyWalletMethodHelper(), responseObserver);
    }

    /**
     */
    public void openWallet(me.exrates.service.decred.rpc.Api.OpenWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.OpenWalletResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getOpenWalletMethodHelper(), responseObserver);
    }

    /**
     */
    public void closeWallet(me.exrates.service.decred.rpc.Api.CloseWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CloseWalletResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCloseWalletMethodHelper(), responseObserver);
    }

    /**
     */
    public void startConsensusRpc(me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getStartConsensusRpcMethodHelper(), responseObserver);
    }

    /**
     */
    public void discoverAddresses(me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDiscoverAddressesMethodHelper(), responseObserver);
    }

    /**
     */
    public void subscribeToBlockNotifications(me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSubscribeToBlockNotificationsMethodHelper(), responseObserver);
    }

    /**
     */
    public void fetchHeaders(me.exrates.service.decred.rpc.Api.FetchHeadersRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.FetchHeadersResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFetchHeadersMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getWalletExistsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.WalletExistsRequest,
                me.exrates.service.decred.rpc.Api.WalletExistsResponse>(
                  this, METHODID_WALLET_EXISTS)))
          .addMethod(
            getCreateWalletMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.CreateWalletRequest,
                me.exrates.service.decred.rpc.Api.CreateWalletResponse>(
                  this, METHODID_CREATE_WALLET)))
          .addMethod(
            getCreateWatchingOnlyWalletMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest,
                me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse>(
                  this, METHODID_CREATE_WATCHING_ONLY_WALLET)))
          .addMethod(
            getOpenWalletMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.OpenWalletRequest,
                me.exrates.service.decred.rpc.Api.OpenWalletResponse>(
                  this, METHODID_OPEN_WALLET)))
          .addMethod(
            getCloseWalletMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.CloseWalletRequest,
                me.exrates.service.decred.rpc.Api.CloseWalletResponse>(
                  this, METHODID_CLOSE_WALLET)))
          .addMethod(
            getStartConsensusRpcMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest,
                me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse>(
                  this, METHODID_START_CONSENSUS_RPC)))
          .addMethod(
            getDiscoverAddressesMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest,
                me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse>(
                  this, METHODID_DISCOVER_ADDRESSES)))
          .addMethod(
            getSubscribeToBlockNotificationsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest,
                me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse>(
                  this, METHODID_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS)))
          .addMethod(
            getFetchHeadersMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.FetchHeadersRequest,
                me.exrates.service.decred.rpc.Api.FetchHeadersResponse>(
                  this, METHODID_FETCH_HEADERS)))
          .build();
    }
  }

  /**
   */
  public static final class WalletLoaderServiceStub extends io.grpc.stub.AbstractStub<WalletLoaderServiceStub> {
    private WalletLoaderServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletLoaderServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletLoaderServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletLoaderServiceStub(channel, callOptions);
    }

    /**
     */
    public void walletExists(me.exrates.service.decred.rpc.Api.WalletExistsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.WalletExistsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getWalletExistsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createWallet(me.exrates.service.decred.rpc.Api.CreateWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateWalletResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateWalletMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createWatchingOnlyWallet(me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateWatchingOnlyWalletMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void openWallet(me.exrates.service.decred.rpc.Api.OpenWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.OpenWalletResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getOpenWalletMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void closeWallet(me.exrates.service.decred.rpc.Api.CloseWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CloseWalletResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCloseWalletMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void startConsensusRpc(me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStartConsensusRpcMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void discoverAddresses(me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDiscoverAddressesMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToBlockNotifications(me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSubscribeToBlockNotificationsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fetchHeaders(me.exrates.service.decred.rpc.Api.FetchHeadersRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.FetchHeadersResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFetchHeadersMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class WalletLoaderServiceBlockingStub extends io.grpc.stub.AbstractStub<WalletLoaderServiceBlockingStub> {
    private WalletLoaderServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletLoaderServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletLoaderServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletLoaderServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.WalletExistsResponse walletExists(me.exrates.service.decred.rpc.Api.WalletExistsRequest request) {
      return blockingUnaryCall(
          getChannel(), getWalletExistsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.CreateWalletResponse createWallet(me.exrates.service.decred.rpc.Api.CreateWalletRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateWalletMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse createWatchingOnlyWallet(me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateWatchingOnlyWalletMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.OpenWalletResponse openWallet(me.exrates.service.decred.rpc.Api.OpenWalletRequest request) {
      return blockingUnaryCall(
          getChannel(), getOpenWalletMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.CloseWalletResponse closeWallet(me.exrates.service.decred.rpc.Api.CloseWalletRequest request) {
      return blockingUnaryCall(
          getChannel(), getCloseWalletMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse startConsensusRpc(me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest request) {
      return blockingUnaryCall(
          getChannel(), getStartConsensusRpcMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse discoverAddresses(me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest request) {
      return blockingUnaryCall(
          getChannel(), getDiscoverAddressesMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse subscribeToBlockNotifications(me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest request) {
      return blockingUnaryCall(
          getChannel(), getSubscribeToBlockNotificationsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.FetchHeadersResponse fetchHeaders(me.exrates.service.decred.rpc.Api.FetchHeadersRequest request) {
      return blockingUnaryCall(
          getChannel(), getFetchHeadersMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class WalletLoaderServiceFutureStub extends io.grpc.stub.AbstractStub<WalletLoaderServiceFutureStub> {
    private WalletLoaderServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletLoaderServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletLoaderServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletLoaderServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.WalletExistsResponse> walletExists(
        me.exrates.service.decred.rpc.Api.WalletExistsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getWalletExistsMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.CreateWalletResponse> createWallet(
        me.exrates.service.decred.rpc.Api.CreateWalletRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateWalletMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse> createWatchingOnlyWallet(
        me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateWatchingOnlyWalletMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.OpenWalletResponse> openWallet(
        me.exrates.service.decred.rpc.Api.OpenWalletRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getOpenWalletMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.CloseWalletResponse> closeWallet(
        me.exrates.service.decred.rpc.Api.CloseWalletRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCloseWalletMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse> startConsensusRpc(
        me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStartConsensusRpcMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse> discoverAddresses(
        me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDiscoverAddressesMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse> subscribeToBlockNotifications(
        me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSubscribeToBlockNotificationsMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.FetchHeadersResponse> fetchHeaders(
        me.exrates.service.decred.rpc.Api.FetchHeadersRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFetchHeadersMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_WALLET_EXISTS = 0;
  private static final int METHODID_CREATE_WALLET = 1;
  private static final int METHODID_CREATE_WATCHING_ONLY_WALLET = 2;
  private static final int METHODID_OPEN_WALLET = 3;
  private static final int METHODID_CLOSE_WALLET = 4;
  private static final int METHODID_START_CONSENSUS_RPC = 5;
  private static final int METHODID_DISCOVER_ADDRESSES = 6;
  private static final int METHODID_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS = 7;
  private static final int METHODID_FETCH_HEADERS = 8;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final WalletLoaderServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(WalletLoaderServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_WALLET_EXISTS:
          serviceImpl.walletExists((me.exrates.service.decred.rpc.Api.WalletExistsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.WalletExistsResponse>) responseObserver);
          break;
        case METHODID_CREATE_WALLET:
          serviceImpl.createWallet((me.exrates.service.decred.rpc.Api.CreateWalletRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateWalletResponse>) responseObserver);
          break;
        case METHODID_CREATE_WATCHING_ONLY_WALLET:
          serviceImpl.createWatchingOnlyWallet((me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateWatchingOnlyWalletResponse>) responseObserver);
          break;
        case METHODID_OPEN_WALLET:
          serviceImpl.openWallet((me.exrates.service.decred.rpc.Api.OpenWalletRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.OpenWalletResponse>) responseObserver);
          break;
        case METHODID_CLOSE_WALLET:
          serviceImpl.closeWallet((me.exrates.service.decred.rpc.Api.CloseWalletRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CloseWalletResponse>) responseObserver);
          break;
        case METHODID_START_CONSENSUS_RPC:
          serviceImpl.startConsensusRpc((me.exrates.service.decred.rpc.Api.StartConsensusRpcRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StartConsensusRpcResponse>) responseObserver);
          break;
        case METHODID_DISCOVER_ADDRESSES:
          serviceImpl.discoverAddresses((me.exrates.service.decred.rpc.Api.DiscoverAddressesRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.DiscoverAddressesResponse>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS:
          serviceImpl.subscribeToBlockNotifications((me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SubscribeToBlockNotificationsResponse>) responseObserver);
          break;
        case METHODID_FETCH_HEADERS:
          serviceImpl.fetchHeaders((me.exrates.service.decred.rpc.Api.FetchHeadersRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.FetchHeadersResponse>) responseObserver);
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

  private static abstract class WalletLoaderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WalletLoaderServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.rpc.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("WalletLoaderService");
    }
  }

  private static final class WalletLoaderServiceFileDescriptorSupplier
      extends WalletLoaderServiceBaseDescriptorSupplier {
    WalletLoaderServiceFileDescriptorSupplier() {}
  }

  private static final class WalletLoaderServiceMethodDescriptorSupplier
      extends WalletLoaderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    WalletLoaderServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (WalletLoaderServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WalletLoaderServiceFileDescriptorSupplier())
              .addMethod(getWalletExistsMethodHelper())
              .addMethod(getCreateWalletMethodHelper())
              .addMethod(getCreateWatchingOnlyWalletMethodHelper())
              .addMethod(getOpenWalletMethodHelper())
              .addMethod(getCloseWalletMethodHelper())
              .addMethod(getStartConsensusRpcMethodHelper())
              .addMethod(getDiscoverAddressesMethodHelper())
              .addMethod(getSubscribeToBlockNotificationsMethodHelper())
              .addMethod(getFetchHeadersMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
