package EtherHack.Ether;

import EtherHack.Ether.EtherLuaMethods;
import EtherHack.Ether.EtherMain;
import EtherHack.annotations.LuaEvents;
import EtherHack.annotations.SubscribeLuaEvent;
import EtherHack.utils.ColorUtils;
import EtherHack.utils.ConfigUtils;
import EtherHack.utils.EventSubscriber;
import EtherHack.utils.Exposer;
import EtherHack.utils.Logger;
import EtherHack.utils.PlayerUtils;
import EtherHack.utils.Rendering;
import EtherHack.utils.VehicleUtils;
import EtherHack.utils.ZombieUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import se.krka.kahlua.vm.Platform;
import zombie.Lua.LuaManager;
import zombie.SandboxOptions;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.ui.UIFont;
import zombie.vehicles.BaseVehicle;

/**
 * Этот класс предоставляет API Ether для приложения.
 */
public class EtherAPI
{
    private Exposer exposer;
    private final EtherLuaMethods etherLuaMethods = new EtherLuaMethods();
    public HashMap<String, Texture> textureCache = new HashMap();
    public HashMap<String, float[]> originalWeaponStats = new HashMap();
    public boolean[] toggles = new boolean[64];
    public Color[] colors = new Color[12];

    public EtherAPI() {
        this.initStartupConfig();
        EventSubscriber.register((Object)this);
    }

    private void initStartupConfig()
    {   
        for (boolean b: toggles)
            b = false;
        for (Color c: colors)
            c = new Color(0, 0, 0);
    }

    @LuaEvents(value={@SubscribeLuaEvent(eventName="OnResetLua"), @SubscribeLuaEvent(eventName="OnMainMenuEnter")})
    public void loadAPI() {
        Logger.printLog((String)"Loading EtherAPI...");
        if (this.exposer != null) {
            this.exposer.destroy();
        }
        this.exposer = new Exposer(LuaManager.converterManager, (Platform)LuaManager.platform, LuaManager.env);
        this.exposer.exposeAPI(this.etherLuaMethods);
    }

    private void updateLocalPlayerFeatures()
    {
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        InventoryItem playerItem = localPlayer.getPrimaryHandItem();
        HandWeapon weapon = (HandWeapon)playerItem;
        String weaponType = weapon.getFullType();
        ArrayList<InventoryItem> inventoryItems = localPlayer.getInventory().getItems();

        if (localPlayer == null)
            return;

        if (playerItem != null)
        {        
            if ((Boolean)SandboxOptions.instance.getOptionByName("MultiHitZombies").asConfigOption().getValueAsObject() != toggles[0] )
                SandboxOptions.instance.set("MultiHitZombies", (Object)toggles[0]);

            if (playerItem.getStringItemType().equals("RangedWeapon") && playerItem instanceof HandWeapon)
            {
                if(toggles[3])weapon.setAlwaysKnockdown(true);
                if(toggles[5])weapon.setCriticalChance(100.0f);
                if(toggles[0])weapon.setRackAfterShoot(true);
                if(toggles[13])weapon.setJammed(false);
                if(toggles[1])weapon.setRoundChambered(true);
                if(toggles[14])weapon.setSpentRoundChambered(false);
                if(toggles[4])weapon.setAimingTime(0);
                if(toggles[11])weapon.setRecoilDelay(0);
                if(toggles[12])weapon.setReloadTime(0);
                if(toggles[41])playerItem.setCurrentAmmoCount(playerItem.getMaxAmmo());
            }
                
            if(toggles[2]) playerItem.setHaveBeenRepaired(1);
            if(toggles[23]) playerItem.setCondition(playerItem.getConditionMax());
        }
        if (inventoryItems != null && !inventoryItems.isEmpty())
        {
            for (InventoryItem item : inventoryItems)
            {
                if (item == null)
                    continue;

                if (item.getVisual() != null)
                {
                    if(toggles[18]) for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) item.getVisual().removeHole(i);
                    if(toggles[19]) item.getVisual().removeDirt();
                    if(toggles[20]) item.getVisual().removeBlood();
                }

                if(toggles[15]) item.setBroken(false);
                if(toggles[2]) item.setHaveBeenRepaired(1);
                if(toggles[17]) item.setWet(false);
                if(toggles[16]) item.setInfected(false);
                if(toggles[23]) item.setCondition(item.getConditionMax());
            }
        }

        if (localPlayer != null)
        {
            if(toggles[40]) localPlayer.getStats().setEndurance(1.0f);
            if(toggles[42]) localPlayer.getStats().setFatigue(0.0f);
            if(toggles[43]) localPlayer.getStats().setHunger(0.0f);
            if(toggles[44]) localPlayer.getStats().setThirst(0.0f);
            if(toggles[45]) localPlayer.getStats().setDrunkenness(0.0f);
            if(toggles[46]) localPlayer.getStats().setAnger(0.0f);
            if(toggles[47]) localPlayer.getStats().setFear(0.0f);
            if(toggles[48]) localPlayer.getStats().setPain(0.0f);
            if(toggles[49]) localPlayer.getStats().setPanic(0.0f);
            if(toggles[50]) localPlayer.getStats().setMorale(1.0f);
            if(toggles[51]) localPlayer.getStats().setStress(0.0f);
            if(toggles[52]) localPlayer.getStats().setSickness(0.0f);
            if(toggles[53]) localPlayer.getStats().setStressFromCigarettes(0.0f);
            if(toggles[54]) localPlayer.getStats().setSanity(1.0f);
            if(toggles[55]) localPlayer.getBodyDamage().setBoredomLevel(0.0f);
            if(toggles[56]) localPlayer.getBodyDamage().setUnhappynessLevel(0.0f);
            if(toggles[57]) localPlayer.getBodyDamage().setWetness(0.0f);
            if(toggles[58]) localPlayer.getBodyDamage().setInfectionLevel(0.0f);
            if(toggles[59]) localPlayer.getBodyDamage().setFakeInfectionLevel(0.0f);
            if(toggles[60]) localPlayer.setOnFire(false);
            if(toggles[61]) localPlayer.getNutrition().setCalories(1200.0f);
            if(toggles[62]) localPlayer.getNutrition().setWeight(80.0);
            if(localPlayer.isGodMod() != toggles[9]) localPlayer.setGodMod(toggles[9]);
            if(localPlayer.isNoClip() != toggles[8]) localPlayer.setNoClip(toggles[8]);
            if(localPlayer.isInvisible() != toggles[6]) localPlayer.setInvisible(toggles[6]);
            if(localPlayer.isTimedActionInstantCheat() != toggles[21]) localPlayer.setTimedActionInstantCheat(toggles[21]);
            if(localPlayer.isZombiesDontAttack() != toggles[7]) localPlayer.setZombiesDontAttack(toggles[7]);
            if(localPlayer.isWearingNightVisionGoggles() != toggles[10]) localPlayer.setWearingNightVisionGoggles(toggles[10]);
        }
    }

    private void bypassDebugMode()
    {
        boolean isGameActive = GameClient.bIngame;
        boolean isAntiCheatProtectionEnabled = ServerOptions.instance.getBoolean("AntiCheatProtectionType12");
        boolean isServerMode = GameServer.bServer;
        boolean isCooperativeMode = GameServer.bCoop;
        Core.bDebug = isGameActive && toggles[39] && (!isAntiCheatProtectionEnabled && isServerMode || isCooperativeMode || !isServerMode);
    }

    @SubscribeLuaEvent(eventName="OnPostUIDraw")
    public void updateVisuals() {
        try
        {
            this.updateUltraPlayerVision();
        }
        catch (Exception exception)
        {}
    }

    public void updateUltraPlayerVision()
    {
        ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
        ArrayList<IsoZombie> zombies = IsoWorld.instance.getCell().getZombieList();
        ArrayList<BaseVehicle> vehicles = IsoWorld.instance.getCell().getVehicles();
        
        if (!toggles[24])
            return;
        
        if (vehicles != null && !vehicles.isEmpty())
            for (BaseVehicle vehicle : vehicles)
                vehicle.setAlpha(100.0f);
        
        if (zombies != null && !zombies.isEmpty())
            for (IsoZombie zombie : zombies)
                zombie.setAlpha(100.0f);
        
        if (players != null && !players.isEmpty())
            for (IsoPlayer player : players)
                player.setAlpha(100.0f);
    }

    @SubscribeLuaEvent(eventName="OnRenderTick")
    public void updateAPI()
    {
        try
        {
            this.bypassDebugMode();
            this.updateLocalPlayerFeatures();
        }
        catch(Exception e)
        {}
    }
}
