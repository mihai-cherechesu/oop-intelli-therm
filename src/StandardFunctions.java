public class StandardFunctions {
    private final static String observe;
    private final static String trigger;
    private final static String temperature;
    private final static String list;
    private final static String humidity;

    static {
        observe = "OBSERVE";
        trigger = "TRIGGER";
        temperature = "TEMPERATURE";
        list = "LIST";
        humidity = "HUMIDITY";
    }

    public static String getObserve() {
        return observe;
    }

    public static String getTrigger() {
        return trigger;
    }

    public static String getTemperature() {
        return temperature;
    }

    public static String getList() {
        return list;
    }

    public static String getHumidity() {
        return humidity;
    }
}
