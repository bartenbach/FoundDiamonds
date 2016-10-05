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
            case GLOWING_REDSTONE_ORE:
            case NETHER_WARTS:
            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
                return ChatColor.DARK_RED;
            case GOLD_ORE:
            case PUMPKIN:
            case JACK_O_LANTERN:
            case GLOWSTONE:
            case CROPS:
            case GOLD_BLOCK:
                return ChatColor.GOLD;
            case MOSSY_COBBLESTONE:
            case LEAVES:
            case VINE:
            case LONG_GRASS:
            case WATER_LILY:
                return ChatColor.DARK_GREEN;
            case IRON_ORE:
            case CLAY_BRICK:
            case CAULDRON:
            case IRON_FENCE:
            case STONE:
            case SMOOTH_BRICK:
            case COBBLESTONE:
            case COBBLESTONE_STAIRS:
            case CLAY:
            case GRAVEL:
            case DISPENSER:
            case FURNACE:
            case BURNING_FURNACE:
            case IRON_DOOR_BLOCK:
            case STONE_BUTTON:
                return ChatColor.GRAY;
            case LAPIS_BLOCK:
            case LAPIS_ORE:
                return ChatColor.BLUE;
            case COAL_ORE:
            case MOB_SPAWNER:
            case BROWN_MUSHROOM:
            case SOUL_SAND:
                return ChatColor.DARK_GRAY;
            case OBSIDIAN:
            case MYCEL:
            case PORTAL:
                return ChatColor.DARK_PURPLE;
            case MELON_BLOCK:
            case SUGAR_CANE_BLOCK:
            case CACTUS:
            case GRASS:
            case SAPLING:
            case EMERALD_BLOCK:
            case EMERALD_ORE:
                return ChatColor.GREEN;
            case BRICK:
            case BRICK_STAIRS:
            case RED_MUSHROOM:
            case RED_ROSE:
            case NETHERRACK:
            case TNT:
                return ChatColor.RED;
            case SPONGE:
            case YELLOW_FLOWER:
            case SAND:
            case SANDSTONE:
            case TORCH:
                return ChatColor.YELLOW;
            default:
                return ChatColor.WHITE;
        }
    }
}
