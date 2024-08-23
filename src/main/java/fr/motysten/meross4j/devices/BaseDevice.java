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

public class BaseDevice {
    public void toggleX(boolean on, String id, HttpApi api, MQTTClient client) {
        JSONObject toggleX = new JSONObject();
        if (on) {
            toggleX.put("channel", 1);
        } else {
            toggleX.put("channel", 0);
        }

        JSONObject payload = new JSONObject();
        payload.put("togglex", toggleX);

        try {
            HashMap<String, String> messageInfos = Utils.buildMessage("SET", Namespace.CONTROL_TOGGLEX, payload, id, api, client);
            client.getClient().publish(Utils.buildDeviceRequestTopic(id), new MqttMessage(messageInfos.get("message").getBytes()));
        } catch (NoSuchAlgorithmException | MqttException e) {
            System.err.println(e.getMessage());
        }
    }
}
