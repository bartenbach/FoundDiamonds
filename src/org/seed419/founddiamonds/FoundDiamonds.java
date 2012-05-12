package org.seed419.founddiamonds;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.seed419.founddiamonds.listeners.*;
import org.seed419.founddiamonds.metrics.MetricsLite;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/* TODO
* AdminMap memory?
* VERSITALE LISTS - in progress
* Smarter trap blocks - remember material.
* Finish set menu, integrate with main menu
* Look into pulling stats from MC client?  Or MySQL?
* Finish config and toggle
* /fd top ?
* Re-implement admin messages
* implement light level block list

/*
* Changelog:
* Implemented versatile lists that can be edited to whatever you want!
* Made fd log player commands to the console, including failed command attempts.
* Fixed a terrible admin message bug :C
* Moved prefix to broadcast message as @Prefix@ instead of having a separate option for it.
* Fixed color bug when using nicknames in broadcast (now looks way better imo)
* Fixed a bug with random items not even working -.-
* Added 3 new potions.
* Made potion messages more descriptive, and configurable.
* Fixed bugs with menus and made other improvements to them.
* Traps can now be set without using '_' characters, and just spaces (/fd trap gold ore)
* Fixed a bug with /fd reload   (The fuck was the bug?)
* Refactored a TON of code.

*
  */

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

    private final File mainDir = new File("plugins/founddiamonds/");
    private final File logs = new File(getDataFolder(), "log.txt");
    private final File traps = new File(getDataFolder(), ".traplocations");
    private final File cleanLog = new File(getDataFolder(), "cleanlog.txt");
    private final File configFile = new File(getDataFolder(), "config.yml");
    private final File placed = new File(getDataFolder(), ".placed");
    private final static String prefix = "[FD]";
    private final static String adminPrefix = ChatColor.RED + "[FD Admin]" + ChatColor.YELLOW;
    private final static String debugPrefix = "[FD Debug] ";
    private final static String loggerPrefix = "[founddiamonds]";
    private final List<Location> trapBlocks = new LinkedList<Location>();
    private static final List<Location> announcedBlocks = new LinkedList<Location>();
    private static final List<Location> placedBlocks = new LinkedList<Location>();
    private final HashMap<Player, Boolean> adminMessagePlayers = new HashMap<Player, Boolean>();
    private final HashMap<Player, Boolean> jumpPotion = new HashMap<Player,Boolean>();
    private final static Logger log = Logger.getLogger("founddiamonds");
    private final BlockBreakListener breakListener = new BlockBreakListener(this);
    private final BlockPlaceListener placeListener = new BlockPlaceListener(this);
    private final ListHandler lh = new ListHandler(this);
    private final Config config = new Config(this);
    private final JoinListener join = new JoinListener(this);
    private final QuitListener quit = new QuitListener(this);
    private final PlayerDamageListener damage = new PlayerDamageListener(this);
    private final WorldManager wm = new WorldManager(this);
    private String pluginName;
    private boolean printed = false;
    private final static int togglePages = 2;
    private final static int configPages = 2;


    @Override
    public void onEnable() {

        if (configFile.exists()) {
            System.out.println("Config file exists!");
        } else {
            System.out.println("Config file doesn't exist.");
        }
        checkFiles();
        wm.checkWorlds();

        /*Load the new lists*/
        lh.loadAllBlocks();

        /*Register events*/
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this.breakListener, this);
        pm.registerEvents(this.join, this);
        pm.registerEvents(this.quit, this);
        pm.registerEvents(this.placeListener, this);
        pm.registerEvents(damage, this);

	    final PluginDescriptionFile pdf = this.getDescription();
        pluginName = pdf.getName();

        startMetrics();

        log.info(MessageFormat.format("[{0}] Enabled", pluginName));
    }

    @Override
    public void onDisable() {
        saveLists();

        log.info(MessageFormat.format("[{0}] Saving all data...", pluginName));
        String info = "This file stores your trap block locations.";
        String info2 = "If you have any issues with traps - feel free to delete this file.";
        boolean temp = writeBlocksToFile(traps, trapBlocks, info, info2);
        String info5 = "This file stores blocks that would be announced that players placed";
        String info6 = "If you'd like to announce these placed blocks, feel free to delete this file.";
        boolean temp3 = writeBlocksToFile(placed, placedBlocks, info5, info6);
        if (temp && temp3) {
            log.info(MessageFormat.format("[{0}] Data successfully saved.", pluginName));
        } else {
            log.warning(MessageFormat.format("[{0}] Couldn't save blocks to files!", pluginName));
            log.warning(MessageFormat.format("[{0}] You could try deleting .placed and .traplocations", pluginName));
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
                } else if (arg.equalsIgnoreCase("bc")) {
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
                } else if (arg.equalsIgnoreCase("toggle")) {
                    if (sender instanceof Player) {
                        if (hasPerms(player, "fd.toggle")) {
                            if (args.length == 1) {
                                Menu.showToggle(sender);
                            } else  if (args.length == 2) {
                                arg = args[1];
                                handleToggle(sender, arg);
                            } else {
                                sender.sendMessage(getPrefix() + ChatColor.RED + " Invalid number of arguments.");
                                sender.sendMessage(ChatColor.RED + "See '/fd toggle' for the list of valid arguments.");
                            }
                        } else {
                            sendPermissionsMessage(player);
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
     * Configuration File
     */
    public void loadYaml() {
        try {
            System.out.println("printing path now.");
            System.out.println("path: " + getConfig().getCurrentPath());
            getConfig().options().copyDefaults(true);
            getConfig().load(configFile);
        } catch (FileNotFoundException ex) {
            log.severe(MessageFormat.format("[{0}] Couldn't find config.yml {1}", pluginName, ex));
        } catch (IOException ex) {
            log.severe(MessageFormat.format("[{0}] Unable to load configuration file {1}", pluginName, ex));
        } catch (InvalidConfigurationException ex) {
            log.severe(MessageFormat.format("[{0}] Unable to load configuration file {1}", pluginName, ex));
        }
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
        block1.setType(trap);
        block2.setType(trap);
        block3.setType(trap);
        block4.setType(trap);
        player.sendMessage(getPrefix() + ChatColor.AQUA + " Trap set using " + trap.name().toLowerCase().replace("_", " "));
    }


    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<Location> getTrapBlocks() {
        return trapBlocks;
    }




    /*
     * File handlers
     */
    private boolean writeBlocksToFile(File file, List<Location> blockList, String info, String info2) {
        if (blockList.size() > 0) {
            if (this.getDataFolder().exists()) {
                PrintWriter out = null;
                try {
                    file.createNewFile();
                    out =  new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
                    out.write("# " + info);
                    out.println();
                    out.write("# " + info2);
                    out.println();
                    for (Iterator<Location> it = blockList.iterator(); it.hasNext();) {
                        Location m = it.next();
                        out.write(m.getWorld().getName() + ";" + m.getX() + ";" + m.getY() + ";" + m.getZ());
                        out.println();
                    }
                } catch (IOException ex) {
                    log.severe(MessageFormat.format("[{0}] Error writing blocks to file!", pluginName, file.getName()));
                } finally {
                    close(out);
                }
                return true;
            } else {
                if (!printed) {
                    log.warning(MessageFormat.format("[{0}] Plugin folder not found.  Did you delete it?", pluginName));
                    printed = true;
                    return false;
                }
                return false;
            }
        } else {
            if (file.exists()) {
                file.delete();
            }
            return true;
        }
    }

    private void readBlocksFromFile(File file, List<Location> list) {
        BufferedReader b = null;
        try {
            b = new BufferedReader(new FileReader(file));
            String strLine = b.readLine();
            while (strLine != null) {
                if (!strLine.startsWith("#")) {
                    try {
                        String[] fs = strLine.split(";");
                        Location lo = new Location(getServer().getWorld(fs[0]), Double.parseDouble(fs[1]),
                            Double.parseDouble(fs[2]), Double.parseDouble(fs[3]));
                        list.add(lo);
                    } catch (Exception ex) {
                        log.severe(MessageFormat.format("[{0}] Invalid block in file.  Please delete the founddiamonds folder.", pluginName));
                    }
                }
                strLine = b.readLine();
            }
        } catch (Exception ex) {
            log.severe(MessageFormat.format("[{0}] Unable to read blocks from file, {1}", pluginName, ex));
        } finally {
            close(b);
        }
    }

    private void checkFiles() {
        if (!mainDir.exists()) {
            mainDir.mkdir();
        }
        if (!logs.exists()) {
            try {
                logs.createNewFile();
            } catch (IOException ex) {
                log.severe(MessageFormat.format("[{0}] Unable to create log file, {1}", pluginName, ex));
       	    }
         }
         if (traps.exists()) {
             readBlocksFromFile(traps, trapBlocks);
         }
         if (placed.exists()) {
             readBlocksFromFile(placed, placedBlocks);
         }
         if (!configFile.exists()) {
             System.out.println("doesn't exist");
             config.load();
             wm.addAllWorlds();
         } else {
             System.out.println("loading yaml");
             loadYaml();
         }
         if (getConfig().getBoolean(Config.cleanLog)) {
             try {
                 cleanLog.createNewFile();
             } catch (IOException ex) {
                 Logger.getLogger(FoundDiamonds.class.getName()).log(Level.SEVERE, "Couldn't create clean log file, ", ex);
             }
         }
    }

    private void saveLists() {

    }

    public File getLogFile() {
        return logs;
    }

    public File getCleanLog() {
        return cleanLog;
    }

    public void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ex) {
                Logger.getLogger(FoundDiamonds.class.getName()).log(Level.SEVERE, "Couldn't close a stream, ", ex);
            }
        }
    }




    /*
     * Toggle handler
     */
    private boolean handleToggle(CommandSender sender, String arg) {
        if (arg.equalsIgnoreCase("creative")) {
            getConfig().set(Config.disableInCreative, !getConfig().getBoolean(Config.disableInCreative));
            Menu.printSaved(this, sender);
        } else if (arg.equalsIgnoreCase("admin") || arg.equalsIgnoreCase("adminmessages")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (hasPerms(player, "fd.admin")) {
                    reloadAdminMessageMap(player);
                    if (adminMessagePlayers.get(player)) {
                        player.sendMessage(getPrefix() + ChatColor.AQUA + " Admin messages are " + ChatColor.DARK_GREEN + "ON");
                    } else {
                        player.sendMessage(getPrefix() + ChatColor.AQUA + " Admin messages are " + ChatColor.RED + "OFF");
                    }
                } else {
                    sendPermissionsMessage(player);
                }
            } else {
                sender.sendMessage(getPrefix() + ChatColor.DARK_RED + " Admin messages cannot be toggled from the console.");
            }
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
            if (!cleanLog.exists()) {
                try {
                    boolean successful = cleanLog.createNewFile();
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
     * Placed blocks
     */
    public static void addToPlacedBlocks(Location w) {
        placedBlocks.add(w);
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public static List<Location> getPlacedBlocks() {
        return placedBlocks;
    }




    /*
     * Misc
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public static List<Location> getAnnouncedBlocks() {
        return announcedBlocks;
    }

    public static boolean isRedstone(Block m) {
        return (m.getType() == Material.REDSTONE_ORE || m.getType() == Material.GLOWING_REDSTONE_ORE);
    }

    public boolean hasPerms(Player player, String permission) {
        return (player.hasPermission(permission) || (getConfig().getBoolean(Config.opsAsFDAdmin) && player.isOp()));
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

    public static void sendPermissionsMessage(Player player) {
        player.sendMessage(getPrefix() + ChatColor.RED + " You don't have permission to do that.");
        log.warning( player.getName() + " was denied access to a command.");
    }

    public Logger getLog() {
        return log;
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
     * metrics
     */
    private void startMetrics() {
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            //couldn't start metrics :(
        }
    }




    /*
    * Admin Message Hashmap
    */
    private void reloadAdminMessageMap(Player player) {
        if ((adminMessagePlayers.containsKey(player)) && adminMessagePlayers.get(player)) {
            adminMessagePlayers.put(player, false);
        } else if ((adminMessagePlayers.containsKey(player)) && (!adminMessagePlayers.get(player))) {
            adminMessagePlayers.put(player, true);
        } else {
            adminMessagePlayers.put(player, true);
        }
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public HashMap<Player,Boolean> getAdminMessageMap() {
        return adminMessagePlayers;
    }

}
