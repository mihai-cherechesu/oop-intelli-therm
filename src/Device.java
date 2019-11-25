import java.util.SortedMap;
import java.util.TreeMap;

public class Device {
    private final String id;
    private SortedMap<Long, Double> records;

    public Device(String id) {
        this.id = id;
        this.records = new TreeMap<>();
    }

    public String getId() {
        return id;
    }

    public SortedMap<Long, Double> getRecords() {
        return records;
    }
}
