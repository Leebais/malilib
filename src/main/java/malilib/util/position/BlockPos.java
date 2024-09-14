package malilib.util.position;

import malilib.util.MathUtils;

public class BlockPos extends Vec3i
{
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    public BlockPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public BlockPos(Vec3i pos)
    {
        super(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos offset(Direction direction, int amount)
    {
        return new BlockPos(this.getX() + direction.getXOffset() * amount,
                            this.getY() + direction.getYOffset() * amount,
                            this.getZ() + direction.getZOffset() * amount);
    }

    public BlockPos offset(Direction direction)
    {
        return this.offset(direction, 1);
    }

    public BlockPos add(int x, int y, int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos subtract(int x, int y, int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() - x, this.getY() - y, this.getZ() - z);
    }

    public BlockPos add(Vec3i other)
    {
        return this.add(other.getX(), other.getY(), other.getZ());
    }

    public BlockPos subtract(Vec3i other)
    {
        return this.subtract(other.getX(), other.getY(), other.getZ());
    }

    public BlockPos toImmutable()
    {
        return this;
    }

    /*
    public net.minecraft.util.math.BlockPos toVanillaPos()
    {
        return this;
    }
    */

    public BlockPos down()
    {
        return new BlockPos(this.getX(), this.getY() - 1, this.getZ());
    }

    public BlockPos up()
    {
        return new BlockPos(this.getX(), this.getY() + 1, this.getZ());
    }

    public BlockPos north()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ() - 1);
    }

    public BlockPos south()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ() + 1);
    }

    public BlockPos west()
    {
        return new BlockPos(this.getX() - 1, this.getY(), this.getZ());
    }

    public BlockPos east()
    {
        return new BlockPos(this.getX() + 1, this.getY(), this.getZ());
    }

    public long toPackedLong()
    {
        int x = this.getX() & 0x3FFFFFF;
        int y = this.getY() & 0xFFF;
        int z = this.getZ() & 0x3FFFFFF;

        return ((long) x << (26 + 12)) | ((long) y << 26) | ((long) z);
    }

    @Override
    public String toString()
    {
        return "BlockPos{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }

    public static BlockPos fromPacked(long posLong)
    {
        int x = (int) ((posLong & (0x3FFFFFFL << (26L + 12L)) >> (26L + 12L)));
        int y = (int) ((posLong & (0xFFFL << 26)) >> 26);
        int z = (int) (posLong & 0x3FFFFFFL);

        return new BlockPos(x, y, z);
    }

    public static BlockPos ofFloored(Vec3d pos)
    {
        return ofFloored(pos.x, pos.y, pos.z);
    }

    public static BlockPos ofFloored(double x, double y, double z)
    {
        return new BlockPos(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(z));
    }

    /*
    @Nullable
    public static BlockPos of(@Nullable net.minecraft.util.math.BlockPos pos)
    {
        if (pos == null)
        {
            return null;
        }

        return new BlockPos(pos.x, pos.y, pos.z);
    }
    */

    public static class MutBlockPos extends BlockPos
    {
        private int x;
        private int y;
        private int z;

        public MutBlockPos()
        {
            this(0, 0, 0);
        }

        public MutBlockPos(int x, int y, int z)
        {
            super(x, y, z);
        }

        public MutBlockPos(Vec3i pos)
        {
            super(pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public int getX()
        {
            return this.x;
        }

        @Override
        public int getY()
        {
            return this.y;
        }

        @Override
        public int getZ()
        {
            return this.z;
        }

        public void setX(int x)
        {
            this.x = x;
        }

        public void setY(int y)
        {
            this.y = y;
        }

        public void setZ(int z)
        {
            this.z = z;
        }

        public MutBlockPos set(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;

            return this;
        }

        public MutBlockPos set(Vec3i pos)
        {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            return this;
        }

        /*
        public MutBlockPos setOffset(net.minecraft.util.math.Vec3i pos, EnumFacing direction)
        {
            this.x = pos.getX() + direction.getXOffset();
            this.y = pos.getY() + direction.getYOffset();
            this.z = pos.getZ() + direction.getZOffset();
            return this;
        }
        */

        public MutBlockPos setOffset(Vec3i pos, Direction direction)
        {
            return this.setOffset(pos, direction, 1);
        }

        public MutBlockPos setOffset(Vec3i pos, Direction direction, int amount)
        {
            this.x = pos.getX() + direction.getXOffset() * amount;
            this.y = pos.getY() + direction.getYOffset() * amount;
            this.z = pos.getZ() + direction.getZOffset() * amount;
            return this;
        }

        public MutBlockPos move(Direction direction, int amount)
        {
            this.set(this.getX() + direction.getXOffset() * amount,
                     this.getY() + direction.getYOffset() * amount,
                     this.getZ() + direction.getZOffset() * amount);

            return this;
        }

        public MutBlockPos move(Direction direction)
        {
            return this.move(direction, 1);
        }

        @Override
        public BlockPos toImmutable()
        {
            return new BlockPos(this.x, this.y, this.z);
        }
    }
}
