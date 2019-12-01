import java.util.*;

/**
 * Device class: stores the time series of the
 * registered parameters (humidity and temperature)
 */
public class Device {

    private final String id;
    private SortedMap<Long, Double> records;                         // Temperature collector records.
    private SortedMap<Long, SortedMap<Long, Double>> series;         // Temperature 1h time series.
    private SortedMap<Long, Double> humidityRecords;                 // Humidity collector records.
    private SortedMap<Long, SortedMap<Long, Double>> humiditySeries; // Humidity 1h time series.

    public Device(String id) {
        this.id = id;
        this.records = new TreeMap<>();
        this.series = new TreeMap<>();
        this.humidityRecords = new TreeMap<>();
        this.humiditySeries = new TreeMap<>();

        for (int interval = 1; interval <= StandardTokenIO.getIntervals();      // Configure the
                               interval++) {                                    // 24 hours
            this.series.put(HeatSystem.getTimestamp() -                         // temperature buckets.
                            interval * StandardTokenIO.getHour(), null);
        }

        for (int interval = 1; interval <= StandardTokenIO.getIntervals();      // Configure the
                               interval++) {                                    // 24 hours
            this.humiditySeries.put(HeatSystem.getTimestamp() -                 // humidity buckets.
                                    interval * StandardTokenIO.getHour(), null);
        }
    }

    /**
     * Frames the current timestamp into the time buckets.
     * @param timestamp
     * @param tokens
     */
    public void frame(long timestamp, String[] tokens) {
        long minimum = timestamp;
        long closest = 0;
        long lowerBound = 0, upperBound = 0;
        SortedMap<Long, Double> subMap = null;
        String function = tokens[StandardTokenIO.getFunction()];

        // Identifies the lowest timestamp that bounds our current registered timestamp.
        for (Long key: this.getSeries().keySet()) {
            closest = timestamp - key;

            if (closest <= minimum && closest >= 0) {        // The lowest positive number
                minimum = closest;                           // is the lowest bound.
                lowerBound = key;
            }
        }
        upperBound = lowerBound + StandardTokenIO.getHour(); // Add one hour to get the upper bound.

        if (function.equals(StandardFunctions.getHumidity())) {
            subMap = this.humidityRecords.subMap(lowerBound, upperBound);                   // Sub-mapping the records

            SortedMap<Long, Double> sortedMap = new TreeMap<>(new ValueComparator(subMap)); // Compares by values in
            sortedMap.putAll(subMap);                                                       // ascending order.
            this.humiditySeries.put(lowerBound, sortedMap);                                 // Loads the sub-map into
                                                                                            // the HashMap at the
                                                                                            // timestamp-key accordingly.

        } else if (function.equals(StandardFunctions.getObserve())) {
            subMap = this.records.subMap(lowerBound, upperBound);                           // Same as above, but for
                                                                                            // temperature.
            SortedMap<Long, Double> sortedMap = new TreeMap<>(new ValueComparator(subMap));
            sortedMap.putAll(subMap);
            this.series.put(lowerBound, sortedMap);
        }
    }

    public SortedMap<Long, Double> getHumidityRecords() {
        return humidityRecords;
    }

    public SortedMap<Long, SortedMap<Long, Double>> getHumiditySeries() {
        return humiditySeries;
    }

    public void setHumiditySeries(SortedMap<Long, SortedMap<Long, Double>> humiditySeries) {
        this.humiditySeries = humiditySeries;
    }

    public SortedMap<Long, SortedMap<Long, Double>> getSeries() {
        return series;
    }

    public String getId() {
        return id;
    }

    public SortedMap<Long, Double> getRecords() {
        return records;
    }
}
