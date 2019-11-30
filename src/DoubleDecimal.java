import java.text.DecimalFormat;

public class DoubleDecimal {
    private static DecimalFormat format = new DecimalFormat("##.##");

    public static DecimalFormat getFormat() {
        return format;
    }
}
