package io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieprops.metallicshield;

import io.github.GrassyDev.pvzmod.registry.ModItems;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import io.github.GrassyDev.pvzmod.registry.PvZSounds;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombietypes.ZombieShieldEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import static io.github.GrassyDev.pvzmod.PvZCubed.PVZCONFIG;

public class MetalShieldEntity extends ZombieShieldEntity implements IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private String controllerName = "shieldcontroller";

    public MetalShieldEntity(EntityType<? extends MetalShieldEntity> entityType, World world) {
        super(entityType, world);

        this.experiencePoints = 3;
	}

	public MetalShieldEntity(World world) {
		this(PvZEntity.SCREENDOORSHIELD, world);
	}

	static {

	}

	@Environment(EnvType.CLIENT)
	public void handleStatus(byte status) {
		if (status != 2 && status != 60){
			super.handleStatus(status);
		}
	}

	/** /~*~//~*TICKING*~//~*~/ **/


	public void tick() {
		super.tick();
		if (this.getVehicle() == null){
			this.kill();
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
        return PlayState.CONTINUE;
    }


	/** /~*~//~*ATTRIBUTES*~//~*~/ **/

	public static DefaultAttributeContainer.Builder createScreendoorShieldAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0D)
				.add(ReachEntityAttributes.ATTACK_RANGE, 1.5D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.12D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, PVZCONFIG.nestedZombieHealth.screendoorShieldH());
    }

	protected SoundEvent getAmbientSound() {
		return PvZSounds.SILENCEVENET;
	}

	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	protected SoundEvent getStepSound() {
		return PvZSounds.SILENCEVENET;
	}

	@Nullable
	@Override
	public ItemStack getPickBlockStack() {
		return ModItems.SCREENDOOREGG.getDefaultStack();
	}
}
