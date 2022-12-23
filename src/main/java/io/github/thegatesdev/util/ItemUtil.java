package io.github.thegatesdev.util;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utils for fast modification of ItemStacks
 */
public class ItemUtil {


    public static <T, Z> boolean modifyNbt(ItemStack itemStack, NamespacedKey key, PersistentDataType<T, Z> persistentDataType, Z value) {
        final ItemMeta meta = getMeta(itemStack);
        if (meta == null) return false;
        meta.getPersistentDataContainer().set(key, persistentDataType, value);
        itemStack.setItemMeta(meta);
        return true;
    }

    public static <T, Z> Z getNbt(ItemStack itemStack, NamespacedKey key, PersistentDataType<T, Z> persistentDataType) {
        final ItemMeta meta = getMeta(itemStack);
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(key, persistentDataType);
    }

    public static <T, Z> Optional<Z> opNbt(ItemStack itemStack, NamespacedKey key, PersistentDataType<T, Z> persistentDataType) {
        return Optional.ofNullable(getNbt(itemStack, key, persistentDataType));
    }

    public static <T, Z> boolean hasNbt(ItemStack itemStack, NamespacedKey key, PersistentDataType<T, Z> persistentDataType) {
        final ItemMeta meta = getMeta(itemStack);
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(key, persistentDataType);
    }


    public static Optional<ItemMeta> opMeta(ItemStack itemStack) {
        return Optional.ofNullable(getMeta(itemStack));
    }

    public static ItemMeta getMeta(ItemStack itemStack) {
        if (itemStack == null) return null;
        return itemStack.getItemMeta();
    }

    public static void withMeta(ItemStack itemStack, Consumer<ItemMeta> consumer) {
        final ItemMeta meta = getMeta(itemStack);
        if (meta != null) {
            consumer.accept(meta);
            itemStack.setItemMeta(meta);
        }
    }

    public static <T> T withMeta(ItemStack itemStack, Function<ItemMeta, T> function) {
        final ItemMeta meta = getMeta(itemStack);
        final T t = function.apply(meta);
        itemStack.setItemMeta(meta);
        return t;
    }

    public static void addLore(ItemMeta itemMeta, List<String> list) {
        final List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>(list.size());
        assert lore != null;
        lore.addAll(list);
        itemMeta.setLore(lore);
    }

    public static void addLore(ItemMeta itemMeta, String line) {
        final List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        assert lore != null;
        lore.add(line);
        itemMeta.setLore(lore);
    }


    public static AttributeModifier fastModifier(Attribute attribute, double amount, EquipmentSlot equipmentSlot) {
        return new AttributeModifier(UUID.nameUUIDFromBytes((attribute.name() + amount).getBytes()), (attribute.name() + amount), amount, AttributeModifier.Operation.ADD_NUMBER, equipmentSlot);
    }
}
