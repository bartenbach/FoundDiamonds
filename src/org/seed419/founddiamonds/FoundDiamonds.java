package org.seed419.founddiamonds;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.seed419.founddiamonds.listeners.BlockListener;
import org.seed419.founddiamonds.listeners.PlayerDamageListener;
import org.seed419.founddiamonds.metrics.MetricsLite;
import org.seed419.founddiamonds.sql.MySQL;

/* TODO
* Smarter trap blocks - remember material NOT just the location!  Prevents pistons and physics from tricking them.
* Implement Item IDs as an acceptable form of entering blocks
* Finish set menu, integrate with main menu
* Look into pulling stats from MC client?  Or MySQL?
* /fd top ?
* /

/*  Attribute Only (Public) License
        Version 0.a3, July 11, 2011

    Copyright (C) 2012 Blake Bartenbach <seed419@gmail.com> (@seed419)

    Anyone is allowed to copy and distribute verbatim or modified
    copies of this license document and altering is allowed as long
    as you attribute the author(s) of this license document / files.

    ATTRIBUTE ONLY PUBLIC LICENSE
    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

      1. Attribute anyone attached to the license document.
         * Do not remove pre-existing attributes.

         Plausible attribution methods:
            1. Through comment blocks.
            2. Referencing on a site, wiki, or about page.

      2. Do whatever you want as long as you don't invalidate 1.


@license AOL v.a3 <http://aol.nexua.org>*/



public class FoundDiamonds extends JavaPlugin {


    private final static String prefix = "[FD]";
    private final static String adminPrefix = ChatColor.RED + "[FD]";
    private final static String debugPrefix = "[FD Debug] ";
    private final static String loggerPrefix = "[FoundDiamonds]";
    private final Set<Location> trapBlocks = new HashSet<Location>();
    private final HashMap<Player, Boolean> jumpPotion = new HashMap<Player,Boolean>();
    private final static Logger log = Logger.getLogger("FoundDiamonds");
    private final MySQL mysql = new MySQL(this);
    private final BlockListener bl = new BlockListener(this, mysql);
    private final ListHandler lh = new ListHandler(this);
    private final PlayerDamageListener damage = new PlayerDamageListener(this);
    private final WorldManager wm = new WorldManager(this);
    private final FileHandler fh = new FileHandler(this, wm, bl);
    private static PluginDescriptionFile pdf;
    private String pluginName;
    private final static int togglePages = 2;
    private final static int configPages = 2;

    /*
     * Changelog:
     *
     */


    @Override
    public void onEnable() {
        pdf = this.getDescription();
        pluginName = pdf.getName();

        fh.initFileVariables();
        fh.checkFiles();
        wm.checkWorlds();

        /*Load the new lists*/
        lh.loadAllBlocks();

        bl.setMySQLEnabled(getConfig().getBoolean(Config.mysqlEnabled));

        /*Register events*/
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this.bl, this);
        pm.registerEvents(damage, this);

        startMetrics();

        mysql.getConnection();

        log.info(MessageFormat.format("[{0}] Enabled", pluginName));
    }

    @Override
    public void onDisable() {
        /*File I/O*/
        log.info(MessageFormat.format("[{0}] Saving all data...", pluginName));
        String info = "This file stores your trap block locations.";
        String info2 = "If you have any issues with traps - feel free to delete this file.";
        boolean temp = fh.writeBlocksToFile(fh.getTrapsFile(), trapBlocks, info, info2);
        String info5 = "This file stores blocks that would be announced that players placed";
        String info6 = "If you'd like to announce these placed blocks, feel free to delete this file.";
        boolean temp3 = fh.writeBlocksToFile(fh.getPlacedFile(), bl.getCantAnnounce(), info5, info6);
        if (temp && temp3) {
            log.info(MessageFormat.format("[{0}] Data successfully saved.", pluginName));
        } else {
            log.warning(MessageFormat.format("[{0}] Couldn't save blocks to files!", pluginName));
            log.warning(MessageFormat.format("[{0}] You could try deleting .placed and .traps if they exist", pluginName));
        }
        log.info(MessageFormat.format("[{0}] Disabled", pluginName));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            StringBuilder sb = new StringBuilder();
            sb.append(commandLabel).append(" ");
            if (args.length > 0) {
                for (String x : args) {
                    sb.append(x).append(" ");
                }
            }
            String cmd = sb.toString();
            log.info("[PLAYER_COMMAND] " + player.getName() + ": /" + cmd);
        }
        if (((commandLabel.equalsIgnoreCase("fd")) || commandLabel.equalsIgnoreCase("founddiamonds"))) {
            if (args.length == 0) {
                Menu.printMainMenu(this, sender);
                return true;
            } else {
                String arg = args[0];
                if (arg.equalsIgnoreCase("admin")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.manage.admin.add") || hasPerms(player, "fd.manage.admin.remove")
                                || hasPerms(player, "fd.manage.admin.list")) {
                            Menu.handleAdminMenu(this, sender, args);
                        } else {
                            sendPermissionsMessage(player);
                        }
                    } else {
                        Menu.handleAdminMenu(this, sender, args);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("bc") || arg.equalsIgnoreCase("broadcast")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.manage.bc.add") || hasPerms(player, "fd.manage.bc.remove")
                                || hasPerms(player, "fd.manage.bc.list")) {
                            Menu.handleBcMenu(this, sender, args);
                        } else {
                            sendPermissionsMessage(player);
                        }
                    } else {
                        Menu.handleBcMenu(this, sender, args);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("config")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.config")) {
                            if (args.length == 2) {
                                if (args[1].equalsIgnoreCase("2")) {
                                    Menu.showConfig2(this, sender);
                                }
                            } else {
                                Menu.showConfig(this, sender);
                            }
                        } else {
                            sendPermissionsMessage(player);
                        }
                    }
                } else if (arg.equalsIgnoreCase("debug")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.toggle")) {
                            if (args.length == 2) {
                                if (args[1].equalsIgnoreCase("2")) {
                                    Menu.showConfig2(this, sender);
                                }
                            } else {
                                Menu.showConfig(this, sender);
                            }
                        } else {
                            sendPermissionsMessage(player);
                        }
                    }
                } else if (arg.equalsIgnoreCase("light")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.manage.light.add") || hasPerms(player, "fd.manage.light.remove")
                                || hasPerms(player, "fd.manage.light.list")) {
                            Menu.handleLightMenu(this, sender, args);
                        } else {
                            sendPermissionsMessage(player);
                        }
                    } else {
                        Menu.handleLightMenu(this, sender, args);
                    }
                    return true;
               } else if (arg.equalsIgnoreCase("reload")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.reload")) {
                            reloadConfig();
                            saveConfig();
                            sender.sendMessage(getPrefix() + ChatColor.AQUA + " Configuration saved and reloaded.");
                        } else {
                            sendPermissionsMessage(player);
                        }
                    } else {
                        reloadConfig();
                        saveConfig();
                        sender.sendMessage(getPrefix() + ChatColor.AQUA + " Configuration saved and reloaded.");
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("set")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.toggle")) {
                            Menu.handleSetMenu(this, sender, args);
                        } else {
                            sendPermissionsMessage(player);
                        }
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("stats")) {
                    if (sender instanceof Player) {
                        mysql.printStats(player);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("toggle")) {
                    if (!hasPerms(sender, "fd.toggle")) {
                        sendPermissionsMessage(sender);
                    } else {
                        if (args.length == 1) {
                            Menu.showToggle(sender);
                        } else  if (args.length == 2) {
                            arg = args[1];
                            handleToggle(sender, arg);
                        } else {
                            sender.sendMessage(getPrefix() + ChatColor.RED + " Invalid number of arguments.");
                            sender.sendMessage(ChatColor.RED + "See '/fd toggle' for the list of valid arguments.");
                        }
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("trap")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.trap")) {
                            handleTrap(player, args);
                        } else {
                            sendPermissionsMessage(player);
                        }
                    } else {
                        sender.sendMessage(getPrefix() + ChatColor.DARK_RED + " Can't set a trap from the console.");
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("world")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.world")) {
                            wm.handleWorldMenu(sender, args);
                        } else {
                            sendPermissionsMessage(player);
                        }
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("version")) {
                    Menu.showVersion(sender);
                    return true;
                } else if (arg.equalsIgnoreCase("diamond") || arg.equalsIgnoreCase("gold")
                        || arg.equalsIgnoreCase("lapis") || arg.equalsIgnoreCase("iron")
                        || arg.equalsIgnoreCase("redstone") || arg.equalsIgnoreCase("coal")) {
                    if (this.getConfig().getBoolean(Config.mysqlEnabled)) {
                        if (args[0].equalsIgnoreCase("diamond")) {
                            mysql.handleTop(sender, "diamond");
                        } else if (args[0].equalsIgnoreCase("gold")) {
                            mysql.handleTop(sender, "gold");
                        } else if (args[0].equalsIgnoreCase("lapis")) {
                            mysql.handleTop(sender, "lapis");
                        } else if (args[0].equalsIgnoreCase("iron")) {
                            mysql.handleTop(sender, "iron");
                        } else if (args[0].equalsIgnoreCase("coal")) {
                            mysql.handleTop(sender, "coal");
                        } else if (args[0].equalsIgnoreCase("redstone")) {
                            mysql.handleTop(sender, "redstone");
                        }
                    }
                    return true;
                } else {
                        sender.sendMessage(getPrefix() + ChatColor.DARK_RED + " Unrecognized command '"
                                + ChatColor.WHITE + args[0] + ChatColor.DARK_RED + "'");
                    return true;
                }
            }
        }
        return false;
    }




    /*
     * Trap Blocks
     */
    private void handleTrap(Player player, String[] args) {
        Location playerLoc = player.getLocation();
        Material trap;
        String item;
        int depth=0;
        if (args.length == 1) {
            trap = Material.DIAMOND_ORE;
            item = "Diamond ore";
        } else if (args.length == 2) {	//either trap block specified, old format, or depth specified, assuming diamond blocks
            item = args[1];
            trap = Material.matchMaterial(item);
            if(trap==null) {
            	try {
            		depth = Integer.parseInt(args[1]);
            	}catch(NumberFormatException ex) {
            		player.sendMessage(ChatColor.RED + "Please specifiy a valid number as depth");
            		return;
            	}
            	item = "Diamond ore";
            	trap = Material.DIAMOND_ORE;
            }
        } else if (args.length == 3) {	//either new block format specification, or depth + old block formatting
            item = args[1] + "_" + args[2];
            trap = Material.matchMaterial(item);
            if(trap == null) {
            	try {
            		depth = Integer.parseInt(args[2]);
            	}catch(NumberFormatException ex) {
               		player.sendMessage(ChatColor.RED + "Please specifiy a valid number as depth");
            		return;
            	}
            	item = args[1];
            	trap = Material.matchMaterial(item);
            }
        }else if(args.length == 4) {	//new block format + depth
            item = args[1] + "_" + args[2];
            trap = Material.matchMaterial(item);
            try {
        		depth = Integer.parseInt(args[3]);
        	}catch(NumberFormatException ex) {
           		player.sendMessage(ChatColor.RED + "Please specifiy a valid number as depth");
        		return;
        	}
        }
        	else {
            player.sendMessage(getPrefix() + ChatColor.RED + " Invalid number of arguments");
            player.sendMessage(ChatColor.RED + "Is it a block and a valid item? Try /fd trap gold ore");
            return;
        }
        if (trap != null && trap.isBlock()) {
            getTrapLocations(player, playerLoc, trap, depth);
        } else {
            player.sendMessage(getPrefix() + ChatColor.RED + " Unable to set a trap with '" + item + "'");
            player.sendMessage(ChatColor.RED + "Is it a block and a valid item? Try /fd trap gold ore");
        }
    }

    private void getTrapLocations(Player player, Location playerLoc, Material trap, int depth) {
        int x = playerLoc.getBlockX();
        int y = playerLoc.getBlockY() - depth;
        int maxHeight = player.getWorld().getMaxHeight();
        if ((y - 2) < 0) {
            player.sendMessage(getPrefix() + ChatColor.RED + " I can't place a trap down there, sorry.");
            return;
        } else if ((y - 1) > maxHeight) {
            player.sendMessage(getPrefix() + ChatColor.RED + " I can't place a trap this high, sorry.");
            return;
        }
        int z = playerLoc.getBlockZ();
        World world = player.getWorld();
        int randomnumber = (int)(Math.random() * 100);
        if ((randomnumber >= 0) && randomnumber < 50) {
            Block block1 = world.getBlockAt(x, y - 1, z);
            Block block2 = world.getBlockAt(x, y - 2, z + 1);
            Block block3 = world.getBlockAt(x - 1, y - 2, z);
            Block block4 = world.getBlockAt(x, y - 2, z);
            handleTrapBlocks(player, trap, block1, block2, block3, block4);
        } else if (randomnumber >= 50) {
            Block block1 = world.getBlockAt(x, y - 1, z);
            Block block2 = world.getBlockAt(x - 1, y - 2, z);
            Block block3 = world.getBlockAt(x , y - 2, z);
            Block block4 = world.getBlockAt(x -1, y - 1, z);
            handleTrapBlocks(player, trap, block1, block2, block3, block4);
        }
    }

    public void handleTrapBlocks(Player player, Material trap, Block block1, Block block2, Block block3, Block block4) {
        trapBlocks.add(block1.getLocation());
        trapBlocks.add(block2.getLocation());
        trapBlocks.add(block3.getLocation());
        trapBlocks.add(block4.getLocation());
        if (trap == Material.EMERALD_ORE) {
        	block1.setType(trap);
        }
        else {
        	block1.setType(trap);
        	block2.setType(trap);
        	block3.setType(trap);
        	block4.setType(trap);
        	}
        player.sendMessage(getPrefix() + ChatColor.AQUA + " Trap set using " + trap.name().toLowerCase().replace("_", " "));
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Set<Location> getTrapBlocks() {
        return trapBlocks;
    }




    /*
     * Toggle handler
     */
    private boolean handleToggle(CommandSender sender, String arg) {
        if (arg.equalsIgnoreCase("creative")) {
            getConfig().set(Config.disableInCreative, !getConfig().getBoolean(Config.disableInCreative));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("ops")) {
            getConfig().set(Config.opsAsFDAdmin, !getConfig().getBoolean(Config.opsAsFDAdmin));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("kick")) {
            getConfig().set(Config.kickOnTrapBreak, !getConfig().getBoolean(Config.kickOnTrapBreak));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("ban") || arg.equalsIgnoreCase("bans")) {
            getConfig().set(Config.banOnTrapBreak, !getConfig().getBoolean(Config.banOnTrapBreak));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("trapalerts")) {
            getConfig().set(Config.adminAlertsOnAllTrapBreaks, !getConfig().getBoolean(Config.adminAlertsOnAllTrapBreaks));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("items")) {
            getConfig().set(Config.itemsForFindingDiamonds, !getConfig().getBoolean(Config.itemsForFindingDiamonds));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("logging")) {
            getConfig().set(Config.logDiamondBreaks, !getConfig().getBoolean(Config.logDiamondBreaks));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("spells")) {
            getConfig().set(Config.potionsForFindingDiamonds, !getConfig().getBoolean(Config.potionsForFindingDiamonds));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("cleanlog")) {
            getConfig().set(Config.cleanLog, !getConfig().getBoolean(Config.cleanLog));
            if (!FileHandler.getCleanLog().exists()) {
                try {
                    boolean successful = FileHandler.getCleanLog().createNewFile();
                    if (successful) {sender.sendMessage(getPrefix() + ChatColor.DARK_GREEN +" Cleanlog created.");}
                    } catch (IOException ex) {
                    sender.sendMessage(getPrefix() + ChatColor.DARK_RED + " Uh-oh...couldn't create CleanLog.txt");
                    Logger.getLogger(FoundDiamonds.class.getName()).log(Level.SEVERE, "Failed to create CleanLog file.", ex);
                }
            }
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("nick") || arg.equalsIgnoreCase("nicks")) {
            getConfig().set(Config.useNick, !getConfig().getBoolean(Config.useNick));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("debug")) {
            getConfig().set(Config.debug, !getConfig().getBoolean(Config.debug));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("prefix")) {
            sender.sendMessage(getPrefix() + ChatColor.DARK_RED + " Prefix is now a part of the broadcast message.");
            sender.sendMessage(getPrefix() + ChatColor.DARK_RED + " Please modify it in the config file.");
        } else if (arg.equalsIgnoreCase("2")) {
            Menu.showToggle2(sender);
        } else {
            sender.sendMessage(getPrefix() + ChatColor.RED + " Argument '" + arg + "' unrecognized.");
            sender.sendMessage(ChatColor.RED + "See '/fd toggle' for the list of valid arguments.");
            return false;
        }
        return true;
    }




    /*
     * Misc
     */
    public static boolean isRedstone(Block m) {
        return (m.getType() == Material.REDSTONE_ORE || m.getType() == Material.GLOWING_REDSTONE_ORE);
    }

    public static boolean isRedstone(Material m) {
        return (m == Material.REDSTONE_ORE || m == Material.GLOWING_REDSTONE_ORE);
    }

    public boolean hasPerms(CommandSender sender, String permission) {
        return (sender.hasPermission(permission) || (getConfig().getBoolean(Config.opsAsFDAdmin) && sender.isOp()));
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public HashMap<Player, Boolean> getJumpPotion() {
        return jumpPotion;
    }

    public static int getTogglePages() {
        return togglePages;
    }

    public static int getConfigPages() {
        return configPages;
    }

    public static void sendPermissionsMessage(CommandSender sender) {
        sender.sendMessage(getPrefix() + ChatColor.RED + " You don't have permission to do that.");
        log.warning(sender.getName() + " was denied access to a command.");
    }

    public Logger getLog() {
        return log;
    }

    public String getPluginName() {
        return pluginName;
    }

    public static PluginDescriptionFile getPdf() {
        return pdf;
    }



    /*
     * Prefix
     */
    public static String getPrefix() {
        return prefix;
    }

    public static String getAdminPrefix() {
        return adminPrefix;
    }

    public static String getDebugPrefix() {
        return debugPrefix;
    }

    public static String getLoggerPrefix() {
        return loggerPrefix;
    }


    /*
     * Metrics
     */
    private void startMetrics() {
        if (this.getConfig().getBoolean(Config.metrics)) {
            try {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();
            } catch (IOException e) {}
        }
    }

}
