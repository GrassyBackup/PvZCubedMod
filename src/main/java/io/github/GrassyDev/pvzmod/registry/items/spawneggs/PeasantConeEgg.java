package io.github.GrassyDev.pvzmod.registry.items.spawneggs;

import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.browncoat.darkages.PeasantEntity;
import io.github.GrassyDev.pvzmod.registry.items.seedpackets.SeedItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.GrassyDev.pvzmod.PvZCubed.PVZCONFIG;

public class PeasantConeEgg extends SeedItem {
    public PeasantConeEgg(Settings settings) {
        super(settings);
    }

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);

		tooltip.add(Text.translatable("item.pvzmod.creative")
				.formatted(Formatting.UNDERLINE));
	}

	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		ItemStack itemStack = context.getStack();
		Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
		Box box = PvZEntity.PEASANTCONE.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
		if (world.isSpaceEmpty((Entity)null, box) && world.getOtherEntities((Entity) null, box).isEmpty()) {
			if (world instanceof ServerWorld) {
				ServerWorld serverWorld = (ServerWorld) world;
				PeasantEntity coneheadEntity = (PeasantEntity) PvZEntity.PEASANTCONE.create(serverWorld, itemStack.getNbt(), (Text) null, context.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
				if (coneheadEntity == null) {
					return ActionResult.FAIL;
				}

				float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
				coneheadEntity.refreshPositionAndAngles(coneheadEntity.getX(), coneheadEntity.getY(), coneheadEntity.getZ(), f, 0.0F);
				coneheadEntity.setPersistent();
				((ServerWorld) world).spawnEntityAndPassengers(coneheadEntity);
				world.playSound((PlayerEntity) null, coneheadEntity.getX(), coneheadEntity.getY(), coneheadEntity.getZ(), PvZCubed.ENTITYRISINGEVENT, SoundCategory.BLOCKS, 0.75F, 0.8F);
			}

			if (!PVZCONFIG.nestedSeeds.infiniteSeeds() && !world.getGameRules().getBoolean(PvZCubed.INFINITE_SEEDS)) {
				itemStack.decrement(1);
			};
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.FAIL;
		}
	}
}
