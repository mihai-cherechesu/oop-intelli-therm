import java.io.IOException;
import java.util.*;

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

    public Double minimum(SortedMap<Long, Double> map) {
        Double minimum = map.get(map.firstKey());

        for (Long key : map.keySet()) {
            if (map.get(key) <= minimum) minimum = map.get(key);
        }
        return minimum;
    }

    public Double maximum(SortedMap<Long,Double> map) {
        Double maximum = map.get(map.firstKey());

        for (Long key : map.keySet()) {
            if (map.get(key) >= maximum) maximum = map.get(key);
        }
        return maximum;
    }

    public void observe(String[] tokens) {
        String id = tokens[StandardTokenIO.getDevice()];

        long hours = StandardTokenIO.getIntervals() * StandardTokenIO.getHour();
        long timestamp = Long.parseLong(tokens[StandardTokenIO.getTimestamp()]);

        if (timestamp > HeatSystem.getTimestamp()) return;
        if (timestamp < HeatSystem.getTimestamp() - hours) return;

        Room room = rooms.get(id);
        Device device = room.getDevice();
        String function = tokens[StandardTokenIO.getFunction()];

        if (function.equals(StandardFunctions.getHumidity())) {
            double humidity = Double.parseDouble
                    (tokens[StandardTokenIO.getHumidity() + 1]);
            device.getHumidityRecords().put(timestamp, humidity);

        } else if (function.equals(StandardFunctions.getObserve())) {
            double temperature = Double.parseDouble
                    (tokens[StandardTokenIO.getTemperature()]);
            device.getRecords().put(timestamp, temperature);
        }

        device.frame(timestamp, tokens);
    }

    public void trigger(String[] tokens) throws IOException {

        int surface = 0;
        double weightedMean = (double) 0;
        double weightedHumidity = (double) 0;

        for (String key : rooms.keySet()) {
            surface += rooms.get(key).getSurface();
        }

        Map<String, Room> rooms = HeatSystem.rooms;             // Shadowing the rooms.

        if (globalHumidity != 0) {
            for (String key : rooms.keySet()) {

                Room room = rooms.get(key);
                Device device = room.getDevice();
                Long lastKey = device.getHumiditySeries().lastKey();
                Long firstKey = device.getHumiditySeries().firstKey();

                while (device.getHumiditySeries().get(lastKey) == null && lastKey != firstKey) {
                    lastKey -= StandardTokenIO.getHour();
                    continue;
                }

                weightedHumidity += (minimum(device.getHumiditySeries().get(lastKey)) * room.getSurface());
            }
            weightedHumidity /= surface;
        }

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

        if (weightedHumidity < globalHumidity) {
            DataLoader.getBufferedWriter().write("NO" + "\n");
            return;
        }

        if (weightedMean <= globalTemperature) {
            DataLoader.getBufferedWriter().write("YES" + "\n");
        } else {
            DataLoader.getBufferedWriter().write("NO" + "\n");
        }
    }

    public void temperature(String[] tokens) {
        globalTemperature = Double.parseDouble(tokens[StandardTokenIO.getGlobalTemperature()]);
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
