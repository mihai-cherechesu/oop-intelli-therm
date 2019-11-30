import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.*;

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

    public void list(String[] tokens) throws IOException {

        long lower = Long.parseLong(tokens[StandardTokenIO.getStart()]);
        long upper = Long.parseLong(tokens[StandardTokenIO.getEnd()]);

        String name = tokens[StandardTokenIO.getMonitoring()];
        Map<String, Room> rooms = HeatSystem.getRooms();                // Shadowing the static.
        SortedMap<Long, SortedMap<Long, Double>> reversed = new TreeMap<>(Comparator.reverseOrder());
        String output = "";

        for (String key : rooms.keySet()) {
            if (rooms.get(key).getName().equals(name)) {                // Found the room name.

                // Fetches the time series buckets in reversed order and the
                // entries within the buckets in ascending order.
                reversed.putAll(rooms.get(key).getDevice().getSeries().subMap(lower, upper));

                for (Long bucket : reversed.keySet()) {
                    for (Long entry : reversed.get(bucket).keySet()) {

                        Double temperature = reversed.get(bucket).get(entry);
                        output +=  String.format("%.2f", temperature) + " ";

                    }
                }

                // Write the room name into the output file.
                DataLoader.getBufferedWriter().write(rooms.get(key).getName() + " ");
                DataLoader.getBufferedWriter().write(output.trim() + "\n");
                break;
            }
        }
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
