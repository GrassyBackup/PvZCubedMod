package io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz2.gemium.ghostpepper;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GhostpepperEntityModel extends AnimatedGeoModel<GhostpepperEntity> {

    @Override
    public Identifier getModelResource(GhostpepperEntity object)
    {
        return new Identifier("pvzmod", "geo/ghostpepper.geo.json");
    }

    @Override
    public Identifier getTextureResource(GhostpepperEntity object)
    {
        return new Identifier("pvzmod", "textures/entity/ghostpepper/ghostpepper.png");
    }

    @Override
    public Identifier getAnimationResource(GhostpepperEntity object)
    {
        return new Identifier ("pvzmod", "animations/ghostpepper.json");
    }
}
