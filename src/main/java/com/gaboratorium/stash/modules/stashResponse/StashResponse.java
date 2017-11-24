package com.gaboratorium.stash.modules.stashResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jetty.http.HttpStatus;
import javax.ws.rs.core.Response;

public class StashResponse {

    @Getter @Setter
    private  boolean isValid;

    private Integer status = 200;

    private static ObjectMapper mapper = Jackson.newObjectMapper();

    // Ok
    public static Response ok() { return build(HttpStatus.OK_200); }
    public static Response ok(String message) { return build(HttpStatus.OK_200, message); }
    public static Response ok(Object responseObject) { return build(HttpStatus.OK_200, responseObject); }

    // Created
    public static Response created(Object responseObject) { return build(HttpStatus.CREATED_201, responseObject); }

    // No content
    public static Response noContent() { return build(HttpStatus.NO_CONTENT_204); }

    // Bad request
    public static Response badRequest() { return build(HttpStatus.BAD_REQUEST_400, "Bad Request"); }
    public static Response badRequest(String message) { return build(HttpStatus.BAD_REQUEST_400, message); }
    public static Response badRequest(Object responseObject) { return build(HttpStatus.BAD_REQUEST_400, responseObject); }

    // Not found
    public static Response notFound() { return build(HttpStatus.NOT_FOUND_404, "Not Found"); }
    public static Response notFound(String message) { return build(HttpStatus.NOT_FOUND_404, message); }
    public static Response notFound(Object responseObject) { return build(HttpStatus.NOT_FOUND_404, responseObject); }

    // Forbidden
    public static Response forbidden() { return build(HttpStatus.FORBIDDEN_403, "Forbidden"); }
    public static Response forbidden(String message) { return build(HttpStatus.FORBIDDEN_403, message); }
    public static Response forbidden(Object responseObject) { return build(HttpStatus.FORBIDDEN_403, responseObject); }

    // Conflict
    public static Response conflict() { return build(HttpStatus.CONFLICT_409, "Conflict"); }
    public static Response conflict(String message) { return build(HttpStatus.CONFLICT_409, message); }
    public static Response conflict(Object responseObject) { return build(HttpStatus.CONFLICT_409, responseObject); }

    // Internal Server Error
    public static Response error() { return build(HttpStatus.INTERNAL_SERVER_ERROR_500, "Internal Server Error"); }
    public static Response error(String message) { return build(HttpStatus.INTERNAL_SERVER_ERROR_500, message); }
    public static Response error(Object responseObject) { return build(HttpStatus.INTERNAL_SERVER_ERROR_500, responseObject); }

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
