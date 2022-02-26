package kumomi.teleportstones.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import kumomi.teleportstones.storage.model.SimpleBlock;

public class TeleportStoneUtil {
    
    // The offsets are relative to the block behind the sign
    public final static SimpleBlock offsetTopBlock;
    public final static SimpleBlock offsetMiddleBlock;
    public final static SimpleBlock offsetBottomBlock;

    public final static SimpleBlock offsetFoundationBlock1;
    public final static SimpleBlock offsetFoundationBlock2;
    public final static SimpleBlock offsetFoundationBlock3;
    public final static SimpleBlock offsetFoundationBlock4;
    public final static SimpleBlock offsetFoundationBlock5;
    public final static SimpleBlock offsetFoundationBlock6;
    public final static SimpleBlock offsetFoundationBlock7;
    public final static SimpleBlock offsetFoundationBlock8;
    public final static SimpleBlock offsetFoundationBlock9;

    public final static List<SimpleBlock> offsets;

    /*
     * The structure blocks are relative to the middle block of the tower part of
     * TeleportStone.
     */

    // | top block
    // | middle block
    // | bottom block
    // ||| foundation

    static {

        // Top Block
        offsetTopBlock = new SimpleBlock(0, 1, 0, null, Material.OBSIDIAN);

        // Middle Block (Sign is played on a side of this block)
        offsetMiddleBlock = new SimpleBlock(0, 0, 0, null, Material.OBSIDIAN);

        // Bottom Block
        offsetBottomBlock = new SimpleBlock(0, -1, 0, null, Material.OBSIDIAN);

        // Foundation
        offsetFoundationBlock1 = new SimpleBlock(-1, -2, -1, null, Material.STONE);
        offsetFoundationBlock2 = new SimpleBlock(0, -2, -1, null, Material.STONE);
        offsetFoundationBlock3 = new SimpleBlock(1, -2, -1, null, Material.STONE);
        offsetFoundationBlock4 = new SimpleBlock(-1, -2, 0, null, Material.STONE);
        offsetFoundationBlock5 = new SimpleBlock(0, -2, 0, null, Material.STONE);
        offsetFoundationBlock6 = new SimpleBlock(1, -2, 0, null, Material.STONE);
        offsetFoundationBlock7 = new SimpleBlock(-1, -2, 1, null, Material.STONE);
        offsetFoundationBlock8 = new SimpleBlock(0, -2, 1, null, Material.STONE);
        offsetFoundationBlock9 = new SimpleBlock(1, -2, 1, null, Material.STONE);

        offsets = Arrays.asList(offsetTopBlock, offsetMiddleBlock, offsetBottomBlock, offsetFoundationBlock1,
                offsetFoundationBlock2, offsetFoundationBlock3, offsetFoundationBlock4, offsetFoundationBlock5,
                offsetFoundationBlock6, offsetFoundationBlock7, offsetFoundationBlock8, offsetFoundationBlock9);
    }
}
