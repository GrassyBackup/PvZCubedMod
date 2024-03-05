package io.github.GrassyDev.pvzmod.registry.entity.projectileentity.plants.lobbed.icemelon;


import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ShootingIceMelonEntityRenderer extends GeoProjectilesRenderer {

	public ShootingIceMelonEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new ShootingIceMelonEntityModel());
		this.shadowRadius = 0.3F; //change 0.7 to the desired shadow size.
	}
}
