package co.proxa.founddiamonds.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class PluginUtils {

    public static String getArgs1Plus(String[] args) {
        StringBuilder sb = new StringBuilder();
        args[0] = "";
        for (String x : args) {
            sb.append(x).append(" ");
        }
        return sb.toString().trim();
    }
    public static String getArgs2Plus(String[] args) {
        StringBuilder sb = new StringBuilder();
        args[0] = ""; args[1] = "";
        for (String x : args) {
            sb.append(x).append(" ");
        }
        return sb.toString().trim();
    }

    public static boolean isRedstone(Block m) {
        return (m.getType() == Material.REDSTONE_ORE || m.getType() == Material.GLOWING_REDSTONE_ORE);
    }

    public static boolean isRedstone(Material m) {
        return (m == Material.REDSTONE_ORE || m == Material.GLOWING_REDSTONE_ORE);
    }

    public static String customTranslateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] charArray = textToTranslate.toCharArray();
        for (int i = 0; i < charArray.length - 1; i++) {
            if (charArray[i] == altColorChar && "0123456789AaBbCcDdEeFfKkNnRrLlMmOo".indexOf(charArray[i+1]) > -1) {
                charArray[i] = ChatColor.COLOR_CHAR;
                charArray[i+1] = Character.toLowerCase(charArray[i+1]);
            }
        }
        return new String(charArray);
    }
}
