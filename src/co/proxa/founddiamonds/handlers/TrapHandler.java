package co.proxa.founddiamonds.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.Trap;
import co.proxa.founddiamonds.file.Config;
import co.proxa.founddiamonds.util.BlockColor;
import co.proxa.founddiamonds.util.Format;
import co.proxa.founddiamonds.util.Prefix;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrapHandler {

	private FoundDiamonds fd;
    private static ArrayList<Trap> trapList = new ArrayList<Trap>(); // map linking traps to locations, the middle of the trap
    private static Map<Block,Trap> inverseList = new HashMap<Block,Trap>(); // map linking locations to traps

	public TrapHandler(FoundDiamonds fd) {
		this.fd = fd;
	}

	public void handleSetTrap(CommandSender sender, String[] args) {
		Material trapMaterial = Material.DIAMOND_ORE;
        boolean persistent = false;
		int depth = 0;

        Player player = (Player) sender; // MenuHandler verifies this is a player.
        if (args[args.length - 1].equalsIgnoreCase("true")) {
            persistent = true;
        }

        if (args.length == 3) {
            // either trap block specified with no depth, or depth specified with diamond blocks
            // note that the depth will almost *always* match a material ID, so this is NOT recommended.
            trapMaterial = Material.matchMaterial(args[2]);
            if (trapMaterial == null) {
                try {
                    depth = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sendTrapError(player);
                    return;
                }
            }
        } else if (args.length == 4) { // either new block format specification, or depth + old block formatting
            trapMaterial = Material.matchMaterial(args[2] + "_" + args[3]);
            if (trapMaterial == null) {
                try {
                    trapMaterial = Material.matchMaterial(args[2]);
                    depth = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    sendTrapError(player);
                    return;
                }
            }
        } else if (args.length == 5) { // new block format + depth
            trapMaterial = Material.matchMaterial(args[2] + "_" + args[3]);
            try {
                depth = Integer.parseInt(args[4]);
            } catch (NumberFormatException ex) {
                sendTrapError(player);
            }
        }
        if (trapMaterial != null) {
            createTrap(player, trapMaterial, depth, persistent);
        } else {
            sendTrapError(player);
        }
	}

    public void handleRemoveTrap(CommandSender sender, String[] args) {
        int id;
        if (args.length == 3) {
            try {
                id = Integer.parseInt(args[2]);
                removeTrapCmd(sender, id);
            } catch (NumberFormatException ex) {
                sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Trap ID number must be an integer");
            }
        } else {
            sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " You must specify a trap ID number to remove");
            sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Get the ID number from /fd trap list");
        }
    }

    public void handleListTraps(CommandSender sender, String[] args) {
        int page = 0;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Page number must be an integer");
                return;
            }
        }
        listTraps(sender, page);
    }

    private void sendTrapError(Player player) {
        player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED +" Unable to understand the entered trap format.");
        player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED +" Please ensure your formatting makes sense.");
        player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED +" If it does - please report this bug.");
    }

    private void createTrap(Player player, Material trapMaterial, int depth, boolean persistent) {
        if (trapMaterial != null && isSensibleTrapBlock(trapMaterial)) {
            Location trapLoc = player.getLocation().add(0, -(depth), 0);
            if (!isValidHeight(player, trapLoc)) { return; }
            Trap newTrap = new Trap(getTrapType(trapMaterial), trapMaterial, player.getName(), trapLoc, persistent);
            if (newTrap.createBlocks()) {
                player.sendMessage(Prefix.getMenuPrefix() + ChatColor.WHITE + "Trap ID [" + ChatColor.YELLOW
                        + trapList.size() + ChatColor.WHITE + "] set with " + BlockColor.getBlockColor(trapMaterial)
                        + Format.capitalize(Format.getFormattedName(trapMaterial, 1)) + ChatColor.WHITE + getFormattedDepthString(depth));
                trapList.add(newTrap);
            } else {
                player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Unable to place a trap here");
                player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " There's another one in the way!");
            }
        } else {
            player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Unable to set a trap with " + BlockColor.getBlockColor(trapMaterial)
                    + Format.getFormattedName(trapMaterial, 1));
            player.sendMessage(ChatColor.RED + "Use a valid block, for example, /fd trap gold ore");
        }
    }

    private String getFormattedDepthString(int depth) {
        if (depth > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ").append(ChatColor.WHITE).append(depth).append(" ")
                    .append((depth == 1 ? "block" : "blocks")).append(" below you");
            return sb.toString();
        }
        return "";
    }

    private byte getTrapType(Material mat) {
        if (mat == Material.EMERALD_ORE) {
            return 3;
        } else {
            return (byte) Math.ceil(Math.random() + 0.5);
        }
    }

    private boolean isValidHeight(Player player, Location trapLocation) {
        int maxHeight = player.getWorld().getMaxHeight();
        int y = trapLocation.getBlockY();
        if ((y - 2) < 0) {
            player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " I can't place a trap down there, sorry.");
            return false;
        } else if ((y - 1) > maxHeight) {
            player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " I can't place a trap this high, sorry.");
            return false;
        }
        return true;
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
                return trap.isBlock();
		}
	}

	public boolean isTrapBlock(Location loc) {
		return inverseList.containsKey(loc.getBlock());
	}

	public void handleTrapBlockBreak(BlockBreakEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Trap trap = inverseList.get(block);
		if (fd.getPermissions().hasPerm(player, "fd.trap")) {
			//player.sendMessage(ChatColor.AQUA + "Trap block removed");
            sendTrapRemovedMessage(player, trap);
            //trap.removeTrap();
		} else {
			String trapMessage;
			if (trap.isPersistent()) {
				trapMessage = ChatColor.YELLOW + player.getName() + ChatColor.RED + " just triggered a persistent trap block";
			} else {
				trapMessage = ChatColor.YELLOW + player.getName() + ChatColor.RED + " just triggered a trap block";
				//trap.removeTrap(); // traps are removed once triggered, persistent traps stay armed
			}
			for (Player x : fd.getServer().getOnlinePlayers()) {
				if ((fd.getPermissions().hasPerm(x, "fd.trap")) || fd.getPermissions().hasPerm(x, "fd.admin")) {
					x.sendMessage(trapMessage);
				}
			}
			fd.getServer().getConsoleSender().sendMessage(Prefix.getLoggingPrefix() + trapMessage);
			boolean banned = false;
			boolean kicked = false;
            boolean command = false;
			if (fd.getConfig().getBoolean(Config.kickOnTrapBreak)) {
				String kickMessage = fd.getConfig().getString(Config.kickMessage);
				player.kickPlayer(kickMessage);
				kicked = true;
			}
			if (fd.getConfig().getBoolean(Config.banOnTrapBreak)) {
				player.setBanned(true);
				banned = true;
			}
            if(fd.getConfig().getBoolean(Config.ExecutecommandOnTrapBreak)){
                String commandString = fd.getConfig().getString(Config.commandOnTrapBreak).replaceAll("//@player@",player.getName());
                Bukkit.getServer().dispatchCommand(player, commandString);
                command = true;
            }
			if (fd.getConfig().getBoolean(Config.logTrapBreaks)) {
				fd.getLoggingHandler().handleLogging(player, block, true, kicked, banned, command);
			}
		}
	}

    public static void listTraps(CommandSender sender, int page) { // TODO: Page numbers
        sender.sendMessage(Prefix.getMenuPrefix() + Format.formatMenuHeader("Active Traps"));
        if (trapList.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "  None");
            return;
        }
        if (sender.hasPermission("fd.trap.remove.all") || sender.isOp()) {
            if (page >= 0 && ( (page) *5 < trapList.size()) ) { //sane page specified?
                int id1 = (page)*5 ; //begin of the substring
                int id2 = (page +1) *5 -1;  //end of the substring
                if (id2 > trapList.size()) {
                    id2 = trapList.size()-1;
                }
                for (Trap object : trapList) {
                    TrapHandler.sendTrapListing(sender, object);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Page number is invalid");
            }
        } else if (sender.hasPermission("fd.trap.remove.self")) { // permission to remove, and thus to see own traps
            ArrayList<Trap> showList = new ArrayList<Trap>();
            for (Trap trap : trapList) {
                if (trap.getPlacer().equals(sender.getName())) {
                    showList.add(trap);
                }
            }
            if (page >= 0 && ( (page) *5 < showList.size()) ) { //sane page specified?
                int id1 = (page)*5 ; //begin of the substring
                int id2 = (page +1) *5 -1;  //end of the substring
                if (id2 > showList.size()) {
                    id2 = showList.size()-1;
                }
                for (Trap object : showList) {
                    TrapHandler.sendTrapListing(sender, object);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Page number is invalid");
            }
        } else {
            sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "You don't have permission to view any traps");
        }

    }

    public static void sendTrapListing(CommandSender sender, Trap trap) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.YELLOW + trap.getID() + ChatColor.WHITE + "] "
                + ChatColor.AQUA + DateFormat.getDateInstance(DateFormat.MEDIUM).format((trap.getTime())) + ChatColor.WHITE
                + " @ x" + Format.leftGreenParen + trap.getLocation().getBlockX() + Format.rightGreenParen
                + " y" + Format.leftGreenParen + trap.getLocation().getBlockY() + Format.rightGreenParen
                + " z" + Format.leftGreenParen + trap.getLocation().getBlockZ() + Format.rightGreenParen + " "
                + ChatColor.RED + (trap.isPersistent() ? "{Persistent}" : ChatColor.GREEN + "{Breakable}" ));
        sender.sendMessage("        " + BlockColor.getBlockColor(trap.getMaterial())
                + Format.capitalize(Format.getFormattedName(trap.getMaterial(), 1)) + ChatColor.WHITE
                + " placed by " + ChatColor.YELLOW + trap.getPlacer() + ChatColor.WHITE + " in world "
                + ChatColor.YELLOW + trap.getLocation().getWorld().getName());
    }

    public void removeTrapCmd(CommandSender sender, int id) {
        if (id >= 0 && id < trapList.size()) {
            Trap trap = trapList.get(id);
            if ((trap.getPlacer().equals(sender.getName())) &&
                    (sender.hasPermission("fd.trap.remove.self") || sender.hasPermission("fd.trap.remove.all"))) {
                //Block[] temp = trap.returnLocations(this.location.getWorld());
                //for (int i = 0; i < temp.length; i++) {
                    //temp[i].setType(oldMat[i]);
                    //inverseList.remove(temp[i]);
                //}
                trapList.remove(this);
                sendTrapRemovedMessage(sender, trapList.get(id));
            }
        } else {
            System.out.println("FoundDiamonds: What just happened?");
            //TODO then what happens?
        }
    }

    private void sendTrapRemovedMessage(CommandSender sender, Trap trap) {
        sender.sendMessage(Prefix.getMenuPrefix() + ChatColor.WHITE + "Trap ID " + ChatColor.WHITE + "["
                + ChatColor.YELLOW + trapList.indexOf(trap) + ChatColor.WHITE + "]" + ChatColor.GREEN +" removed successfully");
    }

    public ArrayList<Trap> getTrapList() {
        return trapList;
    }

    public static Map<Block, Trap> getInverseList() {
        return inverseList;
    }

}
