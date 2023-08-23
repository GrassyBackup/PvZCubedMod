package io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz2c.browncoat.sargeant;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SargeantEntityModel extends AnimatedGeoModel<SargeantEntity> {

    @Override
    public Identifier getModelResource(SargeantEntity object)
    {
		return SargeantEntityRenderer.LOCATION_MODEL_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getTextureResource(SargeantEntity object) {
		Identifier identifier;
		identifier = new Identifier("pvzmod", "textures/entity/browncoat/sargeant/sargeant.png");
		if (object.armless && object.geardmg) {
			identifier = new Identifier("pvzmod", "textures/entity/browncoat/sargeant/sargeant_dmg1_geardmg1.png");
		} else if (object.armless && object.gearless) {
			identifier = new Identifier("pvzmod", "textures/entity/browncoat/sargeant/sargeant_gearless_dmg1.png");
		} else if (object.gearless) {
			identifier = new Identifier("pvzmod", "textures/entity/browncoat/sargeant/sargeant_gearless.png");
		} else if (object.geardmg) {
			identifier = new Identifier("pvzmod", "textures/entity/browncoat/sargeant/sargeant_geardmg1.png");
		} else if (object.armless) {
			identifier = new Identifier("pvzmod", "textures/entity/browncoat/sargeant/sargeant_dmg1.png");
		}
		return identifier;
    }

    @Override
    public Identifier getAnimationResource(SargeantEntity object)
    {
        return new Identifier ("pvzmod", "animations/newbrowncoat.json");
    }
}
