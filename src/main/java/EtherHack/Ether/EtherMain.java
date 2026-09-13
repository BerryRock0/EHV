package EtherHack.Ether;

import EtherHack.Ether.EtherAPI;
import EtherHack.Ether.EtherLuaManager;
import EtherHack.Ether.EtherTranslator;
import EtherHack.utils.Logger;

public class EtherMain {
    private static EtherMain instance;
    public EtherTranslator etherTranslator;
    public EtherLuaManager etherLuaManager;
    public EtherAPI etherAPI;

    private EtherMain()
    {}

    public void init() {
        Logger.printLog((String)"Initializing EtherHack...");
        this.etherAPI = new EtherAPI();
        this.etherLuaManager = new EtherLuaManager();
        this.etherTranslator = new EtherTranslator();
        this.etherAPI.loadAPI();
        this.etherLuaManager.loadLua();
        this.etherTranslator.loadTranslations();
        Logger.printLog((String)"Initialization EtherHack was completed!");
    }

    public static EtherMain getInstance()
    {
        if (instance == null)
            instance = new EtherMain();

        return instance;
    }
}
