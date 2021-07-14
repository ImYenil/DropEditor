package net.choco.dropeditor.listener;

import net.choco.dropeditor.Main;
import net.choco.dropeditor.inventory.MobInventory;
import net.choco.dropeditor.utility.Serialization;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DropEditListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("dropedit.admin")) {
            return;
        }
        if (e.getRightClicked() instanceof Player) {
            return;
        }
        if (!p.isSneaking()) {
            return;
        }
        e.setCancelled(true);
        String type = e.getRightClicked().getType().toString();
        this.openDropInventory(p, type);
    }

    public void openDropInventory(Player p, String entityType) {
        if (!Main.getInstance().getDrops().containsKey(entityType)) {
            Inventory inventory = Bukkit.createInventory(new MobInventory(), 54, entityType);
            Main.getInstance().getDrops().put(entityType, inventory);
            p.openInventory(inventory);
        }
        else {
            p.openInventory(Main.getInstance().getDrops().get(entityType));
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof MobInventory) {
            for (String s : Main.getInstance().getDrops().keySet()) {
                Main.getInstance().mobConfig(1, s, "data", Serialization.toBase64(Main.getInstance().getDrops().get(s)));
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent ev) {
        if (Main.getInstance().getDrops().containsKey(ev.getEntityType().toString())) {
            Inventory inv = Main.getInstance().getDrops().get(ev.getEntityType().toString());
            ItemStack is = inv.getItem(Main.getInstance().getR().nextInt(inv.getSize()));
            if (is == null) {
                return;
            }
            ev.getEntity().getWorld().dropItemNaturally(ev.getEntity().getLocation(), is);
        }
        if (!Main.getInstance().getVanillaDrops()) {
            ev.getDrops().clear();
        }
    }
}
