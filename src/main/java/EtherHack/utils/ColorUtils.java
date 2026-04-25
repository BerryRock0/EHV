package EtherHack.utils;

import zombie.core.Color;

public class ColorUtils {
    public static Color stringToColor(String str) {
        String[] rgb = str.split(",");
        return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    }

    public static String colorToString(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
}
