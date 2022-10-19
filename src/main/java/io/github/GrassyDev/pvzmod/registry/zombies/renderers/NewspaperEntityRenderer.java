package io.github.GrassyDev.pvzmod.registry.zombies.renderers;

import io.github.GrassyDev.pvzmod.registry.zombies.models.NewspaperEntityModel;
import io.github.GrassyDev.pvzmod.registry.zombies.zombieentity.NewspaperEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class NewspaperEntityRenderer extends GeoEntityRenderer<NewspaperEntity> {

    public NewspaperEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new NewspaperEntityModel());
        this.shadowRadius = 0.7F; //change 0.7 to the desired shadow size.
    }

}
