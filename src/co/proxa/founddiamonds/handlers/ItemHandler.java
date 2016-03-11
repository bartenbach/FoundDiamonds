package co.proxa.founddiamonds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.file.Config;
import co.proxa.founddiamonds.util.Format;

public class ItemHandler {

    private FoundDiamonds fd;

    public ItemHandler(FoundDiamonds fd) {
        this.fd = fd;
    }

    public void handleRandomItems(final Player player) {
        int randomInt = (int) (Math.random()*100);
        if (randomInt <= fd.getConfig().getInt(Config.chanceToGetItem)) {
            int randomNumber = (int)(Math.random()*150);
            if (randomNumber >= 0 && randomNumber <= 150) {
                selectRandomItem(player, randomNumber);
            }
        }
    }

    private void selectRandomItem(Player player, int randomNumber) {
        int randomItem;
        if (randomNumber < 50) {
            randomItem = fd.getConfig().getInt(Config.randomItem1);
        } else if (randomNumber >= 50 && randomNumber < 100) {
            randomItem = fd.getConfig().getInt(Config.randomItem2);
        } else {
            randomItem = fd.getConfig().getInt(Config.randomItem3);
        }
        int amount = getRandomItemAmount();
        giveItems(player, randomItem, amount);
    }

    //@SuppressWarnings("deprecation")
    private void giveItems(Player player, int item, int amount) {
        if (fd.getConfig().getBoolean(Config.awardAllItems)) {
            for(Player p: fd.getServer().getOnlinePlayers()) {
                if (fd.getWorldHandler().isEnabledWorld(p)) {
                    p.sendMessage(ChatColor.GRAY + "Everyone else got " + amount +
                            " " + Format.getFormattedName(Material.getMaterial(item), amount));
                    if (p != player) {
                        p.getInventory().addItem(new ItemStack(item, amount));
                        p.updateInventory();
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.GRAY + "You got " + amount +
                    " " + Format.getFormattedName(Material.getMaterial(item), amount));
            player.getInventory().addItem(new ItemStack(item, amount));
            player.updateInventory();
        }
    }

    private int getRandomItemAmount(){
        return ((fd.getConfig().getInt(Config.maxItems)));
    }
}
