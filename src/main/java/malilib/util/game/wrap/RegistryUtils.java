package malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import malilib.util.StringUtils;
import malilib.util.data.Identifier;
import malilib.util.game.BlockUtils;
import malilib.util.game.ItemUtils;

public class RegistryUtils
{
    @Nullable
    public static Block getBlockByIdStr(String name)
    {
        OptionalInt opt = StringUtils.tryParseToInt(name);

        if (opt.isPresent())
        {
            int id = opt.getAsInt();

            if (id >= 0 && id < Block.BY_ID.length)
            {
                return Block.BY_ID[id];
            }
        }

        return null;
    }

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

        for (Block block : Block.BY_ID)
        {
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

        for (Block block : Block.BY_ID)
        {
            if (block != null)
            {
                blocks.add(block);
            }
        }

        blocks.sort(Comparator.comparing(BlockUtils::getDisplayNameForBlock));

        return blocks;
    }

    @Nullable
    public static Item getItemByIdStr(String name)
    {
        OptionalInt opt = StringUtils.tryParseToInt(name);

        if (opt.isPresent())
        {
            int id = opt.getAsInt();

            if (id >= 0 && id < Item.BY_ID.length)
            {
                return Item.BY_ID[id];
            }
        }

        return null;
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
        return String.valueOf(item.id);
    }

    public static Collection<Identifier> getRegisteredItemIds()
    {
        ArrayList<Identifier> list = new ArrayList<>();

        for (Item item : Item.BY_ID)
        {
            if (item != null)
            {
                list.add(getItemId(item));
            }
        }

        return list;
    }

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>();

        for (Item item : Item.BY_ID)
        {
            if (item != null)
            {
                items.add(item);
            }
        }

        items.sort(Comparator.comparing(ItemUtils::getDisplayNameForItem));

        return items;
    }
}
