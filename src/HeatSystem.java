import java.util.HashSet;
import java.util.Set;

public class HeatSystem {
    private static int roomCount;
    private static double global;
    private static long timestamp;
    private static Set<Room> rooms;

    static {
        roomCount = 0;
        global = 0;
        timestamp = 0;
        rooms = new HashSet<>();
    }
    





    public static void configureRoom(String name, String id, int surface) {
        rooms.add(new Room(name, id, surface));
    }

    public static Set<Room> getRooms() {
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
