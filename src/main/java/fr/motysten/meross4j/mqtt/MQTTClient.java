package fr.motysten.meross4j.mqtt;

import fr.motysten.meross4j.HttpApi;
import fr.motysten.meross4j.devices.BaseDevice;
import fr.motysten.meross4j.devices.Plug;
import fr.motysten.meross4j.models.Namespace;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MQTTClient {

    private final MqttClient client;
    private final String appId;

    public HashMap<String, BaseDevice> devices = new HashMap<>();

    public MQTTClient(HttpApi api) throws MqttException, NoSuchAlgorithmException {
        appId = Utils.generateAppId();
        String clientId = "app:" + appId;
        client = new MqttClient(
                "ssl://" + api.getMqttDomain() + ":443",
                clientId
        );

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(api.getUserId());
        options.setPassword(Utils.generatePass(api.getUserId(), api.getKey()).toCharArray());
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        options.setKeepAliveInterval(30);

        client.connect(options);

        String[] topicFilter = {Utils.buildClientUserTopic(api.getUserId()), Utils.buildClientResponseTopic(api.getUserId(), appId)};
        int[] topicQos = {1, 1};
        client.subscribe(topicFilter, topicQos);

        initCallback();
    }

    public void initCallback() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Connection to broker lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                if (topic.contains("-")) {
                    JSONObject header = new JSONObject(mqttMessage.toString()).getJSONObject("header");
                    JSONObject payload = new JSONObject(mqttMessage.toString()).getJSONObject("payload");
                    if (devices.get(header.getString("uuid")) instanceof Plug device) {
                        if (header.getString("namespace").equalsIgnoreCase(Namespace.CONTROL_ELECTRICITY) && device.isPowerCapable()) {
                            device.setPowerUsage(payload.getJSONObject("electricity").getFloat("power") / 1000);
                        } else if (header.getString("namespace").equalsIgnoreCase(Namespace.SYSTEM_ALL)) {
                            try {
                                device.setOn(payload.getJSONObject("all").getJSONObject("digest").getJSONArray("togglex").getJSONObject(0).getInt("onoff") == 1);

                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                        }
//                        if (device.isPowerCapable()) {
//                            device.setPowerUsage(payload.getJSONObject("electricity").getFloat("power") / 1000);
//                        }
                        System.out.println(mqttMessage);
                        device.latch.countDown();
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public MqttClient getClient() {return client;}

    public String getAppId() {return appId;}

}
