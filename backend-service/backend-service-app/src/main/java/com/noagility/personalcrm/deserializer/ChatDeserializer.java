package com.noagility.personalcrm.deserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Chat;
import com.noagility.personalcrm.model.Message;

import org.springframework.stereotype.Component;

@Component("chatDeserializer")
public class ChatDeserializer extends StdDeserializer<Chat>{
    
    //  Autowired doesn't work for these for some reason
    MessageDeserializer messageDeserializer = new MessageDeserializer();

    AccountDeserializer accountDeserializer = new AccountDeserializer();

    public ChatDeserializer(){
        this(null);
    }

    public ChatDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Chat deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException{
        Chat chat = new Chat();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        
        try{
            chat.setChatCreation(LocalDateTime.parse(node.get("chatCreation").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            chat.setChatID(Integer.parseInt(node.get("chatID").asText()));
            String m = node.get("messages").toString();

            if(!m.equals("[]")){
                Message messages[] = messageDeserializer.deserializeMessages(m);
                chat.setChatMessages(new ArrayList<>(Arrays.asList(messages)));
            }
            else{
                chat.setChatMessages(new ArrayList<>());
            }

            List<Account> accounts = new ArrayList<>(Arrays.asList(accountDeserializer.deserializeAccounts(node.get("chatParticipants").toString())));
            chat.setChatParticipants(accounts);

            return chat;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Chat deserializeChat(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("ChatDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Chat.class, new ChatDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Chat.class);
    }

    public Chat[] deserializeChats(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("ChatDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Chat.class, new ChatDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Chat[].class);
    }
}

