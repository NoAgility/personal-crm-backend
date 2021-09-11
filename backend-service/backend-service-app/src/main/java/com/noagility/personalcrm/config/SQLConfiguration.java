package com.noagility.personalcrm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.jdbc.Sql;

@Sql("/main/resources/init.sql")
@Profile("local")
@Configuration
public class SQLConfiguration {

}
