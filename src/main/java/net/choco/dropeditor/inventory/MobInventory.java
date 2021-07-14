package net.choco.dropeditor.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MobInventory implements InventoryHolder
{

    public Inventory getInventory() {
        return Bukkit.createInventory(null, 54);
    }
}
