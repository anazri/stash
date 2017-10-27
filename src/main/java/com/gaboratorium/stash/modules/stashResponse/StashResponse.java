package com.gaboratorium.stash.modules.stashResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import lombok.Getter;
import lombok.Setter;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

public class StashResponse {

    @Getter @Setter
    private  boolean isValid;

    private Integer status = 200;

    private static ObjectMapper mapper = Jackson.newObjectMapper();

    // Ok
    public static Response ok() { return build(200); }
    public static Response ok(String message) { return build(200, message); }
    public static Response ok(Object responseObject) { return build(200, responseObject); }

    // Not found
    public static Response notFound() { return build(404, "Not found"); }
    public static Response notFound(String message) { return build(404, message); }
    public static Response notFound(Object responseObject) { return build(404, responseObject); }

    // Forbidden
    public static Response forbidden() { return build(403, "Forbidden"); }
    public static Response forbidden(String message) { return build(403, message); }
    public static Response forbidden(Object responseObject) { return build(403, responseObject); }

    // Conflict
    public static Response conflict() { return build(409, "Conflict"); }
    public static Response conflict(String message) { return build(409, message); }
    public static Response conflict(Object responseObject) { return build(409, responseObject); }

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

    // Chaining

    public StashResponse validate(Callable<Boolean> func) throws Exception {
        this.setValid(func.call());
        return this;
    }

    public StashResponse onInvalid(Callable<StashResponse> func) throws Exception {
        return !this.isValid ? func.call() : this;
    }

    public StashResponse onValid(Callable<StashResponse> func) throws Exception {
        return this.isValid ? func.call() : this;
    }

    public Response build() {
        return build(this.status);
    }
}
