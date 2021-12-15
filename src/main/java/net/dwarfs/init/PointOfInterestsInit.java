package net.dwarfs.init;

import com.google.common.collect.ImmutableSet;

import net.dwarfs.entity.extra.DwarfProfession;
import net.dwarfs.entity.extra.DwarfTradeOffers;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterestsInit {

    // public static final DwarfPointOfInterestType UNEMPLOYED = DwarfPointOfInterestType.register("unemployed", ImmutableSet.of(), 1, IS_USED_BY_PROFESSION, 1);
    public static final PointOfInterestType NOTHING = PointOfInterestHelper.register(new Identifier("dwarfs", "nothing"), 1, 1, ImmutableSet.of());
    public static final PointOfInterestType BARTENDER = PointOfInterestHelper.register(new Identifier("dwarfs", "bartender"), 1, 1,
            ImmutableSet.copyOf(Blocks.DRAGON_EGG.getStateManager().getStates()));

    public static void init() {
        TrackedDataHandlerRegistry.register(DwarfProfession.DWARF_DATA);
    }
}
