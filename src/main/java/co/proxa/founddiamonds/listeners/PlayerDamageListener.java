package co.proxa.founddiamonds.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import co.proxa.founddiamonds.FoundDiamonds;

public class PlayerDamageListener implements Listener {

    private FoundDiamonds fd;

    public PlayerDamageListener(FoundDiamonds fd) {
        this.fd = fd;
    }

    @EventHandler
    void onPlayerDamage(final EntityDamageEvent event) {
        if ((event.getCause() == DamageCause.FALL) && (event.getEntity() instanceof Player)) {
            final Player player = (Player) event.getEntity();
            if (fd.getPotionHandler().playerHasJumpPotion(player)) {
                event.setCancelled(true);
            }
        }
    }
}
