package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.Holiday;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.fortuna.ical4j.model.Calendar;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    /**
     * API Endpoint to fetch the national public holidays of Australia
     * @return A ResponseEntity containing an array of Holiday objects
     * @throws Exception Indicates that the server has failed to load and send holiday data
     */
    @RequestMapping(
            value = "/public_holiday/get",
            method = RequestMethod.GET
    )
    public ResponseEntity<ArrayList<Holiday>> create() throws Exception{

        InputStream fin = getClass().getResourceAsStream("/Victorian-public-holiday-dates.ics");

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(fin);

        ArrayList<Holiday> holidays = new ArrayList<Holiday>();
        for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            //jankiest line in the project
            if(component.getName().equals("VEVENT")) {
                String date = component.getProperties().get(2).getValue();
                holidays.add(new Holiday(component.getProperties().get(4).getValue(), LocalDate.parse(date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6))));
            }
        }

        return ResponseEntity.ok().body(holidays);
    }




}
