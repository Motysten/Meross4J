package fr.motysten.meross4j;

import com.google.common.primitives.Bytes;
import fr.motysten.meross4j.devices.Light;
import fr.motysten.meross4j.devices.Plug;
import fr.motysten.meross4j.models.Constants;
import fr.motysten.meross4j.mqtt.MQTTClient;
import jakarta.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * HTTP Connection to Meross servers
 */
public class HttpApi {

    private String userId;
    private String token;
    private String key;
    private String mqttDomain;

    /**
     * Initiate HTTP connection to Meross infrastructure
     * @param email     User's email
     * @param password  User's password
     */
    public HttpApi(String email, String password) {
        String credentials = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        HttpResponse<String> response = sendRequest(Constants.LOGIN_URL, credentials);
        JSONObject responseJson = new JSONObject(response.body());

        JSONObject data = responseJson.getJSONObject("data");
        try {
            userId = data.getString("userid");
            token = data.getString("token");
            key = data.getString("key");
            mqttDomain = data.getString("mqttDomain");
        } catch (JSONException e) {
            System.err.println("Incorrect credentials !");
            System.exit(1);
        }
    }

    /**
     * Method that destroy credentials to avoid being blocked by Meross
     * @param client    Instance of your MQTT connection
     */
    public void destroy(MQTTClient client) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                sendRequest(Constants.LOGOUT_URL, "{}");
                client.getClient().disconnect();
                System.out.println("API connection revoked !");
            } catch (InterruptedException | MqttException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Method that lists all your devices and saves their instances
     * @param client    Instance of your MQTT connection
     */
    public void listDevices(MQTTClient client) {
        HttpResponse<String> response = sendRequest(Constants.DEV_LIST, "{}");
        JSONObject responseJson = new JSONObject(response.body());

        for (Object object : responseJson.getJSONArray("data")) {
            JSONObject device = (JSONObject) object;
            if (Plug.types.contains(device.getString("deviceType"))) {
                client.devices.put(device.getString("uuid"), new Plug(device.getString("uuid"), device.getString("devName"), device.getString("fmwareVersion"), device.getString("deviceType"), this, client));
                System.out.println("Device found : " + device.getString("devName") + " (" + device.getString("deviceType") + ") | New Plug device created !");
            } else if (Light.types.contains((device.getString("deviceType")))) {
                client.devices.put(device.getString("uuid"), new Light(device.getString("uuid"), device.getString("devName"), device.getString("fmwareVersion"), device.getString("deviceType"), this, client));
                System.out.println("Device found : " + device.getString("devName") + " (" + device.getString("deviceType") + ") | New Light device created !");
            } else {
                System.out.println("Device found : " + device.getString("devName") + " (" + device.getString("deviceType") + ") | This device is not supported yet. Feel free to open an issue on https://github.com/Motysten/Meross4J and I'll try to implement it as fast as possible !");
            }
        }
    }

    /**
     * Method that sends a request to the HTTP api
     * @param endpoint  Endpoint to use
     * @param payload   JSON payload to send
     * @return          Returns the response of the server
     */
    public HttpResponse<String> sendRequest(String endpoint, String payload) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(Constants.HTTP_API + endpoint));
            requestBuilder.setHeader("Authorization", "Basic " + token);
            requestBuilder.setHeader("Content-Type", "application/json");
            requestBuilder.setHeader("vender", "Meross");
            requestBuilder.setHeader("AppVersion", "1.3.0");
            requestBuilder.setHeader("AppLanguage", "FR");
            requestBuilder.setHeader("User-Agent", "okhttp/3.6.0");

            String b64Params = Base64.getEncoder().encodeToString(payload.getBytes());
            String timestamp = String.valueOf(System.currentTimeMillis());
            String nonce = RandomStringUtils.random(16, true, true).toUpperCase();
            String md5Params = getMD5(b64Params, timestamp, nonce);

            JSONObject params = new JSONObject();
            params.put("params", b64Params);
            params.put("timestamp", timestamp);
            params.put("nonce", nonce);
            params.put("sign", md5Params);

            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(params.toString()));
            HttpRequest request = requestBuilder.build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param params
     * @param timestamp
     * @param nonce
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String getMD5(String params, String timestamp, String nonce) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Bytes.concat(Constants.SECRET.getBytes(), timestamp.getBytes(), nonce.getBytes(), params.getBytes()));
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getKey() {
        return key;
    }

    public String getMqttDomain() {
        return mqttDomain;
    }

}
