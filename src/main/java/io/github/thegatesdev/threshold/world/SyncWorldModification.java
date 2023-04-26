package io.github.thegatesdev.threshold.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SyncWorldModification implements WorldModification {
    private final World world;
    private final ServerLevel serverLevel;

    private final List<ModifiedBlock> modifications = new ArrayList<>();

    SyncWorldModification(World world) {
        this.world = world;
        this.serverLevel = ((CraftWorld) world).getHandle();
    }

    @Override
    public void set(final int x, final int y, final int z, final BlockData blockData) {
        modifications.add(new ModifiedBlock(x, y, z, SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z), blockData));
    }

    @Override
    public int update() {
        final Set<LevelChunk> modifiedChunks = new HashSet<>(2, 0.4f);
        final var lightEngine = serverLevel.getChunkSource().getLightEngine();

        final BlockPos.MutableBlockPos currentBlockPos = new BlockPos.MutableBlockPos();

        int placedBlocks = 0;

        for (final ModifiedBlock modification : modifications) {
            currentBlockPos.set(modification.posX, modification.posY, modification.posZ);
            final LevelChunk blockChunk = serverLevel.getChunk(modification.cPosX, modification.cPosZ);
            modifiedChunks.add(blockChunk);
            // Set block
            blockChunk.setBlockState(currentBlockPos, ((CraftBlockData) modification.data).getState(), false);
            placedBlocks++;
            // Update lighting
            lightEngine.checkBlock(currentBlockPos);
        }

        final int view = Bukkit.getViewDistance();

        // Reload player chunks
        for (final LevelChunk chunk : modifiedChunks) {
            final ChunkPos pos = chunk.getPos();

            final var unloadPacket = new ClientboundForgetLevelChunkPacket(pos.x, pos.z);
            final var loadPacket = new ClientboundLevelChunkWithLightPacket(chunk, lightEngine, null, null, true, true);

            for (final Player player : world.getPlayers()) {
                final ServerPlayer handle = ((CraftPlayer) player).getHandle();
                final ChunkPos playerChunk = handle.chunkPosition();
                if (pos.x < playerChunk.x - view ||
                        pos.x > playerChunk.x + view ||
                        pos.z < playerChunk.z - view ||
                        pos.z > playerChunk.z + view
                ) continue;
                handle.connection.send(unloadPacket);
                handle.connection.send(loadPacket);
            }
        }

        modifications.clear();
        return placedBlocks;
    }


    private record ModifiedBlock(int posX, int posY, int posZ, int cPosX, int cPosZ, BlockData data) {
    }
}
