public class Room {
    private final String name;
    private final Device device;
    private final int surface;

    public Room (String name, String id, int surface){
        this.name = name;
        this.device = new Device(id);
        this.surface = surface;
    }

    public String getName() {
        return name;
    }

    public Device getDevice() {
        return device;
    }

    public int getSurface() {
        return surface;
    }

    @Override
    public String toString() {
        return this.name + ": device "
             + this.device.getId() + " and "
             + this.surface + " sq.\n";
    }
}
