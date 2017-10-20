package com.gaboratorium.stash.modules.stashResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import javax.ws.rs.core.Response;

public class StashResponse {
    private static ObjectMapper mapper = Jackson.newObjectMapper();

    // Ok

    public static Response ok() { return build(200); }
    public static Response ok(String message) { return build(200, message); }
    public static Response ok(Object responseObject) { return build(200, responseObject); }

    // Forbidden
    public static Response forbidden() { return build(403, "Forbidden"); }
    public static Response forbidden(String message) { return build(403, message); }
    public static Response forbidden(Object responseObject) { return build(403, responseObject); }

    // Build

    private static Response build(Integer status) {
        return Response.status(status).build();
    }

    private static Response build(Integer status, String message) {
        final JsonNode responseMessage = mapper
            .createObjectNode()
            .put("response_message", message);

        return Response.status(status).entity(responseMessage).build();
    }

    private static Response build(Integer status, Object responseObject) {
        final String responseObjectJson = getResponseObjectAsString(responseObject);
        return Response.status(status).entity(responseObjectJson).build();
    }

    private static String getResponseObjectAsString(Object responseObject) {
        try {
            return mapper
                .writer()
                .withDefaultPrettyPrinter()
                .writeValueAsString(responseObject);
        } catch (JsonProcessingException e) {
            return "Response object parsing failed.";
        }
    }
}
