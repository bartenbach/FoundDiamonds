package co.proxa.founddiamonds.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.file.Config;
import co.proxa.founddiamonds.util.Format;
import co.proxa.founddiamonds.util.PluginUtils;
import co.proxa.founddiamonds.util.Prefix;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

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
        String message = fd.getConfig().getString(Config.bcMessage).replace("@Prefix@", Prefix.getChatPrefix() + color).replace("@Player@",
                getBroadcastName(player) + (fd.getConfig().getBoolean(Config.useOreColors) ? color : "")).replace("@Number@",
                (blockTotal) == 500 ? "over 500" :String.valueOf(blockTotal)).replace("@BlockName@", matName).replace(
                "@LightLevel@", String.valueOf(lightLevel)).replace("@LightPercent@", formattedPercent + "%");
        String formatted = PluginUtils.customTranslateAlternateColorCodes('&', message);
        fd.getServer().getConsoleSender().sendMessage(formatted);
       
        if(fd.getConfig().getBoolean(Config.useBungeeCord)){
        	List<String> bungeeadmins = (List<String>) fd.getConfig().getList(Config.BungeeCordAdminList);
        	if(bungeeadmins.size() > 0){
        		for(String admin : bungeeadmins){
        			if(Bukkit.getPlayer(admin) == null){
        				ByteArrayDataOutput out = ByteStreams.newDataOutput();
                  		out.writeUTF("Message");
                  		out.writeUTF(admin);                  		  
                  		out.writeUTF(formatted);
                  		Player p = (Player)Bukkit.getOnlinePlayers().toArray()[0];
                  		p.sendPluginMessage(fd, "BungeeCord", out.toByteArray());        		
        			}            		       		  
            	}
        	}else{
        		System.out.println("[ERROR] Founddiamonds: Bungeecordsupport is enabled but no admins are defined. Can't send infos!");
        	}
        }
        
        for (Player x : fd.getServer().getOnlinePlayers()) {
            if (fd.getPermissions().hasBroadcastPerm(x) && fd.getWorldHandler().isEnabledWorld(x) && !fd.getAdminMessageHandler().receivedAdminMessage(x)) {
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
