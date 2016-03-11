package co.proxa.founddiamonds;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import co.proxa.founddiamonds.file.Config;
import co.proxa.founddiamonds.file.FileHandler;
import co.proxa.founddiamonds.file.FileUtils;
import co.proxa.founddiamonds.handlers.*;
import co.proxa.founddiamonds.listeners.*;
import co.proxa.founddiamonds.metrics.MetricsLite;
import co.proxa.founddiamonds.sql.MySQL;

import java.io.IOException;
import java.util.logging.Logger;

/*
Copyright 2011-2013 Blake Bartenbach

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

public class FoundDiamonds extends JavaPlugin {


    private Logger log;
    private final BlockPlaceListener blockPlaceListener = new BlockPlaceListener(this);
    private final BlockBreakListener blockBreakListener = new BlockBreakListener(this);
    private final PlayerDamageListener playerDamageListener = new PlayerDamageListener(this);
    private final PistonListener pistonListener = new PistonListener(this);
    private final TrapListener trapListener = new TrapListener(this);
    private final BroadcastHandler broadcastHandler = new BroadcastHandler(this);
    private final AdminMessageHandler adminMessageHandler = new AdminMessageHandler(this);
    private final BlockDamageListener lightLevelListener = new BlockDamageListener(this);
    private final LightLevelHandler lightLevelHandler = new LightLevelHandler(this);
    private final FileUtils fileUtils = new FileUtils(this);
    private final MySQL mysql = new MySQL(this);
    private final MapHandler mapHandler = new MapHandler(this);
    private final Permissions permissions = new Permissions(this);
    private final WorldHandler worldHandler = new WorldHandler(this);
    private final LoggingHandler loggingHandler = new LoggingHandler(this);
    private final TrapHandler trapHandler = new TrapHandler(this);
    private final FileHandler fileHandler = new FileHandler(this);
    private final PotionHandler potionHandler = new PotionHandler(this);
    private final ItemHandler itemHandler = new ItemHandler(this);
    private final MenuHandler menuHandler = new MenuHandler(this);
    private final BlockCounter blockTotal = new BlockCounter(this);

   /*
   TODO:
    MenuHandler set area?
    Is clean logging in SQL a popular request?
    Customizable admin message formats!
    Customizable light level message formats!
    */

   /*
   Changelog:

    */


   /*
   Test:
    Ideally?  Everything.
    */

    @Override
    public void onEnable() {
        log = this.getLogger();
        fileHandler.initFileVariables();
        fileHandler.checkFiles();
        potionHandler.getPotionList();
        worldHandler.checkWorlds();
        mapHandler.loadAllBlocks();
        getCommand("fd").setExecutor(new CommandHandler(this));
        registerEvents();
        startMetrics();
        mysql.getConnection();
        log.info("Enabled");
    }

    @Override
    public void onDisable() {
        log.info("Saving all data...");
        fileHandler.saveFlatFileData();
        log.info("Disabled");
    }

    public void registerEvents() {
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerDamageListener, this);
        pm.registerEvents(blockBreakListener, this);
        pm.registerEvents(blockPlaceListener, this);
        pm.registerEvents(pistonListener, this);
        pm.registerEvents(trapListener, this);
        pm.registerEvents(lightLevelListener, this);
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public LoggingHandler getLoggingHandler() {
        return loggingHandler;
    }

    public TrapHandler getTrapHandler() {
        return trapHandler;
    }

    public BlockPlaceListener getBlockPlaceListener() {
        return blockPlaceListener;
    }

    public WorldHandler getWorldHandler() {
        return worldHandler;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public PotionHandler getPotionHandler() {
        return potionHandler;
    }

    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }

    public BroadcastHandler getBroadcastHandler() {
        return broadcastHandler;
    }

    public AdminMessageHandler getAdminMessageHandler() {
        return adminMessageHandler;
    }

    public LightLevelHandler getLightLevelHandler() {
        return lightLevelHandler;
    }

    public BlockCounter getBlockCounter() {
        return blockTotal;
    }

    public Logger getLog() {
        return log;
    }

    public PluginDescriptionFile getPdf() {
        return this.getDescription();
    }

    private void startMetrics() {
        if (getConfig().getBoolean(Config.metrics)) {
            try {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();
            } catch (IOException e) {
                this.log.warning("Metrics failed to start - Ignoring.");
            }
        }
    }
}
