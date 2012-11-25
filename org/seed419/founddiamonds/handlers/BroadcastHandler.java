package org.seed419.founddiamonds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.seed419.founddiamonds.FoundDiamonds;
import org.seed419.founddiamonds.file.Config;
import org.seed419.founddiamonds.util.Format;
import org.seed419.founddiamonds.util.PluginUtils;
import org.seed419.founddiamonds.util.Prefix;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Attribute Only (Public) License
 * Version 0.a3, July 11, 2011
 *
 * Copyright (C) 2012 Blake Bartenbach <seed419@gmail.com> (@seed419)
 *
 * Anyone is allowed to copy and distribute verbatim or modified
 * copies of this license document and altering is allowed as long
 * as you attribute the author(s) of this license document / files.
 *
 * ATTRIBUTE ONLY PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 * 1. Attribute anyone attached to the license document.
 * Do not remove pre-existing attributes.
 *
 * Plausible attribution methods:
 * 1. Through comment blocks.
 * 2. Referencing on a site, wiki, or about page.
 *
 * 2. Do whatever you want as long as you don't invalidate 1.
 *
 * @license AOL v.a3 <http://aol.nexua.org>
 */
public class BroadcastHandler {


    private FoundDiamonds fd;


    public BroadcastHandler(FoundDiamonds fd) {
        this.fd = fd;
    }

    public void handleBroadcast(final Material mat,final int blockTotal, final Player player, final int lightLevel) {
        broadcastFoundBlock(player, mat, blockTotal, lightLevel);
        if (mat== Material.DIAMOND_ORE) {
            if (fd.getConfig().getBoolean(Config.potionsForFindingDiamonds)) {
                fd.getPotionHandler().handlePotions(player);
            }
            if (fd.getConfig().getBoolean(Config.itemsForFindingDiamonds)) {
                fd.getItemHandler().handleRandomItems(player);
            }
        }
    }


    private void broadcastFoundBlock(final Player player, final Material mat, final int blockTotal, final int lightLevel) {
        String matName = Format.getFormattedName(mat, blockTotal);
        ChatColor color = fd.getMapHandler().getBroadcastedBlocks().get(mat);
        double lightPercent = ((double)lightLevel / 15) * 100;
        DecimalFormat df = new DecimalFormat("##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String formattedPercent = df.format(lightPercent);
        if (fd.getConfig().getBoolean(Config.debug)) {
            System.out.println("Decimal: " + lightPercent + " Formatted: " + formattedPercent);
        }
        String message = fd.getConfig().getString(Config.bcMessage).replace("@Prefix@", Prefix.getChatPrefix() + color).replace("@Player@",
                getBroadcastName(player) + (fd.getConfig().getBoolean(Config.useOreColors) ? color : "")).replace("@Number@",
                (blockTotal) == 500 ? "over 500" :String.valueOf(blockTotal)).replace("@BlockName@", matName).replace(
                "@LightLevel@", String.valueOf(lightLevel)).replace("@LightPercent@", formattedPercent + "%");
        String formatted = PluginUtils.customTranslateAlternateColorCodes('&', message);
        fd.getServer().getConsoleSender().sendMessage(formatted);
        for (Player x : fd.getServer().getOnlinePlayers()) {
            if (fd.getPermissions().hasBroadcastPerm(x) && fd.getWorldHandler().isEnabledWorld(x) && !fd.getAdminMessageHandler().recievedAdminMessage(x)) {
                x.sendMessage(formatted);
            }
        }
        if (fd.getConfig().getBoolean(Config.cleanLog)) {
            fd.getLoggingHandler().writeToCleanLog(matName, blockTotal, player.getName());
        }
    }

    private String getBroadcastName(Player player) {
        return (fd.getConfig().getBoolean(Config.useNick) ? player.getDisplayName() : player.getName());
    }
}
