package io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz1.upgrades.wintermelon;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WinterMelonEntityModel extends AnimatedGeoModel<WinterMelonEntity> {

    @Override
    public Identifier getModelResource(WinterMelonEntity object)
    {
        return new Identifier("pvzmod", "geo/wintermelon.geo.json");
    }

    @Override
    public Identifier getTextureResource(WinterMelonEntity object)
    {
        return new Identifier("pvzmod", "textures/entity/cabbagepult/wintermelon.png");
    }

    @Override
    public Identifier getAnimationResource(WinterMelonEntity object)
    {
        return new Identifier ("pvzmod", "animations/cabbagepult.json");
    }
}
