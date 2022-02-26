package kumomi.teleportstones.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

import kumomi.teleportstones.build.TeleportStoneValidator.ValidationStatus;
import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.build.structure.OffsetBlock;
import kumomi.teleportstones.storage.model.SimpleBlock;
import kumomi.teleportstones.storage.model.SimpleSign;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.BlockUtil;

public class TeleportStoneBuilder {

    public static String getNameFromSign(SimpleSign simpleSign) {
        return simpleSign.getLine(1) + (simpleSign.getLine(2).isEmpty() ? "" : " " + simpleSign.getLine(2));
    }

    public static String getNameFromSign(Sign sign) {
        return sign.getLine(1) + (sign.getLine(2).isEmpty() ? "" : " " + sign.getLine(2));
    }

    public static enum BuildStatus {
        ERROR, // Any unkown error
        INVALID_BLUEPRINT, //
        INVALID_SIGN, //
        INVALID_STRUCTURE, //
        SUCCESS //
    }

    private String message;
    private BuildStatus status;

    private TeleportStone teleportStone;

    private SimpleSign simpleSign;
    private Blueprint blueprint;

    private TeleportStoneValidator validator;

    public TeleportStoneBuilder() {
        this.teleportStone = new TeleportStone();
        this.validator = new TeleportStoneValidator();

    }

    public TeleportStoneBuilder sign(SimpleSign simpleSign) {
        this.simpleSign = simpleSign;
        return this;
    }

    public TeleportStoneBuilder blueprint(Blueprint blueprint) {
        this.blueprint = blueprint;
        return this;
    }

    /**
     * <p>
     * This method builds and validates a TeleportStone. Many aspects will be
     * tested, like name, structure, etc. Validation does not include storage. So
     * you need to seperatly validate this aspect.
     * </p>
     * <p>
     * If the TeleportStone failed validation, use getValidator() for more
     * information.
     * </p>
     * 
     * @return An Optional of an validated TeleportStone.
     */
    public Optional<TeleportStone> buildAndValidate() {

        if (blueprint == null) {
            this.status = BuildStatus.INVALID_BLUEPRINT;
            this.message = "Missing blueprint.";
            return Optional.empty();
        }

        teleportStone.setBluePrintId(blueprint.getId());

        if (!this.validator.fullyValidateSign(this.simpleSign, this.blueprint)) {

            if (this.validator.getStatus() == ValidationStatus.ERROR) {
                this.status = BuildStatus.ERROR;
                this.message = validator.getMessage();
            } else {
                this.status = BuildStatus.INVALID_SIGN;
                this.message = validator.getMessage();
            }

            return Optional.empty();
        }

        teleportStone.setSign(simpleSign);
        teleportStone.setWorld(simpleSign.getWorld());

        String name = getNameFromSign(simpleSign);

        teleportStone.setName(name);

        World world = Bukkit.getWorld(simpleSign.getWorld());

        Block signBlock = world.getBlockAt(simpleSign.getX(), simpleSign.getY(), simpleSign.getZ());
        WallSign signData = (WallSign) signBlock.getState().getBlockData();
        BlockFace attached = signData.getFacing().getOppositeFace();
        Block blockBehindSign = signBlock.getRelative(attached);

        List<SimpleBlock> structure = new ArrayList<>();
        BlockUtil blockUtil = new BlockUtil();

        for (OffsetBlock offsetBlock : blueprint.getOffsetBlocks()) {

            SimpleBlock simpleBlock = blockUtil.getOffsetSimpleBlock(//
                    blockBehindSign, //
                    offsetBlock.getX(), //
                    offsetBlock.getY(), //
                    offsetBlock.getZ() //
            );

            Block b = world.getBlockAt( //
                    simpleBlock.getX(), //
                    simpleBlock.getY(), //
                    simpleBlock.getZ() //
            );

            simpleBlock.setMaterial(b.getType());

            structure.add(simpleBlock);
        }

        if (!this.validator.validateStructure(simpleSign, structure, blueprint)) {

            if (this.validator.getStatus() == ValidationStatus.ERROR) {
                this.status = BuildStatus.ERROR;
                this.message = validator.getMessage();
            } else {
                this.status = BuildStatus.INVALID_STRUCTURE;
                this.message = validator.getMessage();
            }

            return Optional.empty();
        }

        this.teleportStone.setStructure(structure);

        this.status = BuildStatus.SUCCESS;
        this.message = "Successfully build TeleportStone.";

        return Optional.of(this.teleportStone);
    }

    public String getMessage() {
        return message;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public TeleportStoneValidator getValidator() {
        return validator;
    }

    

}
