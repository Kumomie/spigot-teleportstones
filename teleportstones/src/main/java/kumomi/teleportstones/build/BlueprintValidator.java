package kumomi.teleportstones.build;

import org.bukkit.Material;

import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.build.structure.OffsetBlock;
import kumomi.teleportstones.storage.blueprint.BlueprintManager;
import kumomi.teleportstones.storage.scope.ScopeManager;

public class BlueprintValidator {

    public static enum ValidationStatus {
        ERROR //
        , NOT_RUN //
        , MISSING_BLUEPRINT //
        , MISSING_ID //
        , EMPTY_ID //
        , ID_ALREADY_EXISTS //
        , INVALID_ID //
        , MISSING_SCOPE //
        , EMPTY_SCOPE //
        , UNKNOWN_SCOPE //
        , NOT_SET_VARIABLE_WORLDBOUND //
        , RANGE_SPECIFIED_IN_NOT_WORLDBOUND //
        , INVALID_RANGE //
        , MISSING_OFFSET_BLOCKS //
        , INVALID_OFFSET_BLOCK //
        , MISSING_SIGN_MATERIALS //
        , EMPTY_SIGN_MATERIALS //
        , INVALID_SIGN_MATERIAL //
        , SUCCESS
    }

    private ValidationStatus status;
    private String message;

    public BlueprintValidator() {
        this.status = ValidationStatus.NOT_RUN;
        this.message = "";
    }

    public boolean validateBeforeAdd(Blueprint bluePrint) {

        if (bluePrint == null) {
            this.status = ValidationStatus.MISSING_BLUEPRINT;
            this.message = "BluePrint is missing.";
            return false;
        }

        // Check ID
        if (bluePrint.getId() == null) {
            this.status = ValidationStatus.MISSING_ID;
            this.message = "ID is missing.";
            return false;
        }

        if (bluePrint.getId().equals("")) {
            this.status = ValidationStatus.EMPTY_ID;
            this.message = "ID is empty.";
            return false;
        }

        if (BlueprintManager.blueprints.containsKey(bluePrint.getId())) {
            this.status = ValidationStatus.ID_ALREADY_EXISTS;
            this.message = "ID " + bluePrint.getId() + " already exists.";
            return false;
        }

        if(bluePrint.getId().contains(" ")){
            this.status = ValidationStatus.INVALID_ID;
            this.message = "ID " + bluePrint.getId() + " contains white space.";
            return false;
        }

        // Check Scope
        if (bluePrint.getScope() == null) {
            this.status = ValidationStatus.MISSING_SCOPE;
            this.message = "Scope is missing.";
            return false;
        }

        if (bluePrint.getScope().equals("")) {
            this.status = ValidationStatus.EMPTY_SCOPE;
            this.message = "Scope is empty.";
            return false;
        }

        if (!ScopeManager.scopes.containsKey(bluePrint.getScope()) //
                && !ScopeManager.defaultScopes.contains(bluePrint.getScope())) {
            this.status = ValidationStatus.UNKNOWN_SCOPE;
            this.message = "Scope " + bluePrint.getScope() + " is unknown.";
            return false;
        }

        // Check isWorldBound Variable
        if (bluePrint.isWorldBound() == null) {
            this.status = ValidationStatus.NOT_SET_VARIABLE_WORLDBOUND;
            this.message = "Variable isWorldBound is missing.";
            return false;
        }

        // Check range
        if (bluePrint.getRange() != null && bluePrint.getRange() < 0) {
            this.status = ValidationStatus.INVALID_RANGE;
            this.message = "Range is a negative number, which is not allowed.";
            return false;
        }

        // Check worldbound and range
        if (bluePrint.isWorldBound() == false && bluePrint.getRange() != null) {
            this.status = ValidationStatus.RANGE_SPECIFIED_IN_NOT_WORLDBOUND;
            this.message = "It's invalid to specify a range in a not world bound TeleportStone.";
            return false;
        }

        // Check offset blocks
        if (bluePrint.getOffsetBlocks() == null || bluePrint.getOffsetBlocks().isEmpty()) {
            this.status = ValidationStatus.MISSING_OFFSET_BLOCKS;
            this.message = "No offset blocks specified.";
            return false;
        }

        for (OffsetBlock offsetBlock : bluePrint.getOffsetBlocks()) {
            if (offsetBlock.getX() == null) {
                this.status = ValidationStatus.INVALID_OFFSET_BLOCK;
                this.message = "Offset block missing x value.";
                return false;
            }

            if (offsetBlock.getY() == null) {
                this.status = ValidationStatus.INVALID_OFFSET_BLOCK;
                this.message = "Offset block missing y value.";
                return false;
            }

            if (offsetBlock.getZ() == null) {
                this.status = ValidationStatus.INVALID_OFFSET_BLOCK;
                this.message = "Offset block missing z value.";
                return false;
            }

            if (offsetBlock.getMaterials() == null) {
                this.status = ValidationStatus.INVALID_OFFSET_BLOCK;
                this.message = "Offset block missing materials value.";
                return false;
            }

            if (offsetBlock.getMaterials().isEmpty()) {
                this.status = ValidationStatus.INVALID_OFFSET_BLOCK;
                this.message = "Offset block has no materials specified.";
                return false;
            }

            for (Material material : offsetBlock.getMaterials()) {
                if (material == null) {
                    this.status = ValidationStatus.INVALID_OFFSET_BLOCK;
                    this.message = "Offset block has unknown material.";
                    return false;
                }
            }
        }

        if (bluePrint.getSignMaterials() == null) {
            this.status = ValidationStatus.MISSING_SIGN_MATERIALS;
            this.message = "Missing sign materials.";
            return false;
        }

        if (bluePrint.getSignMaterials().isEmpty()) {
            this.status = ValidationStatus.EMPTY_SIGN_MATERIALS;
            this.message = "No sign materials are specified.";
            return false;
        }

        for (Material material : bluePrint.getSignMaterials()) {
            if (material == null) {
                this.status = ValidationStatus.INVALID_SIGN_MATERIAL;
                this.message = "Offset block has unknown material.";
                return false;
            }
        }

        this.status = ValidationStatus.SUCCESS;
        this.message = "Blueprint successfully specified.";

        return true;
    }

    public ValidationStatus getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }
}
