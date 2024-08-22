package fr.motysten.meross4j;

import fr.motysten.meross4j.models.Namespace;
import fr.motysten.meross4j.mqtt.MQTTClient;
import fr.motysten.meross4j.mqtt.Utils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Main {

    public static String mqttPass;

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InterruptedException, MqttException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("E-mail : ");
        String email = reader.readLine();

        System.out.println("Password : ");
        String password = reader.readLine();

        HttpApi api = new HttpApi(email, password);

        JSONArray devices = api.listDevices();
        for (Object object : devices) {
            JSONObject device = (JSONObject) object;
            System.out.println("Device found : " + device.getString("devName") + " (" + device.getString("deviceType") + ")");
        }

        //mqttPass = Utils.generatePass("386602", "411ecdc64a856c84d6ef1401046a07bd");
        // api.destroy();

        MQTTClient client = new MQTTClient(api);
        client.initCallback();

        System.out.println(client.getClient().isConnected());

        JSONObject togglex = new JSONObject();
        togglex.put("channel", 0);

        JSONObject payload = new JSONObject();
        payload.put("togglex", togglex);

        HashMap<String, String> messageInfos = Utils.buildMessage("GET", Namespace.CONTROL_ELECTRICITY, payload, devices.getJSONObject(0).getString("uuid"), api, client);

        System.out.println("Sending message to topic : " + new MqttMessage(messageInfos.get("message").getBytes()));
        client.getClient().publish(Utils.buildDeviceRequestTopic(devices.getJSONObject(0).getString("uuid")), new MqttMessage(messageInfos.get("message").getBytes()));
        System.out.println("Published !");

        System.out.println();
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                api.destroy();
            } catch (InterruptedException | NoSuchAlgorithmException | IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
