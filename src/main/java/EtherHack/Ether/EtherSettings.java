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
}
