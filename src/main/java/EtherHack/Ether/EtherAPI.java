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
            this.updatePlayersVisuals();
            this.updateVehiclesVisuals();
            this.updateZombiesVisuals();
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

    private void updateVehiclesVisuals()
    {
        if (!this.isVisualsEnable || !this.isVisualsVehiclesEnable)
            return;

        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null)
            return;

        ArrayList<BaseVehicle> vehicles = IsoWorld.instance.getCell().getVehicles();
        float posLocalPlayerX = PlayerUtils.getScreenPositionX((IsoPlayer)localPlayer);
        float posLocalPlayerY = PlayerUtils.getScreenPositionY((IsoPlayer)localPlayer);
        float colorA = this.vehicleUIColor.a;
        float colorR = this.vehicleUIColor.r;
        float colorG = this.vehicleUIColor.g;
        float colorB = this.vehicleUIColor.b;

        if (vehicles == null && vehicles.isEmpty())
            return;

        for (BaseVehicle vehicle : vehicles)
        {
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
        if (!this.isVisualsEnable || !this.isVisualsZombiesEnable)
            return;
    
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null)
            return;

        ArrayList<IsoZombie> zombies = IsoWorld.instance.getCell().getZombieList();
        float colorA = this.zombieUIColor.a;
        float colorR = this.zombieUIColor.r;
        float colorG = this.zombieUIColor.g;
        float colorB = this.zombieUIColor.b;

        if (zombies == null && zombies.isEmpty())
            return;

        for (IsoZombie zombie : zombies)
        {
            float posX = ZombieUtils.getScreenPositionX((IsoZombie)zombie);
            float posY = ZombieUtils.getScreenPositionY((IsoZombie)zombie);
            int health = (int)(zombie.getHealth() * 100.0f);
            Rendering.drawTextCenterWithShadow((String)EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_ZombieTitle"), (UIFont)UIFont.Small, (float)posX, (float)posY, (float)colorR, (float)colorG, (float)colorB, (float)colorA);
            Rendering.drawTextCenterWithShadow((String)(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_ZombieHealth") + health), (UIFont)UIFont.Small, (float)posX, (float)(posY + 10.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);
        }
    }

    private void updatePlayersVisuals()
    {
        if (!this.isVisualsEnable || !this.isVisualsPlayersEnable)
            return;
            
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null)
            return;

        ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
        float posLocalPlayerX = PlayerUtils.getScreenPositionX((IsoPlayer)localPlayer);
        float posLocalPlayerY = PlayerUtils.getScreenPositionY((IsoPlayer)localPlayer);
        float colorA = this.playerUIColor.a;
        float colorR = this.playerUIColor.r;
        float colorG = this.playerUIColor.g;
        float colorB = this.playerUIColor.b;
        if (players == null && players.isEmpty())
            return;

        for (IsoPlayer player : players)
        {
            float playerPosX = PlayerUtils.getScreenPositionX((IsoPlayer)player);
            float playerPosY = PlayerUtils.getScreenPositionY((IsoPlayer)player);
            if (player.isLocalPlayer() && !this.isVisualDrawToLocalPlayer)
                continue;
            
            if (this.isVisualDrawPlayerNickname)
                Rendering.drawTextCenterWithShadow((String)player.getUsername(), (UIFont)UIFont.Small, (float)playerPosX, (float)(playerPosY - 30.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);

            if (this.isVisualDrawPlayerInfo)
            {
                String firstHandItem = player.getPrimaryHandItem() != null ? player.getPrimaryHandItem().getDisplayName() : "None";
                String secondHandItem = player.getSecondaryHandItem() != null ? player.getSecondaryHandItem().getDisplayName() : "None";
                Rendering.drawTextCenterWithShadow((String)(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_PrimaryHand") + firstHandItem), (UIFont)UIFont.Small, (float)playerPosX, (float)(playerPosY + 70.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);
                Rendering.drawTextCenterWithShadow((String)(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_SecondaryHand") + secondHandItem), (UIFont)UIFont.Small, (float)playerPosX, (float)(playerPosY + 80.0f), (float)colorR, (float)colorG, (float)colorB, (float)colorA);
            }
            
            if (player.isLocalPlayer() || !this.isVisualDrawLineToPlayers)
                continue;

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
