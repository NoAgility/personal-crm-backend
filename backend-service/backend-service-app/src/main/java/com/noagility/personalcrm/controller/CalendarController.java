package com.noagility.personalcrm.controller;


import net.fortuna.ical4j.data.CalendarBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.fortuna.ical4j.model.Calendar;
import java.io.FileInputStream;

@RestController
@RequestMapping("/calendar")
public class CalendarController {


    @RequestMapping(
            value = "/public_holiday/get",
            method = RequestMethod.GET
    )
    public ResponseEntity<Resource> create() throws Exception{

        FileInputStream fin = new FileInputStream("src/main/resources/Victorian-public-holiday-dates.ics");

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(fin);



        byte[] calendarByte = calendar.toString().getBytes();
        Resource resource = new ByteArrayResource(calendarByte);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Victorian-public-holiday-dates.ics");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.ok().headers(header).contentType(MediaType.
                        APPLICATION_OCTET_STREAM)
                .body(resource);
    }




}
