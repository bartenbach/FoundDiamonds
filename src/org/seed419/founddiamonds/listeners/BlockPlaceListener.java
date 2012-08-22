package org.seed419.founddiamonds.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.seed419.founddiamonds.*;
import org.seed419.founddiamonds.file.Config;
import org.seed419.founddiamonds.handlers.ListHandler;
import org.seed419.founddiamonds.handlers.WorldHandler;
import org.seed419.founddiamonds.sql.MySQL;

import java.util.HashSet;

/**
 * Attribute Only (Public) License
 * Version 0.a3, July 11, 2011
 * <p/>
 * Copyright (C) 2012 Blake Bartenbach <seed419@gmail.com> (@seed419)
 * <p/>
 * Anyone is allowed to copy and distribute verbatim or modified
 * copies of this license document and altering is allowed as long
 * as you attribute the author(s) of this license document / files.
 * <p/>
 * ATTRIBUTE ONLY PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 * <p/>
 * 1. Attribute anyone attached to the license document.
 * Do not remove pre-existing attributes.
 * <p/>
 * Plausible attribution methods:
 * 1. Through comment blocks.
 * 2. Referencing on a site, wiki, or about page.
 * <p/>
 * 2. Do whatever you want as long as you don't invalidate 1.
 *
 * @license AOL v.a3 <http://aol.nexua.org>
 */
public class BlockPlaceListener implements Listener {


    private HashSet<Location> placed = new HashSet<Location>();
    private FoundDiamonds fd;
    private MySQL mysql;


    public BlockPlaceListener(FoundDiamonds fd, MySQL mysql) {
        this.fd = fd;
        this.mysql = mysql;
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (WorldHandler.isEnabledWorld(event.getPlayer())) {
            if (isMonitoredBlock(event)) {
                addBlock(event);
            }
        }
    }

    public void addBlock(BlockPlaceEvent event) {
        if (fd.getConfig().getBoolean(Config.mysqlEnabled)) {
            mysql.updatePlacedBlockinSQL(event.getBlock().getLocation());
        } else {
            placed.add(event.getBlock().getLocation());
        }
    }

    public boolean isMonitoredBlock(BlockPlaceEvent event) {
        for (Node x : ListHandler.getBroadcastedBlocks()) {
            if (x.getMaterial() == event.getBlockPlaced().getType()) {
                return true;
            }
        }
        for (Node x : ListHandler.getAdminMessageBlocks()) {
            if (x.getMaterial() == event.getBlockPlaced().getType()) {
                return true;
            }
        }

        for (Node x : ListHandler.getLightLevelBlocks()) {
            if (x.getMaterial() == event.getBlockPlaced().getType()) {
                return true;
            }
        }
        return false;
    }

    public HashSet<Location> getFlatFilePlacedBlocks() {
        return placed;
    }


}
