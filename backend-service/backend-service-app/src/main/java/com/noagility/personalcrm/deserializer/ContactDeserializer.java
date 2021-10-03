package com.noagility.personalcrm.deserializer;

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
import com.noagility.personalcrm.model.Contact;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component("contactDeserializer")
public class ContactDeserializer extends StdDeserializer<Contact> {
    public ContactDeserializer() {
        this(null);
    }

    public ContactDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Contact deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        Contact contact = new Contact();
        ObjectCodec codec= parser.getCodec();
        JsonNode node = codec.readTree(parser);

        try{
            contact.setContactID(Integer.parseInt(node.get("contactID").asText()));
            contact.setContactCreatedOn(LocalDate.parse(node.get("contactCreatedOn").asText()));
            return contact;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Contact deserializeContact(String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("ContactDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Contact.class, new ContactDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Contact.class);
    }
}
