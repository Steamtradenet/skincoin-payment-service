package net.steamtrade.payment.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sasha on 24.02.16.
 */
@NoArgsConstructor
@JsonSerialize(using = SocketMessage.SoketMessageSerializer.class)
public class SocketMessage {

    public enum Type {
        STATUS("/queue/mine");

        @Getter private String destination;

        Type(String destination) {
            this.destination = destination;
        }
    }

    @Getter private Type type;
    @Getter private final Map<String, Object> payloads = new HashMap<>();

    public SocketMessage(Type type) {
        this.type = type;
    }

    public SocketMessage addPayload(String name, Object payload) {
        this.payloads.put(name, payload);
        return this;
    }

    public static class SoketMessageSerializer extends JsonSerializer<SocketMessage> {
        @Override
        public void serialize(SocketMessage response, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            if(response.getType() != null) {
                jgen.writeStringField("type", response.getType().name());
            }
            for(Map.Entry<String, Object> payload : response.getPayloads().entrySet()) {
                jgen.writeFieldName(payload.getKey());
                jgen.writeObject(payload.getValue());
            }
            jgen.writeEndObject();
        }
    }

}
