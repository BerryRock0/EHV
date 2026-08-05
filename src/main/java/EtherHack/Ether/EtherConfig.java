package EtherHack.Ether;

public class EtherConfig
{
    public boolean[] toggles = new boolean[70];
    public Color[] colors = new Color[12];

    private void initStartupConfig()
    {   
        for (boolean b: toggles)
            b = false;
        for (Color c: colors)
            c = new Color(0, 0, 0);
    }
}
