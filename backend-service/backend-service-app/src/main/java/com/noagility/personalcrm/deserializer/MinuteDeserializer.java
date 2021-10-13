package com.noagility.personalcrm.deserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.noagility.personalcrm.model.Minute;

import org.springframework.stereotype.Component;

@Component("minuteDeserializer")
public class MinuteDeserializer extends StdDeserializer<Minute> {
    
    public MinuteDeserializer(){
        this(null);
    }

    public MinuteDeserializer(Class<?> vc){
        super(vc);
    }

    @Override
    public Minute deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException{
        Minute minute = new Minute();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        try{
            minute.setMinuteID(Integer.parseInt(node.get("minuteID").asText()));
            minute.setMeetingID(Integer.parseInt(node.get("meetingID").asText()));
            minute.setMinuteCreation(LocalDateTime.parse(node.get("minuteCreation").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            minute.setAccountID(Integer.parseInt(node.get("accountID").asText()));
            minute.setMinuteText(node.get("minuteText").asText());
            return minute;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Minute deserializeMinute(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("MinuteDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Minute.class, new MinuteDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Minute.class);
    }

    public Minute[] deserializeMinutes(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("MinuteDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Minute.class, new MinuteDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Minute[].class);
    }
}
