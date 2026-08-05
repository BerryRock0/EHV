package EtherHack.Ether;

public class EtherSettings
{
    private final boolean[] value = new boolean[EtherValue.values().length];

    public boolean is(EtherValue ev) {
        return value[ev.ordinal()];
    }

    public void set(EtherValue ev, boolean value) {
        value[ev.ordinal()] = value;
    }

    public void saveConfig(String configFileName)
    {
        Properties config = new Properties();
        FileOutputStream out = new FileOutputStream("EtherHack/config/" + configFileName + ".properties");
        
        for (EtherValue ev : EtherValue.values())
            config.setProperty(ev.name(), ev.is(ev));

        try {config.store(out, null);}
        catch (Exception e) {return;}
    }
    public void loadConfig()
    {


        
    }
}
