package malilib.util.game.wrap;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

import malilib.mixin.access.NbtCompoundMixin;
import malilib.mixin.access.NbtDoubleMixin;
import malilib.mixin.access.NbtListMixin;
import malilib.util.data.Constants;

public class NbtWrap
{
    public static boolean containsByte(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_BYTE);
    }

    public static boolean containsShort(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_SHORT);
    }

    public static boolean containsInt(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_INT);
    }

    public static boolean containsLong(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LONG);
    }

    public static boolean containsFloat(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_FLOAT);
    }

    public static boolean containsDouble(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_DOUBLE);
    }

    public static boolean containsString(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_STRING);
    }

    public static boolean containsCompound(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_COMPOUND);
    }

    public static boolean containsList(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LIST);
    }

    public static boolean containsByteArray(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_BYTE_ARRAY);
    }

    public static boolean containsIntArray(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_INT_ARRAY);
    }

    public static boolean containsLongArray(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LONG_ARRAY);
    }

    public static boolean contains(NbtCompound tag, String name, int typeId)
    {
        NbtElement element = ((NbtCompoundMixin) (Object) tag).malilib_getTags().get(name);
        return element != null && element.getType() == typeId;
    }

    public static boolean hasUUID(NbtCompound tag)
    {
        return hasUUID(tag, "UUIDM", "UUIDL");
    }

    public static boolean hasUUID(NbtCompound tag, String keyM, String keyL)
    {
        return containsLong(tag, keyM) && containsLong(tag, keyL);
    }

    public static boolean getBoolean(NbtCompound tag, String name)
    {
        return tag.getBoolean(name);
    }

    public static byte getByte(NbtCompound tag, String name)
    {
        return tag.getByte(name);
    }

    public static short getShort(NbtCompound tag, String name)
    {
        return tag.getShort(name);
    }

    public static int getInt(NbtCompound tag, String name)
    {
        return tag.getInt(name);
    }

    public static long getLong(NbtCompound tag, String name)
    {
        return tag.getLong(name);
    }

    public static float getFloat(NbtCompound tag, String name)
    {
        return tag.getFloat(name);
    }

    public static double getDouble(NbtCompound tag, String name)
    {
        Map<String, NbtElement> tags = ((NbtCompoundMixin) (Object) tag).malilib_getTags();

        if (tags.containsKey(name) && tags.get(name).getType() == Constants.NBT.TAG_DOUBLE)
        {
            return ((NbtDoubleMixin) tags.get(name)).getValue();
        }

        return 0.0;
    }

    public static String getString(NbtCompound tag, String name)
    {
        return tag.getString(name);
    }

    public static NbtCompound getCompound(NbtCompound tag, String name)
    {
        return tag.getCompound(name);
    }

    public static NbtList getList(NbtCompound tag, String name, int type)
    {
        NbtList list = tag.getList(name);

        if (getListStoredType(list) == type)
        {
            return list;
        }

        list = new NbtList();
        list.setName(name);

        return list;
    }

    public static byte[] getByteArray(NbtCompound tag, String name)
    {
        return tag.getByteArray(name);
    }

    /*
    public static int[] getIntArray(NbtCompound tag, String name)
    {
        return tag.getIntArray(name);
    }
    */

    @Nullable
    public static NbtElement getTag(NbtCompound tag, String name)
    {
        return ((NbtCompoundMixin) (Object) tag).malilib_getTags().get(name);
    }

    public static boolean getBooleanOrDefault(NbtCompound tag, String name, boolean defaultValue)
    {
        return containsByte(tag, name) ? getByte(tag, name) != 0 : defaultValue;
    }

    public static byte getByteOrDefault(NbtCompound tag, String name, byte defaultValue)
    {
        return containsByte(tag, name) ? getByte(tag, name) : defaultValue;
    }

    public static short getShortOrDefault(NbtCompound tag, String name, short defaultValue)
    {
        return containsShort(tag, name) ? getShort(tag, name) : defaultValue;
    }

    public static int getIntOrDefault(NbtCompound tag, String name, int defaultValue)
    {
        return containsInt(tag, name) ? getInt(tag, name) : defaultValue;
    }

    public static long getLongOrDefault(NbtCompound tag, String name, long defaultValue)
    {
        return containsLong(tag, name) ? getLong(tag, name) : defaultValue;
    }

    public static float getFloatOrDefault(NbtCompound tag, String name, float defaultValue)
    {
        return containsFloat(tag, name) ? getFloat(tag, name) : defaultValue;
    }

    public static double getDoubleOrDefault(NbtCompound tag, String name, double defaultValue)
    {
        return containsDouble(tag, name) ? getDouble(tag, name) : defaultValue;
    }

    public static String getStringOrDefault(NbtCompound tag, String name, String defaultValue)
    {
        return containsString(tag, name) ? getString(tag, name) : defaultValue;
    }

    public static String getCommandFeedbackName(NbtElement tag)
    {
        return getName(getTypeId(tag));
    }

    public static byte getTypeId(NbtElement tag)
    {
        return tag.getType();
    }

    // TODO b1.7.3 the tags store their own name for some reason...
    public static NbtByte asByteTag(byte value)
    {
        return new NbtByte(value);
    }

    public static NbtShort asShortTag(short value)
    {
        return new NbtShort(value);
    }

    public static NbtInt asIntTag(int value)
    {
        return new NbtInt(value);
    }

    public static NbtLong asLongTag(long value)
    {
        return new NbtLong(value);
    }

    public static NbtFloat asFloatTag(float value)
    {
        return new NbtFloat(value);
    }

    public static NbtDouble asDoubleTag(double value)
    {
        NbtDouble tag = new NbtDouble();
        ((NbtDoubleMixin) (Object) tag).setValue(value);
        return tag;
    }

    public static NbtString asStringTag(String value)
    {
        return new NbtString(value);
    }

    public static void putBoolean(NbtCompound tag, String name, boolean value)
    {
        tag.putBoolean(name, value);
    }

    public static void putByte(NbtCompound tag, String name, byte value)
    {
        tag.putByte(name, value);
    }

    public static void putShort(NbtCompound tag, String name, short value)
    {
        tag.putShort(name, value);
    }

    public static void putInt(NbtCompound tag, String name, int value)
    {
        tag.putInt(name, value);
    }

    public static void putLong(NbtCompound tag, String name, long value)
    {
        tag.putLong(name, value);
    }

    public static void putFloat(NbtCompound tag, String name, float value)
    {
        tag.putFloat(name, value);
    }

    public static void putDouble(NbtCompound tag, String name, double value)
    {
        ((NbtCompoundMixin) (Object) tag).malilib_getTags().put(name, asDoubleTag(value));
    }

    public static void putString(NbtCompound tag, String name, String value)
    {
        tag.putString(name, value);
    }

    public static void putTag(NbtCompound tag, String name, NbtElement value)
    {
        tag.put(name, value);
    }

    public static void putByteArray(NbtCompound tag, String name, byte[] value)
    {
        tag.putByteArray(name, value);
    }

    /*
    public static void putIntArray(NbtCompound tag, String name, int[] value)
    {
        tag.setIntArray(name, value);
    }
    */

    public static void remove(NbtCompound tag, String name)
    {
        ((NbtCompoundMixin) (Object) tag).malilib_getTags().remove(name);
    }

    public static void addTag(NbtList listTag, NbtElement value)
    {
        listTag.add(value);
    }

    public static Set<String> getKeys(NbtCompound tag)
    {
        return ((NbtCompoundMixin) (Object) tag).malilib_getTags().keySet();
    }

    public static int getListSize(NbtList list)
    {
        return list.size();
    }

    public static int getListStoredType(NbtList listTag)
    {
        return ((NbtListMixin) (Object) listTag).malilib_getContainedType();
    }

    public static NbtList getListOfCompounds(NbtCompound tag, String name)
    {
        return getList(tag, name, Constants.NBT.TAG_COMPOUND);
    }

    public static double getDoubleAt(NbtList listTag, int index)
    {
        return ((NbtDoubleMixin) (Object) ((NbtDouble) listTag.get(index))).getValue();
    }

    public static int getIntAt(NbtList listTag, int index)
    {
        return ((NbtInt) listTag.get(index)).value;
    }

    public static NbtCompound getCompoundAt(NbtList listTag, int index)
    {
        return (NbtCompound) listTag.get(index);
    }

    /* TODO b1.7.3
    public static NbtCompound copy(NbtCompound tag)
    {
        return tag.m_1696745();//copy();
    }

    public static NBTTagList copy(NBTTagList tag)
    {
        return tag.m_4371252();//copy();
    }
    */

    public static String getName(byte type)
    {
        switch(type)
        {
            case 0:  return "TAG_End";
            case 1:  return "TAG_Byte";
            case 2:  return "TAG_Short";
            case 3:  return "TAG_Int";
            case 4:  return "TAG_Long";
            case 5:  return "TAG_Float";
            case 6:  return "TAG_Double";
            case 7:  return "TAG_Byte_Array";
            case 8:  return "TAG_String";
            case 9:  return "TAG_List";
            case 10: return "TAG_Compound";
            default: return "UNKNOWN";
        }
    }
}
