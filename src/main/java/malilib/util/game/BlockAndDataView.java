package malilib.util.game;

import javax.annotation.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

import malilib.util.position.BlockPos;

public interface BlockAndDataView extends BlockView
{
    @Nullable
    BlockEntity getBlockEntity(int x, int y, int z);

    @Nullable
    BlockEntity getBlockEntity(BlockPos pos);

    @Nullable
    NbtCompound getBlockDataTag(int x, int y, int z);

    @Nullable
    NbtCompound getBlockDataTag(BlockPos pos);
}
