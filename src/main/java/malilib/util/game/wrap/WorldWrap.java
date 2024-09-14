package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WorldWrap
{
    public static int getDimensionId(World world)
    {
        return world.provider.getDimensionType().getId();
    }

    public static String getDimensionIdAsString(World world)
    {
        return String.valueOf(world.provider.getDimensionType().getId());
    }

    public static long getTotalTick(World world)
    {
        return world.getTotalWorldTime();
    }

    public static long getDayTick(World world)
    {
        return world.getWorldTime();
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world (or null if not in world).
     */
    @Nullable
    public static World getBestWorld()
    {
        World world = GameWrap.getClientWorld();

        if (GameWrap.isSinglePlayer() && world != null)
        {
            MinecraftServer server = GameWrap.getIntegratedServer();
            return server.getWorld(getDimensionId(world));
        }
        else
        {
            return world;
        }
    }

    @Nullable
    public static WorldServer getServerWorldForClientWorld()
    {
        World world = GameWrap.getClientWorld();
        return world != null ? getServerWorldForClientWorld(world) : null;
    }

    @Nullable
    public static WorldServer getServerWorldForClientWorld(World world)
    {
        MinecraftServer server = GameWrap.getIntegratedServer();
        return server != null ? server.getWorld(getDimensionId(world)) : null;
    }

    public static boolean isClientChunkLoaded(int chunkX, int chunkZ, WorldClient world)
    {
        return world.getChunkProvider().isChunkGeneratedAt(chunkX, chunkZ);
    }

    public static void loadClientChunk(int chunkX, int chunkZ, WorldClient world)
    {
        world.m_5061960()/*getChunkProvider()*/.loadChunk(chunkX, chunkZ);
    }

    public static void unloadClientChunk(int chunkX, int chunkZ, WorldClient world)
    {
        world.m_5061960()/*getChunkProvider()*/.unloadChunk(chunkX, chunkZ);
    }
}
