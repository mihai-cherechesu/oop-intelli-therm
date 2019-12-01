import java.io.IOException;
import java.util.*;

/**
 * Class that comprises the global static variables
 * and the core functions.
 */
public class HeatSystem {
    private static int roomCount;
    private static double globalTemperature;
    private static double globalHumidity;
    private static long timestamp;

    private static Map<String, Room> rooms;

    public HeatSystem() {}

    static {
        roomCount = 0;
        globalTemperature = 0;
        globalHumidity = 0;
        timestamp = 0;
        rooms = new HashMap<>();
    }

    /**
     * Used to find the lowest temperature in a time series.
     * @param map
     * @return the minimum double from a map
     */
    public Double minimum(SortedMap<Long, Double> map) {
        Double minimum = map.get(map.firstKey());

        for (Long key : map.keySet()) {
            if (map.get(key) <= minimum) minimum = map.get(key);
        }
        return minimum;
    }

    /**
     * Used to find the highest humidity in a time series.
     * @param map
     * @return the maximum double from a map
     */
    public Double maximum(SortedMap<Long,Double> map) {
        Double maximum = map.get(map.firstKey());

        for (Long key : map.keySet()) {
            if (map.get(key) >= maximum) maximum = map.get(key);
        }
        return maximum;
    }

    /**
     * OBSERVE and OBSERVEH functions.
     * @param tokens
     */
    public void observe(String[] tokens) {
        String id = tokens[StandardTokenIO.getDevice()];

        long hours = StandardTokenIO.getIntervals() * StandardTokenIO.getHour(); // 24 hours
        long timestamp = Long.parseLong(tokens[StandardTokenIO.getTimestamp()]); // Current timestamp

        if (timestamp > HeatSystem.getTimestamp()) return;         // Bigger than reference timestamp
        if (timestamp < HeatSystem.getTimestamp() - hours) return; // Lower than reference timestamp

        Room room = rooms.get(id);                                 // Identifies the room by device ID.
        Device device = room.getDevice();
        String function = tokens[StandardTokenIO.getFunction()];

        if (function.equals(StandardFunctions.getHumidity())) {       // Observe humidity.
            double humidity = Double.parseDouble
                    (tokens[StandardTokenIO.getHumidity() + 1]);
            device.getHumidityRecords().put(timestamp, humidity);     // Store it in the records collector.

        } else if (function.equals(StandardFunctions.getObserve())) { // Observe temperature.
            double temperature = Double.parseDouble
                    (tokens[StandardTokenIO.getTemperature()]);
            device.getRecords().put(timestamp, temperature);          // Store it in the records collector.
        }

        device.frame(timestamp, tokens);
    }

    /**
     * TRIGGER function
     * @param tokens
     * @throws IOException
     */
    public void trigger(String[] tokens) throws IOException {

        int surface = 0;
        double weightedMean = (double) 0;
        double weightedHumidity = (double) 0;

        for (String key : rooms.keySet()) {
            surface += rooms.get(key).getSurface();             // Tallies up the total surface.
        }

        Map<String, Room> rooms = HeatSystem.rooms;             // Shadowing the rooms.


        if (globalHumidity != 0) {                              // Only when we have global humidity,
            for (String key : rooms.keySet()) {                 // otherwise it is redundant.

                Room room = rooms.get(key);
                Device device = room.getDevice();
                Long lastKey = device.getHumiditySeries().lastKey();    // First key within the map.
                Long firstKey = device.getHumiditySeries().firstKey();  // Last key within the map.

                // Iterate until it gets the last key that references an object (not null).
                while (device.getHumiditySeries().get(lastKey) == null && lastKey != firstKey) {
                    lastKey -= StandardTokenIO.getHour();
                }

                // Calculates the weighted humidity mean.
                weightedHumidity += (maximum(device.getHumiditySeries().get(lastKey)) * room.getSurface());
            }
            weightedHumidity /= surface;
        }

        // Same as the humidity weighted mean, but for temperature.
        for (String key : rooms.keySet()) {

            Room room = rooms.get(key);
            Device device = room.getDevice();
            Long lastKey = device.getSeries().lastKey();
            Long firstKey = device.getSeries().firstKey();

            while (device.getSeries().get(lastKey) == null && lastKey != firstKey) {
                lastKey -= StandardTokenIO.getHour();
                continue;
            }

            weightedMean += (minimum(device.getSeries().get(lastKey)) * room.getSurface());
        }
        weightedMean /= surface;

        if (weightedHumidity > globalHumidity) {                // Mean humidity is above the maximum level.
            DataLoader.getBufferedWriter().write("NO" + "\n");  // Cannot open the heat system.
            return;
        }

        if (weightedMean < globalTemperature) {                 // Mean temperature is under the desired level.
            DataLoader.getBufferedWriter().write("YES" + "\n"); // Open the heat system.
        } else {
            DataLoader.getBufferedWriter().write("NO" + "\n");
        }
    }

    /**
     * TEMPERATURE function
     * @param tokens
     */
    public void temperature(String[] tokens) {
        globalTemperature = Double.parseDouble(tokens[StandardTokenIO.getGlobalTemperature()]);
    }

    /**
     * LIST ROOM* function
     * @param tokens
     * @throws IOException
     */
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

    /**
     * Instantiates a Room and loads it into the rooms HashMap.
     * @param name
     * @param id
     * @param surface
     */
    public static void configureRoom(String name, String id, int surface) {
        rooms.put(id, new Room(name, id, surface));
    }

    public static void setGlobalHumidity(double globalHumidity) {
        HeatSystem.globalHumidity = globalHumidity;
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

    public static void setGlobalTemperature(double globalTemperature) {
        HeatSystem.globalTemperature = globalTemperature;
    }

    public static long getTimestamp() {
        return timestamp;
    }

    public static void setTimestamp(long timestamp) {
        HeatSystem.timestamp = timestamp;
    }
}
