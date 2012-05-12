/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seed419.founddiamonds;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author seed419
 */
public class Menu {



    /*
     * Main Menu
     */
    public static boolean printMainMenu(FoundDiamonds fd, CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (fd.hasPerms(player, "fd.trap") || fd.hasPerms(player, "fd.manage.config") || fd.hasPerms(player, "fd.manage.reload")
                    || fd.hasPerms(player, "fd.manage.toggle") || fd.hasPerms(player, "fd.manage.admin.add") || fd.hasPerms(player, "fd.manage.admin.remove")
                    || fd.hasPerms(player, "fd.manage.worlds") || fd.hasPerms(player, "fd.manage.admin.list")
                    || fd.hasPerms(player, "fd.manage.broadcast.add") || fd.hasPerms(player, "fd.manage.broadcast.remove")
                    || fd.hasPerms(player, "fd.manage.broadcast.list") || fd.hasPerms(player, "fd.manage.light.add")
                    || fd.hasPerms(player, "fd.manage.light.list") || fd.hasPerms(player, "fd.manage.light.remove")) {
                player.sendMessage(fd.getPrefix() + ChatColor.AQUA + " [founddiamonds Main Menu]");
                player.sendMessage("/fd " + ChatColor.RED + "<option>");
            } else {
                return false;
            }
            if (fd.hasPerms(player, "fd.manage.admin.add") || fd.hasPerms(player, "fd.manage.admin.remove")
                    || fd.hasPerms(player, "fd.manage.admin.list")) {
                player.sendMessage(ChatColor.RED + "    admin" + ChatColor.WHITE + " - Manage admin message blocks");
            }
            if (fd.hasPerms(player, "fd.manage.broadcast.add") || fd.hasPerms(player, "fd.broadcast.remove")
                    || fd.hasPerms(player, "fd.broadcast.list")) {
                player.sendMessage(ChatColor.RED + "    bc" + ChatColor.WHITE + " - Manage broadcasted blocks");
            }
            if (fd.hasPerms(player, "fd.manage.config")) {
                player.sendMessage(ChatColor.RED + "    config" + ChatColor.WHITE + " - View the configuration file");
            }
            if (fd.hasPerms(player, "fd.manage.light.add") || fd.hasPerms(player, "fd.manage.light.remove")
                    || fd.hasPerms(player, "fd.manage.light.list")) {
                player.sendMessage(ChatColor.RED + "    light" + ChatColor.WHITE + " - Manage light-monitored blocks");
            }
            if (fd.hasPerms(player, "fd.manage.reload")) {
                player.sendMessage(ChatColor.RED + "    reload" + ChatColor.WHITE + " - Reload the configuration file");
            }
            if (fd.hasPerms(player, "fd.manage.toggle")) {
                player.sendMessage(ChatColor.RED + "    set" + ChatColor.WHITE + " - Modify values in the config");
            }
            if (fd.hasPerms(player, "fd.manage.toggle")) {
                player.sendMessage(ChatColor.RED + "    toggle <option>" + ChatColor.WHITE + " - Change the configuration");
            }
            if (fd.hasPerms(player, "fd.trap")) {
                player.sendMessage(ChatColor.RED + "    trap" + ChatColor.WHITE + " - Set a diamond ore trap");
                player.sendMessage(ChatColor.RED + "    trap <itemname>" + ChatColor.WHITE + " - Set a trap with another block");
                player.sendMessage(ChatColor.WHITE + "    You can also specify a depth after each trap command ");
            }
            if (fd.hasPerms(player, "fd.manage.worlds")) {
                player.sendMessage(ChatColor.RED + "    world" + ChatColor.WHITE + " - Manage enabled worlds");
            }
        } else {
            sender.sendMessage("/fd " + ChatColor.RED + "<option>");
            sender.sendMessage(ChatColor.RED + "    admin" + ChatColor.WHITE + " - Manage admin message blocks");
            sender.sendMessage(ChatColor.RED + "    bc" + ChatColor.WHITE + " - Manage broadcasted blocks");
            sender.sendMessage(ChatColor.RED + "    config" + ChatColor.WHITE + " - View the configuration file");
            sender.sendMessage(ChatColor.RED + "    light" + ChatColor.WHITE + " - Manage light-monitored blocks");
            sender.sendMessage(ChatColor.RED + "    reload" + ChatColor.WHITE + " - Reload the configuration file");
            sender.sendMessage(ChatColor.RED + "    set" + ChatColor.WHITE + " - Modify values in the config");
            sender.sendMessage(ChatColor.RED + "    toggle <option>" + ChatColor.WHITE + " - Change the configuration");
            sender.sendMessage(ChatColor.RED + "    world" + ChatColor.WHITE + " - Manange FD enabled worlds");
        }
        return true;
    }




    //Toggle Menu
    public static void showToggle(CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Toggle Options 1/" + FoundDiamonds.getTogglePages() + "]");
        sender.sendMessage(ChatColor.RED + "    ops" + ChatColor.WHITE + " - OPs have all permissions");
        sender.sendMessage(ChatColor.RED + "    kick" + ChatColor.WHITE + " - Kick players on trap breaks");
        sender.sendMessage(ChatColor.RED + "    ban" + ChatColor.WHITE + " - Ban players on trap breaks");
        sender.sendMessage(ChatColor.RED + "    trapalerts" + ChatColor.WHITE + " - Send admin messages on all trap breaks");
        sender.sendMessage(ChatColor.RED + "    logging" + ChatColor.WHITE + " - Log all diamond breaks to log.txt");
        sender.sendMessage(ChatColor.RED + "    nicks" + ChatColor.WHITE + " - Use player nicknames in broadcasts");
        sender.sendMessage(ChatColor.RED + "    creative" + ChatColor.WHITE + " - Disable in creative gamemode");
        sender.sendMessage(ChatColor.RED + "    items" + ChatColor.WHITE + " - Random items for finding diamonds");
        sender.sendMessage(ChatColor.RED + "    spells" + ChatColor.WHITE + " - Random spells for finding diamonds");
        sender.sendMessage("Type /fd toggle 2 to read the next page");
    }

    public static void showToggle2(CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Toggle Options 2/" + FoundDiamonds.getTogglePages() + "]");
        sender.sendMessage(ChatColor.RED + "    cleanlog" + ChatColor.WHITE + " - Clean log (all ore announcements)");
        sender.sendMessage(ChatColor.RED + "    debug" + ChatColor.WHITE + " - Toggle debug output to the console");
        //sender.sendMessage("Type /fd toggle 3 to read the next page");
    }



    //Config menus
    public static void showConfig(FoundDiamonds fd, CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Configuration 1/" + FoundDiamonds.getConfigPages() + "]");
        sender.sendMessage(ChatColor.RED + "    Random spells for finding diamonds: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.potionsForFindingDiamonds)));
        sender.sendMessage(ChatColor.RED + "    Spell Strength: " + ChatColor.AQUA + fd.getConfig().getInt(Config.potionStrength));
        sender.sendMessage(ChatColor.RED + "    Odds of casting spells: " + ChatColor.AQUA + fd.getConfig().getInt(Config.chanceToGetPotion) + "%");
        sender.sendMessage(ChatColor.RED + "    Random items for finding diamonds: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.itemsForFindingDiamonds)));
        sender.sendMessage(ChatColor.RED + "    Odds of getting items: " + ChatColor.AQUA + fd.getConfig().getInt(Config.chanceToGetItem) + "%");
        sender.sendMessage(ChatColor.RED + "    Item 1: " + ChatColor.AQUA + Format.material(Material.getMaterial(fd.getConfig().getInt(Config.randomItem1))));
        sender.sendMessage(ChatColor.RED + "    Item 2: " + ChatColor.AQUA + Format.material(Material.getMaterial(fd.getConfig().getInt(Config.randomItem2))));
        sender.sendMessage(ChatColor.RED + "    Item 3: " + ChatColor.AQUA + Format.material(Material.getMaterial(fd.getConfig().getInt(Config.randomItem3))));
        sender.sendMessage(ChatColor.RED + "    Logging all diamond ore breaks: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.logDiamondBreaks)));
        sender.sendMessage("Type /fd config 2 to read the next page");

    }

    public static void showConfig2(FoundDiamonds fd, CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Configuration 2/" + FoundDiamonds.getConfigPages() + "]");
        sender.sendMessage(ChatColor.RED + "    Disable in creative mode: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.disableInCreative)));
        sender.sendMessage(ChatColor.RED + "    Clean Log: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.cleanLog)));
        sender.sendMessage(ChatColor.RED + "    Debug Mode: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.debug)));
        sender.sendMessage(ChatColor.RED + "    Light Level blocks disabling at " + ChatColor.AQUA + fd.getConfig().getString(Config.percentOfLightRequired) + ChatColor.RED + " light");
        sender.sendMessage(ChatColor.RED + "    Use player nicknames: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.useNick)));
        sender.sendMessage(ChatColor.RED + "    Give OPs all permissions: " +  getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.opsAsFDAdmin)));
        sender.sendMessage(ChatColor.RED + "    Kick players on trap break: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.kickOnTrapBreak)));
        sender.sendMessage(ChatColor.RED + "    Ban players on trap break: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.banOnTrapBreak)));
        sender.sendMessage(ChatColor.RED + "    Admin alerts on all trap breaks: " + getPrettyMenuBoolean(fd.getConfig().getBoolean(Config.adminAlertsOnAllTrapBreaks)));
        //sender.sendMessage("Type /fd config 3 to read the next page");
    }



    //Light Level block
    public static void showLightMenu(CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Light Level Blocks]");
        sender.sendMessage(ChatColor.RED + "    add" + ChatColor.WHITE + " - Monitor a new blocks light level");
        sender.sendMessage(ChatColor.RED + "    remove" + ChatColor.WHITE + " - Stop a block from being monitored");
        sender.sendMessage(ChatColor.RED + "    list" + ChatColor.WHITE + " - List the currently monitored blocks");
    }


    //Admin message block menu
    public static void showAdminMenu(CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Admin Message Blocks]");
        sender.sendMessage(ChatColor.RED + "    add" + ChatColor.WHITE + " - Add a block that will send admin messages");
        sender.sendMessage(ChatColor.RED + "    remove" + ChatColor.WHITE + " - Remove an admin message block");
        sender.sendMessage(ChatColor.RED + "    list" + ChatColor.WHITE + " - List the current admin message blocks");
    }

    //Broadcasted block menu
    public static void showBcMenu(CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Broadcasted Blocks]");
        sender.sendMessage(ChatColor.RED + "    add" + ChatColor.WHITE + " - Add a new block to be broadcasted");
        sender.sendMessage(ChatColor.RED + "    remove" + ChatColor.WHITE + " - Remove a block from being broadcasted");
        sender.sendMessage(ChatColor.RED + "    list" + ChatColor.WHITE + " - List all currently broadcasted blocks");
    }

    //World Menu
    public static void showWorldMenu(CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Worlds 1/1]");
        sender.sendMessage("/fd world" + ChatColor.RED + " <option>");
        sender.sendMessage(ChatColor.RED + "    list" + ChatColor.WHITE + " - List FD enabled worlds");
        sender.sendMessage(ChatColor.RED + "    add <world>" + ChatColor.WHITE + " - Add FD to a world.");
        sender.sendMessage(ChatColor.RED + "    remove <world>" + ChatColor.WHITE + " - Remove FD from a world.");
    }



    /*
    * Set Menu
    */
    public static void showSetMenu(CommandSender sender) {
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Set]");
/*        sender.sendMessage(ChatColor.RED + "    item1 <id number>");
        sender.sendMessage(ChatColor.RED + "    item2 <id number>");
        sender.sendMessage(ChatColor.RED + "    item3 <id number>");*/
        sender.sendMessage(ChatColor.RED + "    spellpercent <percent>" + ChatColor.WHITE + " Set odds that spells are casted.");
        sender.sendMessage(ChatColor.RED + "    itempercent <percent>" + ChatColor.WHITE + " Set odds that items are awarded.");
    }

    public static boolean handleSetMenu(FoundDiamonds fd, CommandSender sender, String[] args) {
        if (args.length == 1) {
            showSetMenu(sender);
            return true;
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("spellpercent")) {
                System.out.println("spellpercent");
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Usage: /fd set "
                        + "spellpercent <percent>");
                return true;
            } else if (args[1].equalsIgnoreCase("itempercent")) {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Usage: /fd set "
                        + "itempercent <percent>");
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("spellpercent")) {
                int percent = getPercent(fd, sender, args);
                setPercent(fd, sender, percent, Config.chanceToGetPotion);
            } else if (args[1].equalsIgnoreCase("itempercent")) {
                int percent = getPercent(fd, sender, args);
                setPercent(fd, sender, percent, Config.chanceToGetItem);
            }
        }
        return false;
    }

    public static void setPercent(FoundDiamonds fd, CommandSender sender, int percent, String configuration) {
        if (percent != 999) {
            fd.getConfig().set(configuration, percent);
            printSaved(fd, sender);
        } else {
            sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Percentage "
                    + "must be between 0 and 100");
        }
    }

    public static int getPercent(FoundDiamonds fd, CommandSender sender, String[] args) {
        int percent;
        try {
            percent = Integer.parseInt(args[2].trim());
        } catch (Exception ex) {
            sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.RED + " Invalid argument");
            return 999;
        }
        if (percent >=0 && percent <=100) {
            fd.getConfig().set(Config.chanceToGetPotion, percent);
            printSaved(fd, sender);
            return percent;
        } else {
            return 999;
        }
    }




    /*List menu handlers*/
    public static void handleBcMenu(FoundDiamonds fd, CommandSender sender, String[] args) {
        if (args.length == 1) {
            Menu.showBcMenu(sender);
        } else if (args.length > 1) {
            if (args[1].equalsIgnoreCase("list")) {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Broadcasted Blocks]");
                ListHandler.handleListingList(sender, ListHandler.getBroadcastedBlocks());
            } else if (args[1].equalsIgnoreCase("add")) {
                if (args.length == 2) {
                    sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.RED + " Format: /fd add bc item:color ex: coal ore:dark gray");
                    sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.RED + " Color is an optional argument.  If color is left out");
                    sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.RED + " FD will attempt to pick a color for you. ex: obsidian");
                }
                ListHandler.handleAddToList(sender, args, ListHandler.getBroadcastedBlocks(), Config.broadcastedBlocks);
            } else if (args[1].equalsIgnoreCase("remove")) {
                ListHandler.handleRemoveFromList(sender, args, ListHandler.getBroadcastedBlocks(), Config.broadcastedBlocks);
            } else {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Unrecognized command " + ChatColor.WHITE + "'" + args[1] + "'");
            }
        }
    }

    public static void handleAdminMenu(FoundDiamonds fd, CommandSender sender, String[] args) {
        if (args.length == 1) {
            Menu.showAdminMenu(sender);
        } else if (args.length > 1) {
            if (args[1].equalsIgnoreCase("list")) {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Admin Message Blocks]");
                ListHandler.handleListingList(sender, ListHandler.getAdminMessageBlocks());
            } else if (args[1].equalsIgnoreCase("add")) {
                ListHandler.handleAddToList(sender, args, ListHandler.getAdminMessageBlocks(), Config.adminMessageBlocks);
            } else if (args[1].equalsIgnoreCase("remove")) {
                ListHandler.handleRemoveFromList(sender, args, ListHandler.getAdminMessageBlocks(), Config.adminMessageBlocks);
            } else {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Unrecognized command " + ChatColor.WHITE + "'" + args[1] + "'");
            }
        }
    }

    public static void handleLightMenu(FoundDiamonds fd, CommandSender sender, String[] args) {
        if (args.length == 1) {
            Menu.showLightMenu(sender);
        } else if (args.length > 1) {
            if (args[1].equalsIgnoreCase("list")) {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " [Light-Monitored Blocks]");
                ListHandler.handleListingList(sender, ListHandler.getLightLevelBlocks());
            } else if (args[1].equalsIgnoreCase("add")) {
                ListHandler.handleAddToList(sender, args, ListHandler.getLightLevelBlocks(), Config.lightLevelBlocks);
            } else if (args[1].equalsIgnoreCase("remove")) {
                ListHandler.handleRemoveFromList(sender, args, ListHandler.getLightLevelBlocks(), Config.lightLevelBlocks);
            } else {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Unrecognized command " + ChatColor.WHITE + "'" + args[1] + "'");
            }
        }
    }



    //Menu helpers
    private static String getPrettyMenuBoolean(Boolean b) {
        return (b ? ChatColor.DARK_GREEN + "[On]" : ChatColor.DARK_RED + "[Off]");
    }

    public static void printSaved(FoundDiamonds fd, CommandSender sender) {
        fd.saveConfig();
        sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " Configuration updated.");
    }



}
