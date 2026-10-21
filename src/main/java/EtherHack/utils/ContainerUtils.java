package EtherHack.utils;

import zombie.characters.IsoPlayer;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.util.list.PZArrayList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Класс для поиска контейнеров (ящиков, сейфов, шкафов) рядом с игроком
 * и получения их содержимого.
 */
public class ContainerUtils
{
    /**
     * Информация о предмете, находящемся в контейнере.
     */
    public static class ContainerItem
    {
        /** Отображаемое название предмета */
        public String name;
        /** Количество предметов с таким названием */
        public int count;
    }

    /**
     * Информация о найденном контейнере.
     */
    public static class ContainerInfo
    {
        /** Тип объекта (crate, safe, lockers и т.д.) */
        public String name;
        /** Координаты контейнера в мире */
        public float x;
        public float y;
        public float z;
        /** Расстояние до контейнера от игрока (в тайлах) */
        public float distance;
        /** Содержимое контейнера */
        public ArrayList<ContainerItem> items = new ArrayList<ContainerItem>();
    }

    /**
     * Поиск всех контейнеров с содержимым в радиусе от игрока.
     * Результат отсортирован по расстоянию до игрока.
     *
     * @param player игрок, относительно которого выполняется поиск
     * @param radius радиус поиска в тайлах
     * @return список найденных контейнеров с их содержимым
     */
    public static ArrayList<ContainerInfo> getNearbyContainers(IsoPlayer player, float radius)
    {
        ArrayList<ContainerInfo> containers = new ArrayList<ContainerInfo>();

        if (player == null || IsoWorld.instance == null || radius <= 0.0f)
            return containers;

        IsoCell cell = IsoWorld.instance.getCell();
        if (cell == null)
            return containers;

        float playerX = player.getX();
        float playerY = player.getY();
        int playerZ = (int)player.getZ();

        int minX = (int)Math.floor(playerX - radius);
        int maxX = (int)Math.ceil(playerX + radius);
        int minY = (int)Math.floor(playerY - radius);
        int maxY = (int)Math.ceil(playerY + radius);

        for (int z = playerZ - 1; z <= playerZ + 1; z++)
        {
            for (int x = minX; x <= maxX; x++)
            {
                for (int y = minY; y <= maxY; y++)
                {
                    IsoGridSquare square = cell.getGridSquare(x, y, z);
                    if (square == null)
                        continue;

                    PZArrayList<IsoObject> objects = square.getObjects();
                    if (objects == null)
                        continue;

                    for (int i = 0; i < objects.size(); i++)
                    {
                        ContainerInfo containerInfo = getContainerInfo(objects.get(i), player, radius);
                        if (containerInfo != null)
                            containers.add(containerInfo);
                    }
                }
            }
        }

        containers.sort((first, second) -> Float.compare(first.distance, second.distance));

        return containers;
    }

    /**
     * Получение информации о контейнере, если он находится в радиусе и в нём есть предметы.
     */
    private static ContainerInfo getContainerInfo(IsoObject object, IsoPlayer player, float radius)
    {
        if (object == null)
            return null;

        ItemContainer container = object.getContainer();
        if (container == null)
            return null;

        ArrayList<ContainerItem> items = getContainerItems(container);
        if (items.isEmpty())
            return null;

        float x = object.getX();
        float y = object.getY();
        float z = object.getZ();

        float deltaX = x - player.getX();
        float deltaY = y - player.getY();
        float distance = (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > radius)
            return null;

        ContainerInfo containerInfo = new ContainerInfo();
        containerInfo.name = object.getObjectName();
        containerInfo.x = x;
        containerInfo.y = y;
        containerInfo.z = z;
        containerInfo.distance = distance;
        containerInfo.items = items;

        return containerInfo;
    }

    /**
     * Получение содержимого контейнера.
     *
     * @param container контейнер
     * @return список предметов контейнера с их количеством
     */
    public static ArrayList<ContainerItem> getContainerItems(ItemContainer container)
    {
        ArrayList<ContainerItem> items = new ArrayList<ContainerItem>();

        if (container == null || container.getItems() == null || container.getItems().isEmpty())
            return items;

        Map<String, ContainerItem> itemsMap = new LinkedHashMap<String, ContainerItem>();

        for (InventoryItem item : container.getItems())
        {
            if (item == null)
                continue;

            String itemName = item.getDisplayName();
            if (itemName == null || itemName.isEmpty())
                itemName = item.getName();
            if (itemName == null || itemName.isEmpty())
                continue;

            ContainerItem containerItem = itemsMap.get(itemName);
            if (containerItem == null)
            {
                containerItem = new ContainerItem();
                containerItem.name = itemName;
                containerItem.count = 0;
                itemsMap.put(itemName, containerItem);
                items.add(containerItem);
            }

            containerItem.count++;
        }

        return items;
    }
}
