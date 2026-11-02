package com.project.booking.service.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.TreeSet;

public final class CanonicalPayload {
    private static final ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();

    private CanonicalPayload() {}

    public static String hash(Object payload) {
        try {
            JsonNode root = JSON.valueToTree(payload);
            if (!root.isObject()) throw new IllegalArgumentException("Command must be a JSON object");
            ((ObjectNode) root).remove("clientRequestId");
            byte[] bytes = JSON.writeValueAsString(sorted(root)).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot canonicalize command", ex);
        }
    }

    private static JsonNode sorted(JsonNode value) {
        if (value.isObject()) {
            ObjectNode result = JSON.createObjectNode();
            TreeSet<String> keys = new TreeSet<>();
            value.fieldNames().forEachRemaining(keys::add);
            keys.forEach(key -> result.set(key, sorted(value.get(key))));
            return result;
        }
        if (value.isArray()) {
            ArrayNode result = JSON.createArrayNode();
            value.forEach(item -> result.add(sorted(item)));
            return result;
        }
        return value;
    }
}
