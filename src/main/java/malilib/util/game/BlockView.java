package malilib.util.game;

import net.minecraft.block.Block;

import malilib.util.position.BlockPos;

public interface BlockView
{
    // TODO b1.7.3 - add a custom BlockState class that wraps the block and metadata?
    Block getBlockState(int x, int y, int z);

    Block getBlockState(BlockPos pos);
}
