package co.proxa.founddiamonds;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Date;

public class Trap {


	private final byte type;
	private final Material mat;
	private Material[] oldMat; // the blocks that were replaced
	private final String placer; // name of the player who set the trap
	private final Location location; // the 'middle' of the trap
	private final Date time; // the date the trap was added;
	private boolean persistent; // will the trap persist when broken



	public Trap(byte type, Material mat, String player, Location location, boolean persistent) {
		this.type = type;
		this.mat = mat;
		this.placer = player;
		this.location = location;
		this.time = new Date(System.currentTimeMillis());
		this.persistent = persistent;
	}

	public Trap(byte type, Material mat, Material[] oldMat, String player, Location loc, long time, boolean persistent) {
		this.type = type;
		this.mat = mat;
		this.oldMat = oldMat;
		this.placer = player;
		this.location = loc;
		this.time = new Date(time);
		this.persistent = persistent;
		//trapList.add(this);
		this.refillInverse();
	}

    public String getPlacer() {
        return this.placer;
    }

    public Date getTime() {
        return this.time;
    }

    public Location getLocation() {
        return this.location;
    }

    public Material getMaterial() {
        return this.mat;
    }

	private void refillInverse() {
		Block[] temp = this.returnLocations(this.location.getWorld());
		for(Block block : temp){
			//inverseList.put(block, this);
		}
	}

	public boolean createBlocks() {
		Block[] locations = this.returnLocations(this.location.getWorld());
		oldMat = new Material[locations.length];
        for (Block block : locations) {
            //if (inverseList.containsKey(block)) {
                //return false;
            //}
        }
		if (this.mat == Material.EMERALD_ORE) {
			oldMat[0] = this.location.getBlock().getType();
			//inverseList.put(location.getBlock(), this);
			location.getBlock().setType(mat);
		} else {
			oldMat[0] = locations[0].getType();
			oldMat[1] = locations[1].getType();
			oldMat[2] = locations[2].getType();
			oldMat[3] = locations[3].getType();
			//inverseList.put(locations[0], this);
			//inverseList.put(locations[1], this);
			//inverseList.put(locations[2], this);
			//inverseList.put(locations[3], this);
			locations[0].setType(mat);
			locations[1].setType(mat);
			locations[2].setType(mat);
			locations[3].setType(mat);
		}
		return true;
	}

	private Block[] returnLocations(World world) {
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
                return new Block[]{block1, block2, block3, block4};
            case 2:
                block1 = world.getBlockAt(x, y - 1, z);
                block2 = world.getBlockAt(x - 1, y - 2, z);
                block3 = world.getBlockAt(x , y - 2, z);
                block4 = world.getBlockAt(x -1, y - 1, z);
                return new Block[]{block1, block2, block3, block4};
            case 3:
                return new Block[]{this.location.getBlock()}; // emeralds
            default:
                System.out.println("FoundDiamonds: Trap has no type!");
                return null; // aliens
		}
	}

	public boolean isPersistent() {
		return this.persistent;
	}

    public int getID() {
        //return TrapHandler.getTrapList().indexOf(this);
        return 1; //FIXME bogus
    }

	public String getTrapSummary() { // method to summarize the trap object, for saving
		String oldMatString = "";
		for (Material material : oldMat) {
            oldMatString += material.getId() + ";";
		}
		return this.type + ";" + this.mat.getId() + ";" + oldMatString + this.placer + ";" + this.location
				.getBlockX() + ";" + this.location.getBlockY() + ";" + this.location
				.getBlockZ() + ";" + this.location.getWorld().getName() + ";" + this.time
				.getTime()   + ";" + this.persistent;
	}
}
