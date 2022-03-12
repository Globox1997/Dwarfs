package net.dwarfs.item;

import net.dwarfs.entity.BeerEntity;
import net.dwarfs.init.ItemInit;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BeerItem extends Item {

    public BeerItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!user.isSneaking()) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_LINGERING_POTION_THROW, SoundCategory.PLAYERS, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            if (!world.isClient) {
                BeerEntity beerEntity = new BeerEntity(world, user);
                beerEntity.setItem(itemStack);
                beerEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);
                world.spawnEntity(beerEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
        } else
            return ItemUsage.consumeHeldItem(world, user, hand);
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) playerEntity, stack);
        }
        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 2));
        }
        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }
        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(ItemInit.JUG_ITEM);
            }
            if (playerEntity != null) {
                playerEntity.getInventory().insertStack(new ItemStack(ItemInit.JUG_ITEM));
            }
        }
        world.emitGameEvent(user, GameEvent.DRINKING_FINISH, user.getCameraBlockPos());
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

}
