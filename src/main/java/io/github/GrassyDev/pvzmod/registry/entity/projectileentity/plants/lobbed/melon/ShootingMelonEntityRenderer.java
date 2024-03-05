package io.github.GrassyDev.pvzmod.registry.entity.projectileentity.plants.lobbed.melon;


import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ShootingMelonEntityRenderer extends GeoProjectilesRenderer {

	public ShootingMelonEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new ShootingMelonEntityModel());
		this.shadowRadius = 0.3F; //change 0.7 to the desired shadow size.
	}
}
