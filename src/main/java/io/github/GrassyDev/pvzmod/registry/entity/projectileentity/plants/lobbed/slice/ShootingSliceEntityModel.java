package io.github.GrassyDev.pvzmod.registry.entity.projectileentity.plants.lobbed.slice;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ShootingSliceEntityModel extends AnimatedGeoModel<ShootingSliceEntity> {

    @Override
    public Identifier getModelResource(ShootingSliceEntity object)
    {
        return new Identifier("pvzmod", "geo/slice.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShootingSliceEntity object){
			return new Identifier("pvzmod", "textures/entity/cabbagepult/melonslice.png");
	}

    @Override
    public Identifier getAnimationResource(ShootingSliceEntity object)
    {
        return new Identifier ("pvzmod", "animations/peashot.json");
    }
}
