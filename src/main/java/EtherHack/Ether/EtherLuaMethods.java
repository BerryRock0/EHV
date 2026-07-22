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
    @LuaMethod(name="getZombieUIColor", global=true)
    public static Color getZombieUIColor() {
        return EtherMain.getInstance().etherAPI.zombiesUIColor;
    }

    @LuaMethod(name="setZombieUIColor", global=true)
    public static void setZombieUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.zombiesUIColor = new Color(r, g, b);
    }

    @LuaMethod(name="getVehicleUIColor", global=true)
    public static Color getVehicleUIColor() {
        return EtherMain.getInstance().etherAPI.vehiclesUIColor;
    }

    @LuaMethod(name="setVehicleUIColor", global=true)
    public static void setVehicleUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.vehiclesUIColor = new Color(r, g, b);
    }

    @LuaMethod(name="getPlayersUIColor", global=true)
    public static Color getPlayersUIColor() {
        return EtherMain.getInstance().etherAPI.playersUIColor;
    }

    @LuaMethod(name="setPlayersUIColor", global=true)
    public static void setPlayersUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.playersUIColor = new Color(r, g, b);
    }

    @LuaMethod(name="getSurvivorUIColor", global=true)
    public static Color getSurvivorUIColor() {
        return EtherMain.getInstance().etherAPI.survivorsUIColor;
    }

    @LuaMethod(name="setSurvivorUIColor", global=true)
    public static void setSurvivorUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.survivorsUIColor = new Color(r, g, b);
    }

    @LuaMethod(name="getRemoteSurvivorUIColor", global=true)
    public static Color getRemoteSurvivorUIColor() {
        return EtherMain.getInstance().etherAPI.remoteSurvivorsUIColor;
    }

    @LuaMethod(name="setRemoteSurvivorUIColor", global=true)
    public static void setRemoteSurvivorUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.remoteSurvivorsUIColor = new Color(r, g, b);
    }

    @LuaMethod(name="getRoomUIColor", global=true)
    public static Color getRoomUIColor() {
        return EtherMain.getInstance().etherAPI.roomsUIColor;
    }

    @LuaMethod(name="setRoomUIColor", global=true)
    public static void setRoomUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.roomsUIColor = new Color(r, g, b);
    }

    @LuaMethod(name="getBuildingUIColor", global=true)
    public static Color getBuildingUIColor() {
        return EtherMain.getInstance().etherAPI.buildingsUIColor;
    }

    @LuaMethod(name="setBuildingUIColor", global=true)
    public static void setBuildingUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.buildingsUIColor = new Color(r, g, b);
    }
    
    @LuaMethod(name="getPushableUIColor", global=true)
    public static Color getPushableUIColor() {
        return EtherMain.getInstance().etherAPI.pushablesUIColor;
    }

    @LuaMethod(name="setPushableUIColor", global=true)
    public static void setPushableUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.pushablesUIColor = new Color(r, g, b);
    }
    
    @LuaMethod(name="getAccentUIColor", global=true)
    public static Color getAccentUIColor() {
        return EtherMain.getInstance().etherAPI.mainUIAccentColor;
    }
    
    @LuaMethod(name="setAccentUIColor", global=true)
    public static void setAccentUIColor(float r, float g, float b) {
        EtherMain.getInstance().etherAPI.mainUIAccentColor = new Color(r, g, b);
    }
    
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
        try {
            Path configFolderPath = Paths.get("EtherHack/config", new String[0]);
            List<Path> fileList = Files.list(configFolderPath).filter(file -> file.toString().endsWith(".properties")).toList();
            for (Path filePath : fileList) {
                String fileName = filePath.getFileName().toString().replace(".properties", "");
                configFiles.add(fileName);
            }
        }
        catch (IOException e) {
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
    
    


    @LuaMethod(name="getAntiCheat12Status", global=true) public static boolean getAntiCheat12Status() {return ServerOptions.instance.getBoolean("AntiCheatProtectionType12");}
    @LuaMethod(name="getAntiCheat8Status", global=true) public static boolean getAntiCheat8Status() {return ServerOptions.instance.getBoolean("AntiCheatProtectionType8");}
    @LuaMethod(name="getDistanceBetweenPlayers", global=true)public static float getDistanceBetweenPlayers(IsoPlayer player1, IsoPlayer player2) {return PlayerUtils.getDistanceBetweenPlayers((IsoPlayer)player1, (IsoPlayer)player2);}
    @LuaMethod(name="resetWeaponsStats", global=true)public static void resetWeaponsStats() {EtherMain.getInstance().etherAPI.resetWeaponsStats();}
    
    @LuaMethod(name="isBlockCompileLuaWithBadWords", global=true)public static boolean isBlockCompileLuaWithBadWords() {return EtherLuaCompiler.getInstance().isBlockCompileLuaWithBadWords;}
    @LuaMethod(name="toggleBlockCompileLuaWithBadWords", global=true)public static void toggleBlockCompileLuaWithBadWords(boolean isToggled) {EtherLuaCompiler.getInstance().isBlockCompileLuaWithBadWords = isToggled;}
    
    @LuaMethod(name="isBlockCompileLuaAboutEtherHack", global=true)public static boolean isBlockCompileLuaAboutEtherHack() {return EtherLuaCompiler.getInstance().isBlockCompileLuaAboutEtherHack;}
    @LuaMethod(name="toggleBlockCompileLuaAboutEtherHack", global=true)public static void toggleBlockCompileLuaAboutEtherHack(boolean isToggled) {EtherLuaCompiler.getInstance().isBlockCompileLuaAboutEtherHack = isToggled;}

    @LuaMethod(name="isBlockCompileDefaultLua", global=true)public static boolean isBlockCompileDefaultLua() {return EtherLuaCompiler.getInstance().isBlockCompileDefaultLua;}
    @LuaMethod(name="toggleBlockCompileDefaultLua", global=true)public static void toggleBlockCompileDefaultLua(boolean isToggled) {EtherLuaCompiler.getInstance().isBlockCompileDefaultLua = isToggled;}

    @LuaMethod(name="isAlwaysRack", global=true)public static boolean isAlwaysRack() {return EtherMain.getInstance().etherAPI.isAlwaysRack}
    @LuaMethod(name="toggleAlwaysRack", global=true)public static void toggleAlwaysRack(boolean isToggled) {EtherMain.getInstance().etherAPI.isAlwaysRack = isToggled;}

    @LuaMethod(name="isAlwaysRoundChamber", global=true) public static boolean isAlwaysRoundChamber() {return EtherMain.getInstance().etherAPI.isAlwaysRoundChamber;}
    @LuaMethod(name="toggleAlwaysRoundChamber", global=true) public static void toggleAlwaysRoundChamber(boolean isToggled) {EtherMain.getInstance().etherAPI.isAlwaysRoundChamber = isToggled;}

    @LuaMethod(name="isAlwaysKnockdown", global=true)public static boolean isAlwaysKnockdown() {return EtherMain.getInstance().etherAPI.isAlwaysKnockdown;}
    @LuaMethod(name="toggleAlwaysKnockdown", global=true)public static void toggleAlwaysKnockdown(boolean isToggled) {EtherMain.getInstance().etherAPI.isAlwaysKnockdown = isToggled;}

    @LuaMethod(name="isAlwaysAiming", global=true) public static boolean isAlwaysAiming() {return EtherMain.getInstance().etherAPI.isAlwaysAiming;}
    @LuaMethod(name="toggleAlwaysAiming", global=true) public static void toggleAlwaysAiming(boolean isToggled) {EtherMain.getInstance().etherAPI.isAlwaysAiming = isToggled;}

    @LuaMethod(name="isAlwaysCritical", global=true) public static boolean isAlwaysCritical() {return EtherMain.getInstance().etherAPI.isAlwaysCritical;}
    @LuaMethod(name="toggleAlwaysCritical", global=true)public static void toggleAlwaysCritical(boolean isToggled) {EtherMain.getInstance().etherAPI.isAlwaysCritical = isToggled;}

    @LuaMethod(name="isEnableInvisible", global=true)public static boolean isEnableInvisible() {return EtherMain.getInstance().etherAPI.isEnableInvisible;}
    @LuaMethod(name="toggleInvisible", global=true)public static void toggleInvisible(boolean isToggled) {EtherMain.getInstance().etherAPI.isEnableInvisible = isToggled;}

    @LuaMethod(name="isZombieDontAttack", global=true)public static boolean isZombieDontAttack() {return EtherMain.getInstance().etherAPI.isZombieDontAttack;}
    @LuaMethod(name="toggleZombieDontAttack", global=true)public static void toggleZombieDontAttack(boolean isToggled) {EtherMain.getInstance().etherAPI.isZombieDontAttack = isToggled;}

    @LuaMethod(name="isEnableNoclip", global=true)public static boolean isEnableNoclip() {return EtherMain.getInstance().etherAPI.isEnableNoclip;}
    @LuaMethod(name="toggleNoclip", global=true)public static void toggleNoclip(boolean isToggled) {EtherMain.getInstance().etherAPI.isEnableNoclip = isToggled;}

    @LuaMethod(name="isEnableGodMode", global=true) public static boolean isEnableGodMode() {return EtherMain.getInstance().etherAPI.isEnableGodMode;}
    @LuaMethod(name="toggleGodMode", global=true)public static void toggleGodMode(boolean isToggled) {EtherMain.getInstance().etherAPI.isEnableGodMode = isToggled;}

    @LuaMethod(name="isEnableNightVision", global=true)public static boolean isEnableNightVision() {return EtherMain.getInstance().etherAPI.isEnableNightVision;}
    @LuaMethod(name="toggleNightVision", global=true)public static void toggleNightVision(boolean isToggled) {EtherMain.getInstance().etherAPI.isEnableNightVision = isToggled;}

    @LuaMethod(name="isNoRecoil", global=true)public static boolean isNoRecoil() {return EtherMain.getInstance().etherAPI.isNoRecoil;}
    @LuaMethod(name="toggleNoRecoil", global=true)public static void toggleNoRecoil(boolean isToggled) {EtherMain.getInstance().etherAPI.isNoRecoil = isToggled;}

    @LuaMethod(name="isNoReload", global=true)public static boolean isNoReload() {return EtherMain.getInstance().etherAPI.isNoReload;}
    @LuaMethod(name="toggleNoReload", global=true)public static void toggleNoReload(boolean isToggled) {EtherMain.getInstance().etherAPI.isNoReload = isToggled;}

    @LuaMethod(name="isNoJam", global=true)public static boolean isNoJam() {return EtherMain.getInstance().etherAPI.isNoJam;}
    @LuaMethod(name="toggleNoJam", global=true)public static void toggleNoJam(boolean isToggled) {EtherMain.getInstance().etherAPI.isNoJam = isToggled;}

    @LuaMethod(name="isNoSpentRoundChamber", global=true)public static boolean isNoSpentRoundChamber() {return EtherMain.getInstance().etherAPI.isNoSpentRoundChamber;}
    @LuaMethod(name="toggleNoSpentRoundChamber", global=true)public static void toggleNoSpentRoundChamber(boolean isToggled) {EtherMain.getInstance().etherAPI.isNoSpentRoundChamber = isToggled;}

    @LuaMethod(name="isNoBroken", global=true) public static boolean isNoBroken() {return EtherMain.getInstance().etherAPI.isNoBroken;}
    @LuaMethod(name="toggleNoBroken", global=true) public static void toggleNoBroken(boolean var0) {EtherMain.getInstance().etherAPI.isNoBroken = var0;}

    @LuaMethod(name="isNoInfected", global=true) public static boolean isNoInfected() {return EtherMain.getInstance().etherAPI.isNoInfected;}
    @LuaMethod(name="toggleNoInfected", global=true) public static void toggleNoInfected(boolean var0) {EtherMain.getInstance().etherAPI.isNoInfected = var0;}

    @LuaMethod(name="isNoWet", global=true) public static boolean isNoWet() {return EtherMain.getInstance().etherAPI.isNoWet;}
    @LuaMethod(name="toggleNoWet", global=true) public static void toggleNoWet(boolean var0) {EtherMain.getInstance().etherAPI.isNoWet = var0;}

    @LuaMethod(name="isNoHoled", global=true) public static boolean isNoHoled() {return EtherMain.getInstance().etherAPI.isNoHoled;}
    @LuaMethod(name="toggleNoHoled", global=true) public static void toggleNoHoled(boolean var0) {EtherMain.getInstance().etherAPI.isNoHoled = var0;}

    @LuaMethod(name="isNoDirted", global=true) public static boolean isNoDirted() {return EtherMain.getInstance().etherAPI.isNoDirted;}
    @LuaMethod(name="toggleNoDirted", global=true) public static void toggleNoDirted(boolean var0) {EtherMain.getInstance().etherAPI.isNoDirted = var0;}

    @LuaMethod(name="isNoBlooded", global=true) public static boolean isNoBlooded() {return EtherMain.getInstance().etherAPI.isNoBlooded;}
    @LuaMethod(name="toggleNo", global=true) public static void toggleNoBlooded(boolean var0) {EtherMain.getInstance().etherAPI.isNoBlooded = var0;}

    @LuaMethod(name="isExtraDamage", global=true) public static boolean isExtraDamage() {return EtherMain.getInstance().etherAPI.isExtraDamage;}
    @LuaMethod(name="toggleExtraDamage", global=true)public static void toggleExtraDamage(boolean isToggled) {EtherMain.getInstance().etherAPI.isExtraDamage = isToggled;}

    @LuaMethod(name="isTimedActionCheat", global=true) public static boolean isTimedActionCheat() {return EtherMain.getInstance().etherAPI.isTimedActionCheat;}
    @LuaMethod(name="toggleTimedActionCheat", global=true)public static void toggleTimedActionCheat(boolean isToggled) {EtherMain.getInstance().etherAPI.isTimedActionCheat = isToggled;}

    @LuaMethod(name="isMultiHitZombies", global=true) public static boolean isMultiHitZombies() {return EtherMain.getInstance().etherAPI.isMultiHitZombies;}
    @LuaMethod(name="toggleMultiHitZombies", global=true)public static void toggleMultiHitZombies(boolean isToggled) {EtherMain.getInstance().etherAPI.isMultiHitZombies = isToggled;}

    @LuaMethod(name="isUnlimitedCondition", global=true)public static boolean isUnlimitedCondition() {return EtherMain.getInstance().etherAPI.isUnlimitedCondition;}
    @LuaMethod(name="toggleUnlimitedCondition", global=true)public static void toggleUnlimitedCondition(boolean isToggled) {EtherMain.getInstance().etherAPI.isUnlimitedCondition = isToggled;}

    @LuaMethod(name="isVisualEnable360Vision", global=true)public static boolean isVisualEnable360Vision() {return EtherMain.getInstance().etherAPI.isVisualEnable360Vision;}
    @LuaMethod(name="toggleVisualEnable360Vision", global=true)public static void toggleVisualEnable360Vision(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualEnable360Vision = isToggled;}

    @LuaMethod(name="isVisualDrawLineToPlayers", global=true)public static boolean isVisualDrawLineToPlayers() {return EtherMain.getInstance().etherAPI.isVisualDrawLineToPlayers;}
    @LuaMethod(name="toggleVisualDrawLineToPlayers", global=true)public static void toggleVisualDrawLineToPlayers(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualDrawLineToPlayers = isToggled;}

    @LuaMethod(name="isVisualDrawLineToVehicle", global=true)public static boolean isVisualDrawLineToVehicle() {return EtherMain.getInstance().etherAPI.isVisualDrawLineToVehicle;}
    @LuaMethod(name="toggleVisualDrawLineToVehicle", global=true)public static void toggleVisualDrawLineToVehicle(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualDrawLineToVehicle = isToggled;}
    
    @LuaMethod(name="isMapDrawZombies", global=true)public static boolean isMapDrawZombies() {return EtherMain.getInstance().etherAPI.isMapDrawZombies;}
    @LuaMethod(name="toggleMapDrawZombies", global=true)public static void toggleMapDrawZombies(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawZombies = isToggled;}
    
    @LuaMethod(name="isMapDrawVehicles", global=true)public static boolean isMapDrawVehicles() {return EtherMain.getInstance().etherAPI.isMapDrawVehicles;}
    @LuaMethod(name="toggleMapDrawVehicles", global=true)public static void toggleMapDrawVehicles(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawVehicles = isToggled;}

    @LuaMethod(name="isMapDrawAllPlayers", global=true)public static boolean isMapDrawAllPlayers() {return EtherMain.getInstance().etherAPI.isMapDrawAllPlayers;}
    @LuaMethod(name="toggleMapDrawAllPlayers", global=true)public static void toggleMapDrawAllPlayers(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawAllPlayers = isToggled;}
    
    @LuaMethod(name="isMapDrawLocalPlayer", global=true) public static boolean isMapDrawLocalPlayer() {return EtherMain.getInstance().etherAPI.isMapDrawLocalPlayer;}
    @LuaMethod(name="toggleMapDrawLocalPlayer", global=true)public static void toggleMapDrawLocalPlayer(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawLocalPlayer = isToggled;}
    
    @LuaMethod(name="isMapDrawSurvivors", global=true)public static boolean isMapDrawSurvivors() {return EtherMain.getInstance().etherAPI.isMapDrawSurvivors;}
    @LuaMethod(name="toggleMapDrawSurvivors", global=true)public static void toggleMapDrawSurvivors(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawSurvivors = isToggled;}
    
    @LuaMethod(name="isMapDrawRemoteSurvivors", global=true)public static boolean isMapDrawRemoteSurvivors() {return EtherMain.getInstance().etherAPI.isMapDrawRemoteSurvivors;}
    @LuaMethod(name="toggleMapDrawRemoteSurvivors", global=true)public static void toggleMapDrawRemoteSurvivors(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawRemoteSurvivors = isToggled;}
    
    @LuaMethod(name="isMapDrawPushables", global=true)public static boolean isMapDrawPushables() {return EtherMain.getInstance().etherAPI.isMapDrawPushables;}
    @LuaMethod(name="toggleMapDrawPushables", global=true)public static void toggleMapDrawPushables(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawPushables = isToggled;}
    
    @LuaMethod(name="isMapDrawBuildings", global=true)public static boolean isMapDrawBuildings() {return EtherMain.getInstance().etherAPI.isMapDrawBuildings;}
    @LuaMethod(name="toggleMapDrawBuildings", global=true)public static void toggleMapDraw(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawBuildings = isToggled;}
    
    @LuaMethod(name="isMapDrawItems", global=true) public static boolean isMapDrawItems() {return EtherMain.getInstance().etherAPI.isMapDrawItems;}
    @LuaMethod(name="toggleMapDraw", global=true)public static void toggleMapDrawItems(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawItems = isToggled;}
    
    @LuaMethod(name="isMapDrawWorldItems", global=true) public static boolean isMapDrawWorldItems() {return EtherMain.getInstance().etherAPI.isMapDrawWorldItems;}
    @LuaMethod(name="toggleMapDrawWorldItems", global=true) public static void toggleMapDrawWorldItems(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawWorldItems = isToggled;}

    @LuaMethod(name="isMapDrawRooms", global=true) public static boolean isMapDrawRooms() {return EtherMain.getInstance().etherAPI.isMapDrawRooms;}
    @LuaMethod(name="toggleMapDrawRooms", global=true)public static void toggleMapDrawRooms(boolean isToggled) {EtherMain.getInstance().etherAPI.isMapDrawRooms = isToggled;}
    
    @LuaMethod(name="isVisualDrawPlayerInfo", global=true)public static boolean isVisualDrawPlayerInfo() {return EtherMain.getInstance().etherAPI.isVisualDrawPlayerInfo;}
    @LuaMethod(name="toggleVisualDrawPlayerInfo", global=true) public static void toggleVisualDrawPlayerInfo(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualDrawPlayerInfo = isToggled;}

    @LuaMethod(name="isVisualsZombiesEnable", global=true) public static boolean isVisualsZombiesEnable() {return EtherMain.getInstance().etherAPI.isVisualsZombiesEnable;}
    @LuaMethod(name="toggleVisualsZombiesEnable", global=true) public static void toggleVisualsZombiesEnable(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualsZombiesEnable = isToggled;}

    @LuaMethod(name="isVisualsVehiclesEnable", global=true)public static boolean isVisualsVehiclesEnable() {return EtherMain.getInstance().etherAPI.isVisualsVehiclesEnable;}
    @LuaMethod(name="toggleVisualsVehiclesEnable", global=true)public static void toggleVisualsVehiclesEnable(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualsVehiclesEnable = isToggled;}
    
    @LuaMethod(name="isVisualsPlayersEnable", global=true)public static boolean isVisualsPlayersEnable() {return EtherMain.getInstance().etherAPI.isVisualsPlayersEnable;}
    @LuaMethod(name="isVisualDrawPlayerNickname", global=true)public static boolean isVisualDrawPlayerNickname() {return EtherMain.getInstance().etherAPI.isVisualDrawPlayerNickname;}
    @LuaMethod(name="toggleVisualDrawPlayerNickname", global=true) public static void toggleVisualDrawPlayerNickname(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualDrawPlayerNickname = isToggled;}
    
    @LuaMethod(name="isVisualDrawToLocalPlayer", global=true) public static boolean isVisualDrawToLocalPlayer() {return EtherMain.getInstance().etherAPI.isVisualDrawToLocalPlayer;}
    @LuaMethod(name="toggleVisualDrawToLocalPlayer", global=true) public static void toggleVisualDrawToLocalPlayer(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualDrawToLocalPlayer = isToggled;}
    
    @LuaMethod(name="isVisualsEnable", global=true) public static boolean isVisualsEnable() {return EtherMain.getInstance().etherAPI.isVisualsEnable;}
    @LuaMethod(name="toggleVisualsEnable", global=true) public static void toggleVisualsEnable(boolean isToggled) {EtherMain.getInstance().etherAPI.isVisualsEnable = isToggled;}
    
    @LuaMethod(name="isBypassDebugMode", global=true) public static boolean isBypassDebugMode() {return EtherMain.getInstance().etherAPI.isBypassDebugMode;}
    @LuaMethod(name="toggleBypassDebugMode", global=true)public static void toggleBypassDebugMode(boolean isToggled) {EtherMain.getInstance().etherAPI.isBypassDebugMode = isToggled;}
    
    @LuaMethod(name="toggleUnlimitedEndurance", global=true)public static void toggleUnlimitedEndurance(boolean isToggled) {EtherMain.getInstance().etherAPI.isUnlimitedEndurance = isToggled;}
    @LuaMethod(name="isUnlimitedEndurance", global=true)public static boolean isUnlimitedEndurance() {return EtherMain.getInstance().etherAPI.isUnlimitedEndurance;}
    
    @LuaMethod(name="toggleUnlimitedAmmo", global=true)public static void toggleUnlimitedAmmo(boolean isToggled) {EtherMain.getInstance().etherAPI.isUnlimitedAmmo = isToggled;}
    @LuaMethod(name="isUnlimitedAmmo", global=true)public static boolean isUnlimitedAmmo() {return EtherMain.getInstance().etherAPI.isUnlimitedAmmo;}
    
    @LuaMethod(name="toggleDisableFatigue", global=true)public static void toggleDisableFatigue(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableFatigue = isToggled;}
    @LuaMethod(name="isDisableFatigue", global=true) public static boolean isDisableFatigue() {return EtherMain.getInstance().etherAPI.isDisableFatigue;}

    @LuaMethod(name="toggleDisableHunger", global=true)public static void toggleDisableHunger(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableHunger = isToggled;}
    @LuaMethod(name="isDisableHunger", global=true)public static boolean isDisableHunger() {return EtherMain.getInstance().etherAPI.isDisableHunger;}

    @LuaMethod(name="toggleDisableThirst", global=true)public static void toggleDisableThirst(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableThirst = isToggled;}
    @LuaMethod(name="isDisableThirst", global=true)public static boolean isDisableThirst() {return EtherMain.getInstance().etherAPI.isDisableThirst;}

    @LuaMethod(name="toggleDisableDrunkenness", global=true)public static void toggleDisableDrunkenness(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableDrunkenness = isToggled;}
    @LuaMethod(name="isDisableDrunkenness", global=true)public static boolean isDisableDrunkenness() {return EtherMain.getInstance().etherAPI.isDisableDrunkenness;}

    @LuaMethod(name="toggleDisableAnger", global=true)public static void toggleDisableAnger(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableAnger = isToggled;}
    @LuaMethod(name="isDisableAnger", global=true)public static boolean isDisableAnger() {return EtherMain.getInstance().etherAPI.isDisableAnger;}

    @LuaMethod(name="toggleDisableFear", global=true) public static void toggleDisableFear(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableFear = isToggled;}
    @LuaMethod(name="isDisableFear", global=true)public static boolean isDisableFear() {return EtherMain.getInstance().etherAPI.isDisableFear;}

    @LuaMethod(name="toggleDisablePain", global=true)public static void toggleDisablePain(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisablePain = isToggled;}
    @LuaMethod(name="isDisablePain", global=true)public static boolean isDisablePain() {return EtherMain.getInstance().etherAPI.isDisablePain;}

    @LuaMethod(name="toggleDisablePanic", global=true) public static void toggleDisablePanic(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisablePanic = isToggled;}
    @LuaMethod(name="isDisablePanic", global=true) public static boolean isDisablePanic() {return EtherMain.getInstance().etherAPI.isDisablePanic;}

    @LuaMethod(name="toggleDisableMorale", global=true) public static void toggleDisableMorale(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableMorale = isToggled;}
    @LuaMethod(name="isDisableMorale", global=true) public static boolean isDisableMorale() {return EtherMain.getInstance().etherAPI.isDisableMorale;}

    @LuaMethod(name="toggleDisableStress", global=true) public static void toggleDisableStress(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableStress = isToggled;}
    @LuaMethod(name="isDisableStress", global=true)public static boolean isDisableStress() {return EtherMain.getInstance().etherAPI.isDisableStress;}

    @LuaMethod(name="toggleDisableSickness", global=true) public static void toggleDisableSickness(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableSickness = isToggled;}
    @LuaMethod(name="isDisableSickness", global=true) public static boolean isDisableSickness() {return EtherMain.getInstance().etherAPI.isDisableSickness;}

    @LuaMethod(name="toggleDisableStressFromCigarettes", global=true)  public static void toggleDisableStressFromCigarettes(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableStressFromCigarettes = isToggled;}
    @LuaMethod(name="isDisableStressFromCigarettes", global=true) public static boolean isDisableStressFromCigarettes() {return EtherMain.getInstance().etherAPI.isDisableStressFromCigarettes;}

    @LuaMethod(name="toggleDisableSanity", global=true) public static void toggleDisableSanity(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableSanity = isToggled;}
    @LuaMethod(name="isDisableSanity", global=true) public static boolean isDisableSanity() {return EtherMain.getInstance().etherAPI.isDisableSanity;}

    @LuaMethod(name="toggleDisableBoredomLevel", global=true)public static void toggleDisableBoredomLevel(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableBoredomLevel = isToggled;}
    @LuaMethod(name="isDisableBoredomLevel", global=true) public static boolean isDisableBoredomLevel() {return EtherMain.getInstance().etherAPI.isDisableBoredomLevel;}

    @LuaMethod(name="toggleDisableUnhappynessLevel", global=true)public static void toggleDisableUnhappynessLevel(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableUnhappynessLevel = isToggled;}
    @LuaMethod(name="isDisableUnhappynessLevel", global=true)public static boolean isDisableUnhappynessLevel() {return EtherMain.getInstance().etherAPI.isDisableUnhappynessLevel;}

    @LuaMethod(name="toggleDisableWetness", global=true)public static void toggleDisableWetness(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableWetness = isToggled;}
    @LuaMethod(name="isDisableWetness", global=true) public static boolean isDisableWetness() {return EtherMain.getInstance().etherAPI.isDisableWetness;}

    @LuaMethod(name="toggleDisableInfectionLevel", global=true)public static void toggleDisableInfectionLevel(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableInfectionLevel = isToggled;}
    @LuaMethod(name="isDisableInfectionLevel", global=true)public static boolean isDisableInfectionLevel() {return EtherMain.getInstance().etherAPI.isDisableInfectionLevel;}

    @LuaMethod(name="toggleDisableFakeInfectionLevel", global=true) public static void toggleDisableFakeInfectionLevel(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableFakeInfectionLevel = isToggled;}
    @LuaMethod(name="isDisableFakeInfectionLevel", global=true)public static boolean isDisableFakeInfectionLevel() {return EtherMain.getInstance().etherAPI.isDisableFakeInfectionLevel;}

    @LuaMethod(name="isDisableFire", global=true)public static boolean isDisableFire() {return EtherMain.getInstance().etherAPI.isDisableFire;}
    @LuaMethod(name="toggleDisableFire", global=true)public static void toggleDisableFire(boolean isToggled) {EtherMain.getInstance().etherAPI.isDisableFire = isToggled;}

    @LuaMethod(name="isOptimalCalories", global=true) public static boolean isOptimalCalories() {return EtherMain.getInstance().etherAPI.isOptimalCalories;}
    @LuaMethod(name="toggleOptimalCalories", global=true)public static void toggleOptimalCalories(boolean isToggled) {EtherMain.getInstance().etherAPI.isOptimalCalories = isToggled;}

    @LuaMethod(name="isOptimalWeight", global=true)public static boolean isOptimalWeight() {return EtherMain.getInstance().etherAPI.isOptimalWeight;}
    @LuaMethod(name="toggleOptimalWeight", global=true)public static void toggleOptimalWeight(boolean isToggled) {EtherMain.getInstance().etherAPI.isOptimalWeight = isToggled;}

    @LuaMethod(name="isEnableUnlimitedCarry", global=true)public static boolean isEnableUnlimitedCarry() {return EtherMain.getInstance().etherAPI.isUnlimitedCarry;}
    @LuaMethod(name="toggleEnableUnlimitedCarry", global=true)public static void toggleEnableUnlimitedCarry(boolean isToggled) {EtherMain.getInstance().etherAPI.isUnlimitedCarry = isToggled;}



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

    @LuaMethod(name="getTranslate", global=true)
    public static String getTranslate(String key, KahluaTable args) {
        return EtherMain.getInstance().etherTranslator.getTranslate(key, args);
    }

    @LuaMethod(name="getTranslate", global=true)
    public static String getTranslate(String key) {
        return EtherMain.getInstance().etherTranslator.getTranslate(key);
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
