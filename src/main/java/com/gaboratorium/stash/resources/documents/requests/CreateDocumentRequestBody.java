package com.gaboratorium.stash.resources.documents.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.ValidationMethod;
import lombok.Getter;
import org.postgresql.util.PGobject;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@JsonDeserialize
public class CreateDocumentRequestBody {

    final ObjectMapper mapper = Jackson.newObjectMapper();

    @JsonProperty @NotNull @Getter
    public JsonNode documentContent;

    @JsonProperty @Getter
    public Optional<String> documentOwnerId;

    public PGobject getDocumentContentAsJsonb() throws SQLException, JsonProcessingException {
        PGobject documentContentAsJsonb = new PGobject();
        documentContentAsJsonb.setType("jsonb");
        documentContentAsJsonb.setValue(
            mapper.writeValueAsString(documentContent)
        );
        return documentContentAsJsonb;
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
