package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.entity.extra.DwarfProfession;
import net.dwarfs.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.AttackTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.CrossbowAttackTask;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.FindWalkTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetCompletedPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.JumpInBedTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.ScheduleActivityTask;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.entity.ai.brain.task.StartRaidTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WakeUpTask;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.brain.task.WanderIndoorsTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.world.poi.PointOfInterestType;

public class DwarfTaskListProvider {
    // private static final float field_30189 = 0.4f;

    // Pair<Integer, ? extends Task<? super DwarfEntity>>
    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createCoreTasks(DwarfProfession profession, float speed) {

        return ImmutableList.of(Pair.of(0, new StayAboveWaterTask(0.8f)), Pair.of(0, new OpenDoorsTask()), Pair.of(0, new LookAroundTask(45, 90)), Pair.of(0, new WakeUpTask()),
                Pair.of(0, new StartRaidTask()), Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE)),
                Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.POTENTIAL_JOB_SITE)), Pair.of(1, new WanderAroundTask()),
                Pair.of(2, new WorkStationCompetitionTask(profession)), Pair.of(3, new DwarfAttackTask(5, 0.75f)),
                // new Pair[] { Pair.of(5, new WalkToNearestVisibleWantedItemTask<>(speed, false, 4)),
                // Pair.of(6, new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())),
                // Pair.of(7, new WalkTowardJobSiteTask(speed)), Pair.of(8, new TakeJobSiteTask(speed)),
                // Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte) 14))),
                // Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte) 14))), Pair.of(10, new GoToWorkTask()),
                // Pair.of(10, new LoseJobOnSiteLossTask()) });
                Pair.of(5, new WalkToNearestVisibleWantedItemTask<>(speed, false, 4)),
                Pair.of(6, new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())),
                Pair.of(7, new WalkTowardJobSiteTask(speed)), Pair.of(8, new TakeJobSiteTask(speed)),
                Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte) 14))),
                Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte) 14))), Pair.of(10, new GoToWorkTask()),
                Pair.of(10, new LoseJobOnSiteLossTask()));
        // , Pair.of(0, new PanicTask()), Pair.of(0, new HideWhenBellRingsTask()), Pair.of(3, new FollowCustomerTask(speed))
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createWorkTasks(DwarfProfession profession, float speed) {
        DwarfWorkTask dwarfWorkTask = profession == DwarfProfession.FARMER ? new FarmerWorkTask() : new DwarfWorkTask();
        return ImmutableList.of(DwarfTaskListProvider.createBusyFollowTask(),
                Pair.of(5,
                        new RandomTask(ImmutableList.of(Pair.of(dwarfWorkTask, 7), Pair.of(new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 0.4f, 4), 2),
                                Pair.of(new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 0.4f, 1, 10), 5),
                                Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5),
                                Pair.of(new FarmerDwarfTask(), profession == DwarfProfession.FARMER ? 2 : 5), Pair.of(new BoneMealTask(), profession == DwarfProfession.FARMER ? 4 : 7)))),
                Pair.of(10, new HoldTradeOffersTask(400, 1600)), Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
                Pair.of(2, new DwarfWalkTowardsTask(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)), Pair.of(99, new ScheduleActivityTask()));
        // , Pair.of(3, new GiveGiftsToHeroTask(100))
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPlayTasks(float speed) {
        return ImmutableList.of(Pair.of(0, new WanderAroundTask(80, 120)), DwarfTaskListProvider.createFreeFollowTask(), Pair.of(5, new PlayWithDwarfBabiesTask()),
                Pair.of(5,
                        new RandomTask(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT),
                                ImmutableList.of(Pair.of(FindEntityTask.create(EntityInit.DWARF, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
                                        Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1), Pair.of(new FindWalkTargetTask(speed), 1),
                                        Pair.of(new GoTowardsLookTarget(speed, 2), 1), Pair.of(new JumpInBedTask(speed), 2), Pair.of(new WaitTask(20, 40), 2)))),
                Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createRestTasks(DwarfProfession profession, float speed) {
        return ImmutableList.of(Pair.of(2, new DwarfWalkTowardsTask(MemoryModuleType.HOME, speed, 1, 150, 1200)),
                Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)), Pair.of(3, new SleepTask()),
                Pair.of(5,
                        new RandomTask(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(new WalkHomeTask(speed), 1),
                                Pair.of(new WanderIndoorsTask(speed), 4), Pair.of(new GoToPointOfInterestTask(speed, 4), 2), Pair.of(new WaitTask(20, 40), 2)))),
                DwarfTaskListProvider.createBusyFollowTask(), Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createMeetTasks(DwarfProfession profession, float speed) {
        return ImmutableList.of(Pair.of(2, new RandomTask(ImmutableList.of(Pair.of(new GoToIfNearbyTask(MemoryModuleType.MEETING_POINT, 0.4f, 40), 2), Pair.of(new MeetDwarfTask(), 2)))),
                Pair.of(10, new HoldTradeOffersTask(400, 1600)), Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
                Pair.of(2, new DwarfWalkTowardsTask(MemoryModuleType.MEETING_POINT, speed, 6, 100, 200)),
                Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)), Pair.of(3, new CompositeTask(ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of(Pair.of(new GatherItemsDwarfTask(), 1)))),
                DwarfTaskListProvider.createFreeFollowTask(), Pair.of(99, new ScheduleActivityTask()));
        // Pair.of(3, new GiveGiftsToHeroTask(100)),
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createIdleTasks(DwarfProfession profession, float speed) {
        return ImmutableList.of(
                Pair.of(2,
                        new RandomTask(ImmutableList.of(Pair.of(FindEntityTask.create(EntityInit.DWARF, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
                                Pair.of(new FindEntityTask<DwarfEntity, PassiveEntity>(EntityInit.DWARF, 8, PassiveEntity::isReadyToBreed, PassiveEntity::isReadyToBreed,
                                        MemoryModuleType.BREED_TARGET, speed, 2), 1),
                                Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1), Pair.of(new FindWalkTargetTask(speed), 1),
                                Pair.of(new GoTowardsLookTarget(speed, 2), 1), Pair.of(new JumpInBedTask(speed), 1), Pair.of(new WaitTask(30, 60), 1)))),
                Pair.of(3, new FindInteractionTargetTask(EntityType.PLAYER, 4)), Pair.of(3, new HoldTradeOffersTask(400, 1600)),
                Pair.of(3,
                        new CompositeTask(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE,
                                ImmutableList.of(Pair.of(new GatherItemsDwarfTask(), 1)))),
                Pair.of(3, new CompositeTask(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE,
                        ImmutableList.of(Pair.of(new DwarfBreedTask(), 1)))),
                DwarfTaskListProvider.createFreeFollowTask(), Pair.of(99, new ScheduleActivityTask()));
        // Pair.of(3, new GiveGiftsToHeroTask(100)),
    }

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createFightTasks(DwarfProfession profession, float speed, DwarfEntity dwarfEntity) {

    // // return ImmutableList.of(Pair.of(10, ImmutableList.of(new ForgetAttackTargetTask(livingEntity -> !PiglinBrain.isPreferredAttackTarget(piglin, livingEntity)), new
    // // ConditionalTask<DwarfEntity>(PiglinBrain::isHoldingCrossbow, new AttackTask(5, 0.75f)), new RangedApproachTask(1.0f), new MeleeAttackTask(20), new CrossbowAttackTask(), new
    // // ForgetTask<DwarfEntity>(PiglinBrain::getNearestZombifiedPiglin, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET));

    // // return ImmutableList.of(Pair.of(10, ImmutableList.of(new ForgetAttackTargetTask(livingEntity -> !getPreferredTarget(dwarf).filter(livingEntity2 -> livingEntity2 == target).isPresent()),
    // // new ConditionalTask<DwarfEntity>(dwarf -> dwarf.isHolding(Items.CROSSBOW), new AttackTask(5, 0.75f)), new RangedApproachTask(1.0f), new MeleeAttackTask(20), new CrossbowAttackTask()),
    // // MemoryModuleType.ATTACK_TARGET));
    // // return ImmutableList.of(Pair.of(10, ImmutableList.of(new ForgetAttackTargetTask(livingEntity -> !DwarfTaskListProvider.isPreferredAttackTarget(dwarfEntity, (LivingEntity)livingEntity)),
    // // new ConditionalTask<DwarfEntity>(dwarf -> dwarfEntity.isHolding(Items.CROSSBOW)), new AttackTask(5, 0.75f)), new RangedApproachTask(1.0f), new MeleeAttackTask(20), new
    // // CrossbowAttackTask()),
    // // MemoryModuleType.ATTACK_TARGET);#
    // return ImmutableList.of(Pair.of(10, new ForgetAttackTargetTask(livingEntity -> !DwarfTaskListProvider.isPreferredAttackTarget(dwarfEntity, (LivingEntity) livingEntity))),
    // Pair.of(5, new AttackTask(5, 0.75f)), Pair.of(5, new RangedApproachTask(1.0f)), Pair.of(5, new MeleeAttackTask(20)), Pair.of(5, new CrossbowAttackTask()),
    // MemoryModuleType.ATTACK_TARGET);
    // // , new HuntFinishTask()
    // }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createFightTasks(DwarfProfession profession, float speed) {
        return ImmutableList.of(Pair.of(0, new ForgetAttackTargetTask()), Pair.of(1, new AttackTask(5, 0.5f)), Pair.of(2, new RangedApproachTask(1.0f)),
                Pair.of(3, new FindWalkTargetTask(0.5F, 2, 2)), Pair.of(4, new DwarfMeleeAttackTask(20)), Pair.of(5, new CrossbowAttackTask()));
        // , Pair.of(6, new DwarfAttackTask(5, 0.75f))
    }

    // private static boolean isPreferredAttackTarget(DwarfEntity dwarf, LivingEntity target) {
    // return DwarfTaskListProvider.getPreferredTarget(dwarf).filter(livingEntity2 -> livingEntity2 == target).isPresent();
    // }

    // private static Optional<? extends LivingEntity> getPreferredTarget(DwarfEntity dwarf) {
    // Optional<PlayerEntity> optional2;
    // Brain<DwarfEntity> brain = dwarf.getBrain();

    // Optional<LivingEntity> optional = LookTargetUtil.getEntity(dwarf, MemoryModuleType.ANGRY_AT);
    // if (optional.isPresent() && Sensor.testAttackableTargetPredicateIgnoreVisibility(dwarf, optional.get())) {
    // return optional;
    // }
    // if (brain.hasMemoryModule(MemoryModuleType.UNIVERSAL_ANGER) && (optional2 = brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)).isPresent()) {
    // return optional2;
    // }
    // // optional2 = (Optional<MobEntity>) brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
    // // if (optional2.isPresent()) {
    // // return optional2;
    // // }
    // Optional<PlayerEntity> optional3 = brain.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
    // if (optional3.isPresent() && Sensor.testAttackableTargetPredicate(dwarf, optional3.get())) {
    // return optional3;
    // }
    // return Optional.empty();
    // }

    // private static void addFightActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
    // brain.setTaskList(Activity.FIGHT, 10, ImmutableList.of(new ForgetAttackTargetTask(livingEntity -> !PiglinBrain.isPreferredAttackTarget(piglin, livingEntity)), new
    // ConditionalTask<PiglinEntity>(PiglinBrain::isHoldingCrossbow, new AttackTask(5, 0.75f)), new RangedApproachTask(1.0f), new MeleeAttackTask(20), new CrossbowAttackTask(), new HuntFinishTask(),
    // new ForgetTask<PiglinEntity>(PiglinBrain::getNearestZombifiedPiglin, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    // }

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPanicTasks(DwarfProfession profession, float speed) {
    // float f = speed * 1.5f;
    // return ImmutableList.of(Pair.of(0, new StopPanickingTask()), Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, f, 6, false)),
    // Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.HURT_BY_ENTITY, f, 6, false)), Pair.of(3, new FindWalkTargetTask(f, 2, 2)),
    // DwarfTaskListProvider.createBusyFollowTask());
    // }

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPreRaidTasks(DwarfProfession profession, float speed) {
    // return ImmutableList.of(Pair.of(0, new RingBellTask()),
    // Pair.of(0, new RandomTask(
    // ImmutableList.of(Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, speed * 1.5f, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(speed * 1.5f), 2)))),
    // DwarfTaskListProvider.createBusyFollowTask(), Pair.of(99, new EndRaidTask()));
    // }

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createRaidTasks(DwarfProfession profession, float speed) {
    // return ImmutableList.of(Pair.of(0, new RandomTask(ImmutableList.of(Pair.of(new SeekSkyAfterRaidWinTask(speed), 5), Pair.of(new RunAroundAfterRaidTask(speed * 1.1f), 2)))),
    // Pair.of(0, new CelebrateRaidWinTask(600, 600)), Pair.of(2, new HideInHomeDuringRaidTask(24, speed * 1.4f)), DwarfTaskListProvider.createBusyFollowTask(),
    // Pair.of(99, new EndRaidTask()));
    // }

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createHideTasks(DwarfProfession profession, float speed) {
    // int i = 2;
    // return ImmutableList.of(Pair.of(0, new ForgetBellRingTask(15, 3)), Pair.of(1, new HideInHomeTask(32, speed * 1.25f, 2)), DwarfTaskListProvider.createBusyFollowTask());
    // }

    private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
        return Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(new FollowMobTask(EntityType.CAT, 8.0f), 8), Pair.of(new FollowMobTask(EntityInit.DWARF, 8.0f), 2),
                Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0f), 2), Pair.of(new FollowMobTask(SpawnGroup.CREATURE, 8.0f), 1), Pair.of(new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0f), 1),
                Pair.of(new FollowMobTask(SpawnGroup.AXOLOTLS, 8.0f), 1), Pair.of(new FollowMobTask(SpawnGroup.UNDERGROUND_WATER_CREATURE, 8.0f), 1),
                Pair.of(new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0f), 1), Pair.of(new FollowMobTask(SpawnGroup.MONSTER, 8.0f), 1), Pair.of(new WaitTask(30, 60), 2))));
    }

    private static Pair<Integer, Task<LivingEntity>> createBusyFollowTask() {
        return Pair.of(5,
                new RandomTask(ImmutableList.of(Pair.of(new FollowMobTask(EntityInit.DWARF, 8.0f), 2), Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0f), 2), Pair.of(new WaitTask(30, 60), 8))));
    }

    public static Stream<DwarfEntity> streamSeenDwarfs(DwarfEntity dwarfEntity, Predicate<DwarfEntity> filter) {
        return dwarfEntity.getBrain().getOptionalMemory(MemoryModuleType.MOBS).map(list -> list.stream().filter(entity -> entity instanceof DwarfEntity && entity != dwarfEntity)
                .map(livingEntity -> (DwarfEntity) livingEntity).filter(LivingEntity::isAlive).filter(filter)).orElseGet(Stream::empty);
    }
}
