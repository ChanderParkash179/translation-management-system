package com.tms.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.app.enums.Message;
import com.tms.app.utils.AppLogger;
import com.tms.app.utils.EncryptionHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//@Component
public class MessageConverter extends AbstractHttpMessageConverter<Object> {

    private final ObjectMapper objectMapper;
    private final AppLogger log = new AppLogger(Message.class);

    public MessageConverter(ObjectMapper objectMapper) {

        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json", StandardCharsets.UTF_8));
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(@NonNull Class<?> clazz) {

        return true;
    }

    @Override
    protected Object readInternal(@NonNull Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        return objectMapper.readValue(decrypt(inputMessage.getBody()), clazz);
    }

    @Override
    protected void writeInternal(@NonNull Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        outputMessage.getBody().write(encrypt(objectMapper.writeValueAsBytes(object)));
    }

    private InputStream decrypt(InputStream inputStream) {

        StringBuilder requestParamString = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                requestParamString.append((char) c);
            }
        } catch (IOException e) {
            log.info("Message {}", e.getMessage());
        }

        try {
            JSONObject requestJsonObject = new
                    JSONObject(requestParamString.toString().replace("\n", ""));
            requestJsonObject = new
                    JSONObject(requestJsonObject.toString().replace("\r", ""));

            log.debug(String.valueOf(requestJsonObject));
            String jsonString = requestJsonObject.getString("data");
            String decryptRequestString = EncryptionHelper.decrypt(jsonString);
            log.debug("decryptRequestString: {}", decryptRequestString);

            if (decryptRequestString != null) {
                return new ByteArrayInputStream(decryptRequestString.getBytes(StandardCharsets.UTF_8));
            } else {
                return inputStream;
            }
        } catch (JSONException ex) {
            log.info("Json Exception Message: {}", ex.getMessage());
            return inputStream;
        }
    }

    private byte[] encrypt(byte[] bytesToEncrypt) {

        String apiJsonResponse = new String(bytesToEncrypt);

        String encryptedString = EncryptionHelper.encrypt(apiJsonResponse);
        if (encryptedString != null) {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("data", encryptedString);
            JSONObject json = new JSONObject(hashMap);
            return json.toString().getBytes();
        } else {
            return bytesToEncrypt;
        }
    }
}
