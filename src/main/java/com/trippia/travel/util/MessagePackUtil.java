package com.trippia.travel.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.stereotype.Component;

@Component
public class MessagePackUtil {
    public static final ObjectMapper msgpackMapper = new ObjectMapper(new MessagePackFactory());
}
