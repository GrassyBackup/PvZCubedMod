package io.github.GrassyDev.pvzmod.registry.entity.projectileentity.plants.lobbed.slice;


import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ShootingSliceEntityRenderer extends GeoProjectilesRenderer {

	public ShootingSliceEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new ShootingSliceEntityModel());
		this.shadowRadius = 0.3F; //change 0.7 to the desired shadow size.
	}
}
