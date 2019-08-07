package me.exrates.service.lisk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import me.exrates.service.exception.LiskRestException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class LiskRestUtils {

    private LiskRestUtils() {
    }

    /**
     * An additional method without a field `success` for the node Lisk v1.0 | Special for Lisk v1.0
     * @param objectMapper
     * @param responseBody
     * @param targetFieldName
     * @param type
     * @param <T>
     * @return
     */
    public static  <T> T extractObjectFromResponseAdditional(ObjectMapper objectMapper, String responseBody, String targetFieldName, Class<T> type)  {
        try {
            return objectMapper.treeToValue(extractTargetNodeFromLiskResponseAdditional(objectMapper, responseBody, targetFieldName, JsonNodeType.ARRAY), type);
        } catch (JsonProcessingException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    public static  <T> T extractObjectFromResponse(ObjectMapper objectMapper, String responseBody, String targetFieldName, Class<T> type)  {
        try {
            return objectMapper.treeToValue(extractTargetNodeFromLiskResponse(objectMapper, responseBody, targetFieldName, JsonNodeType.OBJECT), type);
        } catch (JsonProcessingException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    public static <T> List<T> extractListFromResponse(ObjectMapper objectMapper, String responseBody, String targetFieldName, Class<T> listElementType)  {
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, listElementType);
            String array = extractTargetNodeFromLiskResponse(objectMapper, responseBody, targetFieldName, JsonNodeType.ARRAY).toString();
            return objectMapper.readValue(array, type);
        } catch (IOException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    /**
     * An additional method without a field `success` for the node Lisk v1.0 | Special for Lisk v1.0
     * @param objectMapper
     * @param responseBody
     * @param targetFieldName
     * @param listElementType
     * @param <T>
     * @return
     */
    public static <T> List<T> extractListFromResponseAdditional(ObjectMapper objectMapper, String responseBody, String targetFieldName, Class<T> listElementType)  {
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, listElementType);
            String array = extractTargetNodeFromLiskResponseAdditional(objectMapper, responseBody, targetFieldName, JsonNodeType.ARRAY).toString();
            return objectMapper.readValue(array, type);
        } catch (IOException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    public static JsonNode extractTargetNodeFromLiskResponse(ObjectMapper objectMapper, String responseBody, String targetFieldName, JsonNodeType targetNodeType)  {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode successNode = getAndValidateJsonNode("success", root, JsonNode::isBoolean);
            boolean success = successNode.booleanValue();
            if (success) {
                return getAndValidateJsonNode(targetFieldName, root, jsonNode -> jsonNode.getNodeType() == targetNodeType);
            } else {
                JsonNode error = getAndValidateJsonNode("error", root, JsonNode::isTextual);
                throw new LiskRestException(String.format("API error: %s", error.textValue()));
            }
        } catch (IOException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    /**
     * An additional method without a field `success` for the node Lisk v1.0 | Special for Lisk v1.0
     * @param objectMapper
     * @param responseBody
     * @param targetFieldName
     * @param targetNodeType
     * @return
     */
    public static JsonNode extractTargetNodeFromLiskResponseAdditional(ObjectMapper objectMapper, String responseBody, String targetFieldName, JsonNodeType targetNodeType)  {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
                return getAndValidateJsonNode(targetFieldName, root, jsonNode -> jsonNode.getNodeType() == targetNodeType);
        } catch (IOException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    public static JsonNode getAndValidateJsonNode(String fieldName, JsonNode parent, Predicate<JsonNode> validator) {
        JsonNode target = parent.findValue(fieldName);
        if (target == null) {
            throw new LiskRestException(String.format("Field not found: %s", fieldName));
        } else if (!validator.test(target)) {
            throw new LiskRestException(String.format("Field %s is not in appropriate format: %s", fieldName, target.getNodeType()));
        }
        return target;
    }

    public static URI getURIWithParams(String absoluteURI, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(absoluteURI);
        params.forEach(builder::queryParam);
        return builder.build().encode().toUri();
    }


}
