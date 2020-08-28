package co.proxa.founddiamonds.util;

import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Format {

    public static final String leftGreenParen = ChatColor.DARK_GREEN + "(" + ChatColor.WHITE;
    public static final String rightGreenParen = ChatColor.DARK_GREEN + ")" + ChatColor.WHITE;
    public static final String commandFormat = ChatColor.RED + " [required] " + ChatColor.GRAY + "{optional}";
    private static final String boldLeftBracket = ChatColor.BOLD + "[" + ChatColor.RESET;
    private static final String boldRightBracket = ChatColor.BOLD + "]" + ChatColor.RESET;

    public static String formatMenuHeader(String menu) {
        return ChatColor.AQUA + boldLeftBracket + ChatColor.WHITE + menu + ChatColor.AQUA + boldRightBracket;
    }

    public static String getFormattedName(Material mat, int total) {
        String matName;
        if (mat == Material.REDSTONE_ORE) {
            if (total > 1) {
                matName = "redstone ores";
            } else {
                matName = "redstone ore";
            }
        } else if (mat == Material.OBSIDIAN) {
            matName = "obsidian";
        } else if (mat == Material.ICE) {
            if (total > 1) {
                matName = "blocks of ice";
            } else {
                matName = "block of ice";
            }
        } else if (mat == Material.SNOW || mat == Material.SNOW_BLOCK) {
            if (total > 1) {
                matName = "snow blocks";
            } else {
                matName = "snow block";
            }
        } else if (mat == Material.BREAD) {
            matName = "bread";
        } else if (mat == Material.CHAINMAIL_LEGGINGS) {
            matName = "chainmail leggings";
        } else if (mat == Material.IRON_LEGGINGS) {
            matName = "iron leggings";
        } else if (mat == Material.DIAMOND_LEGGINGS) {
            matName = "diamond leggings";
        } else if (mat == Material.CLAY) {
            if (total > 1) {
                matName = "clay blocks";
            } else {
                matName = "clay block";
            }
        } else if (mat == Material.JUKEBOX) {
            if (total > 1) {
                matName = "jukeboxes";
            } else {
                matName = "jukebox";
            }
        } else if (mat == Material.BOOKSHELF) {
            if (total > 1) {
                matName = "bookshelves";
            } else {
                matName = "bookshelf";
            }
        } else if (mat == Material.COBBLESTONE_STAIRS) {
            matName = "cobblestone stairs";
        } else if (mat == Material.STONE) {
            matName = "stone";
        } else if (mat == Material.GLASS) {
            matName = "glass";
        } else if (mat == Material.TNT) {
            matName = "TNT";
        } else if (mat == Material.SAND) {
            matName = "sand";
        } else if (mat == Material.DIRT) {
            matName = "dirt";
        } else if (mat == Material.NETHERRACK) {
            matName = "netherrack";
        } else if (mat == Material.SOUL_SAND) {
            matName = "soul sand";
        } else if (mat == Material.BEDROCK) {
            matName = "bedrock";
        } else if (mat == Material.GRAVEL) {
            if (total > 1) {
                matName = "gravel blocks";
            } else {
                matName = "gravel block";
            }
        } else if (mat == Material.GRASS) {
            if (total > 1) {
                matName = "grass blocks";
            } else {
                matName = "grass block";
            }
        } else if (mat == Material.CACTUS) {
            if (total > 1) {
                matName = "cacti";
            } else {
                matName = "cactus";
            }
        } else if (mat == Material.TORCH) {
            if (total > 1) {
                matName = "torches";
            } else {
                matName = "torch";
            }
        } else if (mat == Material.COBBLESTONE) {
            matName = "cobblestone";
        } else if (mat == Material.NETHER_BRICK_STAIRS) {
            if (total > 1) {
                matName = "nether brick stairs";
            } else {
                matName = "nether brick stair";
            }
        } else if (mat == Material.SANDSTONE) {
            matName = "sandstone";
        } else {
            matName = material(mat);
            if (total > 1) {
                matName += "s";
            }
        }
        return matName;
    }

    public static String material(Material mat) {
        return mat.name().toLowerCase().replace("_", " ");
    }

    public static String chatColor(ChatColor color) {
        return color.name().toLowerCase().replace("_", " ");
    }

    public static String capitalize(String string) {
        String[] words = string.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String x : words) {
            String capped = WordUtils.capitalize(x);
            sb.append(capped).append(" ");
        }
        return sb.toString().trim();
    }
}
