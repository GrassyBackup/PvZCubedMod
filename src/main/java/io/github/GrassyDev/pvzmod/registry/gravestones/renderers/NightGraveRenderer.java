package io.github.GrassyDev.pvzmod.registry.gravestones.renderers;

import io.github.GrassyDev.pvzmod.registry.gravestones.gravestoneentity.NightGraveEntity;
import io.github.GrassyDev.pvzmod.registry.gravestones.models.NightGraveModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class NightGraveRenderer extends GeoEntityRenderer<NightGraveEntity> {

    public NightGraveRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new NightGraveModel());
        this.shadowRadius = 0.5F; //change 0.7 to the desired shadow size.
    }

}
