package kumomi.teleportstones.build;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.build.structure.OffsetBlock;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.blueprint.BlueprintManager;
import kumomi.teleportstones.storage.model.SimpleBlock;
import kumomi.teleportstones.storage.model.SimpleSign;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.BlockUtil;
import kumomi.teleportstones.util.SignTypes;

public class TeleportStoneValidator {

    public static String keyword;
    public static Integer minNameLength;

    public static enum ValidationStatus {
        ERROR, // Any unkown error
        NOT_RUN, //
        MISSING_BLUEPRINT, //
        UNKNOWN_BLUEPRINT, //
        MISSING_SIGN, //
        INVALID_SIGN_MATERIAL, //
        UNKOWN_WORLD, //
        INCORRECTLY_MARKED, // Sign doesn't start with right keyword
        MISSING_NAME, //
        INVALID_NAME_LENGTH, //
        INVALID_NAME, //
        MISSPLACED_SIGN, //
        MISSING_ATTACHED_BLOCK, //
        INCONSISTENT_NAME, //
        INVALID_STRUCTURE, //
        INCONSISTENT_WORLD, //
        INVALID_AMOUNT_BLOCKS, //
        WRONG_MATERIAL, //
        FULLY_VALIDATED_SIGN, //
        VALIDATED_SIGN, //
        VALIDATED_STRUCTURE, //
        VALIDATED_TELEPORTSTONE, //
        STRUCTURE_BLOCK_NOT_FOUND_IN_WORLD, //
        STRUCTURE_ALREADY_IN_USE //
    }

    private ValidationStatus status;
    private String message;

    public TeleportStoneValidator() {
        this.status = ValidationStatus.NOT_RUN;
        this.message = "";
    }

    public boolean validate(TeleportStone teleportStone) {

        if (teleportStone.getBluePrintId() == null) {
            status = ValidationStatus.MISSING_BLUEPRINT;
            message = "TeleportStone has no blueprint.";
            return false;
        }

        Blueprint bluePrint = BlueprintManager.blueprints.get(teleportStone.getBluePrintId());

        if (bluePrint == null) {
            status = ValidationStatus.UNKNOWN_BLUEPRINT;
            message = "TeleportStone has an unknown blueprint.";
            return false;
        }

        if (!validateSign(teleportStone.getSign(), bluePrint)) {
            return false;
        }

        if (teleportStone.getName() == null) {
            status = ValidationStatus.MISSING_NAME;
            message = "TeleportStone has no name.";
            return false;
        }

        String name = TeleportStoneBuilder.getNameFromSign(teleportStone.getSign());

        if (!teleportStone.getName().equals(name)) {
            status = ValidationStatus.INCONSISTENT_NAME;
            message = "TeleportStone name and name specified on sign differ.";
            return false;
        }

        if (!teleportStone.getWorld().equals(teleportStone.getSign().getWorld())) {
            status = ValidationStatus.INCONSISTENT_WORLD;
            message = "TeleportStone world and world of sign differ.";
            return false;
        }

        SimpleSign simpleSign = teleportStone.getSign();
        World world = Bukkit.getWorld(simpleSign.getWorld());
        Block signBlock = world.getBlockAt(simpleSign.getX(), simpleSign.getY(), simpleSign.getZ());

        if (!(signBlock.getState() instanceof Sign)) {
            this.status = ValidationStatus.INVALID_SIGN_MATERIAL;
            this.message = "Sign is not a sign.";
            return false;
        }

        Sign sign = (Sign) signBlock.getState();
        if (!TeleportStoneBuilder.getNameFromSign(sign).equals(name)) {
            this.status = ValidationStatus.INCONSISTENT_NAME;
            this.message = "Model sign name and ingame sign name differ.";
            return false;
        }

        if (!validateStructure(teleportStone.getSign(), teleportStone.getStructure(), bluePrint)) {
            return false;
        }

        status = ValidationStatus.VALIDATED_TELEPORTSTONE;
        message = "Successfully validated TeleportStone.";
        return true;
    }

    public boolean validateSign(SimpleSign simpleSign) {

        if (simpleSign == null) {
            status = ValidationStatus.MISSING_SIGN;
            message = "No sign specified.";
            return false;
        }

        World world = Bukkit.getWorld(simpleSign.getWorld());

        if (world == null) {
            status = ValidationStatus.UNKOWN_WORLD;
            message = "Target world of TeleportStone unknown. Target world: " + simpleSign.getWorld();
            return false;
        }

        if (!checkKeyword(simpleSign.getLine(0))) {
            status = ValidationStatus.INCORRECTLY_MARKED;
            message = "Missing TeleportStone marking. (No keyword)";
            return false;
        }

        String name = TeleportStoneBuilder.getNameFromSign(simpleSign);

        // if (name == null) {
        // status = ValidationStatus.MISSING_NAME;
        // message = "No name specified.";
        // return false;
        // }

        if (checkTeleportStoneNameLength(name)) {
            status = ValidationStatus.INVALID_NAME_LENGTH;
            message = "Specified name has an invalid length.";
            return false;
        }

        Block signBlock = world.getBlockAt(simpleSign.getX(), simpleSign.getY(), simpleSign.getZ());

        if (!SignTypes.signTypes.contains(signBlock.getType())) {
            status = ValidationStatus.INVALID_SIGN_MATERIAL;
            message = "Sign is not made out of a qualified material.";
            return false;
        }

        this.status = ValidationStatus.VALIDATED_SIGN;
        this.message = "Sign validated.";

        return true;
    }

    public boolean validateSign(SimpleSign simpleSign, Blueprint bluePrint) {

        if (simpleSign == null) {
            status = ValidationStatus.MISSING_SIGN;
            message = "No sign specified.";
            return false;
        }

        World world = Bukkit.getWorld(simpleSign.getWorld());

        if (world == null) {
            status = ValidationStatus.UNKOWN_WORLD;
            message = "Target world of TeleportStone unknown. Target world: " + simpleSign.getWorld();
            return false;
        }

        if (!checkKeyword(simpleSign.getLine(0))) {
            status = ValidationStatus.INCORRECTLY_MARKED;
            message = "Missing TeleportStone marking. (No keyword)";
            return false;
        }

        String name = TeleportStoneBuilder.getNameFromSign(simpleSign);

        if (checkTeleportStoneNameLength(name)) {
            status = ValidationStatus.INVALID_NAME_LENGTH;
            message = "Specified name has an invalid length.";
            return false;
        }

        Block signBlock = world.getBlockAt(simpleSign.getX(), simpleSign.getY(), simpleSign.getZ());

        if (!bluePrint.getSignMaterials().contains(signBlock.getType())) {
            status = ValidationStatus.INVALID_SIGN_MATERIAL;
            message = "Sign is not made out of material specified in blueprint.";
            return false;
        }

        this.status = ValidationStatus.VALIDATED_SIGN;
        this.message = "Sign validated.";

        return true;
    }

    public boolean fullyValidateSign(SimpleSign simpleSign, Blueprint bluePrint) {

        if (simpleSign == null) {
            status = ValidationStatus.MISSING_SIGN;
            message = "No sign specified.";
            return false;
        }

        if (bluePrint == null) {
            status = ValidationStatus.MISSING_BLUEPRINT;
            message = "Missing blueprint.";
            return false;
        }

        if (!validateSign(simpleSign, bluePrint)) {
            return false;
        }

        World world = Bukkit.getWorld(simpleSign.getWorld());

        Block signBlock = world.getBlockAt(simpleSign.getX(), simpleSign.getY(), simpleSign.getZ());

        if (!(signBlock.getState() instanceof Sign)) {
            this.status = ValidationStatus.INVALID_SIGN_MATERIAL;
            this.message = "Sign is not a sign.";
            return false;
        }


        WallSign signData;
        try {
            signData = (WallSign) signBlock.getState().getBlockData();
        } catch (ClassCastException e) {
            this.status = ValidationStatus.MISSPLACED_SIGN;
            this.message = "Sign needs to be placed on a side of a block.";
            return false;
        }

        BlockFace attached = signData.getFacing().getOppositeFace();
        Block blockBehingSign = signBlock.getRelative(attached);

        if (blockBehingSign == null) {
            this.status = ValidationStatus.MISSING_ATTACHED_BLOCK;
            this.message = "Block attached to sign not found.";
            return false;
        }

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        for (TeleportStone teleportStone : storage.findAll()) {
            for (SimpleBlock structureBlock : teleportStone.getStructure()) {
                if (structureBlock.equals(blockBehingSign)) {
                    this.status = ValidationStatus.STRUCTURE_ALREADY_IN_USE;
                    this.message = "Block attached to sign is already in use by another TeleportStone.";
                    return false;
                }
            }
        }

        this.status = ValidationStatus.VALIDATED_SIGN;
        this.message = "Sign validated.";

        return true;
    }

    public boolean validateStructure(SimpleSign simpleSign, List<SimpleBlock> structure, Blueprint bluePrint) {

        World world = Bukkit.getWorld(simpleSign.getWorld());

        if (world == null) {
            status = ValidationStatus.UNKOWN_WORLD;
            message = "Target world of TeleportStone unknown. Target world: " + simpleSign.getWorld();
            return false;
        }

        Block signBlock = world.getBlockAt(simpleSign.getX(), simpleSign.getY(), simpleSign.getZ());

        WallSign signData;
        try {
            signData = (WallSign) signBlock.getState().getBlockData();
        } catch (ClassCastException e) {
            this.status = ValidationStatus.MISSPLACED_SIGN;
            this.message = "Sign needs to be placed on a side of a block.";
            return false;
        }

        BlockFace attached = signData.getFacing().getOppositeFace();
        Block blockBehindSign = signBlock.getRelative(attached);

        if (blockBehindSign == null) {
            this.status = ValidationStatus.MISSING_ATTACHED_BLOCK;
            this.message = "Block attached to sign not found.";
            return false;
        }

        if (bluePrint.getOffsetBlocks().size() != structure.size()) {
            this.status = ValidationStatus.INVALID_AMOUNT_BLOCKS;
            this.message = "Invalid amount of blocks in structure.";
            return false;
        }

        BlockUtil blockUtil = new BlockUtil();

        // Validate blueprint blocks with blocks in game world
        // and saved blocks in TeleportStone
        // This is a transitive check.
        for (OffsetBlock offsetBlock : bluePrint.getOffsetBlocks()) {

            // Get block in game world.
            Block block = blockUtil.offsetBlock( //
                    blockBehindSign, //
                    offsetBlock.getX(), //
                    offsetBlock.getY(), //
                    offsetBlock.getZ() //
            );

            if (block == null) {
                this.status = ValidationStatus.STRUCTURE_BLOCK_NOT_FOUND_IN_WORLD;
                this.message = "Couldn't find block of structure in world.";
                return false;
            }

            if (!block.getWorld().getName().equals(simpleSign.getWorld())) {
                status = ValidationStatus.INCONSISTENT_WORLD;
                message = "World of sign and TeleportStone structure block differ.";
                return false;
            }

            // Check if block in game world has right material.
            if (!offsetBlock.getMaterials().contains(block.getType())) {
                this.status = ValidationStatus.WRONG_MATERIAL;
                this.message = "Wrong material used in TeleportStone structure. " //
                        + block.getType().name() //
                        + " at " //
                        + block.getLocation().getBlockX() //
                        + " | " //
                        + block.getLocation().getBlockY() //
                        + " | " //
                        + block.getLocation().getBlockZ() //
                        + " should be one of the following: " //
                        + offsetBlock.getMaterials(); //
                return false;
            }

            boolean foundStructureBlock = false;
            for (SimpleBlock structureBlock : structure) {
                if (structureBlock.equals(block)) {
                    foundStructureBlock = true;
                    break;
                }
            }

            if (!foundStructureBlock) {
                this.status = ValidationStatus.INVALID_STRUCTURE;
                this.message = "Couldn't find block in saved structure of TeleportStone.";
                return false;
            }

        }

        this.status = ValidationStatus.VALIDATED_STRUCTURE;
        this.message = "Structure validated.";
        return true;
    }

    public String getMessage() {
        return message;
    }

    public ValidationStatus getStatus() {
        return status;
    }

    public static boolean checkKeyword(String keyword) {
        if (keyword == null)
            return false;

        return ChatColor.stripColor(keyword).equals(TeleportStoneValidator.keyword);
    }

    // TODO maybe make configurable
    public static boolean checkTeleportStoneNameLength(String name) {
        if (name == null)
            return false;

        if (name.equals("")) {
            return false;
        }

        return name.length() < TeleportStoneValidator.minNameLength;
    }

}
