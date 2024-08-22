package fr.motysten.meross4j;

import fr.motysten.meross4j.devices.Plug;
import fr.motysten.meross4j.mqtt.MQTTClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Main {

    public static HashMap<String, Object> devices = new HashMap<>();

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InterruptedException, MqttException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("E-mail : ");
        String email = reader.readLine();

        System.out.println("Password : ");
        String password = reader.readLine();

        System.out.println();

        HttpApi api = new HttpApi(email, password);

        MQTTClient client = new MQTTClient(api);
        client.initCallback();

        JSONArray devicesList = api.listDevices();
        for (Object object : devicesList) {
            JSONObject device = (JSONObject) object;
            if (device.getString("deviceType").equalsIgnoreCase("mss310")) {
                devices.put(device.getString("uuid"), new Plug(device.getString("uuid"), device.getString("devName"), device.getString("fmwareVersion"), device.getString("deviceType"), api, client));
            }
            System.out.println("Device found : " + device.getString("devName") + " (" + device.getString("deviceType") + ")");
        }

        Plug plug = (Plug) devices.get("2303165514246051200348e1e9bfae29");
        plug.sync();
        System.out.println("Current power consumption : " + plug.getPowerUsage() + "W");

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                api.destroy();
                client.getClient().disconnect();
                System.out.println("API connection revoked !");
            } catch (InterruptedException | NoSuchAlgorithmException | IOException | MqttException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
