package com.noagility.personalcrm.deserializer;
import java.io.IOException;
import java.time.LocalDate;

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

import org.springframework.stereotype.Component;

@Component("accountDeserializer")
public class AccountDeserializer extends StdDeserializer<Account> {

    public AccountDeserializer() {
        this(null);
    }

    public AccountDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Account deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException{
        Account account = new Account();
        ObjectCodec codec= parser.getCodec();
        JsonNode node = codec.readTree(parser);

        try{
            account.setAccountID(Integer.parseInt(node.get("accountID").asText()));
            account.setAccountUsername(node.get("accountUsername").asText());
            account.setAccountName(node.get("accountName").asText());
            account.setAccountDOB(LocalDate.parse(node.get("accountDOB").asText()));
            account.setAccountCreation(LocalDate.parse(node.get("accountCreation").asText()));
            account.setAccountActive(node.get("accountActive").asBoolean());
            return account;
        }
        catch(Exception e){
            e.printStackTrace();
        } 

        return null;
    }

    public Account deserializeAccount(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("AccountDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Account.class, new AccountDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Account.class);
    }

    public Account[] deserializeAccounts(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("AccountDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Account.class, new AccountDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Account[].class);
    }
}