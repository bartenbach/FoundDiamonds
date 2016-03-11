package co.proxa.founddiamonds.file;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import co.proxa.founddiamonds.FoundDiamonds;
import co.proxa.founddiamonds.Trap;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

public class FileHandler {


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
        boolean firstRun = false;
        if (!fd.getDataFolder().exists()) {
            firstRun = true;
            try {
                verifyFileCreation(fd.getDataFolder().mkdirs(), fd.getDataFolder());
            } catch (Exception ex) {
                fd.getLog().severe(MessageFormat.format("Couldn't create plugins/FoundDiamonds folder {0}", ex));
                fd.getServer().getPluginManager().disablePlugin(fd);
            }
        }
        if (!logs.exists()) {
            try {
                verifyFileCreation(logs.createNewFile(), logs);
            } catch (Exception ex) {
                fd.getLog().severe(MessageFormat.format("Unable to create log file, {0}", ex));
            }
        }
        if (traps.exists()) {
            readTrapsFromFile(traps);
        }
        if (placed.exists() && !fd.getConfig().getBoolean(Config.mysqlEnabled)) {
            readBlocksFromFile(placed, fd.getBlockPlaceListener().getFlatFilePlacedBlocks());
        }
        fd.getConfig().options().copyDefaults(true);
        if (firstRun) {
            fd.getWorldHandler().addAllWorlds();
        }
        fd.saveConfig();
        if (fd.getConfig().getBoolean(Config.cleanLog)) {
            if (!cleanLog.exists()) {
                try {
                    verifyFileCreation(cleanLog.createNewFile(), cleanLog);
                } catch (IOException ex) {
                    fd.getLog().severe(MessageFormat.format("Unable to create log file, {0}", ex));
                }
            }
        }
    }

    public void verifyFileCreation(boolean b, File file) {
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

    public boolean writeTrapsToFile(File file, ArrayList<Trap> list, String info) {
        if (list.size() > 0) {
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
                        for (Trap m : list) {
                            out.write(m.getTrapSummary());
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

    public void readTrapsFromFile(File file) {
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
                               fs[fs.length-7], temp, Long.parseLong(fs[fs.length-2]) , Boolean.parseBoolean(fs[fs.length-1]));
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
        boolean temp = writeTrapsToFile(traps, fd.getTrapHandler().getTrapList(), info);
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
