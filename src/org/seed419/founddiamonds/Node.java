package org.seed419.founddiamonds;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Created with IntelliJ IDEA.
 * User: seed419
 * Date: 4/13/12
 * Time: 12:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class Node {


    private ItemStack item;
    private ChatColor color;


    public Node(Material mat, ChatColor color) {
        this.item = new ItemStack(mat);
        this.color = color;
    }
    
    public Node(ItemStack stack, ChatColor color) {
        this.item = stack;
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    public Material getMaterial() {
        return item.getType();
    }

    public static Node parseNode(String cb) {
        String[] bi = cb.split(",");
        //Parse the material string
        ItemStack matchAttempt = parseItemStack(bi[0]);
        if (matchAttempt == null) {
            return null;
        }
        if (bi.length < 2) {
            if (matchAttempt.getType() == null) {
                return new Node(matchAttempt, ChatColor.WHITE);
            }
            if (!matchAttempt.getType().isBlock()) return null;
            ChatColor color = null;
            // No color specified - attempt to get default
            color = BlockColor.getBlockColor(matchAttempt.getType());
            return new Node(matchAttempt, color);
        }
        else {
            // parse the color
            ChatColor color = parseChatColor(bi[1]);
            if (color == null) return null;
            return new Node(matchAttempt, color);
        }
    }


    public static ItemStack parseItemStack(String matName) {
        Material matchMaterial = Material.matchMaterial(matName);
        if (matchMaterial != null) return new ItemStack(matchMaterial);
        String[] split = matName.split(":");
        if (split.length < 2) {
            try {
                int id = Integer.parseInt(split[0]);
                return new ItemStack(id, 1);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        else {
            try {
                int id = Integer.parseInt(split[0]);
                short damage = (short) Integer.parseInt(split[1]);
                return new ItemStack(id, 1, damage);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    public static ChatColor parseChatColor(String colorName) {
        ChatColor color;
        String re = colorName.replace(" ","_").toUpperCase();
        try {
            color = ChatColor.valueOf(re);
            return color;
        } catch (IllegalArgumentException ex) {
             return null;
        }
    }

    public static boolean containsMat(List<Node> list, Material mat) {
        for (Node x : list) {
            if (x.getMaterial() == mat) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsColor(List<Node> list, ChatColor color) {
        for (Node x : list) {
            if (x.getColor() == color) {
                return true;
            }
        }
        return false;
    }

    public static Node getNodeByMaterial(List<Node> list, Material mat) {
        for (Node x : list) {
            if (x.getMaterial() == mat) {
                return x;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return Format.material(this.getMaterial()) + ":" + Format.chatColor(this.getColor());
    }
}
