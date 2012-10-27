package org.seed419.founddiamonds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.seed419.founddiamonds.FoundDiamonds;
import org.seed419.founddiamonds.file.Config;
import org.seed419.founddiamonds.util.PluginUtils;
import org.seed419.founddiamonds.util.Prefix;

import java.io.IOException;

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
public class CommandHandler implements CommandExecutor {


    private FoundDiamonds fd;


    public CommandHandler(FoundDiamonds fd) {
        this.fd = fd;
    }


    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (((commandLabel.equalsIgnoreCase("fd")) || commandLabel.equalsIgnoreCase("founddiamonds"))) {
            if (args.length == 0) {
                if (fd.getPermissions().hasAnyMenuPerm(sender)) {
                    fd.getMenuHandler().printMainMenu(sender);
                } else {
                    fd.getPermissions().sendPermissionsMessage(sender);
                }
                return true;
            } else {
                String arg = args[0];
                if (arg.equalsIgnoreCase("admin")) {
                    if (fd.getPermissions().hasAdminManagementPerm(sender)) {
                        fd.getMenuHandler().handleAdminMenu(fd, sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("bc") || arg.equalsIgnoreCase("broadcast")) {
                    if (fd.getPermissions().hasBroadcastManagementPerm(sender)) {
                        fd.getMenuHandler().handleBcMenu(fd, sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("clearplaced")) {
                    if (fd.getPermissions().hasPerm(sender, "fd.*")) {
                        if (fd.getConfig().getBoolean(Config.mysqlEnabled)) {
                            fd.getMySQL().clearPlaced(sender);
                        }
                        fd.getFileHandler().deletePlaced(sender);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("config")) {
                    if (fd.getPermissions().hasConfigPerm(sender)) {
                        if (args.length == 2) {
                            if (args[1].equalsIgnoreCase("2")) {
                                fd.getMenuHandler().showConfig2(fd, sender);
                            }
                        } else {
                            fd.getMenuHandler().showConfig(fd, sender);
                        }
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                } else if (arg.equalsIgnoreCase("light")) {
                    if (fd.getPermissions().hasLightManagementPerm(sender)) {
                        fd.getMenuHandler().handleLightMenu(fd, sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
               } else if (arg.equalsIgnoreCase("reload")) {
                    if (fd.getPermissions().hasReloadPerm(sender)) {
                        fd.reloadConfig();
                        fd.saveConfig();
                        sender.sendMessage(Prefix.getChatPrefix() + ChatColor.AQUA + " Configuration saved and reloaded.");
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("set")) {
                    if (fd.getPermissions().hasTogglePerm(sender)) {
                        fd.getMenuHandler().handleSetMenu(fd, sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("toggle")) {
                    if (fd.getPermissions().hasTogglePerm(sender)) {
                        if (args.length == 1) {
                            fd.getMenuHandler().showToggle(sender);
                        } else  if (args.length == 2) {
                            arg = args[1];
                            handleToggle(sender, arg);
                        } else {
                            sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Invalid number of arguments.");
                            sender.sendMessage(ChatColor.RED + "See '/fd toggle' for the list of valid arguments.");
                        }
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("trap")) {		
                	/* TODO Still unsure about how the perms would work out. 
                	* currently, players would need fd.trap to be able to set traps
                	* and they would need fd.trap AND fd.trap.remove.* to remove any traps
                	* but I personally don't think it's bad. This way admins can
                	* give mods for instance the ability to just place traps, and admins
                	* to remove them as well
                	*/
                    if (sender instanceof Player) {
                        if (fd.getPermissions().hasTrapPerm(sender)) {
                            fd.getTrapHandler().handleTrap(player, args);
                        } else {
                            fd.getPermissions().sendPermissionsMessage(sender);
                        }
                    } else {
                        sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Can't handle traps from the console.");
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("world")) {
                    if (fd.getPermissions().hasWorldManagementPerm(sender)) {
                        fd.getWorldHandler().handleWorldMenu(sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("version")) {
                    if (fd.getPermissions().hasAnyMenuPerm(sender)) {
                        fd.getMenuHandler().showVersion(fd, sender);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else {
                    if (fd.getPermissions().hasAnyMenuPerm(sender)) {
                        sender.sendMessage(Prefix.getChatPrefix() + ChatColor.DARK_RED + " Unrecognized argument '"
                                + ChatColor.WHITE + PluginUtils.getArgs1Plus(args) + ChatColor.DARK_RED + "'");
                    }
                }
            }
        }
        return false;
    }

    private boolean handleToggle(CommandSender sender, String arg) {
        if (arg.equalsIgnoreCase("creative")) {
            fd.getConfig().set(Config.disableInCreative, !fd.getConfig().getBoolean(Config.disableInCreative));
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("ops")) {
            fd.getConfig().set(Config.opsAsFDAdmin, !fd.getConfig().getBoolean(Config.opsAsFDAdmin));
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("kick")) {
            fd.getConfig().set(Config.kickOnTrapBreak, !fd.getConfig().getBoolean(Config.kickOnTrapBreak));
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("ban") || arg.equalsIgnoreCase("bans")) {
            fd.getConfig().set(Config.banOnTrapBreak, !fd.getConfig().getBoolean(Config.banOnTrapBreak));
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("items")) {
            fd.getConfig().set(Config.itemsForFindingDiamonds, !fd.getConfig().getBoolean(Config.itemsForFindingDiamonds));
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("logging")) {
            fd.getConfig().set(Config.logDiamondBreaks, !fd.getConfig().getBoolean(Config.logDiamondBreaks));
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("spells")) {
            fd.getConfig().set(Config.potionsForFindingDiamonds, !fd.getConfig().getBoolean(Config.potionsForFindingDiamonds));
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("cleanlog")) {
            fd.getConfig().set(Config.cleanLog, !fd.getConfig().getBoolean(Config.cleanLog));
            if (!fd.getFileHandler().getCleanLog().exists()) {
                try {
                    boolean successful = fd.getFileHandler().getCleanLog().createNewFile();
                    if (successful) {sender.sendMessage(Prefix.getChatPrefix() + ChatColor.DARK_GREEN +" Cleanlog created.");}
                    } catch (IOException ex) {
                    sender.sendMessage(Prefix.getChatPrefix() + ChatColor.DARK_RED + " Uh-oh...couldn't create CleanLog.txt");
                    fd.getLog().severe("Failed to create CleanLog.txt");
                }
            }
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("nick") || arg.equalsIgnoreCase("nicks")) {
            fd.getConfig().set(Config.useNick, !fd.getConfig().getBoolean(Config.useNick));
            fd.getMenuHandler().printSaved(fd, sender);
        } else if (arg.equalsIgnoreCase("debug")) {
            fd.getConfig().set(Config.debug, !fd.getConfig().getBoolean(Config.debug));
            fd.getMenuHandler().printSaved(fd, sender);
        } else {
            sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Argument '" + arg + "' unrecognized.");
            sender.sendMessage(ChatColor.RED + "See '/fd toggle' for the list of valid arguments.");
            return false;
        }
        return true;
    }

}