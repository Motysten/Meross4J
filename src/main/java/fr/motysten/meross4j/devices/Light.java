package fr.motysten.meross4j.devices;

import fr.motysten.meross4j.HttpApi;
import fr.motysten.meross4j.models.Namespace;
import fr.motysten.meross4j.mqtt.MQTTClient;
import fr.motysten.meross4j.mqtt.Utils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Light extends BaseDevice {
    public static final List<String> types = List.of("msl120");

    private String id;
    private String name;
    private String version;
    private String deviceType;
    private HttpApi api;
    private MQTTClient client;

    private float powerUsage;

    public CountDownLatch latch;

    public Light(String id, String name, String version, String deviceType, HttpApi api, MQTTClient client) {
        if (!types.contains(deviceType)) {
            System.out.println("Not a light !");
            return;
        }
        this.id = id;
        this.name = name;
        this.version = version;
        this.deviceType = deviceType;
        this.api = api;
        this.client = client;
    }

    public void togglePower(boolean on) {
        super.toggleX(on, this.id, this.api, this.client);
    }
}
