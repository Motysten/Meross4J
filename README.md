# Meross4J Library

Welcome to the Java library for controlling Meross devices. This library is inspired by the Python [MerossIot](https://github.com/albertogeniola/MerossIot) library developed by Alberto Geniola. It allows you to interact with several Meross devices, specifically models MSS110, MSS210, MSS310, and MSS310H.

**/!\\ This library supports only EU servers of Meross for now. This should be resolved in the next version. /!\\**

## Features

- Control Meross devices (MSS110, MSS210, MSS310, MSS310H) through a Java interface.
- Support for basic functionalities like turning devices on and off.
- Simplified interface for easy integration into Java projects.

## Installation

To use this library in your Java project, add it as a dependency. Ensure that you have a dependency manager like Maven or Gradle configured in your project.

### Maven

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>fr.motysten</groupId>
    <artifactId>meross4j</artifactId>
    <version>0.1-DEV</version>
</dependency>
```

### Gradle

Add this line to your `build.gradle` file:

```gradle
implementation 'fr.motysten:meross4j:0.1-DEV'
```

## Configuration

To initiate the connection to Meross servers, you need only two objects :

```java
import fr.motysten.meross4j.HttpApi;
import fr.motysten.meross4j.mqtt.MQTTClient;

public class Main() {
    public static void main(String[] args) {
        // Communication with Meross HTTP api :
        // - Login / Logout
        // - Devices listing
        HttpApi api = new HttpApi("email", "password");

        //Communication with Meross MQTT server :
        // - Control devices
        // - Gather devices infos
        MQTTClient client = new MQTTClient(api);
    }
}

```

## Usage

### Example Code

Here’s a basic example of how to turn on all Meross plugs :

```java
import fr.motysten.meross4j.HttpApi;
import fr.motysten.meross4j.devices.Plug;
import fr.motysten.meross4j.mqtt.MQTTClient;

public class Main() {
    public static void main(String[] args) {
        HttpApi api = new HttpApi("email", "password");
        MQTTClient client = new MQTTClient(api);
        
        api.listDevices(client);

        for (Object device : client.devices.values()) {
            if (device instanceof Plug plug) {
                plug.togglePower(true);
            }
        }
        
        // Don't forget to destroy the connection at the end of your code
        // Meross could block you for like 12-24 hours if you generate too many tokens without destroying them at the end !
        api.destroy(client);
    }
}
```

### Managing devices

You can also access devices information like power consumption (only for MSS310 | MSS310H devices):

```java
import fr.motysten.meross4j.HttpApi;
import fr.motysten.meross4j.mqtt.MQTTClient;

public class Main() {
    public static void main(String[] args) {
        HttpApi api = new HttpApi("email", "password");
        MQTTClient client = new MQTTClient(api);
        
        api.listDevices(client);
        
        if (client.devices.get("deviceId") instanceof Plug plug) {
            plug.sync();
            System.out.println("Current power consumption : " + plug.getPowerUsage() + "W");
        }
        
        api.destroy(client);
    }
}
```

## Supported Devices

Currently, the library supports the following devices:

- MSS110
- MSS210
- MSS310
- MSS310H

Future updates may include support for additional Meross devices.

## Contribution

Contributions are welcome ! If you want to add features or fix bugs, feel free to fork the repository and submit a pull request.

## License

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Thank you for using this Java library to control your Meross devices. If you have any questions or issues, feel free to open an issue on the GitHub repository.

---

## Author

Developed by [Motysten](https://github.com/Motysten). Inspired by the Python library developed by Alberto Geniola.
