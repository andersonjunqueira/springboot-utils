package br.com.iwstech.util.infraestructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class EnvService {

    @Autowired
    private Environment env;

    protected String getEnvProperty(String str) {
        return env.getProperty(str);
    }
}