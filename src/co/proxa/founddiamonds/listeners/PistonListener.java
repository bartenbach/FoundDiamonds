package co.proxa.founddiamonds.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import co.proxa.founddiamonds.FoundDiamonds;

public class PistonListener implements Listener {

    private FoundDiamonds fd;

    public PistonListener(FoundDiamonds fd) {
        this.fd = fd;
    }

    @EventHandler
    void onPistonRetract(final BlockPistonRetractEvent event) {
        if (fd.getTrapHandler().isTrapBlock(event.getRetractLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPistonExtend(final BlockPistonExtendEvent event) {
        for (Block x : event.getBlocks()) {
            if (fd.getTrapHandler().isTrapBlock(x.getLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
