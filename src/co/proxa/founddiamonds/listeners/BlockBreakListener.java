package co.proxa.founddiamonds.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.file.Config;

public class BlockBreakListener implements Listener  {

    private FoundDiamonds fd;

    public BlockBreakListener(FoundDiamonds fd) {
        this.fd = fd;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {

        final Player player = event.getPlayer();

        if (!fd.getWorldHandler().isEnabledWorld(player)) { FoundDiamonds.fd.debug("world is disabled");return; }
        if (!fd.getWorldHandler().isValidGameMode(player)) { FoundDiamonds.fd.debug("world is disabled");return; }
        if (event.getEventName().equalsIgnoreCase("FakeBlockBreakEvent")) { FoundDiamonds.fd.debug("Fakeblockbreakevent");return; }

        final Location loc = event.getBlock().getLocation();
        fd.getLightLevelHandler().checkAndClearLightLevelLocation(loc);
        if (!fd.getBlockCounter().isAnnounceable(loc)) {
            fd.getBlockCounter().removeAnnouncedOrPlacedBlock(loc);
            return;
        }

        final Material mat = event.getBlock().getType();
        int blockTotal = 0;
        int lightLevel = 99;

        if (fd.getPermissions().hasMonitorPerm(player)) {
        	FoundDiamonds.fd.debug(player.getName() + " has monitor perm");
        	System.out.println(fd.getMapHandler().getAdminMessageBlocks().keySet());
            if (fd.getMapHandler().getAdminMessageBlocks().containsKey(mat)) {
            	FoundDiamonds.fd.debug("Material is logged");
                lightLevel = fd.getLightLevelHandler().getLightLevel(event.getBlock());
                blockTotal = fd.getBlockCounter().getTotalBlocks(event.getBlock());
                fd.getAdminMessageHandler().sendAdminMessage(mat, blockTotal, player);
            }
        }

        if (fd.getPermissions().hasBroadcastPerm(player)) {
            if (fd.getMapHandler().getBroadcastedBlocks().containsKey(mat)) {
                if (blockTotal == 0) {blockTotal = fd.getBlockCounter().getTotalBlocks(event.getBlock());}
                if (lightLevel == 99) {lightLevel = fd.getLightLevelHandler().getLightLevel(event.getBlock());}
                fd.getBroadcastHandler().handleBroadcast(mat, blockTotal, player, lightLevel);
                fd.getAdminMessageHandler().clearReceivedAdminMessage();
            }
        }

        if (mat == Material.DIAMOND_ORE) {
            if (fd.getConfig().getBoolean(Config.logDiamondBreaks)) {
                fd.getLoggingHandler().handleLogging(event.getPlayer(), event.getBlock(), false, false, false, false);
            }
        }
    }
}
