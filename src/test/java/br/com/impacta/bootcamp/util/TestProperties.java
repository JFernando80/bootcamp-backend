package br.com.impacta.bootcamp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.Iterator;

public class TestProperties {

    public static void testar(Object o) throws JsonProcessingException, JSONException {
        String json = new ObjectMapper().writeValueAsString(o);

        JSONObject jsonObject = new JSONObject(json);
        Iterator keys  = jsonObject.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            Assertions.assertNotEquals("null", jsonObject.getString(key));
        }
    }
}
