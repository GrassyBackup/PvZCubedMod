package io.github.GrassyDev.pvzmod.registry.entity.environment.rifttile;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RiftTileModel extends AnimatedGeoModel<RiftTile> {

    @Override
    public Identifier getModelResource(RiftTile object)
    {
        return new Identifier("pvzmod", "geo/rifttile.geo.json");
    }

    @Override
    public Identifier getTextureResource(RiftTile object)
    {
        return new Identifier("pvzmod", "textures/entity/environment/tiles.png");
    }

    @Override
    public Identifier getAnimationResource(RiftTile object)
    {
        return new Identifier ("pvzmod", "animations/tile.json");
    }
}
