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
import com.noagility.personalcrm.model.Message;

import org.springframework.stereotype.Component;

@Component("messageDeserializer")
public class MessageDeserializer extends StdDeserializer<Message>{
    
    public MessageDeserializer(){
        this(null);
    }

    public MessageDeserializer(Class<?> vc){
        super(vc);
    }

    @Override
    public Message deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException{
        Message message = new Message();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        try{
            message.setAccountID(Integer.parseInt(node.get("accountID").asText()));
            message.setChatID(Integer.parseInt(node.get("chatID").asText()));
            message.setMessageID(Integer.parseInt(node.get("messageID").asText()));
            message.setMessageText(node.get("messageText").asText());
            message.setMessageTime(LocalDateTime.parse(node.get("messageTime").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return message;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Message deserializeMessage(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("MessageDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Message.class, new MessageDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Message.class);
    }

    public Message[] deserializeMessages(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("MessageDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Message.class, new MessageDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Message[].class);
    }
}
