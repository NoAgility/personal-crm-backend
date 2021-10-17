package com.noagility.personalcrm.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"local", "ete"})
public class TinyintMapperH2Service implements TinyintMapperService {

    @Override
    public boolean map(Object target) {
        return (Byte)target != 0;
    }
}
