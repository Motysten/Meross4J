package fr.motysten.meross4j;

import fr.motysten.meross4j.devices.Plug;
import fr.motysten.meross4j.mqtt.MQTTClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

public class Main {

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

        api.listDevices(client);

        Plug plug = (Plug) client.devices.get("2303165514246051200348e1e9bfae29");
        plug.sync();
        System.out.println(plug.isOn());
        System.out.println("Current power consumption : " + plug.getPowerUsage() + "W");

        api.destroy(client);
    }

}
