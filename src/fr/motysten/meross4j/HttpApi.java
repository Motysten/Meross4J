package fr.motysten.meross4j;

import com.google.common.primitives.Bytes;
import fr.motysten.meross4j.models.Constants;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HttpApi {

    private String userId;
    private String token;
    private String key;
    private String mqttDomain;

    public HttpApi(String email, String password) throws NoSuchAlgorithmException, IOException, InterruptedException {
        String credentials = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        HttpResponse<String> response = sendRequest(Constants.LOGIN_URL, credentials);
        JSONObject responseJson = new JSONObject(response.body());

        JSONObject data = responseJson.getJSONObject("data");
        try {
            userId = data.getString("userid");
            token = data.getString("token");
            key = data.getString("key");
            mqttDomain = data.getString("mqttDomain");

            System.out.println(response.body());
        } catch (JSONException e) {
            System.err.println("Incorrect credentials !");
            System.exit(1);
        }

    }

    public void destroy() throws NoSuchAlgorithmException, IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(Constants.LOGOUT_URL, "{}");
        JSONObject responseJson = new JSONObject(response.body());

        System.out.println(responseJson);
    }

    public JSONArray listDevices() throws NoSuchAlgorithmException, IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(Constants.DEV_LIST, "{}");
        JSONObject responseJson = new JSONObject(response.body());

        System.out.println(responseJson);
        return responseJson.getJSONArray("data");
    }

    public HttpResponse<String> sendRequest(String endpoint, String credentials) throws IOException, InterruptedException, NoSuchAlgorithmException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(Constants.HTTP_API + endpoint));
        System.out.println(token);
        requestBuilder.setHeader("Authorization", "Basic " + token);
        requestBuilder.setHeader("Content-Type", "application/json");
        requestBuilder.setHeader("vender", "Meross");
        requestBuilder.setHeader("AppVersion", "1.3.0");
        requestBuilder.setHeader("AppLanguage", "FR");
        requestBuilder.setHeader("User-Agent", "okhttp/3.6.0");

        String b64Params = Base64.getEncoder().encodeToString(credentials.getBytes());
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
    }

    public String getMD5(String params, String timestamp, String nonce) throws NoSuchAlgorithmException {
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
