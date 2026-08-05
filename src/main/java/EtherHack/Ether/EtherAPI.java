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
    public Color mainUIAccentColor;
    public Color vehicleUIColor;
    public Color zombieUIColor;
    public Color playerUIColor;
    public Color survivorUIColor;
    public Color remoteSurvivorUIColor;
    public Color itemUIColor;
    public Color worldItemUIColor;
    public Color pushableUIColor;
    public Color buildingUIColor;
    public Color roomUIColor;
    public boolean isAlwaysRack;
    public boolean isAlwaysRoundChamber;
    public boolean isAlwaysRepaired;
    public boolean isAlwaysKnockdown;
    public boolean isAlwaysCritical;
    public boolean isAlwaysAiming;
    public boolean isPlayerInSafeTeleported;
    public boolean isMultiHitZombies;
    public boolean isTimedActionCheat;
    public boolean isEnableGodMode;
    public boolean isEnableNoclip;
    public boolean isEnableInvisible;
    public boolean isEnableNightVision;
    public boolean isZombieDontAttack;
    public boolean isNoRecoil;
    public boolean isNoReload;
    public boolean isNoJam;
    public boolean isNoSpentRoundChamber;
    public boolean isNoBroken;
    public boolean isNoInfected;
    public boolean isNoWet;
    public boolean isNoHoled;
    public boolean isNoDirted;
    public boolean isNoBlooded;
    public boolean isBypassDebugMode;
    public boolean isUnlimitedCarry;
    public boolean isUnlimitedCondition;
    public boolean isUnlimitedEndurance;
    public boolean isUnlimitedAmmo;
    public boolean isDisableFatigue;
    public boolean isDisableHunger;
    public boolean isDisableThirst;
    public boolean isDisableDrunkenness;
    public boolean isDisableAnger;
    public boolean isDisableFear;
    public boolean isDisablePain;
    public boolean isDisablePanic;
    public boolean isDisableMorale;
    public boolean isDisableStress;
    public boolean isDisableSickness;
    public boolean isDisableStressFromCigarettes;
    public boolean isDisableSanity;
    public boolean isDisableBoredomLevel;
    public boolean isDisableUnhappynessLevel;
    public boolean isDisableWetness;
    public boolean isDisableInfectionLevel;
    public boolean isDisableFakeInfectionLevel;
    public boolean isDisableFire;
    public boolean isOptimalCalories;
    public boolean isOptimalWeight;
    public boolean isVisualEnable360Vision;
    public boolean isMapDrawLocalPlayer;
    public boolean isMapDrawAllPlayers;
    public boolean isMapDrawVehicles;
    public boolean isMapDrawZombies;
    public boolean isMapDrawBuildings;
    public boolean isMapDrawSurvivors;
    public boolean isMapDrawRemoteSurvivors;
    public boolean isMapDrawPushables;
    public boolean isMapDrawItems;
    public boolean isMapDrawWorldItems;
    public boolean isMapDrawRooms;

    private void initStartupConfig()
    {
        Properties config = new Properties();
        //this. = ConfigUtils.getBooleanFromConfig(config, "", false);
        this.mainUIAccentColor = ConfigUtils.getColorFromConfig(config, "mainUIAccentColor", new Color(56, 239, 125));
        this.vehicleUIColor = ConfigUtils.getColorFromConfig(config, "vehicleUIColor", new Color(150, 150, 200));
        this.zombieUIColor = ConfigUtils.getColorFromConfig(config, "zombieUIColor", new Color(255, 150, 100));
        this.playerUIColor = ConfigUtils.getColorFromConfig(config, "playerUIColor", new Color(255, 50, 100));
        this.survivorUIColor = ConfigUtils.getColorFromConfig(config, "survivorUIColor", new Color(0, 0, 0));
        this.remoteSurvivorUIColor = ConfigUtils.getColorFromConfig(config, "remoteSurvivorUIColor", new Color(0, 0, 0));
        this.itemUIColor = ConfigUtils.getColorFromConfig(config, "itemUIColor", new Color(0, 0, 0));
        this.worldItemUIColor = ConfigUtils.getColorFromConfig(config, "worldItemUIColor", new Color(0, 0, 0));
        this.roomUIColor = ConfigUtils.getColorFromConfig(config, "roomUIColor", new Color(0, 0, 0));
        this.buildingUIColor = ConfigUtils.getColorFromConfig(config, "buildingUIColor", new Color(0, 0, 0));
        this.pushableUIColor = ConfigUtils.getColorFromConfig(config, "pushableUIColor", new Color(0, 0, 0));
        this.isAlwaysRack = ConfigUtils.getBooleanFromConfig(config, "isAlwaysRack", false);
        this.isAlwaysRoundChamber = ConfigUtils.getBooleanFromConfig(config, "isAlwaysRoundChamber", false);
        this.isAlwaysRepaired = ConfigUtils.getBooleanFromConfig(config, "isAlwaysRepaired", false);
        this.isAlwaysKnockdown = ConfigUtils.getBooleanFromConfig(config, "isAlwaysKnockdown", false);
        this.isAlwaysAiming = ConfigUtils.getBooleanFromConfig(config, "isAlwaysAiming", false);
        this.isAlwaysCritical = ConfigUtils.getBooleanFromConfig(config, "isAlwaysCritical", false);
        this.isPlayerInSafeTeleported = ConfigUtils.getBooleanFromConfig(config, "isPlayerInSafeTeleported", false);
        this.isMultiHitZombies = ConfigUtils.getBooleanFromConfig(config, "isMultiHitZombies", false);
        this.isTimedActionCheat = ConfigUtils.getBooleanFromConfig(config, "isTimedActionCheat", false);
        this.isEnableGodMode = ConfigUtils.getBooleanFromConfig(config, "isEnableGodMode", false);
        this.isEnableNoclip = ConfigUtils.getBooleanFromConfig(config, "isEnableNoclip", false);
        this.isEnableInvisible = ConfigUtils.getBooleanFromConfig(config, "isEnableInvisible", false);
        this.isEnableNightVision = ConfigUtils.getBooleanFromConfig(config, "isEnableNightVision", false);
        this.isZombieDontAttack = ConfigUtils.getBooleanFromConfig(config, "isZombieDontAttack", false);
        this.isNoRecoil = ConfigUtils.getBooleanFromConfig(config, "isNoRecoil", false);
        this.isNoReload = ConfigUtils.getBooleanFromConfig(config, "isNoReload", false);
        this.isNoJam = ConfigUtils.getBooleanFromConfig(config, "isNoJam", false);
        this.isNoSpentRoundChamber = ConfigUtils.getBooleanFromConfig(config, "isNoSpentRoundChamber", false);
        this.isNoBroken = ConfigUtils.getBooleanFromConfig(config, "isNoBroken", false);
        this.isNoInfected = ConfigUtils.getBooleanFromConfig(config, "isNoInfected", false);
        this.isNoWet = ConfigUtils.getBooleanFromConfig(config, "isNoWet", false);
        this.isNoHoled = ConfigUtils.getBooleanFromConfig(config, "isNoHoled", false);
        this.isNoDirted = ConfigUtils.getBooleanFromConfig(config, "isNoDirted", false);
        this.isNoBlooded = ConfigUtils.getBooleanFromConfig(config, "isNoBlooded", false);
        this.isBypassDebugMode = ConfigUtils.getBooleanFromConfig(config, "isBypassDebugMode", false);
        this.isUnlimitedCarry = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedCarry", false);
        this.isUnlimitedCondition = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedCondition", false);
        this.isUnlimitedEndurance = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedEndurance", false);
        this.isUnlimitedAmmo = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedAmmo", false);
        this.isDisableFatigue = ConfigUtils.getBooleanFromConfig(config, "isDisableFatigue", false);
        this.isDisableHunger = ConfigUtils.getBooleanFromConfig(config, "isDisableHunger", false);
        this.isDisableThirst = ConfigUtils.getBooleanFromConfig(config, "isDisableThirst", false);
        this.isDisableDrunkenness = ConfigUtils.getBooleanFromConfig(config, "isDisableDrunkenness", false);
        this.isDisableAnger = ConfigUtils.getBooleanFromConfig(config, "isDisableAnger", false);
        this.isDisableFear = ConfigUtils.getBooleanFromConfig(config, "isDisableFear", false);
        this.isDisablePain = ConfigUtils.getBooleanFromConfig(config, "isDisablePain", false);
        this.isDisablePanic = ConfigUtils.getBooleanFromConfig(config, "isDisablePanic", false);
        this.isDisableMorale = ConfigUtils.getBooleanFromConfig(config, "isDisableMorale", false);
        this.isDisableStress = ConfigUtils.getBooleanFromConfig(config, "isDisableStress", false);
        this.isDisableSickness = ConfigUtils.getBooleanFromConfig(config, "isDisableSickness", false);
        this.isDisableStressFromCigarettes = ConfigUtils.getBooleanFromConfig(config, "isDisableStressFromCigarettes", false);
        this.isDisableSanity = ConfigUtils.getBooleanFromConfig(config, "isDisableSanity", false);
        this.isDisableBoredomLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableBoredomLevel", false);
        this.isDisableUnhappynessLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableUnhappynessLevel", false);
        this.isDisableWetness = ConfigUtils.getBooleanFromConfig(config, "isDisableWetness", false);
        this.isDisableInfectionLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableInfectionLevel", false);
        this.isDisableFakeInfectionLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableFakeInfectionLevel", false);
        this.isDisableFire = ConfigUtils.getBooleanFromConfig(config, "isDisableFire", false);
        this.isOptimalCalories = ConfigUtils.getBooleanFromConfig(config, "isOptimalCalories", false);
        this.isOptimalWeight = ConfigUtils.getBooleanFromConfig(config, "isOptimalWeight", false);
        this.isVisualEnable360Vision = ConfigUtils.getBooleanFromConfig(config, "isVisualEnable360Vision", false);
        this.isMapDrawLocalPlayer = ConfigUtils.getBooleanFromConfig(config, "isMapDrawLocalPlayer", true);
        this.isMapDrawAllPlayers = ConfigUtils.getBooleanFromConfig(config, "isMapDrawAllPlayers", false);
        this.isMapDrawVehicles = ConfigUtils.getBooleanFromConfig(config, "isMapDrawVehicles", false);
        this.isMapDrawZombies = ConfigUtils.getBooleanFromConfig(config, "isMapDrawZombies", false);
        this.isMapDrawSurvivors = ConfigUtils.getBooleanFromConfig(config, "isMapDrawSurvivors", false);
        this.isMapDrawRemoteSurvivors = ConfigUtils.getBooleanFromConfig(config, "isMapDrawRemoteSurvivors", false);
        this.isMapDrawRooms = ConfigUtils.getBooleanFromConfig(config, "isMapDrawRooms", false);
        this.isMapDrawBuildings = ConfigUtils.getBooleanFromConfig(config, "isMapDrawBuildings", false);
        this.isMapDrawPushables = ConfigUtils.getBooleanFromConfig(config, "isMapDrawPushables", false);
    }

    public EtherAPI() {
        this.initStartupConfig();
        EventSubscriber.register((Object)this);
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
            if ((Boolean)SandboxOptions.instance.getOptionByName("MultiHitZombies").asConfigOption().getValueAsObject() != this.isMultiHitZombies)
                SandboxOptions.instance.set("MultiHitZombies", (Object)this.isMultiHitZombies);

            if (playerItem.getStringItemType().equals("RangedWeapon") && playerItem instanceof HandWeapon)
            {
                if(this.isAlwaysKnockdown) weapon.setAlwaysKnockdown(true);
                if(this.isAlwaysCritical)weapon.setCriticalChance(100.0f);
                if(this.isAlwaysRack)weapon.setRackAfterShoot(true);
                if(this.isNoJam) weapon.setJammed(false);
                if(this.isAlwaysRoundChamber)weapon.setRoundChambered(true);
                if(this.isNoSpentRoundChamber) weapon.setSpentRoundChambered(false);
                if(this.isAlwaysAiming) weapon.setAimingTime(0);
                if(this.isNoRecoil) weapon.setRecoilDelay(0);
                if(this.isNoReload) weapon.setReloadTime(0);
                if (this.isUnlimitedAmmo) playerItem.setCurrentAmmoCount(playerItem.getMaxAmmo());
            }
                
            if(this.isAlwaysRepaired) playerItem.setHaveBeenRepaired(1);
            if(this.isUnlimitedCondition) playerItem.setCondition(playerItem.getConditionMax());
        }
        if (inventoryItems != null && !inventoryItems.isEmpty())
        {
            for (InventoryItem item : inventoryItems)
            {
                if (item == null)
                    continue;

                if (item.getVisual() != null)
                {
                    if(this.isNoHoled) for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) item.getVisual().removeHole(i);
                    if(this.isNoDirted) item.getVisual().removeDirt();
                    if(this.isNoBlooded) item.getVisual().removeBlood();
                }

                if(this.isNoBroken) item.setBroken(false);
                if(this.isAlwaysRepaired) item.setHaveBeenRepaired(1);
                if(this.isNoWet) item.setWet(false);
                if(this.isNoInfected) item.setInfected(false);
                if(this.isUnlimitedCondition) item.setCondition(item.getConditionMax());
            }
        }

        if (localPlayer != null)
        {
            if(this.isUnlimitedEndurance) localPlayer.getStats().setEndurance(1.0f);
            if(this.isDisableFatigue) localPlayer.getStats().setFatigue(0.0f);
            if(this.isDisableHunger) localPlayer.getStats().setHunger(0.0f);
            if(this.isDisableThirst) localPlayer.getStats().setThirst(0.0f);
            if(this.isDisableDrunkenness) localPlayer.getStats().setDrunkenness(0.0f);
            if(this.isDisableAnger) localPlayer.getStats().setAnger(0.0f);
            if(this.isDisableFear) localPlayer.getStats().setFear(0.0f);
            if(this.isDisablePain) localPlayer.getStats().setPain(0.0f);
            if(this.isDisablePanic) localPlayer.getStats().setPanic(0.0f);
            if(this.isDisableMorale) localPlayer.getStats().setMorale(1.0f);
            if(this.isDisableStress) localPlayer.getStats().setStress(0.0f);
            if(this.isDisableSickness) localPlayer.getStats().setSickness(0.0f);
            if(this.isDisableStressFromCigarettes) localPlayer.getStats().setStressFromCigarettes(0.0f);
            if(this.isDisableSanity) localPlayer.getStats().setSanity(1.0f);
            if(this.isDisableBoredomLevel) localPlayer.getBodyDamage().setBoredomLevel(0.0f);
            if(this.isDisableUnhappynessLevel) localPlayer.getBodyDamage().setUnhappynessLevel(0.0f);
            if(this.isDisableWetness) localPlayer.getBodyDamage().setWetness(0.0f);
            if(this.isDisableInfectionLevel) localPlayer.getBodyDamage().setInfectionLevel(0.0f);
            if(this.isDisableFakeInfectionLevel) localPlayer.getBodyDamage().setFakeInfectionLevel(0.0f);
            if(this.isDisableFire) localPlayer.setOnFire(false);
            if(this.isOptimalCalories) localPlayer.getNutrition().setCalories(1200.0f);
            if(this.isOptimalWeight) localPlayer.getNutrition().setWeight(80.0);
            if(localPlayer.isGodMod() != this.isEnableGodMode) localPlayer.setGodMod(this.isEnableGodMode);
            if(localPlayer.isNoClip() != this.isEnableNoclip) localPlayer.setNoClip(this.isEnableNoclip);
            if(localPlayer.isInvisible() != this.isEnableInvisible) localPlayer.setInvisible(this.isEnableInvisible);
            if(localPlayer.isTimedActionInstantCheat() != this.isTimedActionCheat) localPlayer.setTimedActionInstantCheat(this.isTimedActionCheat);
            if(localPlayer.isZombiesDontAttack() != this.isZombieDontAttack) localPlayer.setZombiesDontAttack(this.isZombieDontAttack);
            if(localPlayer.isWearingNightVisionGoggles() != this.isEnableNightVision) localPlayer.setWearingNightVisionGoggles(this.isEnableNightVision);
        }
    }

    private void bypassDebugMode()
    {
        boolean isGameActive = GameClient.bIngame;
        boolean isAntiCheatProtectionEnabled = ServerOptions.instance.getBoolean("AntiCheatProtectionType12");
        boolean isServerMode = GameServer.bServer;
        boolean isCooperativeMode = GameServer.bCoop;
        Core.bDebug = isGameActive && this.isBypassDebugMode && (!isAntiCheatProtectionEnabled && isServerMode || isCooperativeMode || !isServerMode);
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
        
        if (!this.isVisualEnable360Vision)
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
