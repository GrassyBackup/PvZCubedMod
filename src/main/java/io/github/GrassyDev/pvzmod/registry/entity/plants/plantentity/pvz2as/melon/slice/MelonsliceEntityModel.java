package io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz2as.melon.slice;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MelonsliceEntityModel extends AnimatedGeoModel<MelonsliceEntity> {

    @Override
    public Identifier getModelResource(MelonsliceEntity object)
    {
        return new Identifier("pvzmod", "geo/melonslice.geo.json");
    }

    @Override
    public Identifier getTextureResource(MelonsliceEntity object)
    {
        return new Identifier("pvzmod", "textures/entity/cabbagepult/melonslice.png");
    }

    @Override
    public Identifier getAnimationResource(MelonsliceEntity object)
    {
        return new Identifier ("pvzmod", "animations/cabbagepult.json");
    }
}
