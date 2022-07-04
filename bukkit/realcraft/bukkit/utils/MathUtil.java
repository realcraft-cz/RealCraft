package realcraft.bukkit.utils;

public class MathUtil {

    public static double round(double amount, int places) {
        int factor = (int) Math.pow(10, places);
        return (double) Math.round((amount * factor)) / factor;
    }
}
