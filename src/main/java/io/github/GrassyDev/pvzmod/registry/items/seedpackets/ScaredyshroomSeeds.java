package io.github.GrassyDev.pvzmod.registry.items.seedpackets;

import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz1.night.scaredyshroom.ScaredyshroomEntity;
import io.github.GrassyDev.pvzmod.registry.entity.variants.plants.ScaredyshroomVariants;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScaredyshroomSeeds extends Item implements FabricItem {
	public static int cooldown = 100;
    public ScaredyshroomSeeds(Settings settings) {
        super(settings);
    }

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}


	public static final String COOL_KEY = "Cooldown";

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		if (entity instanceof PlayerEntity player){
			if (player.getItemCooldownManager().getCooldownProgress(this, 0) > 0.0f){
				nbtCompound.putFloat("Cooldown", player.getItemCooldownManager().getCooldownProgress(this, 0));
			}
			else if (nbtCompound.getFloat("Cooldown") > 0.1f && player.getItemCooldownManager().getCooldownProgress(this, 0) <= 0.0f){
				float progress = nbtCompound.getFloat("Cooldown");
				player.getItemCooldownManager().set(this, (int) Math.floor(cooldown * progress));
			}
		}
	}


	//Credits to Patchouli for the tooltip code!
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);

		tooltip.add(Text.translatable("item.pvzmod.seed_packet.ailment.family")
				.formatted(Formatting.DARK_PURPLE));

		tooltip.add(Text.translatable("item.pvzmod.seed_packet.nocturnal.tooltip")
				.formatted(Formatting.UNDERLINE));

		tooltip.add(Text.translatable("item.pvzmod.scaredyshroom_seed_packet.flavour")
				.formatted(Formatting.DARK_GRAY));
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
            Box box = PvZEntity.SCAREDYSHROOM.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
             if (world.isSpaceEmpty((Entity)null, box) && world.getOtherEntities((Entity) null, box).isEmpty()) {
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    ScaredyshroomEntity scaredyshroomEntity = (ScaredyshroomEntity) PvZEntity.SCAREDYSHROOM.create(serverWorld, itemStack.getNbt(), (Text) null, context.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
                    if (scaredyshroomEntity == null) {
                        return ActionResult.FAIL;
                    }

                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    scaredyshroomEntity.refreshPositionAndAngles(scaredyshroomEntity.getX(), scaredyshroomEntity.getY(), scaredyshroomEntity.getZ(), f, 0.0F);
					double random = Math.random();
					if (random <= 0.125) {
						scaredyshroomEntity.setVariant(ScaredyshroomVariants.DEMIBOY);
					}
					else if (random <= 0.25) {
						scaredyshroomEntity.setVariant(ScaredyshroomVariants.LINK);
					}
					else {
						scaredyshroomEntity.setVariant(ScaredyshroomVariants.DEFAULT);
					}
                    world.spawnEntity(scaredyshroomEntity);
                    world.playSound((PlayerEntity) null, scaredyshroomEntity.getX(), scaredyshroomEntity.getY(), scaredyshroomEntity.getZ(), PvZCubed.PLANTPLANTEDEVENT, SoundCategory.BLOCKS, 0.6f, 0.8F);
                }

				 PlayerEntity user = context.getPlayer();
				 if (!user.getAbilities().creativeMode) {
					 itemStack.decrement(1);
					 user.getItemCooldownManager().set(this, cooldown);
				 }
                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.FAIL;
            }
        }
    }
}
