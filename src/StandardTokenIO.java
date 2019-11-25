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

    }

    public static int getTokens() {
        return tokens;
    }

    public static int getRoom() {
        return room;
    }

    public static int getGlobal() {
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
