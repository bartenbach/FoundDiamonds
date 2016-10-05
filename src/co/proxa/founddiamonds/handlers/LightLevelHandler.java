package co.proxa.founddiamonds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.file.Config;
import co.proxa.founddiamonds.util.Format;

import java.util.HashSet;

public class LightLevelHandler {

    private FoundDiamonds fd;
    private HashSet<Location> announcedLightBlocks = new HashSet<Location>();
    private final BlockFace[] lightFaces = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH,
            BlockFace.DOWN, BlockFace.UP};

    public LightLevelHandler(FoundDiamonds fd) {
        this.fd = fd;
    }

    public void handleLightLevelMonitor(final BlockDamageEvent event, final Material mat, final Player player) {
        final Block block = event.getBlock();
        final Location loc = event.getBlock().getLocation();
        if (blockIsBelowAcceptableLightLevel(block)) {
            if (!announcedLightBlocks.contains(loc)) {
                for (Location x : fd.getBlockCounter().getAllLikeBlockLocations(event.getBlock())) {
                     announcedLightBlocks.add(x);
                }
                if (!fd.getConfig().getBoolean(Config.silentMode)) {
                    player.sendMessage(ChatColor.RED + "Mining in the dark is dangerous, place a torch!");
                }
                if (fd.getConfig().getBoolean(Config.lightLevelAdminMessages)) {
                    sendLightAdminMessage(player, mat);
                }
                if ((fd.getConfig().getBoolean(Config.logLightLevelViolations))) {
                        fd.getLoggingHandler().logLightLevelViolation(mat, player);
                }
            }
            if (!fd.getConfig().getBoolean(Config.silentMode)) {
                event.setCancelled(true);
            }
        }
    }

    public void sendLightAdminMessage(final Player player, final Material mat) {
        String lightAdminMessage = ChatColor.YELLOW + player.getName() +
                ChatColor.GRAY +" is mining " + fd.getMapHandler().getLightLevelBlocks().get(mat) +
                Format.getFormattedName(mat, 1) + ChatColor.GRAY + " below "
                + ChatColor.WHITE + fd.getConfig().getString(Config.percentOfLightRequired) + " light";
        fd.getServer().getConsoleSender().sendMessage(lightAdminMessage);
        for (Player y : fd.getServer().getOnlinePlayers()) {
            if (fd.getPermissions().hasAdminManagementPerm(y)) {
                if (y != player) {
                    y.sendMessage(lightAdminMessage);
                }
            }
        }
    }

    public boolean blockIsBelowAcceptableLightLevel(final Block block) {
        double percentage = Double.parseDouble(fd.getConfig().getString(Config.percentOfLightRequired).replaceAll("%", ""));
        double levelToDisableAt = percentage / 15.0;
        int lightLevel = getLightLevel(block);
        return (lightLevel < levelToDisableAt);
    }

    public void checkAndClearLightLevelLocation(Location loc) {
        if(announcedLightBlocks.contains(loc)) {
            announcedLightBlocks.remove(loc);
        }
    }

    public int getLightLevel(final Block block) {
        int highestLevel = 0;
        int lightLevel;
        for (BlockFace y : lightFaces) {
            lightLevel = block.getRelative(y).getLightLevel();
            if (lightLevel > highestLevel) {
                highestLevel = lightLevel;
            }
        }
        return highestLevel;
    }
}
