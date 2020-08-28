package co.proxa.founddiamonds.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.file.Config;

import java.util.HashSet;

public class BlockPlaceListener implements Listener {

    private HashSet<Location> placed = new HashSet<Location>();
    private FoundDiamonds fd;

    public BlockPlaceListener(FoundDiamonds fd) {
        this.fd = fd;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (fd.getWorldHandler().isEnabledWorld(event.getPlayer())) {
            if (isMonitoredBlock(event)) {
                addBlock(event);
            }
        }
    }

    public void addBlock(BlockPlaceEvent event) {
        if (fd.getConfig().getBoolean(Config.mysqlEnabled)) {
            fd.getMySQL().updatePlacedBlockinSQL(event.getBlock().getLocation());
        } else {
            placed.add(event.getBlock().getLocation());
        }
    }

    public boolean isMonitoredBlock(BlockPlaceEvent event) {
        final Material mat = event.getBlock().getType();
        return fd.getMapHandler().getAdminMessageBlocks().containsKey(mat) ||
                fd.getMapHandler().getBroadcastedBlocks().containsKey(mat) ||
                fd.getMapHandler().getLightLevelBlocks().containsKey(mat);
    }

    public void clearPlaced() {
        placed.clear();
    }

    public HashSet<Location> getFlatFilePlacedBlocks() {
        return placed;
    }

}
