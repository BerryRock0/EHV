package EtherHack.Ether;

import EtherHack.Ether.EtherAPI;
import EtherHack.Ether.EtherLuaManager;
import EtherHack.Ether.EtherTranslator;
import EtherHack.utils.Logger;

public class EtherMain {
    private static EtherMain instance;
    public EtherTranslator etherTranslator;
    public EtherCredits etherCredits;
    public EtherLuaManager etherLuaManager;
    public EtherAPI etherAPI;

    private EtherMain() {
    }

    public void init() {
        Logger.printLog((String)"Initializing EtherHack...");
        this.etherTranslator = new EtherTranslator();
        this.etherTranslator.loadTranslations();
        this.etherAPI = new EtherAPI();
        this.etherAPI.loadAPI();
        this.etherLuaManager = new EtherLuaManager();
        this.etherLuaManager.loadLua();
        Logger.printLog((String)"Initialization EtherHack was completed!");
    }

    public static EtherMain getInstance() {
        if (instance == null) {
            instance = new EtherMain();
        }
        return instance;
    }
}
