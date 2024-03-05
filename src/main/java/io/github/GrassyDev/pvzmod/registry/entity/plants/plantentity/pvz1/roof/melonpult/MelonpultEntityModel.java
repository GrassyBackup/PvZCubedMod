package io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz1.roof.melonpult;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MelonpultEntityModel extends AnimatedGeoModel<MelonpultEntity> {

    @Override
    public Identifier getModelResource(MelonpultEntity object)
    {
        return new Identifier("pvzmod", "geo/melonpult.geo.json");
    }

    @Override
    public Identifier getTextureResource(MelonpultEntity object)
    {
        return new Identifier("pvzmod", "textures/entity/cabbagepult/melonpult.png");
    }

    @Override
    public Identifier getAnimationResource(MelonpultEntity object)
    {
        return new Identifier ("pvzmod", "animations/cabbagepult.json");
    }
}
