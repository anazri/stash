package com.gaboratorium.stash.resources.documents.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dropwizard.jackson.Jackson;
import lombok.Getter;
import org.postgresql.util.PGobject;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;

@JsonDeserialize
public class UpdateDocumentRequestBody {

    final ObjectMapper mapper = Jackson.newObjectMapper();

    @JsonProperty @NotNull @Getter
    public JsonNode documentContent;

    public PGobject getDocumentContentAsJsonb() throws SQLException, JsonProcessingException {
        PGobject documentContentAsJsonb = new PGobject();
        documentContentAsJsonb.setType("jsonb");
        documentContentAsJsonb.setValue(
            mapper.writeValueAsString(documentContent)
        );
        return documentContentAsJsonb;
    }
}
