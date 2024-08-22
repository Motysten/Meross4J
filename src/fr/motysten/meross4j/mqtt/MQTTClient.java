package fr.motysten.meross4j.mqtt;

import fr.motysten.meross4j.HttpApi;
import org.eclipse.paho.client.mqttv3.*;

import java.security.NoSuchAlgorithmException;

public class MQTTClient {

    private final MqttClient client;
    private final String appId;

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
    }

    public void initCallback() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Connection to broker lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                System.out.println("Topic : " + topic);
                System.out.println("Message : " + mqttMessage);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public MqttClient getClient() {return client;}

    public String getAppId() {return appId;}

}
