package io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity;

import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.PvZSounds;
import io.github.GrassyDev.pvzmod.registry.entity.environment.TileEntity;
import io.github.GrassyDev.pvzmod.registry.entity.environment.oiltile.OilTile;
import io.github.GrassyDev.pvzmod.registry.entity.environment.snowtile.SnowTile;
import io.github.GrassyDev.pvzmod.registry.entity.gravestones.GraveEntity;
import io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz1.pool.lilypad.LilyPadEntity;
import io.github.GrassyDev.pvzmod.registry.entity.projectileentity.armor.MetalHelmetProjEntity;
import io.github.GrassyDev.pvzmod.registry.entity.variants.projectiles.MetalHelmetVariants;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz1.snorkel.SnorkelEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieprops.metallichelmet.MetalHelmetEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieprops.metallicshield.MetalShieldEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombietypes.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.github.GrassyDev.pvzmod.PvZCubed.*;

public abstract class PlantEntity extends GolemEntity {

	public boolean onWater;

	public boolean naturalSpawn;

	public boolean isBurst;

	protected boolean dryLand;

	protected int tickDelay;

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return false;
	}

	protected PlantEntity(EntityType<? extends GolemEntity> entityType, World world) {
		super(entityType, world);
	}

	public void rideLilyPad(LivingEntity livingEntity){
		this.refreshPositionAndAngles(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.bodyYaw, 0.0F);
		this.startRiding(livingEntity);
	}

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(DATA_ID_ASLEEP, false);
		this.dataTracker.startTracking(DATA_ALTFIRE, false);
		this.dataTracker.startTracking(DATA_ID_LOWPROF, false);
		this.dataTracker.startTracking(DATA_ID_FIREIMMUNE, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);
		tag.putBoolean("Asleep", this.getIsAsleep());
		tag.putBoolean("AltFire", this.getIsAltFire());
		tag.putBoolean("lowProf", this.getLowProfile());
		tag.putBoolean("fireImmune", this.getFireImmune());
	}

	public void readCustomDataFromNbt(NbtCompound tag) {
		super.readCustomDataFromNbt(tag);
		this.dataTracker.set(DATA_ID_ASLEEP, tag.getBoolean("Asleep"));
		this.dataTracker.set(DATA_ALTFIRE, tag.getBoolean("AltFire"));
		this.dataTracker.set(DATA_ID_LOWPROF, tag.getBoolean("lowProf"));
		this.dataTracker.set(DATA_ID_FIREIMMUNE, tag.getBoolean("fireImmune"));
	}

	/** /~*~//~*VARIANTS*~//~*~/ **/


	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
								 SpawnReason spawnReason, @Nullable EntityData entityData,
								 @Nullable NbtCompound entityNbt) {
		if (PLANT_LOCATION.get(this.getType()).orElse("normal").equals("ground")){
			this.setLowprof(LowProf.TRUE);
		}
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}


	//Low Profile Tag

	protected static final TrackedData<Boolean> DATA_ID_LOWPROF =
			DataTracker.registerData(PlantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


	public enum LowProf {
		FALSE(false),
		TRUE(true);

		LowProf(boolean id) {
			this.id = id;
		}

		private final boolean id;

		public boolean getId() {
			return this.id;
		}
	}

	public Boolean getLowProfile() {
		return this.dataTracker.get(DATA_ID_LOWPROF);
	}

	public void setLowprof(PlantEntity.LowProf lowprof) {
		this.dataTracker.set(DATA_ID_LOWPROF, lowprof.getId());
	}

	// Fire Immune

	protected static final TrackedData<Boolean> DATA_ID_FIREIMMUNE =
			DataTracker.registerData(PlantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public enum FireImmune {
		FALSE(false),
		TRUE(true);

		FireImmune(boolean id) {
			this.id = id;
		}

		private final boolean id;

		public boolean getId() {
			return this.id;
		}
	}

	public Boolean getFireImmune() {
		return this.dataTracker.get(DATA_ID_FIREIMMUNE);
	}

	public void setFireImmune(PlantEntity.FireImmune fireImmune) {
		this.dataTracker.set(DATA_ID_FIREIMMUNE, fireImmune.getId());
	}


	protected static final TrackedData<Boolean> DATA_ID_ASLEEP =
			DataTracker.registerData(PlantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public enum IsAsleep {
		FALSE(false),
		TRUE(true);

		IsAsleep(boolean id) {
			this.id = id;
		}

		private final boolean id;

		public boolean getId() {
			return this.id;
		}
	}

	public Boolean getIsAsleep() {
		return this.dataTracker.get(DATA_ID_ASLEEP);
	}

	public void setIsAsleep(PlantEntity.IsAsleep asleep) {
		this.dataTracker.set(DATA_ID_ASLEEP, asleep.getId());
	}


	protected static final TrackedData<Boolean> DATA_ALTFIRE =
			DataTracker.registerData(PlantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public enum AltFire {
		FALSE(false),
		TRUE(true);

		AltFire(boolean id) {
			this.id = id;
		}

		private final boolean id;

		public boolean getId() {
			return this.id;
		}
	}

	public Boolean getIsAltFire() {
		return this.dataTracker.get(DATA_ALTFIRE);
	}

	public void setAltfire(PlantEntity.AltFire fireImmune) {
		this.dataTracker.set(DATA_ALTFIRE, fireImmune.getId());
	}

	/** ----------------------------------------------------------------------- **/

	public boolean targetStrength;
	public boolean lobbedTarget;
	public boolean targetPoison;
	public boolean targetIce;
	public boolean targetHelmet;
	public boolean magnetshroom;
	public boolean magnetoshroom;
	public boolean targetNoHelmet;
	public boolean targetChilled;
	public boolean illuminate;
	public boolean targetMedium;
	public boolean targetNotCovered;
	public boolean targetNotObstacle;
	public boolean noBiggie;

	protected void targetZombies(Vec3d pos, int yDiff, boolean canHitSnorkel, boolean canHitFlying, boolean canHitStealth){
		List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, PvZEntity.PEASHOOTER.getDimensions().getBoxAt(this.getPos()).expand(this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) + 1));
		int zombieStrength = 0;
		int prioritizedStrength = 0;
		Vec3d prevZombiePosition = Vec3d.ZERO;
		boolean isIced;
		boolean isPoisoned;
		boolean prevIced = false;
		boolean hasHelmet = false;
		boolean hasShield = false;
		boolean prevHelmet = false;
		boolean hasMetalGear = false;
		boolean tooHeavy = true;
		LivingEntity targeted = null;
		LivingEntity prioritizedTarget = null;
		if (!this.world.isClient()) {
			for (LivingEntity hostileEntity : list) {
				if (hostileEntity.isAlive()) {
					if (hostileEntity instanceof Monster && !(hostileEntity instanceof GraveEntity graveEntity && graveEntity.decorative)) {
						if (illuminate && hostileEntity instanceof GeneralPvZombieEntity generalPvZombieEntity && generalPvZombieEntity.isStealth() && hostileEntity.squaredDistanceTo(this) < 36) {
							if (PLANT_TYPE.get(this.getType()).orElse("appease").equals("pepper") && this.isWet()) {

							} else {
								generalPvZombieEntity.setStealthTag(GeneralPvZombieEntity.Stealth.FALSE);
							}
						}
						if (!(hostileEntity instanceof ZombiePropEntity && !(hostileEntity instanceof ZombieObstacleEntity))) {
							if (hasHelmet) {
								prevHelmet = true;
								hasHelmet = false;
							}
							if (hasShield) {
								prevHelmet = true;
								hasShield = false;
							}
							for (Entity zombiePropEntity : hostileEntity.getPassengerList()) {
								if (zombiePropEntity instanceof ZombiePropEntity && !(zombiePropEntity instanceof ZombieShieldEntity)) {
									hasHelmet = true;
								}
								if (zombiePropEntity instanceof ZombieShieldEntity) {
									hasShield = true;
								}
								if (zombiePropEntity instanceof MetalHelmetEntity || zombiePropEntity instanceof MetalShieldEntity) {
									hasMetalGear = true;
								}
								if (zombiePropEntity instanceof ZombiePropEntity zombiePropEntity1 && !zombiePropEntity1.isHeavy) {
									tooHeavy = false;
								}
							}
							if (this.noBiggie && (ZOMBIE_SIZE.get(hostileEntity.getType()).orElse("medium").equals("big") || ZOMBIE_SIZE.get(hostileEntity.getType()).orElse("medium").equals("gargantuar"))) {

							} else if (magnetshroom) {
								for (Entity zombiePropEntity : hostileEntity.getPassengerList()) {
									if ((zombiePropEntity instanceof MetalHelmetEntity || zombiePropEntity instanceof MetalShieldEntity) &&
											zombiePropEntity instanceof ZombiePropEntity zombiePropEntity1 && !zombiePropEntity1.isHeavy) {
										if (hostileEntity.squaredDistanceTo(pos) <= Math.pow(this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE), 2) &&
												(hostileEntity.getY() < (this.getY() + yDiff) && hostileEntity.getY() > (this.getY() - yDiff)) && hostileEntity.isAlive()) {
											if (hostileEntity instanceof GeneralPvZombieEntity generalPvZombieEntity &&
													!(generalPvZombieEntity.getHypno())) {
												int currentStrength = ZOMBIE_STRENGTH.get(generalPvZombieEntity.getType()).orElse(0);
												if (zombieStrength < currentStrength) {
													zombieStrength = currentStrength;
													prevZombiePosition = hostileEntity.getPos();
													targeted = hostileEntity;
													prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
												}
											}
										}
									}
								}
							} else if (magnetoshroom) {
								for (Entity zombiePropEntity : hostileEntity.getPassengerList()) {
									if ((zombiePropEntity instanceof MetalHelmetEntity || zombiePropEntity instanceof MetalShieldEntity)) {
										if (hostileEntity.squaredDistanceTo(pos) <= Math.pow(this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE), 2) &&
												(hostileEntity.getY() < (this.getY() + yDiff) && hostileEntity.getY() > (this.getY() - yDiff)) && hostileEntity.isAlive()) {
											if (hostileEntity instanceof GeneralPvZombieEntity generalPvZombieEntity &&
													!(generalPvZombieEntity.getHypno())) {
												int currentStrength = ZOMBIE_STRENGTH.get(generalPvZombieEntity.getType()).orElse(0);
												if (zombieStrength < currentStrength) {
													zombieStrength = currentStrength;
													prevZombiePosition = hostileEntity.getPos();
													targeted = hostileEntity;
													prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
												}
											}
										}
									}
								}
							} else if (hostileEntity.squaredDistanceTo(pos) <= Math.pow(this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE), 2) &&
									(hostileEntity.getY() < (this.getY() + yDiff) && hostileEntity.getY() > (this.getY() - yDiff)) && hostileEntity.isAlive()) {
								if (hostileEntity instanceof GeneralPvZombieEntity generalPvZombieEntity &&
										!(generalPvZombieEntity.getHypno())) {
									int currentStrength = ZOMBIE_STRENGTH.get(generalPvZombieEntity.getType()).orElse(0);
									if (!(!ZOMBIE_SIZE.get(hostileEntity.getType()).orElse("medium").equals("medium") && targetMedium) &&
											!(generalPvZombieEntity.isCovered() && targetNotCovered) &&
											!(generalPvZombieEntity instanceof ZombieVehicleEntity && targetNotCovered) &&
											!((generalPvZombieEntity instanceof ZombieObstacleEntity && !(generalPvZombieEntity instanceof ZombieRiderEntity zombieRiderEntity && !(zombieRiderEntity.hasVehicle()))) && targetNotObstacle)) {
										isIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
										isPoisoned = hostileEntity.hasStatusEffect(PvZCubed.PVZPOISON);
										if (zombieStrength < currentStrength && this.targetStrength) {
											if (canHitFlying) {
												if (generalPvZombieEntity.hasPassengers() && generalPvZombieEntity.getFirstPassenger() instanceof ZombieRiderEntity zombieRiderEntity){
													zombieStrength = currentStrength;
													prevZombiePosition = zombieRiderEntity.getPos();
													targeted = zombieRiderEntity;
													prevIced = zombieRiderEntity.hasStatusEffect(PvZCubed.ICE) || zombieRiderEntity.hasStatusEffect(PvZCubed.FROZEN);
												}
												else if (generalPvZombieEntity.isFlying()) {
													zombieStrength = currentStrength;
													prevZombiePosition = hostileEntity.getPos();
													targeted = hostileEntity;
													prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
												}
											} else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													zombieStrength = currentStrength;
													prevZombiePosition = hostileEntity.getPos();
													targeted = hostileEntity;
													prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity) {
													zombieStrength = currentStrength;
													prevZombiePosition = hostileEntity.getPos();
													targeted = hostileEntity;
													prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													}
												}
											} else if (canHitFlying) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													zombieStrength = currentStrength;
													prevZombiePosition = hostileEntity.getPos();
													targeted = hostileEntity;
													prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity) {
													zombieStrength = currentStrength;
													prevZombiePosition = hostileEntity.getPos();
													targeted = hostileEntity;
													prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													}
												}
											}
										} else if ((zombieStrength == currentStrength || !this.targetStrength) &&
												this.squaredDistanceTo(prevZombiePosition) > this.squaredDistanceTo(hostileEntity.getPos())) {
											if (!(targetChilled && prevIced && !isIced)) {
												if (canHitFlying) {
													if (generalPvZombieEntity.hasPassengers() && generalPvZombieEntity.getFirstPassenger() instanceof ZombieRiderEntity zombieRiderEntity){
														zombieStrength = currentStrength;
														prevZombiePosition = zombieRiderEntity.getPos();
														targeted = zombieRiderEntity;
														prevIced = zombieRiderEntity.hasStatusEffect(PvZCubed.ICE) || zombieRiderEntity.hasStatusEffect(PvZCubed.FROZEN);
													}
													else if (generalPvZombieEntity.isFlying()) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													}
												} else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
													if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
															!(generalPvZombieEntity instanceof SnorkelEntity)) {
														if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
																generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
															zombieStrength = currentStrength;
															prevZombiePosition = hostileEntity.getPos();
															targeted = hostileEntity;
															prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
														} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
															zombieStrength = currentStrength;
															prevZombiePosition = hostileEntity.getPos();
															targeted = hostileEntity;
															prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
														} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
															zombieStrength = currentStrength;
															prevZombiePosition = hostileEntity.getPos();
															targeted = hostileEntity;
															prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
														}
													}
												} else if (canHitFlying) {
													if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
														zombieStrength = currentStrength;
														prevZombiePosition = hostileEntity.getPos();
														targeted = hostileEntity;
														prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
													} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
															!(generalPvZombieEntity instanceof SnorkelEntity)) {
														if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
																generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
															zombieStrength = currentStrength;
															prevZombiePosition = hostileEntity.getPos();
															targeted = hostileEntity;
															prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
														} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
															zombieStrength = currentStrength;
															prevZombiePosition = hostileEntity.getPos();
															targeted = hostileEntity;
															prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
														} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
															zombieStrength = currentStrength;
															prevZombiePosition = hostileEntity.getPos();
															targeted = hostileEntity;
															prevIced = hostileEntity.hasStatusEffect(PvZCubed.ICE) || hostileEntity.hasStatusEffect(PvZCubed.FROZEN);
														}
													}
												}
											}
										}
										if (prioritizedTarget != null && lobbedTarget && this.squaredDistanceTo(prioritizedTarget) > this.squaredDistanceTo(hostileEntity.getPos())) {
											if (lobbedTarget && hasShield) {
												if (canHitFlying && generalPvZombieEntity.isFlying()) {
													prioritizedTarget = hostileEntity;
												} else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
													if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
														prioritizedTarget = hostileEntity;
													} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
															!(generalPvZombieEntity instanceof SnorkelEntity)) {
														if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
																generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
															prioritizedTarget = hostileEntity;
														} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
															prioritizedTarget = hostileEntity;
														} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
															prioritizedTarget = hostileEntity;
														}
													}
												} else if (canHitFlying) {
													if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
														prioritizedTarget = hostileEntity;
													} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
															!(generalPvZombieEntity instanceof SnorkelEntity)) {
														if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
																generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
															prioritizedTarget = hostileEntity;
														} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
															prioritizedTarget = hostileEntity;
														} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
															prioritizedTarget = hostileEntity;
														}
													}
												}
											}
										} else if (lobbedTarget && hasShield) {
											if (canHitFlying) {
												if (generalPvZombieEntity.hasPassengers() && generalPvZombieEntity.getFirstPassenger() instanceof ZombieRiderEntity zombieRiderEntity){
													prioritizedTarget = zombieRiderEntity;
												}
												else if (generalPvZombieEntity.isFlying()) {
													prioritizedTarget = hostileEntity;
												}
											} else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													}
												}
											} else if (canHitFlying) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													}
												}
											}
										}
										if (prioritizedTarget != null && targetIce && this.squaredDistanceTo(prioritizedTarget) > this.squaredDistanceTo(hostileEntity.getPos())) {
											if (targetIce && !isIced) {
												if (canHitFlying) {
													if (generalPvZombieEntity.hasPassengers() && generalPvZombieEntity.getFirstPassenger() instanceof ZombieRiderEntity zombieRiderEntity){
														prioritizedTarget = zombieRiderEntity;
													}
													else if (generalPvZombieEntity.isFlying()) {
														prioritizedTarget = hostileEntity;
													}
												} else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
													if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
														prioritizedTarget = hostileEntity;
													} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
															!(generalPvZombieEntity instanceof SnorkelEntity)) {
														if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
																generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
															prioritizedTarget = hostileEntity;
														} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
															prioritizedTarget = hostileEntity;
														} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
															prioritizedTarget = hostileEntity;
														}
													}
												} else if (canHitFlying) {
													if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
														prioritizedTarget = hostileEntity;
													} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
															!(generalPvZombieEntity instanceof SnorkelEntity)) {
														if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
																generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
															prioritizedTarget = hostileEntity;
														} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
															prioritizedTarget = hostileEntity;
														} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
															prioritizedTarget = hostileEntity;
														}
													}
												}
											}
										} else if (targetIce && !isIced) {
											if (canHitFlying) {
												if (generalPvZombieEntity.hasPassengers() && generalPvZombieEntity.getFirstPassenger() instanceof ZombieRiderEntity zombieRiderEntity){
													prioritizedTarget = zombieRiderEntity;
												}
												else if (generalPvZombieEntity.isFlying()) {
													prioritizedTarget = hostileEntity;
												}
											} else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													}
												}
											} else if (canHitFlying) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													}
												}
											}
										}
										if (targetPoison && !isPoisoned) {
											if (canHitFlying) {
												if (generalPvZombieEntity.hasPassengers() && generalPvZombieEntity.getFirstPassenger() instanceof ZombieRiderEntity zombieRiderEntity){
													prioritizedTarget = zombieRiderEntity;
												}
												else if (generalPvZombieEntity.isFlying()) {
													prioritizedTarget = hostileEntity;
												}
											} else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													}
												}
											} else if (canHitFlying) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedTarget = hostileEntity;
													}
												}
											}
										}
										if (targetNoHelmet && hasHelmet && prioritizedTarget == hostileEntity) {
											prioritizedTarget = null;
										}
										if (targetNoHelmet && !hasHelmet && prioritizedStrength < currentStrength) {
											if (canHitFlying) {
												if (generalPvZombieEntity.hasPassengers() && generalPvZombieEntity.getFirstPassenger() instanceof ZombieRiderEntity zombieRiderEntity){
													prioritizedTarget = zombieRiderEntity;
													prioritizedStrength = currentStrength;
												}
												else if (generalPvZombieEntity.isFlying()) {
													prioritizedTarget = hostileEntity;
													prioritizedStrength = currentStrength;
												}
											} else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedStrength = currentStrength;
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedStrength = currentStrength;
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													}
												}
											} else if (canHitFlying) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedStrength = currentStrength;
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedStrength = currentStrength;
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													}
												}
											}
										}
										if (targetHelmet && hasHelmet && prioritizedTarget == hostileEntity) {
											prioritizedTarget = null;
										}
										if (targetHelmet && hasHelmet && prioritizedStrength < currentStrength) {
											if (canHitFlying) {
												if (generalPvZombieEntity.hasPassengers() && generalPvZombieEntity.getFirstPassenger() instanceof ZombieRiderEntity zombieRiderEntity){
													prioritizedTarget = zombieRiderEntity;
													prioritizedStrength = currentStrength;
												}
												else if (generalPvZombieEntity.isFlying()) {
													prioritizedTarget = hostileEntity;
													prioritizedStrength = currentStrength;
												}
											}  else if (!canHitFlying && !generalPvZombieEntity.isFlying()) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedStrength = currentStrength;
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedStrength = currentStrength;
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													}
												}
											} else if (canHitFlying) {
												if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && snorkelEntity.isInvisibleSnorkel()) {
													prioritizedStrength = currentStrength;
													prioritizedTarget = hostileEntity;
												} else if (canHitSnorkel && generalPvZombieEntity instanceof SnorkelEntity snorkelEntity) {
													prioritizedStrength = currentStrength;
													prioritizedTarget = hostileEntity;
												} else if (!canHitSnorkel && (generalPvZombieEntity instanceof SnorkelEntity snorkelEntity && !snorkelEntity.isInvisibleSnorkel()) ||
														!(generalPvZombieEntity instanceof SnorkelEntity)) {
													if ((canHitStealth && generalPvZombieEntity.isStealth()) ||
															generalPvZombieEntity.isStealth() && this.squaredDistanceTo(generalPvZombieEntity) <= 4) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													} else if (canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													} else if (!canHitStealth && !generalPvZombieEntity.isStealth()) {
														prioritizedStrength = currentStrength;
														prioritizedTarget = hostileEntity;
													}
												}
											}
										}
									}
									if (targeted == null && prioritizedTarget == null && !(hostileEntity instanceof GeneralPvZombieEntity)) {
										targeted = hostileEntity;
									}
								} else if (!(hostileEntity instanceof GeneralPvZombieEntity) && targeted == null && !this.naturalSpawn) {
									targeted = hostileEntity;
								}
							}
						}
					}
				}
			}
			if (prioritizedTarget != null){
				this.setTarget(prioritizedTarget);
			}
			else {
				this.setTarget(targeted);
			}
		}
	}

	protected int heatTicks = 40;

    public void tick() {
		if (tickDelay <= -1){
			tickDelay = 5;
		}
		if (this.getFireImmune()){
			this.setFireTicks(0);
		}
		super.tick();
		Entity vehicle = this.getVehicle();
		if (vehicle instanceof LilyPadEntity){
			vehicle.setBodyYaw(this.bodyYaw);
		}
		if (PLANT_TYPE.get(this.getType()).orElse("appease").equals("pepper")){
			if (--heatTicks <= 0) {
				List<TileEntity> list = world.getNonSpectatingEntities(TileEntity.class, PvZEntity.PEASHOOTER.getDimensions().getBoxAt(this.getPos()).expand(1.5));
				for (TileEntity tileEntity : list) {
					if (tileEntity instanceof SnowTile) {
						tileEntity.discard();
					}
					if (tileEntity instanceof OilTile oilTile) {
						System.out.println("test");
						oilTile.makeFireTrail(oilTile.getBlockPos());
					}
				}
				heatTicks = 40;
			}
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		if (!PLANT_LOCATION.get(this.getType()).orElse("normal").equals("flying")){
			RandomGenerator randomGenerator = this.getRandom();
			BlockState blockState = this.getLandingBlockState();
			for (int i = 0; i < 4; ++i) {
				double d = this.getX() + (double) MathHelper.nextBetween(randomGenerator, -0.4F, 0.4F);
				double e = this.getY() + 0.3;
				double f = this.getZ() + (double) MathHelper.nextBetween(randomGenerator, -0.4F, 0.4F);
				this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0);
			}
		}
		super.onDeath(source);
		super.discard();
	}

	@Override
	protected void dropLoot(DamageSource source, boolean causedByPlayer) {
		if (this.world.getGameRules().getBoolean(PvZCubed.SHOULD_PLANT_DROP)){
			super.dropLoot(source, causedByPlayer);
		}
	}

	public void magnetize(){
		LivingEntity livingEntity = this.getTarget();
		LivingEntity livingEntity2 = null;
		LivingEntity livingEntity3 = null;
		MetalHelmetVariants helmetProj = MetalHelmetVariants.BUCKET;
		MetalHelmetVariants helmetProj2 = MetalHelmetVariants.BUCKET;
		MetalHelmetVariants helmetProj3 = MetalHelmetVariants.BUCKET;
		ZombiePropEntity setGear = null;
		ZombiePropEntity setGear2 = null;
		ZombiePropEntity setGear3 = null;

		if (livingEntity != null){
			List<LivingEntity> magnetList = this.world.getNonSpectatingEntities(LivingEntity.class, livingEntity.getBoundingBox().expand(2));
			EntityType<?> entityType = null;
			EntityType<?> entityType2 = null;
			EntityType<?> entityType3 = null;
			for (Entity entity : livingEntity.getPassengerList()){
				if (entity instanceof ZombiePropEntity zombiePropEntity) {
					if (entity instanceof MetalShieldEntity metalShieldEntity || entity instanceof MetalHelmetEntity metalHelmetEntity) {
						setGear = (ZombiePropEntity) entity;
						break;
					}
				}
			}
			if (magnetoshroom) {
				for (Entity entity : magnetList) {
					if (entity instanceof ZombiePropEntity zombiePropEntity && entity.squaredDistanceTo(livingEntity) <= 4 && entity != setGear) {
						if (entity instanceof MetalShieldEntity metalShieldEntity || entity instanceof MetalHelmetEntity metalHelmetEntity) {
							if (setGear2 == null) {
								setGear2 = (ZombiePropEntity) entity;
								livingEntity2 = (LivingEntity) entity.getVehicle();
							} else if (setGear3 == null) {
								setGear3 = (ZombiePropEntity) entity;
								livingEntity3 = (LivingEntity) entity.getVehicle();
							}
						}
					}
				}
			}
			if (setGear != null) {
				entityType = setGear.getType();
				if (entityType.equals(PvZEntity.BUCKETGEAR)) {
					if (livingEntity.getType().equals(PvZEntity.PEASANTBUCKET)) {
						helmetProj = MetalHelmetVariants.PEASANTBUCKET;
					} else if (livingEntity.getType().equals(PvZEntity.MUMMYBUCKET)) {
						helmetProj = MetalHelmetVariants.MUMMYBUCKET;
					} else {
						helmetProj = MetalHelmetVariants.BUCKET;
					}
				} else if (entityType.equals(PvZEntity.SCREENDOORSHIELD)) {
					helmetProj = MetalHelmetVariants.SCREENDOOR;
				} else if (entityType.equals(PvZEntity.FOOTBALLGEAR)) {
					helmetProj = MetalHelmetVariants.FOOTBALL;
				} else if (entityType.equals(PvZEntity.BERSERKERGEAR)) {
					helmetProj = MetalHelmetVariants.BERSERKER;
				} else if (entityType.equals(PvZEntity.DEFENSIVEENDGEAR)) {
					helmetProj = MetalHelmetVariants.DEFENSIVEEND;
				} else if (entityType.equals(PvZEntity.TRASHCANBIN)) {
					helmetProj = MetalHelmetVariants.TRASHCAN;
				} else if (entityType.equals(PvZEntity.BLASTRONAUTGEAR)) {
					helmetProj = MetalHelmetVariants.BLASTRONAUT ;
				} else if (entityType.equals(PvZEntity.KNIGHTGEAR)) {
					helmetProj = MetalHelmetVariants.KNIGHT;
				} else if (entityType.equals(PvZEntity.MEDALLIONGEAR)) {
					helmetProj = MetalHelmetVariants.MEDALLION;
				} else if (entityType.equals(PvZEntity.HELMETGEAR)) {
					helmetProj = MetalHelmetVariants.SERGEANTHELMET;
				} else if (entityType.equals(PvZEntity.SOLDIERGEAR)) {
					helmetProj = MetalHelmetVariants.SOLDIERHELMET;
				}
				if (magnetoshroom) {
					if (setGear2 != null) {
						entityType2 = setGear2.getType();
						if (entityType2.equals(PvZEntity.BUCKETGEAR)) {
							if (livingEntity2 != null) {
								if (livingEntity2.getType().equals(PvZEntity.PEASANTBUCKET)) {
									helmetProj2 = MetalHelmetVariants.PEASANTBUCKET;
								} else if (livingEntity.getType().equals(PvZEntity.MUMMYBUCKET)) {
									helmetProj2 = MetalHelmetVariants.MUMMYBUCKET;
								} else {
									helmetProj2 = MetalHelmetVariants.BUCKET;
								}
							}
						} else if (entityType2.equals(PvZEntity.SCREENDOORSHIELD)) {
							helmetProj2 = MetalHelmetVariants.SCREENDOOR;
						} else if (entityType2.equals(PvZEntity.FOOTBALLGEAR)) {
							helmetProj2 = MetalHelmetVariants.FOOTBALL;
						} else if (entityType2.equals(PvZEntity.BERSERKERGEAR)) {
							helmetProj2 = MetalHelmetVariants.BERSERKER;
						} else if (entityType2.equals(PvZEntity.DEFENSIVEENDGEAR)) {
							helmetProj2 = MetalHelmetVariants.DEFENSIVEEND;
						} else if (entityType2.equals(PvZEntity.TRASHCANBIN)) {
							helmetProj2 = MetalHelmetVariants.TRASHCAN;
						} else if (entityType2.equals(PvZEntity.BLASTRONAUTGEAR)) {
							helmetProj2 = MetalHelmetVariants.BERSERKER;
						} else if (entityType2.equals(PvZEntity.KNIGHTGEAR)) {
							helmetProj2 = MetalHelmetVariants.KNIGHT;
						} else if (entityType2.equals(PvZEntity.MEDALLIONGEAR)) {
							helmetProj2 = MetalHelmetVariants.MEDALLION;
						}
					}
					if (setGear3 != null) {
						entityType3 = setGear3.getType();
						if (entityType3.equals(PvZEntity.BUCKETGEAR)) {
							if (livingEntity3 != null) {
								if (livingEntity3.getType().equals(PvZEntity.PEASANTBUCKET)) {
									helmetProj3 = MetalHelmetVariants.PEASANTBUCKET;
								} else if (livingEntity.getType().equals(PvZEntity.MUMMYBUCKET)) {
									helmetProj3 = MetalHelmetVariants.MUMMYBUCKET;
								} else {
									helmetProj3 = MetalHelmetVariants.BUCKET;
								}
							}
						} else if (entityType3.equals(PvZEntity.SCREENDOORSHIELD)) {
							helmetProj3 = MetalHelmetVariants.SCREENDOOR;
						} else if (entityType3.equals(PvZEntity.FOOTBALLGEAR)) {
							helmetProj3 = MetalHelmetVariants.FOOTBALL;
						} else if (entityType3.equals(PvZEntity.BERSERKERGEAR)) {
							helmetProj3 = MetalHelmetVariants.BERSERKER;
						} else if (entityType3.equals(PvZEntity.DEFENSIVEENDGEAR)) {
							helmetProj3 = MetalHelmetVariants.DEFENSIVEEND;
						} else if (entityType3.equals(PvZEntity.TRASHCANBIN)) {
							helmetProj3 = MetalHelmetVariants.TRASHCAN;
						} else if (entityType3.equals(PvZEntity.BLASTRONAUTGEAR)) {
							helmetProj3 = MetalHelmetVariants.BERSERKER;
						} else if (entityType3.equals(PvZEntity.KNIGHTGEAR)) {
							helmetProj3 = MetalHelmetVariants.KNIGHT;
						} else if (entityType3.equals(PvZEntity.MEDALLIONGEAR)) {
							helmetProj3 = MetalHelmetVariants.MEDALLION;
						}
					}
				}
			}
		}
		if (setGear != null) {
			playSound(PvZSounds.MAGNETATTRACTEVENT);
			MetalHelmetProjEntity helmetProjEntity = (MetalHelmetProjEntity) PvZEntity.METALHELMETPROJ.create(world);
			helmetProjEntity.setOwner(this);
			if (this.getType().equals(PvZEntity.MAGNETOSHROOM)) {
				helmetProjEntity.setMaxAge(150);
			}
			else {
				helmetProjEntity.setMaxAge(200);
			}
			Vec3d vec3d = new Vec3d((double) magnetOffsetX, 0, 0).rotateY(-this.getHeadYaw() * (float) (Math.PI / 180.0) - ((float) (Math.PI / 2)));
			helmetProjEntity.refreshPositionAndAngles(this.getX() + vec3d.getX(), this.getY() + vec3d.getY() + magnetOffsetY, this.getZ() + vec3d.getZ(), 0, 0);
			helmetProjEntity.setVariant(helmetProj);
			helmetProjEntity.setDamage(setGear.getHealth());
			helmetProjEntity.setMaxHealth(setGear.getMaxHealth());
			setGear.discard();
			((ServerWorld) this.world).spawnEntityAndPassengers(helmetProjEntity);
		}
		if (magnetoshroom) {
			if (setGear2 != null) {
				MetalHelmetProjEntity helmetProjEntity = (MetalHelmetProjEntity) PvZEntity.METALHELMETPROJ.create(world);
				helmetProjEntity.setOwner(this);
				if (this.getType().equals(PvZEntity.MAGNETOSHROOM)) {
					helmetProjEntity.setMaxAge(150);
				}
				else {
					helmetProjEntity.setMaxAge(200);
				}
				Vec3d vec3d = new Vec3d((double) magnetOffsetX, 0, 0.5).rotateY(-this.getHeadYaw() * (float) (Math.PI / 180.0) - ((float) (Math.PI / 2)));
				helmetProjEntity.refreshPositionAndAngles(this.getX() + vec3d.getX(), this.getY() + vec3d.getY() + magnetOffsetY, this.getZ() + vec3d.getZ(), 0, 0);
				helmetProjEntity.setVariant(helmetProj2);
				helmetProjEntity.setDamage(setGear2.getHealth());
				helmetProjEntity.setMaxHealth(setGear2.getMaxHealth());
				setGear2.discard();
				((ServerWorld) this.world).spawnEntityAndPassengers(helmetProjEntity);
			}
			if (setGear3 != null) {
				MetalHelmetProjEntity helmetProjEntity = (MetalHelmetProjEntity) PvZEntity.METALHELMETPROJ.create(world);
				helmetProjEntity.setOwner(this);
				if (this.getType().equals(PvZEntity.MAGNETOSHROOM)) {
					helmetProjEntity.setMaxAge(150);
				}
				else {
					helmetProjEntity.setMaxAge(200);
				}
				Vec3d vec3d = new Vec3d((double) magnetOffsetX, 0, -0.5).rotateY(-this.getHeadYaw() * (float) (Math.PI / 180.0) - ((float) (Math.PI / 2)));
				helmetProjEntity.refreshPositionAndAngles(this.getX() + vec3d.getX(), this.getY() + vec3d.getY() + magnetOffsetY, this.getZ() + vec3d.getZ(), 0, 0);
				helmetProjEntity.setVariant(helmetProj3);
				helmetProjEntity.setDamage(setGear3.getHealth());
				helmetProjEntity.setMaxHealth(setGear3.getMaxHealth());
				setGear3.discard();
				((ServerWorld) this.world).spawnEntityAndPassengers(helmetProjEntity);
			}
		}
	}

	protected float magnetOffsetY = 1.5f;
	protected float magnetOffsetX = 0f;

	public HitResult amphibiousRaycast(double maxDistance) {
		Vec3d vec3d1 = this.getPos();
		Vec3d vec3d2 = new Vec3d(vec3d1.x, vec3d1.y - maxDistance, vec3d1.z);
		return this.world.raycast(new RaycastContext(vec3d1, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, this));
	}

	public static boolean checkPlant(Vec3d pos, ServerWorldAccess world, EntityType<?> type) {
		List<PlantEntity> list = world.getNonSpectatingEntities(PlantEntity.class, PvZEntity.PEASHOOTER.getDimensions().getBoxAt(pos).expand(20));
		List<PlantEntity> list1 = new ArrayList<>();
		for (PlantEntity plantEntity : list){
			if (plantEntity.getType() != type){
				list1.add(plantEntity);
			}
		}
		return !list1.isEmpty();
	}

	public static class PlantData implements EntityData {
		public final boolean tryLilyPad;

		public PlantData(boolean tryLilyPad) {
			this.tryLilyPad = tryLilyPad;
		}
	}
}
