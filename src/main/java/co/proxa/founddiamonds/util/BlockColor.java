package co.proxa.founddiamonds.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class BlockColor {

    public static ChatColor getBlockColor(Material mat) {
        switch (mat) {
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
                return ChatColor.AQUA;
            case REDSTONE_ORE:
            case REDSTONE_BLOCK:
            case NETHER_WART:
            case REDSTONE_TORCH:
                return ChatColor.DARK_RED;
            case GOLD_ORE:
            case PUMPKIN:
            case JACK_O_LANTERN:
            case GLOWSTONE:
            case WHEAT:
            case GOLD_BLOCK:
                return ChatColor.GOLD;
            case MOSSY_COBBLESTONE:
            case JUNGLE_LEAVES:
            case VINE:
            case TALL_GRASS:
                return ChatColor.DARK_GREEN;
            case IRON_ORE:
            case CAULDRON:
            case STONE:
            case COBBLESTONE:
            case COBBLESTONE_STAIRS:
            case CLAY:
            case GRAVEL:
            case DISPENSER:
            case FURNACE:
            case STONE_BUTTON:
                return ChatColor.GRAY;
            case LAPIS_BLOCK:
            case LAPIS_ORE:
                return ChatColor.BLUE;
            case COAL_ORE:
            case SPAWNER:
            case BROWN_MUSHROOM:
            case SOUL_SAND:
                return ChatColor.DARK_GRAY;
            case OBSIDIAN:
                return ChatColor.DARK_PURPLE;
            case CACTUS:
            case GRASS:
            case EMERALD_BLOCK:
            case EMERALD_ORE:
                return ChatColor.GREEN;
            case BRICK:
            case BRICK_STAIRS:
            case RED_MUSHROOM:
            case NETHERRACK:
            case TNT:
                return ChatColor.RED;
            case SPONGE:
            case SAND:
            case SANDSTONE:
            case TORCH:
                return ChatColor.YELLOW;
            default:
                return ChatColor.WHITE;
        }
    }
}
