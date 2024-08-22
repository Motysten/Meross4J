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

public class Plug {

    private String id;
    private String name;
    private String version;
    private HttpApi api;
    private MQTTClient client;

    public Plug(String id, String name, String version, String deviceType) {
        if (!deviceType.equalsIgnoreCase("mss310")) {
            System.out.println("Not a plug !");
            return;
        }
        this.id = id;
        this.name = name;
        this.version = version;
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

}
