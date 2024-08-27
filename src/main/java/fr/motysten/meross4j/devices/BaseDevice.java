package fr.motysten.meross4j.devices;

import fr.motysten.meross4j.HttpApi;
import fr.motysten.meross4j.models.Namespace;
import fr.motysten.meross4j.mqtt.MQTTClient;
import fr.motysten.meross4j.mqtt.Utils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Base Meross IoT Device
 */
public class BaseDevice {
    /**
     * Enable or disable a meross device using MQTT api
     * @param on        Boolean that determine if the device need to be on (true) or off (false)
     * @param id        The ID of the device you wish to control
     * @param api       The instance of your HTTP connection
     * @param client    The instance of your MQTT connection
     */
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
            assert messageInfos != null;
            client.getClient().publish(Utils.buildDeviceRequestTopic(id), new MqttMessage(messageInfos.get("message").getBytes()));
        } catch (MqttException e) {
            System.err.println(e.getMessage());
        }
    }
}
