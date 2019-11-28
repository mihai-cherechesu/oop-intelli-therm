import java.util.HashMap;
import java.util.Map;

public class HeatSystem {
    private static int roomCount;
    private static double global;
    private static long timestamp;
    private static Map<String, Room> rooms;

    public HeatSystem() {}

    static {
        roomCount = 0;
        global = 0;
        timestamp = 0;
        rooms = new HashMap<>();
    }

    public static long convert(long timestamp) {
        long lowerBound = HeatSystem.timestamp - 3600 * 24;
        return ((timestamp - lowerBound) / 3600);
    }

    public void observe(String[] tokens) {
        String id = tokens[StandardTokenIO.getDevice()];

        long hours = StandardTokenIO.getIntervals() * StandardTokenIO.getHour();
        long timestamp = Long.parseLong
                   (tokens[StandardTokenIO.getTimestamp()]);
        double temperature = Double.parseDouble
                   (tokens[StandardTokenIO.getTemperature()]);

        if (timestamp > HeatSystem.getTimestamp()) return;
        if (timestamp < HeatSystem.getTimestamp() - hours) return;

        Room room = rooms.get(id);
        Device device = room.getDevice();

        device.getRecords().put(timestamp, temperature);
        device.frame(timestamp);
    }

    public void trigger(String[] tokens) {

    }

    public void temperature(String[] tokens) {

    }

    public void list(String[] tokens) {

    }

    public void observeHumidity(String[] tokens) {

    }

    public static void configureRoom(String name, String id, int surface) {
        rooms.put(id, new Room(name, id, surface));
    }

    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static void setRoomCount(int roomCount) {
        HeatSystem.roomCount = roomCount;
    }

    public static int getRoomCount() {
        return roomCount;
    }

    public static double getGlobal() {
        return global;
    }

    public static void setGlobal(double global) {
        HeatSystem.global = global;
    }

    public static long getTimestamp() {
        return timestamp;
    }

    public static void setTimestamp(long timestamp) {
        HeatSystem.timestamp = timestamp;
    }
}
