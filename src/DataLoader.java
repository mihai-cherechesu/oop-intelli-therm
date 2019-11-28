import java.io.*;
import java.util.Map;
import java.util.SortedMap;

/**
 * Class that reads the input, loads the objects and
 * runs the functions for the heap.
 */
public class DataLoader {

    /***
     * Attributes of DataLoader class.
     */
    private static final String inputFileName = "therm.in";
    private static final String outputFileName = "therm.out";
    private static File inputFile;
    private static File outputFile;
    private static String currentLine;
    private static String []tokens;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static boolean firstLineIO;

    /***
     * Non-static fields constructor without parameters.
     */
    private DataLoader() {}

    /***
     * Static fields initialization block which opens the file in question.
     */
    static {
        inputFile = new File(inputFileName);
        outputFile = new File(outputFileName);
        firstLineIO = true;
        currentLine = "";

        try { bufferedReader = new BufferedReader(new FileReader(inputFile));
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        try { bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) { e.printStackTrace(); }
    }

    /***
     * Getters and setters for non-static fields.
     */
    public static void setFirstLineIO() { firstLineIO = false; }
    public static boolean isFirstLineIO() { return firstLineIO; }
    public static String[] getTokens() { return tokens; }

    /***
     * Data loader method.
     * @param tokens is an array of strings, which represent the input nibbles.
     * @return true or false
     */
    private boolean loader(String []tokens) {

        String name = tokens[StandardTokenIO.getRoom()];
        String id   = tokens[StandardTokenIO.getDevice()];
        int surface = Integer.parseInt(tokens[StandardTokenIO.getSurface()]);

        HeatSystem.configureRoom(name, id, surface);
        HeatSystem.setRoomCount(HeatSystem.getRoomCount() - 1);
        return true;
    }


    /***
     * Calls the functions related to the thermostat.
     * @param tokens is an array of strings, which represent the input nibbles.
     * @return true or false
     * @throws IOException
     */


    private boolean runner(String[] tokens, HeatSystem system) throws IOException {

        String function = tokens[StandardTokenIO.getFunction()];

        if (function.equals(StandardFunctions.getObserve())) {
            system.observe(tokens);

        } else if (function.equals(StandardFunctions.getHumidity())) {
            system.observeHumidity(tokens);

        } else if (function.equals(StandardFunctions.getList())) {
            system.list(tokens);

        } else if (function.equals(StandardFunctions.getTemperature())) {
            system.temperature(tokens);

        } else if (function.equals(StandardFunctions.getTrigger())) {
            system.trigger(tokens);
        }

        return true;
    }

    /***
     * Main class for input fetching and class' instance loading.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        HeatSystem system = new HeatSystem();
        DataLoader loader = new DataLoader();
        boolean bool; // Auxiliary boolean for ternary operator

        try {
            currentLine = bufferedReader.readLine();                      // Read line
            tokens = currentLine.split(" ", StandardTokenIO.getTokens()); // Split line

            int roomCount = Integer.parseInt(tokens[StandardTokenIO.getRoom()]);
            double global = Double.parseDouble(tokens[StandardTokenIO.getGlobal()]);
            long timestamp = Long.parseLong(tokens[StandardTokenIO.getTimestamp()]);

            HeatSystem.setRoomCount(roomCount);
            HeatSystem.setGlobal(global);
            HeatSystem.setTimestamp(timestamp);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while ((currentLine = bufferedReader.readLine()) != null) {

                // Split the input line into tokens.
                tokens = currentLine.split(" ", StandardTokenIO.getTokens());

                bool = (HeatSystem.getRoomCount() != 0) ?
                       (loader.loader(tokens)) :        // Load objects
                       (loader.runner(tokens, system)); // Run function upon objects
            }

        } catch (IOException e) {
            e.printStackTrace();
        }




        int x = 0;
        for (long ts : HeatSystem.getRooms().get("XUID5NZ").getDevice().getSeries().keySet()) {
            if (HeatSystem.getRooms().get("XUID5NZ").getDevice().getSeries().get(ts) != null) {
                Map<Long, SortedMap<Long, Double>> sr = HeatSystem.getRooms().get("XUID5NZ").getDevice().getSeries();

                for (long rts : sr.get(ts).keySet()) {
                    System.out.println(HeatSystem.convert(rts) + " : " + sr.get(ts).get(rts));
                }
//                x += HeatSystem.getRooms().get("XUID5NZ").getDevice().getSeries().get(ts).values().size();
            }
        }
        System.out.println(x);

        DataLoader.bufferedReader.close();
        DataLoader.bufferedWriter.close();
    }
}

