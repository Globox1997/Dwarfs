package net.dwarfs.entity.extra;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dwarfs.init.EntityInit;

public class DwarfData {
    public static final int field_30613 = 1;
    public static final int field_30614 = 5;
    private static final int[] LEVEL_BASE_EXPERIENCE = new int[] { 0, 10, 70, 150, 250 };
    public static final Codec<DwarfData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(((MapCodec) EntityInit.DWARF_PROFESSION.getCodec().fieldOf("profession")).orElseGet(() -> DwarfProfession.NONE).forGetter(dwarfData -> ((DwarfData) dwarfData).profession),

                    ((MapCodec) Codec.INT.fieldOf("level")).orElse(1).forGetter(dwarfData -> ((DwarfData) dwarfData).level))
            .apply((Applicative) instance, (profession1, level1) -> new DwarfData((DwarfProfession) profession1, (int) level1)));// (Applicative<DwarfData, ?>)

    // public static final Codec<DwarfData> CODEC = RecordCodecBuilder.create(instance -> instance
    // .group(((MapCodec) EntityInit.DWARF_PROFESSION.getCodec().fieldOf("profession")).orElseGet(() -> DwarfProfession.NONE).forGetter(dwarfData -> ((DwarfData) dwarfData).profession),

    // ((MapCodec) Codec.INT.fieldOf("level")).orElse(1).forGetter(dwarfData -> ((DwarfData) dwarfData).level))
    // .apply((Applicative) instance, DwarfData::new));// (Applicative<DwarfData, ?>)
    // private final VillagerType type;
    private final DwarfProfession profession;
    private final int level;

    public DwarfData(DwarfProfession profession, int level) {
        // this.type = type;
        this.profession = profession;
        this.level = Math.max(1, level);
    }

    // public DwarfData(Object object, Object object2) {
    // this.profession = (DwarfProfession) object;
    // this.level = Math.max(1, (int) object2);
    // }

    // public VillagerType getType() {
    // return this.type;
    // }

    public DwarfProfession getProfession() {
        return this.profession;
    }

    public int getLevel() {
        return this.level;
    }

    // public DwarfData withType(VillagerType type) {
    // return new DwarfData(type, this.profession, this.level);
    // }

    public DwarfData withProfession(DwarfProfession profession) {
        return new DwarfData(profession, this.level);
    }

    public DwarfData withLevel(int level) {
        return new DwarfData(this.profession, level);
    }

    public static int getLowerLevelExperience(int level) {
        return DwarfData.canLevelUp(level) ? LEVEL_BASE_EXPERIENCE[level - 1] : 0;
    }

    public static int getUpperLevelExperience(int level) {
        return DwarfData.canLevelUp(level) ? LEVEL_BASE_EXPERIENCE[level] : 0;
    }

    public static boolean canLevelUp(int level) {
        return level >= 1 && level < 5;
    }
}
