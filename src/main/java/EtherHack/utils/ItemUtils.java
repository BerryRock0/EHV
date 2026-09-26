package EtherHack.utils;

import zombie.inventory.InventoryItem;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoWorldInventoryObject;

public class ItemUtils
{
    private void scanItems(IsoGridSquare square, float px, float py) 
    {
        List<IsoObject> objects = square.getObjects();
        int n = objects.size();
        for (int i = 0; i < n; i++)
        {
            if (i >= objects.size())
                return;

            IsoObject o = objects.get(i);
            if (!(o instanceof IsoWorldInventoryObject)) 
                continue;
          
            InventoryItem item = ((IsoWorldInventoryObject) o).getItem();
            if (item == null || !matchesFilter(item))
                continue;
  
            String label = nz(item.getDisplayName());
            if (label.isEmpty()) 
                label = nz(item.getFullType());
  
            IsoMovingObject m = (IsoMovingObject) o;
            add(m.getX(), m.getY(), m.getZ(), px, py, cfg.radius, KIND_ITEM, label);
        }
    }
}
