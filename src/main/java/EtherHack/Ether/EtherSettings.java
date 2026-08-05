package EtherHack.Ether;

public class EtherSettings
{
    private final boolean[] flags = new boolean[Flag.values().length];

    public void load(Properties config)
    {
        for (Flag f : Flag.values())
          flags[f.ordinal()] = Boolean.parseBoolean(config.getProperty(f.name(), "false"));
    }

    public boolean is(Flag f) {
        return flags[f.ordinal()];
    }

    public void set(Flag f, boolean value) {
        flags[f.ordinal()] = value;
    }
}
