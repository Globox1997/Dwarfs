package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;

import net.dwarfs.entity.DwarfEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import org.jetbrains.annotations.Nullable;

public class HoldTradeOffersTask extends Task<DwarfEntity> {
    private static final int RUN_INTERVAL = 900;
    private static final int OFFER_SHOWING_INTERVAL = 40;
    @Nullable
    private ItemStack customerHeldStack;
    private final List<ItemStack> offers = Lists.newArrayList();
    private int offerShownTicks;
    private int offerIndex;
    private int ticksLeft;

    public HoldTradeOffersTask(int minRunTime, int maxRunTime) {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT), minRunTime, maxRunTime);
    }

    @Override
    public boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        if (!brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        }
        LivingEntity livingEntity = brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return livingEntity.getType() == EntityType.PLAYER && dwarfEntity.isAlive() && livingEntity.isAlive() && !dwarfEntity.isBaby() && dwarfEntity.squaredDistanceTo(livingEntity) <= 17.0;
    }

    @Override
    public boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return this.shouldRun(serverWorld, dwarfEntity) && this.ticksLeft > 0 && dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        super.run(serverWorld, dwarfEntity, l);
        this.findPotentialCustomer(dwarfEntity);
        this.offerShownTicks = 0;
        this.offerIndex = 0;
        this.ticksLeft = 40;
    }

    @Override
    public void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        LivingEntity livingEntity = this.findPotentialCustomer(dwarfEntity);
        this.setupOffers(livingEntity, dwarfEntity);
        if (!this.offers.isEmpty()) {
            this.refreshShownOffer(dwarfEntity);
        } else {
            HoldTradeOffersTask.holdNothing(dwarfEntity);
            this.ticksLeft = Math.min(this.ticksLeft, 40);
        }
        --this.ticksLeft;
    }

    @Override
    public void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        super.finishRunning(serverWorld, dwarfEntity, l);
        dwarfEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
        HoldTradeOffersTask.holdNothing(dwarfEntity);
        this.customerHeldStack = null;
    }

    private void setupOffers(LivingEntity customer, DwarfEntity dwarfEntity) {
        boolean bl = false;
        ItemStack itemStack = customer.getMainHandStack();
        if (this.customerHeldStack == null || !ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, itemStack)) {
            this.customerHeldStack = itemStack;
            bl = true;
            this.offers.clear();
        }
        if (bl && !this.customerHeldStack.isEmpty()) {
            this.loadPossibleOffers(dwarfEntity);
            if (!this.offers.isEmpty()) {
                this.ticksLeft = 900;
                this.holdOffer(dwarfEntity);
            }
        }
    }

    private void holdOffer(DwarfEntity dwarfEntity) {
        HoldTradeOffersTask.holdOffer(dwarfEntity, this.offers.get(0));
    }

    private void loadPossibleOffers(DwarfEntity dwarfEntity) {
        for (TradeOffer tradeOffer : dwarfEntity.getOffers()) {
            if (tradeOffer.isDisabled() || !this.isPossible(tradeOffer))
                continue;
            this.offers.add(tradeOffer.getSellItem());
        }
    }

    private boolean isPossible(TradeOffer offer) {
        return ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, offer.getAdjustedFirstBuyItem()) || ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, offer.getSecondBuyItem());
    }

    private static void holdNothing(DwarfEntity dwarfEntity) {
        dwarfEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        dwarfEntity.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.085f);
    }

    private static void holdOffer(DwarfEntity dwarfEntity, ItemStack stack) {
        dwarfEntity.equipStack(EquipmentSlot.MAINHAND, stack);
        dwarfEntity.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    private LivingEntity findPotentialCustomer(DwarfEntity dwarfEntity) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        LivingEntity livingEntity = brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(livingEntity, true));
        return livingEntity;
    }

    private void refreshShownOffer(DwarfEntity dwarfEntity) {
        if (this.offers.size() >= 2 && ++this.offerShownTicks >= 40) {
            ++this.offerIndex;
            this.offerShownTicks = 0;
            if (this.offerIndex > this.offers.size() - 1) {
                this.offerIndex = 0;
            }
            HoldTradeOffersTask.holdOffer(dwarfEntity, this.offers.get(this.offerIndex));
        }
    }

}
