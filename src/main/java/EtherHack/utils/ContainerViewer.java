package EtherHack.utils;

import zombie.core.Color;
import zombie.core.Core;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

import java.util.ArrayList;

/**
 * Класс для отрисовки информации о контейнерах (ящиках) прямо в игровом мире.
 * Информация отображается даже в тех случаях, когда до контейнера нельзя дотянуться.
 */
public class ContainerViewer
{
    /** Максимальное количество предметов, отображаемых для одного контейнера */
    public static final int MAX_DISPLAYED_ITEMS = 5;

    /**
     * Отрисовка информации о контейнерах.
     *
     * @param containers список контейнеров для отрисовки
     * @param accentColor цвет для названия контейнера
     */
    public static void render(ArrayList<ContainerUtils.ContainerInfo> containers, Color accentColor)
    {
        if (containers == null || containers.isEmpty())
            return;

        int lineHeight = TextManager.instance.getFontHeight(UIFont.Small);
        float screenWidth = Core.getInstance().getScreenWidth();
        float screenHeight = Core.getInstance().getScreenHeight();

        for (ContainerUtils.ContainerInfo container : containers)
        {
            float screenX = ScreenUtils.getScreenPositionX(container.x, container.y, container.z);
            float screenY = ScreenUtils.getScreenPositionY(container.x, container.y, container.z);

            // Контейнеры за пределами экрана не отображаются
            if (screenX < 0.0f || screenX > screenWidth || screenY < 0.0f || screenY > screenHeight)
                continue;

            int displayedItems = Math.min(container.items.size(), MAX_DISPLAYED_ITEMS);
            int linesCount = 1 + displayedItems + (container.items.size() > MAX_DISPLAYED_ITEMS ? 1 : 0);
            float textY = screenY - 12.0f - linesCount * lineHeight;

            // Название контейнера и расстояние до него
            String title = container.name + " (" + Math.round(container.distance) + "m)";
            Rendering.drawTextCenterWithShadow(title, UIFont.Small, screenX, textY,
                    accentColor.getR(), accentColor.getG(), accentColor.getB(), 1.0f);

            // Содержимое контейнера
            float itemY = textY + lineHeight;
            for (int i = 0; i < displayedItems; i++)
            {
                ContainerUtils.ContainerItem item = container.items.get(i);
                String itemText = item.count > 1 ? item.name + " x" + item.count : item.name;
                Rendering.drawTextCenterWithShadow(itemText, UIFont.Small, screenX, itemY, 1.0f, 1.0f, 1.0f, 0.9f);
                itemY += lineHeight;
            }

            if (container.items.size() > MAX_DISPLAYED_ITEMS)
            {
                Rendering.drawTextCenterWithShadow("...", UIFont.Small, screenX, itemY, 1.0f, 1.0f, 1.0f, 0.6f);
            }
        }
    }
}
