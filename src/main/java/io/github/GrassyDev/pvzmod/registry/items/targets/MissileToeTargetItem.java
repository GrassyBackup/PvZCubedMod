package io.github.GrassyDev.pvzmod.registry.items.targets;

import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.entity.environment.target.missiletoe.MissileToeTarget;
import io.github.GrassyDev.pvzmod.registry.items.seedpackets.SeedItem;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MissileToeTargetItem extends SeedItem implements FabricItem {
    public boolean used;

    public MissileToeTargetItem(Settings settings) {
        super(settings);
		targetID = 0;
    }

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}


	public int targetID = 0;

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if (targetID == 0){
			if (entity instanceof PlayerEntity player){
				player.getInventory().removeStack(slot);
			}
		}
	}

	//Credits to Patchouli for the tooltip code!
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);

		tooltip.add(Text.translatable("item.pvzmod.missiletoetarget")
				.formatted(Formatting.LIGHT_PURPLE));
	}

    public ActionResult useOnBlock(ItemUsageContext context) {
        Direction direction = context.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        }
        else if (direction == Direction.SOUTH) {
            return ActionResult.FAIL;
        }
        else if (direction == Direction.EAST) {
            return ActionResult.FAIL;
        }
        else if (direction == Direction.NORTH) {
            return ActionResult.FAIL;
        }
        else if (direction == Direction.WEST) {
            return ActionResult.FAIL;
        }
        else {
			World world = context.getWorld();
			ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
			BlockPos blockPos = itemPlacementContext.getBlockPos();
			ItemStack itemStack = context.getStack();
			Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
			Box box = PvZEntity.MISSILETOETARGET.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
			if (world.isSpaceEmpty((Entity)null, box) && world instanceof ServerWorld serverWorld) {
				MissileToeTarget targetTile = (MissileToeTarget) PvZEntity.MISSILETOETARGET.create(serverWorld, itemStack.getNbt(), (Text) null, context.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
				float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
				targetTile.refreshPositionAndAngles(targetTile.getX(), targetTile.getY(), targetTile.getZ(), f, 0.0F);
				if (targetID != 0){
					targetTile.setTargetID(targetID);
				}
				world.spawnEntity(targetTile);
				PlayerEntity player = context.getPlayer();
				int slot = player.getInventory().getSlotWithStack(itemStack);
				player.getInventory().removeStack(slot);
				targetID = 0;
				return ActionResult.success(world.isClient);
			}
			else {
				return ActionResult.PASS;
			}
		}
    }

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		World world = user.getWorld();
		BlockPos blockPos = entity.getBlockPos();
		if (world instanceof ServerWorld serverWorld) {
			MissileToeTarget targetTile = PvZEntity.MISSILETOETARGET.create(serverWorld, stack.getNbt(), (Text) null, user, blockPos, SpawnReason.SPAWN_EGG, true, true);
			float f = (float) MathHelper.floor((MathHelper.wrapDegrees(user.getYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
			targetTile.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), f, 0.0F);
			if (targetID != 0){
				targetTile.setTargetID(targetID);
			}
			world.spawnEntity(targetTile);
			int slot = user.getInventory().getSlotWithStack(stack);
			user.getInventory().removeStack(slot);
			targetID = 0;
		}
		return ActionResult.SUCCESS;
	}
}
