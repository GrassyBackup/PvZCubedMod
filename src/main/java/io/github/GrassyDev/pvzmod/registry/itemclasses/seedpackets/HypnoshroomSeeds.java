package io.github.GrassyDev.pvzmod.registry.itemclasses.seedpackets;

import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.plants.plantentity.hypnoshroom.HypnoshroomEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class HypnoshroomSeeds extends Item {
    public HypnoshroomSeeds(Settings settings) {
        super(settings);
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
            Box box = PvZEntity.HYPNOSHROOM.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
             if (world.isSpaceEmpty((Entity)null, box) && world.getOtherEntities((Entity) null, box).isEmpty()) {
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    HypnoshroomEntity hypnoshroomEntity = (HypnoshroomEntity) PvZEntity.HYPNOSHROOM.create(serverWorld, itemStack.getNbt(), (Text) null, context.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
                    if (hypnoshroomEntity == null) {
                        return ActionResult.FAIL;
                    }

                    serverWorld.spawnEntityAndPassengers(hypnoshroomEntity);
                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    hypnoshroomEntity.refreshPositionAndAngles(hypnoshroomEntity.getX(), hypnoshroomEntity.getY(), hypnoshroomEntity.getZ(), f, 0.0F);
                    world.spawnEntity(hypnoshroomEntity);
                    world.playSound((PlayerEntity) null, hypnoshroomEntity.getX(), hypnoshroomEntity.getY(), hypnoshroomEntity.getZ(), PvZCubed.PLANTPLANTEDEVENT, SoundCategory.BLOCKS, 0.75F, 0.8F);
                }

                itemStack.decrement(1);
                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.FAIL;
            }
        }
    }
}