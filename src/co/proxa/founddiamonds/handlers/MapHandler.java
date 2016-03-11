package co.proxa.founddiamonds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.file.Config;
import co.proxa.founddiamonds.util.BlockColor;
import co.proxa.founddiamonds.util.Format;
import co.proxa.founddiamonds.util.PluginUtils;
import co.proxa.founddiamonds.util.Prefix;

import java.util.*;

public class MapHandler {

    private FoundDiamonds fd;
    private HashMap<Material,ChatColor> broadcastedBlocks = new HashMap<Material,ChatColor>();
    private HashMap<Material,ChatColor> adminMessageBlocks = new HashMap<Material,ChatColor>();
    private HashMap<Material,ChatColor> lightLevelBlocks = new HashMap<Material,ChatColor>();

    public MapHandler(FoundDiamonds fd) {
        this.fd = fd;
    }

    public void loadAllBlocks() {
        loadBlocksFromConfig(broadcastedBlocks, Config.broadcastedBlocks);
        loadBlocksFromConfig(adminMessageBlocks, Config.adminMessageBlocks);
        loadBlocksFromConfig(lightLevelBlocks, Config.lightLevelBlocks);
    }

    public void createList(HashMap<Material,ChatColor> list, String configLoc) {
        if (configLoc.equals(Config.broadcastedBlocks)) {
            loadDefaults();
        } else {
            fd.getConfig().set(configLoc, new HashSet<String>());
        }
        writeMapToConfig(list, configLoc);
    }

    public void loadBlocksFromConfig(HashMap<Material,ChatColor> map, String configLoc) {
        if (fd.getConfig().getList(configLoc) == null) {
            createList(map, configLoc);
        } else {
            List<String> thelist = fd.getConfig().getStringList(configLoc);
            for (String x : thelist) {
                String[] sp = x.split(",");
                Material mat = parseMaterial(sp[0]);
                if (mat != null && mat.isBlock()) {
                    try {
                        ChatColor color = parseColor(sp[1], mat);
                        if (!map.containsKey(mat)) {
                            map.put(mat, color);
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        fd.getLog().severe("Your configuration file is outdated and is probably using : to separate items and colors.");
                        fd.getLog().severe("The format is item:data,color.  You probably need to change the colons to commas.");
                        return;
                    } catch (Exception ex) {
                        fd.getLog().severe("Unable to match color " + sp[1]);
                    }
                } else {
                    //TODO support for deprecated item format
                    try {
                        String[] s = x.split(":");
                        Material mat2 = parseMaterial(s[0]);
                        if (mat != null && mat.isBlock()) {
                            fd.getLog().warning("Your configuration is outdated and using the old style of separating blocks and colors with a colon here: " + s[0]);
                            fd.getLog().warning("Please update these to be commas as support for the old style will be dropped.");
                            ChatColor color = parseColor(s[1], mat2);
                            if (!map.containsKey(mat)) {
                                map.put(mat, color);
                            }
                        }
                    //TODO end support for deprecated item format
                    } catch (Exception ex) {
                        fd.getLog().warning("Unable to add " + sp[0]);
                        fd.getLog().warning("Check the FD wiki for valid names.");
                    }
                }
            }
        }
    }

    private ChatColor parseColor(String co, Material mat) {
        String parsedColor = co.replace(" ","_").toUpperCase();
        ChatColor color;
        try {
            color = ChatColor.valueOf(parsedColor);
        } catch (IllegalArgumentException ex) {
            color = BlockColor.getBlockColor(mat);
            fd.getLog().info("No such color '" + parsedColor + "'.  Using default.");
        }
        return color;
    }

    private Material parseMaterial(String is) {
        Material mat;
        try {
            mat = Material.getMaterial(Integer.parseInt(is));
            return mat;
        } catch (NumberFormatException ex) {}
        try {
            mat = Material.getMaterial(is.toUpperCase().replace(" ", "_"));
            return mat;
        } catch (Exception ex) {
            fd.getLog().severe("Unable to match material '" + is + "'");
        }
        return null;
    }

    private void loadDefaults() {
        fd.getLog().info("Adding broadcast defaults...");
        broadcastedBlocks.put(Material.DIAMOND_ORE, ChatColor.AQUA);
        fd.getLog().info("Diamond Ore added");
        broadcastedBlocks.put(Material.GOLD_ORE, ChatColor.GOLD);
        fd.getLog().info("Gold Ore added");
        broadcastedBlocks.put(Material.LAPIS_ORE, ChatColor.BLUE);
        fd.getLog().info("Lapis Ore added");
        broadcastedBlocks.put(Material.IRON_ORE, ChatColor.GRAY);
        fd.getLog().info("Iron Ore added");
        broadcastedBlocks.put(Material.COAL_ORE, ChatColor.DARK_GRAY);
        fd.getLog().info("Coal Ore added");
        broadcastedBlocks.put(Material.REDSTONE_ORE, ChatColor.DARK_RED);
        broadcastedBlocks.put(Material.GLOWING_REDSTONE_ORE, ChatColor.DARK_RED);
        fd.getLog().info("Redstone Ore added");
        broadcastedBlocks.put(Material.EMERALD_ORE, ChatColor.GREEN);
        fd.getLog().info("Emerald Ore added");
    }

    private void writeMapToConfig(HashMap<Material, ChatColor> map, String configLoc) {
        List<String> temp = new ArrayList<String>();
        for (Map.Entry<Material, ChatColor> x : map.entrySet()) {
            temp.add(Format.material(x.getKey()) + "," + Format.chatColor(x.getValue()));
        }
        fd.getConfig().set(configLoc, temp);
        fd.saveConfig();
    }

    public void handleAddToList(CommandSender sender, String[] args, HashMap<Material,ChatColor> map, String configString) {
        if (args.length == 2) {
            sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Format is: item,color");
            sender.sendMessage(ChatColor.DARK_RED + "    Color is an optional argument.");
            sender.sendMessage(ChatColor.GOLD + "    Example: sugar cane block:dark green");
            sender.sendMessage(ChatColor.GOLD + "    Example: sugar cane block");
            sender.sendMessage(ChatColor.GOLD + "    In order to work, blocks must match bukkit's material type!");
            sender.sendMessage(ChatColor.GOLD + "    These are listed here:");
            sender.sendMessage(ChatColor.BLUE + "        http://jd.bukkit.org/apidocs/org/bukkit/Material.html");
        } else if (args.length >= 3) {
            String s = PluginUtils.getArgs2Plus(args);
            String[] sp = s.split(",");
            Material mat = parseMaterial(sp[0]);
            if (mat != null) {
                ChatColor c;
                try {
                    c = parseColor(sp[1], mat);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    c = BlockColor.getBlockColor(mat);
                }
                if (!map.containsKey(mat)) {
                    map.put(mat, c);
                    sender.sendMessage(Prefix.getChatPrefix() + ChatColor.DARK_GREEN + " Added " + c
                            + Format.material(mat));
                } else {
                    map.remove(mat);
                    map.put(mat, c);
                    sender.sendMessage(Prefix.getChatPrefix() + ChatColor.GREEN + " Updated " + c
                            + Format.material(mat));
                }
                writeMapToConfig(map, configString);
            } else {
                sender.sendMessage(Prefix.getChatPrefix() + ChatColor.DARK_RED + " Unable to add block.  Please check your format.");
            }
        }
    }

    public void handleRemoveFromList(CommandSender sender, String[] args, HashMap<Material,ChatColor> map, String configString) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.GOLD + "    In order to work, blocks must match bukkit's material type!");
            sender.sendMessage(ChatColor.GOLD + "    These are listed here:");
            sender.sendMessage(ChatColor.BLUE + "        http://jd.bukkit.org/apidocs/org/bukkit/Material.html");
        } else if (args.length > 2) {
            String s = PluginUtils.getArgs2Plus(args);
            Material mat = parseMaterial(s);
            if (mat != null) {
                if (map.containsKey(mat)) {
                    sender.sendMessage(Prefix.getChatPrefix() + ChatColor.DARK_RED + " Removed " + map.get(mat) + Format.material(mat));
                    map.remove(mat);
                    writeMapToConfig(map, configString);
                } else {
                    sender.sendMessage(Prefix.getChatPrefix() + " "  + ChatColor.WHITE + Format.material(mat) + ChatColor.DARK_RED + " isn't listed.");
                }
            } else {
                sender.sendMessage(Prefix.getChatPrefix() + ChatColor.DARK_RED + " Unrecognized material");
            }
        }
    }

    public void handleListingList(CommandSender sender, HashMap<Material,ChatColor> map) {
        for (Map.Entry<Material, ChatColor> x : map.entrySet()) {
            sender.sendMessage(x.getValue() + Format.capitalize(Format.material(x.getKey())));
        }
    }

    public HashMap<Material, ChatColor> getBroadcastedBlocks() {
        return broadcastedBlocks;
    }

    public HashMap<Material, ChatColor> getAdminMessageBlocks() {
        return adminMessageBlocks;
    }

    public HashMap<Material, ChatColor> getLightLevelBlocks() {
        return lightLevelBlocks;
    }
}
