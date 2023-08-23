package io.github.GrassyDev.pvzmod.registry.entity.projectileentity.zombies.zpg;

import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.PvZSounds;
import io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.PlantEntity;
import io.github.GrassyDev.pvzmod.registry.entity.projectileentity.PvZProjectileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static io.github.GrassyDev.pvzmod.PvZCubed.PLANT_LOCATION;
import static io.github.GrassyDev.pvzmod.PvZCubed.PVZCONFIG;

public class ZPGEntity extends PvZProjectileEntity implements IAnimatable {

	private String controllerName = "projectilecontroller";
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public int maxAge = 60;

	@Override
	public void registerControllers(AnimationData animationData) {
		AnimationController controller = new AnimationController(this, controllerName, 0, this::predicate);

		animationData.addAnimationController(controller);
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	private <P extends IAnimatable > PlayState predicate(AnimationEvent<P> event) {
		event.getController().setAnimation(new AnimationBuilder().loop("spike.idle"));
		return PlayState.CONTINUE;
	}

    public static final Identifier PacketID = new Identifier(PvZEntity.ModID, "zpg");

    public ZPGEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
		this.setNoGravity(true);
    }

    public ZPGEntity(World world, LivingEntity owner) {
        super(EntityType.SNOWBALL, owner, world);
    }

    @Environment(EnvType.CLIENT)
    public ZPGEntity(World world, double x, double y, double z, float yaw, float pitch, int interpolation, boolean interpolate, int id, UUID uuid) {
        super(PvZEntity.FIREPEA, world);
		updatePosition(x, y, z);
		updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolation, interpolate);
		setId(id);
		setUuid(uuid);
	}

    public void tick() {
        super.tick();
		HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
		RandomGenerator randomGenerator = this.random;
		boolean bl = false;
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.isOf(Blocks.NETHER_PORTAL)) {
				this.setInNetherPortal(blockPos);
				bl = true;
			} else if (blockState.isOf(Blocks.END_GATEWAY)) {
				BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
				if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
					EndGatewayBlockEntity.tryTeleportingEntity(this.world, blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
				}

				bl = true;
			}
		}

		if (hitResult.getType() != HitResult.Type.MISS && !bl) {
			this.onCollision(hitResult);
		}

        if (!this.world.isClient && this.isWet()) {
            this.world.sendEntityStatus(this, (byte) 3);
            this.remove(RemovalReason.DISCARDED);
        }

        if (!this.world.isClient && this.age >= maxAge) {
            this.world.sendEntityStatus(this, (byte) 3);
            this.remove(RemovalReason.DISCARDED);
        }

		for (int j = 0; j < 1; ++j) {
			double d = (double) MathHelper.nextBetween(randomGenerator, -0.1F, 0.1F);
			double e = (double) MathHelper.nextBetween(randomGenerator, -0.1F, 0.1F);;
			double f = (double) MathHelper.nextBetween(randomGenerator, -0.1F, 0.1F);;
			this.world.addParticle(ParticleTypes.SMALL_FLAME, this.getX(), this.getY(), this.getZ(), d, e, f);
			this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), d, e * -1, f);
		}
    }

    @Override
    protected Item getDefaultItem() {
        return null;
    }


	@Override
	public void hitEntities() {
		super.hitEntities();
		boolean hit = false;
		Iterator var9 = hitEntities.iterator();
		while (true) {
			Entity entity;
			do {
				if (!var9.hasNext()) {
					return;
				}

				entity = (Entity) var9.next();
			} while (entity == this.getOwner());
			if (!world.isClient && (entity instanceof GolemEntity || entity instanceof VillagerEntity || entity instanceof PlayerEntity) && !(entity instanceof PlantEntity plantEntity && (plantEntity.getLowProfile() || PLANT_LOCATION.get(plantEntity.getType()).orElse("ground").equals("flying")))) {
				entity.playSound(PvZSounds.CHERRYBOMBEXPLOSIONEVENT, 0.2F, 1F);
				float damage = PVZCONFIG.nestedProjDMG.zpgDMG();
				entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), damage);
				List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(5.0));
				this.world.sendEntityStatus(this, (byte) 3);
				this.remove(RemovalReason.DISCARDED);
				Iterator var10 = list.iterator();
				while (true) {
					LivingEntity livingEntity;
					do {
						do {
							if (!var10.hasNext()) {
								return;
							}

							livingEntity = (LivingEntity) var10.next();
						} while (livingEntity == this.getOwner());
					} while (entity.squaredDistanceTo(livingEntity) > 3);

					if (!world.isClient && (livingEntity instanceof GolemEntity || livingEntity instanceof VillagerEntity || livingEntity instanceof PlayerEntity)) {
						if (livingEntity != entity) {
							float damageSplash = PVZCONFIG.nestedProjDMG.zpgDMG();
							livingEntity.damage(DamageSource.thrownProjectile(this, this.getOwner()), damageSplash);
						}
						this.world.sendEntityStatus(this, (byte) 3);
						this.remove(RemovalReason.DISCARDED);
					}
				}
			}
		}
	}

        @Environment(EnvType.CLIENT)
    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.FLAME : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }


    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
		if (status != 2 && status != 60){
			super.handleStatus(status);
		}
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for(int i = 0; i < 8; ++i) {
				double vx = this.random.nextDouble() / 2 * this.random.range(-1, 1);
				double vy = this.random.nextDouble() / 2 * this.random.range(-1, 1);
				double vz = this.random.nextDouble() / 2 * this.random.range(-1, 1);
                this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), vx, vy, vz);
            }

			for (int j = 0; j < 16; ++j) {

				double d = this.random.nextDouble() / 2 * this.random.range(-1, 1);
				double e = this.random.nextDouble() / 2 * this.random.range(-1, 1);
				double f = this.random.nextDouble() / 2 * this.random.range(-1, 1);
				this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), d, e, f);
			}
        }

    }
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }
    }


    public boolean collides() {
        return false;
    }
}
