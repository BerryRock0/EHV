package EtherHack.utils;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.vehicles.BaseVehicle;

public class PlayerUtils {
    public static float getDistanceBetweenPlayerAndZombie(IsoPlayer player, IsoZombie zombie) {
        float dx = player.x - zombie.x;
        float dy = player.y - zombie.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static float getDistanceBetweenPlayerAndVehicle(IsoPlayer player, BaseVehicle vehicle) {
        float dx = player.x - vehicle.x;
        float dy = player.y - vehicle.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static float getDistanceBetweenPlayers(IsoPlayer player1, IsoPlayer player2) {
        float dx = player1.x - player2.x;
        float dy = player1.y - player2.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static float getScreenPositionX(IsoPlayer player) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenX = IsoUtils.XToScreen((float)player.x, (float)player.y, (float)player.getZ(), (int)0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenX -= IsoCamera.getOffX();
        return posScreenX /= scale;
    }

    public static float getScreenPositionY(IsoPlayer player) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenY = IsoUtils.YToScreen((float)player.x, (float)player.y, (float)player.getZ(), (int)0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenY -= IsoCamera.getOffY();
        posScreenY -= (float)(128 / (2 / Core.TileScale));
        return posScreenY /= scale;
    }
}
