package EtherHack.Ether;

import EtherHack.Ether.EtherLuaCompiler;
import EtherHack.Ether.EtherMain;
import EtherHack.utils.Logger;
import EtherHack.utils.PlayerUtils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.network.packets.PlayerPacket;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;

public final class EtherLuaMethods {
    @LuaMethod(name="getZombieUIColor",global=true)public static Color getZombieUIColor(){return EtherMain.getInstance().etherAPI.colors[0];}
    @LuaMethod(name="setZombieUIColor",global=true)public static void setZombieUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[0] = new Color(r, g, b);}
    
    @LuaMethod(name="getVehicleUIColor",global=true)public static Color getVehicleUIColor(){return EtherMain.getInstance().etherAPI.colors[1];}
    @LuaMethod(name="setVehicleUIColor",global=true)public static void setVehicleUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[1] = new Color(r, g, b);}
    
    @LuaMethod(name="getPlayerUIColor",global=true)public static Color getPlayerUIColor(){return EtherMain.getInstance().etherAPI.colors[2];}
    @LuaMethod(name="setPlayerUIColor",global=true)public static void setPlayerUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[2] = new Color(r, g, b);}
    
    @LuaMethod(name="getSurvivorUIColor",global=true)public static Color getSurvivorUIColor(){return EtherMain.getInstance().etherAPI.colors[3];}
    @LuaMethod(name="setSurvivorUIColor",global=true)public static void setSurvivorUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[3] = new Color(r, g, b);}
    
    @LuaMethod(name="getRemoteSurvivorUIColor",global=true)public static Color getRemoteSurvivorUIColor(){return EtherMain.getInstance().etherAPI.colors[4];}
    @LuaMethod(name="setRemoteSurvivorUIColor",global=true)public static void setRemoteSurvivorUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[4] = new Color(r, g, b);}

    @LuaMethod(name="getItemUIColor",global=true)public static Color getItemUIColor(){return EtherMain.getInstance().etherAPI.colors[5];}
    @LuaMethod(name="setItemUIColor",global=true)public static void setItemUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[5] = new Color(r, g, b);}

    @LuaMethod(name="getWorldItemUIColor",global=true)public static Color getWorldItemUIColor(){return EtherMain.getInstance().etherAPI.colors[6];}
    @LuaMethod(name="setWorldItemUIColor",global=true)public static void setWorldItemUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[6] = new Color(r, g, b);}

    @LuaMethod(name="getRoomUIColor",global=true)public static Color getRoomUIColor(){return EtherMain.getInstance().etherAPI.colors[7];}
    @LuaMethod(name="setRoomUIColor",global=true)public static void setRoomUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[7] = new Color(r, g, b);}
    
    @LuaMethod(name="getBuildingUIColor",global=true)public static Color getBuildingUIColor(){return EtherMain.getInstance().etherAPI.colors[8];}
    @LuaMethod(name="setBuildingUIColor",global=true)public static void setBuildingUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[8] = new Color(r, g, b);}
    
    @LuaMethod(name="getPushableUIColor",global=true)public static Color getPushableUIColor(){return EtherMain.getInstance().etherAPI.colors[9];}
    @LuaMethod(name="setPushableUIColor",global=true)public static void setPushableUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[9] = new Color(r, g, b);}
    
    @LuaMethod(name="getAccentUIColor",global=true)public static Color getAccentUIColor(){return EtherMain.getInstance().etherAPI.colors[10];}
    @LuaMethod(name="setAccentUIColor",global=true)public static void setAccentUIColor(float r, float g, float b){EtherMain.getInstance().etherAPI.colors[10] = new Color(r, g, b);}
    
    @LuaMethod(name="deleteConfig", global=true)
    public static void deleteConfig(String configName) {
        Path configFilePath = Paths.get("EtherHack/config/" + configName + ".properties", new String[0]);
        try {
            Files.deleteIfExists(configFilePath);
        }
        catch (IOException e) {
            Logger.printLog((String)("The file '" + configName + "' does not exist. Deletion canceled. Exception: " + e.getMessage()));
        }
    }

    @LuaMethod(name="getConfigList", global=true)
    public static ArrayList<String> getConfigList() {
        ArrayList<String> configFiles = new ArrayList<String>();
        try
        {
            Path configFolderPath = Paths.get("EtherHack/config", new String[0]);
            List<Path> fileList = Files.list(configFolderPath).filter(file -> file.toString().endsWith(".properties")).toList();
            for (Path filePath : fileList) configFiles.add(filePath.getFileName().toString().replace(".properties", ""));
        }
        catch (IOException e)
        {
            Logger.printLog((String)("An error occurred while getting the list of config files: " + e));
            return null;
        }
        return configFiles;
    }

    @LuaMethod(name="loadConfig", global=true)
    public static void loadConfig(String configName) {
        EtherMain.getInstance().etherAPI.loadConfig(configName);
    }

    @LuaMethod(name="saveConfig", global=true)
    public static void saveConfig(String configName) {
        EtherMain.getInstance().etherAPI.saveConfig(configName);
    }

    @LuaMethod(name="safePlayerTeleport", global=true)
    public static void safePlayerTeleport(int x, int y) {
        EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = true;
        IsoPlayer player = IsoPlayer.getInstance();
        float z = player.z;
        float deltaX = (float)x - player.x;
        float deltaY = (float)y - player.y;
        float deltaZ = z - player.z;
        float remX = Math.abs(deltaX);
        float remY = Math.abs(deltaY);
        float remZ = Math.abs(deltaZ);
        float maxSpeed = 10.0f;
        float deltaTime = 0.1f;
        while (remX > 0.0f || remY > 0.0f || remZ > 0.0f) {
            float maxMove = maxSpeed * deltaTime;
            float moveX = Math.min(Math.min(remX, maxMove), 1.0f);
            float moveY = Math.min(Math.min(remY, maxMove), 1.0f);
            float moveZ = Math.min(Math.min(remZ, maxMove), 1.0f);
            remX -= moveX;
            remY -= moveY;
            remZ -= moveZ;
            if (deltaX < 0.0f) {
                moveX = -moveX;
            }
            if (deltaY < 0.0f) {
                moveY = -moveY;
            }
            if (deltaZ < 0.0f) {
                moveZ = -moveZ;
            }
            player.setX(player.x + moveX);
            player.setY(player.y + moveY);
            player.setZ(player.z + moveZ);
            player.setLx(player.getX());
            player.setLy(player.getY());
            player.setLz(player.getZ());
            GameClient.instance.sendPlayer(player);
            if (GameClient.connection == null || !PlayerPacket.l_send.playerPacket.set(player)) continue;
            ByteBufferWriter writer = GameClient.connection.startPacket();
            PacketTypes.PacketType.PlayerUpdateReliable.doPacket(writer);
            PlayerPacket.l_send.playerPacket.write(writer);
            PacketTypes.PacketType.PlayerUpdateReliable.send(GameClient.connection);
        }
        EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = false;
    }

    @LuaMethod(name="isPlayerInSafeTeleported", global=true)
    public static boolean isPlayerInSafeTeleported() {
        return EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported;
    }

    @LuaMethod(name="learnAllRecipes", global=true)
    public static void learnAllRecipes() {
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) {
            return;
        }
        ArrayList<Recipe> recipesList = ScriptManager.instance.getAllRecipes();
        if (recipesList != null) {
            for (Recipe recipe : recipesList) {
                if (recipe.getOriginalname() == null) continue;
                localPlayer.learnRecipe(recipe.getOriginalname());
            }
        }
    }

    @LuaMethod(name="giveItem", global=true)
    public static void giveItem(InventoryItem itemID, int amount) {
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) {
            return;
        }
        for (int i = 0; i < amount; ++i) {
            localPlayer.getInventory().AddItem(itemID);
        }
    }

    @LuaMethod(name="giveItem", global=true)
    public static void giveItem(String itemID, int amount) {
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) {
            return;
        }
        for (int i = 0; i < amount; ++i) {
            localPlayer.getInventory().AddItem(itemID);
        }
    }
    
    @LuaMethod(name="getTranslate",global=true)public static String getTranslate(String key, KahluaTable args){return EtherMain.getInstance().etherTranslator.getTranslate(key, args);}
    @LuaMethod(name="getTranslate",global=true)public static String getTranslate(String key){return EtherMain.getInstance().etherTranslator.getTranslate(key);}
    @LuaMethod(name="getAntiCheat12Status",global=true)public static boolean getAntiCheat12Status() {return ServerOptions.instance.getBoolean("AntiCheatProtectionType12");}
    @LuaMethod(name="getAntiCheat8Status",global=true)public static boolean getAntiCheat8Status() {return ServerOptions.instance.getBoolean("AntiCheatProtectionType8");}
    @LuaMethod(name="getDistanceBetweenPlayers",global=true)public static float getDistanceBetweenPlayers(IsoPlayer player1, IsoPlayer player2) {return PlayerUtils.getDistanceBetweenPlayers((IsoPlayer)player1, (IsoPlayer)player2);}
    
    @LuaMethod(name="isBlockCompileLuaWithBadWords",global=true)public static boolean isBlockCompileLuaWithBadWords(){return EtherLuaCompiler.getInstance().isBlockCompileLuaWithBadWords;}
    @LuaMethod(name="toggleBlockCompileLuaWithBadWords",global=true)public static void toggleBlockCompileLuaWithBadWords(boolean isToggled){EtherLuaCompiler.getInstance().isBlockCompileLuaWithBadWords = isToggled;}
    
    @LuaMethod(name="isBlockCompileLuaAboutEtherHack",global=true)public static boolean isBlockCompileLuaAboutEtherHack(){return EtherLuaCompiler.getInstance().isBlockCompileLuaAboutEtherHack;}
    @LuaMethod(name="toggleBlockCompileLuaAboutEtherHack",global=true)public static void toggleBlockCompileLuaAboutEtherHack(boolean isToggled){EtherLuaCompiler.getInstance().isBlockCompileLuaAboutEtherHack = isToggled;}

    @LuaMethod(name="isBlockCompileDefaultLua",global=true)public static boolean isBlockCompileDefaultLua(){return EtherLuaCompiler.getInstance().isBlockCompileDefaultLua;}
    @LuaMethod(name="toggleBlockCompileDefaultLua",global=true)public static void toggleBlockCompileDefaultLua(boolean isToggled){EtherLuaCompiler.getInstance().isBlockCompileDefaultLua = isToggled;}

    @LuaMethod(name="isAlwaysRack",global=true)public static boolean isAlwaysRack(){return EtherMain.getInstance().etherAPI.toggles[0];}
    @LuaMethod(name="toggleAlwaysRack",global=true)public static void toggleAlwaysRack(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[0] = isToggled;}

    @LuaMethod(name="isAlwaysRoundChamber",global=true)public static boolean isAlwaysRoundChamber(){return EtherMain.getInstance().etherAPI.toggles[1];}
    @LuaMethod(name="toggleAlwaysRoundChamber",global=true)public static void toggleAlwaysRoundChamber(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[1] = isToggled;}

    @LuaMethod(name="isAlwaysRepaired", global=true)public static boolean isAlwaysRepaired() {return EtherMain.getInstance().etherAPI.toggles[2];}
    @LuaMethod(name="toggleAlwaysRepaired", global=true)public static void toggleAlwaysRepaired(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[2] = isToggled;}

    @LuaMethod(name="isAlwaysKnockdown",global=true)public static boolean isAlwaysKnockdown(){return EtherMain.getInstance().etherAPI.toggles[3];}
    @LuaMethod(name="toggleAlwaysKnockdown",global=true)public static void toggleAlwaysKnockdown(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[3] = isToggled;}

    @LuaMethod(name="isAlwaysAiming",global=true)public static boolean isAlwaysAiming(){return EtherMain.getInstance().etherAPI.toggles[4];}
    @LuaMethod(name="toggleAlwaysAiming",global=true)public static void toggleAlwaysAiming(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[4] = isToggled;}

    @LuaMethod(name="isAlwaysCritical",global=true)public static boolean isAlwaysCritical(){return EtherMain.getInstance().etherAPI.toggles[5];}
    @LuaMethod(name="toggleAlwaysCritical",global=true)public static void toggleAlwaysCritical(boolean isToggled) {EtherMain.getInstance().etherAPI.toggles[5] = isToggled;}

    @LuaMethod(name="isEnableInvisible",global=true)public static boolean isEnableInvisible(){return EtherMain.getInstance().etherAPI.toggles[6];}
    @LuaMethod(name="toggleInvisible",global=true)public static void toggleInvisible(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[6] = isToggled;}

    @LuaMethod(name="isZombieDontAttack",global=true)public static boolean isZombieDontAttack(){return EtherMain.getInstance().etherAPI.toggles[7];}
    @LuaMethod(name="toggleZombieDontAttack",global=true)public static void toggleZombieDontAttack(boolean isToggled) {EtherMain.getInstance().etherAPI.toggles[7] = isToggled;}

    @LuaMethod(name="isEnableNoclip",global=true)public static boolean isEnableNoclip(){return EtherMain.getInstance().etherAPI.toggles[8];}
    @LuaMethod(name="toggleNoclip",global=true)public static void toggleNoclip(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[9] = isToggled;}

    @LuaMethod(name="isEnableGodMode",global=true)public static boolean isEnableGodMode(){return EtherMain.getInstance().etherAPI.toggles[9];}
    @LuaMethod(name="toggleGodMode",global=true)public static void toggleGodMode(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[9] = isToggled;}

    @LuaMethod(name="isEnableNightVision",global=true)public static boolean isEnableNightVision(){return EtherMain.getInstance().etherAPI.toggles[10];}
    @LuaMethod(name="toggleNightVision",global=true)public static void toggleNightVision(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[10] = isToggled;}

    @LuaMethod(name="isNoRecoil",global=true)public static boolean isNoRecoil(){return EtherMain.getInstance().etherAPI.toggles[11];}
    @LuaMethod(name="toggleNoRecoil",global=true)public static void toggleNoRecoil(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[11] = isToggled;}

    @LuaMethod(name="isNoReload", global=true)public static boolean isNoReload(){return EtherMain.getInstance().etherAPI.toggles[12];}
    @LuaMethod(name="toggleNoReload", global=true)public static void toggleNoReload(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[12] = isToggled;}

    @LuaMethod(name="isNoJam",global=true)public static boolean isNoJam(){return EtherMain.getInstance().etherAPI.toggles[13];}
    @LuaMethod(name="toggleNoJam",global=true)public static void toggleNoJam(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[13] = isToggled;}

    @LuaMethod(name="isNoSpentRoundChamber",global=true)public static boolean isNoSpentRoundChamber(){return EtherMain.getInstance().etherAPI.toggles[14];}
    @LuaMethod(name="toggleNoSpentRoundChamber",global=true)public static void toggleNoSpentRoundChamber(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[14] = isToggled;}

    @LuaMethod(name="isNoBroken",global=true)public static boolean isNoBroken(){return EtherMain.getInstance().etherAPI.toggles[15];}
    @LuaMethod(name="toggleNoBroken",global=true)public static void toggleNoBroken(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[15] = isToggled;}

    @LuaMethod(name="isNoInfected",global=true)public static boolean isNoInfected(){return EtherMain.getInstance().etherAPI.toggles[16];}
    @LuaMethod(name="toggleNoInfected",global=true)public static void toggleNoInfected(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[16] = isToggled;}

    @LuaMethod(name="isNoWet",global=true)public static boolean isNoWet(){return EtherMain.getInstance().etherAPI.toggles[17];}
    @LuaMethod(name="toggleNoWet",global=true)public static void toggleNoWet(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[17] = isToggled;}

    @LuaMethod(name="isNoHoled",global=true)public static boolean isNoHoled(){return EtherMain.getInstance().etherAPI.toggles[18];}
    @LuaMethod(name="toggleNoHoled",global=true)public static void toggleNoHoled(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[18] = isToggled;}

    @LuaMethod(name="isNoDirted",global=true)public static boolean isNoDirted(){return EtherMain.getInstance().etherAPI.toggles[19];}
    @LuaMethod(name="toggleNoDirted",global=true) public static void toggleNoDirted(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[19] = isToggled;}

    @LuaMethod(name="isNoBlooded",global=true)public static boolean isNoBlooded(){return EtherMain.getInstance().etherAPI.toggles[20];}
    @LuaMethod(name="toggleNoBlooded",global=true)public static void toggleNoBlooded(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[20] = isToggled;}

    @LuaMethod(name="isTimedActionCheat",global=true)public static boolean isTimedActionCheat(){return EtherMain.getInstance().etherAPI.toggles[21];}
    @LuaMethod(name="toggleTimedActionCheat",global=true)public static void toggleTimedActionCheat(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[21] = isToggled;}

    @LuaMethod(name="isMultiHitZombies",global=true) public static boolean isMultiHitZombies(){return EtherMain.getInstance().etherAPI.toggles[22];}
    @LuaMethod(name="toggleMultiHitZombies",global=true)public static void toggleMultiHitZombies(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[22] = isToggled;}

    @LuaMethod(name="isUnlimitedCondition",global=true)public static boolean isUnlimitedCondition(){return EtherMain.getInstance().etherAPI.toggles[23];}
    @LuaMethod(name="toggleUnlimitedCondition",global=true)public static void toggleUnlimitedCondition(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[23] = isToggled;}

    @LuaMethod(name="isVisualEnable360Vision", global=true)public static boolean isVisualEnable360Vision(){return EtherMain.getInstance().etherAPI.toggles[24];}
    @LuaMethod(name="toggleVisualEnable360Vision", global=true)public static void toggleVisualEnable360Vision(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[24] = isToggled;}
    
    @LuaMethod(name="isMapDrawZombies",global=true)public static boolean isMapDrawZombies(){return EtherMain.getInstance().etherAPI.toggles[27];}
    @LuaMethod(name="toggleMapDrawZombies",global=true)public static void toggleMapDrawZombies(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[27] = isToggled;}
    
    @LuaMethod(name="isMapDrawVehicles",global=true)public static boolean isMapDrawVehicles(){return EtherMain.getInstance().etherAPI.toggles[28];}
    @LuaMethod(name="toggleMapDrawVehicles",global=true)public static void toggleMapDrawVehicles(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[28] = isToggled;}

    @LuaMethod(name="isMapDrawAllPlayers",global=true)public static boolean isMapDrawAllPlayers(){return EtherMain.getInstance().etherAPI.toggles[29];}
    @LuaMethod(name="toggleMapDrawAllPlayers",global=true)public static void toggleMapDrawAllPlayers(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[29] = isToggled;}
    
    @LuaMethod(name="isMapDrawLocalPlayer",global=true)public static boolean isMapDrawLocalPlayer(){return EtherMain.getInstance().etherAPI.toggles[30];}
    @LuaMethod(name="toggleMapDrawLocalPlayer",global=true)public static void toggleMapDrawLocalPlayer(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[30] = isToggled;}
    
    @LuaMethod(name="isMapDrawSurvivors",global=true)public static boolean isMapDrawSurvivors(){return EtherMain.getInstance().etherAPI.toggles[31];}
    @LuaMethod(name="toggleMapDrawSurvivors",global=true)public static void toggleMapDrawSurvivors(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[31] = isToggled;}
    
    @LuaMethod(name="isMapDrawRemoteSurvivors",global=true)public static boolean isMapDrawRemoteSurvivors(){return EtherMain.getInstance().etherAPI.toggles[32];}
    @LuaMethod(name="toggleMapDrawRemoteSurvivors",global=true)public static void toggleMapDrawRemoteSurvivors(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[32] = isToggled;}
    
    @LuaMethod(name="isMapDrawPushables",global=true)public static boolean isMapDrawPushables(){return EtherMain.getInstance().etherAPI.toggles[33];}
    @LuaMethod(name="toggleMapDrawPushables",global=true)public static void toggleMapDrawPushables(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[33] = isToggled;}
    
    @LuaMethod(name="isMapDrawBuildings",global=true)public static boolean isMapDrawBuildings(){return EtherMain.getInstance().etherAPI.toggles[34];}
    @LuaMethod(name="toggleMapDrawBuildings",global=true)public static void toggleMapDraw(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[34] = isToggled;}
    
    @LuaMethod(name="isMapDrawItems",global=true)public static boolean isMapDrawItems(){return EtherMain.getInstance().etherAPI.toggles[35];}
    @LuaMethod(name="toggleMapDrawItems",global=true)public static void toggleMapDrawItems(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[35] = isToggled;}
    
    @LuaMethod(name="isMapDrawWorldItems",global=true)public static boolean isMapDrawWorldItems(){return EtherMain.getInstance().etherAPI.toggles[36];}
    @LuaMethod(name="toggleMapDrawWorldItems",global=true)public static void toggleMapDrawWorldItems(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[36] = isToggled;}

    @LuaMethod(name="isMapDrawRooms",global=true)public static boolean isMapDrawRooms(){return EtherMain.getInstance().etherAPI.toggles[38];}
    @LuaMethod(name="toggleMapDrawRooms",global=true)public static void toggleMapDrawRooms(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[38] = isToggled;}
    
    @LuaMethod(name="isBypassDebugMode",global=true)public static boolean isBypassDebugMode(){return EtherMain.getInstance().etherAPI.toggles[39];}
    @LuaMethod(name="toggleBypassDebugMode",global=true)public static void toggleBypassDebugMode(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[39] = isToggled;}
    
    @LuaMethod(name="toggleUnlimitedEndurance",global=true)public static void toggleUnlimitedEndurance(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[40] = isToggled;}
    @LuaMethod(name="isUnlimitedEndurance",global=true)public static boolean isUnlimitedEndurance(){return EtherMain.getInstance().etherAPI.toggles[40];}
    
    @LuaMethod(name="toggleUnlimitedAmmo",global=true)public static void toggleUnlimitedAmmo(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[41] = isToggled;}
    @LuaMethod(name="isUnlimitedAmmo",global=true)public static boolean isUnlimitedAmmo(){return EtherMain.getInstance().etherAPI.toggles[41];}
    
    @LuaMethod(name="toggleDisableFatigue",global=true)public static void toggleDisableFatigue(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[42] = isToggled;}
    @LuaMethod(name="isDisableFatigue",global=true) public static boolean isDisableFatigue(){return EtherMain.getInstance().etherAPI.toggles[42];}

    @LuaMethod(name="toggleDisableHunger",global=true)public static void toggleDisableHunger(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[43] = isToggled;}
    @LuaMethod(name="isDisableHunger",global=true)public static boolean isDisableHunger(){return EtherMain.getInstance().etherAPI.toggles[43];}

    @LuaMethod(name="toggleDisableThirst",global=true)public static void toggleDisableThirst(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[44] = isToggled;}
    @LuaMethod(name="isDisableThirst",global=true)public static boolean isDisableThirst(){return EtherMain.getInstance().etherAPI.toggles[44];}

    @LuaMethod(name="toggleDisableDrunkenness",global=true)public static void toggleDisableDrunkenness(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[45] = isToggled;}
    @LuaMethod(name="isDisableDrunkenness",global=true)public static boolean isDisableDrunkenness(){return EtherMain.getInstance().etherAPI.toggles[45];}

    @LuaMethod(name="toggleDisableAnger",global=true)public static void toggleDisableAnger(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[46] = isToggled;}
    @LuaMethod(name="isDisableAnger",global=true)public static boolean isDisableAnger(){return EtherMain.getInstance().etherAPI.toggles[46];}

    @LuaMethod(name="toggleDisableFear",global=true) public static void toggleDisableFear(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[47] = isToggled;}
    @LuaMethod(name="isDisableFear",global=true)public static boolean isDisableFear(){return EtherMain.getInstance().etherAPI.toggles[47];}

    @LuaMethod(name="toggleDisablePain",global=true)public static void toggleDisablePain(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[48] = isToggled;}
    @LuaMethod(name="isDisablePain",global=true)public static boolean isDisablePain(){return EtherMain.getInstance().etherAPI.toggles[48];}

    @LuaMethod(name="toggleDisablePanic",global=true)public static void toggleDisablePanic(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[49] = isToggled;}
    @LuaMethod(name="isDisablePanic",global=true)public static boolean isDisablePanic(){return EtherMain.getInstance().etherAPI.toggles[49];}

    @LuaMethod(name="toggleDisableMorale",global=true)public static void toggleDisableMorale(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[50] = isToggled;}
    @LuaMethod(name="isDisableMorale",global=true)public static boolean isDisableMorale(){return EtherMain.getInstance().etherAPI.toggles[50];}

    @LuaMethod(name="toggleDisableStress",global=true)public static void toggleDisableStress(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[51] = isToggled;}
    @LuaMethod(name="isDisableStress",global=true)public static boolean isDisableStress(){return EtherMain.getInstance().etherAPI.toggles[51];}

    @LuaMethod(name="toggleDisableSickness",global=true)public static void toggleDisableSickness(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[52] = isToggled;}
    @LuaMethod(name="isDisableSickness",global=true) public static boolean isDisableSickness(){return EtherMain.getInstance().etherAPI.toggles[52];}

    @LuaMethod(name="toggleDisableStressFromCigarettes",global=true)public static void toggleDisableStressFromCigarettes(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[53] = isToggled;}
    @LuaMethod(name="isDisableStressFromCigarettes",global=true)public static boolean isDisableStressFromCigarettes(){return EtherMain.getInstance().etherAPI.toggles[53];}

    @LuaMethod(name="toggleDisableSanity",global=true)public static void toggleDisableSanity(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[54] = isToggled;}
    @LuaMethod(name="isDisableSanity",global=true)public static boolean isDisableSanity(){return EtherMain.getInstance().etherAPI.toggles[54];}

    @LuaMethod(name="toggleDisableBoredomLevel",global=true)public static void toggleDisableBoredomLevel(boolean isToggled) {EtherMain.getInstance().etherAPI.toggles[55] = isToggled;}
    @LuaMethod(name="isDisableBoredomLevel",global=true)public static boolean isDisableBoredomLevel(){return EtherMain.getInstance().etherAPI.toggles[61];}

    @LuaMethod(name="toggleDisableUnhappynessLevel",global=true)public static void toggleDisableUnhappynessLevel(boolean isToggled) {EtherMain.getInstance().etherAPI.toggles[56] = isToggled;}
    @LuaMethod(name="isDisableUnhappynessLevel",global=true)public static boolean isDisableUnhappynessLevel(){return EtherMain.getInstance().etherAPI.toggles[56];}

    @LuaMethod(name="toggleDisableWetness",global=true)public static void toggleDisableWetness(boolean isToggled) {EtherMain.getInstance().etherAPI.toggles[57] = isToggled;}
    @LuaMethod(name="isDisableWetness",global=true)public static boolean isDisableWetness(){return EtherMain.getInstance().etherAPI.toggles[57];}

    @LuaMethod(name="toggleDisableInfectionLevel",global=true)public static void toggleDisableInfectionLevel(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[58] = isToggled;}
    @LuaMethod(name="isDisableInfectionLevel",global=true)public static boolean isDisableInfectionLevel(){return EtherMain.getInstance().etherAPI.toggles[58];}

    @LuaMethod(name="toggleDisableFakeInfectionLevel",global=true)public static void toggleDisableFakeInfectionLevel(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[59] = isToggled;}
    @LuaMethod(name="isDisableFakeInfectionLevel",global=true)public static boolean isDisableFakeInfectionLevel(){return EtherMain.getInstance().etherAPI.toggles[59];}

    @LuaMethod(name="isDisableFire",global=true)public static boolean isDisableFire(){return EtherMain.getInstance().etherAPI.toggles[60];}
    @LuaMethod(name="toggleDisableFire",global=true)public static void toggleDisableFire(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[60] = isToggled;}

    @LuaMethod(name="isOptimalCalories",global=true)public static boolean isOptimalCalories(){return EtherMain.getInstance().etherAPI.toggles[61];}
    @LuaMethod(name="toggleOptimalCalories",global=true)public static void toggleOptimalCalories(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[61] = isToggled;}

    @LuaMethod(name="isOptimalWeight",global=true)public static boolean isOptimalWeight(){return EtherMain.getInstance().etherAPI.toggles[62];}
    @LuaMethod(name="toggleOptimalWeight",global=true)public static void toggleOptimalWeight(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[62] = isToggled;}

    @LuaMethod(name="isEnableUnlimitedCarry",global=true)public static boolean isEnableUnlimitedCarry(){return EtherMain.getInstance().etherAPI.toggles[63];}
    @LuaMethod(name="toggleEnableUnlimitedCarry",global=true)public static void toggleEnableUnlimitedCarry(boolean isToggled){EtherMain.getInstance().etherAPI.toggles[63] = isToggled;}


    @LuaMethod(name="requireExtra", global=true)
    public static void requireExtra(String path) {
        Object fixedPath;
        Object object = fixedPath = path.endsWith(".lua") ? path : path + ".lua";
        if (!EtherMain.getInstance().etherLuaManager.luaFilesList.contains(fixedPath)) {
            EtherMain.getInstance().etherLuaManager.luaFilesList.add((String)fixedPath);
        }
        Path p = Paths.get((String)fixedPath, new String[0]);
        String filename = p.getFileName().toString();
        filename = filename.substring(0, filename.lastIndexOf("."));
        EtherLuaCompiler.getInstance().addWordToBlacklistLuaCompiler(filename);
        EtherLuaCompiler.getInstance().addPathToWhiteListLuaCompiler((String)fixedPath);
        LuaManager.RunLua((String)fixedPath);
    }

    @LuaMethod(name="getExtraTexture", global=true)
    public static Texture getExtraTexture(String relativePath) {
        if (!relativePath.endsWith(".png")) {
            Logger.printLog((String)"Incorrect path to the image file. Required .png");
            return null;
        }
        HashMap<String, Texture> textureCache = EtherMain.getInstance().etherAPI.textureCache;
        if (textureCache.containsKey(relativePath)) {
            return textureCache.get(relativePath);
        }
        try {
            FileInputStream fis = new FileInputStream(Paths.get(relativePath, new String[0]).toFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            Texture texture = new Texture(relativePath, bis, false);
            textureCache.put(relativePath, texture);
            return texture;
        }
        catch (Exception e) {
            Logger.printLog((String)("Error when reading the image: " + e));
            return null;
        }
    }

    @LuaMethod(name="hackAdminAccess", global=true)
    public static void hackAdminAccess() {
        for (IsoPlayer p : GameClient.instance.getPlayers()) {
            if (!p.isLocalPlayer()) continue;
            p.accessLevel = "admin";
            p.accessLevel.equals("admin");
        }
    }
}
