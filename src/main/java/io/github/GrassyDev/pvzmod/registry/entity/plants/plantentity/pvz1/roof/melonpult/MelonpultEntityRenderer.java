package io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz1.roof.melonpult;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class MelonpultEntityRenderer extends GeoEntityRenderer<MelonpultEntity> {

    public MelonpultEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new MelonpultEntityModel());
        this.shadowRadius = 0.9F; //change 0.7 to the desired shadow size.
    }

}
