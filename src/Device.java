import java.util.*;

public class Device {

    private final String id;
    private SortedMap<Long, Double> records;
    private SortedMap<Long, SortedMap<Long, Double>> series;
    private SortedMap<Long, Double> humidityRecords;
    private SortedMap<Long, SortedMap<Long, Double>> humiditySeries;

    public Device(String id) {
        this.id = id;
        this.records = new TreeMap<>();
        this.series = new TreeMap<>();
        this.humidityRecords = new TreeMap<>();
        this.humiditySeries = new TreeMap<>();

        for (int interval = 1; interval <= StandardTokenIO.getIntervals();
                               interval++) {
            this.series.put(HeatSystem.getTimestamp() -
                            interval * StandardTokenIO.getHour(), null);
        }

        for (int interval = 1; interval <= StandardTokenIO.getIntervals();
                               interval++) {
            this.humiditySeries.put(HeatSystem.getTimestamp() -
                                    interval * StandardTokenIO.getHour(), null);
        }
    }

    public void frame(long timestamp, String[] tokens) {
        long minimum = timestamp;
        long closest = 0;
        long lowerBound = 0, upperBound = 0;
        SortedMap<Long, Double> subMap = null;
        String function = tokens[StandardTokenIO.getFunction()];

        for (Long key: this.getSeries().keySet()) {
            closest = timestamp - key;

            if (closest <= minimum && closest >= 0) {
                minimum = closest;
                lowerBound = key;
            }
        }
        upperBound = lowerBound + StandardTokenIO.getHour();

        if (function.equals(StandardFunctions.getHumidity())) {
            subMap = this.humidityRecords.subMap(lowerBound, upperBound);

            SortedMap<Long, Double> sortedMap = new TreeMap<>(new ValueComparator(subMap));
            sortedMap.putAll(subMap);
            this.humiditySeries.put(lowerBound, sortedMap);

        } else if (function.equals(StandardFunctions.getObserve())) {
            subMap = this.records.subMap(lowerBound, upperBound);

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
