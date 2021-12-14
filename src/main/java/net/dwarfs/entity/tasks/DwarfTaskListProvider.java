package net.dwarfs.entity.tasks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.dwarfs.entity.DwarfEntity;
import net.dwarfs.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.CelebrateRaidWinTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.EndRaidTask;
import net.minecraft.entity.ai.brain.task.FarmerWorkTask;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.FindWalkTargetTask;
import net.minecraft.entity.ai.brain.task.FollowCustomerTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetBellRingTask;
import net.minecraft.entity.ai.brain.task.ForgetCompletedPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.GiveGiftsToHeroTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.GoToRememberedPositionTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.HideInHomeDuringRaidTask;
import net.minecraft.entity.ai.brain.task.HideInHomeTask;
import net.minecraft.entity.ai.brain.task.HideWhenBellRingsTask;
import net.minecraft.entity.ai.brain.task.JumpInBedTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RingBellTask;
import net.minecraft.entity.ai.brain.task.RunAroundAfterRaidTask;
import net.minecraft.entity.ai.brain.task.ScheduleActivityTask;
import net.minecraft.entity.ai.brain.task.SeekSkyAfterRaidWinTask;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.entity.ai.brain.task.StartRaidTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StopPanickingTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerWorkTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WakeUpTask;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.brain.task.WanderIndoorsTask;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class DwarfTaskListProvider {
    // private static final float field_30189 = 0.4f;

    // Pair<Integer, ? extends Task<? super DwarfEntity>>
    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createCoreTasks(VillagerProfession profession, float speed) {

        return ImmutableList.of(Pair.of(0, new StayAboveWaterTask(0.8f)), Pair.of(0, new OpenDoorsTask()), Pair.of(0, new LookAroundTask(45, 90)), Pair.of(0, new WakeUpTask()),
                Pair.of(0, new StartRaidTask()), Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE)),
                Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.POTENTIAL_JOB_SITE)), Pair.of(1, new WanderAroundTask()),
                Pair.of(2, new WorkStationCompetitionTask(profession)),

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

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createWorkTasks(VillagerProfession profession, float speed) {
        VillagerWorkTask villagerWorkTask = profession == VillagerProfession.FARMER ? new FarmerWorkTask() : new VillagerWorkTask();
        return ImmutableList.of(DwarfTaskListProvider.createBusyFollowTask(),
                Pair.of(5,
                        new RandomTask(ImmutableList.of(Pair.of(villagerWorkTask, 7), Pair.of(new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 0.4f, 4), 2),
                                Pair.of(new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 0.4f, 1, 10), 5),
                                Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5),
                                Pair.of(new FarmerDwarfTask(), profession == VillagerProfession.FARMER ? 2 : 5), Pair.of(new BoneMealTask(), profession == VillagerProfession.FARMER ? 4 : 7)))),
                Pair.of(10, new HoldTradeOffersTask(400, 1600)), Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
                Pair.of(2, new DwarfWalkTowardsTask(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)), Pair.of(99, new ScheduleActivityTask()));
        // , Pair.of(3, new GiveGiftsToHeroTask(100))
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPlayTasks(float speed) {
        return ImmutableList.of(Pair.of(0, new WanderAroundTask(80, 120)), DwarfTaskListProvider.createFreeFollowTask(), Pair.of(5, new PlayWithDwarfBabiesTask()),
                Pair.of(5,
                        new RandomTask(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT),
                                ImmutableList.of(Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
                                        Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1), Pair.of(new FindWalkTargetTask(speed), 1),
                                        Pair.of(new GoTowardsLookTarget(speed, 2), 1), Pair.of(new JumpInBedTask(speed), 2), Pair.of(new WaitTask(20, 40), 2)))),
                Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createRestTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of(Pair.of(2, new DwarfWalkTowardsTask(MemoryModuleType.HOME, speed, 1, 150, 1200)),
                Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)), Pair.of(3, new SleepTask()),
                Pair.of(5,
                        new RandomTask(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(new WalkHomeTask(speed), 1),
                                Pair.of(new WanderIndoorsTask(speed), 4), Pair.of(new GoToPointOfInterestTask(speed, 4), 2), Pair.of(new WaitTask(20, 40), 2)))),
                DwarfTaskListProvider.createBusyFollowTask(), Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createMeetTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of(Pair.of(2, new RandomTask(ImmutableList.of(Pair.of(new GoToIfNearbyTask(MemoryModuleType.MEETING_POINT, 0.4f, 40), 2), Pair.of(new MeetDwarfTask(), 2)))),
                Pair.of(10, new HoldTradeOffersTask(400, 1600)), Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
                Pair.of(2, new DwarfWalkTowardsTask(MemoryModuleType.MEETING_POINT, speed, 6, 100, 200)),
                Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)), Pair.of(3, new CompositeTask(ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of(Pair.of(new GatherItemsDwarfTask(), 1)))),
                DwarfTaskListProvider.createFreeFollowTask(), Pair.of(99, new ScheduleActivityTask()));
        // Pair.of(3, new GiveGiftsToHeroTask(100)),
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createIdleTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of(
                Pair.of(2,
                        new RandomTask(ImmutableList.of(Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
                                Pair.of(new FindEntityTask<DwarfEntity, PassiveEntity>(EntityType.VILLAGER, 8, PassiveEntity::isReadyToBreed, PassiveEntity::isReadyToBreed,
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

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPanicTasks(VillagerProfession profession, float speed) {
    // float f = speed * 1.5f;
    // return ImmutableList.of(Pair.of(0, new StopPanickingTask()), Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, f, 6, false)),
    // Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.HURT_BY_ENTITY, f, 6, false)), Pair.of(3, new FindWalkTargetTask(f, 2, 2)),
    // DwarfTaskListProvider.createBusyFollowTask());
    // }

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPreRaidTasks(VillagerProfession profession, float speed) {
    // return ImmutableList.of(Pair.of(0, new RingBellTask()),
    // Pair.of(0, new RandomTask(
    // ImmutableList.of(Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, speed * 1.5f, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(speed * 1.5f), 2)))),
    // DwarfTaskListProvider.createBusyFollowTask(), Pair.of(99, new EndRaidTask()));
    // }

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createRaidTasks(VillagerProfession profession, float speed) {
    // return ImmutableList.of(Pair.of(0, new RandomTask(ImmutableList.of(Pair.of(new SeekSkyAfterRaidWinTask(speed), 5), Pair.of(new RunAroundAfterRaidTask(speed * 1.1f), 2)))),
    // Pair.of(0, new CelebrateRaidWinTask(600, 600)), Pair.of(2, new HideInHomeDuringRaidTask(24, speed * 1.4f)), DwarfTaskListProvider.createBusyFollowTask(),
    // Pair.of(99, new EndRaidTask()));
    // }

    // public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createHideTasks(VillagerProfession profession, float speed) {
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
