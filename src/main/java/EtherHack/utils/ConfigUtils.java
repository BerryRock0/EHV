package EtherHack.utils;

import EtherHack.utils.ColorUtils;
import java.util.Properties;
import zombie.core.Color;

public class ConfigUtils {
    public static boolean getBooleanFromConfig(Properties config, String key, boolean defaultValue) {
        String valueStr = config.getProperty(key);
        return valueStr != null ? Boolean.parseBoolean(valueStr) : defaultValue;
    }

    public static Color getColorFromConfig(Properties config, String key, Color defaultColor) {
        String colorStr = config.getProperty(key);
        return colorStr != null ? ColorUtils.stringToColor(colorStr) : defaultColor;
    }
}
