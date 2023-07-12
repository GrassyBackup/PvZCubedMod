package io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.zomblob;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.GrassyDev.pvzmod.registry.PvZEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class ZomblobEntityRenderer extends GeoEntityRenderer<ZomblobEntity> {

    public ZomblobEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new ZomblobEntityModel());
        this.shadowRadius = 0.85F; //change 0.7 to the desired shadow size.
    }

	@Override
	public void render(GeoModel model, ZomblobEntity animatable, float partialTick, RenderLayer type, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (animatable.getHypno()) {
			super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, 255, packedOverlay, 1, 255, 1, alpha);
		}
		else if (animatable.fireSplashTicks > 0){
			super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, 1, 255, 255, alpha);
		}
		else if(animatable.isIced || animatable.isFrozen){
			super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, 255, 75, 1, alpha);
		}
		else if (animatable.isPoisoned){
			super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, 100, 255, 1, alpha);
		}
		else {
			super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	@Override
	public float getWidthScale(ZomblobEntity animatable) {
		if (animatable.getType().equals(PvZEntity.ZOMBLOBBIG) || animatable.getType().equals(PvZEntity.ZOMBLOBBIGHYPNO)){
			return 1.5f;
		}
		else if (animatable.getType().equals(PvZEntity.ZOMBLOBSMALL) || animatable.getType().equals(PvZEntity.ZOMBLOBSMALLHYPNO)){
			return 0.75f;
		}
		else {
			return 1f;
		}
	}

	@Override
	public float getHeightScale(ZomblobEntity entity) {
		if (entity.getType().equals(PvZEntity.ZOMBLOBBIG) || entity.getType().equals(PvZEntity.ZOMBLOBBIGHYPNO)){
			return 1.5f;
		}
		else if (entity.getType().equals(PvZEntity.ZOMBLOBSMALL) || entity.getType().equals(PvZEntity.ZOMBLOBSMALLHYPNO)){
			return 0.75f;
		}
		else {
			return 1f;
		}
	}
}
