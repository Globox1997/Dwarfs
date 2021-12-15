package net.dwarfs.entity.extra;

import com.google.common.collect.ImmutableSet;

import net.dwarfs.init.EntityInit;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class DwarfProfession {
    public static final DwarfProfession NONE = DwarfProfession.register("none", DwarfPointOfInterestType.UNEMPLOYED, null);
    public static final DwarfProfession ARMORER = DwarfProfession.register("armorer", DwarfPointOfInterestType.ARMORER, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
    public static final DwarfProfession BUTCHER = DwarfProfession.register("butcher", DwarfPointOfInterestType.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
    public static final DwarfProfession CARTOGRAPHER = DwarfProfession.register("cartographer", DwarfPointOfInterestType.CARTOGRAPHER, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
    public static final DwarfProfession CLERIC = DwarfProfession.register("cleric", DwarfPointOfInterestType.CLERIC, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
    public static final DwarfProfession FARMER = DwarfProfession.register("farmer", DwarfPointOfInterestType.FARMER,
            ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
    public static final DwarfProfession FISHERMAN = DwarfProfession.register("fisherman", DwarfPointOfInterestType.FISHERMAN, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
    public static final DwarfProfession FLETCHER = DwarfProfession.register("fletcher", DwarfPointOfInterestType.FLETCHER, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
    public static final DwarfProfession LEATHERWORKER = DwarfProfession.register("leatherworker", DwarfPointOfInterestType.LEATHERWORKER, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER);
    public static final DwarfProfession LIBRARIAN = DwarfProfession.register("librarian", DwarfPointOfInterestType.LIBRARIAN, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
    public static final DwarfProfession MASON = DwarfProfession.register("mason", DwarfPointOfInterestType.MASON, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
    public static final DwarfProfession NITWIT = DwarfProfession.register("nitwit", DwarfPointOfInterestType.NITWIT, null);
    public static final DwarfProfession SHEPHERD = DwarfProfession.register("shepherd", DwarfPointOfInterestType.SHEPHERD, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
    public static final DwarfProfession TOOLSMITH = DwarfProfession.register("toolsmith", DwarfPointOfInterestType.TOOLSMITH, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
    public static final DwarfProfession WEAPONSMITH = DwarfProfession.register("weaponsmith", DwarfPointOfInterestType.WEAPONSMITH, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);
    private final String id;
    private final DwarfPointOfInterestType workStation;
    private final ImmutableSet<Item> gatherableItems;
    private final ImmutableSet<Block> secondaryJobSites;
    @Nullable
    private final SoundEvent workSound;

    private DwarfProfession(String id, DwarfPointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        this.id = id;
        this.workStation = workStation;
        this.gatherableItems = gatherableItems;
        this.secondaryJobSites = secondaryJobSites;
        this.workSound = workSound;
    }

    public String getId() {
        return this.id;
    }

    public DwarfPointOfInterestType getWorkStation() {
        return this.workStation;
    }

    public ImmutableSet<Item> getGatherableItems() {
        return this.gatherableItems;
    }

    public ImmutableSet<Block> getSecondaryJobSites() {
        return this.secondaryJobSites;
    }

    @Nullable
    public SoundEvent getWorkSound() {
        return this.workSound;
    }

    public String toString() {
        return this.id;
    }

    static DwarfProfession register(String id, DwarfPointOfInterestType workStation, @Nullable SoundEvent workSound) {
        return DwarfProfession.register(id, workStation, ImmutableSet.of(), ImmutableSet.of(), workSound);
    }

    static DwarfProfession register(String id, DwarfPointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(EntityInit.DWARF_PROFESSION, new Identifier(id), new DwarfProfession(id, workStation, gatherableItems, secondaryJobSites, workSound));
    }
}
