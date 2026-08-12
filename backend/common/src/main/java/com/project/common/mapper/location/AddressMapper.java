package com.project.common.mapper.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.common.dto.location.AddressResponse;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponse toResponse(JsonNode rootNode) {

        AddressResponse response = new AddressResponse();

        if (rootNode == null || rootNode.isNull()) {
            return response;
        }

        response.setFullAddress(
                getJsonValue(rootNode, "display_name"));

        JsonNode addressNode = rootNode.get("address");

        if (addressNode != null && !addressNode.isNull()) {

            String city = getJsonValue(addressNode, "city");

            /*
             * Nominatim không phải lúc nào cũng trả "city".
             * Có thể trả town hoặc municipality tùy vị trí.
             */
            if (city.isBlank()) {
                city = getJsonValue(addressNode, "town");
            }

            if (city.isBlank()) {
                city = getJsonValue(addressNode, "municipality");
            }

            response.setCity(city);

            response.setCountry(
                    getJsonValue(addressNode, "country"));
        }

        return response;
    }

    private String getJsonValue(
            JsonNode node,
            String fieldName) {

        if (node == null) {
            return "";
        }

        JsonNode field = node.get(fieldName);

        if (field == null || field.isNull()) {
            return "";
        }

        return field.asText("");
    }
}