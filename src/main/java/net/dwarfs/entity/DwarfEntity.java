package net.dwarfs.entity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import net.dwarfs.entity.extra.DwarfData;
import net.dwarfs.entity.extra.DwarfDataContainer;
import net.dwarfs.entity.extra.DwarfProfession;
import net.dwarfs.entity.extra.DwarfTradeOffers;
import net.dwarfs.entity.tasks.DwarfTaskListProvider;
import net.dwarfs.init.EntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.VillagerGossips;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public class DwarfEntity extends PassiveEntity implements InteractionObserver, DwarfDataContainer, InventoryOwner, Npc, Merchant {
    // Merchant
    private static final TrackedData<Integer> HEAD_ROLLING_TIME_LEFT = DataTracker.registerData(MerchantEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final int field_30599 = 300;
    private static final int INVENTORY_SIZE = 8;
    @Nullable
    private PlayerEntity customer;
    @Nullable
    protected TradeOfferList offers;
    private final SimpleInventory inventory = new SimpleInventory(8);

    // Villager
    private static final TrackedData<DwarfData> DWARF_DATA = DataTracker.registerData(DwarfEntity.class, DwarfProfession.DWARF_DATA);
    public static final int field_30602 = 12;
    public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final int field_30604 = 2;
    private static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, new Item[] { Items.BEETROOT_SEEDS });
    private static final int field_30605 = 10;
    private static final int field_30606 = 1200;
    private static final int field_30607 = 24000;
    private static final int field_30608 = 25;
    private static final int field_30609 = 10;
    private static final int field_30610 = 5;
    private static final long field_30611 = 24000L;
    @VisibleForTesting
    public static final float field_30603 = 0.5f;
    private int levelUpTimer;
    private boolean levelingUp;
    @Nullable
    private PlayerEntity lastCustomer;
    private boolean field_30612;
    private byte foodLevel;
    private final VillagerGossips gossip = new VillagerGossips();
    private long gossipStartTime;
    private long lastGossipDecayTime;
    private int experience;
    private long lastRestockTime;
    private int restocksToday;
    private long lastRestockCheckTime;
    private boolean natural;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE,
            MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET,
            new MemoryModuleType[] { MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE,
                    MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE,
                    MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN,
                    MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY });
    private static final ImmutableList<SensorType<? extends Sensor<? super DwarfEntity>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY);
    // , SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<DwarfEntity, PointOfInterestType>> POINTS_OF_INTEREST = ImmutableMap.of(MemoryModuleType.HOME,
            (villager, poiType) -> poiType == PointOfInterestType.HOME, MemoryModuleType.JOB_SITE, (villager, poiType) -> villager.getDwarfData().getProfession().getWorkStation() == poiType,
            MemoryModuleType.POTENTIAL_JOB_SITE, (villager, poiType) -> PointOfInterestType.IS_USED_BY_PROFESSION.test((PointOfInterestType) poiType), MemoryModuleType.MEETING_POINT,
            (villager, poiType) -> poiType == PointOfInterestType.MEETING);

    // public DwarfEntity(EntityType<? extends DwarfEntity> entityType, World world) {
    // super(entityType, world);
    // this.stepHeight = 1.0F;
    // }

    // public DwarfEntity(EntityType<? extends DwarfEntity> entityType, World world) {
    // this(entityType, world, VillagerType.PLAINS);
    // }

    public DwarfEntity(EntityType<? extends DwarfEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1.0F;
        ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(true);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setDwarfData(this.getDwarfData().withProfession(DwarfProfession.NONE));
    }

    public static DefaultAttributeContainer.Builder createDwarfAttributes() {
        return PassiveEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D).add(EntityAttributes.GENERIC_ARMOR, 1.0D);
    }

    @Override
    public Brain<DwarfEntity> getBrain() {
        return (Brain<DwarfEntity>) super.getBrain();
    }

    @Override
    protected Brain.Profile<DwarfEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<DwarfEntity> brain = this.createBrainProfile().deserialize(dynamic);
        this.initBrain(brain);
        return brain;
    }

    public void reinitializeBrain(ServerWorld world) {
        Brain<DwarfEntity> brain = this.getBrain();
        brain.stopAllTasks(world, this);
        this.brain = brain.copy();
        this.initBrain(this.getBrain());
    }

    private void initBrain(Brain<DwarfEntity> brain) {
        DwarfProfession dwarfProfession = this.getDwarfData().getProfession();
        if (this.isBaby()) {
            brain.setSchedule(Schedule.VILLAGER_BABY);
            brain.setTaskList(Activity.PLAY, DwarfTaskListProvider.createPlayTasks(0.5f));
        } else {
            brain.setSchedule(Schedule.VILLAGER_DEFAULT);
            brain.setTaskList(Activity.WORK, DwarfTaskListProvider.createWorkTasks(dwarfProfession, 0.5f), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT)));
        }
        brain.setTaskList(Activity.CORE, DwarfTaskListProvider.createCoreTasks(dwarfProfession, 0.5f));
        brain.setTaskList(Activity.MEET, DwarfTaskListProvider.createMeetTasks(dwarfProfession, 0.5f), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT)));
        brain.setTaskList(Activity.REST, DwarfTaskListProvider.createRestTasks(dwarfProfession, 0.5f));
        brain.setTaskList(Activity.IDLE, DwarfTaskListProvider.createIdleTasks(dwarfProfession, 0.5f));
        // brain.setTaskList(Activity.PANIC, VillagerTaskListProvider.createPanicTasks(villagerProfession, 0.5f));
        // brain.setTaskList(Activity.PRE_RAID, VillagerTaskListProvider.createPreRaidTasks(villagerProfession, 0.5f));
        // brain.setTaskList(Activity.RAID, VillagerTaskListProvider.createRaidTasks(villagerProfession, 0.5f));
        // brain.setTaskList(Activity.HIDE, VillagerTaskListProvider.createHideTasks(villagerProfession, 0.5f));
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.doExclusively(Activity.IDLE);
        brain.refreshActivities(this.world.getTimeOfDay(), this.world.getTime());
    }

    @Override
    protected void onGrowUp() {
        super.onGrowUp();
        if (this.world instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld) this.world);
        }
    }

    public boolean isNatural() {
        return this.natural;
    }

    @Override
    protected void mobTick() {
        Raid raid;
        this.world.getProfiler().push("dwarfBrain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().pop();
        if (this.natural) {
            this.natural = false;
        }
        if (!this.hasCustomer() && this.levelUpTimer > 0) {
            --this.levelUpTimer;
            if (this.levelUpTimer <= 0) {
                if (this.levelingUp) {
                    this.levelUp();
                    this.levelingUp = false;
                }
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastCustomer != null && this.world instanceof ServerWorld) {
            ((ServerWorld) this.world).handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
            this.world.sendEntityStatus(this, (byte) 14);
            this.lastCustomer = null;
        }
        if (!this.isAiDisabled() && this.random.nextInt(100) == 0 && (raid = ((ServerWorld) this.world).getRaidAt(this.getBlockPos())) != null && raid.isActive() && !raid.isFinished()) {
            this.world.sendEntityStatus(this, (byte) 42);
        }
        if (this.getDwarfData().getProfession() == DwarfProfession.NONE && this.hasCustomer()) {
            this.resetCustomer();
        }
        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getHeadRollingTimeLeft() > 0) {
            this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
        }
        this.decayGossip();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                return ActionResult.success(this.world.isClient);
            }
            boolean bl = this.getOffers().isEmpty();
            if (hand == Hand.MAIN_HAND) {
                if (bl && !this.world.isClient) {
                    this.sayNo();
                }
                player.incrementStat(Stats.TALKED_TO_VILLAGER);
            }
            if (bl) {
                return ActionResult.success(this.world.isClient);
            }
            if (!this.world.isClient && !this.offers.isEmpty()) {
                this.beginTradeWith(player);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(player, hand);
    }

    private void sayNo() {
        this.setHeadRollingTimeLeft(40);
        if (!this.world.isClient()) {
            this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private void beginTradeWith(PlayerEntity customer) {
        this.prepareOffersFor(customer);
        this.setCurrentCustomer(customer);
        this.sendOffers(customer, this.getDisplayName(), this.getDwarfData().getLevel());
    }

    @Override
    public void setCurrentCustomer(@Nullable PlayerEntity customer) {
        boolean bl = this.getCurrentCustomer() != null && customer == null;
        this.customer = customer;
        if (bl) {
            this.resetCustomer();
        }
    }

    private void resetCustomer() {
        this.setCurrentCustomer(null);
        this.clearSpecialPrices();
    }

    /**
     * Resets the special price of all the trade offers of this villager.
     */
    private void clearSpecialPrices() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.clearSpecialPrice();
        }
    }

    @Override
    public boolean canRefreshTrades() {
        return true;
    }

    public void restock() {
        this.updateDemandBonus();
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.resetUses();
        }
        this.lastRestockTime = this.world.getTime();
        ++this.restocksToday;
    }

    /**
     * Returns whether this villager needs restock.
     * 
     * <p>
     * Checks if at least one of its trade offers has been used.
     */
    private boolean needsRestock() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            if (!tradeOffer.hasBeenUsed())
                continue;
            return true;
        }
        return false;
    }

    private boolean canRestock() {
        return this.restocksToday == 0 || this.restocksToday < 2 && this.world.getTime() > this.lastRestockTime + 2400L;
    }

    public boolean shouldRestock() {
        long l = this.lastRestockTime + 12000L;
        long m = this.world.getTime();
        boolean bl = m > l;
        long n = this.world.getTimeOfDay();
        if (this.lastRestockCheckTime > 0L) {
            long p = n / 24000L;
            long o = this.lastRestockCheckTime / 24000L;
            bl |= p > o;
        }
        this.lastRestockCheckTime = n;
        if (bl) {
            this.lastRestockTime = m;
            this.clearDailyRestockCount();
        }
        return this.canRestock() && this.needsRestock();
    }

    private void restockAndUpdateDemandBonus() {
        int i = 2 - this.restocksToday;
        if (i > 0) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.resetUses();
            }
        }
        for (int j = 0; j < i; ++j) {
            this.updateDemandBonus();
        }
    }

    /**
     * Updates the demand bonus of all the trade offers of this villager.
     */
    private void updateDemandBonus() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.updateDemandBonus();
        }
    }

    private void prepareOffersFor(PlayerEntity player) {
        int i = this.getReputation(player);
        if (i != 0) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice(-MathHelper.floor((float) i * tradeOffer.getPriceMultiplier()));
            }
        }
        if (player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
            int tradeOffer = statusEffectInstance.getAmplifier();
            for (TradeOffer tradeOffer2 : this.getOffers()) {
                double d = 0.3 + 0.0625 * (double) tradeOffer;
                int j = (int) Math.floor(d * (double) tradeOffer2.getOriginalFirstBuyItem().getCount());
                tradeOffer2.increaseSpecialPrice(-Math.max(j, 1));
            }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HEAD_ROLLING_TIME_LEFT, 0);
        this.dataTracker.startTracking(DWARF_DATA, new DwarfData(DwarfProfession.NONE, 1));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        TradeOfferList tradeOfferList = this.getOffers();
        if (!tradeOfferList.isEmpty()) {
            nbt.put("Offers", tradeOfferList.toNbt());
        }
        nbt.put("Inventory", this.inventory.toNbtList());

        DwarfData.CODEC.encodeStart(NbtOps.INSTANCE, this.getDwarfData()).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> nbt.put("DwarfData", (NbtElement) nbtElement));
        nbt.putByte("FoodLevel", this.foodLevel);
        nbt.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE).getValue());
        nbt.putInt("Xp", this.experience);
        nbt.putLong("LastRestock", this.lastRestockTime);
        nbt.putLong("LastGossipDecay", this.lastGossipDecayTime);
        nbt.putInt("RestocksToday", this.restocksToday);
        if (this.natural) {
            nbt.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        Object dataResult;
        if (nbt.contains("Offers", 10)) {
            this.offers = new TradeOfferList(nbt.getCompound("Offers"));
        }
        this.inventory.readNbtList(nbt.getList("Inventory", 10));

        if (nbt.contains("DwarfData", 10)) {
            dataResult = DwarfData.CODEC.parse(new Dynamic<NbtElement>(NbtOps.INSTANCE, nbt.get("DwarfData")));
            ((DataResult<DwarfData>) dataResult).resultOrPartial(LOGGER::error).ifPresent(this::setDwarfData);
        }
        if (nbt.contains("Offers", 10)) {
            this.offers = new TradeOfferList(nbt.getCompound("Offers"));
        }
        if (nbt.contains("FoodLevel", 1)) {
            this.foodLevel = nbt.getByte("FoodLevel");
        }
        dataResult = nbt.getList("Gossips", 10);
        this.gossip.deserialize(new Dynamic(NbtOps.INSTANCE, dataResult));
        // this.gossip.deserialize(new Dynamic<Object>(NbtOps.INSTANCE, dataResult));
        if (nbt.contains("Xp", 3)) {
            this.experience = nbt.getInt("Xp");
        }
        this.lastRestockTime = nbt.getLong("LastRestock");
        this.lastGossipDecayTime = nbt.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);
        if (this.world instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld) this.world);
        }
        this.restocksToday = nbt.getInt("RestocksToday");
        if (nbt.contains("AssignProfessionWhenSpawned")) {
            this.natural = nbt.getBoolean("AssignProfessionWhenSpawned");
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.hasCustomer()) {
            return SoundEvents.ENTITY_VILLAGER_TRADE;
        }
        return SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    public void playWorkSound() {
        SoundEvent soundEvent = this.getDwarfData().getProfession().getWorkSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void setDwarfData(DwarfData dwarfData) {
        DwarfData dwarfData2 = this.getDwarfData();
        if (dwarfData2.getProfession() != dwarfData.getProfession()) {
            this.offers = null;
        }
        this.dataTracker.set(DWARF_DATA, dwarfData);
    }

    @Override
    public DwarfData getDwarfData() {
        return this.dataTracker.get(DWARF_DATA);
    }

    private void afterUsing(TradeOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.experience += offer.getMerchantExperience();
        this.lastCustomer = this.getCurrentCustomer();
        if (this.canLevelUp()) {
            this.levelUpTimer = 40;
            this.levelingUp = true;
            i += 5;
        }
        if (offer.shouldRewardPlayerExperience()) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    public void method_35201(boolean bl) {
        this.field_30612 = bl;
    }

    public boolean method_35200() {
        return this.field_30612;
    }

    @Override
    public void setAttacker(@Nullable LivingEntity attacker) {
        if (attacker != null && this.world instanceof ServerWorld) {
            ((ServerWorld) this.world).handleInteraction(EntityInteraction.VILLAGER_HURT, attacker, this);
            if (this.isAlive() && attacker instanceof PlayerEntity) {
                this.world.sendEntityStatus(this, (byte) 13);
            }
        }
        super.setAttacker(attacker);
    }

    @Override
    public void onDeath(DamageSource source) {
        LOGGER.info("Villager {} died, message: '{}'", (Object) this, (Object) source.getDeathMessage(this).getString());
        Entity entity = source.getAttacker();
        if (entity != null) {
            this.notifyDeath(entity);
        }
        this.releaseAllTickets();
        this.resetCustomer();
        super.onDeath(source);
    }

    private void releaseAllTickets() {
        this.releaseTicketFor(MemoryModuleType.HOME);
        this.releaseTicketFor(MemoryModuleType.JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
    }

    private void notifyDeath(Entity killer) {
        World world = this.world;
        if (!(world instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) world;
        Optional<LivingTargetCache> optional = this.brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
        if (optional.isEmpty()) {
            return;
        }
        optional.get().iterate(InteractionObserver.class::isInstance)
                .forEach(livingEntity -> serverWorld.handleInteraction(EntityInteraction.VILLAGER_KILLED, killer, (InteractionObserver) ((Object) livingEntity)));
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> memoryModuleType) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerWorld) this.world).getServer();
        this.brain.getOptionalMemory(memoryModuleType).ifPresent(pos -> {
            ServerWorld serverWorld = minecraftServer.getWorld(pos.getDimension());
            if (serverWorld == null) {
                return;
            }
            PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
            Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(pos.getPos());
            BiPredicate<DwarfEntity, PointOfInterestType> biPredicate = POINTS_OF_INTEREST.get(memoryModuleType);
            if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                pointOfInterestStorage.releaseTicket(pos.getPos());
                DebugInfoSender.sendPointOfInterest(serverWorld, pos.getPos());
            }
        });
    }

    @Override
    public boolean isReadyToBreed() {
        return this.foodLevel + this.getAvailableFood() >= 12 && this.getBreedingAge() == 0;
    }

    private boolean lacksFood() {
        return this.foodLevel < 12;
    }

    private void consumeAvailableFood() {
        if (!this.lacksFood() || this.getAvailableFood() == 0) {
            return;
        }
        for (int i = 0; i < this.getInventory().size(); ++i) {
            int j;
            Integer integer;
            ItemStack itemStack = this.getInventory().getStack(i);
            if (itemStack.isEmpty() || (integer = ITEM_FOOD_VALUES.get(itemStack.getItem())) == null)
                continue;
            for (int k = j = itemStack.getCount(); k > 0; --k) {
                this.foodLevel = (byte) (this.foodLevel + integer);
                this.getInventory().removeStack(i, 1);
                if (this.lacksFood())
                    continue;
                return;
            }
        }
    }

    public int getReputation(PlayerEntity player) {
        return this.gossip.getReputationFor(player.getUuid(), gossipType -> true);
    }

    private void depleteFood(int amount) {
        this.foodLevel = (byte) (this.foodLevel - amount);
    }

    public void eatForBreeding() {
        this.consumeAvailableFood();
        this.depleteFood(12);
    }

    public void setOffers(TradeOfferList offers) {
        this.offers = offers;
    }

    private boolean canLevelUp() {
        int i = this.getDwarfData().getLevel();
        return DwarfData.canLevelUp(i) && this.experience >= DwarfData.getUpperLevelExperience(i);
    }

    private void levelUp() {
        this.setDwarfData(this.getDwarfData().withLevel(this.getDwarfData().getLevel() + 1));
        this.fillRecipes();
    }

    @Override
    protected Text getDefaultName() {
        return new TranslatableText(this.getType().getTranslationKey() + "." + EntityInit.DWARF_PROFESSION.getId(this.getDwarfData().getProfession()).getPath());
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 12) {
            this.produceParticles(ParticleTypes.HEART);
        } else if (status == 13) {
            this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
        } else if (status == 14) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        } else if (status == 42) {
            this.produceParticles(ParticleTypes.SPLASH);
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason == SpawnReason.BREEDING) {
            this.setDwarfData(this.getDwarfData().withProfession(DwarfProfession.NONE));
        }
        if (spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.DISPENSER) {
            this.setDwarfData(this.getDwarfData().withProfession(DwarfProfession.NONE));
        }
        if (spawnReason == SpawnReason.STRUCTURE) {
            this.natural = true;
        }
        if (entityData == null) {
            entityData = new PassiveEntity.PassiveData(false);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public DwarfEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        DwarfEntity dwarfEntity = new DwarfEntity(EntityInit.DWARF, serverWorld);
        dwarfEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(dwarfEntity.getBlockPos()), SpawnReason.BREEDING, null, null);
        return dwarfEntity;
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        if (world.getDifficulty() != Difficulty.PEACEFUL) {
            LOGGER.info("Dwarf {} was struck by lightning {}.", (Object) this, (Object) lightning);
            WitchEntity witchEntity = EntityType.WITCH.create(world);
            witchEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
            witchEntity.initialize(world, world.getLocalDifficulty(witchEntity.getBlockPos()), SpawnReason.CONVERSION, null, null);
            witchEntity.setAiDisabled(this.isAiDisabled());
            if (this.hasCustomName()) {
                witchEntity.setCustomName(this.getCustomName());
                witchEntity.setCustomNameVisible(this.isCustomNameVisible());
            }
            witchEntity.setPersistent();
            world.spawnEntityAndPassengers(witchEntity);
            this.releaseAllTickets();
            this.discard();
        } else {
            super.onStruckByLightning(world, lightning);
        }
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.canGather(itemStack)) {
            SimpleInventory simpleInventory = this.getInventory();
            boolean bl = simpleInventory.canInsert(itemStack);
            if (!bl) {
                return;
            }
            this.triggerItemPickedUpByEntityCriteria(item);
            this.sendPickup(item, itemStack.getCount());
            ItemStack itemStack2 = simpleInventory.addStack(itemStack);
            if (itemStack2.isEmpty()) {
                item.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }
    }

    @Override
    public boolean canGather(ItemStack stack) {
        Item item = stack.getItem();
        return (GATHERABLE_ITEMS.contains(item) || this.getDwarfData().getProfession().getGatherableItems().contains(item)) && this.getInventory().canInsert(stack);
    }

    public boolean wantsToStartBreeding() {
        return this.getAvailableFood() >= 24;
    }

    public boolean canBreed() {
        return this.getAvailableFood() < 12;
    }

    private int getAvailableFood() {
        SimpleInventory simpleInventory = this.getInventory();
        return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(entry -> simpleInventory.count((Item) entry.getKey()) * (Integer) entry.getValue()).sum();
    }

    public boolean hasSeedToPlant() {
        return this.getInventory().containsAny(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
    }

    private void fillRecipes() {
        DwarfData dwarfData = this.getDwarfData();
        Int2ObjectMap<DwarfTradeOffers.Factory[]> int2ObjectMap = DwarfTradeOffers.PROFESSION_TO_LEVELED_TRADE.get(dwarfData.getProfession());
        if (int2ObjectMap == null || int2ObjectMap.isEmpty()) {
            return;
        }
        DwarfTradeOffers.Factory[] factorys = (DwarfTradeOffers.Factory[]) int2ObjectMap.get(dwarfData.getLevel());
        if (factorys == null) {
            return;
        }
        TradeOfferList tradeOfferList = this.getOffers();
        this.fillRecipesFromPool(tradeOfferList, factorys, 2);
    }

    public void talkWithVillager(ServerWorld world, DwarfEntity villager, long time) {
        if (time >= this.gossipStartTime && time < this.gossipStartTime + 1200L || time >= villager.gossipStartTime && time < villager.gossipStartTime + 1200L) {
            return;
        }
        this.gossip.shareGossipFrom(villager.gossip, this.random, 10);
        this.gossipStartTime = time;
        villager.gossipStartTime = time;
        this.summonGolem(world, time, 5);
    }

    private void decayGossip() {
        long l = this.world.getTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = l;
            return;
        }
        if (l < this.lastGossipDecayTime + 24000L) {
            return;
        }
        this.gossip.decay();
        this.lastGossipDecayTime = l;
    }

    public void summonGolem(ServerWorld world, long time, int requiredCount) {
        if (!this.canSummonGolem(time)) {
            return;
        }
        Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
        List<DwarfEntity> list = world.getNonSpectatingEntities(DwarfEntity.class, box);
        List list2 = list.stream().filter(villager -> villager.canSummonGolem(time)).limit(5L).collect(Collectors.toList());
        if (list2.size() < requiredCount) {
            return;
        }
        IronGolemEntity ironGolemEntity = this.spawnIronGolem(world);
        if (ironGolemEntity == null) {
            return;
        }
        list.forEach(GolemLastSeenSensor::rememberIronGolem);
    }

    public boolean canSummonGolem(long time) {
        if (!this.hasRecentlySlept(this.world.getTime())) {
            return false;
        }
        return !this.brain.hasMemoryModule(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    }

    @Nullable
    private IronGolemEntity spawnIronGolem(ServerWorld world) {
        BlockPos blockPos = this.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            IronGolemEntity ironGolemEntity;
            double e;
            double d = world.random.nextInt(16) - 8;
            BlockPos blockPos2 = this.getHighestOpenPositionOnOffset(blockPos, d, e = (double) (world.random.nextInt(16) - 8));
            if (blockPos2 == null || (ironGolemEntity = EntityType.IRON_GOLEM.create(world, null, null, null, blockPos2, SpawnReason.MOB_SUMMONED, false, false)) == null)
                continue;
            if (ironGolemEntity.canSpawn(world, SpawnReason.MOB_SUMMONED) && ironGolemEntity.canSpawn(world)) {
                world.spawnEntityAndPassengers(ironGolemEntity);
                return ironGolemEntity;
            }
            ironGolemEntity.discard();
        }
        return null;
    }

    @Nullable
    private BlockPos getHighestOpenPositionOnOffset(BlockPos pos, double x, double z) {
        int i = 6;
        BlockPos blockPos = pos.add(x, 6.0, z);
        BlockState blockState = this.world.getBlockState(blockPos);
        for (int j = 6; j >= -6; --j) {
            BlockPos blockPos2 = blockPos;
            BlockState blockState2 = blockState;
            blockPos = blockPos2.down();
            blockState = this.world.getBlockState(blockPos);
            if (!blockState2.isAir() && !blockState2.getMaterial().isLiquid() || !blockState.getMaterial().blocksLight())
                continue;
            return blockPos2;
        }
        return null;
    }

    @Override
    public void onInteractionWith(EntityInteraction interaction, Entity entity) {
        if (interaction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_POSITIVE, 20);
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_POSITIVE, 25);
        } else if (interaction == EntityInteraction.TRADE) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.TRADING, 2);
        } else if (interaction == EntityInteraction.VILLAGER_HURT) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_NEGATIVE, 25);
        } else if (interaction == EntityInteraction.VILLAGER_KILLED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_NEGATIVE, 25);
        }
    }

    @Override
    public int getExperience() {
        return this.experience;
    }

    public void setExperience(int amount) {
        this.experience = amount;
    }

    private void clearDailyRestockCount() {
        this.restockAndUpdateDemandBonus();
        this.restocksToday = 0;
    }

    public VillagerGossips getGossip() {
        return this.gossip;
    }

    public void readGossipDataNbt(NbtElement nbt) {
        this.gossip.deserialize(new Dynamic<NbtElement>(NbtOps.INSTANCE, nbt));
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public void sleep(BlockPos pos) {
        super.sleep(pos);
        this.brain.remember(MemoryModuleType.LAST_SLEPT, this.world.getTime());
        this.brain.forget(MemoryModuleType.WALK_TARGET);
        this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void wakeUp() {
        super.wakeUp();
        this.brain.remember(MemoryModuleType.LAST_WOKEN, this.world.getTime());
    }

    private boolean hasRecentlySlept(long worldTime) {
        Optional<Long> optional = this.brain.getOptionalMemory(MemoryModuleType.LAST_SLEPT);
        if (optional.isPresent()) {
            return worldTime - optional.get() < 24000L;
        }
        return false;
    }

    // MerchantEntity

    public int getHeadRollingTimeLeft() {
        return this.dataTracker.get(HEAD_ROLLING_TIME_LEFT);
    }

    public void setHeadRollingTimeLeft(int ticks) {
        this.dataTracker.set(HEAD_ROLLING_TIME_LEFT, ticks);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        if (this.isBaby()) {
            return 0.81f;
        }
        return 1.62f;
    }

    @Override
    @Nullable
    public PlayerEntity getCurrentCustomer() {
        return this.customer;
    }

    public boolean hasCustomer() {
        return this.customer != null;
    }

    @Override
    public TradeOfferList getOffers() {
        if (this.offers == null) {
            this.offers = new TradeOfferList();
            this.fillRecipes();
        }
        return this.offers;
    }

    @Override
    public void setOffersFromServer(@Nullable TradeOfferList offers) {
    }

    @Override
    public void setExperienceFromServer(int experience) {
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.afterUsing(offer);
        if (this.customer instanceof ServerPlayerEntity) {
            // Criteria.VILLAGER_TRADE.trigger((ServerPlayerEntity) this.customer, this, offer.getSellItem());
        }
    }

    @Override
    public boolean isLeveledMerchant() {
        return true;
    }

    @Override
    public void onSellingItem(ItemStack stack) {
        if (!this.world.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
            this.ambientSoundChance = -this.getMinAmbientSoundDelay();
            this.playSound(this.getTradingSound(!stack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }

    protected SoundEvent getTradingSound(boolean sold) {
        return sold ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
    }

    public void playCelebrateSound() {
        this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        this.resetCustomer();
        return super.moveToWorld(destination);
    }

    protected void produceParticles(ParticleEffect parameters) {
        for (int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        int i = mappedIndex - 300;
        if (i >= 0 && i < this.inventory.size()) {
            return StackReference.of(this.inventory, i);
        }
        return super.getStackReference(mappedIndex);
    }

    protected void fillRecipesFromPool(TradeOfferList recipeList, DwarfTradeOffers.Factory[] pool, int count) {
        HashSet<Integer> set = Sets.newHashSet();
        if (pool.length > count) {
            while (set.size() < count) {
                set.add(this.random.nextInt(pool.length));
            }
        } else {
            for (int i = 0; i < pool.length; ++i) {
                set.add(i);
            }
        }
        for (Integer integer : set) {
            DwarfTradeOffers.Factory factory = pool[integer];
            TradeOffer tradeOffer = factory.create(this, this.random);
            if (tradeOffer == null)
                continue;
            recipeList.add(tradeOffer);
        }
    }

    @Override
    public Vec3d getLeashPos(float delta) {
        float f = MathHelper.lerp(delta, this.prevBodyYaw, this.bodyYaw) * ((float) Math.PI / 180);
        Vec3d vec3d = new Vec3d(0.0, this.getBoundingBox().getYLength() - 1.0, 0.2);
        return this.getLerpedPos(delta).add(vec3d.rotateY(-f));
    }

    @Override
    public boolean isClient() {
        return this.world.isClient;
    }

}
