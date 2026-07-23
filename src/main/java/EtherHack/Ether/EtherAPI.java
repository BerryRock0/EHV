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
    public Color vehiclesUIColor;
    public Color zombiesUIColor;
    public Color playersUIColor;
    public Color survivorsUIColor;
    public Color remoteSurvivorsUIColor;
    public Color pushablesUIColor;
    public Color buildingsUIColor;
    public Color roomsUIColor;
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
    public boolean isVisualsEnable;
    public boolean isVisualsPlayersEnable;
    public boolean isVisualsVehiclesEnable;
    public boolean isVisualsZombiesEnable;
    public boolean isVisualDrawToLocalPlayer;
    public boolean isVisualDrawPlayerNickname;
    public boolean isVisualDrawPlayerInfo;
    public boolean isVisualDrawLineToVehicle;
    public boolean isVisualDrawLineToPlayers;
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

    public void saveConfig(String configFileName) {
        Properties config = new Properties();
        //config.setProperty("", Boolean.toString(this.));
        config.setProperty("mainUIAccentColor", ColorUtils.colorToString((Color)this.mainUIAccentColor));
        config.setProperty("vehiclesUIColor", ColorUtils.colorToString((Color)this.vehiclesUIColor));
        config.setProperty("zombiesUIColor", ColorUtils.colorToString((Color)this.zombiesUIColor));
        config.setProperty("playersUIColor", ColorUtils.colorToString((Color)this.playersUIColor));
        config.setProperty("survivorsUIColor", ColorUtils.colorToString((Color)this.survivorsUIColor));
        config.setProperty("remoteSurvivorsUIColor", ColorUtils.colorToString((Color)this.remoteSurvivorsUIColor));
        config.setProperty("pushablesUIColor", ColorUtils.colorToString((Color)this.pushablesUIColor));
        config.setProperty("roomsUIColor", ColorUtils.colorToString((Color)this.roomsUIColor));
        config.setProperty("buildingsUIColor", ColorUtils.colorToString((Color)this.buildingsUIColor));
        config.setProperty("isAlwaysRack", Boolean.toString(this.isAlwaysRack));
        config.setProperty("isAlwaysRoundChamber", Boolean.toString(this.isAlwaysRoundChamber));
        config.setProperty("isAlwaysRepaired", Boolean.toString(this.isAlwaysRepaired));
        config.setProperty("isAlwaysKnockdown", Boolean.toString(this.isAlwaysKnockdown));
        config.setProperty("isAlwaysAiming", Boolean.toString(this.isAlwaysAiming));
        config.setProperty("isAlwaysCritical", Boolean.toString(this.isAlwaysCritical));
        config.setProperty("isPlayerInSafeTeleported", Boolean.toString(this.isPlayerInSafeTeleported));
        config.setProperty("isMultiHitZombies", Boolean.toString(this.isMultiHitZombies));
        config.setProperty("isPlayerInSafeTeleported", Boolean.toString(this.isPlayerInSafeTeleported));
        config.setProperty("isMultiHitZombies", Boolean.toString(this.isMultiHitZombies));
        config.setProperty("isTimedActionCheat", Boolean.toString(this.isTimedActionCheat));
        config.setProperty("isEnableGodMode", Boolean.toString(this.isEnableGodMode));
        config.setProperty("isEnableNoclip", Boolean.toString(this.isEnableNoclip));
        config.setProperty("isEnableInvisible", Boolean.toString(this.isEnableInvisible));
        config.setProperty("isEnableNightVision", Boolean.toString(this.isEnableNightVision));
        config.setProperty("isZombieDontAttack", Boolean.toString(this.isZombieDontAttack));
        config.setProperty("isAlwaysRack", Boolean.toString(this.isNoJam));
        config.setProperty("isNoRecoil", Boolean.toString(this.isNoRecoil));
        config.setProperty("isNoReload", Boolean.toString(this.isNoReload));
        config.setProperty("isNoJam", Boolean.toString(this.isNoJam));
        config.setProperty("isNoSpentRoundChamber", Boolean.toString(this.isNoSpentRoundChamber));
        config.setProperty("isNoBroken", Boolean.toString(this.isNoBroken));
        config.setProperty("isNoInfected", Boolean.toString(this.isNoInfected));
        config.setProperty("isNoWet", Boolean.toString(this.isNoWet)); 
        config.setProperty("isNoHoled", Boolean.toString(this.isNoHoled));
        config.setProperty("isNoDirted", Boolean.toString(this.isNoDirted));
        config.setProperty("isNoBlooded", Boolean.toString(this.isNoBlooded));
        config.setProperty("isBypassDebugMode", Boolean.toString(this.isBypassDebugMode));
        config.setProperty("isUnlimitedCarry", Boolean.toString(this.isUnlimitedCarry));
        config.setProperty("isUnlimitedCondition", Boolean.toString(this.isUnlimitedCondition));
        config.setProperty("isUnlimitedEndurance", Boolean.toString(this.isUnlimitedEndurance));
        config.setProperty("isUnlimitedAmmo", Boolean.toString(this.isUnlimitedAmmo));
        config.setProperty("isDisableFatigue", Boolean.toString(this.isDisableFatigue));
        config.setProperty("isDisableHunger", Boolean.toString(this.isDisableHunger));
        config.setProperty("isDisableThirst", Boolean.toString(this.isDisableThirst));
        config.setProperty("isDisableDrunkenness", Boolean.toString(this.isDisableDrunkenness));
        config.setProperty("isDisableAnger", Boolean.toString(this.isDisableAnger));
        config.setProperty("isDisableFear", Boolean.toString(this.isDisableFear));
        config.setProperty("isDisablePain", Boolean.toString(this.isDisablePain));
        config.setProperty("isDisablePanic", Boolean.toString(this.isDisablePanic));
        config.setProperty("isDisableMorale", Boolean.toString(this.isDisableMorale));
        config.setProperty("isDisableStress", Boolean.toString(this.isDisableStress));
        config.setProperty("isDisableSickness", Boolean.toString(this.isDisableSickness));
        config.setProperty("isDisableStressFromCigarettes", Boolean.toString(this.isDisableStressFromCigarettes));
        config.setProperty("isDisableSanity", Boolean.toString(this.isDisableSanity));
        config.setProperty("isDisableBoredomLevel", Boolean.toString(this.isDisableBoredomLevel));
        config.setProperty("isDisableUnhappynessLevel", Boolean.toString(this.isDisableUnhappynessLevel));
        config.setProperty("isDisableWetness", Boolean.toString(this.isDisableWetness));
        config.setProperty("isDisableInfectionLevel", Boolean.toString(this.isDisableInfectionLevel));
        config.setProperty("isDisableFire", Boolean.toString(this.isDisableFire));
        config.setProperty("isDisableFakeInfectionLevel", Boolean.toString(this.isDisableFakeInfectionLevel));
        config.setProperty("isOptimalCalories", Boolean.toString(this.isOptimalCalories));
        config.setProperty("isOptimalWeight", Boolean.toString(this.isOptimalWeight));
        config.setProperty("isVisualsEnable", Boolean.toString(this.isVisualsEnable));
        config.setProperty("isVisualsPlayersEnable", Boolean.toString(this.isVisualsPlayersEnable));
        config.setProperty("isVisualsVehiclesEnable", Boolean.toString(this.isVisualsVehiclesEnable));
        config.setProperty("isVisualsZombiesEnable", Boolean.toString(this.isVisualsZombiesEnable));
        config.setProperty("isVisualDrawToLocalPlayer", Boolean.toString(this.isVisualDrawToLocalPlayer));
        config.setProperty("isVisualDrawPlayerNickname", Boolean.toString(this.isVisualDrawPlayerNickname));
        config.setProperty("isVisualDrawPlayerInfo", Boolean.toString(this.isVisualDrawPlayerInfo));
        config.setProperty("isVisualDrawLineToVehicle", Boolean.toString(this.isVisualDrawLineToVehicle));
        config.setProperty("isVisualDrawLineToPlayers", Boolean.toString(this.isVisualDrawLineToPlayers));
        config.setProperty("isVisualEnable360Vision", Boolean.toString(this.isVisualEnable360Vision));
        config.setProperty("isMapDrawLocalPlayer", Boolean.toString(this.isMapDrawLocalPlayer));
        config.setProperty("isMapDrawAllPlayers", Boolean.toString(this.isMapDrawAllPlayers));
        config.setProperty("isMapDrawVehicles", Boolean.toString(this.isMapDrawVehicles));
        config.setProperty("isMapDrawZombies", Boolean.toString(this.isMapDrawZombies));
        config.setProperty("isMapDrawBuildings", Boolean.toString(this.isMapDrawBuildings));
        config.setProperty("isMapDrawPushables", Boolean.toString(this.isMapDrawPushables));
        config.setProperty("isMapDrawRooms", Boolean.toString(this.isMapDrawRooms));
        config.setProperty("isMapDrawSurvivors", Boolean.toString(this.isMapDrawSurvivors));
        config.setProperty("isMapDrawRemoteSurvivors", Boolean.toString(this.isMapDrawRemoteSurvivors));
        try (FileOutputStream out = new FileOutputStream("EtherHack/config/" + configFileName + ".properties");){
            config.store(out, null);
        }
        catch (IOException e) {
            Logger.printLog((String)("Error while saving config: " + e));
            return;
        }
    }

    public void loadConfig(String configFileName) {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("EtherHack/config/" + configFileName + ".properties");){
            config.load(fis);
        }
        catch (IOException e) {
            Logger.printLog((String)"The config file was not found. Loading canceled.");
            return;
        }
        //this. = ConfigUtils.getBooleanFromConfig((Properties)config, (String)"", (boolean)false);
        this.mainUIAccentColor = ConfigUtils.getColorFromConfig(config, "mainUIAccentColor", new Color(56, 239, 125));
        this.vehiclesUIColor = ConfigUtils.getColorFromConfig(config, "vehiclesUIColor", new Color(150, 150, 200));
        this.zombiesUIColor = ConfigUtils.getColorFromConfig(config, "zombiesUIColor", new Color(255, 150, 100));
        this.playersUIColor = ConfigUtils.getColorFromConfig(config, "playersUIColor", new Color(255, 50, 100));
        this.survivorsUIColor = ConfigUtils.getColorFromConfig(config, "survivorsUIColor", new Color(0, 0, 0));
        this.remoteSurvivorsUIColor = ConfigUtils.getColorFromConfig(config, "remoteSurvivorsUIColor", new Color(0, 0, 0));
        this.roomsUIColor = ConfigUtils.getColorFromConfig(config, "roomsUIColor", new Color(0, 0, 0));
        this.buildingsUIColor = ConfigUtils.getColorFromConfig(config, "buildingsUIColor", new Color(0, 0, 0));
        this.pushablesUIColor = ConfigUtils.getColorFromConfig(config, "pushablesUIColor", new Color(0, 0, 0));
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
        this.isVisualsEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsEnable", false);
        this.isVisualsPlayersEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsPlayersEnable", false);
        this.isVisualsVehiclesEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsVehiclesEnable", false);
        this.isVisualsZombiesEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsZombiesEnable", false);
        this.isVisualDrawToLocalPlayer = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawToLocalPlayer", false);
        this.isVisualDrawPlayerNickname = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawPlayerNickname", false);
        this.isVisualDrawPlayerInfo = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawPlayerInfo", false);
        this.isVisualDrawLineToVehicle = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawLineToVehicle", false);
        this.isVisualDrawLineToPlayers = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawLineToPlayers", false);
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

    private void initStartupConfig() {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("EtherHack/config/startup.properties");)
        {
            if (Files.noExists(Paths.get("EtherHack","config")))
            {
                Files.createDirectories(Paths.get("EtherHack","config"));
                Files.createFile(Paths.get("EtherHack","config").resolve("startup.properties"));
            }

            config.load(fis);    
        }
        catch (IOException e) 
        {}

        //this. = ConfigUtils.getBooleanFromConfig((Properties)config, (String)"", (boolean)false);
        this.mainUIAccentColor = ConfigUtils.getColorFromConfig(config, "mainUIAccentColor", new Color(56, 239, 125));
        this.vehiclesUIColor = ConfigUtils.getColorFromConfig(config, "vehiclesUIColor", new Color(150, 150, 200));
        this.zombiesUIColor = ConfigUtils.getColorFromConfig(config, "zombiesUIColor", new Color(255, 150, 100));
        this.playersUIColor = ConfigUtils.getColorFromConfig(config, "playersUIColor", new Color(255, 50, 100));
        this.survivorsUIColor = ConfigUtils.getColorFromConfig(config, "survivorsUIColor", new Color(0, 0, 0));
        this.remoteSurvivorsUIColor = ConfigUtils.getColorFromConfig(config, "remoteSurvivorsUIColor", new Color(0, 0, 0));
        this.roomsUIColor = ConfigUtils.getColorFromConfig(config, "roomsUIColor", new Color(0, 0, 0));
        this.buildingsUIColor = ConfigUtils.getColorFromConfig(config, "buildingsUIColor", new Color(0, 0, 0));
        this.pushablesUIColor = ConfigUtils.getColorFromConfig(config, "pushablesUIColor", new Color(0, 0, 0));
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
        this.isVisualsEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsEnable", false);
        this.isVisualsPlayersEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsPlayersEnable", false);
        this.isVisualsVehiclesEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsVehiclesEnable", false);
        this.isVisualsZombiesEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsZombiesEnable", false);
        this.isVisualDrawToLocalPlayer = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawToLocalPlayer", false);
        this.isVisualDrawPlayerNickname = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawPlayerNickname", false);
        this.isVisualDrawPlayerInfo = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawPlayerInfo", false);
        this.isVisualDrawLineToVehicle = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawLineToVehicle", false);
        this.isVisualDrawLineToPlayers = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawLineToPlayers", false);
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
        ArrayList<InventoryItem> inventoryItems;
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        InventoryItem playerItem = localPlayer.getPrimaryHandItem();
        HandWeapon weapon = (HandWeapon)playerItem;
        String weaponType = weapon.getFullType();

        if (localPlayer == null)
            return;

        if (playerItem != null)
        {        
            if ((Boolean)SandboxOptions.instance.getOptionByName("MultiHitZombies").asConfigOption().getValueAsObject() != this.isMultiHitZombies)
                SandboxOptions.instance.set("MultiHitZombies", (Object)this.isMultiHitZombies);

            if (playerItem != null && playerItem.getStringItemType().equals("RangedWeapon") && playerItem instanceof HandWeapon)
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
        if ((inventoryItems = localPlayer.getInventory().getItems()) != null && !inventoryItems.isEmpty())
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
                if(this.isNoInfected)item.setInfected(false);
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

    private void bypassDebugMode() {
        boolean isGameActive = GameClient.bIngame;
        boolean isAntiCheatProtectionEnabled = ServerOptions.instance.getBoolean("AntiCheatProtectionType12");
        boolean isServerMode = GameServer.bServer;
        boolean isCooperativeMode = GameServer.bCoop;
        Core.bDebug = isGameActive && this.isBypassDebugMode && (!isAntiCheatProtectionEnabled && isServerMode || isCooperativeMode || !isServerMode);
    }

    @SubscribeLuaEvent(eventName="OnPostUIDraw")
    public void updateVisuals() {
        try {
            this.updatePlayersVisuals();
            this.updateVehiclesVisuals();
            this.updateZombiesVisuals();
            this.updateUltraPlayerVision();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void updateUltraPlayerVision() {
        ArrayList<IsoPlayer> players;
        ArrayList<IsoZombie> zombies;
        
        if (!this.isVisualEnable360Vision) {
            return;
        }
        ArrayList<BaseVehicle> vehicles = IsoWorld.instance.getCell().getVehicles();
        if (vehicles != null && !vehicles.isEmpty()) {
            for (BaseVehicle vehicle : vehicles) {
                vehicle.setAlpha(100.0f);
            }
        }
        if ((zombies = IsoWorld.instance.getCell().getZombieList()) != null && !zombies.isEmpty()) {
            for (IsoZombie zombie : zombies) {
                zombie.setAlpha(100.0f);
            }
        }
        if ((players = GameClient.instance.getPlayers()) != null && !players.isEmpty()) {
            for (IsoPlayer player : players) {
                if (player.isLocalPlayer()) continue;
                player.setAlpha(100.0f);
            }
        }
    }

    private void updateVehiclesVisuals() {
        if (!this.isVisualsEnable || !this.isVisualsVehiclesEnable) {
            return;
        }
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) {
            return;
        }
        ArrayList<BaseVehicle> vehicles = IsoWorld.instance.getCell().getVehicles();
        float posLocalPlayerX = PlayerUtils.getScreenPositionX((IsoPlayer)localPlayer);
        float posLocalPlayerY = PlayerUtils.getScreenPositionY((IsoPlayer)localPlayer);
        float colorA = this.vehiclesUIColor.a;
        float colorR = this.vehiclesUIColor.r;
        float colorG = this.vehiclesUIColor.g;
        float colorB = this.vehiclesUIColor.b;
        if (vehicles == null && vehicles.isEmpty()) {
            return;
        }
        for (BaseVehicle vehicle : vehicles) {
            float vehiclePosX = VehicleUtils.getScreenPositionX((BaseVehicle)vehicle);
            float vehiclePosY = VehicleUtils.getScreenPositionY((BaseVehicle)vehicle);
            Rendering.drawTextCenterWithShadow((String)("ID:" + vehicle.getScriptName()), (UIFont)UIFont.Small, (float)vehiclePosX, (float)vehiclePosY, (float)colorR, (float)colorG, (float)colorB, (float)colorA);
            Rendering.drawTextCenterWithShadow((String)(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_VehicleSpeed") + vehicle.getMaxSpeed()), (UIFont)UIFont.Small, (float)vehiclePosX, (float)(vehiclePosY + 10.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);
            if (!this.isVisualDrawLineToVehicle) continue;
            int distance = (int)PlayerUtils.getDistanceBetweenPlayerAndVehicle((IsoPlayer)localPlayer, (BaseVehicle)vehicle);
            int textDistance = Math.max(30, Math.min(150, distance));
            float totalLength = (float)Math.sqrt(Math.pow(vehiclePosX - posLocalPlayerX, 2.0) + Math.pow(vehiclePosY - posLocalPlayerY, 2.0));
            float ratio = (float)textDistance / totalLength;
            float textPosX = posLocalPlayerX + ratio * (vehiclePosX - posLocalPlayerX);
            float textPosY = posLocalPlayerY + 60.0f + ratio * (vehiclePosY - posLocalPlayerY);
            Rendering.drawLine((int)((int)vehiclePosX), (int)((int)vehiclePosY), (int)((int)posLocalPlayerX), (int)((int)posLocalPlayerY + 60), (float)colorR, (float)colorG, (float)colorB, (float)0.8f, (int)1);
            Rendering.drawTextCenterWithShadow((String)String.valueOf(distance), (UIFont)UIFont.Small, (float)textPosX, (float)textPosY, (float)colorR, (float)colorG, (float)colorB, (float)colorA);
        }
    }

    private void updateZombiesVisuals() {
        if (!this.isVisualsEnable || !this.isVisualsZombiesEnable) {
            return;
        }
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) {
            return;
        }
        ArrayList<IsoZombie> zombies = IsoWorld.instance.getCell().getZombieList();
        float colorA = this.zombiesUIColor.a;
        float colorR = this.zombiesUIColor.r;
        float colorG = this.zombiesUIColor.g;
        float colorB = this.zombiesUIColor.b;
        if (zombies == null && zombies.isEmpty()) {
            return;
        }
        for (IsoZombie zombie : zombies) {
            float posX = ZombieUtils.getScreenPositionX((IsoZombie)zombie);
            float posY = ZombieUtils.getScreenPositionY((IsoZombie)zombie);
            int health = (int)(zombie.getHealth() * 100.0f);
            Rendering.drawTextCenterWithShadow((String)EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_ZombieTitle"), (UIFont)UIFont.Small, (float)posX, (float)posY, (float)colorR, (float)colorG, (float)colorB, (float)colorA);
            Rendering.drawTextCenterWithShadow((String)(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_ZombieHealth") + health), (UIFont)UIFont.Small, (float)posX, (float)(posY + 10.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);
        }
    }

    private void updatePlayersVisuals() {
        if (!this.isVisualsEnable || !this.isVisualsPlayersEnable) {
            return;
        }
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) {
            return;
        }
        ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
        float posLocalPlayerX = PlayerUtils.getScreenPositionX((IsoPlayer)localPlayer);
        float posLocalPlayerY = PlayerUtils.getScreenPositionY((IsoPlayer)localPlayer);
        float colorA = this.playersUIColor.a;
        float colorR = this.playersUIColor.r;
        float colorG = this.playersUIColor.g;
        float colorB = this.playersUIColor.b;
        if (players == null && players.isEmpty()) {
            return;
        }
        for (IsoPlayer player : players) {
            float playerPosX = PlayerUtils.getScreenPositionX((IsoPlayer)player);
            float playerPosY = PlayerUtils.getScreenPositionY((IsoPlayer)player);
            if (player.isLocalPlayer() && !this.isVisualDrawToLocalPlayer) continue;
            if (this.isVisualDrawPlayerNickname) {
                Rendering.drawTextCenterWithShadow((String)player.getUsername(), (UIFont)UIFont.Small, (float)playerPosX, (float)(playerPosY - 30.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);
            }
            if (this.isVisualDrawPlayerInfo) {
                String firstHandItem = player.getPrimaryHandItem() != null ? player.getPrimaryHandItem().getDisplayName() : "None";
                String secondHandItem = player.getSecondaryHandItem() != null ? player.getSecondaryHandItem().getDisplayName() : "None";
                Rendering.drawTextCenterWithShadow((String)(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_PrimaryHand") + firstHandItem), (UIFont)UIFont.Small, (float)playerPosX, (float)(playerPosY + 70.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);
                Rendering.drawTextCenterWithShadow((String)(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_SecondaryHand") + secondHandItem), (UIFont)UIFont.Small, (float)playerPosX, (float)(playerPosY + 80.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);
            }
            if (player.isLocalPlayer() || !this.isVisualDrawLineToPlayers) continue;
            int distance = (int)PlayerUtils.getDistanceBetweenPlayers((IsoPlayer)player, (IsoPlayer)localPlayer);
            int textDistance = distance;
            float totalLength = (float)Math.sqrt(Math.pow(playerPosX - posLocalPlayerX, 2.0) + Math.pow(playerPosY - posLocalPlayerY, 2.0));
            float ratio = (float)textDistance / totalLength;
            float textPosX = posLocalPlayerX + ratio * (playerPosX - posLocalPlayerX);
            float textPosY = posLocalPlayerY + ratio * (playerPosY - posLocalPlayerY);
            Rendering.drawLine((int)((int)playerPosX), (int)((int)playerPosY), (int)((int)posLocalPlayerX), (int)((int)posLocalPlayerY), (float)colorR, (float)colorG, (float)colorB, (float)0.8f, (int)1);
            Rendering.drawTextCenterWithShadow((String)String.valueOf(distance), (UIFont)UIFont.Small, (float)textPosX, (float)textPosY, (float)colorR, (float)colorG, (float)colorB, (float)colorA);
        }
    }

    @SubscribeLuaEvent(eventName="OnRenderTick")
    public void updateAPI() {
        try
        {
            this.bypassDebugMode();
            this.updateLocalPlayerFeatures();
        }
        catch(Exception e)
        {}
    }
}
