package com.noagility.personalcrm.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "prod", "no_h2"})
public class TinyintMapperMySQLService implements TinyintMapperService {

    @Override
    public boolean map(Object target) {
        return (Integer)target != 0;
    }
}
