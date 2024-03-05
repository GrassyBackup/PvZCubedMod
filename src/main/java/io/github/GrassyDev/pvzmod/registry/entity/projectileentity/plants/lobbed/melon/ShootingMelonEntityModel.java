package io.github.GrassyDev.pvzmod.registry.entity.projectileentity.plants.lobbed.melon;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ShootingMelonEntityModel extends AnimatedGeoModel<ShootingMelonEntity> {

    @Override
    public Identifier getModelResource(ShootingMelonEntity object)
    {
        return new Identifier("pvzmod", "geo/melon.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShootingMelonEntity object){
			return new Identifier("pvzmod", "textures/entity/cabbagepult/melonpult.png");
	}

    @Override
    public Identifier getAnimationResource(ShootingMelonEntity object)
    {
        return new Identifier ("pvzmod", "animations/peashot.json");
    }
}
