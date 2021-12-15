// package net.dwarfs.entity.extra;

// import com.google.common.base.Suppliers;
// import com.google.common.collect.ImmutableList;
// import com.google.common.collect.ImmutableSet;
// import com.google.common.collect.Maps;
// import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
// import java.util.Map;
// import java.util.Optional;
// import java.util.Set;
// import java.util.function.Predicate;
// import java.util.function.Supplier;
// import java.util.stream.Collectors;

// import net.dwarfs.init.EntityInit;
// import net.minecraft.block.BedBlock;
// import net.minecraft.block.Block;
// import net.minecraft.block.BlockState;
// import net.minecraft.block.Blocks;
// import net.minecraft.block.enums.BedPart;
// import net.minecraft.util.Identifier;
// import net.minecraft.util.Util;
// import net.minecraft.util.registry.Registry;
// import net.minecraft.village.VillagerProfession;
// import net.minecraft.world.poi.PointOfInterestType;

// public class DwarfPointOfInterestType {

// private static final Supplier<Set<DwarfPointOfInterestType>> VILLAGER_WORKSTATIONS = Suppliers
// .memoize(() -> EntityInit.DWARF_PROFESSION.stream().map(DwarfProfession::getWorkStation).collect(Collectors.toSet()));
// public static final Predicate<DwarfPointOfInterestType> IS_USED_BY_PROFESSION = poiType -> VILLAGER_WORKSTATIONS.get().contains(poiType);
// public static final Predicate<DwarfPointOfInterestType> ALWAYS_TRUE = poiType -> true;
// private static final Set<BlockState> BED_STATES = ImmutableList
// .of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED,
// Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[] { Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED })
// .stream().flatMap(block -> block.getStateManager().getStates().stream()).filter(state -> state.get(BedBlock.PART) == BedPart.HEAD).collect(ImmutableSet.toImmutableSet());
// private static final Set<BlockState> CAULDRON_STATES = ImmutableList.of(Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON).stream()
// .flatMap(block -> block.getStateManager().getStates().stream()).collect(ImmutableSet.toImmutableSet());
// private static final Map<BlockState, DwarfPointOfInterestType> BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE = Maps.newHashMap();
// public static final DwarfPointOfInterestType UNEMPLOYED = DwarfPointOfInterestType.register("unemployed", ImmutableSet.of(), 1, IS_USED_BY_PROFESSION, 1);
// public static final DwarfPointOfInterestType ARMORER = DwarfPointOfInterestType.register("armorer", DwarfPointOfInterestType.getAllStatesOf(Blocks.BLAST_FURNACE), 1, 1);
// public static final DwarfPointOfInterestType BUTCHER = DwarfPointOfInterestType.register("butcher", DwarfPointOfInterestType.getAllStatesOf(Blocks.SMOKER), 1, 1);
// public static final DwarfPointOfInterestType CARTOGRAPHER = DwarfPointOfInterestType.register("cartographer", DwarfPointOfInterestType.getAllStatesOf(Blocks.CARTOGRAPHY_TABLE), 1, 1);
// public static final DwarfPointOfInterestType CLERIC = DwarfPointOfInterestType.register("cleric", DwarfPointOfInterestType.getAllStatesOf(Blocks.BREWING_STAND), 1, 1);
// public static final DwarfPointOfInterestType FARMER = DwarfPointOfInterestType.register("farmer", DwarfPointOfInterestType.getAllStatesOf(Blocks.COMPOSTER), 1, 1);
// public static final DwarfPointOfInterestType FISHERMAN = DwarfPointOfInterestType.register("fisherman", DwarfPointOfInterestType.getAllStatesOf(Blocks.BARREL), 1, 1);
// public static final DwarfPointOfInterestType FLETCHER = DwarfPointOfInterestType.register("fletcher", DwarfPointOfInterestType.getAllStatesOf(Blocks.FLETCHING_TABLE), 1, 1);
// public static final DwarfPointOfInterestType LEATHERWORKER = DwarfPointOfInterestType.register("leatherworker", CAULDRON_STATES, 1, 1);
// public static final DwarfPointOfInterestType LIBRARIAN = DwarfPointOfInterestType.register("librarian", DwarfPointOfInterestType.getAllStatesOf(Blocks.LECTERN), 1, 1);
// public static final DwarfPointOfInterestType MASON = DwarfPointOfInterestType.register("mason", DwarfPointOfInterestType.getAllStatesOf(Blocks.STONECUTTER), 1, 1);
// public static final DwarfPointOfInterestType NITWIT = DwarfPointOfInterestType.register("nitwit", ImmutableSet.of(), 1, 1);
// public static final DwarfPointOfInterestType SHEPHERD = DwarfPointOfInterestType.register("shepherd", DwarfPointOfInterestType.getAllStatesOf(Blocks.LOOM), 1, 1);
// public static final DwarfPointOfInterestType TOOLSMITH = DwarfPointOfInterestType.register("toolsmith", DwarfPointOfInterestType.getAllStatesOf(Blocks.SMITHING_TABLE), 1, 1);
// public static final DwarfPointOfInterestType WEAPONSMITH = DwarfPointOfInterestType.register("weaponsmith", DwarfPointOfInterestType.getAllStatesOf(Blocks.GRINDSTONE), 1, 1);
// public static final DwarfPointOfInterestType HOME = DwarfPointOfInterestType.register("home", BED_STATES, 1, 1);
// public static final DwarfPointOfInterestType MEETING = DwarfPointOfInterestType.register("meeting", DwarfPointOfInterestType.getAllStatesOf(Blocks.BELL), 32, 6);
// public static final DwarfPointOfInterestType BEEHIVE = DwarfPointOfInterestType.register("beehive", DwarfPointOfInterestType.getAllStatesOf(Blocks.BEEHIVE), 0, 1);
// public static final DwarfPointOfInterestType BEE_NEST = DwarfPointOfInterestType.register("bee_nest", DwarfPointOfInterestType.getAllStatesOf(Blocks.BEE_NEST), 0, 1);
// public static final DwarfPointOfInterestType NETHER_PORTAL = DwarfPointOfInterestType.register("nether_portal", DwarfPointOfInterestType.getAllStatesOf(Blocks.NETHER_PORTAL), 0, 1);
// public static final DwarfPointOfInterestType LODESTONE = DwarfPointOfInterestType.register("lodestone", DwarfPointOfInterestType.getAllStatesOf(Blocks.LODESTONE), 0, 1);
// public static final DwarfPointOfInterestType LIGHTNING_ROD = DwarfPointOfInterestType.register("lightning_rod", DwarfPointOfInterestType.getAllStatesOf(Blocks.LIGHTNING_ROD), 0, 1);
// protected static final Set<BlockState> REGISTERED_STATES = new ObjectOpenHashSet<BlockState>(BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.keySet());
// private final String id;
// private final Set<BlockState> blockStates;
// private final int ticketCount;
// private final Predicate<DwarfPointOfInterestType> completionCondition;
// private final int searchDistance;

// private static Set<BlockState> getAllStatesOf(Block block) {
// return ImmutableSet.copyOf(block.getStateManager().getStates());
// }

// private DwarfPointOfInterestType(String id, Set<BlockState> blockStates, int ticketCount, Predicate<DwarfPointOfInterestType> completionCondition, int searchDistance) {
// this.id = id;
// this.blockStates = ImmutableSet.copyOf(blockStates);
// this.ticketCount = ticketCount;
// this.completionCondition = completionCondition;
// this.searchDistance = searchDistance;
// }

// private DwarfPointOfInterestType(String id, Set<BlockState> blockStates, int ticketCount, int searchDistance) {
// this.id = id;
// this.blockStates = ImmutableSet.copyOf(blockStates);
// this.ticketCount = ticketCount;
// this.completionCondition = poiType -> poiType == this;
// this.searchDistance = searchDistance;
// }

// public String getId() {
// return this.id;
// }

// public int getTicketCount() {
// return this.ticketCount;
// }

// public Predicate<DwarfPointOfInterestType> getCompletionCondition() {
// return this.completionCondition;
// }

// public boolean contains(BlockState state) {
// return this.blockStates.contains(state);
// }

// public int getSearchDistance() {
// return this.searchDistance;
// }

// public String toString() {
// return this.id;
// }

// private static DwarfPointOfInterestType register(String id, Set<BlockState> workStationStates, int ticketCount, int searchDistance) {
// return DwarfPointOfInterestType
// .setup(Registry.register(EntityInit.DWARF_POINT_OF_INTEREST_TYPE, new Identifier(id), new DwarfPointOfInterestType(id, workStationStates, ticketCount, searchDistance)));
// }

// private static DwarfPointOfInterestType register(String id, Set<BlockState> workStationStates, int ticketCount, Predicate<DwarfPointOfInterestType> completionCondition, int searchDistance) {
// return DwarfPointOfInterestType.setup(
// Registry.register(EntityInit.DWARF_POINT_OF_INTEREST_TYPE, new Identifier(id), new DwarfPointOfInterestType(id, workStationStates, ticketCount, completionCondition, searchDistance)));
// }

// private static DwarfPointOfInterestType setup(DwarfPointOfInterestType poiType) {
// poiType.blockStates.forEach(state -> {
// DwarfPointOfInterestType pointOfInterestType2 = BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.put((BlockState) state, poiType);
// if (pointOfInterestType2 != null) {
// throw Util.throwOrPause(new IllegalStateException(String.format("%s is defined in too many tags", state)));
// }
// });
// return poiType;
// }

// public static Optional<DwarfPointOfInterestType> from(BlockState state) {
// return Optional.ofNullable(BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.get(state));
// }
// }
