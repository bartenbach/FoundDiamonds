package co.proxa.founddiamonds.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import co.proxa.founddiamonds.FoundDiamonds;

public class TrapListener implements Listener {

	private FoundDiamonds fd;

	public TrapListener(FoundDiamonds fd) {
		this.fd = fd;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	void onBlockBreak(final BlockBreakEvent event) {
		if (!fd.getWorldHandler().isEnabledWorld(event.getPlayer())) {
			return;
		}
		final Location loc = event.getBlock().getLocation();
		if (fd.getTrapHandler().isTrapBlock(loc)) {
			fd.getTrapHandler().handleTrapBlockBreak(event);
			event.setCancelled(true);
		}
	}
}
