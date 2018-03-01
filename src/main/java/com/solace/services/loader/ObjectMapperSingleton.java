package com.solace.services.loader;

import com.fasterxml.jackson.databind.ObjectMapper;

class ObjectMapperSingleton extends ObjectMapper {
    private static ObjectMapper instance;

    private ObjectMapperSingleton() {}

    public static ObjectMapper getInstance() {
        if (instance == null) instance = new ObjectMapper();
        return instance;
    }
}
