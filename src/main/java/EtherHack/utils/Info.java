package EtherHack.utils;

import java.io.IOException;
import java.util.Properties;

public class Info {
    private static final String CHEAT_VERSION;
    public static final String CHEAT_NAME = "EtherHack";
    public static final String CHEAT_AUTHOR = "Quzile";
    public static final String CHEAT_TAG = "[EtherHack]: ";
    public static final String CHEAT_CREDITS_AUTHOR = "Author: Quzile";

    static {
        Properties properties = new Properties();
        try {
            properties.load(Info.class.getClassLoader().getResourceAsStream("EtherHack/EtherHack.properties"));
            CHEAT_VERSION = properties.getProperty("version").replace("'", "");
        }
        catch (IOException e) {
            throw new ExceptionInInitializerError("Unable to load version from EtherHack.properties");
        }
    }
}
