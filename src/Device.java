import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Device {

    private final String id;
    private SortedMap<Long, Double> records;
    private Map<Long, SortedMap<Long, Double>> series;

    public Device(String id) {
        this.id = id;
        this.records = new TreeMap<>();
        this.series = new HashMap<>();

        for (int interval = 1; interval <= StandardTokenIO.getIntervals(); interval++) {

            this.series.put(HeatSystem.getTimestamp() -
                            interval * StandardTokenIO.getHour(), null);
        }
    }

    public void frame(long timestamp) {
        long minimum = 0, closest = 0;
        long lowerBound = 0, upperBound = 0;
        SortedMap<Long, Double> subMap = null;

        for (Long key: this.getSeries().keySet()) {
            closest = timestamp - key;

            if (closest <= minimum && closest >= 0) {
                minimum = closest;
                lowerBound = key;
            }
        }
        upperBound = lowerBound + StandardTokenIO.getHour();
        subMap = this.records.subMap(lowerBound, upperBound);

        this.series.put(lowerBound, subMap);
    }

    public Map<Long, SortedMap<Long, Double>> getSeries() {
        return series;
    }

    public String getId() {
        return id;
    }

    public SortedMap<Long, Double> getRecords() {
        return records;
    }
}
