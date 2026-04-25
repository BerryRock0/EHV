package EtherHack.utils;

import EtherHack.Ether.EtherLuaMethods;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;

public final class Exposer
extends LuaJavaClassExposer {
    public Exposer(KahluaConverterManager converterManager, Platform platform, KahluaTable env) {
        super(converterManager, platform, env);
    }

    public void exposeAPI(EtherLuaMethods api) {
        this.exposeGlobalFunctions(api);
    }
}
