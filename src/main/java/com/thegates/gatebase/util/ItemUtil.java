package com.thegates.gatebase.util;

import org.bukkit.Bukkit;
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


    public static <T, Z> void modifyNbt(ItemStack itemStack, NamespacedKey key, PersistentDataType<T, Z> persistentDataType, Z value) {
        Optional<ItemMeta> meta = opMeta(itemStack, false);
        if (meta.isEmpty()) return;
        ItemMeta itemMeta = meta.get();
        itemMeta.getPersistentDataContainer().set(key, persistentDataType, value);
        itemStack.setItemMeta(itemMeta);
    }

    public static <T, Z> Optional<Z> getNbt(ItemStack itemStack, NamespacedKey key, PersistentDataType<T, Z> persistentDataType) {
        return getNbt(itemStack, key, persistentDataType, false);
    }

    public static <T, Z> Optional<Z> getNbt(ItemStack itemStack, NamespacedKey key, PersistentDataType<T, Z> persistentDataType, boolean fast) {
        Optional<ItemMeta> meta = opMeta(itemStack, fast);
        if (meta.isEmpty()) return Optional.empty();
        return Optional.ofNullable(meta.get().getPersistentDataContainer().get(key, persistentDataType));
    }

    public static <T, Z> boolean hasNbt(ItemStack itemStack, NamespacedKey key, PersistentDataType<T, Z> persistentDataType) {
        Optional<ItemMeta> meta = opMeta(itemStack, true);
        if (meta.isEmpty()) return false;
        return meta.get().getPersistentDataContainer().has(key, persistentDataType);
    }


    public static Optional<ItemMeta> opMeta(ItemStack itemStack, boolean fast) {
        if (itemStack == null) return Optional.empty();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            if (fast) return Optional.empty();
            meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }
        return Optional.ofNullable(meta);
    }

    public static void withMeta(ItemStack itemStack, Consumer<ItemMeta> consumer) {
        opMeta(itemStack, false).ifPresent(itemMeta -> {
            consumer.accept(itemMeta);
            itemStack.setItemMeta(itemMeta);
        });
    }

    public static <T> T withMeta(ItemStack itemStack, Function<ItemMeta, T> function) {
        final Optional<ItemMeta> itemMeta = opMeta(itemStack, false);
        return itemMeta.map(function).orElse(null);
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
