package io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz2.gemium.ghostpepper;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class GhostPepperEntityRenderer extends GeoEntityRenderer<GhostpepperEntity> {

    public GhostPepperEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new GhostpepperEntityModel());
        this.shadowRadius = 0F; //change 0.7 to the desired shadow size.
    }

}
