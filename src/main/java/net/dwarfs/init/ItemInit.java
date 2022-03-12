package net.dwarfs.init;

import net.dwarfs.item.BeerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemInit {

    public static final Item BEER_ITEM = register("beer", new BeerItem(new Item.Settings().group(ItemGroup.MISC)));
    public static final Item JUG_ITEM = register("jug", new Item(new Item.Settings().group(ItemGroup.MISC)));

    private static Item register(String id, Item item) {
        return register(new Identifier("dwarfs", id), item);
    }

    private static Item register(Identifier id, Item item) {
        return Registry.register(Registry.ITEM, id, item);
    }

    public static void init() {

    }

}
