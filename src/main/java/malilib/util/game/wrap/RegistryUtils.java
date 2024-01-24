package malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import malilib.util.data.Identifier;

public class RegistryUtils
{
    @Nullable
    public static Block getBlockByIdStr(String name)
    {
        try
        {
            return getBlockById(Integer.parseInt(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Nullable
    public static Block getBlockById(int id)
    {
        return id >= 0 && id < Block.BY_ID.length ? Block.BY_ID[id] : null;
    }

    @Nullable
    public static Identifier getBlockId(Block block)
    {
        return new Identifier(String.valueOf(block.id));
    }

    /*
    @Nullable
    public static ResourceLocation getBlockId(IBlockState state)
    {
        return getBlockId(state.getBlock());
    }
    */

    public static String getBlockIdStr(Block block)
    {
        return "minecraft:" + block.id;
    }

    /*
    public static String getBlockIdStr(IBlockState state)
    {
        return getBlockIdStr(state.getBlock());
    }
    */

    public static Collection<Identifier> getRegisteredBlockIds()
    {
        ArrayList<Identifier> list = new ArrayList<>();

        for (int i = 0; i < Block.BY_ID.length; ++i)
        {
            Block block = Block.BY_ID[i];

            if (block != null)
            {
                list.add(getBlockId(block));
            }
        }

        return list;
    }

    public static List<Block> getSortedBlockList()
    {
        List<Block> blocks = new ArrayList<>();

        for (int i = 0; i < Block.BY_ID.length; ++i)
        {
            Block block = Block.BY_ID[i];

            if (block != null)
            {
                blocks.add(block);
            }
        }

        blocks.sort(Comparator.comparing(RegistryUtils::getBlockIdStr));

        return blocks;
    }

    @Nullable
    public static Item getItemByIdStr(String name)
    {
        try
        {
            return getItemById(Integer.parseInt(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Nullable
    public static Item getItemById(int id)
    {
        return id >= 0 && id < Item.BY_ID.length ? Item.BY_ID[id] : null;
    }

    @Nullable
    public static Identifier getItemId(Item item)
    {
        return new Identifier(String.valueOf(item.id));
    }

    public static String getItemIdStr(Item item)
    {
        return "minecraft:" + item.id;
    }

    public static Collection<Identifier> getRegisteredItemIds()
    {
        ArrayList<Identifier> list = new ArrayList<>();

        for (int i = 0; i < Item.BY_ID.length; ++i)
        {
            Item item = Item.BY_ID[i];

            if (item != null)
            {
                list.add(getItemId(item));
            }
        }

        return list;
    }

    public static List<Item> getSortedItemList()
    {
        List<Item> blocks = new ArrayList<>();

        for (int i = 0; i < Item.BY_ID.length; ++i)
        {
            Item item = Item.BY_ID[i];

            if (item != null)
            {
                blocks.add(item);
            }
        }

        blocks.sort(Comparator.comparing(RegistryUtils::getItemIdStr));

        return blocks;
    }
}
