package io.github.GrassyDev.pvzmod.registry.plants.models;

import io.github.GrassyDev.pvzmod.registry.plants.plantentity.WallnutEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WallnutEntityModel extends AnimatedGeoModel<WallnutEntity> {

    @Override
    public Identifier getModelResource(WallnutEntity object)
    {
        return new Identifier("pvzcubed", "geo/wallnut.geo.json");
    }

    @Override
    public Identifier getTextureResource(WallnutEntity object)
    {
        return new Identifier("pvzcubed", "textures/entity/wallnut/wallnut.png");
    }

    @Override
    public Identifier getAnimationResource(WallnutEntity object)
    {
        return new Identifier ("pvzcubed", "animations/wallnut.json");
    }
}
