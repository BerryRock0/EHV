package EtherHack.utils;

import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.vehicles.BaseVehicle;

public class VehicleUtils {
    public static float getScreenPositionX(BaseVehicle vehicle) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenX = IsoUtils.XToScreen((float)vehicle.x, (float)vehicle.y, (float)vehicle.getZ(), (int)0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenX -= IsoCamera.getOffX();
        return posScreenX /= scale;
    }

    public static float getScreenPositionY(BaseVehicle vehicle) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenY = IsoUtils.YToScreen((float)vehicle.x, (float)vehicle.y, (float)vehicle.getZ(), (int)0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenY -= IsoCamera.getOffY();
        posScreenY -= (float)(128 / (2 / Core.TileScale));
        return posScreenY /= scale;
    }
}
