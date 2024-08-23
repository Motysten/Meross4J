package fr.motysten.meross4j.devices;

import fr.motysten.meross4j.HttpApi;
import fr.motysten.meross4j.models.Namespace;
import fr.motysten.meross4j.mqtt.MQTTClient;
import fr.motysten.meross4j.mqtt.Utils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Plug {

    public static final List<String> types = Arrays.asList("mss110", "mss210", "mss310", "mss310h");
    public static final List<String> typesWithPowerMeasure = Arrays.asList("mss310", "mss310h");

    private String id;
    private String name;
    private String version;
    private String deviceType;
    private HttpApi api;
    private MQTTClient client;

    private float powerUsage;

    public CountDownLatch latch;

    public Plug(String id, String name, String version, String deviceType, HttpApi api, MQTTClient client) {
        if (!types.contains(deviceType)) {
            System.out.println("Not a plug !");
            return;
        }
        this.id = id;
        this.name = name;
        this.version = version;
        this.deviceType = deviceType;
        this.api = api;
        this.client = client;
    }

    public void sync() {
        JSONObject payload = new JSONObject();

        try{
            latch = new CountDownLatch(1);

            HashMap<String, String> messageInfos = Utils.buildMessage("GET", Namespace.CONTROL_ELECTRICITY, payload, this.id, this.api, this.client);
            client.getClient().publish(Utils.buildDeviceRequestTopic(this.id), new MqttMessage(messageInfos.get("message").getBytes()));

            latch.await();
        } catch (NoSuchAlgorithmException | MqttException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean isPowerCapable() {
        return typesWithPowerMeasure.contains(this.deviceType);
    }

    public float getPowerUsage() {
        if (!isPowerCapable()) {
            System.err.println("This plug is not able to measure power usage !");
        }
        return this.powerUsage;
    }

    public void setPowerUsage(float powerUsage) {
        if (isPowerCapable()) {
            this.powerUsage = powerUsage;
        } else {
            this.powerUsage = 0;
        }
    }

    public boolean togglePower(boolean on) {
        JSONObject toggleX = new JSONObject();
        if (on) {
            toggleX.put("channel", 1);
        } else {
            toggleX.put("channel", 0);
        }

        JSONObject payload = new JSONObject();
        payload.put("togglex", toggleX);

        try {
            HashMap<String, String> messageInfos = Utils.buildMessage("SET", Namespace.CONTROL_TOGGLEX, payload, this.id, this.api, this.client);
            client.getClient().publish(Utils.buildDeviceRequestTopic(this.id), new MqttMessage(messageInfos.get("message").getBytes()));
            return true;
        } catch (NoSuchAlgorithmException | MqttException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public String getId() {
        return this.id;
    }

}
