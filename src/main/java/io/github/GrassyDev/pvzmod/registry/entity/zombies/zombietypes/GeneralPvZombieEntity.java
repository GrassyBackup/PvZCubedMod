package io.github.GrassyDev.pvzmod.registry.entity.zombies.zombietypes;

import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.ModItems;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.PlantEntity;
import io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz1.pool.jalapeno.FireTrailEntity;
import io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz1.pool.lilypad.LilyPadEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.gargantuar.modernday.GargantuarEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.imp.modernday.ImpEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.GrassyDev.pvzmod.PvZCubed.*;

public abstract class GeneralPvZombieEntity extends HostileEntity {
	private static final TrackedData<Byte> FLYING_TAG;
	protected GeneralPvZombieEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.setFlying(false);
		this.setPathfindingPenalty(PathNodeType.RAIL, 0.0F);
		this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0F);
		this.getNavigation().setCanSwim(true);
		this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 0.0F);
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
		this.setPathfindingPenalty(PathNodeType.LAVA, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
		this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
	}

	public float colliderOffset = 0.4F;

	public boolean armless;
	public boolean geardmg;
	public boolean gearless;

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(FLYING_TAG, (byte)16);
		this.dataTracker.startTracking(DATA_ID_HYPNOTIZED, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);
		tag.putBoolean("isFlying", this.isFlying());
		tag.putBoolean("Hypnotized", this.getHypno());
	}

	public void readCustomDataFromNbt(NbtCompound tag) {
		super.readCustomDataFromNbt(tag);
		if (tag.contains("isFlying")) {
			this.setFlying(tag.getBoolean("isFlying"));
		}
		this.dataTracker.set(DATA_ID_HYPNOTIZED, tag.getBoolean("Hypnotized"));
	}

	public boolean isFlying() {
		return ((Byte)this.dataTracker.get(FLYING_TAG) & 16) != 0;
	}

	public void setFlying(boolean isFlying) {
		byte b = (Byte)this.dataTracker.get(FLYING_TAG);
		if (isFlying) {
			this.dataTracker.set(FLYING_TAG, (byte)(b | 16));
		} else {
			this.dataTracker.set(FLYING_TAG, (byte)(b & -17));
		}

	}

	static {
		FLYING_TAG = DataTracker.registerData(GeneralPvZombieEntity.class, TrackedDataHandlerRegistry.BYTE);
	}

	/** /~*~//~*VARIANTS*~//~*~/ **/

	protected static final TrackedData<Boolean> DATA_ID_HYPNOTIZED =
			DataTracker.registerData(GeneralPvZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
								 SpawnReason spawnReason, @Nullable EntityData entityData,
								 @Nullable NbtCompound entityNbt) {
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

		public enum IsHypno {
		FALSE(false),
		TRUE(true);

		IsHypno(boolean id) {
			this.id = id;
		}

		private final boolean id;

		public boolean getId() {
			return this.id;
		}
	}

	public Boolean getHypno() {
		return this.dataTracker.get(DATA_ID_HYPNOTIZED);
	}

	public void setHypno(GeneralPvZombieEntity.IsHypno hypno) {
		this.dataTracker.set(DATA_ID_HYPNOTIZED, hypno.getId());
	}

	/** ----------------------------------------------------------------------- **/

	public EntityType<? extends HostileEntity> entityBox = PvZEntity.BROWNCOAT;

	@Override
	public boolean canBeRiddenInWater() {
		return true;
	}

	public boolean isPushable() {
		return false;
	}

	protected void pushAway(Entity entity) {
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return (this.getHypno()) ? PvZCubed.ZOMBIEBITEEVENT : PvZCubed.SILENCEVENET;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return PvZCubed.SILENCEVENET;
	}

	@Override
	public void onDeath(DamageSource source) {
		if (!(this instanceof ZombiePropEntity)) {
			double random = Math.random();
			float multiplier = ZOMBIE_STRENGTH.get(this.getType()).orElse(1);
			if (multiplier > 9){
				multiplier = 10;
			}
			double multiplierFinal = Math.pow(multiplier / 5, 2);
			Item item = ModItems.SEED_PACKET_LIST.get(getRandom().nextInt(ModItems.SEED_PACKET_LIST.size()));
			if (random <= 0.05 * multiplierFinal) {
				dropItem(item);
				playSound(LOOTGIFTDEVENT);
			} else if (random <= 0.10 * multiplierFinal) {
				dropItem(Items.DIAMOND);
				playSound(LOOTDIAMONDEVENT);
			} else if (random <= 0.30 * multiplierFinal) {
				dropItem(Items.GOLD_NUGGET);
				playSound(LOOTNUGGETEVENT);
			} else if (random <= 0.70 * multiplierFinal) {
				dropItem(Items.IRON_NUGGET);
				playSound(LOOTNUGGETEVENT);
			}
		}
		super.onDeath(source);
	}

	public PlantEntity CollidesWithPlant(){
		Vec3d vec3d = new Vec3d((double)colliderOffset, 0.0, 0.0).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - ((float) (Math.PI / 2)));
		List<PlantEntity> list = world.getNonSpectatingEntities(PlantEntity.class, entityBox.getDimensions().getBoxAt(this.getX() + vec3d.x, this.getY(), this.getZ() + vec3d.z));
		PlantEntity setPlant = null;
		for (PlantEntity plantEntity : list) {
			if (plantEntity instanceof LilyPadEntity lilyPadEntity) {
				if (!(lilyPadEntity.hasPassengers())) {
					setPlant = lilyPadEntity;
				} else {
					setPlant = (PlantEntity) lilyPadEntity.getFirstPassenger();
				}
			}
			else if (PLANT_LOCATION.get(plantEntity.getType()).orElse("normal").equals("ground") && TARGET_GROUND.get(this.getType()).orElse(false).equals(true)){
				setPlant = plantEntity;
			}
			else if (PLANT_LOCATION.get(plantEntity.getType()).orElse("normal").equals("ground")){
				setPlant = null;
			}
			else if (PLANT_LOCATION.get(plantEntity.getType()).orElse("normal").equals("flying") && TARGET_FLY.get(this.getType()).orElse(false).equals(true)){
				setPlant = plantEntity;
			}
			else if (PLANT_LOCATION.get(plantEntity.getType()).orElse("normal").equals("flying")){
				setPlant = null;
			}
			else {
				setPlant = plantEntity;
			}
		}
		return setPlant;
	}

	public PlayerEntity CollidesWithPlayer(){
		Vec3d vec3d = new Vec3d((double)colliderOffset, 0.0, 0.0).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - ((float) (Math.PI / 2)));
		List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, entityBox.getDimensions().getBoxAt(this.getX() + vec3d.x, this.getY(), this.getZ() + vec3d.z));
		if (!list.isEmpty()){
			return list.get(0);
		}
		else {
			return null;
		}
	}

	public ZombieObstacleEntity CollidesWithObstacle(){
		List<ZombieObstacleEntity> list = world.getNonSpectatingEntities(ZombieObstacleEntity.class, entityBox.getDimensions().getBoxAt(this.getX(), this.getY(), this.getZ()));
		ZombieObstacleEntity obstacleEntity = null;
		if (!list.isEmpty()) {
			for (ZombieObstacleEntity zombieObstacleEntity : list) {
				if (!zombieObstacleEntity.hasVehicle() && !zombieObstacleEntity.isDead()) {
					obstacleEntity = zombieObstacleEntity;
				}
			}
		}
		return obstacleEntity;
	}

	boolean pop = true;

	@Override
	protected void mobTick() {
		super.mobTick();
	}

	public void tick() {
		super.tick();
		LivingEntity target = this.getTarget();
		if (this.getHypno() && (target instanceof PlayerEntity || target instanceof PassiveEntity || target instanceof GolemEntity)){
			this.setTarget(null);
		}
		LivingEntity target2 = this.getTarget();
		if (target != null) {
			if (target.squaredDistanceTo(this) < 6.25) {
				this.setVelocity(0, -0.3, 0);
			}
		}
		else if (!this.getHypno()) {
			this.setTarget(this.world.getClosestPlayer(this.getX(), this.getY(), this.getZ(), 100, true));
		}
		if (this.hasStatusEffect(PvZCubed.FROZEN) && this.isInsideWaterOrBubbleColumn()){
			this.kill();
		}
		if (this.getTarget() != null && this.getTarget().isDead()){
			this.setTarget(null);
		}

		// thanks to Pluiedev for this hipster code
		var zombiePropEntity = this.getPassengerList()
				.stream()
				.filter(e -> e instanceof ZombiePropEntity)
				.map(e -> (ZombiePropEntity) e)
				.findFirst();

		if (zombiePropEntity.isPresent()) {
			var e = zombiePropEntity.get();
			this.geardmg = e.getHealth() < e.getMaxHealth() / 2;
			this.gearless = false;
		} else {
			this.gearless = true;
			this.geardmg = false;
		}

		this.armless = this.getHealth() < this.getMaxHealth() / 2;
		Entity entity = this;
		if (this.getHealth() < this.getMaxHealth() / 2 && !(entity instanceof ZombiePropEntity) &&
				!(entity instanceof GargantuarEntity) && !(entity instanceof ImpEntity)){
			if (this.pop && !this.dead){
				playSound(PvZCubed.POPLIMBEVENT, 0.75f, (float) (0.5F + Math.random()));
				pop = false;
			}
		}
		if (this.getTarget() instanceof GeneralPvZombieEntity generalPvZombieEntity && generalPvZombieEntity.getHypno() && this.getHypno()){
			this.setTarget(null);
		}
		if (this.getTarget() instanceof FireTrailEntity){
			this.setTarget(null);
		}
		if (this.getTarget() instanceof ZombieObstacleEntity zombieObstacleEntity && !zombieObstacleEntity.getHypno() && !this.getHypno()){
			this.setTarget(null);
		}

		if (this.submergedInWater){
			this.jump();
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (this.getTarget() != null &&
				((PLANT_LOCATION.get(this.getTarget().getType()).orElse("normal").equals("ground") &&
				TARGET_GROUND.get(this.getType()).orElse(false).equals(true)) ||
						(PLANT_LOCATION.get(this.getTarget().getType()).orElse("normal").equals("flying") &&
						TARGET_FLY.get(this.getType()).orElse(false).equals(true)))) {
			if (!this.hasStatusEffect(PvZCubed.FROZEN)) {
				float sound = 0.75f;
				if (this.getHypno()) {
					sound = 0.33f;
				}
				target.playSound(PvZCubed.ZOMBIEBITEEVENT, sound, 1f);
			}
			return super.tryAttack(target);
		}
		else if (this.getTarget() != null &&
				!((PLANT_LOCATION.get(this.getTarget().getType()).orElse("normal").equals("ground"))) &&
				!((PLANT_LOCATION.get(this.getTarget().getType()).orElse("normal").equals("flying")))){
			if (!this.hasStatusEffect(PvZCubed.FROZEN)) {
				float sound = 0.75f;
				if (this.getHypno()) {
					sound = 0.33f;
				}
				target.playSound(PvZCubed.ZOMBIEBITEEVENT, sound, 1f);
			}
			return super.tryAttack(target);
		}
		else {
			return false;
		}
	}
}
