package fr.motysten.meross4j.mqtt;

import com.google.common.primitives.Bytes;
import fr.motysten.meross4j.HttpApi;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Utils {

    public static String generatePass(String userId, String key) throws NoSuchAlgorithmException {
        System.out.println("Clear : " + userId + key);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Bytes.concat(userId.getBytes(), key.getBytes()));
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }

    public static String generateAppId() throws NoSuchAlgorithmException {
        UUID uuid = UUID.randomUUID();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Bytes.concat("API".getBytes(), uuid.toString().getBytes()));
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }

    public static HashMap<String, String> buildMessage(String method, String namespace, JSONObject payload, String destinationDeviceUUID, HttpApi api, MQTTClient client) throws NoSuchAlgorithmException {
        String randomString = RandomStringUtils.random(16, true, true).toUpperCase();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(randomString.getBytes(StandardCharsets.UTF_8));
        String messageId = DatatypeConverter.printHexBinary(md.digest()).toLowerCase();

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        md = MessageDigest.getInstance("MD5");
        md.update(Bytes.concat(messageId.getBytes(), api.getKey().getBytes(), timestamp.getBytes()));
        String signature = DatatypeConverter.printHexBinary(md.digest()).toLowerCase();

        JSONObject header = new JSONObject();
        header.put("from", "/app/" + api.getUserId() + "-" + client.getAppId() + "/subscribe");
        header.put("messageId", messageId);
        header.put("method", method);
        header.put("namespace", namespace);
        header.put("payloadVersion", 1);
        header.put("sign", signature);
        header.put("timestamp", timestamp);
        header.put("triggerSrc", "Android");
        header.put("uuid", destinationDeviceUUID);

        JSONObject data = new JSONObject();
        data.put("header", header);
        data.put("payload", payload);

        HashMap<String, String> returnValue = new HashMap<>();
        returnValue.put("messageId", messageId);
        returnValue.put("message", data.toString());

        System.out.println(data);

        return returnValue;
    }

    public static String buildDeviceRequestTopic(String clientId) {
        return "/appliance/" + clientId + "/subscribe";
    }

    public static String buildClientResponseTopic(String userId, String appId) {
        return "/app/" + userId + "-" + appId + "/subscribe";
    }

    public static String buildClientUserTopic(String userId) {
        return "/app/" + userId + "/subscribe";
    }

}
