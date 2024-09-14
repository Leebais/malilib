package malilib.render.inventory;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import malilib.config.value.HorizontalAlignment;
import malilib.config.value.VerticalAlignment;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.icon.PositionedIcon;
import malilib.gui.util.GuiUtils;
import malilib.render.ItemRenderUtils;
import malilib.render.RenderContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.MathUtils;
import malilib.util.game.RayTraceUtils;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.ItemWrap;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.game.wrap.WorldWrap;
import malilib.util.inventory.ColoredVanillaInventoryView;
import malilib.util.inventory.InventoryView;
import malilib.util.inventory.VanillaInventoryView;
import malilib.util.position.BlockPos;
import malilib.util.position.HitResult;
import malilib.util.position.Vec2i;

public class InventoryRenderUtils
{
    /**
     * Renders all the slots from the given inventory that exist in the given customSlotPositions map,
     * at their indicated offsets from the base xy-coordinate.
     */
    public static void renderCustomPositionedSlots(int x, int y, float z, InventoryView inv,
                                                   Int2ObjectOpenHashMap<Vec2i> customSlotPositions,
                                                   RenderContext ctx)
    {
        final int invSize = inv.getSize();

        RenderWrap.enableGuiItemLighting(ctx);
        RenderWrap.enableDepthTest();
        RenderWrap.enableRescaleNormal();

        for (int slot : customSlotPositions.keySet())
        {
            if (slot >= 0 && slot < invSize)
            {
                ItemStack stack = inv.getStack(slot);
                Vec2i pos = customSlotPositions.get(slot);

                if (ItemWrap.notEmpty(stack) && pos != null)
                {
                    ItemRenderUtils.renderStackAt(stack, x + pos.x, y + pos.y, z, 1f, ctx);
                }
            }
        }
    }

    /**
     * Renders the given inventory slot ranges from the given inventory,
     * at their relative offsets from the base xy-coordinate.
     */
    public static void renderInventoryRanges(int x, int y, float z, InventoryView inv,
                                             List<InventoryRange> inventoryRanges,
                                             int backgroundTintColor,
                                             RenderContext ctx)
    {
        for (InventoryRange range : inventoryRanges)
        {
            int slotsPerRow = range.slotsPerRowFunction.applyAsInt(inv.getSize());
            int slotCount = range.slotCount;
            Vec2i startPos = range.startPos;

            if (slotCount < 0)
            {
                slotCount = inv.getSize() - range.startSlot;
            }

            if (range.renderSlotBackgrounds && slotsPerRow > 0 && slotCount > 0)
            {
                int tx = x + startPos.x - 1;
                int ty = y + startPos.y - 1;

                renderDynamicInventoryEmptySlotBackgrounds(tx, ty, z, backgroundTintColor, slotsPerRow, slotCount, ctx);
            }

            renderGenericInventoryItems(x, y, z + 100f, range.startSlot, slotCount, slotsPerRow, startPos, inv, ctx);
        }
    }

    /**
     * Renders a dynamically sized inventory background texture for the given
     * inventory (slot count), with the given number of slots per row.
     * <br> Note that the maximum size is currently capped to 13x13 slots,
     * due to that being the maximum size of the original background image used.
     */
    public static void renderDynamicInventoryBackground(int x, int y, float z, int backgroundTintColor,
                                                        int slotsPerRow, int slotCount, RenderContext ctx)
    {
        if (slotCount <= 0 || slotsPerRow <= 0)
        {
            return;
        }

        slotsPerRow = Math.min(slotsPerRow, slotCount);
        slotsPerRow = Math.min(slotsPerRow, 13);

        Icon icon = DefaultIcons.INV_BACKGROUND_EMPTY_13_X_13;
        int rows = Math.min((int) Math.ceil((double) slotCount / slotsPerRow), 13);
        int w1 = slotsPerRow * 18 + 7;
        int h1 = rows * 18 + 7;
        int w2 = 7;
        int h2 = 7;

        int width = icon.getWidth();
        int height = icon.getHeight();
        int u = icon.getU();
        int v = icon.getV();
        float pw = icon.getTexturePixelWidth();
        float ph = icon.getTexturePixelHeight();

        RenderWrap.color(1f, 1f, 1f, 1f);
        RenderWrap.setupBlendSeparate();
        RenderWrap.bindTexture(icon.getTexture());

        VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();
        // Main part (top left) with all the slots
        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x, y, z, u, v, w1, h1,
                                                             w1, h1, pw, ph, backgroundTintColor, builder);

        // The right edge strip
        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x + w1, y, z, u + width - w2, v,
                                                             w2, h1, w2, h1, pw, ph, backgroundTintColor, builder);

        // The bottom edge strip
        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x, y + h1, z, u, v + height - h2,
                                                             w1, h2, w1, h2, pw, ph, backgroundTintColor, builder);

        // The bottom right corner piece
        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x + w1, y + h1, z, u + width - w2, v + height - h2,
                                                             w2, h2, w2, h2, pw, ph, backgroundTintColor, builder);

        builder.draw();

        renderDynamicInventoryEmptySlotBackgrounds(x + 7, y + 7, z, backgroundTintColor, slotsPerRow, slotCount, ctx);
    }

    public static void renderDynamicInventoryEmptySlotBackgrounds(int x, int y, float z, int backgroundTintColor,
                                                                  int slotsPerRow, int slotCount, RenderContext ctx)
    {
        if (slotCount <= 0 || slotsPerRow <= 0)
        {
            return;
        }

        int fullWidthRows = Math.min(slotCount / slotsPerRow, 14);
        int totalRows = Math.min((int) Math.ceil((double) slotCount / slotsPerRow), 14);
        int lastRowSlots = slotCount % slotsPerRow;
        int xInc = 0;
        int yInc = 0;
        int w = 18;
        int h = 18;
        int loopCount;
        int color = backgroundTintColor;
        Icon icon;

        // More rows than slots per row -> use vertical columns instead of horizontal rows
        if (totalRows > slotsPerRow)
        {
            icon = DefaultIcons.INV_BACKGROUND_14_SLOTS_VERTICAL;
            loopCount = slotsPerRow;
            // The height of the columns to render is the number of full width rows,
            // ie. the continuous part of the inventory before the possible last partial row
            h = fullWidthRows * 18;
            xInc = 18;
        }
        else
        {
            icon = DefaultIcons.INV_BACKGROUND_14_SLOTS_HORIZONTAL;
            loopCount = fullWidthRows;
            w = slotsPerRow * 18;
            yInc = 18;
        }

        int tx = x;
        int ty = y;
        int u = icon.getU();
        int v = icon.getV();
        float pw = icon.getTexturePixelWidth();
        float ph = icon.getTexturePixelHeight();

        RenderWrap.color(1f, 1f, 1f, 1f);
        RenderWrap.setupBlendSeparate();
        RenderWrap.disableItemLighting();
        RenderWrap.bindTexture(icon.getTexture());

        VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();

        for (int i = 0; i < loopCount; ++i)
        {
            ShapeRenderUtils.renderScaledTintedTexturedRectangle(tx, ty, z, u, v, w, h, w, h, pw, ph, color, builder);
            tx += xInc;
            ty += yInc;
        }

        builder.draw();

        // There is one partial row at the bottom
        if (lastRowSlots > 0)
        {
            icon = DefaultIcons.INV_BACKGROUND_14_SLOTS_HORIZONTAL;
            ty = y + fullWidthRows * 18;
            w = lastRowSlots * 18;
            h = 18;
            u = icon.getU();
            v = icon.getV();
            pw = icon.getTexturePixelWidth();
            ph = icon.getTexturePixelHeight();

            RenderWrap.bindTexture(icon.getTexture());
            ShapeRenderUtils.renderScaledTintedTexturedRectangle(x, ty, z, u, v, w, h, w, h, pw, ph, color, ctx);
        }
    }

    /**
     * Renders an empty slot background image for the empty slots in the given inventory,
     * that exist in the give map of icons. The map index is the slot number.
     */
    public static void renderEmptySlotBackgrounds(int x, int y, float z, int backgroundTintColor, InventoryView inv,
                                                  Int2ObjectOpenHashMap<PositionedIcon> emptySlotTextures,
                                                  RenderContext ctx)
    {
        final int invSize = inv.getSize();

        for (Map.Entry<Integer, PositionedIcon> entry : emptySlotTextures.int2ObjectEntrySet())
        {
            int slotNum = entry.getKey();

            if (slotNum >= 0 && slotNum < invSize &&
                ItemWrap.isEmpty(inv.getStack(slotNum)))
            {
                PositionedIcon posIcon = entry.getValue();
                Vec2i position = posIcon.pos;
                Icon icon = posIcon.icon;
                int posX = x + position.x;
                int posY = y + position.y;

                if (backgroundTintColor == 0xFFFFFFFF)
                {
                    icon.renderAt(posX, posY, z, ctx);
                }
                else
                {
                    icon.renderTintedAt(posX, posY, z, backgroundTintColor, ctx);
                }
            }
        }
    }

    /**
     * Renders the items for a generic row-based inventory.
     * @param startSlot the slot number to start from
     * @param maxSlotCount maximum number of slots to render, starting from the startSlot.
     *                     -1 can be used for "all slots".
     *                     It's safe to give a number larger than the inventory size,
     *                     it will be clamped automatically.
     * @param slotsPerRow the number of slots to render per row
     * @param slotOffset the start offset of the first slot from the given base x and y coordinate.
     *                   Note that if startSlot is not 0, this is the offset of the first rendered slot,
     *                   not the would-be slot 0.
     * @param inv the inventory from which the slots are rendered
     */
    public static void renderGenericInventoryItems(int x, int y, float z, int startSlot, int maxSlotCount,
                                                   int slotsPerRow, Vec2i slotOffset, InventoryView inv,
                                                   RenderContext ctx)
    {
        final int invSize = inv.getSize();

        if (maxSlotCount < 0)
        {
            maxSlotCount = invSize;
        }

        maxSlotCount = Math.min(maxSlotCount, invSize - startSlot);

        if (startSlot < 0 || slotsPerRow <= 0 || maxSlotCount <= 0)
        {
            return;
        }

        final int endSlot = startSlot + maxSlotCount;
        final int startX = x + slotOffset.x;
        int slot = startSlot;

        x = startX;
        y += slotOffset.y;

        RenderWrap.enableGuiItemLighting(ctx);
        RenderWrap.enableDepthTest();
        RenderWrap.enableRescaleNormal();

        for (int slotOnRow = 0; slot < endSlot; ++slot)
        {
            ItemStack stack = inv.getStack(slot);

            if (ItemWrap.notEmpty(stack))
            {
                ItemRenderUtils.renderStackAt(stack, x, y, z, 1f, ctx);
            }

            if (++slotOnRow >= slotsPerRow)
            {
                x = startX;
                y += 18;
                slotOnRow = 0;
            }
            else
            {
                x += 18;
            }
        }
    }

    public static void renderInventoryPreview(InventoryView inv,
                                              InventoryRenderDefinition renderDefinition,
                                              int baseX, int baseY, float z, int bgTintColor,
                                              HorizontalAlignment horizontalAlignment,
                                              VerticalAlignment verticalAlignment,
                                              RenderContext ctx)
    {
        int screenWidth = GuiUtils.getScaledWindowWidth();
        int screenHeight = GuiUtils.getScaledWindowHeight();
        int width = renderDefinition.getRenderWidth(inv);
        int height = renderDefinition.getRenderHeight(inv) + 8;
        int x = baseX + horizontalAlignment.getXStartOffsetForEdgeAlignment(width);
        int y = baseY + verticalAlignment.getYStartOffsetForEdgeAlignment(height);

        x = MathUtils.clamp(x, 0, screenWidth - width);
        y = MathUtils.clamp(y, 0, screenHeight - height);

        if (bgTintColor == 0xFFFFFFFF && inv instanceof ColoredVanillaInventoryView)
        {
            bgTintColor = ((ColoredVanillaInventoryView) inv).getBackgroundTintColor();
        }

        renderDefinition.renderInventory(x, y, z, bgTintColor, inv, ctx);
    }

    public static InventoryRenderDefinition getInventoryType(Inventory inv)
    {
        /*
        if (inv instanceof TileEntityShulkerBox)
        {
            return BuiltinInventoryRenderDefinitions.GENERIC_27;
        }
        */
        if (inv instanceof ChestBlockEntity)
        {
            return BuiltinInventoryRenderDefinitions.GENERIC_27;
        }
        /*
        else if (inv instanceof InventoryLargeChest)
        {
            return BuiltinInventoryRenderDefinitions.GENERIC_54;
        }
        */
        else if (inv instanceof FurnaceBlockEntity)
        {
            return BuiltinInventoryRenderDefinitions.FURNACE;
        }
        /*
        else if (inv instanceof TileEntityBrewingStand)
        {
            return BuiltinInventoryRenderDefinitions.BREWING_STAND;
        }
        else if (inv instanceof DispenserBlockEntity) // this includes the Dropper as a sub class
        {
            return BuiltinInventoryRenderDefinitions.DROPPER;
        }
        else if (inv instanceof TileEntityHopper)
        {
            return BuiltinInventoryRenderDefinitions.HOPPER;
        }
        else if (inv instanceof ContainerHorseChest)
        {
            return BuiltinInventoryRenderDefinitions.HORSE;
        }
        */
        else
        {
            return BuiltinInventoryRenderDefinitions.GENERIC;
        }
    }

    /*
    public static InventoryRenderDefinition getInventoryType(ItemStack stack)
    {
        Item item = stack.getItem();

        if (item instanceof ItemBlock)
        {
            Block block = ((ItemBlock) item).getBlock();

            if (block instanceof BlockShulkerBox || block instanceof BlockChest)
            {
                return BuiltinInventoryRenderDefinitions.GENERIC_27;
            }
            else if (block instanceof BlockFurnace)
            {
                return BuiltinInventoryRenderDefinitions.FURNACE;
            }
            else if (block instanceof BlockDispenser) // this includes the Dropper as a sub class
            {
                return BuiltinInventoryRenderDefinitions.DROPPER;
            }
            else if (block instanceof BlockHopper)
            {
                return BuiltinInventoryRenderDefinitions.HOPPER;
            }
        }
        else if (item == Items.BREWING_STAND)
        {
            return BuiltinInventoryRenderDefinitions.BREWING_STAND;
        }

        return BuiltinInventoryRenderDefinitions.GENERIC;
    }
    */

    @Nullable
    public static Pair<InventoryView, InventoryRenderDefinition> getPointedInventory()
    {
        World world = WorldWrap.getBestWorld();
        PlayerEntity player = GameWrap.getClientPlayer();
        RayTraceUtils.RayTraceFluidHandling fluidHandling = RayTraceUtils.RayTraceFluidHandling.NONE;
        HitResult trace = RayTraceUtils.getRayTraceFromEntity(world, player, fluidHandling, true, 6.0);

        if (trace == null)
        {
            return null;
        }

        if (trace.type == HitResult.Type.BLOCK)
        {
            return getInventoryViewFromBlock(world, trace.blockPos);
        }
        else if (trace.type == HitResult.Type.ENTITY)
        {
            return getInventoryViewFromEntity(trace.entity);
        }

        return null;
    }

    @Nullable
    public static Pair<InventoryView, InventoryRenderDefinition> getInventoryViewFromBlock(World world, BlockPos pos)
    {
        BlockEntity te = world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ());

        if (te instanceof Inventory)
        {
            Inventory inv = (Inventory) te;
            /*
            IBlockState state = world.getBlockState(pos);

            // Prevent loot generation attempt from crashing due to NPEs
            // TODO 1.13+ check if this is still needed
            if (te instanceof TileEntityLockableLoot && (world instanceof WorldServer) == false)
            {
                ((TileEntityLockableLoot) te).setLootTable(null, 0);
            }

            if (state.getBlock() instanceof BlockChest)
            {
                ILockableContainer cont = ((BlockChest) state.getBlock()).getLockableContainer(world, pos);

                if (cont instanceof InventoryLargeChest)
                {
                    inv = cont;
                }
            }

            Block block = world.getBlockState(pos).getBlock();

            if (block instanceof BlockShulkerBox)
            {
                BlockShulkerBox shulkerBoxBlock = (BlockShulkerBox) block;
                int bgColor = InventoryRenderUtils.getShulkerBoxBackgroundTintColor(shulkerBoxBlock);
                return Pair.of(new ColoredVanillaInventoryView(inv, bgColor), getInventoryType(inv));
            }
            */

            return Pair.of(new VanillaInventoryView(inv), getInventoryType(inv));
        }

        return null;
    }

    @Nullable
    public static Pair<InventoryView, InventoryRenderDefinition> getInventoryViewFromEntity(Entity entity)
    {
        /* TODO b1.7.3
        if (entity instanceof EntityVillager)
        {
            IInventory inv = ((EntityVillager) entity).getVillagerInventory();
            InventoryView equipmentInv = new EquipmentInventoryView((EntityVillager) entity);
            InventoryView mainInventory = new VanillaInventoryView(inv);

            return Pair.of(new CombinedInventoryView(equipmentInv, mainInventory),
                           BuiltinInventoryRenderDefinitions.VILLAGER);
        }
        else if (entity instanceof AbstractHorse)
        {
            IInventory inv = ((AbstractHorseMixin) entity).malilib_getHorseChest();
            InventoryView equipmentInv = new EquipmentInventoryView((AbstractHorse) entity);
            InventoryView mainInventory = new VanillaInventoryView(inv);
            InventoryRenderDefinition def = (entity instanceof EntityLlama) ?
                                                    BuiltinInventoryRenderDefinitions.LLAMA :
                                                    BuiltinInventoryRenderDefinitions.HORSE;

            return Pair.of(new CombinedInventoryView(equipmentInv, mainInventory), def);
        }
        else if (entity instanceof IInventory)
        {
            return Pair.of(new VanillaInventoryView((IInventory) entity),
                           BuiltinInventoryRenderDefinitions.GENERIC);
        }
        else if (entity instanceof EntityLivingBase)
        {
            return Pair.of(new EquipmentInventoryView((EntityLivingBase) entity),
                           BuiltinInventoryRenderDefinitions.LIVING_ENTITY);
        }
        */

        return null;
    }
}
