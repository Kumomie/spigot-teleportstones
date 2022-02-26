package kumomi.teleportstones.util;

import org.bukkit.block.Block;

import kumomi.teleportstones.build.structure.OffsetBlock;
import kumomi.teleportstones.storage.model.SimpleBlock;

public class BlockUtil {

    public Block offsetBlock(Block block, int offsetX, int offsetY, int offsetZ) {

        int x = block.getX() + offsetX;
        int y = block.getY() + offsetY;
        int z = block.getZ() + offsetZ;

        return block.getWorld().getBlockAt(x, y, z);
    }

    public Block offsetBlock(Block block, OffsetBlock offsetBlock) {

        int x = block.getX() + offsetBlock.getX();
        int y = block.getY() + offsetBlock.getY();
        int z = block.getZ() + offsetBlock.getZ();

        return block.getWorld().getBlockAt(x, y, z);
    }



    public SimpleBlock getOffsetSimpleBlock(SimpleBlock block, int offsetX, int offsetY, int offsetZ) {

        int x = block.getX() + offsetX;
        int y = block.getY() + offsetY;
        int z = block.getZ() + offsetZ;

        SimpleBlock offsetBlock = new SimpleBlock(x, y, z, block.getWorld(), null);

        return offsetBlock;
    }

    public SimpleBlock getOffsetSimpleBlock(Block block, int offsetX, int offsetY, int offsetZ) {

        int x = block.getX() + offsetX;
        int y = block.getY() + offsetY;
        int z = block.getZ() + offsetZ;

        SimpleBlock offsetBlock = new SimpleBlock(x, y, z, block.getWorld().getName(), null);

        return offsetBlock;
    }
}
