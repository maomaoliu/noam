package com.thoughtworks.maomao.integration.example;

import com.thoughtworks.maomao.annotations.Bean;
import com.thoughtworks.maomao.annotations.Configuration;
import com.thoughtworks.maomao.noam.SessionFactory;

@Configuration
public class DBConfig {
    @Bean
    public SessionFactory sessionFactory(){
        return new SessionFactory("com.thoughtworks.maomao.integration.example.model");
    }
}
