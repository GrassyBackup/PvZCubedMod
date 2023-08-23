package io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz2c.browncoat.sargeant;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.GrassyDev.pvzmod.registry.ModItems;
import io.github.GrassyDev.pvzmod.registry.entity.variants.zombies.BrowncoatVariants;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz1.browncoat.modernday.BrowncoatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static io.github.GrassyDev.pvzmod.PvZCubed.PVZCONFIG;

public class SargeantEntity extends BrowncoatEntity {
	public SargeantEntity(EntityType<? extends BrowncoatEntity> entityType, World world) {
		super(entityType, world);
	}


	/** /~*~//~*INTERACTION*~//~*~/ **/

	@Nullable
	@Override
	public ItemStack getPickBlockStack() {
		ItemStack itemStack;
		if (this.getVariant().equals(BrowncoatVariants.CONEHEAD) || this.getVariant().equals(BrowncoatVariants.CONEHEADHYPNO)){
			itemStack = ModItems.PEASANTCONEEGG.getDefaultStack();
		}
		else if (this.getVariant().equals(BrowncoatVariants.BUCKETHEAD) || this.getVariant().equals(BrowncoatVariants.BUCKETHEADHYPNO)){
			itemStack = ModItems.PEASANTBUCKETEGG.getDefaultStack();
		}
		else if (this.getVariant().equals(BrowncoatVariants.PEASANTKNIGHT) || this.getVariant().equals(BrowncoatVariants.PEASANTKNIGHTHYPNO)){
			itemStack = ModItems.PEASANTKNIGHTEGG.getDefaultStack();
		}
		else{
			itemStack = ModItems.PEASANTEGG.getDefaultStack();
		}
		return itemStack;
	}

	/** /~*~//~*ATTRIBUTES*~//~*~/ **/

	public static DefaultAttributeContainer.Builder createSeargeantAttributes() {
		return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0D)
				.add(ReachEntityAttributes.ATTACK_RANGE, 2.5D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.12D)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
				.add(EntityAttributes.GENERIC_MAX_HEALTH, PVZCONFIG.nestedZombieHealth.sargeantH());
	}
}
