package org.seed419.founddiamonds.file;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.seed419.founddiamonds.FoundDiamonds;
import org.seed419.founddiamonds.Trap;

import com.avaje.ebean.enhance.ant.OfflineFileTransform;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
public class FileHandler {


    //todo this clearly still needs testing and refactoring
    private FoundDiamonds fd;
    private File logs;
    private File traps;
    private File cleanLog;
    private File placed;
    private boolean printed = false;


    public FileHandler(FoundDiamonds f) {
        this.fd = f;
    }

    public void initFileVariables() {
        logs = new File(fd.getDataFolder(), "log.txt");
        traps = new File(fd.getDataFolder(), ".traps");
        placed = new File(fd.getDataFolder(), ".placed");
        cleanLog = new File(fd.getDataFolder(), "cleanlog.txt");
    }

    public void checkFiles() {
        boolean firstrun = false;
        if (!fd.getDataFolder().exists()) {
            firstrun = true;
            try {
                verfiyFileCreation(fd.getDataFolder().mkdirs(), fd.getDataFolder());
            } catch (Exception ex) {
                fd.getLog().severe(MessageFormat.format("Couldn't create plugins/FoundDiamonds folder {0}", ex));
                fd.getServer().getPluginManager().disablePlugin(fd);
            }
        }
        if (!logs.exists()) {
            try {
                verfiyFileCreation(logs.createNewFile(), logs);
            } catch (Exception ex) {
                fd.getLog().severe(MessageFormat.format("Unable to create log file, {0}", ex));
            }
        }
        if (traps.exists()) {
            readTrapsFromFile(traps, fd.getTrapHandler().getTrapBlocks());
        }
        if (placed.exists() && !fd.getConfig().getBoolean(Config.mysqlEnabled)) {
            readBlocksFromFile(placed, fd.getBlockPlaceListener().getFlatFilePlacedBlocks());
        }
        fd.getConfig().options().copyDefaults(true);
        if (firstrun) {
            fd.getWorldHandler().addAllWorlds();
        }
        fd.saveConfig();
        if (fd.getConfig().getBoolean(Config.cleanLog)) {
            if (!cleanLog.exists()) {
                try {
                    verfiyFileCreation(cleanLog.createNewFile(), cleanLog);
                } catch (IOException ex) {
                    fd.getLog().severe(MessageFormat.format("Unable to create log file, {0}", ex));
                }
            }
        }
    }

    public void verfiyFileCreation(boolean b, File file) {
        if (!b) {
            fd.getLog().severe(MessageFormat.format("Failed to create {0}!", file.getName()));
        }
    }

    public boolean writeBlocksToFile(File file, Collection<Location> blockList, String info) {
        if (blockList.size() > 0) {
            if (fd.getDataFolder().exists()) {
                PrintWriter out = null;
                try {
                    if (!file.exists()) {
                        boolean success = file.createNewFile();
                        if (!success) {
                            fd.getLog().severe(MessageFormat.format("[{0}] Couldn't create file to store blocks in", file.getName()));
                        }
                    }
                    try {
                        out =  new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
                        out.write("# " + info);
                        out.println();
                        for (Location m : blockList) {
                            out.write(m.getWorld().getName() + ";" + m.getX() + ";" + m.getY() + ";" + m.getZ());
                            out.println();
                        }
                    } catch (IOException ex) {
                        fd.getLog().severe(MessageFormat.format("Error writing blocks to {0}!", file.getName()));
                    } finally {
                        fd.getFileUtils().close(out);
                    }
                    return true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return false;
                }
            } else {
                if (!printed) {
                    fd.getLog().warning("Plugin folder not found.  Did you delete it?");
                    printed = true;
                }
                return false;
            }
        } else {
            if (file.exists()) {
                boolean deletion = file.delete();
                if (deletion) {
                    fd.getLog().info("Deleted an empty unused FoundDiamonds file, for the sake of cleanliness");
                }
            }
            return true;
        }
    }

    public boolean writeTrapsToFile(File file, ArrayList<Trap> trapList, String info) {
        if (trapList.size() > 0) {
            if (fd.getDataFolder().exists()) {
                PrintWriter out = null;
                try {
                    if (!file.exists()) {
                        boolean success = file.createNewFile();
                        if (!success) {
                            fd.getLog().severe(MessageFormat.format("[{0}] Couldn't create file to store traps in", file.getName()));
                        }
                    }
                    try {
                        out =  new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
                        out.write("# " + info);
                        out.println();
                        for (Trap m : trapList) {
                            out.write(m.Trapsummary());
                            out.println();
                        }
                    } catch (IOException ex) {
                        fd.getLog().severe(MessageFormat.format("Error writing traps to {0}!", file.getName()));
                    } finally {
                        fd.getFileUtils().close(out);
                    }
                    return true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return false;
                }
            } else {
                if (!printed) {
                    fd.getLog().warning("Plugin folder not found.  Did you delete it?");
                    printed = true;
                }
                return false;
            }
        } else {
            if (file.exists()) {
                boolean deletion = file.delete();
                if (deletion) {
                    fd.getLog().info("Deleted an empty unused FoundDiamonds file, for the sake of cleanliness");
                }
            }
            return true;
        }
    }

    
    public void readBlocksFromFile(File file, Collection<Location> list) {
        BufferedReader b = null;
        try {
            b = new BufferedReader(new FileReader(file));
            String strLine = b.readLine();
            while (strLine != null) {
                if (!strLine.startsWith("#")) {
                    try {
                        String[] fs = strLine.split(";");
                        Location lo = new Location(fd.getServer().getWorld(fs[0]), Double.parseDouble(fs[1]),
                                Double.parseDouble(fs[2]), Double.parseDouble(fs[3]));
                        list.add(lo);
                    } catch (Exception ex) {
                        fd.getLog().severe(MessageFormat.format("Invalid block in file.  Please delete {0}", file.getName()));
                    }
                }
                strLine = b.readLine();
            }
        } catch (Exception ex) {
            fd.getLog().severe(MessageFormat.format("Problem reading from {0}.  Please delete it.", file.getName()));
        } finally {
            fd.getFileUtils().close(b);
        }
    }

    public void readTrapsFromFile(File file, ArrayList<Trap> arrayList) {
        BufferedReader b = null;
        try {
            b = new BufferedReader(new FileReader(file));
            String strLine = b.readLine();
            while (strLine != null) {
                if (!strLine.startsWith("#")) {
                    try {
                        String[] fs = strLine.split(";");
                        Material[] oldmats = new Material[(Byte.parseByte(fs[0]) == 3) ? 1 : 4];
                        for(int i = 0 ; i < oldmats.length; i++){
                        	oldmats[i] = Material.getMaterial(Integer.parseInt(fs[2+i]));
                        }
                        Location temp = new Location(fd.getServer().getWorld(fs[fs.length-3]),Integer.parseInt(fs[fs.length-6]),Integer.parseInt(fs[fs.length-5]),Integer.parseInt(fs[fs.length-4])); 
                        Trap lo = new Trap(Byte.parseByte(fs[0]), Material.getMaterial(Integer.parseInt(fs[1])),oldmats,
                               Bukkit.getPlayer(fs[fs.length-7]), temp, Long.parseLong(fs[fs.length-2]) , Boolean.parseBoolean(fs[fs.length-1])) ;
                        //arrayList.add(lo); // Not needed, since they get reformed as trap blocks
                    } catch (Exception ex) {
                        fd.getLog().severe(MessageFormat.format("Invalid block in file.  Please delete {0}", file.getName()));
                    }
                }
                strLine = b.readLine();
            }
        } catch (Exception ex) {
            fd.getLog().severe(MessageFormat.format("Problem reading from {0}.  Please delete it.", file.getName()));
        } finally {
            fd.getFileUtils().close(b);
        }
    }
    
    
    public void saveFlatFileData() {
        String info = "This file stores your trap block locations.";
        boolean temp = writeTrapsToFile(traps, fd.getTrapHandler().getTrapBlocks(), info);
        boolean temp2 = true;
        if (!fd.getConfig().getBoolean(Config.mysqlEnabled)) {
            String info5 = "This file stores blocks that won't be announced because players placed them.";
            temp2 = writeBlocksToFile(placed, fd.getBlockPlaceListener().getFlatFilePlacedBlocks(), info5);
        }
        if (temp && temp2) {
            fd.getLog().info("Data successfully saved.");
        } else {
            fd.getLog().warning("Couldn't save blocks to files!");
            fd.getLog().warning("You could try deleting .placed and .traps if they exist");
        }
    }

    public void deletePlaced(CommandSender sender) {
        if (placed.exists()) {
            boolean success = placed.delete();
            if (success) {
                sender.sendMessage(ChatColor.AQUA + "Placed block file deleted.");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Unable to delete .placed file :(");
                sender.sendMessage(ChatColor.DARK_RED + "Try stopping the server and doing it manually.");
            }
        }
    }

    public File getLogFile() {
        return logs;
    }

    public File getCleanLog() {
        return cleanLog;
    }
}
