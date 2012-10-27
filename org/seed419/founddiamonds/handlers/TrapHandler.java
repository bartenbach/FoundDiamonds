package org.seed419.founddiamonds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.seed419.founddiamonds.FoundDiamonds;
import org.seed419.founddiamonds.Trap;
import org.seed419.founddiamonds.file.Config;
import org.seed419.founddiamonds.util.Prefix;

import java.util.ArrayList;

/**
 * Attribute Only (Public) License
 * Version 0.a3, July 11, 2011
 * <p/>
 * Copyright (C) 2012 Blake Bartenbach <seed419@gmail.com> (@seed419)
 * <p/>
 * Anyone is allowed to copy and distribute verbatim or modified copies of this license document and altering is allowed as long as you attribute the author(s)
 * of this license document / files.
 * <p/>
 * ATTRIBUTE ONLY PUBLIC LICENSE TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 * <p/>
 * 1. Attribute anyone attached to the license document. Do not remove pre-existing attributes.
 * <p/>
 * Plausible attribution methods: 1. Through comment blocks. 2. Referencing on a site, wiki, or about page.
 * <p/>
 * 2. Do whatever you want as long as you don't invalidate 1.
 * 
 * @license AOL v.a3 <http://aol.nexua.org>
 */
public class TrapHandler {

	private FoundDiamonds fd;
	private final ArrayList<Trap> trapBlocks = new ArrayList<Trap>();

	public TrapHandler(FoundDiamonds fd) {
		this.fd = fd;
	}

	public void handleTrap(Player player, String[] args) {
		Location playerLoc = player.getLocation();
		Material trap =Material.AIR;
		String item = "";
		int depth = 0;
		boolean persistant = false;
		if ((args[args.length - 1] == "true" || args[args.length - 1] == "false")) {
			persistant = Boolean.parseBoolean(args[args.length - 1]);
			String[] temp = new String[args.length - 1];
			for (int i = 0; i < args.length - 1; i++) {
				temp[i] = args[i];
			}
			args = temp; // continue without the persistant bool in the end, this way I dont have to recode all the command interpreting
			
		}
		if (args.length == 1) {
			trap = Material.DIAMOND_ORE;
			item = "Diamond ore";
		}
		if(args.length > 1){
		if (args[1].equalsIgnoreCase("menu")) {
			int page = 0;
			if (args.length == 3) {
				try {
					page = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
					return;
				}
			}
			Trap.Menu(player, page);
			return;
		}
		if (args[1].equalsIgnoreCase("remove")) {
			int id;
			if (args.length == 3) {
				try {
					id = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
					return;
				}
				Trap.removeTrapCmd(player, id);
			} else {
				player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
			}
			return;
		} else if (args.length == 2) { // either trap block specified, old format, or depth specified, assuming diamond blocks
			item = args[1];
			trap = Material.matchMaterial(item);
			if (trap == null) {
				try {
					depth = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
					return;
				}
				item = "Diamond ore";
				trap = Material.DIAMOND_ORE;
			}
		} else if (args.length == 3) { // either new block format specification, or depth + old block formatting
			item = args[1] + "_" + args[2];
			trap = Material.matchMaterial(item);
			if (trap == null) {
				try {
					depth = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
					return;
				}
				item = args[1];
				trap = Material.matchMaterial(item);
			}
		} else if (args.length == 4) { // new block format + depth
			item = args[1] + "_" + args[2];
			trap = Material.matchMaterial(item);
			try {
				depth = Integer.parseInt(args[3]);
			} catch (NumberFormatException ex) {
				player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
				return;
			}
		} else {
			player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Invalid number of arguments");
			player.sendMessage(ChatColor.RED + "Is it a block and a valid item? Try /fd trap gold ore");
			return;
		}
		}
		if (trap != null && trap.isBlock()) {
			if (isSensibleTrapBlock(trap)) {
				Location trapLoc = playerLoc.add(0, -(depth+1), 0);
				int maxHeight = player.getWorld().getMaxHeight();
				int y = trapLoc.getBlockY();
				if ((y - 2) < 0) {
					player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " I can't place a trap down there, sorry.");
					return;
				} else if ((y - 1) > maxHeight) {
					player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " I can't place a trap this high, sorry.");
					return;
				}
				byte type = 0; // determination of the traptype
				if (trap == Material.EMERALD_ORE) {
					type = 3;
				} else {
					type = (byte) Math.ceil(Math.random() + 0.5); // should work.
				}
				Trap temp = new Trap(type, trap, player, trapLoc, persistant,item);
			} else {
				player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Unable to set a trap with " + item);
				player.sendMessage(ChatColor.RED + "Surely you can use a more sensible block for a trap.");
			}
		} else {
			player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Unable to set a trap with '" + item + "'");
			player.sendMessage(ChatColor.RED + "Is it a valid trap block? Try /fd trap gold ore");
		}
	}
	

	private boolean isSensibleTrapBlock(Material trap) {
		switch (trap) {
		case TORCH:
		case GRAVEL:
		case SAND:
		case DIRT:
		case GRASS:
		case VINE:
		case LEAVES:
		case DEAD_BUSH:
		case REDSTONE_TORCH_ON:
		case REDSTONE_TORCH_OFF:
		case WATER:
		case LAVA:
			return false;
		default:
			return true;
		}
	}

	public boolean isTrapBlock(Location loc) {
		return Trap.getInverselist().containsKey(loc.getBlock());
	}

	public void handleTrapBlockBreak(BlockBreakEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Trap trap = Trap.getInverselist().get(block);
		if (fd.getPermissions().hasPerm(player, "fd.trap")) {
			player.sendMessage(ChatColor.AQUA + "Trap block removed");
			trap.removeTrap();
		} else {
			String trapMessage;
			if (trap.isPersistant()) {
				trapMessage = ChatColor.YELLOW + player.getName() + ChatColor.RED + " just triggered a persistent trap";
			} else {
				trapMessage = ChatColor.YELLOW + player.getName() + ChatColor.RED + " just triggered a trap block";
				trap.removeTrap(); // traps are removed once triggered, persistent traps stay armed
				// someone else might accidently follow the xrayer's path, and get kicked/banned as well
			}
			for (Player x : fd.getServer().getOnlinePlayers()) {
				if ((fd.getPermissions().hasPerm(x, "fd.trap")) || fd
						.getPermissions().hasPerm(x, "fd.admin")) {
					x.sendMessage(trapMessage);
				}
			}
			fd.getServer().getConsoleSender()
					.sendMessage(Prefix.getLoggingPrefix() + trapMessage);
			boolean banned = false;
			boolean kicked = false;
			if (fd.getConfig().getBoolean(Config.kickOnTrapBreak)) {
				String kickMessage = fd.getConfig().getString(
						Config.kickMessage);
				player.kickPlayer(kickMessage);
				kicked = true;
			}
			if (fd.getConfig().getBoolean(Config.banOnTrapBreak)) {
				player.setBanned(true);
				banned = true;
			}
			if (fd.getConfig().getBoolean(Config.logTrapBreaks)) {
				fd.getLoggingHandler().handleLogging(player, block, true,
						kicked, banned);
			}
		}
	}

	public ArrayList<Trap> getTrapBlocks() {
		return Trap.getList();
	}

}
