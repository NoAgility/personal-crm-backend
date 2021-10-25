package com.noagility.personalcrm.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"local", "ete"})
public class TinyintMapperH2Service implements TinyintMapperService {

    /**
     * Method to map an object to a byte to be compared with 0
     * NOTE: This method is for mapping TINYINT of H2 databases.
     * @param target The object to map
     * @return A boolean
     */
    @Override
    public boolean map(Object target) {
        return (Byte)target != 0;
    }
}
