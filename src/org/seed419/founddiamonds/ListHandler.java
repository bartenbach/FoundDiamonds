package org.seed419.founddiamonds;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: seed419
 * Date: 5/10/12
 * Time: 7:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListHandler {


    private static FoundDiamonds fd;
    private static final List<Node> broadcastedBlocks = new ArrayList<Node>();
    private static final List<Node> adminMessageBlocks = new ArrayList<Node>();
    private static final List<Node> lightLevelBlocks = new ArrayList<Node>();


    public ListHandler(FoundDiamonds instance) {
        this.fd = instance;
    }

    public void loadAllBlocks() {
        loadBlocksFromConfig(broadcastedBlocks, Config.broadcastedBlocks);
        loadBlocksFromConfig(adminMessageBlocks, Config.adminMessageBlocks);
        loadBlocksFromConfig(lightLevelBlocks, Config.lightLevelBlocks);
    }

    public static List<?> getVersatileList(String configLoc) {
        return fd.getConfig().getList(configLoc);
    }

    public static void loadBlocksFromConfig(List<Node> list, String configLoc) {
        if (fd.getConfig().getList(configLoc) == null) {
            if (configLoc.equals(Config.broadcastedBlocks)) {
                loadDefaults();
            } else {
                fd.getConfig().set(configLoc, new HashSet<String>());
            }
            updateListInConfig(list, configLoc);
        } else {
            List<String> thelist = fd.getConfig().getStringList(configLoc);
            for (String x : thelist) {
                String[] bi = x.split(":");
                Material matchAttempt= null;
                try {
                    matchAttempt = Material.matchMaterial(bi[0]);
                } catch (Exception ex) {
                    fd.getLog().severe(FoundDiamonds.getLoggerPrefix() + " Unable to match material '" + bi[0] + "'");
                }
                if (matchAttempt != null) {
                    if (matchAttempt.isBlock()) {
                        try {
                            String re = bi[1].replace(" ","_").toUpperCase();
                            ChatColor color = ChatColor.valueOf(re);
                            if (color == null) {
                                color = BlockColor.getBlockColor(matchAttempt);
                            }
                            if (!Node.containsMat(list, matchAttempt)) {
                                list.add(new Node(matchAttempt,color));
                            }
                        } catch (Exception ex) {
                            fd.getLog().severe(FoundDiamonds.getLoggerPrefix() + " Unable to match color '" + bi[1] + "'");
                        }

                    } else {
                        fd.getLog().warning(FoundDiamonds.getLoggerPrefix() + " Unable to add " + x + " because it is not a block!");
                    }
                } else {
                    fd.getLog().warning(FoundDiamonds.getLoggerPrefix() + " Unable to add " + x
                            + ".  Unrecognized block.  See bukkit material enum for valid block names.");
                }
            }
        }
    }


    public static void loadDefaults() {
        fd.getLog().info(FoundDiamonds.getLoggerPrefix() + " Adding broadcast defaults...");
        broadcastedBlocks.add(new Node(Material.DIAMOND_ORE, ChatColor.AQUA));
        broadcastedBlocks.add(new Node(Material.GOLD_ORE, ChatColor.GOLD));
        broadcastedBlocks.add(new Node(Material.LAPIS_ORE, ChatColor.BLUE));
        broadcastedBlocks.add(new Node(Material.IRON_ORE, ChatColor.GRAY));
        broadcastedBlocks.add(new Node(Material.COAL_ORE, ChatColor.DARK_GRAY));
        broadcastedBlocks.add(new Node(Material.REDSTONE_ORE, ChatColor.DARK_RED));
        broadcastedBlocks.add(new Node(Material.GLOWING_REDSTONE_ORE, ChatColor.DARK_RED));
    }

    public static void updateListInConfig(Collection<Node> list, String configLoc) {
        List<String> temp = new ArrayList<String>();
        for (Node x : list) {
            temp.add(x.toString());
            if (fd.getConfig().getBoolean(Config.debug)) {fd.getLog().info(FoundDiamonds.getDebugPrefix() + x.toString());}
        }
        fd.getConfig().set(configLoc, temp);
        fd.saveConfig();
    }

    public static void handleAddToList(CommandSender sender, String[] args, List<Node> list, String configString) {
        if (args.length == 2) {
            sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.RED + " Format is: item:data,color");
            sender.sendMessage(ChatColor.RED + " Color is an optional argument.");
            sender.sendMessage(ChatColor.RED + " Ex: sugar cane block:dark green");
        } else if (args.length >= 3) {
            StringBuilder sb = new StringBuilder();
            for (int i=2;i<args.length;i++) {
                sb.append(args[i]).append(" ");
            }
            Node block = Node.parseNode(sb.toString().trim());
            if (block != null) {
                if (!Node.containsMat(list, block.getMaterial())) {
                    list.add(block);
                    sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " Added " + block.getColor()
                            + Format.material(block.getMaterial()));
                } else {
                    removeMaterialFromList(block.getMaterial(), list, configString);
                    list.add(block);
                    sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.AQUA + " Updated " + block.getColor()
                            + Format.material(block.getMaterial()));
                }
                updateListInConfig(list, configString);
            } else {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Unable to add block.  Please check your format.");
            }
        }
    }

    public static void handleRemoveFromList(CommandSender sender, String[] args, List<Node> list, String configString) {
        if (args.length == 2) {
            sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.RED + " Simply type the name of the block you want to remove");
            sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.RED + " It unfortunely must match bukkit's material enum");
            sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.RED + " If this is really buggy, please ask SeeD419 for help!");
        } else if (args.length > 2) {
            StringBuilder sb = new StringBuilder();
            if (fd.getConfig().getBoolean(Config.debug)) {fd.getLog().info(FoundDiamonds.getDebugPrefix() + " Size of block list = " + list.size());}
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i] + " ");
            }
            Material matToRemove = Material.matchMaterial(sb.toString().trim());
            if (matToRemove == null) {
                sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Unrecognized material");
            } else {
                ChatColor color = getNodeColor(matToRemove,  list);
                if (removeMaterialFromList(matToRemove, list, configString)) {
                    sender.sendMessage(FoundDiamonds.getPrefix() + ChatColor.DARK_RED + " Removed " + color + Format.material(matToRemove));
                    if (fd.getConfig().getBoolean(Config.debug)) {fd.getLog().info(FoundDiamonds.getDebugPrefix() + " Size of block list = " + list.size());}
                    updateListInConfig(list, configString);
                } else {
                    sender.sendMessage(FoundDiamonds.getPrefix() + " "  + ChatColor.WHITE + Format.material(matToRemove) + ChatColor.DARK_RED + " isn't listed.");
                }
            }
        }
    }

    public static void handleListingList(CommandSender sender, List<Node> list) {
        for (Node x : list) {
            sender.sendMessage(x.getColor() + Format.capitalize(Format.material(x.getMaterial())));
        }
    }

    public static ChatColor getNodeColor(Material mat, List<Node> list) {
        for (Node x : list) {
            if (x.getMaterial() == mat) {
                return x.getColor();
            }
        }
        return null;
    }

    public static boolean removeMaterialFromList(Material mat, List<Node> list, String configString) {
        for (Node x : list) {
            if (x.getMaterial() == mat) {
                list.remove(x);
                return true;
            }
        }
        return false;
    }

    public static List<Node> getBroadcastedBlocks() {
        return broadcastedBlocks;
    }

    public static List<Node> getAdminMessageBlocks() {
        return adminMessageBlocks;
    }

    public static  List<Node> getLightLevelBlocks() {
        return lightLevelBlocks;
    }
}
