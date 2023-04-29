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


    default void fill(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, BlockData blockData) {
        final int minZ = Math.min(fromZ, toZ);
        final int maxZ = Math.max(fromZ, toZ);
        final int minY = Math.min(fromY, toY);
        final int maxY = Math.max(fromY, toY);
        for (int x = Math.min(fromX, toX), xMax = Math.max(fromX, toX); x < xMax; x++)
            for (int z = minZ; z < maxZ; z++)
                for (int y = minY; y < maxY; y++) set(x, y, z, blockData);
    }

    default void fill(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, Material material) {
        fill(fromX, fromY, fromZ, toX, toY, toZ, material.createBlockData());
    }


    int update();
}
