package fr.motysten.meross4j.models;

/**
 * Just constants
 */
public class Constants {

    /**
     * Constant SECRET. Just needed in signatures. Don't ask me why !
     */
    public static final String SECRET = "23x17ahWarFH6w29";

    /**
     * HTTP API link (EU Server only for now)
     */
    public static final String HTTP_API = "https://iotx-eu.meross.com";

    /**
     * HTTP Endpoint used to create credentials
     */
    public static final String LOGIN_URL = "/v1/Auth/signIn";

    /**
     * HTTP Endpoint that lists all devices
     */
    public static final String DEV_LIST = "/v1/Device/devList";

    /**
     * HTTP Endpoint used to destroy credentials after use
     */
    public static final String LOGOUT_URL = "/v1/Profile/logout";

}
