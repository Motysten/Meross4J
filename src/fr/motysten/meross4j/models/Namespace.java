package fr.motysten.meross4j.models;

public class Namespace {

    // Common abilities
public static final String SYSTEM_ALL = "Appliance.System.All";
public static final String SYSTEM_ABILITY = "Appliance.System.Ability";
public static final String SYSTEM_ONLINE = "Appliance.System.Online";
public static final String SYSTEM_REPORT = "Appliance.System.Report";
public static final String SYSTEM_DEBUG = "Appliance.System.Debug";
public static final String SYSTEM_RUNTIME = "Appliance.System.Runtime";

public static final String SYSTEM_ENCRYPTION = "Appliance.Encrypt.Suite";
public static final String SYSTEM_ENCRYPTION_ECDHE = "Appliance.Encrypt.ECDHE";

public static final String CONTROL_BIND = "Appliance.Control.Bind";
public static final String CONTROL_UNBIND = "Appliance.Control.Unbind";
public static final String CONTROL_TRIGGER = "Appliance.Control.Trigger";
public static final String CONTROL_TRIGGERX = "Appliance.Control.TriggerX";

public static final String CONFIG_WIFI_LIST = "Appliance.Config.WifiList";
public static final String CONFIG_TRACE = "Appliance.Config.Trace";

public static final String SYSTEM_DND_MODE = "Appliance.System.DNDMode";

    // Power plug/bulbs abilities
public static final String CONTROL_TOGGLE = "Appliance.Control.Toggle";
public static final String CONTROL_TOGGLEX = "Appliance.Control.ToggleX";
public static final String CONTROL_ELECTRICITY = "Appliance.Control.Electricity";
public static final String CONTROL_CONSUMPTION = "Appliance.Control.Consumption";
public static final String CONTROL_CONSUMPTIONX = "Appliance.Control.ConsumptionX";

    // Bulbs-only abilities
public static final String CONTROL_LIGHT = "Appliance.Control.Light";

    // Garage opener abilities
public static final String GARAGE_DOOR_STATE = "Appliance.GarageDoor.State";
public static final String GARAGE_DOOR_MULTIPLECONFIG = "Appliance.GarageDoor.MultipleConfig";

    // Roller shutter timer
public static final String ROLLER_SHUTTER_STATE = "Appliance.RollerShutter.State";
public static final String ROLLER_SHUTTER_POSITION = "Appliance.RollerShutter.Position";
public static final String ROLLER_SHUTTER_CONFIG = "Appliance.RollerShutter.Config";

    // Humidifier
public static final String CONTROL_SPRAY = "Appliance.Control.Spray";

public static final String SYSTEM_DIGEST_HUB = "Appliance.Digest.Hub";

    // Oil diffuser
public static final String DIFFUSER_LIGHT = "Appliance.Control.Diffuser.Light";
public static final String DIFFUSER_SPRAY = "Appliance.Control.Diffuser.Spray";

    // HUB
public static final String HUB_EXCEPTION = "Appliance.Hub.Exception";
public static final String HUB_BATTERY = "Appliance.Hub.Battery";
public static final String HUB_TOGGLEX = "Appliance.Hub.ToggleX";
public static final String HUB_ONLINE = "Appliance.Hub.Online";
public static final String HUB_SUBDEVICELIST = "Appliance.Hub.SubdeviceList";

    // SENSORS
public static final String HUB_SENSOR_ALL = "Appliance.Hub.Sensor.All";
public static final String HUB_SENSOR_TEMPHUM = "Appliance.Hub.Sensor.TempHum";
public static final String HUB_SENSOR_ALERT = "Appliance.Hub.Sensor.Alert";

    // MTS100
public static final String HUB_MTS100_ALL = "Appliance.Hub.Mts100.All";
public static final String HUB_MTS100_TEMPERATURE = "Appliance.Hub.Mts100.Temperature";
public static final String HUB_MTS100_MODE = "Appliance.Hub.Mts100.Mode";
public static final String HUB_MTS100_ADJUST = "Appliance.Hub.Mts100.Adjust";

    // Thermostat / MTS200
public static final String CONTROL_THERMOSTAT_MODE = "Appliance.Control.Thermostat.Mode";
public static final String CONTROL_THERMOSTAT_WINDOWOPENED = "Appliance.Control.Thermostat.WindowOpened";

}
