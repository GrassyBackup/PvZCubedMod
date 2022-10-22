package io.github.GrassyDev.pvzmod.registry.plants.projectileentity;

import com.google.common.collect.Maps;
import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.ModItems;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.hypnotizedzombies.hypnotizedentity.HypnoDancingZombieEntity;
import io.github.GrassyDev.pvzmod.registry.hypnotizedzombies.hypnotizedentity.HypnoFlagzombieEntity;
import io.github.GrassyDev.pvzmod.registry.variants.plants.FumeshroomVariants;
import io.github.GrassyDev.pvzmod.registry.zombies.zombieentity.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Iterator;
import java.util.UUID;

public class FumeEntity extends ThrownItemEntity implements IAnimatable {

	private String controllerName = "projectilecontroller";
	public AnimationFactory factory = new AnimationFactory(this);

	public static final Identifier PacketID = new Identifier(PvZEntity.ModID, "fume");
	@Override
	public void registerControllers(AnimationData animationData) {
		AnimationController controller = new AnimationController(this, controllerName, 0, this::predicate);

		animationData.addAnimationController(controller);
	}

	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("fume.idle", true));
		return PlayState.CONTINUE;
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

    public FumeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FumeEntity(World world, LivingEntity owner) {
        super(EntityType.SNOWBALL, owner, world);
    }

    @Environment(EnvType.CLIENT)
    public FumeEntity(World world, double x, double y, double z, float yaw, float pitch, int interpolation, boolean interpolate, int id, UUID uuid) {
        super(PvZEntity.FUME, world);
		updatePosition(x, y, z);
		updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolation, interpolate);
		setId(id);
		setUuid(uuid);
	}

    public void tick() {
		super.tick();
		if (!this.world.isClient && this.isInsideWaterOrBubbleColumn()) {
			this.world.sendEntityStatus(this, (byte) 3);
			this.remove(RemovalReason.DISCARDED);
		}

		if (!this.world.isClient && this.age == 7) {
			this.world.sendEntityStatus(this, (byte) 3);
			this.remove(RemovalReason.DISCARDED);
		}

		double d = (double)(180 & 255) / 255.0;
		double e = (double)(30 & 255) / 255.0;
		double f = (double)(200 & 255) / 255.0;

		for(int j = 0; j < 8; ++j) {
			this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
		}
	}

    @Override
    protected Item getDefaultItem() {
        return null;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof ScreendoorEntity) {
            float sound = this.random.nextFloat();
            entity.playSound(PvZCubed.BUCKETHITEVENT, 0.25F, 1F);
            entity.playSound(PvZCubed.PEAHITEVENT, 0.25F, 1F);
            entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 106);
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.WITHER, 60, 6)));
        }
        else if (entity instanceof NewspaperEntity) {
            float sound = this.random.nextFloat();
            entity.playSound(PvZCubed.PEAHITEVENT, 0.25F, 1F);
            entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 19);
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.WITHER, 60, 6)));
        }
        else if ((entity instanceof BucketheadEntity) ||
                (entity instanceof BerserkerEntity)) {
            float sound = this.random.nextFloat();
            entity.playSound(PvZCubed.BUCKETHITEVENT, 0.25F, 1F);
            entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 16);
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.WITHER, 60, 6)));
        }
        else if ((entity instanceof ConeheadEntity) ||
                (entity instanceof FootballEntity) ||
                (entity instanceof BackupDancerEntity)) {
            float sound = this.random.nextFloat();
            entity.playSound(PvZCubed.CONEHITEVENT, 0.25F, 1F);
            entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 16);
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.WITHER, 60, 6)));
        }
        else if (entity instanceof Monster && !(entity instanceof HypnoDancingZombieEntity) &&
                !(entity instanceof HypnoFlagzombieEntity)) {
            float sound = this.random.nextFloat();
            entity.playSound(PvZCubed.PEAHITEVENT, 0.25F, 1F);
            entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 16);
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.WITHER, 60, 6)));
        }
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 3) {

			double d = (double) (180 & 255) / 255.0;
			double e = (double) (30 & 255) / 255.0;
			double f = (double) (200 & 255) / 255.0;

			for (int j = 0; j < 8; ++j) {
				this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
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
