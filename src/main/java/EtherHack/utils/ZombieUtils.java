package EtherHack.utils;

import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;

public class ZombieUtils {
    public static float getScreenPositionX(IsoZombie zombie) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenX = IsoUtils.XToScreen((float)zombie.x, (float)zombie.y, (float)zombie.getZ(), (int)0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenX -= IsoCamera.getOffX();
        return posScreenX /= scale;
    }

    public static float getScreenPositionY(IsoZombie zombie) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenY = IsoUtils.YToScreen((float)zombie.x, (float)zombie.y, (float)zombie.getZ(), (int)0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenY -= IsoCamera.getOffY();
        posScreenY -= (float)(128 / (2 / Core.TileScale));
        return posScreenY /= scale;
    }
}
