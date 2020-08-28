package co.proxa.founddiamonds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.file.Config;
import co.proxa.founddiamonds.util.Prefix;

import java.io.IOException;

public class CommandHandler implements CommandExecutor {

    private FoundDiamonds fd;

    public CommandHandler(FoundDiamonds fd) {
        this.fd = fd;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
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
                if (arg.equalsIgnoreCase("admin") || arg.equalsIgnoreCase("a")) {
                    if (fd.getPermissions().hasAdminManagementPerm(sender)) {
                        fd.getMenuHandler().handleAdminMenu(fd, sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("bc") || arg.equalsIgnoreCase("broadcast") || arg.equalsIgnoreCase("b")) {
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
                        } else {
                            fd.getBlockPlaceListener().clearPlaced();
                            sender.sendMessage(ChatColor.AQUA + "Placed blocks in memory cleared.");
                        }
                        fd.getFileHandler().deletePlaced(sender);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("config") || arg.equalsIgnoreCase("c")) {
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
                } else if (arg.equalsIgnoreCase("light") || arg.equalsIgnoreCase("l")) {
                    if (fd.getPermissions().hasLightManagementPerm(sender)) {
                        fd.getMenuHandler().handleLightMenu(fd, sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
               } else if (arg.equalsIgnoreCase("reload") || arg.equalsIgnoreCase("r")) {
                    if (fd.getPermissions().hasReloadPerm(sender)) {
                        fd.reloadConfig();
                        fd.saveConfig();
                        sender.sendMessage(Prefix.getChatPrefix() + ChatColor.AQUA + " Configuration saved and reloaded.");
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("set") || arg.equalsIgnoreCase("s")) {
                    if (fd.getPermissions().hasTogglePerm(sender)) {
                        fd.getMenuHandler().handleSetMenu(fd, sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("toggle") || arg.equalsIgnoreCase("t")) {
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
                    if (fd.getPermissions().hasTrapPerm(sender)) {
                        fd.getMenuHandler().handleTrapMenu(fd, sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("world") || arg.equalsIgnoreCase("w")) {
                    if (fd.getPermissions().hasWorldManagementPerm(sender)) {
                        fd.getWorldHandler().handleWorldMenu(sender, args);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else if (arg.equalsIgnoreCase("version") || arg.equalsIgnoreCase("v")) {
                    if (fd.getPermissions().hasAnyMenuPerm(sender)) {
                        fd.getMenuHandler().showVersion(fd, sender);
                    } else {
                        fd.getPermissions().sendPermissionsMessage(sender);
                    }
                    return true;
                } else {
                    if (fd.getPermissions().hasAnyMenuPerm(sender)) {
                        sender.sendMessage(Prefix.getChatPrefix() + ChatColor.DARK_RED + " Unrecognized argument '"
                                + ChatColor.WHITE + arg + ChatColor.DARK_RED + "'");
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
        } else {
            sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Argument '" + arg + "' unrecognized.");
            sender.sendMessage(ChatColor.RED + "See '/fd toggle' for the list of valid arguments.");
            return false;
        }
        return true;
    }

}
