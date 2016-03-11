package co.proxa.founddiamonds.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import co.proxa.founddiamonds.FoundDiamonds;

public class BlockDamageListener implements Listener {

    private FoundDiamonds fd;

    public BlockDamageListener(FoundDiamonds fd) {
        this.fd = fd;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onBlockDamage(final BlockDamageEvent event) {
        final Player player = event.getPlayer();
        if (!fd.getWorldHandler().isEnabledWorld(player)) { return; }
        if (!fd.getWorldHandler().isValidGameMode(player)) { return; }
        final Material mat = event.getBlock().getType();
        if (fd.getPermissions().hasMonitorPerm(player)) {
            if (fd.getMapHandler().getLightLevelBlocks().containsKey(mat)) {
                fd.getLightLevelHandler().handleLightLevelMonitor(event, mat, player);
            }
        }
    }
}
