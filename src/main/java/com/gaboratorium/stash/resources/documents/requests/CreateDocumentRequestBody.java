package com.gaboratorium.stash.resources.documents.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.ValidationMethod;
import lombok.Getter;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@JsonDeserialize
public class CreateDocumentRequestBody {

    final ObjectMapper mapper = Jackson.newObjectMapper();

    @JsonProperty @NotNull @Getter
    public JsonNode documentContent;

    @JsonProperty @NotNull @Getter
    public String documentOwnerId;

    public String getDocumentContentAsString() throws JsonProcessingException{
        return mapper.writeValueAsString(documentContent);
    }

    @ValidationMethod(message = "Document content must be a json object.")
    public boolean isDocumentContentJson() {
        boolean isValidJson = false;
        try {
            mapper.readTree(documentContent.asText());
            isValidJson = true;
        } catch (IOException ignored) {

        }
        return isValidJson;
    }
}
