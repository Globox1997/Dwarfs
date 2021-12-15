package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

public class BoneMealTask extends Task<DwarfEntity> {
    private static final int MAX_DURATION = 80;
    private long startTime;
    private long lastEndEntityAge;
    private int duration;
    private Optional<BlockPos> pos = Optional.empty();

    public BoneMealTask() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        if (dwarfEntity.age % 10 != 0 || this.lastEndEntityAge != 0L && this.lastEndEntityAge + 160L > (long) dwarfEntity.age) {
            return false;
        }
        if (dwarfEntity.getInventory().count(Items.BONE_MEAL) <= 0) {
            return false;
        }
        this.pos = this.findBoneMealPos(serverWorld, dwarfEntity);
        return this.pos.isPresent();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return this.duration < 80 && this.pos.isPresent();
    }

    private Optional<BlockPos> findBoneMealPos(ServerWorld world, DwarfEntity entity) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Optional<BlockPos> optional = Optional.empty();
        int i = 0;
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    mutable.set(entity.getBlockPos(), j, k, l);
                    if (!this.canBoneMeal(mutable, world) || world.random.nextInt(++i) != 0)
                        continue;
                    optional = Optional.of(mutable.toImmutable());
                }
            }
        }
        return optional;
    }

    private boolean canBoneMeal(BlockPos pos, ServerWorld world) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block instanceof CropBlock && !((CropBlock) block).isMature(blockState);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        this.addLookWalkTargets(dwarfEntity);
        dwarfEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
        this.startTime = l;
        this.duration = 0;
    }

    private void addLookWalkTargets(DwarfEntity dwarfEntity) {
        this.pos.ifPresent(pos -> {
            BlockPosLookTarget blockPosLookTarget = new BlockPosLookTarget((BlockPos) pos);
            dwarfEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, blockPosLookTarget);
            dwarfEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPosLookTarget, 0.5f, 1));
        });
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.lastEndEntityAge = dwarfEntity.age;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        BlockPos blockPos = this.pos.get();
        if (l < this.startTime || !blockPos.isWithinDistance(dwarfEntity.getPos(), 1.0)) {
            return;
        }
        ItemStack itemStack = ItemStack.EMPTY;
        SimpleInventory simpleInventory = dwarfEntity.getInventory();
        int i = simpleInventory.size();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2 = simpleInventory.getStack(j);
            if (!itemStack2.isOf(Items.BONE_MEAL))
                continue;
            itemStack = itemStack2;
            break;
        }
        if (!itemStack.isEmpty() && BoneMealItem.useOnFertilizable(itemStack, serverWorld, blockPos)) {
            serverWorld.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 0);
            this.pos = this.findBoneMealPos(serverWorld, dwarfEntity);
            this.addLookWalkTargets(dwarfEntity);
            this.startTime = l + 40L;
        }
        ++this.duration;
    }

}
