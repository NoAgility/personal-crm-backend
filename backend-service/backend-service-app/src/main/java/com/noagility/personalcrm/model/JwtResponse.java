package com.noagility.personalcrm.model;

import java.io.Serializable;
import java.util.Date;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    private final Date expDate;

    public JwtResponse(String jwttoken, Date expDate) {
        this.jwttoken = jwttoken;
        this.expDate = expDate;
    }

    public String getToken() {
        return this.jwttoken;
    }
    public Date getExpDate(){
        return this.expDate;
    }
}