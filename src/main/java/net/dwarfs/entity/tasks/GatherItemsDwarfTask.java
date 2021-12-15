package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.entity.extra.DwarfProfession;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;

public class GatherItemsDwarfTask extends Task<DwarfEntity> {
    private static final int MAX_RANGE = 5;
    private static final float WALK_TOGETHER_SPEED = 0.5f;
    private Set<Item> items = ImmutableSet.of();

    public GatherItemsDwarfTask() {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        return LookTargetUtil.canSee(dwarfEntity.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return this.shouldRun(serverWorld, dwarfEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarfEntity2 = (DwarfEntity) dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        LookTargetUtil.lookAtAndWalkTowardsEachOther(dwarfEntity, dwarfEntity2, 0.5f);
        this.items = GatherItemsDwarfTask.getGatherableItems(dwarfEntity, dwarfEntity2);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarfEntity2 = (DwarfEntity) dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (dwarfEntity.squaredDistanceTo(dwarfEntity2) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(dwarfEntity, dwarfEntity2, 0.5f);
        dwarfEntity.talkWithVillager(serverWorld, dwarfEntity2, l);
        if (dwarfEntity.wantsToStartBreeding() && (dwarfEntity.getDwarfData().getProfession() == DwarfProfession.FARMER || dwarfEntity2.canBreed())) {
            GatherItemsDwarfTask.giveHalfOfStack(dwarfEntity, dwarfEntity.ITEM_FOOD_VALUES.keySet(), dwarfEntity2);
        }
        if (dwarfEntity2.getDwarfData().getProfession() == DwarfProfession.FARMER && dwarfEntity.getInventory().count(Items.WHEAT) > Items.WHEAT.getMaxCount() / 2) {
            GatherItemsDwarfTask.giveHalfOfStack(dwarfEntity, ImmutableSet.of(Items.WHEAT), dwarfEntity2);
        }
        if (!this.items.isEmpty() && dwarfEntity.getInventory().containsAny(this.items)) {
            GatherItemsDwarfTask.giveHalfOfStack(dwarfEntity, this.items, dwarfEntity2);
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> getGatherableItems(DwarfEntity dwarfEntity, DwarfEntity dwarfEntity2) {
        ImmutableSet<Item> immutableSet = dwarfEntity2.getDwarfData().getProfession().getGatherableItems();
        ImmutableSet<Item> immutableSet2 = dwarfEntity.getDwarfData().getProfession().getGatherableItems();
        return immutableSet.stream().filter(item -> !immutableSet2.contains(item)).collect(Collectors.toSet());
    }

    private static void giveHalfOfStack(DwarfEntity dwarfEntity, Set<Item> validItems, LivingEntity target) {
        SimpleInventory simpleInventory = dwarfEntity.getInventory();
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < simpleInventory.size(); ++i) {
            int j;
            Item item;
            ItemStack itemStack2 = simpleInventory.getStack(i);
            if (itemStack2.isEmpty() || !validItems.contains(item = itemStack2.getItem()))
                continue;
            if (itemStack2.getCount() > itemStack2.getMaxCount() / 2) {
                j = itemStack2.getCount() / 2;
            } else {
                if (itemStack2.getCount() <= 24)
                    continue;
                j = itemStack2.getCount() - 24;
            }
            itemStack2.decrement(j);
            itemStack = new ItemStack(item, j);
            break;
        }
        if (!itemStack.isEmpty()) {
            LookTargetUtil.give(dwarfEntity, itemStack, target.getPos());
        }
    }

}
