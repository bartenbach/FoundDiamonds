package co.proxa.founddiamonds.util;

import org.bukkit.ChatColor;

public class Prefix {

    private final static String chatPrefix = "[FD]";
    private final static String menuPrefix = ChatColor.BOLD + "[FD] " + ChatColor.RESET;
    private final static String adminPrefix = ChatColor.RED + "[FD]";
    private final static String debugPrefix = "[FD Debug] ";
    private final static String loggingPrefix = "[FoundDiamonds] ";

    public static String getChatPrefix() {
        return chatPrefix;
    }

    public static String getAdminPrefix() {
        return adminPrefix;
    }

    public static String getDebugPrefix() {
        return debugPrefix;
    }

    public static String getLoggingPrefix() {
        return loggingPrefix;
    }

    public static String getMenuPrefix() {
        return menuPrefix;
    }
}
