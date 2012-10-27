/**
 * 
 */
package org.seed419.founddiamonds;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.seed419.founddiamonds.util.Prefix;

/**
 * @author snoepje0
 * 
 * 
 * TODO: 
 * 	empty =)
 */
public class Trap {
	private final byte type; // the type, and thus form and size of the trap,
								// byte variable to save memory
	private final Material mat; // the trapblocks placed
	private Material[] oldmat; // the blocks that were replaced
	private final Player placer; // the person that placed the block
	private final Location location; // the 'middle' of the trap
	private final Date time; // the date the trap was added;
	private boolean persistant; // will the trap persist when broken, or will it
								// dissolve, was a request ticket on the dev
								// page
	private static ArrayList<Trap> list = new ArrayList<Trap>(); // map linking traps to locations, the middle of the trap
	private static Map<Block, Trap> inverselist = new HashMap<Block, Trap>(); // map linking locations to traps

	public Trap(byte type, Material mat, Player placer, Location location,
			boolean persistant, String item) {
		this.type = type;
		this.mat = mat;
		this.placer = placer;
		this.location = location;
		this.time = new Date(System.currentTimeMillis());
		this.persistant = persistant;
		list.add(this);

		if (!this.createBlocks()) {
			list.remove(this);

		} else {
			placer.sendMessage(ChatColor.WHITE + "Trap placed with " + item);
		}
	}

	public Trap(byte type, Material mat, Material[] oldmat, Player placer,
			Location loc, long time, boolean persistent) {
		this.type = type;
		this.mat = mat;
		this.oldmat = oldmat;
		this.placer = placer;
		this.location = loc;
		this.time = new Date(time);
		this.persistant = persistent;
		list.add(this);
		placer.sendMessage("trap placed");
		this.refillinverse();
		//This way, the traps are subject to tampering through the savefile. Maybe some sort of hashing?
		

	}
	
	private void refillinverse() {
		Block[] temp = this.returnLocations();
		for(Block block : temp){
			inverselist.put(block, this);
		}
	}

	private boolean createBlocks() {

		Block[] locations = this.returnLocations();
		oldmat = new Material[locations.length];
		for (int i = 0; i < locations.length; i++) { // need to run this one firstly, this needs to complete before I can start adding the locations
			if (inverselist.containsKey(locations[i])) {
				placer.sendMessage(ChatColor.RED + "Unable to place trap here, there's an other one in the way");
				return false;
			}
		}
		if (this.mat == Material.EMERALD_ORE) {
			oldmat[0] = this.location.getBlock().getType();
			inverselist.put(location.getBlock(), this);
			location.getBlock().setType(mat);
		} else {
			oldmat[0] = locations[0].getType();
			oldmat[1] = locations[1].getType();
			oldmat[2] = locations[2].getType();
			oldmat[3] = locations[3].getType();
			inverselist.put(locations[0], this);
			inverselist.put(locations[1], this);
			inverselist.put(locations[2], this);
			inverselist.put(locations[3], this);
			locations[0].setType(mat);
			locations[1].setType(mat);
			locations[2].setType(mat);
			locations[3].setType(mat);


		}
		/*
		 * for (int i = 0; i < locations.length; i++) {
		 * oldmat[i] = locations[i].getBlock().getType(); // initialization of old materials
		 * inverselist.put(locations[i], this); // adding the locations to the inverse list
		 * locations[i].getBlock().setType(mat); // replacing the block with the trap block
		 * placer.sendMessage("debug : old meterial: " + oldmat[i]);
		 * placer.sendMessage("debug : new material: " + mat);
		 * }
		 */
		return true;
	}

	private Block[] returnLocations() { // edit this method to add more ore formations
		// TODO Make this thing read formations from the config file?

        World world = placer.getWorld();
        Block block1;
        Block block2;
        Block block3;
        Block block4;
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
		switch (this.type) {
		case 1:
			block1 = world.getBlockAt(x, y - 1, z);
            block2 = world.getBlockAt(x, y - 2, z + 1);
            block3 = world.getBlockAt(x - 1, y - 2, z);
            block4 = world.getBlockAt(x, y - 2, z);

			Block[] temp1 = { block1, block2, block3, block4 };
			return temp1;
		case 2:
			block1 = world.getBlockAt(x, y - 1, z);
            block2 = world.getBlockAt(x - 1, y - 2, z);
            block3 = world.getBlockAt(x , y - 2, z);
            block4 = world.getBlockAt(x -1, y - 1, z);

			Block[] temp2 = { block1, block2, block3, block4 };
			return temp2;

			// the 2 basic ones used in the original classes
		case 3:
			Block[] temp3 = { this.location.getBlock() };
			return temp3; // emeralds
		}
		return null;
	}

	public void removeTrap() { // not entirely sure about this, but this should remove the trap object(unfamiliar with garbage collecting :-S )
		// it also puts the old blocks back in place
		// I dislike the public modifier on this thing, but it was the only way to get it to work with the onBlockBreak event (in TrapListener)
		Block[] temp = this.returnLocations();
		for (int i = 0; i < temp.length; i++) {
			temp[i].setType(oldmat[i]);
			inverselist.remove(temp[i]);
		}
		list.remove(this);
	}

	public static boolean Menu(CommandSender sender, int page) { // TODO: This part still looks a bit messy
		if (sender.hasPermission("fd.trap.remove.all") || sender.isOp()) { // permissions to see and remove all traps
			ArrayList<Trap> trapList = new ArrayList<Trap>();

			for (Trap trap : list) {
				trapList.add(trap);
			}
			if (page >= 0 && ( (page) *5 < list.size()) ) {		//sane page specified?
				int id1 = (page)*5 ; //begin of the substring
				int id2 = (page +1) *5 -1;  //end of the substring
				if(id2 > list.size()){
					id2 = list.size()-1;
				}
				for(Trap object : trapList){
				sendMenu(sender, object);
				}
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Page number is invalid");
				return false;
			}
		} else if (sender.hasPermission("fd.trap.remove.self")) { // only permission to remove, and thus to see own traps
			ArrayList<Trap> trapList = new ArrayList<Trap>();
			for (Trap trap : list) {
				if (trap.placer == sender)
					trapList.add(trap);
			}
			if (page >= 0 && ( (page) *5 < list.size()) ) {		//sane page specified?
				int id1 = (page)*5 ; //begin of the substring
				int id2 = (page +1) *5 -1;  //end of the substring
				if(id2 > list.size()){
					id2 = list.size()-1;
				}
				for(Trap object : trapList){
				sendMenu(sender, object);
				}
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Page number is invalid");
				return false;
			}
		} else {
			sender.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " You don't have permission to do that.");
			return false;
		}

	}

	private static void sendMenu(CommandSender sender, Trap object) { // eclipse formats this weirdly...

			sender.sendMessage(""+ChatColor.WHITE + "[" + list.indexOf(object) + "]" + DateFormat
					.getDateInstance(DateFormat.MEDIUM).format((object.time)) + " - Location: " + object.location
					.getBlockX() + " " + object.location.getBlockY() + " " + object.location
					.getBlockZ() + " By " + object.placer.getDisplayName());
			}

	public static void removeTrapCmd(CommandSender sender, int id) {
		if (id >= 0 && id < list.size()) {
			Trap temp = list.get(id);
			if ((temp.placer == sender && sender
					.hasPermission("fd.trap.remove.self") || sender
						.hasPermission("fd.trap.remove.all"))) {
				temp.removeTrap();
				sender.sendMessage(ChatColor.YELLOW + "Trap removed succesfully");
			}
		}
	}

	public boolean isPersistant() {
		return this.persistant;
	}

	public static ArrayList<Trap> getList() {
		return list;
	}

	public static void setList(ArrayList<Trap> list) {
		Trap.list = list;
	}

	public static Map<Block, Trap> getInverselist() {
		return inverselist;
	}

	public String Trapsummary() { // method to summarize the trap object, for saving purposed
		String oldmatstring = "";
		for (Material material : oldmat) {
			oldmatstring += material.getId() + ";";
		}

		String res = this.type + ";" + this.mat.getId() + ";" + oldmatstring + this.placer.getDisplayName() + ";" + this.location
				.getBlockX() + ";" + this.location.getBlockY() + ";" + this.location
				.getBlockZ() + ";" + this.location.getWorld().getName() + ";" + this.time
				.getTime() + ";" + this.persistant;

		return res;
	}
}
