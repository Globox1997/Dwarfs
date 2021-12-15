// package net.dwarfs.entity.extra;

// import com.mojang.datafixers.kinds.Applicative;
// import com.mojang.serialization.Codec;
// import com.mojang.serialization.MapCodec;
// import com.mojang.serialization.codecs.RecordCodecBuilder;
// import java.util.Objects;
// import net.minecraft.util.annotation.Debug;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.util.registry.Registry;

// public class DwarfPointOfInterest {
// private final BlockPos pos;
// private final DwarfPointOfInterestType type;
// private int freeTickets;
// private final Runnable updateListener;

// public static Codec<DwarfPointOfInterest> createCodec(Runnable updateListener) {
// return RecordCodecBuilder.create(instance -> instance
// .group(((MapCodec) BlockPos.CODEC.fieldOf("pos")).forGetter(poi -> ((BlockPos) poi).pos),
// ((MapCodec) Registry.POINT_OF_INTEREST_TYPE.getCodec().fieldOf("type")).forGetter(poi -> poi.type),
// ((MapCodec) Codec.INT.fieldOf("free_tickets")).orElse(0).forGetter(poi -> (int) poi.freeTickets), RecordCodecBuilder.point(updateListener))
// .apply((Applicative) instance,
// (pos, type, freeTickets, updateListener2) -> new DwarfPointOfInterest((BlockPos) pos, (DwarfPointOfInterestType) type, (int) freeTickets, (Runnable) updateListener2)));
// // (profession1, level1) -> new DwarfData((DwarfProfession) profession1, (int) level1)));
// // <DwarfPointOfInterest, ?>
// }

// private DwarfPointOfInterest(BlockPos pos, DwarfPointOfInterestType type, int freeTickets, Runnable updateListener) {
// this.pos = pos.toImmutable();
// this.type = type;
// this.freeTickets = freeTickets;
// this.updateListener = updateListener;
// }

// public DwarfPointOfInterest(BlockPos pos, DwarfPointOfInterestType type, Runnable updateListener) {
// this(pos, type, type.getTicketCount(), updateListener);
// }

// @Deprecated
// @Debug
// public int getFreeTickets() {
// return this.freeTickets;
// }

// protected boolean reserveTicket() {
// if (this.freeTickets <= 0) {
// return false;
// }
// --this.freeTickets;
// this.updateListener.run();
// return true;
// }

// protected boolean releaseTicket() {
// if (this.freeTickets >= this.type.getTicketCount()) {
// return false;
// }
// ++this.freeTickets;
// this.updateListener.run();
// return true;
// }

// public boolean hasSpace() {
// return this.freeTickets > 0;
// }

// public boolean isOccupied() {
// return this.freeTickets != this.type.getTicketCount();
// }

// public BlockPos getPos() {
// return this.pos;
// }

// public DwarfPointOfInterestType getType() {
// return this.type;
// }

// public boolean equals(Object o) {
// if (this == o) {
// return true;
// }
// if (o == null || this.getClass() != o.getClass()) {
// return false;
// }
// return Objects.equals(this.pos, ((DwarfPointOfInterest) o).pos);
// }

// public int hashCode() {
// return this.pos.hashCode();
// }
// }
