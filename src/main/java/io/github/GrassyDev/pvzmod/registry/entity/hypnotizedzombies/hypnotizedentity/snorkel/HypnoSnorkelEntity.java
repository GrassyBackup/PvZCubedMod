package io.github.GrassyDev.pvzmod.registry.entity.hypnotizedzombies.hypnotizedentity.snorkel;

import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.entity.hypnotizedzombies.hypnotizedentity.HypnoPvZombieAttackGoal;
import io.github.GrassyDev.pvzmod.registry.entity.hypnotizedzombies.hypnotizedentity.dancingzombie.HypnoDancingZombieEntity;
import io.github.GrassyDev.pvzmod.registry.entity.hypnotizedzombies.hypnotizedentity.flagzombie.modernday.HypnoFlagzombieEntity;
import io.github.GrassyDev.pvzmod.registry.entity.hypnotizedzombies.hypnotizedtypes.HypnoZombieEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.miscentity.duckytube.DuckyTubeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class HypnoSnorkelEntity extends HypnoZombieEntity implements IAnimatable {
	private static final TrackedData<Byte> HYPNO_SNORKEL_FLAGS;
	private static final byte IS_INVIS_FLAG = 16;
	private MobEntity owner;
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);
	private String controllerName = "walkingcontroller";
	public boolean invisSnorkel;


	public HypnoSnorkelEntity(EntityType<? extends HypnoSnorkelEntity> entityType, World world) {
		super(entityType, world);
		this.invisSnorkel = false;
		setInvisibleSnorkel(false);
		this.ignoreCameraFrustum = true;
		this.getNavigation().setCanSwim(true);
		this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 0.0F);
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
		this.setPathfindingPenalty(PathNodeType.LAVA, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
		this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
	}

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(HYPNO_SNORKEL_FLAGS, (byte)16);
	}

	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("HypnoInvisSnorkel", this.isInvisibleSnorkel());
	}

	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("HypnoInvisSnorkel")) {
			this.setInvisibleSnorkel(nbt.getBoolean("HypnoInvisSnorkel"));
		}

	}

	public boolean isInvisibleSnorkel() {
		return ((Byte)this.dataTracker.get(HYPNO_SNORKEL_FLAGS) & 16) != 0;
	}

	public void setInvisibleSnorkel(boolean isInvisibleSnorkel) {
		byte b = (Byte)this.dataTracker.get(HYPNO_SNORKEL_FLAGS);
		if (isInvisibleSnorkel) {
			this.dataTracker.set(HYPNO_SNORKEL_FLAGS, (byte)(b | 16));
		} else {
			this.dataTracker.set(HYPNO_SNORKEL_FLAGS, (byte)(b & -17));
		}

	}

	static {
		HYPNO_SNORKEL_FLAGS = DataTracker.registerData(HypnoSnorkelEntity.class, TrackedDataHandlerRegistry.BYTE);
	}

	@Environment(EnvType.CLIENT)
	public void handleStatus(byte status) {
		if (status == 66) {
			this.invisSnorkel = true;
		}
		else if (status == 65) {
			this.invisSnorkel = false;
		}
	}


	/** /~*~//~*GECKOLIB ANIMATION*~//~*~/ **/

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController controller = new AnimationController(this, controllerName, 0, this::predicate);

		data.addAnimationController(controller);
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		Entity vehicle = this.getVehicle();
		if (vehicle instanceof DuckyTubeEntity) {
			if (invisSnorkel){
				event.getController().setAnimation(new AnimationBuilder().loop("snorkel.ducky"));
				event.getController().setAnimationSpeed(1);
			}
			else {
				event.getController().setAnimation(new AnimationBuilder().loop("snorkel.duckyattack"));
				event.getController().setAnimationSpeed(1);
			}
		}else {
			if (!(event.getLimbSwingAmount() > -0.01F && event.getLimbSwingAmount() < 0.01F)) {
				event.getController().setAnimation(new AnimationBuilder().loop("snorkel.walking"));
				event.getController().setAnimationSpeed(1);
			} else {
				event.getController().setAnimation(new AnimationBuilder().loop("snorkel.idle"));
				event.getController().setAnimationSpeed(1);
			}
		}
		return PlayState.CONTINUE;
	}

	public void tick() {
		LivingEntity target = this.getTarget();
		Entity vehicle = this.getVehicle();
		if (vehicle instanceof DuckyTubeEntity){
			if (target != null){
				if (this.squaredDistanceTo(target) > 4){
					this.world.sendEntityStatus(this, (byte) 66);
					setInvisibleSnorkel(true);
				}
				else {
					this.world.sendEntityStatus(this, (byte) 65);
					setInvisibleSnorkel(false);
				}
			}
			else {
				this.world.sendEntityStatus(this, (byte) 65);
				setInvisibleSnorkel(false);
			}
		}
		else {
			this.world.sendEntityStatus(this, (byte) 65);
			setInvisibleSnorkel(false);
		}
		super.tick();
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		LivingEntity vehicle = (LivingEntity) getVehicle();
		if (vehicle instanceof DuckyTubeEntity){
			discard();
		}
	}

	/** /~*~//~*AI*~//~*~/ **/

	protected void initGoals() {
		this.goalSelector.add(1, new AttackGoal(this));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D));
		this.initCustomGoals();
	}

	protected void initCustomGoals() {
		this.targetSelector.add(2, new HypnoSnorkelEntity.TrackOwnerTargetGoal(this));
		this.goalSelector.add(1, new HypnoPvZombieAttackGoal(this, 1.0D, true));
		this.targetSelector.add(1, new TargetGoal<>(this, MobEntity.class, 0, true, true, (livingEntity) -> {
			return livingEntity instanceof Monster && !(livingEntity instanceof HypnoDancingZombieEntity) &&
					!(livingEntity instanceof HypnoFlagzombieEntity);
		}));
	}


	/** /~*~//~*ATTRIBUTES*~//~*~/ **/

	public static DefaultAttributeContainer.Builder createHypnoSnorkelAttributes() {
		return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 50.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.15D)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0D)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 50D);
	}

	protected SoundEvent getAmbientSound() {
		return PvZCubed.ZOMBIEMOANEVENT;
	}

	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource source) {
		return PvZCubed.ZOMBIEBITEEVENT;
	}

	public MobEntity getOwner() {
		return this.owner;
	}

	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_ZOMBIE_STEP;
	}
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	public void setOwner(MobEntity owner) {
		this.owner = owner;
	}


	/** /~*~//~*GOALS*~//~*~/ **/

	class TrackOwnerTargetGoal extends TrackTargetGoal {
		private final TargetPredicate TRACK_OWNER_PREDICATE = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();

		public TrackOwnerTargetGoal(PathAwareEntity mob) {
			super(mob, false);
		}

		public boolean canStart() {
			return HypnoSnorkelEntity.this.owner != null && HypnoSnorkelEntity.this.owner.getTarget() != null && this.canTrack(HypnoSnorkelEntity.this.owner.getTarget(), this.TRACK_OWNER_PREDICATE);
		}

		public void start() {
			HypnoSnorkelEntity.this.setTarget(HypnoSnorkelEntity.this.owner.getTarget());
			super.start();
		}
	}
}
