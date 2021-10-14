package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.Holiday;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
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
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Iterator;

@RestController
@RequestMapping("/calendar")
public class CalendarController {


    @RequestMapping(
            value = "/public_holiday/get",
            method = RequestMethod.GET
    )
    public ResponseEntity<ArrayList<Holiday>> create() throws Exception{

        FileInputStream fin = new FileInputStream("src/main/resources/Victorian-public-holiday-dates.ics");

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(fin);

        ArrayList<Holiday> holidays = new ArrayList<Holiday>();
        for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            //jankiest line in the project
            if(component.getName().equals("VEVENT") && component.getProperties().get(2).getValue().substring(0,4).equals(Year.now().toString())) {
                String date = component.getProperties().get(2).getValue();
                holidays.add(new Holiday(component.getProperties().get(4).getValue(), LocalDate.parse(date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6))));
            }
        }

        return ResponseEntity.ok().body(holidays);
    }




}
