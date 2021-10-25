package com.noagility.personalcrm.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "prod", "no_h2"})
public class TinyintMapperMySQLService implements TinyintMapperService {

    /**
     * Method to map an object to an integer to be compared with 0
     * NOTE: This method is for mapping TINYINT of MySQL databases.
     * @param target The object to map
     * @return A boolean
     */
    @Override
    public boolean map(Object target) {
        return (Integer)target != 0;
    }
}
