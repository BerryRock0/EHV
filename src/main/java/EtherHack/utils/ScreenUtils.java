package EtherHack.utils;

import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;

/**
 * Класс для перевода координат мира в координаты экрана.
 * Используется для отрисовки информации об объектах прямо в игровом мире.
 */
public class ScreenUtils
{
    /**
     * Получение экранной координаты X для точки мира.
     *
     * @param worldX координата X в мире
     * @param worldY координата Y в мире
     * @param worldZ координата Z в мире
     * @return координата X на экране в пикселях
     */
    public static float getScreenPositionX(float worldX, float worldY, float worldZ)
    {
        float zoom = getZoom();
        float screenWidth = Core.getInstance().getScreenWidth();
        float projectedX = IsoUtils.XToScreen(worldX, worldY, worldZ, 0) - IsoCamera.getOffX();
        return (projectedX - screenWidth / 2.0f) / zoom + screenWidth / 2.0f;
    }

    /**
     * Получение экранной координаты Y для точки мира.
     *
     * @param worldX координата X в мире
     * @param worldY координата Y в мире
     * @param worldZ координата Z в мире
     * @return координата Y на экране в пикселях
     */
    public static float getScreenPositionY(float worldX, float worldY, float worldZ)
    {
        float zoom = getZoom();
        float screenHeight = Core.getInstance().getScreenHeight();
        float projectedY = IsoUtils.YToScreen(worldX, worldY, worldZ, 0) - IsoCamera.getOffY();
        return (projectedY - screenHeight / 2.0f) / zoom + screenHeight / 2.0f;
    }

    /**
     * Текущий масштаб отображения мира.
     */
    private static float getZoom()
    {
        float zoom = Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
        return zoom > 0.0f ? zoom : 1.0f;
    }
}
