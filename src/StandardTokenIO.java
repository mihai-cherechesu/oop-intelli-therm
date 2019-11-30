public class StandardTokenIO {
    private final static int tokens;
    private final static int room;
    private final static int global;
    private final static int timestamp;
    private final static int function;
    private final static int device;
    private final static int surface;
    private final static int temperature;
    private final static int monitoring;
    private final static int start;
    private final static int end;
    private final static int intervals;
    private final static int hour;
    private final static int humidity;

    static {
        tokens = 4;
        room = 0;
        monitoring = 1;
        device = 1;
        global = 1;
        surface = 2;
        timestamp = 2;
        function = 0;
        temperature = 3;
        start = 2;
        end = 3;
        intervals = 24;
        hour = 3600;
        humidity = 2;
    }

    public static int getHumidity() { return humidity; }

    public static int getHour() { return hour; }

    public static int getIntervals() {
        return intervals;
    }

    public static int getTokens() {
        return tokens;
    }

    public static int getRoom() {
        return room;
    }

    public static int getGlobalTemperature() {
        return global;
    }

    public static int getTimestamp() {
        return timestamp;
    }

    public static int getFunction() {
        return function;
    }

    public static int getDevice() {
        return device;
    }

    public static int getSurface() {
        return surface;
    }

    public static int getTemperature() {
        return temperature;
    }

    public static int getMonitoring() {
        return monitoring;
    }

    public static int getStart() {
        return start;
    }

    public static int getEnd() {
        return end;
    }
}
