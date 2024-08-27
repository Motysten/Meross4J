package fr.motysten.meross4j.devices;

import fr.motysten.meross4j.HttpApi;
import fr.motysten.meross4j.models.Namespace;
import fr.motysten.meross4j.mqtt.MQTTClient;
import fr.motysten.meross4j.mqtt.Utils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Light extends BaseDevice {
    public static final List<String> types = List.of("msl120");

    public static final int MODE_RGB = 1;
    public static final int MODE_TEMPERATURE = 2;
    public static final int MODE_LUMINANCE = 4;

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

    public void setColor(int r, int g, int b) {
        JSONObject lightPayload = new JSONObject();

        int rgb = r << 16 + g << 8 + b;

        lightPayload.put("rgb", rgb);
        lightPayload.put("capacity", Light.MODE_RGB);
        setLight(lightPayload);
    }

    public void setTemperature(int temperature) {
        JSONObject lightPayload = new JSONObject();

        lightPayload.put("temperature", temperature);
        lightPayload.put("capacity", Light.MODE_TEMPERATURE);
        setLight(lightPayload);
    }

    public void setLuminance(int luminance) {
        JSONObject lightPayload = new JSONObject();

        lightPayload.put("luminance", luminance);
        lightPayload.put("capacity", Light.MODE_LUMINANCE);
        setLight(lightPayload);
    }

    private void setLight(JSONObject lightPayload) {
        lightPayload.put("channel", 0);
        lightPayload.put("gradual", 0);

        JSONObject payload = new JSONObject();
        payload.put("light", lightPayload);

        try {
            HashMap<String, String> messageInfos = Utils.buildMessage("SET", Namespace.CONTROL_LIGHT, payload, this.id, this.api, this.client);
            assert messageInfos != null;
            client.getClient().publish(Utils.buildDeviceRequestTopic(this.id), new MqttMessage(messageInfos.get("message").getBytes()));
        } catch (MqttException e) {
            System.err.println(e.getMessage());
        }
    }
}
