package me.exrates.service.neo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.merchants.neo.*;
import me.exrates.service.exception.NeoApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log4j2(topic = "neo_log")
public class NeoNodeServiceImpl implements NeoNodeService {


    private String endpoint;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    NeoNodeServiceImpl(String endpoint, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.endpoint = endpoint;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getNewAddress() {
        return invokeJsonRpcMethod("getnewaddress", Collections.emptyList(), new TypeReference<NeoJsonRpcResponse<String>>() {});
    }

    @Override
    public Integer getBlockCount() {
        return invokeJsonRpcMethod("getblockcount", Collections.emptyList(), new TypeReference<NeoJsonRpcResponse<Integer>>() {});
    }

    @Override
    public Optional<Block> getBlock(Integer height) {
        try {
            return Optional.of(invokeJsonRpcMethod("getblock", Arrays.asList(height, 1), new TypeReference<NeoJsonRpcResponse<Block>>() {}));
        } catch (Exception e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<NeoTransaction> getTransactionById(String txId) {
        try {
            return Optional.of(invokeJsonRpcMethod("getrawtransaction", Arrays.asList(txId, 1), new TypeReference<NeoJsonRpcResponse<NeoTransaction>>() {}));
        } catch (Exception e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public NeoTransaction sendToAddress(NeoAsset asset, String address, BigDecimal amount, String changeAddress) {
        return invokeJsonRpcMethod("sendtoaddress", Arrays.asList(asset.getId(), address, amount, 0, changeAddress), new TypeReference<NeoJsonRpcResponse<NeoTransaction>>() {});
    }


    private <T> T invokeJsonRpcMethod(String methodName, List<Object> args, TypeReference<NeoJsonRpcResponse<T>> typeReference) {
        NeoJsonRpcRequest request = new NeoJsonRpcRequest();
        request.setMethod(methodName);
        request.setParams(args);
        return getNeoJsonRpcResponse(request, typeReference);
    }

    private <T> T getNeoJsonRpcResponse(NeoJsonRpcRequest request, TypeReference<NeoJsonRpcResponse<T>> typeReference) {
        String responseString = restTemplate.postForObject(endpoint, request, String.class);
        try {
            NeoJsonRpcResponse<T> response = objectMapper.readValue(responseString,  typeReference);
            if (response.getError() != null) {
                log.error(response.getError());
                throw new NeoApiException(response.getError().getCode(), response.getError().getMessage());
            }
            if (response.getResult() == null) {
                throw new NeoApiException("No result found in response");
            }
            return response.getResult();
        } catch (IOException e) {
            throw new NeoApiException(e);
        }
    }




}
