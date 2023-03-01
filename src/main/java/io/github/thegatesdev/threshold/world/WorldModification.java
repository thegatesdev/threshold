package io.github.thegatesdev.threshold.world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public interface WorldModification {

    static WorldModification sync(World world) {
        return new SyncWorldModification(world);
    }

    void set(int x, int y, int z, BlockData blockData);

    default void set(int x, int y, int z, Material material) {
        set(x, y, z, material.createBlockData());
    }


    int update();
}
