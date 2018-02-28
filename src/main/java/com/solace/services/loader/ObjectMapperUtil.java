package com.solace.services.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

class ObjectMapperUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ObjectMapperUtil() {}

    public static ObjectReader getReader(Class readerType) {
        return objectMapper.readerFor(readerType);
    }

    public static ObjectWriter getWriter(Class writerType) {
        return objectMapper.writerFor(writerType);
    }
}
