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
public final class WalletServiceGrpc {

  private WalletServiceGrpc() {}

  public static final String SERVICE_NAME = "walletrpc.WalletService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getPingMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PingRequest,
      me.exrates.service.decred.rpc.Api.PingResponse> METHOD_PING = getPingMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PingRequest,
      me.exrates.service.decred.rpc.Api.PingResponse> getPingMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PingRequest,
      me.exrates.service.decred.rpc.Api.PingResponse> getPingMethod() {
    return getPingMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PingRequest,
      me.exrates.service.decred.rpc.Api.PingResponse> getPingMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PingRequest, me.exrates.service.decred.rpc.Api.PingResponse> getPingMethod;
    if ((getPingMethod = WalletServiceGrpc.getPingMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getPingMethod = WalletServiceGrpc.getPingMethod) == null) {
          WalletServiceGrpc.getPingMethod = getPingMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.PingRequest, me.exrates.service.decred.rpc.Api.PingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "Ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.PingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.PingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("Ping"))
                  .build();
          }
        }
     }
     return getPingMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getNetworkMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NetworkRequest,
      me.exrates.service.decred.rpc.Api.NetworkResponse> METHOD_NETWORK = getNetworkMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NetworkRequest,
      me.exrates.service.decred.rpc.Api.NetworkResponse> getNetworkMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NetworkRequest,
      me.exrates.service.decred.rpc.Api.NetworkResponse> getNetworkMethod() {
    return getNetworkMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NetworkRequest,
      me.exrates.service.decred.rpc.Api.NetworkResponse> getNetworkMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NetworkRequest, me.exrates.service.decred.rpc.Api.NetworkResponse> getNetworkMethod;
    if ((getNetworkMethod = WalletServiceGrpc.getNetworkMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getNetworkMethod = WalletServiceGrpc.getNetworkMethod) == null) {
          WalletServiceGrpc.getNetworkMethod = getNetworkMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.NetworkRequest, me.exrates.service.decred.rpc.Api.NetworkResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "Network"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.NetworkRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.NetworkResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("Network"))
                  .build();
          }
        }
     }
     return getNetworkMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getAccountNumberMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNumberRequest,
      me.exrates.service.decred.rpc.Api.AccountNumberResponse> METHOD_ACCOUNT_NUMBER = getAccountNumberMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNumberRequest,
      me.exrates.service.decred.rpc.Api.AccountNumberResponse> getAccountNumberMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNumberRequest,
      me.exrates.service.decred.rpc.Api.AccountNumberResponse> getAccountNumberMethod() {
    return getAccountNumberMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNumberRequest,
      me.exrates.service.decred.rpc.Api.AccountNumberResponse> getAccountNumberMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNumberRequest, me.exrates.service.decred.rpc.Api.AccountNumberResponse> getAccountNumberMethod;
    if ((getAccountNumberMethod = WalletServiceGrpc.getAccountNumberMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getAccountNumberMethod = WalletServiceGrpc.getAccountNumberMethod) == null) {
          WalletServiceGrpc.getAccountNumberMethod = getAccountNumberMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.AccountNumberRequest, me.exrates.service.decred.rpc.Api.AccountNumberResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "AccountNumber"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.AccountNumberRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.AccountNumberResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("AccountNumber"))
                  .build();
          }
        }
     }
     return getAccountNumberMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getAccountsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountsRequest,
      me.exrates.service.decred.rpc.Api.AccountsResponse> METHOD_ACCOUNTS = getAccountsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountsRequest,
      me.exrates.service.decred.rpc.Api.AccountsResponse> getAccountsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountsRequest,
      me.exrates.service.decred.rpc.Api.AccountsResponse> getAccountsMethod() {
    return getAccountsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountsRequest,
      me.exrates.service.decred.rpc.Api.AccountsResponse> getAccountsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountsRequest, me.exrates.service.decred.rpc.Api.AccountsResponse> getAccountsMethod;
    if ((getAccountsMethod = WalletServiceGrpc.getAccountsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getAccountsMethod = WalletServiceGrpc.getAccountsMethod) == null) {
          WalletServiceGrpc.getAccountsMethod = getAccountsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.AccountsRequest, me.exrates.service.decred.rpc.Api.AccountsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "Accounts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.AccountsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.AccountsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("Accounts"))
                  .build();
          }
        }
     }
     return getAccountsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getBalanceMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BalanceRequest,
      me.exrates.service.decred.rpc.Api.BalanceResponse> METHOD_BALANCE = getBalanceMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BalanceRequest,
      me.exrates.service.decred.rpc.Api.BalanceResponse> getBalanceMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BalanceRequest,
      me.exrates.service.decred.rpc.Api.BalanceResponse> getBalanceMethod() {
    return getBalanceMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BalanceRequest,
      me.exrates.service.decred.rpc.Api.BalanceResponse> getBalanceMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BalanceRequest, me.exrates.service.decred.rpc.Api.BalanceResponse> getBalanceMethod;
    if ((getBalanceMethod = WalletServiceGrpc.getBalanceMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getBalanceMethod = WalletServiceGrpc.getBalanceMethod) == null) {
          WalletServiceGrpc.getBalanceMethod = getBalanceMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.BalanceRequest, me.exrates.service.decred.rpc.Api.BalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "Balance"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.BalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.BalanceResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("Balance"))
                  .build();
          }
        }
     }
     return getBalanceMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getGetTransactionMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionRequest,
      me.exrates.service.decred.rpc.Api.GetTransactionResponse> METHOD_GET_TRANSACTION = getGetTransactionMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionRequest,
      me.exrates.service.decred.rpc.Api.GetTransactionResponse> getGetTransactionMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionRequest,
      me.exrates.service.decred.rpc.Api.GetTransactionResponse> getGetTransactionMethod() {
    return getGetTransactionMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionRequest,
      me.exrates.service.decred.rpc.Api.GetTransactionResponse> getGetTransactionMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionRequest, me.exrates.service.decred.rpc.Api.GetTransactionResponse> getGetTransactionMethod;
    if ((getGetTransactionMethod = WalletServiceGrpc.getGetTransactionMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getGetTransactionMethod = WalletServiceGrpc.getGetTransactionMethod) == null) {
          WalletServiceGrpc.getGetTransactionMethod = getGetTransactionMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.GetTransactionRequest, me.exrates.service.decred.rpc.Api.GetTransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "GetTransaction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.GetTransactionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.GetTransactionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("GetTransaction"))
                  .build();
          }
        }
     }
     return getGetTransactionMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getGetTransactionsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionsRequest,
      me.exrates.service.decred.rpc.Api.GetTransactionsResponse> METHOD_GET_TRANSACTIONS = getGetTransactionsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionsRequest,
      me.exrates.service.decred.rpc.Api.GetTransactionsResponse> getGetTransactionsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionsRequest,
      me.exrates.service.decred.rpc.Api.GetTransactionsResponse> getGetTransactionsMethod() {
    return getGetTransactionsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionsRequest,
      me.exrates.service.decred.rpc.Api.GetTransactionsResponse> getGetTransactionsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTransactionsRequest, me.exrates.service.decred.rpc.Api.GetTransactionsResponse> getGetTransactionsMethod;
    if ((getGetTransactionsMethod = WalletServiceGrpc.getGetTransactionsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getGetTransactionsMethod = WalletServiceGrpc.getGetTransactionsMethod) == null) {
          WalletServiceGrpc.getGetTransactionsMethod = getGetTransactionsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.GetTransactionsRequest, me.exrates.service.decred.rpc.Api.GetTransactionsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "GetTransactions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.GetTransactionsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.GetTransactionsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("GetTransactions"))
                  .build();
          }
        }
     }
     return getGetTransactionsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getGetTicketsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTicketsRequest,
      me.exrates.service.decred.rpc.Api.GetTicketsResponse> METHOD_GET_TICKETS = getGetTicketsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTicketsRequest,
      me.exrates.service.decred.rpc.Api.GetTicketsResponse> getGetTicketsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTicketsRequest,
      me.exrates.service.decred.rpc.Api.GetTicketsResponse> getGetTicketsMethod() {
    return getGetTicketsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTicketsRequest,
      me.exrates.service.decred.rpc.Api.GetTicketsResponse> getGetTicketsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.GetTicketsRequest, me.exrates.service.decred.rpc.Api.GetTicketsResponse> getGetTicketsMethod;
    if ((getGetTicketsMethod = WalletServiceGrpc.getGetTicketsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getGetTicketsMethod = WalletServiceGrpc.getGetTicketsMethod) == null) {
          WalletServiceGrpc.getGetTicketsMethod = getGetTicketsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.GetTicketsRequest, me.exrates.service.decred.rpc.Api.GetTicketsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "GetTickets"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.GetTicketsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.GetTicketsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("GetTickets"))
                  .build();
          }
        }
     }
     return getGetTicketsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getTicketPriceMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketPriceRequest,
      me.exrates.service.decred.rpc.Api.TicketPriceResponse> METHOD_TICKET_PRICE = getTicketPriceMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketPriceRequest,
      me.exrates.service.decred.rpc.Api.TicketPriceResponse> getTicketPriceMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketPriceRequest,
      me.exrates.service.decred.rpc.Api.TicketPriceResponse> getTicketPriceMethod() {
    return getTicketPriceMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketPriceRequest,
      me.exrates.service.decred.rpc.Api.TicketPriceResponse> getTicketPriceMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TicketPriceRequest, me.exrates.service.decred.rpc.Api.TicketPriceResponse> getTicketPriceMethod;
    if ((getTicketPriceMethod = WalletServiceGrpc.getTicketPriceMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getTicketPriceMethod = WalletServiceGrpc.getTicketPriceMethod) == null) {
          WalletServiceGrpc.getTicketPriceMethod = getTicketPriceMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.TicketPriceRequest, me.exrates.service.decred.rpc.Api.TicketPriceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "TicketPrice"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.TicketPriceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.TicketPriceResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("TicketPrice"))
                  .build();
          }
        }
     }
     return getTicketPriceMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getStakeInfoMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StakeInfoRequest,
      me.exrates.service.decred.rpc.Api.StakeInfoResponse> METHOD_STAKE_INFO = getStakeInfoMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StakeInfoRequest,
      me.exrates.service.decred.rpc.Api.StakeInfoResponse> getStakeInfoMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StakeInfoRequest,
      me.exrates.service.decred.rpc.Api.StakeInfoResponse> getStakeInfoMethod() {
    return getStakeInfoMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StakeInfoRequest,
      me.exrates.service.decred.rpc.Api.StakeInfoResponse> getStakeInfoMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.StakeInfoRequest, me.exrates.service.decred.rpc.Api.StakeInfoResponse> getStakeInfoMethod;
    if ((getStakeInfoMethod = WalletServiceGrpc.getStakeInfoMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getStakeInfoMethod = WalletServiceGrpc.getStakeInfoMethod) == null) {
          WalletServiceGrpc.getStakeInfoMethod = getStakeInfoMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.StakeInfoRequest, me.exrates.service.decred.rpc.Api.StakeInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "StakeInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.StakeInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.StakeInfoResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("StakeInfo"))
                  .build();
          }
        }
     }
     return getStakeInfoMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getBlockInfoMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BlockInfoRequest,
      me.exrates.service.decred.rpc.Api.BlockInfoResponse> METHOD_BLOCK_INFO = getBlockInfoMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BlockInfoRequest,
      me.exrates.service.decred.rpc.Api.BlockInfoResponse> getBlockInfoMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BlockInfoRequest,
      me.exrates.service.decred.rpc.Api.BlockInfoResponse> getBlockInfoMethod() {
    return getBlockInfoMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BlockInfoRequest,
      me.exrates.service.decred.rpc.Api.BlockInfoResponse> getBlockInfoMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BlockInfoRequest, me.exrates.service.decred.rpc.Api.BlockInfoResponse> getBlockInfoMethod;
    if ((getBlockInfoMethod = WalletServiceGrpc.getBlockInfoMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getBlockInfoMethod = WalletServiceGrpc.getBlockInfoMethod) == null) {
          WalletServiceGrpc.getBlockInfoMethod = getBlockInfoMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.BlockInfoRequest, me.exrates.service.decred.rpc.Api.BlockInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "BlockInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.BlockInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.BlockInfoResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("BlockInfo"))
                  .build();
          }
        }
     }
     return getBlockInfoMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getBestBlockMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BestBlockRequest,
      me.exrates.service.decred.rpc.Api.BestBlockResponse> METHOD_BEST_BLOCK = getBestBlockMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BestBlockRequest,
      me.exrates.service.decred.rpc.Api.BestBlockResponse> getBestBlockMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BestBlockRequest,
      me.exrates.service.decred.rpc.Api.BestBlockResponse> getBestBlockMethod() {
    return getBestBlockMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BestBlockRequest,
      me.exrates.service.decred.rpc.Api.BestBlockResponse> getBestBlockMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.BestBlockRequest, me.exrates.service.decred.rpc.Api.BestBlockResponse> getBestBlockMethod;
    if ((getBestBlockMethod = WalletServiceGrpc.getBestBlockMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getBestBlockMethod = WalletServiceGrpc.getBestBlockMethod) == null) {
          WalletServiceGrpc.getBestBlockMethod = getBestBlockMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.BestBlockRequest, me.exrates.service.decred.rpc.Api.BestBlockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "BestBlock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.BestBlockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.BestBlockResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("BestBlock"))
                  .build();
          }
        }
     }
     return getBestBlockMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getTransactionNotificationsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest,
      me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse> METHOD_TRANSACTION_NOTIFICATIONS = getTransactionNotificationsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest,
      me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse> getTransactionNotificationsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest,
      me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse> getTransactionNotificationsMethod() {
    return getTransactionNotificationsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest,
      me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse> getTransactionNotificationsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest, me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse> getTransactionNotificationsMethod;
    if ((getTransactionNotificationsMethod = WalletServiceGrpc.getTransactionNotificationsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getTransactionNotificationsMethod = WalletServiceGrpc.getTransactionNotificationsMethod) == null) {
          WalletServiceGrpc.getTransactionNotificationsMethod = getTransactionNotificationsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest, me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "TransactionNotifications"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("TransactionNotifications"))
                  .build();
          }
        }
     }
     return getTransactionNotificationsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getAccountNotificationsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNotificationsRequest,
      me.exrates.service.decred.rpc.Api.AccountNotificationsResponse> METHOD_ACCOUNT_NOTIFICATIONS = getAccountNotificationsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNotificationsRequest,
      me.exrates.service.decred.rpc.Api.AccountNotificationsResponse> getAccountNotificationsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNotificationsRequest,
      me.exrates.service.decred.rpc.Api.AccountNotificationsResponse> getAccountNotificationsMethod() {
    return getAccountNotificationsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNotificationsRequest,
      me.exrates.service.decred.rpc.Api.AccountNotificationsResponse> getAccountNotificationsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.AccountNotificationsRequest, me.exrates.service.decred.rpc.Api.AccountNotificationsResponse> getAccountNotificationsMethod;
    if ((getAccountNotificationsMethod = WalletServiceGrpc.getAccountNotificationsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getAccountNotificationsMethod = WalletServiceGrpc.getAccountNotificationsMethod) == null) {
          WalletServiceGrpc.getAccountNotificationsMethod = getAccountNotificationsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.AccountNotificationsRequest, me.exrates.service.decred.rpc.Api.AccountNotificationsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "AccountNotifications"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.AccountNotificationsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.AccountNotificationsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("AccountNotifications"))
                  .build();
          }
        }
     }
     return getAccountNotificationsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getConfirmationNotificationsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest,
      me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse> METHOD_CONFIRMATION_NOTIFICATIONS = getConfirmationNotificationsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest,
      me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse> getConfirmationNotificationsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest,
      me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse> getConfirmationNotificationsMethod() {
    return getConfirmationNotificationsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest,
      me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse> getConfirmationNotificationsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest, me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse> getConfirmationNotificationsMethod;
    if ((getConfirmationNotificationsMethod = WalletServiceGrpc.getConfirmationNotificationsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getConfirmationNotificationsMethod = WalletServiceGrpc.getConfirmationNotificationsMethod) == null) {
          WalletServiceGrpc.getConfirmationNotificationsMethod = getConfirmationNotificationsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest, me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "ConfirmationNotifications"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("ConfirmationNotifications"))
                  .build();
          }
        }
     }
     return getConfirmationNotificationsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getChangePassphraseMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ChangePassphraseRequest,
      me.exrates.service.decred.rpc.Api.ChangePassphraseResponse> METHOD_CHANGE_PASSPHRASE = getChangePassphraseMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ChangePassphraseRequest,
      me.exrates.service.decred.rpc.Api.ChangePassphraseResponse> getChangePassphraseMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ChangePassphraseRequest,
      me.exrates.service.decred.rpc.Api.ChangePassphraseResponse> getChangePassphraseMethod() {
    return getChangePassphraseMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ChangePassphraseRequest,
      me.exrates.service.decred.rpc.Api.ChangePassphraseResponse> getChangePassphraseMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ChangePassphraseRequest, me.exrates.service.decred.rpc.Api.ChangePassphraseResponse> getChangePassphraseMethod;
    if ((getChangePassphraseMethod = WalletServiceGrpc.getChangePassphraseMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getChangePassphraseMethod = WalletServiceGrpc.getChangePassphraseMethod) == null) {
          WalletServiceGrpc.getChangePassphraseMethod = getChangePassphraseMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.ChangePassphraseRequest, me.exrates.service.decred.rpc.Api.ChangePassphraseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "ChangePassphrase"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ChangePassphraseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ChangePassphraseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("ChangePassphrase"))
                  .build();
          }
        }
     }
     return getChangePassphraseMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getRenameAccountMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RenameAccountRequest,
      me.exrates.service.decred.rpc.Api.RenameAccountResponse> METHOD_RENAME_ACCOUNT = getRenameAccountMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RenameAccountRequest,
      me.exrates.service.decred.rpc.Api.RenameAccountResponse> getRenameAccountMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RenameAccountRequest,
      me.exrates.service.decred.rpc.Api.RenameAccountResponse> getRenameAccountMethod() {
    return getRenameAccountMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RenameAccountRequest,
      me.exrates.service.decred.rpc.Api.RenameAccountResponse> getRenameAccountMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RenameAccountRequest, me.exrates.service.decred.rpc.Api.RenameAccountResponse> getRenameAccountMethod;
    if ((getRenameAccountMethod = WalletServiceGrpc.getRenameAccountMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getRenameAccountMethod = WalletServiceGrpc.getRenameAccountMethod) == null) {
          WalletServiceGrpc.getRenameAccountMethod = getRenameAccountMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.RenameAccountRequest, me.exrates.service.decred.rpc.Api.RenameAccountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "RenameAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.RenameAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.RenameAccountResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("RenameAccount"))
                  .build();
          }
        }
     }
     return getRenameAccountMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getRescanMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RescanRequest,
      me.exrates.service.decred.rpc.Api.RescanResponse> METHOD_RESCAN = getRescanMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RescanRequest,
      me.exrates.service.decred.rpc.Api.RescanResponse> getRescanMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RescanRequest,
      me.exrates.service.decred.rpc.Api.RescanResponse> getRescanMethod() {
    return getRescanMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RescanRequest,
      me.exrates.service.decred.rpc.Api.RescanResponse> getRescanMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RescanRequest, me.exrates.service.decred.rpc.Api.RescanResponse> getRescanMethod;
    if ((getRescanMethod = WalletServiceGrpc.getRescanMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getRescanMethod = WalletServiceGrpc.getRescanMethod) == null) {
          WalletServiceGrpc.getRescanMethod = getRescanMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.RescanRequest, me.exrates.service.decred.rpc.Api.RescanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "Rescan"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.RescanRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.RescanResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("Rescan"))
                  .build();
          }
        }
     }
     return getRescanMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getNextAccountMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAccountRequest,
      me.exrates.service.decred.rpc.Api.NextAccountResponse> METHOD_NEXT_ACCOUNT = getNextAccountMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAccountRequest,
      me.exrates.service.decred.rpc.Api.NextAccountResponse> getNextAccountMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAccountRequest,
      me.exrates.service.decred.rpc.Api.NextAccountResponse> getNextAccountMethod() {
    return getNextAccountMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAccountRequest,
      me.exrates.service.decred.rpc.Api.NextAccountResponse> getNextAccountMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAccountRequest, me.exrates.service.decred.rpc.Api.NextAccountResponse> getNextAccountMethod;
    if ((getNextAccountMethod = WalletServiceGrpc.getNextAccountMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getNextAccountMethod = WalletServiceGrpc.getNextAccountMethod) == null) {
          WalletServiceGrpc.getNextAccountMethod = getNextAccountMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.NextAccountRequest, me.exrates.service.decred.rpc.Api.NextAccountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "NextAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.NextAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.NextAccountResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("NextAccount"))
                  .build();
          }
        }
     }
     return getNextAccountMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getNextAddressMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAddressRequest,
      me.exrates.service.decred.rpc.Api.NextAddressResponse> METHOD_NEXT_ADDRESS = getNextAddressMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAddressRequest,
      me.exrates.service.decred.rpc.Api.NextAddressResponse> getNextAddressMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAddressRequest,
      me.exrates.service.decred.rpc.Api.NextAddressResponse> getNextAddressMethod() {
    return getNextAddressMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAddressRequest,
      me.exrates.service.decred.rpc.Api.NextAddressResponse> getNextAddressMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.NextAddressRequest, me.exrates.service.decred.rpc.Api.NextAddressResponse> getNextAddressMethod;
    if ((getNextAddressMethod = WalletServiceGrpc.getNextAddressMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getNextAddressMethod = WalletServiceGrpc.getNextAddressMethod) == null) {
          WalletServiceGrpc.getNextAddressMethod = getNextAddressMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.NextAddressRequest, me.exrates.service.decred.rpc.Api.NextAddressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "NextAddress"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.NextAddressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.NextAddressResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("NextAddress"))
                  .build();
          }
        }
     }
     return getNextAddressMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getImportPrivateKeyMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest,
      me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse> METHOD_IMPORT_PRIVATE_KEY = getImportPrivateKeyMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest,
      me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse> getImportPrivateKeyMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest,
      me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse> getImportPrivateKeyMethod() {
    return getImportPrivateKeyMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest,
      me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse> getImportPrivateKeyMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest, me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse> getImportPrivateKeyMethod;
    if ((getImportPrivateKeyMethod = WalletServiceGrpc.getImportPrivateKeyMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getImportPrivateKeyMethod = WalletServiceGrpc.getImportPrivateKeyMethod) == null) {
          WalletServiceGrpc.getImportPrivateKeyMethod = getImportPrivateKeyMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest, me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "ImportPrivateKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("ImportPrivateKey"))
                  .build();
          }
        }
     }
     return getImportPrivateKeyMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getImportScriptMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportScriptRequest,
      me.exrates.service.decred.rpc.Api.ImportScriptResponse> METHOD_IMPORT_SCRIPT = getImportScriptMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportScriptRequest,
      me.exrates.service.decred.rpc.Api.ImportScriptResponse> getImportScriptMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportScriptRequest,
      me.exrates.service.decred.rpc.Api.ImportScriptResponse> getImportScriptMethod() {
    return getImportScriptMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportScriptRequest,
      me.exrates.service.decred.rpc.Api.ImportScriptResponse> getImportScriptMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ImportScriptRequest, me.exrates.service.decred.rpc.Api.ImportScriptResponse> getImportScriptMethod;
    if ((getImportScriptMethod = WalletServiceGrpc.getImportScriptMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getImportScriptMethod = WalletServiceGrpc.getImportScriptMethod) == null) {
          WalletServiceGrpc.getImportScriptMethod = getImportScriptMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.ImportScriptRequest, me.exrates.service.decred.rpc.Api.ImportScriptResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "ImportScript"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ImportScriptRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ImportScriptResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("ImportScript"))
                  .build();
          }
        }
     }
     return getImportScriptMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getFundTransactionMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FundTransactionRequest,
      me.exrates.service.decred.rpc.Api.FundTransactionResponse> METHOD_FUND_TRANSACTION = getFundTransactionMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FundTransactionRequest,
      me.exrates.service.decred.rpc.Api.FundTransactionResponse> getFundTransactionMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FundTransactionRequest,
      me.exrates.service.decred.rpc.Api.FundTransactionResponse> getFundTransactionMethod() {
    return getFundTransactionMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FundTransactionRequest,
      me.exrates.service.decred.rpc.Api.FundTransactionResponse> getFundTransactionMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.FundTransactionRequest, me.exrates.service.decred.rpc.Api.FundTransactionResponse> getFundTransactionMethod;
    if ((getFundTransactionMethod = WalletServiceGrpc.getFundTransactionMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getFundTransactionMethod = WalletServiceGrpc.getFundTransactionMethod) == null) {
          WalletServiceGrpc.getFundTransactionMethod = getFundTransactionMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.FundTransactionRequest, me.exrates.service.decred.rpc.Api.FundTransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "FundTransaction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.FundTransactionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.FundTransactionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("FundTransaction"))
                  .build();
          }
        }
     }
     return getFundTransactionMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getUnspentOutputsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.UnspentOutputsRequest,
      me.exrates.service.decred.rpc.Api.UnspentOutputResponse> METHOD_UNSPENT_OUTPUTS = getUnspentOutputsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.UnspentOutputsRequest,
      me.exrates.service.decred.rpc.Api.UnspentOutputResponse> getUnspentOutputsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.UnspentOutputsRequest,
      me.exrates.service.decred.rpc.Api.UnspentOutputResponse> getUnspentOutputsMethod() {
    return getUnspentOutputsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.UnspentOutputsRequest,
      me.exrates.service.decred.rpc.Api.UnspentOutputResponse> getUnspentOutputsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.UnspentOutputsRequest, me.exrates.service.decred.rpc.Api.UnspentOutputResponse> getUnspentOutputsMethod;
    if ((getUnspentOutputsMethod = WalletServiceGrpc.getUnspentOutputsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getUnspentOutputsMethod = WalletServiceGrpc.getUnspentOutputsMethod) == null) {
          WalletServiceGrpc.getUnspentOutputsMethod = getUnspentOutputsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.UnspentOutputsRequest, me.exrates.service.decred.rpc.Api.UnspentOutputResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "UnspentOutputs"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.UnspentOutputsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.UnspentOutputResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("UnspentOutputs"))
                  .build();
          }
        }
     }
     return getUnspentOutputsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getConstructTransactionMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConstructTransactionRequest,
      me.exrates.service.decred.rpc.Api.ConstructTransactionResponse> METHOD_CONSTRUCT_TRANSACTION = getConstructTransactionMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConstructTransactionRequest,
      me.exrates.service.decred.rpc.Api.ConstructTransactionResponse> getConstructTransactionMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConstructTransactionRequest,
      me.exrates.service.decred.rpc.Api.ConstructTransactionResponse> getConstructTransactionMethod() {
    return getConstructTransactionMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConstructTransactionRequest,
      me.exrates.service.decred.rpc.Api.ConstructTransactionResponse> getConstructTransactionMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ConstructTransactionRequest, me.exrates.service.decred.rpc.Api.ConstructTransactionResponse> getConstructTransactionMethod;
    if ((getConstructTransactionMethod = WalletServiceGrpc.getConstructTransactionMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getConstructTransactionMethod = WalletServiceGrpc.getConstructTransactionMethod) == null) {
          WalletServiceGrpc.getConstructTransactionMethod = getConstructTransactionMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.ConstructTransactionRequest, me.exrates.service.decred.rpc.Api.ConstructTransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "ConstructTransaction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ConstructTransactionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ConstructTransactionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("ConstructTransaction"))
                  .build();
          }
        }
     }
     return getConstructTransactionMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSignTransactionMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionRequest,
      me.exrates.service.decred.rpc.Api.SignTransactionResponse> METHOD_SIGN_TRANSACTION = getSignTransactionMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionRequest,
      me.exrates.service.decred.rpc.Api.SignTransactionResponse> getSignTransactionMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionRequest,
      me.exrates.service.decred.rpc.Api.SignTransactionResponse> getSignTransactionMethod() {
    return getSignTransactionMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionRequest,
      me.exrates.service.decred.rpc.Api.SignTransactionResponse> getSignTransactionMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionRequest, me.exrates.service.decred.rpc.Api.SignTransactionResponse> getSignTransactionMethod;
    if ((getSignTransactionMethod = WalletServiceGrpc.getSignTransactionMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getSignTransactionMethod = WalletServiceGrpc.getSignTransactionMethod) == null) {
          WalletServiceGrpc.getSignTransactionMethod = getSignTransactionMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SignTransactionRequest, me.exrates.service.decred.rpc.Api.SignTransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "SignTransaction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SignTransactionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SignTransactionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("SignTransaction"))
                  .build();
          }
        }
     }
     return getSignTransactionMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSignTransactionsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionsRequest,
      me.exrates.service.decred.rpc.Api.SignTransactionsResponse> METHOD_SIGN_TRANSACTIONS = getSignTransactionsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionsRequest,
      me.exrates.service.decred.rpc.Api.SignTransactionsResponse> getSignTransactionsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionsRequest,
      me.exrates.service.decred.rpc.Api.SignTransactionsResponse> getSignTransactionsMethod() {
    return getSignTransactionsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionsRequest,
      me.exrates.service.decred.rpc.Api.SignTransactionsResponse> getSignTransactionsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignTransactionsRequest, me.exrates.service.decred.rpc.Api.SignTransactionsResponse> getSignTransactionsMethod;
    if ((getSignTransactionsMethod = WalletServiceGrpc.getSignTransactionsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getSignTransactionsMethod = WalletServiceGrpc.getSignTransactionsMethod) == null) {
          WalletServiceGrpc.getSignTransactionsMethod = getSignTransactionsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SignTransactionsRequest, me.exrates.service.decred.rpc.Api.SignTransactionsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "SignTransactions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SignTransactionsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SignTransactionsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("SignTransactions"))
                  .build();
          }
        }
     }
     return getSignTransactionsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getCreateSignatureMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateSignatureRequest,
      me.exrates.service.decred.rpc.Api.CreateSignatureResponse> METHOD_CREATE_SIGNATURE = getCreateSignatureMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateSignatureRequest,
      me.exrates.service.decred.rpc.Api.CreateSignatureResponse> getCreateSignatureMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateSignatureRequest,
      me.exrates.service.decred.rpc.Api.CreateSignatureResponse> getCreateSignatureMethod() {
    return getCreateSignatureMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateSignatureRequest,
      me.exrates.service.decred.rpc.Api.CreateSignatureResponse> getCreateSignatureMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CreateSignatureRequest, me.exrates.service.decred.rpc.Api.CreateSignatureResponse> getCreateSignatureMethod;
    if ((getCreateSignatureMethod = WalletServiceGrpc.getCreateSignatureMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getCreateSignatureMethod = WalletServiceGrpc.getCreateSignatureMethod) == null) {
          WalletServiceGrpc.getCreateSignatureMethod = getCreateSignatureMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.CreateSignatureRequest, me.exrates.service.decred.rpc.Api.CreateSignatureResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "CreateSignature"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CreateSignatureRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CreateSignatureResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("CreateSignature"))
                  .build();
          }
        }
     }
     return getCreateSignatureMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getPublishTransactionMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishTransactionRequest,
      me.exrates.service.decred.rpc.Api.PublishTransactionResponse> METHOD_PUBLISH_TRANSACTION = getPublishTransactionMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishTransactionRequest,
      me.exrates.service.decred.rpc.Api.PublishTransactionResponse> getPublishTransactionMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishTransactionRequest,
      me.exrates.service.decred.rpc.Api.PublishTransactionResponse> getPublishTransactionMethod() {
    return getPublishTransactionMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishTransactionRequest,
      me.exrates.service.decred.rpc.Api.PublishTransactionResponse> getPublishTransactionMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishTransactionRequest, me.exrates.service.decred.rpc.Api.PublishTransactionResponse> getPublishTransactionMethod;
    if ((getPublishTransactionMethod = WalletServiceGrpc.getPublishTransactionMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getPublishTransactionMethod = WalletServiceGrpc.getPublishTransactionMethod) == null) {
          WalletServiceGrpc.getPublishTransactionMethod = getPublishTransactionMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.PublishTransactionRequest, me.exrates.service.decred.rpc.Api.PublishTransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "PublishTransaction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.PublishTransactionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.PublishTransactionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("PublishTransaction"))
                  .build();
          }
        }
     }
     return getPublishTransactionMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getPublishUnminedTransactionsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest,
      me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse> METHOD_PUBLISH_UNMINED_TRANSACTIONS = getPublishUnminedTransactionsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest,
      me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse> getPublishUnminedTransactionsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest,
      me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse> getPublishUnminedTransactionsMethod() {
    return getPublishUnminedTransactionsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest,
      me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse> getPublishUnminedTransactionsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest, me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse> getPublishUnminedTransactionsMethod;
    if ((getPublishUnminedTransactionsMethod = WalletServiceGrpc.getPublishUnminedTransactionsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getPublishUnminedTransactionsMethod = WalletServiceGrpc.getPublishUnminedTransactionsMethod) == null) {
          WalletServiceGrpc.getPublishUnminedTransactionsMethod = getPublishUnminedTransactionsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest, me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "PublishUnminedTransactions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("PublishUnminedTransactions"))
                  .build();
          }
        }
     }
     return getPublishUnminedTransactionsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getPurchaseTicketsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest,
      me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse> METHOD_PURCHASE_TICKETS = getPurchaseTicketsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest,
      me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse> getPurchaseTicketsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest,
      me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse> getPurchaseTicketsMethod() {
    return getPurchaseTicketsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest,
      me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse> getPurchaseTicketsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest, me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse> getPurchaseTicketsMethod;
    if ((getPurchaseTicketsMethod = WalletServiceGrpc.getPurchaseTicketsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getPurchaseTicketsMethod = WalletServiceGrpc.getPurchaseTicketsMethod) == null) {
          WalletServiceGrpc.getPurchaseTicketsMethod = getPurchaseTicketsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest, me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "PurchaseTickets"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("PurchaseTickets"))
                  .build();
          }
        }
     }
     return getPurchaseTicketsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getRevokeTicketsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RevokeTicketsRequest,
      me.exrates.service.decred.rpc.Api.RevokeTicketsResponse> METHOD_REVOKE_TICKETS = getRevokeTicketsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RevokeTicketsRequest,
      me.exrates.service.decred.rpc.Api.RevokeTicketsResponse> getRevokeTicketsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RevokeTicketsRequest,
      me.exrates.service.decred.rpc.Api.RevokeTicketsResponse> getRevokeTicketsMethod() {
    return getRevokeTicketsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RevokeTicketsRequest,
      me.exrates.service.decred.rpc.Api.RevokeTicketsResponse> getRevokeTicketsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.RevokeTicketsRequest, me.exrates.service.decred.rpc.Api.RevokeTicketsResponse> getRevokeTicketsMethod;
    if ((getRevokeTicketsMethod = WalletServiceGrpc.getRevokeTicketsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getRevokeTicketsMethod = WalletServiceGrpc.getRevokeTicketsMethod) == null) {
          WalletServiceGrpc.getRevokeTicketsMethod = getRevokeTicketsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.RevokeTicketsRequest, me.exrates.service.decred.rpc.Api.RevokeTicketsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "RevokeTickets"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.RevokeTicketsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.RevokeTicketsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("RevokeTickets"))
                  .build();
          }
        }
     }
     return getRevokeTicketsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getLoadActiveDataFiltersMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest,
      me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse> METHOD_LOAD_ACTIVE_DATA_FILTERS = getLoadActiveDataFiltersMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest,
      me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse> getLoadActiveDataFiltersMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest,
      me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse> getLoadActiveDataFiltersMethod() {
    return getLoadActiveDataFiltersMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest,
      me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse> getLoadActiveDataFiltersMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest, me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse> getLoadActiveDataFiltersMethod;
    if ((getLoadActiveDataFiltersMethod = WalletServiceGrpc.getLoadActiveDataFiltersMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getLoadActiveDataFiltersMethod = WalletServiceGrpc.getLoadActiveDataFiltersMethod) == null) {
          WalletServiceGrpc.getLoadActiveDataFiltersMethod = getLoadActiveDataFiltersMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest, me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "LoadActiveDataFilters"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("LoadActiveDataFilters"))
                  .build();
          }
        }
     }
     return getLoadActiveDataFiltersMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSignMessageMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessageRequest,
      me.exrates.service.decred.rpc.Api.SignMessageResponse> METHOD_SIGN_MESSAGE = getSignMessageMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessageRequest,
      me.exrates.service.decred.rpc.Api.SignMessageResponse> getSignMessageMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessageRequest,
      me.exrates.service.decred.rpc.Api.SignMessageResponse> getSignMessageMethod() {
    return getSignMessageMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessageRequest,
      me.exrates.service.decred.rpc.Api.SignMessageResponse> getSignMessageMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessageRequest, me.exrates.service.decred.rpc.Api.SignMessageResponse> getSignMessageMethod;
    if ((getSignMessageMethod = WalletServiceGrpc.getSignMessageMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getSignMessageMethod = WalletServiceGrpc.getSignMessageMethod) == null) {
          WalletServiceGrpc.getSignMessageMethod = getSignMessageMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SignMessageRequest, me.exrates.service.decred.rpc.Api.SignMessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "SignMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SignMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SignMessageResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("SignMessage"))
                  .build();
          }
        }
     }
     return getSignMessageMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSignMessagesMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessagesRequest,
      me.exrates.service.decred.rpc.Api.SignMessagesResponse> METHOD_SIGN_MESSAGES = getSignMessagesMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessagesRequest,
      me.exrates.service.decred.rpc.Api.SignMessagesResponse> getSignMessagesMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessagesRequest,
      me.exrates.service.decred.rpc.Api.SignMessagesResponse> getSignMessagesMethod() {
    return getSignMessagesMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessagesRequest,
      me.exrates.service.decred.rpc.Api.SignMessagesResponse> getSignMessagesMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.SignMessagesRequest, me.exrates.service.decred.rpc.Api.SignMessagesResponse> getSignMessagesMethod;
    if ((getSignMessagesMethod = WalletServiceGrpc.getSignMessagesMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getSignMessagesMethod = WalletServiceGrpc.getSignMessagesMethod) == null) {
          WalletServiceGrpc.getSignMessagesMethod = getSignMessagesMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.SignMessagesRequest, me.exrates.service.decred.rpc.Api.SignMessagesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "SignMessages"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SignMessagesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.SignMessagesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("SignMessages"))
                  .build();
          }
        }
     }
     return getSignMessagesMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getValidateAddressMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ValidateAddressRequest,
      me.exrates.service.decred.rpc.Api.ValidateAddressResponse> METHOD_VALIDATE_ADDRESS = getValidateAddressMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ValidateAddressRequest,
      me.exrates.service.decred.rpc.Api.ValidateAddressResponse> getValidateAddressMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ValidateAddressRequest,
      me.exrates.service.decred.rpc.Api.ValidateAddressResponse> getValidateAddressMethod() {
    return getValidateAddressMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ValidateAddressRequest,
      me.exrates.service.decred.rpc.Api.ValidateAddressResponse> getValidateAddressMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.ValidateAddressRequest, me.exrates.service.decred.rpc.Api.ValidateAddressResponse> getValidateAddressMethod;
    if ((getValidateAddressMethod = WalletServiceGrpc.getValidateAddressMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getValidateAddressMethod = WalletServiceGrpc.getValidateAddressMethod) == null) {
          WalletServiceGrpc.getValidateAddressMethod = getValidateAddressMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.ValidateAddressRequest, me.exrates.service.decred.rpc.Api.ValidateAddressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "ValidateAddress"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ValidateAddressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.ValidateAddressResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("ValidateAddress"))
                  .build();
          }
        }
     }
     return getValidateAddressMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getCommittedTicketsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CommittedTicketsRequest,
      me.exrates.service.decred.rpc.Api.CommittedTicketsResponse> METHOD_COMMITTED_TICKETS = getCommittedTicketsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CommittedTicketsRequest,
      me.exrates.service.decred.rpc.Api.CommittedTicketsResponse> getCommittedTicketsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CommittedTicketsRequest,
      me.exrates.service.decred.rpc.Api.CommittedTicketsResponse> getCommittedTicketsMethod() {
    return getCommittedTicketsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CommittedTicketsRequest,
      me.exrates.service.decred.rpc.Api.CommittedTicketsResponse> getCommittedTicketsMethodHelper() {
    io.grpc.MethodDescriptor<me.exrates.service.decred.rpc.Api.CommittedTicketsRequest, me.exrates.service.decred.rpc.Api.CommittedTicketsResponse> getCommittedTicketsMethod;
    if ((getCommittedTicketsMethod = WalletServiceGrpc.getCommittedTicketsMethod) == null) {
      synchronized (WalletServiceGrpc.class) {
        if ((getCommittedTicketsMethod = WalletServiceGrpc.getCommittedTicketsMethod) == null) {
          WalletServiceGrpc.getCommittedTicketsMethod = getCommittedTicketsMethod = 
              io.grpc.MethodDescriptor.<me.exrates.service.decred.rpc.Api.CommittedTicketsRequest, me.exrates.service.decred.rpc.Api.CommittedTicketsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "walletrpc.WalletService", "CommittedTickets"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CommittedTicketsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  me.exrates.service.decred.rpc.Api.CommittedTicketsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new WalletServiceMethodDescriptorSupplier("CommittedTickets"))
                  .build();
          }
        }
     }
     return getCommittedTicketsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WalletServiceStub newStub(io.grpc.Channel channel) {
    return new WalletServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WalletServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new WalletServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WalletServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new WalletServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class WalletServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Queries
     * </pre>
     */
    public void ping(me.exrates.service.decred.rpc.Api.PingRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethodHelper(), responseObserver);
    }

    /**
     */
    public void network(me.exrates.service.decred.rpc.Api.NetworkRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NetworkResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getNetworkMethodHelper(), responseObserver);
    }

    /**
     */
    public void accountNumber(me.exrates.service.decred.rpc.Api.AccountNumberRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountNumberResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAccountNumberMethodHelper(), responseObserver);
    }

    /**
     */
    public void accounts(me.exrates.service.decred.rpc.Api.AccountsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAccountsMethodHelper(), responseObserver);
    }

    /**
     */
    public void balance(me.exrates.service.decred.rpc.Api.BalanceRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BalanceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getBalanceMethodHelper(), responseObserver);
    }

    /**
     */
    public void getTransaction(me.exrates.service.decred.rpc.Api.GetTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTransactionMethodHelper(), responseObserver);
    }

    /**
     */
    public void getTransactions(me.exrates.service.decred.rpc.Api.GetTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTransactionsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTransactionsMethodHelper(), responseObserver);
    }

    /**
     */
    public void getTickets(me.exrates.service.decred.rpc.Api.GetTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTicketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTicketsMethodHelper(), responseObserver);
    }

    /**
     */
    public void ticketPrice(me.exrates.service.decred.rpc.Api.TicketPriceRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TicketPriceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTicketPriceMethodHelper(), responseObserver);
    }

    /**
     */
    public void stakeInfo(me.exrates.service.decred.rpc.Api.StakeInfoRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StakeInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getStakeInfoMethodHelper(), responseObserver);
    }

    /**
     */
    public void blockInfo(me.exrates.service.decred.rpc.Api.BlockInfoRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BlockInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getBlockInfoMethodHelper(), responseObserver);
    }

    /**
     */
    public void bestBlock(me.exrates.service.decred.rpc.Api.BestBlockRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BestBlockResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getBestBlockMethodHelper(), responseObserver);
    }

    /**
     * <pre>
     * Notifications
     * </pre>
     */
    public void transactionNotifications(me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTransactionNotificationsMethodHelper(), responseObserver);
    }

    /**
     */
    public void accountNotifications(me.exrates.service.decred.rpc.Api.AccountNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountNotificationsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAccountNotificationsMethodHelper(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest> confirmationNotifications(
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getConfirmationNotificationsMethodHelper(), responseObserver);
    }

    /**
     * <pre>
     * Control
     * </pre>
     */
    public void changePassphrase(me.exrates.service.decred.rpc.Api.ChangePassphraseRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ChangePassphraseResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getChangePassphraseMethodHelper(), responseObserver);
    }

    /**
     */
    public void renameAccount(me.exrates.service.decred.rpc.Api.RenameAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RenameAccountResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRenameAccountMethodHelper(), responseObserver);
    }

    /**
     */
    public void rescan(me.exrates.service.decred.rpc.Api.RescanRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RescanResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRescanMethodHelper(), responseObserver);
    }

    /**
     */
    public void nextAccount(me.exrates.service.decred.rpc.Api.NextAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NextAccountResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getNextAccountMethodHelper(), responseObserver);
    }

    /**
     */
    public void nextAddress(me.exrates.service.decred.rpc.Api.NextAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NextAddressResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getNextAddressMethodHelper(), responseObserver);
    }

    /**
     */
    public void importPrivateKey(me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getImportPrivateKeyMethodHelper(), responseObserver);
    }

    /**
     */
    public void importScript(me.exrates.service.decred.rpc.Api.ImportScriptRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ImportScriptResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getImportScriptMethodHelper(), responseObserver);
    }

    /**
     */
    public void fundTransaction(me.exrates.service.decred.rpc.Api.FundTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.FundTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFundTransactionMethodHelper(), responseObserver);
    }

    /**
     */
    public void unspentOutputs(me.exrates.service.decred.rpc.Api.UnspentOutputsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.UnspentOutputResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getUnspentOutputsMethodHelper(), responseObserver);
    }

    /**
     */
    public void constructTransaction(me.exrates.service.decred.rpc.Api.ConstructTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ConstructTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getConstructTransactionMethodHelper(), responseObserver);
    }

    /**
     */
    public void signTransaction(me.exrates.service.decred.rpc.Api.SignTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSignTransactionMethodHelper(), responseObserver);
    }

    /**
     */
    public void signTransactions(me.exrates.service.decred.rpc.Api.SignTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignTransactionsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSignTransactionsMethodHelper(), responseObserver);
    }

    /**
     */
    public void createSignature(me.exrates.service.decred.rpc.Api.CreateSignatureRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateSignatureResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateSignatureMethodHelper(), responseObserver);
    }

    /**
     */
    public void publishTransaction(me.exrates.service.decred.rpc.Api.PublishTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PublishTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPublishTransactionMethodHelper(), responseObserver);
    }

    /**
     */
    public void publishUnminedTransactions(me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPublishUnminedTransactionsMethodHelper(), responseObserver);
    }

    /**
     */
    public void purchaseTickets(me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPurchaseTicketsMethodHelper(), responseObserver);
    }

    /**
     */
    public void revokeTickets(me.exrates.service.decred.rpc.Api.RevokeTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RevokeTicketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRevokeTicketsMethodHelper(), responseObserver);
    }

    /**
     */
    public void loadActiveDataFilters(me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLoadActiveDataFiltersMethodHelper(), responseObserver);
    }

    /**
     */
    public void signMessage(me.exrates.service.decred.rpc.Api.SignMessageRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignMessageResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSignMessageMethodHelper(), responseObserver);
    }

    /**
     */
    public void signMessages(me.exrates.service.decred.rpc.Api.SignMessagesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignMessagesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSignMessagesMethodHelper(), responseObserver);
    }

    /**
     */
    public void validateAddress(me.exrates.service.decred.rpc.Api.ValidateAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ValidateAddressResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getValidateAddressMethodHelper(), responseObserver);
    }

    /**
     */
    public void committedTickets(me.exrates.service.decred.rpc.Api.CommittedTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CommittedTicketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCommittedTicketsMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPingMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.PingRequest,
                me.exrates.service.decred.rpc.Api.PingResponse>(
                  this, METHODID_PING)))
          .addMethod(
            getNetworkMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.NetworkRequest,
                me.exrates.service.decred.rpc.Api.NetworkResponse>(
                  this, METHODID_NETWORK)))
          .addMethod(
            getAccountNumberMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.AccountNumberRequest,
                me.exrates.service.decred.rpc.Api.AccountNumberResponse>(
                  this, METHODID_ACCOUNT_NUMBER)))
          .addMethod(
            getAccountsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.AccountsRequest,
                me.exrates.service.decred.rpc.Api.AccountsResponse>(
                  this, METHODID_ACCOUNTS)))
          .addMethod(
            getBalanceMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.BalanceRequest,
                me.exrates.service.decred.rpc.Api.BalanceResponse>(
                  this, METHODID_BALANCE)))
          .addMethod(
            getGetTransactionMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.GetTransactionRequest,
                me.exrates.service.decred.rpc.Api.GetTransactionResponse>(
                  this, METHODID_GET_TRANSACTION)))
          .addMethod(
            getGetTransactionsMethodHelper(),
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.GetTransactionsRequest,
                me.exrates.service.decred.rpc.Api.GetTransactionsResponse>(
                  this, METHODID_GET_TRANSACTIONS)))
          .addMethod(
            getGetTicketsMethodHelper(),
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.GetTicketsRequest,
                me.exrates.service.decred.rpc.Api.GetTicketsResponse>(
                  this, METHODID_GET_TICKETS)))
          .addMethod(
            getTicketPriceMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.TicketPriceRequest,
                me.exrates.service.decred.rpc.Api.TicketPriceResponse>(
                  this, METHODID_TICKET_PRICE)))
          .addMethod(
            getStakeInfoMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.StakeInfoRequest,
                me.exrates.service.decred.rpc.Api.StakeInfoResponse>(
                  this, METHODID_STAKE_INFO)))
          .addMethod(
            getBlockInfoMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.BlockInfoRequest,
                me.exrates.service.decred.rpc.Api.BlockInfoResponse>(
                  this, METHODID_BLOCK_INFO)))
          .addMethod(
            getBestBlockMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.BestBlockRequest,
                me.exrates.service.decred.rpc.Api.BestBlockResponse>(
                  this, METHODID_BEST_BLOCK)))
          .addMethod(
            getTransactionNotificationsMethodHelper(),
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest,
                me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse>(
                  this, METHODID_TRANSACTION_NOTIFICATIONS)))
          .addMethod(
            getAccountNotificationsMethodHelper(),
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.AccountNotificationsRequest,
                me.exrates.service.decred.rpc.Api.AccountNotificationsResponse>(
                  this, METHODID_ACCOUNT_NOTIFICATIONS)))
          .addMethod(
            getConfirmationNotificationsMethodHelper(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest,
                me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse>(
                  this, METHODID_CONFIRMATION_NOTIFICATIONS)))
          .addMethod(
            getChangePassphraseMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.ChangePassphraseRequest,
                me.exrates.service.decred.rpc.Api.ChangePassphraseResponse>(
                  this, METHODID_CHANGE_PASSPHRASE)))
          .addMethod(
            getRenameAccountMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.RenameAccountRequest,
                me.exrates.service.decred.rpc.Api.RenameAccountResponse>(
                  this, METHODID_RENAME_ACCOUNT)))
          .addMethod(
            getRescanMethodHelper(),
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.RescanRequest,
                me.exrates.service.decred.rpc.Api.RescanResponse>(
                  this, METHODID_RESCAN)))
          .addMethod(
            getNextAccountMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.NextAccountRequest,
                me.exrates.service.decred.rpc.Api.NextAccountResponse>(
                  this, METHODID_NEXT_ACCOUNT)))
          .addMethod(
            getNextAddressMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.NextAddressRequest,
                me.exrates.service.decred.rpc.Api.NextAddressResponse>(
                  this, METHODID_NEXT_ADDRESS)))
          .addMethod(
            getImportPrivateKeyMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest,
                me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse>(
                  this, METHODID_IMPORT_PRIVATE_KEY)))
          .addMethod(
            getImportScriptMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.ImportScriptRequest,
                me.exrates.service.decred.rpc.Api.ImportScriptResponse>(
                  this, METHODID_IMPORT_SCRIPT)))
          .addMethod(
            getFundTransactionMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.FundTransactionRequest,
                me.exrates.service.decred.rpc.Api.FundTransactionResponse>(
                  this, METHODID_FUND_TRANSACTION)))
          .addMethod(
            getUnspentOutputsMethodHelper(),
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.UnspentOutputsRequest,
                me.exrates.service.decred.rpc.Api.UnspentOutputResponse>(
                  this, METHODID_UNSPENT_OUTPUTS)))
          .addMethod(
            getConstructTransactionMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.ConstructTransactionRequest,
                me.exrates.service.decred.rpc.Api.ConstructTransactionResponse>(
                  this, METHODID_CONSTRUCT_TRANSACTION)))
          .addMethod(
            getSignTransactionMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SignTransactionRequest,
                me.exrates.service.decred.rpc.Api.SignTransactionResponse>(
                  this, METHODID_SIGN_TRANSACTION)))
          .addMethod(
            getSignTransactionsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SignTransactionsRequest,
                me.exrates.service.decred.rpc.Api.SignTransactionsResponse>(
                  this, METHODID_SIGN_TRANSACTIONS)))
          .addMethod(
            getCreateSignatureMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.CreateSignatureRequest,
                me.exrates.service.decred.rpc.Api.CreateSignatureResponse>(
                  this, METHODID_CREATE_SIGNATURE)))
          .addMethod(
            getPublishTransactionMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.PublishTransactionRequest,
                me.exrates.service.decred.rpc.Api.PublishTransactionResponse>(
                  this, METHODID_PUBLISH_TRANSACTION)))
          .addMethod(
            getPublishUnminedTransactionsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest,
                me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse>(
                  this, METHODID_PUBLISH_UNMINED_TRANSACTIONS)))
          .addMethod(
            getPurchaseTicketsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest,
                me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse>(
                  this, METHODID_PURCHASE_TICKETS)))
          .addMethod(
            getRevokeTicketsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.RevokeTicketsRequest,
                me.exrates.service.decred.rpc.Api.RevokeTicketsResponse>(
                  this, METHODID_REVOKE_TICKETS)))
          .addMethod(
            getLoadActiveDataFiltersMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest,
                me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse>(
                  this, METHODID_LOAD_ACTIVE_DATA_FILTERS)))
          .addMethod(
            getSignMessageMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SignMessageRequest,
                me.exrates.service.decred.rpc.Api.SignMessageResponse>(
                  this, METHODID_SIGN_MESSAGE)))
          .addMethod(
            getSignMessagesMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.SignMessagesRequest,
                me.exrates.service.decred.rpc.Api.SignMessagesResponse>(
                  this, METHODID_SIGN_MESSAGES)))
          .addMethod(
            getValidateAddressMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.ValidateAddressRequest,
                me.exrates.service.decred.rpc.Api.ValidateAddressResponse>(
                  this, METHODID_VALIDATE_ADDRESS)))
          .addMethod(
            getCommittedTicketsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.rpc.Api.CommittedTicketsRequest,
                me.exrates.service.decred.rpc.Api.CommittedTicketsResponse>(
                  this, METHODID_COMMITTED_TICKETS)))
          .build();
    }
  }

  /**
   */
  public static final class WalletServiceStub extends io.grpc.stub.AbstractStub<WalletServiceStub> {
    private WalletServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries
     * </pre>
     */
    public void ping(me.exrates.service.decred.rpc.Api.PingRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPingMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void network(me.exrates.service.decred.rpc.Api.NetworkRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NetworkResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getNetworkMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void accountNumber(me.exrates.service.decred.rpc.Api.AccountNumberRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountNumberResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAccountNumberMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void accounts(me.exrates.service.decred.rpc.Api.AccountsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAccountsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void balance(me.exrates.service.decred.rpc.Api.BalanceRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BalanceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getBalanceMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getTransaction(me.exrates.service.decred.rpc.Api.GetTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetTransactionMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getTransactions(me.exrates.service.decred.rpc.Api.GetTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTransactionsResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getGetTransactionsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getTickets(me.exrates.service.decred.rpc.Api.GetTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTicketsResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getGetTicketsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ticketPrice(me.exrates.service.decred.rpc.Api.TicketPriceRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TicketPriceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTicketPriceMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stakeInfo(me.exrates.service.decred.rpc.Api.StakeInfoRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StakeInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStakeInfoMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void blockInfo(me.exrates.service.decred.rpc.Api.BlockInfoRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BlockInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getBlockInfoMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void bestBlock(me.exrates.service.decred.rpc.Api.BestBlockRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BestBlockResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getBestBlockMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Notifications
     * </pre>
     */
    public void transactionNotifications(me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getTransactionNotificationsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void accountNotifications(me.exrates.service.decred.rpc.Api.AccountNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountNotificationsResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getAccountNotificationsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsRequest> confirmationNotifications(
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getConfirmationNotificationsMethodHelper(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * Control
     * </pre>
     */
    public void changePassphrase(me.exrates.service.decred.rpc.Api.ChangePassphraseRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ChangePassphraseResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getChangePassphraseMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void renameAccount(me.exrates.service.decred.rpc.Api.RenameAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RenameAccountResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRenameAccountMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void rescan(me.exrates.service.decred.rpc.Api.RescanRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RescanResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getRescanMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nextAccount(me.exrates.service.decred.rpc.Api.NextAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NextAccountResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getNextAccountMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nextAddress(me.exrates.service.decred.rpc.Api.NextAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NextAddressResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getNextAddressMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void importPrivateKey(me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getImportPrivateKeyMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void importScript(me.exrates.service.decred.rpc.Api.ImportScriptRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ImportScriptResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getImportScriptMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fundTransaction(me.exrates.service.decred.rpc.Api.FundTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.FundTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFundTransactionMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void unspentOutputs(me.exrates.service.decred.rpc.Api.UnspentOutputsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.UnspentOutputResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getUnspentOutputsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void constructTransaction(me.exrates.service.decred.rpc.Api.ConstructTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ConstructTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getConstructTransactionMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void signTransaction(me.exrates.service.decred.rpc.Api.SignTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSignTransactionMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void signTransactions(me.exrates.service.decred.rpc.Api.SignTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignTransactionsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSignTransactionsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createSignature(me.exrates.service.decred.rpc.Api.CreateSignatureRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateSignatureResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateSignatureMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void publishTransaction(me.exrates.service.decred.rpc.Api.PublishTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PublishTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPublishTransactionMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void publishUnminedTransactions(me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPublishUnminedTransactionsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void purchaseTickets(me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPurchaseTicketsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void revokeTickets(me.exrates.service.decred.rpc.Api.RevokeTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RevokeTicketsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRevokeTicketsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void loadActiveDataFilters(me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLoadActiveDataFiltersMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void signMessage(me.exrates.service.decred.rpc.Api.SignMessageRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignMessageResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSignMessageMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void signMessages(me.exrates.service.decred.rpc.Api.SignMessagesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignMessagesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSignMessagesMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void validateAddress(me.exrates.service.decred.rpc.Api.ValidateAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ValidateAddressResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getValidateAddressMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void committedTickets(me.exrates.service.decred.rpc.Api.CommittedTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CommittedTicketsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCommittedTicketsMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class WalletServiceBlockingStub extends io.grpc.stub.AbstractStub<WalletServiceBlockingStub> {
    private WalletServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries
     * </pre>
     */
    public me.exrates.service.decred.rpc.Api.PingResponse ping(me.exrates.service.decred.rpc.Api.PingRequest request) {
      return blockingUnaryCall(
          getChannel(), getPingMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.NetworkResponse network(me.exrates.service.decred.rpc.Api.NetworkRequest request) {
      return blockingUnaryCall(
          getChannel(), getNetworkMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.AccountNumberResponse accountNumber(me.exrates.service.decred.rpc.Api.AccountNumberRequest request) {
      return blockingUnaryCall(
          getChannel(), getAccountNumberMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.AccountsResponse accounts(me.exrates.service.decred.rpc.Api.AccountsRequest request) {
      return blockingUnaryCall(
          getChannel(), getAccountsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.BalanceResponse balance(me.exrates.service.decred.rpc.Api.BalanceRequest request) {
      return blockingUnaryCall(
          getChannel(), getBalanceMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.GetTransactionResponse getTransaction(me.exrates.service.decred.rpc.Api.GetTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetTransactionMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.rpc.Api.GetTransactionsResponse> getTransactions(
        me.exrates.service.decred.rpc.Api.GetTransactionsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getGetTransactionsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.rpc.Api.GetTicketsResponse> getTickets(
        me.exrates.service.decred.rpc.Api.GetTicketsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getGetTicketsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.TicketPriceResponse ticketPrice(me.exrates.service.decred.rpc.Api.TicketPriceRequest request) {
      return blockingUnaryCall(
          getChannel(), getTicketPriceMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.StakeInfoResponse stakeInfo(me.exrates.service.decred.rpc.Api.StakeInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getStakeInfoMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.BlockInfoResponse blockInfo(me.exrates.service.decred.rpc.Api.BlockInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getBlockInfoMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.BestBlockResponse bestBlock(me.exrates.service.decred.rpc.Api.BestBlockRequest request) {
      return blockingUnaryCall(
          getChannel(), getBestBlockMethodHelper(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Notifications
     * </pre>
     */
    public java.util.Iterator<me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse> transactionNotifications(
        me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getTransactionNotificationsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.rpc.Api.AccountNotificationsResponse> accountNotifications(
        me.exrates.service.decred.rpc.Api.AccountNotificationsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getAccountNotificationsMethodHelper(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Control
     * </pre>
     */
    public me.exrates.service.decred.rpc.Api.ChangePassphraseResponse changePassphrase(me.exrates.service.decred.rpc.Api.ChangePassphraseRequest request) {
      return blockingUnaryCall(
          getChannel(), getChangePassphraseMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.RenameAccountResponse renameAccount(me.exrates.service.decred.rpc.Api.RenameAccountRequest request) {
      return blockingUnaryCall(
          getChannel(), getRenameAccountMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.rpc.Api.RescanResponse> rescan(
        me.exrates.service.decred.rpc.Api.RescanRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getRescanMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.NextAccountResponse nextAccount(me.exrates.service.decred.rpc.Api.NextAccountRequest request) {
      return blockingUnaryCall(
          getChannel(), getNextAccountMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.NextAddressResponse nextAddress(me.exrates.service.decred.rpc.Api.NextAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), getNextAddressMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse importPrivateKey(me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest request) {
      return blockingUnaryCall(
          getChannel(), getImportPrivateKeyMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.ImportScriptResponse importScript(me.exrates.service.decred.rpc.Api.ImportScriptRequest request) {
      return blockingUnaryCall(
          getChannel(), getImportScriptMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.FundTransactionResponse fundTransaction(me.exrates.service.decred.rpc.Api.FundTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), getFundTransactionMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.rpc.Api.UnspentOutputResponse> unspentOutputs(
        me.exrates.service.decred.rpc.Api.UnspentOutputsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getUnspentOutputsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.ConstructTransactionResponse constructTransaction(me.exrates.service.decred.rpc.Api.ConstructTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), getConstructTransactionMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SignTransactionResponse signTransaction(me.exrates.service.decred.rpc.Api.SignTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), getSignTransactionMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SignTransactionsResponse signTransactions(me.exrates.service.decred.rpc.Api.SignTransactionsRequest request) {
      return blockingUnaryCall(
          getChannel(), getSignTransactionsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.CreateSignatureResponse createSignature(me.exrates.service.decred.rpc.Api.CreateSignatureRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateSignatureMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.PublishTransactionResponse publishTransaction(me.exrates.service.decred.rpc.Api.PublishTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), getPublishTransactionMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse publishUnminedTransactions(me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest request) {
      return blockingUnaryCall(
          getChannel(), getPublishUnminedTransactionsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse purchaseTickets(me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest request) {
      return blockingUnaryCall(
          getChannel(), getPurchaseTicketsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.RevokeTicketsResponse revokeTickets(me.exrates.service.decred.rpc.Api.RevokeTicketsRequest request) {
      return blockingUnaryCall(
          getChannel(), getRevokeTicketsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse loadActiveDataFilters(me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest request) {
      return blockingUnaryCall(
          getChannel(), getLoadActiveDataFiltersMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SignMessageResponse signMessage(me.exrates.service.decred.rpc.Api.SignMessageRequest request) {
      return blockingUnaryCall(
          getChannel(), getSignMessageMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.SignMessagesResponse signMessages(me.exrates.service.decred.rpc.Api.SignMessagesRequest request) {
      return blockingUnaryCall(
          getChannel(), getSignMessagesMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.ValidateAddressResponse validateAddress(me.exrates.service.decred.rpc.Api.ValidateAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), getValidateAddressMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.rpc.Api.CommittedTicketsResponse committedTickets(me.exrates.service.decred.rpc.Api.CommittedTicketsRequest request) {
      return blockingUnaryCall(
          getChannel(), getCommittedTicketsMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class WalletServiceFutureStub extends io.grpc.stub.AbstractStub<WalletServiceFutureStub> {
    private WalletServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.PingResponse> ping(
        me.exrates.service.decred.rpc.Api.PingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPingMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.NetworkResponse> network(
        me.exrates.service.decred.rpc.Api.NetworkRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getNetworkMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.AccountNumberResponse> accountNumber(
        me.exrates.service.decred.rpc.Api.AccountNumberRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAccountNumberMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.AccountsResponse> accounts(
        me.exrates.service.decred.rpc.Api.AccountsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAccountsMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.BalanceResponse> balance(
        me.exrates.service.decred.rpc.Api.BalanceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getBalanceMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.GetTransactionResponse> getTransaction(
        me.exrates.service.decred.rpc.Api.GetTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetTransactionMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.TicketPriceResponse> ticketPrice(
        me.exrates.service.decred.rpc.Api.TicketPriceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTicketPriceMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.StakeInfoResponse> stakeInfo(
        me.exrates.service.decred.rpc.Api.StakeInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStakeInfoMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.BlockInfoResponse> blockInfo(
        me.exrates.service.decred.rpc.Api.BlockInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getBlockInfoMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.BestBlockResponse> bestBlock(
        me.exrates.service.decred.rpc.Api.BestBlockRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getBestBlockMethodHelper(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Control
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.ChangePassphraseResponse> changePassphrase(
        me.exrates.service.decred.rpc.Api.ChangePassphraseRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getChangePassphraseMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.RenameAccountResponse> renameAccount(
        me.exrates.service.decred.rpc.Api.RenameAccountRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRenameAccountMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.NextAccountResponse> nextAccount(
        me.exrates.service.decred.rpc.Api.NextAccountRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getNextAccountMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.NextAddressResponse> nextAddress(
        me.exrates.service.decred.rpc.Api.NextAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getNextAddressMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse> importPrivateKey(
        me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getImportPrivateKeyMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.ImportScriptResponse> importScript(
        me.exrates.service.decred.rpc.Api.ImportScriptRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getImportScriptMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.FundTransactionResponse> fundTransaction(
        me.exrates.service.decred.rpc.Api.FundTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFundTransactionMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.ConstructTransactionResponse> constructTransaction(
        me.exrates.service.decred.rpc.Api.ConstructTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getConstructTransactionMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SignTransactionResponse> signTransaction(
        me.exrates.service.decred.rpc.Api.SignTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSignTransactionMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SignTransactionsResponse> signTransactions(
        me.exrates.service.decred.rpc.Api.SignTransactionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSignTransactionsMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.CreateSignatureResponse> createSignature(
        me.exrates.service.decred.rpc.Api.CreateSignatureRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateSignatureMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.PublishTransactionResponse> publishTransaction(
        me.exrates.service.decred.rpc.Api.PublishTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPublishTransactionMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse> publishUnminedTransactions(
        me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPublishUnminedTransactionsMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse> purchaseTickets(
        me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPurchaseTicketsMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.RevokeTicketsResponse> revokeTickets(
        me.exrates.service.decred.rpc.Api.RevokeTicketsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRevokeTicketsMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse> loadActiveDataFilters(
        me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLoadActiveDataFiltersMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SignMessageResponse> signMessage(
        me.exrates.service.decred.rpc.Api.SignMessageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSignMessageMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.SignMessagesResponse> signMessages(
        me.exrates.service.decred.rpc.Api.SignMessagesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSignMessagesMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.ValidateAddressResponse> validateAddress(
        me.exrates.service.decred.rpc.Api.ValidateAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getValidateAddressMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.rpc.Api.CommittedTicketsResponse> committedTickets(
        me.exrates.service.decred.rpc.Api.CommittedTicketsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCommittedTicketsMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_NETWORK = 1;
  private static final int METHODID_ACCOUNT_NUMBER = 2;
  private static final int METHODID_ACCOUNTS = 3;
  private static final int METHODID_BALANCE = 4;
  private static final int METHODID_GET_TRANSACTION = 5;
  private static final int METHODID_GET_TRANSACTIONS = 6;
  private static final int METHODID_GET_TICKETS = 7;
  private static final int METHODID_TICKET_PRICE = 8;
  private static final int METHODID_STAKE_INFO = 9;
  private static final int METHODID_BLOCK_INFO = 10;
  private static final int METHODID_BEST_BLOCK = 11;
  private static final int METHODID_TRANSACTION_NOTIFICATIONS = 12;
  private static final int METHODID_ACCOUNT_NOTIFICATIONS = 13;
  private static final int METHODID_CHANGE_PASSPHRASE = 14;
  private static final int METHODID_RENAME_ACCOUNT = 15;
  private static final int METHODID_RESCAN = 16;
  private static final int METHODID_NEXT_ACCOUNT = 17;
  private static final int METHODID_NEXT_ADDRESS = 18;
  private static final int METHODID_IMPORT_PRIVATE_KEY = 19;
  private static final int METHODID_IMPORT_SCRIPT = 20;
  private static final int METHODID_FUND_TRANSACTION = 21;
  private static final int METHODID_UNSPENT_OUTPUTS = 22;
  private static final int METHODID_CONSTRUCT_TRANSACTION = 23;
  private static final int METHODID_SIGN_TRANSACTION = 24;
  private static final int METHODID_SIGN_TRANSACTIONS = 25;
  private static final int METHODID_CREATE_SIGNATURE = 26;
  private static final int METHODID_PUBLISH_TRANSACTION = 27;
  private static final int METHODID_PUBLISH_UNMINED_TRANSACTIONS = 28;
  private static final int METHODID_PURCHASE_TICKETS = 29;
  private static final int METHODID_REVOKE_TICKETS = 30;
  private static final int METHODID_LOAD_ACTIVE_DATA_FILTERS = 31;
  private static final int METHODID_SIGN_MESSAGE = 32;
  private static final int METHODID_SIGN_MESSAGES = 33;
  private static final int METHODID_VALIDATE_ADDRESS = 34;
  private static final int METHODID_COMMITTED_TICKETS = 35;
  private static final int METHODID_CONFIRMATION_NOTIFICATIONS = 36;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final WalletServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(WalletServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((me.exrates.service.decred.rpc.Api.PingRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PingResponse>) responseObserver);
          break;
        case METHODID_NETWORK:
          serviceImpl.network((me.exrates.service.decred.rpc.Api.NetworkRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NetworkResponse>) responseObserver);
          break;
        case METHODID_ACCOUNT_NUMBER:
          serviceImpl.accountNumber((me.exrates.service.decred.rpc.Api.AccountNumberRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountNumberResponse>) responseObserver);
          break;
        case METHODID_ACCOUNTS:
          serviceImpl.accounts((me.exrates.service.decred.rpc.Api.AccountsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountsResponse>) responseObserver);
          break;
        case METHODID_BALANCE:
          serviceImpl.balance((me.exrates.service.decred.rpc.Api.BalanceRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BalanceResponse>) responseObserver);
          break;
        case METHODID_GET_TRANSACTION:
          serviceImpl.getTransaction((me.exrates.service.decred.rpc.Api.GetTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTransactionResponse>) responseObserver);
          break;
        case METHODID_GET_TRANSACTIONS:
          serviceImpl.getTransactions((me.exrates.service.decred.rpc.Api.GetTransactionsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTransactionsResponse>) responseObserver);
          break;
        case METHODID_GET_TICKETS:
          serviceImpl.getTickets((me.exrates.service.decred.rpc.Api.GetTicketsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.GetTicketsResponse>) responseObserver);
          break;
        case METHODID_TICKET_PRICE:
          serviceImpl.ticketPrice((me.exrates.service.decred.rpc.Api.TicketPriceRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TicketPriceResponse>) responseObserver);
          break;
        case METHODID_STAKE_INFO:
          serviceImpl.stakeInfo((me.exrates.service.decred.rpc.Api.StakeInfoRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.StakeInfoResponse>) responseObserver);
          break;
        case METHODID_BLOCK_INFO:
          serviceImpl.blockInfo((me.exrates.service.decred.rpc.Api.BlockInfoRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BlockInfoResponse>) responseObserver);
          break;
        case METHODID_BEST_BLOCK:
          serviceImpl.bestBlock((me.exrates.service.decred.rpc.Api.BestBlockRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.BestBlockResponse>) responseObserver);
          break;
        case METHODID_TRANSACTION_NOTIFICATIONS:
          serviceImpl.transactionNotifications((me.exrates.service.decred.rpc.Api.TransactionNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.TransactionNotificationsResponse>) responseObserver);
          break;
        case METHODID_ACCOUNT_NOTIFICATIONS:
          serviceImpl.accountNotifications((me.exrates.service.decred.rpc.Api.AccountNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.AccountNotificationsResponse>) responseObserver);
          break;
        case METHODID_CHANGE_PASSPHRASE:
          serviceImpl.changePassphrase((me.exrates.service.decred.rpc.Api.ChangePassphraseRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ChangePassphraseResponse>) responseObserver);
          break;
        case METHODID_RENAME_ACCOUNT:
          serviceImpl.renameAccount((me.exrates.service.decred.rpc.Api.RenameAccountRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RenameAccountResponse>) responseObserver);
          break;
        case METHODID_RESCAN:
          serviceImpl.rescan((me.exrates.service.decred.rpc.Api.RescanRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RescanResponse>) responseObserver);
          break;
        case METHODID_NEXT_ACCOUNT:
          serviceImpl.nextAccount((me.exrates.service.decred.rpc.Api.NextAccountRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NextAccountResponse>) responseObserver);
          break;
        case METHODID_NEXT_ADDRESS:
          serviceImpl.nextAddress((me.exrates.service.decred.rpc.Api.NextAddressRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.NextAddressResponse>) responseObserver);
          break;
        case METHODID_IMPORT_PRIVATE_KEY:
          serviceImpl.importPrivateKey((me.exrates.service.decred.rpc.Api.ImportPrivateKeyRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ImportPrivateKeyResponse>) responseObserver);
          break;
        case METHODID_IMPORT_SCRIPT:
          serviceImpl.importScript((me.exrates.service.decred.rpc.Api.ImportScriptRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ImportScriptResponse>) responseObserver);
          break;
        case METHODID_FUND_TRANSACTION:
          serviceImpl.fundTransaction((me.exrates.service.decred.rpc.Api.FundTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.FundTransactionResponse>) responseObserver);
          break;
        case METHODID_UNSPENT_OUTPUTS:
          serviceImpl.unspentOutputs((me.exrates.service.decred.rpc.Api.UnspentOutputsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.UnspentOutputResponse>) responseObserver);
          break;
        case METHODID_CONSTRUCT_TRANSACTION:
          serviceImpl.constructTransaction((me.exrates.service.decred.rpc.Api.ConstructTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ConstructTransactionResponse>) responseObserver);
          break;
        case METHODID_SIGN_TRANSACTION:
          serviceImpl.signTransaction((me.exrates.service.decred.rpc.Api.SignTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignTransactionResponse>) responseObserver);
          break;
        case METHODID_SIGN_TRANSACTIONS:
          serviceImpl.signTransactions((me.exrates.service.decred.rpc.Api.SignTransactionsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignTransactionsResponse>) responseObserver);
          break;
        case METHODID_CREATE_SIGNATURE:
          serviceImpl.createSignature((me.exrates.service.decred.rpc.Api.CreateSignatureRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CreateSignatureResponse>) responseObserver);
          break;
        case METHODID_PUBLISH_TRANSACTION:
          serviceImpl.publishTransaction((me.exrates.service.decred.rpc.Api.PublishTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PublishTransactionResponse>) responseObserver);
          break;
        case METHODID_PUBLISH_UNMINED_TRANSACTIONS:
          serviceImpl.publishUnminedTransactions((me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PublishUnminedTransactionsResponse>) responseObserver);
          break;
        case METHODID_PURCHASE_TICKETS:
          serviceImpl.purchaseTickets((me.exrates.service.decred.rpc.Api.PurchaseTicketsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.PurchaseTicketsResponse>) responseObserver);
          break;
        case METHODID_REVOKE_TICKETS:
          serviceImpl.revokeTickets((me.exrates.service.decred.rpc.Api.RevokeTicketsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.RevokeTicketsResponse>) responseObserver);
          break;
        case METHODID_LOAD_ACTIVE_DATA_FILTERS:
          serviceImpl.loadActiveDataFilters((me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.LoadActiveDataFiltersResponse>) responseObserver);
          break;
        case METHODID_SIGN_MESSAGE:
          serviceImpl.signMessage((me.exrates.service.decred.rpc.Api.SignMessageRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignMessageResponse>) responseObserver);
          break;
        case METHODID_SIGN_MESSAGES:
          serviceImpl.signMessages((me.exrates.service.decred.rpc.Api.SignMessagesRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.SignMessagesResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_ADDRESS:
          serviceImpl.validateAddress((me.exrates.service.decred.rpc.Api.ValidateAddressRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ValidateAddressResponse>) responseObserver);
          break;
        case METHODID_COMMITTED_TICKETS:
          serviceImpl.committedTickets((me.exrates.service.decred.rpc.Api.CommittedTicketsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.CommittedTicketsResponse>) responseObserver);
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
        case METHODID_CONFIRMATION_NOTIFICATIONS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.confirmationNotifications(
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.rpc.Api.ConfirmationNotificationsResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class WalletServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WalletServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.rpc.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("WalletService");
    }
  }

  private static final class WalletServiceFileDescriptorSupplier
      extends WalletServiceBaseDescriptorSupplier {
    WalletServiceFileDescriptorSupplier() {}
  }

  private static final class WalletServiceMethodDescriptorSupplier
      extends WalletServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    WalletServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (WalletServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WalletServiceFileDescriptorSupplier())
              .addMethod(getPingMethodHelper())
              .addMethod(getNetworkMethodHelper())
              .addMethod(getAccountNumberMethodHelper())
              .addMethod(getAccountsMethodHelper())
              .addMethod(getBalanceMethodHelper())
              .addMethod(getGetTransactionMethodHelper())
              .addMethod(getGetTransactionsMethodHelper())
              .addMethod(getGetTicketsMethodHelper())
              .addMethod(getTicketPriceMethodHelper())
              .addMethod(getStakeInfoMethodHelper())
              .addMethod(getBlockInfoMethodHelper())
              .addMethod(getBestBlockMethodHelper())
              .addMethod(getTransactionNotificationsMethodHelper())
              .addMethod(getAccountNotificationsMethodHelper())
              .addMethod(getConfirmationNotificationsMethodHelper())
              .addMethod(getChangePassphraseMethodHelper())
              .addMethod(getRenameAccountMethodHelper())
              .addMethod(getRescanMethodHelper())
              .addMethod(getNextAccountMethodHelper())
              .addMethod(getNextAddressMethodHelper())
              .addMethod(getImportPrivateKeyMethodHelper())
              .addMethod(getImportScriptMethodHelper())
              .addMethod(getFundTransactionMethodHelper())
              .addMethod(getUnspentOutputsMethodHelper())
              .addMethod(getConstructTransactionMethodHelper())
              .addMethod(getSignTransactionMethodHelper())
              .addMethod(getSignTransactionsMethodHelper())
              .addMethod(getCreateSignatureMethodHelper())
              .addMethod(getPublishTransactionMethodHelper())
              .addMethod(getPublishUnminedTransactionsMethodHelper())
              .addMethod(getPurchaseTicketsMethodHelper())
              .addMethod(getRevokeTicketsMethodHelper())
              .addMethod(getLoadActiveDataFiltersMethodHelper())
              .addMethod(getSignMessageMethodHelper())
              .addMethod(getSignMessagesMethodHelper())
              .addMethod(getValidateAddressMethodHelper())
              .addMethod(getCommittedTicketsMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
