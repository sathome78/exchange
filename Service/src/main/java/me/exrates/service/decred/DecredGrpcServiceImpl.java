package me.exrates.service.decred;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslProvider;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.service.decred.rpc.Api;
import me.exrates.service.decred.rpc.WalletServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@PropertySource("classpath:/merchants/decred.properties")
@Service
@Log4j2(topic = "decred")
public class DecredGrpcServiceImpl implements DecredGrpcService{

    private @Value("${decred.host}")String host;
    private @Value("${decred.port}")String port;

    private ManagedChannel channel = null;

    /*@PostConstruct
    private void init() {
        connect();
    }*/

    private ManagedChannel getChannel() {
        checkConnect();
        return channel;
    }

    private void connect() {
        log.debug("connect");
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            InputStream stream = loader.getResourceAsStream("ca.crt");
            log.debug("stream size {}", stream.available());
            channel = NettyChannelBuilder.forAddress(host, Integer.valueOf(port))
                    .sslContext(GrpcSslContexts
                        .forClient()
                        .trustManager(stream)
                        .build())
                    .build();
        } catch (Exception e) {
            System.out.println(e);
            log.error(e);
            throw new RuntimeException(e);
        }
        log.debug("channel created");
    }

    private void checkConnect() {
        if (channel == null || channel.isShutdown()) {
            connect();
        }
    }

    @Override
    public Api.NextAddressResponse getNewAddress() {
        WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(getChannel());
        return stub.nextAddress(Api.NextAddressRequest
                .newBuilder()
                .setKind(Api.NextAddressRequest.Kind.BIP0044_INTERNAL)
                .setAccount(0)
                .setGapPolicy(Api.NextAddressRequest.GapPolicy.GAP_POLICY_IGNORE)
                .build());
    }

    @Override
    public Iterator<Api.GetTransactionsResponse> getTransactions(int startBlock, int endBlockHeight) {
        WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(getChannel());
        return stub.getTransactions(Api.GetTransactionsRequest
                .newBuilder()
                .setStartingBlockHeight(startBlock)
                .setEndingBlockHeight(endBlockHeight)
                .build());
    }

    @Override
    public Api.BestBlockResponse getBlockInfo() {
        WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(getChannel());
        return stub.bestBlock(Api.BestBlockRequest.getDefaultInstance());
    }



    @PreDestroy
    private void destroy() {
        channel.shutdown();
    }
}
