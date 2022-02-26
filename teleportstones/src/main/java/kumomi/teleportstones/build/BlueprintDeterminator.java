package kumomi.teleportstones.build;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;

import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.build.structure.OffsetBlock;
import kumomi.teleportstones.storage.blueprint.BlueprintManager;
import kumomi.teleportstones.storage.model.SimpleBlock;
import kumomi.teleportstones.storage.model.SimpleSign;
import kumomi.teleportstones.util.BlockUtil;

public class BlueprintDeterminator {

    private String message = null;

    // When no blueprint fits perfectly. These will give an estimation, for possible
    // solutions.
    private Blueprint mostSimilarBlueprint = null;
    private int matchingAmountOfBlocks = 0;
    private SimpleBlock nextRequiredBlock = null;
    private List<Material> nextRequiredMaterials = null;

    public Optional<Blueprint> determine(SimpleSign simpleSign) {

        World world = Bukkit.getWorld(simpleSign.getWorld());
        Block signBlock = world.getBlockAt( //
                simpleSign.getX() //
                , simpleSign.getY() //
                , simpleSign.getZ() //
        );

        // if (!(signBlock instanceof Sign)) {
        //     this.message = "Sign missing.";
        //     return Optional.empty();
        // }

        if (!(signBlock.getState().getBlockData() instanceof WallSign)) {
            this.message = "Not a WallSign missing.";
            return Optional.empty();
        }

        WallSign wallSign = (WallSign) signBlock.getState().getBlockData();
        BlockFace attached = wallSign.getFacing().getOppositeFace();
        Block blockBehindSign = signBlock.getRelative(attached);

        BlockUtil blockUtil = new BlockUtil();

        for (Blueprint bluePrint : BlueprintManager.blueprints.values()) {

            if (!bluePrint.getSignMaterials().contains(signBlock.getType())) {
                continue;
            }

            int matchingAmountOfBlocks = 0;
            SimpleBlock shouldBeBlock = null;
            List<Material> shouldBeMaterials = null;

            boolean fittingPerfect = true;
            for (OffsetBlock offsetBlock : bluePrint.getOffsetBlocks()) {
                Block b = blockUtil.offsetBlock(blockBehindSign, offsetBlock);

                if (!offsetBlock.getMaterials().contains(b.getType())) {

                    fittingPerfect = false;
                    if (shouldBeBlock == null) {

                        shouldBeBlock = new SimpleBlock( //
                                b.getX(), //
                                b.getY(), //
                                b.getZ(), //
                                b.getWorld().getName(), //
                                null //
                        );

                        shouldBeMaterials = offsetBlock.getMaterials();
                    }

                    continue;
                }

                matchingAmountOfBlocks++;
            }

            if (fittingPerfect) {
                this.message = "Perfect match found.";
                return Optional.of(bluePrint);
            }

            if (this.matchingAmountOfBlocks < matchingAmountOfBlocks) {
                this.matchingAmountOfBlocks = matchingAmountOfBlocks;
                this.nextRequiredBlock = shouldBeBlock;
                this.nextRequiredMaterials = shouldBeMaterials;
                this.mostSimilarBlueprint = bluePrint;
            }

        }

        if (matchingAmountOfBlocks == 0) {
            this.message = "Couldn't determine blueprint to use.";
        } else {
            this.message = //
                    "Couldn't determine blueprint to use. Most similar blueprint was " //
                            + this.mostSimilarBlueprint.getId() //
                            + ". It had " //
                            + this.matchingAmountOfBlocks //
                            + " matching blocks. Next required block would be at " //
                            + this.nextRequiredBlock.getX() //
                            + " | " //
                            + this.nextRequiredBlock.getY() //
                            + " | " //
                            + this.nextRequiredBlock.getZ() //
                            + " with one of the following materials: " //
            ;

            if (this.nextRequiredMaterials.size() != 0) {
                this.message += this.nextRequiredMaterials.get(0).name();
            }

            for (int i = 1; i < this.nextRequiredMaterials.size(); i++) {
                this.message += ", " + this.nextRequiredMaterials.get(i).name();
            }
        }
        return Optional.empty();
    }

    public String getMessage() {
        return message;
    }
}
