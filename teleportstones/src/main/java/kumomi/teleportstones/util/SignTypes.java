package kumomi.teleportstones.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Material;

public class SignTypes {

    public static final Collection<Material> signTypes;

    static {
        signTypes = new HashSet<>();

        signTypes.addAll(Arrays.asList( //
                Material.OAK_SIGN, //
                Material.BIRCH_SIGN, //
                Material.ACACIA_SIGN, //
                Material.SPRUCE_SIGN, //
                Material.JUNGLE_SIGN, //
                Material.SPRUCE_SIGN, //
                Material.WARPED_SIGN, //
                Material.CRIMSON_SIGN, //
                Material.DARK_OAK_SIGN, //
                Material.OAK_WALL_SIGN, //
                Material.BIRCH_WALL_SIGN, //
                Material.ACACIA_WALL_SIGN, //
                Material.JUNGLE_WALL_SIGN, //
                Material.SPRUCE_WALL_SIGN, //
                Material.CRIMSON_WALL_SIGN, //
                Material.DARK_OAK_WALL_SIGN //
        ));
    }
}
