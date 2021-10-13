package com.noagility.personalcrm.deserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.noagility.personalcrm.model.Meeting;
import com.noagility.personalcrm.model.Minute;

import org.springframework.stereotype.Component;

@Component("meetingDeserializer")
public class MeetingDeserializer extends StdDeserializer<Meeting>{
    
    //  Autowired doesn't work here
    MinuteDeserializer minuteDeserializer = new MinuteDeserializer();

    public MeetingDeserializer(){
        this(null);
    }

    public MeetingDeserializer(Class<?> vc){
        super(vc);
    }

    @Override
    public Meeting deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException{
        Meeting meeting = new Meeting();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        try{
            ObjectMapper mapper = new ObjectMapper();
            meeting.setMeetingID(Integer.parseInt(node.get("meetingID").asText()));
            meeting.setMeetingCreatorID(Integer.parseInt(node.get("meetingCreatorID").asText()));
            meeting.setMeetingDescription(node.get("meetingDescription").asText());
            meeting.setMeetingCreation(LocalDateTime.parse(node.get("meetingCreation").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            meeting.setMeetingStart(LocalDateTime.parse(node.get("meetingStart").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            meeting.setMeetingEnd(LocalDateTime.parse(node.get("meetingEnd").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            meeting.setMeetingName(node.get("meetingName").asText());
            meeting.setMeetingParticipants(mapper.readValue(node.get("meetingParticipants").toString(), new TypeReference<Map<Integer, Boolean>>(){}));

            String m = node.get("meetingMinutes").toString();

            if(!m.equals("[]")){
                Minute minutes[] = minuteDeserializer.deserializeMinutes(m);
                meeting.setMeetingMinutes(new ArrayList<>(Arrays.asList(minutes)));
            }
            else{
                meeting.setMeetingMinutes(new ArrayList<>());
            }
            
            return meeting;

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Meeting deserializeMeeting(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("MeetingDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Meeting.class, new MeetingDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Meeting.class);
    }

    public Meeting[] deserializeMeetings(String json) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("MeetingDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Meeting.class, new MeetingDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(json, Meeting[].class);
    }
}
