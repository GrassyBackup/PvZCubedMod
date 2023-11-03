package io.github.GrassyDev.pvzmod.registry.entity.gravestones.mausoleum;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.ModItems;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.PvZSounds;
import io.github.GrassyDev.pvzmod.registry.entity.gravestones.GraveEntity;
import io.github.GrassyDev.pvzmod.registry.entity.variants.graves.GraveDifficulty;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz2o.browncoat.sargeant.SargeantEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz2o.flagzombie.sargeant.FlagSargeantEntity;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz2o.hawker.zombie.HawkerZombieEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Objects;

import static io.github.GrassyDev.pvzmod.PvZCubed.PVZCONFIG;

public class MausoleumGraveEntity extends GraveEntity implements IAnimatable {

	private String controllerName = "walkingcontroller";

	private int spawnCounter;



	double tiltchance = this.random.nextDouble();

    private AnimationFactory factory = GeckoLibUtil.createFactory(this);


    public MausoleumGraveEntity(EntityType<MausoleumGraveEntity> entityType, World world) {
        super(entityType, world);

        this.experiencePoints = 25;
    }

	static {
	}

	@Environment(EnvType.CLIENT)
	public void handleStatus(byte status) {
		if (status != 2 && status != 60){
			super.handleStatus(status);
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
		if (beingEaten){
			event.getController().setAnimation(new AnimationBuilder().loop("obstacle.eating"));
		}
		else if (tiltchance <= 0.5) {
			event.getController().setAnimation(new AnimationBuilder().loop("gravestone.idle"));
		}
		else {
			event.getController().setAnimation(new AnimationBuilder().loop("gravestone.idle2"));
		}
        return PlayState.CONTINUE;
    }


	/** /~*~//~*AI*~//~*~/ **/

	protected void initGoals() {
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(1, new MausoleumGraveEntity.summonZombieGoal(this));
    }


	/** /~*~//~*TICKING*~//~*~/ **/

	public void tick() {
		super.tick();
		this.setTarget(this.world.getClosestPlayer(this.getX(), this.getY(), this.getZ(), 100, true));
		LocalDifficulty localDifficulty = world.getLocalDifficulty(this.getBlockPos());
		double difficulty = 0;
		if (this.getVariant().equals(GraveDifficulty.NONE)){
			difficulty = localDifficulty.getLocalDifficulty();
				if (difficulty >= 2.1){
					difficulty = 2.1;
					if (world.getDifficulty().equals(Difficulty.HARD)){
						difficulty = difficulty + difficultymodifier;
					}
				}
		}
		else if (this.getVariant().equals(GraveDifficulty.EASY)){
			difficulty = 1.0;
		}
		else if (this.getVariant().equals(GraveDifficulty.EASYMED)){
			difficulty = 1.5;
		}
		else if (this.getVariant().equals(GraveDifficulty.MED)){
			difficulty = 1.6;
		}
		else if (this.getVariant().equals(GraveDifficulty.MEDHARD)){
			difficulty = 1.8;
		}
		else if (this.getVariant().equals(GraveDifficulty.HARD)){
			difficulty = 2.1;
		}
		else if (this.getVariant().equals(GraveDifficulty.SUPERHARD)){
			difficulty = 3;
		}
		else if (this.getVariant().equals(GraveDifficulty.NIGHTMARE)){
			difficulty = 4;
		}
		else if (this.getVariant().equals(GraveDifficulty.CRAAAZY)){
			difficulty = 5;
		}
		if (this.spawnCounter == 1 && world.getTime() < 24000) {
			this.kill();
		}
		if (this.spawnCounter == 1 && world.getTime() < 24000) {
			this.kill();
		}
		else if (this.spawnCounter == 2 && difficulty <= 1.509 + difficultymodifier){
			if (!this.isInfinite()) {
				this.kill();
			}
		}
		else if (this.spawnCounter == 3 && difficulty <= 1.709 + difficultymodifier){
			if (!this.isInfinite()) {
				this.kill();
			}
		}
		else if (this.spawnCounter == 4 && difficulty <= 1.909 + difficultymodifier){
			if (!this.isInfinite()) {
				this.kill();
			}
		}
		else if (this.spawnCounter == 5 && difficulty <= 2.109 + difficultymodifier){
			if (!this.isInfinite()) {
				this.kill();
			}
		}
		else if (this.spawnCounter == 6 && difficulty >= 2.309 + difficultymodifier){
			if (!this.isInfinite()) {
				this.kill();
			}
		}
		else if (this.spawnCounter > 7){
			if (!this.isInfinite()) {
				this.kill();
			}
		}
		if (this.world.isClient && this.isSpellcasting()) {
			Spell spell = this.getSpell();
			float g = this.bodyYaw * 0.017453292F + MathHelper.cos((float)this.age * 0.6662F) * 0.25F;
			float h = MathHelper.cos(g);
			float i = MathHelper.sin(g);
			this.world.addParticle(ParticleTypes.SMOKE, this.getX() + (double)h * 0.6, this.getY(), this.getZ() + (double)i * 0.6, 0, 0.0125, 0);
			this.world.addParticle(ParticleTypes.SMOKE, this.getX() - (double)h * 0.6, this.getY(), this.getZ() - (double)i * 0.6, 0, 0.0125, 0);
		}
	}


	/** /~*~//~*ATTRIBUTES*~//~*~/ **/

	public static DefaultAttributeContainer.Builder createMausoleumGraveAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0D)
				.add(ReachEntityAttributes.ATTACK_RANGE, 1.5D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, PVZCONFIG.nestedZombieHealth.mausoleumGraveH());
    }

	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_ANCIENT_DEBRIS_BREAK;
	}

	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.BLOCK_BASALT_HIT;
	}






	/** /~*~//~*SPAWNING*~//~*~/ **/

	public static boolean canMausolemGraveSpawn(EntityType<? extends MausoleumGraveEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, RandomGenerator random) {
		BlockPos blockPos = pos.down();
		float cavespawn = random.nextFloat();
		if (cavespawn <= 0.66) {
			return world.getDifficulty() != Difficulty.PEACEFUL &&
					!world.getBlockState(pos).getMaterial().isLiquid() &&
					world.toServerWorld().getTime() > 144000  &&
					pos.getY() > 50 &&
					(world.getAmbientDarkness() >= 2 ||
							world.getLightLevel(LightType.SKY, pos) < 2) &&
					!world.getBlockState(blockPos).getBlock().hasDynamicBounds() &&
					!checkVillager(Vec3d.ofCenter(pos), world) &&
					!checkPlant(Vec3d.ofCenter(pos), world) && Objects.requireNonNull(world.getServer()).getGameRules().getBoolean(PvZCubed.SHOULD_GRAVE_SPAWN);
		}
		else {
			return world.getDifficulty() != Difficulty.PEACEFUL &&
					!world.getBlockState(pos).getMaterial().isLiquid() &&
					world.toServerWorld().getTime() > 144000 &&
					(world.getAmbientDarkness() >= 2 ||
							world.getLightLevel(LightType.SKY, pos) < 2) &&
					!world.getBlockState(blockPos).getBlock().hasDynamicBounds() &&
					!checkVillager(Vec3d.ofCenter(pos), world) &&
					!checkPlant(Vec3d.ofCenter(pos), world) && Objects.requireNonNull(world.getServer()).getGameRules().getBoolean(PvZCubed.SHOULD_GRAVE_SPAWN);
		}
	}

	/** /~*~//~*GOALS*~//~*~/ **/

	protected SoundEvent getCastSpellSound() {
        return PvZSounds.ENTITYRISINGEVENT;
    }

	protected abstract class CastSpellGoal extends Goal {
		protected int spellCooldown;
		protected int startTime;

		protected CastSpellGoal() {
		}

		public boolean canStart() {
			LivingEntity livingEntity = MausoleumGraveEntity.this.getTarget();
			if (livingEntity != null && livingEntity.isAlive()) {
				if (MausoleumGraveEntity.this.isSpellcasting()) {
					return false;
				} else {
					return MausoleumGraveEntity.this.age >= this.startTime;
				}
			} else {
				return false;
			}
		}

		public boolean shouldContinue() {
			LivingEntity livingEntity = MausoleumGraveEntity.this.getTarget();
			return livingEntity != null && livingEntity.isAlive() && this.spellCooldown > 0;
		}

		public void start() {
			this.spellCooldown = this.getTickCount(this.getInitialCooldown());
			MausoleumGraveEntity.this.spellTicks = this.getSpellTicks();
			this.startTime = MausoleumGraveEntity.this.age + this.startTimeDelay();
			SoundEvent soundEvent = this.getSoundPrepare();
			if (soundEvent != null) {
				MausoleumGraveEntity.this.playSound(soundEvent, 1.0F, 1.0F);
			}

			MausoleumGraveEntity.this.setSpell(this.getSpell());
		}

		public void tick() {
			--this.spellCooldown;
			if (this.spellCooldown == 0) {
				this.castSpell();
				MausoleumGraveEntity.this.addStatusEffect((new StatusEffectInstance(StatusEffects.GLOWING, 70, 1)));
				MausoleumGraveEntity.this.playSound(MausoleumGraveEntity.this.getCastSpellSound(), 1.0F, 1.0F);
			}

		}

		protected abstract void castSpell();

		protected int getInitialCooldown() {
			return 20;
		}

		protected abstract int getSpellTicks();

		protected abstract int startTimeDelay();

		@Nullable
		protected abstract SoundEvent getSoundPrepare();

		protected abstract Spell getSpell();
	}


	/** /~*~//~*INTERACTION*~//~*~/ **/

	@Nullable
	@Override
	public ItemStack getPickBlockStack() {
		return ModItems.MAUSOLEUMGRAVESPAWN.getDefaultStack();
	}

    class summonZombieGoal extends CastSpellGoal {
        private final TargetPredicate closeZombiePredicate;

		private final MausoleumGraveEntity mausoleumGraveEntity;

        private summonZombieGoal(MausoleumGraveEntity mausoleumGraveEntity) {
            super();
			this.mausoleumGraveEntity = mausoleumGraveEntity;
			this.closeZombiePredicate = (TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0D).ignoreVisibility().ignoreDistanceScalingFactor());
		}

		public boolean canStart() {
			return super.canStart();
		}

        protected int getSpellTicks() {
            return 100;
        }

        protected int startTimeDelay() {
            return 400;
        }

        protected void castSpell() {
			graveWeight = 0;
            ServerWorld serverWorld = (ServerWorld) MausoleumGraveEntity.this.world;
			LocalDifficulty localDifficulty = world.getLocalDifficulty(this.mausoleumGraveEntity.getBlockPos());
			double difficulty = 0;
			if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.NONE)){
				difficulty = localDifficulty.getLocalDifficulty();
				if (difficulty >= 2.1){
					difficulty = 2.1;
					if (world.getDifficulty().equals(Difficulty.HARD)){
						difficulty = difficulty + difficultymodifier;
					}
				}
			}
			else if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.EASY)){
				difficulty = 1.0;
			}
			else if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.EASYMED)){
				difficulty = 1.5;
			}
			else if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.MED)){
				difficulty = 1.6;
			}
			else if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.MEDHARD)){
				difficulty = 1.8;
			}
			else if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.HARD)){
				difficulty = 2.1;
			}
			else if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.SUPERHARD)){
				difficulty = 3;
			}
			else if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.NIGHTMARE)){
				difficulty = 4;
			}
			else if (this.mausoleumGraveEntity.getVariant().equals(GraveDifficulty.CRAAAZY)){
				difficulty = 5;
			}
			double probability = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability11 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability2 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability10 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability3 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability4 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability5 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability6 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability7 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability9 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability8 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability12 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));
			double probability13 = random.nextDouble() * Math.pow(difficulty / 2, -1 * (difficulty / 2));

			int zombiePos = -2 + MausoleumGraveEntity.this.random.nextInt(5);
			int zombiePosZ = -2 + MausoleumGraveEntity.this.random.nextInt(5);
			if (MausoleumGraveEntity.this.is1x1()){
				zombiePos = 0;
				zombiePosZ = 0;
			}

			for(int a = 0; a < 2 / halfModifier; ++a) { // 100% x2 Sargeant
				if (!MausoleumGraveEntity.this.is1x1()) {
					zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
					zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
				}
				if (MausoleumGraveEntity.this.isChallengeGrave()) {
					zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
					zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
				}
                BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
                SargeantEntity browncoatEntity = (SargeantEntity) PvZEntity.SARGEANT.create(MausoleumGraveEntity.this.world);
                browncoatEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
                browncoatEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData)null, (NbtCompound)null);
                browncoatEntity.setOwner(MausoleumGraveEntity.this);
                serverWorld.spawnEntityAndPassengers(browncoatEntity);
            }
			if (graveWeight <= 3.5) {
				if (probability <= 0.45 / halfModifier * survChance) { // 45% x1 Conehead
					for (int b = 0; b < 1; ++b) { // 100% x1 Browncoat
						if (!MausoleumGraveEntity.this.is1x1()) {
							zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
							zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
						}
						if (MausoleumGraveEntity.this.isChallengeGrave()) {
							zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
							zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
						}
						BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
						SargeantEntity browncoatEntity = (SargeantEntity) PvZEntity.SARGEANT.create(MausoleumGraveEntity.this.world);
						browncoatEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
						browncoatEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
						browncoatEntity.setOwner(MausoleumGraveEntity.this);
						serverWorld.spawnEntityAndPassengers(browncoatEntity);
					}
					for (int h = 0; h < 1; ++h) {
						if (!MausoleumGraveEntity.this.is1x1()) {
							zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
							zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
						}
						if (MausoleumGraveEntity.this.isChallengeGrave()) {
							zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
							zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
						}
						BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
						SargeantEntity coneheadEntity = (SargeantEntity) PvZEntity.SARGEANTBOWL.create(MausoleumGraveEntity.this.world);
						coneheadEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
						coneheadEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
						coneheadEntity.setOwner(MausoleumGraveEntity.this);
						serverWorld.spawnEntityAndPassengers(coneheadEntity);
						graveWeight += 0.5;
					}
				}
			}
			if (graveWeight <= 3.5) {
				if (probability3 <= 0.10 / halfModifier * survChance) { // 10% x2 Helmet Sargeant
					for (int b = 0; b < 2 / halfModifier; ++b) { // 100% x2 Browncoat
						if (!MausoleumGraveEntity.this.is1x1()) {
							zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
							zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
						}
						if (MausoleumGraveEntity.this.isChallengeGrave()) {
							zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
							zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
						}
						BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
						SargeantEntity browncoatEntity = (SargeantEntity) PvZEntity.SARGEANT.create(MausoleumGraveEntity.this.world);
						browncoatEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
						browncoatEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
						browncoatEntity.setOwner(MausoleumGraveEntity.this);
						serverWorld.spawnEntityAndPassengers(browncoatEntity);
					}
					for (int c = 0; c < 2 / halfModifier; ++c) {
						if (!MausoleumGraveEntity.this.is1x1()) {
							zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
							zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
						}
						if (MausoleumGraveEntity.this.isChallengeGrave()) {
							zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
							zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
						}
						BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
						SargeantEntity sargeantBucket = (SargeantEntity) PvZEntity.SARGEANTHELMET.create(MausoleumGraveEntity.this.world);
						sargeantBucket.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
						sargeantBucket.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
						sargeantBucket.setOwner(MausoleumGraveEntity.this);
						serverWorld.spawnEntityAndPassengers(sargeantBucket);
					}
					graveWeight += 1;
				}
			}
			if (serverWorld.toServerWorld().getTime() > 24000) {
				if (graveWeight <= 3.5) {
					if (difficulty >= 1.609 + difficultymodifier || isUnlock() || isUnlockSpecial()) {
						if (probability5 <= 0.1 / halfModifier * survChance) { // 15% x1 Flag Zombie
							for (int f = 0; f < 1; ++f) {
								if (!MausoleumGraveEntity.this.is1x1()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
									zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
								}
								if (MausoleumGraveEntity.this.isChallengeGrave()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
									zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
								}
								if (MausoleumGraveEntity.this.isChallengeGrave()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
									zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
								}
								double random = Math.random();
								BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
								FlagSargeantEntity flagzombieEntity = (FlagSargeantEntity) PvZEntity.FLAGSARGEANT.create(MausoleumGraveEntity.this.world);
								flagzombieEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
								flagzombieEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
								flagzombieEntity.setOwner(MausoleumGraveEntity.this);
								serverWorld.spawnEntityAndPassengers(flagzombieEntity);

								BlockPos blockPos2 = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
								SargeantEntity browncoatEntity = (SargeantEntity) PvZEntity.SARGEANT.create(MausoleumGraveEntity.this.world);
								browncoatEntity.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
								browncoatEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos2), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
								browncoatEntity.setOwner(MausoleumGraveEntity.this);
								serverWorld.spawnEntityAndPassengers(browncoatEntity);

								BlockPos blockPos3 = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
								SargeantEntity coneheadEntity = (SargeantEntity) PvZEntity.SARGEANTBOWL.create(MausoleumGraveEntity.this.world);
								coneheadEntity.refreshPositionAndAngles(blockPos3, 0.0F, 0.0F);
								coneheadEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos3), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
								coneheadEntity.setOwner(MausoleumGraveEntity.this);
								serverWorld.spawnEntityAndPassengers(coneheadEntity);

								BlockPos blockPos4 = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
								SargeantEntity screendoorEntity = (SargeantEntity) PvZEntity.SARGEANTSHIELD.create(MausoleumGraveEntity.this.world);
								screendoorEntity.refreshPositionAndAngles(blockPos4, 0.0F, 0.0F);
								screendoorEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos4), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
								screendoorEntity.setOwner(MausoleumGraveEntity.this);
								serverWorld.spawnEntityAndPassengers(screendoorEntity);
								graveWeight += 10;
							}
						}
					}
				}
				if (graveWeight <= 3.5) {
					if (probability11 <= 0.25 / halfModifier * survChance) { // 25% x1 Conehead
						for (int b = 0; b < 1; ++b) { // 100% x1 Browncoat
							if (!MausoleumGraveEntity.this.is1x1()) {
								zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
								zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
							}
							if (MausoleumGraveEntity.this.isChallengeGrave()) {
								zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
								zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
							}
							BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
							SargeantEntity browncoatEntity = (SargeantEntity) PvZEntity.SARGEANT.create(MausoleumGraveEntity.this.world);
							browncoatEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
							browncoatEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
							browncoatEntity.setOwner(MausoleumGraveEntity.this);
							serverWorld.spawnEntityAndPassengers(browncoatEntity);
						}
						for (int h = 0; h < 2 / halfModifier; ++h) {
							if (!MausoleumGraveEntity.this.is1x1()) {
								zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
								zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
							}
							if (MausoleumGraveEntity.this.isChallengeGrave()) {
								zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
								zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
							}
							BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
							SargeantEntity coneheadEntity = (SargeantEntity) PvZEntity.SARGEANTBOWL.create(MausoleumGraveEntity.this.world);
							coneheadEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
							coneheadEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
							coneheadEntity.setOwner(MausoleumGraveEntity.this);
							serverWorld.spawnEntityAndPassengers(coneheadEntity);
							graveWeight += 0.5;
						}
					}
				}
				if (graveWeight <= 3.5) {
					if (difficulty >= 1.579 + difficultymodifier || isUnlock() || isUnlockSpecial()) {
						if (probability2 <= 0.2 / halfModifier * survChance) { // 20% x1 Sergeant Shield
							for (int c = 0; c < 1; ++c) {
								if (!MausoleumGraveEntity.this.is1x1()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
									zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
								}
								if (MausoleumGraveEntity.this.isChallengeGrave()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
									zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
								}
								BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
								SargeantEntity screendoor = (SargeantEntity) PvZEntity.SARGEANTSHIELD.create(MausoleumGraveEntity.this.world);
								screendoor.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
								screendoor.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
								screendoor.setOwner(MausoleumGraveEntity.this);
								serverWorld.spawnEntityAndPassengers(screendoor);
								graveWeight += 1.25;
							}
						}
					}
				}
				if (graveWeight <= 3.5) {
					if ((difficulty >= 1.909 + difficultymodifier && isUnlockSpecial()) || isUnlock()) {
						if (probability10 <= 0.2 / halfModifier * survChance) { // 20% x2 Book Burner
							for (int f = 0; f < 2 / halfModifier; ++f) {
								if (!MausoleumGraveEntity.this.is1x1()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
									zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
								}
								if (MausoleumGraveEntity.this.isChallengeGrave()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
									zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
								}
								BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
								SargeantEntity bookburner = (SargeantEntity) PvZEntity.BOOKBURNER.create(MausoleumGraveEntity.this.world);
								bookburner.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
								bookburner.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
								bookburner.setOwner(MausoleumGraveEntity.this);
								serverWorld.spawnEntityAndPassengers(bookburner);
								graveWeight += 1.25;
							}
						}
					}
				}
				if (graveWeight <= 3.5) {
					if ((difficulty >= 1.909 + difficultymodifier && isUnlockSpecial()) || isUnlock()) {
						if (probability12 <= 0.2 / halfModifier * survChance) { // 20% x1 Hawker Cart
							for (int f = 0; f < 1; ++f) {
								if (!MausoleumGraveEntity.this.is1x1()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-1, 1);
									zombiePos = MausoleumGraveEntity.this.random.range(-1, 1);
								}
								if (MausoleumGraveEntity.this.isChallengeGrave()) {
									zombiePosZ = MausoleumGraveEntity.this.random.range(-3, 3);
									zombiePos = MausoleumGraveEntity.this.random.range(-3, 3);
								}
								BlockPos blockPos = MausoleumGraveEntity.this.getBlockPos().add(zombiePos, 0.1, zombiePosZ);
								HawkerZombieEntity hawkerZombieEntity = (HawkerZombieEntity) PvZEntity.HAWKERPUSHER.create(MausoleumGraveEntity.this.world);
								hawkerZombieEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
								hawkerZombieEntity.initialize(serverWorld, MausoleumGraveEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (NbtCompound) null);
								hawkerZombieEntity.setOwner(MausoleumGraveEntity.this);
								serverWorld.spawnEntityAndPassengers(hawkerZombieEntity);
								graveWeight += 1;
							}
						}
					}
				}
			}
			++this.mausoleumGraveEntity.spawnCounter;
        }

        protected SoundEvent getSoundPrepare() {
            return PvZSounds.GRAVERISINGEVENT;
        }

        protected Spell getSpell() {
            return Spell.SUMMON_VEX;
        }
    }
}
