package io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz2c.browncoat.fairytale;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.GrassyDev.pvzmod.registry.entity.variants.zombies.BrowncoatVariants;
import io.github.GrassyDev.pvzmod.registry.entity.zombies.zombieentity.pvz2.browncoat.darkages.PeasantEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Map;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class PokerEntityRenderer extends GeoEntityRenderer<PokerEntity> {

    public PokerEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new PokerEntityModel());
        this.shadowRadius = 0.7F; //change 0.7 to the desired shadow size.
    }

	public static final Map<BrowncoatVariants, Identifier> LOCATION_MODEL_BY_VARIANT =
			Util.make(Maps.newEnumMap(BrowncoatVariants.class), (map) -> {
				map.put(BrowncoatVariants.BROWNCOAT,
				        new Identifier("pvzmod", "geo/pokerzombie.geo.json"));
				map.put(BrowncoatVariants.BROWNCOATHYPNO,
						new Identifier("pvzmod", "geo/pokerzombie.geo.json"));
				map.put(BrowncoatVariants.CONEHEAD,
						new Identifier("pvzmod", "geo/pokerconehead.geo.json"));
				map.put(BrowncoatVariants.CONEHEADHYPNO,
						new Identifier("pvzmod", "geo/pokerconehead.geo.json"));
				map.put(BrowncoatVariants.BUCKETHEAD,
						new Identifier("pvzmod", "geo/pokerbuckethead.geo.json"));
				map.put(BrowncoatVariants.BUCKETHEADHYPNO,
						new Identifier("pvzmod", "geo/pokerbuckethead.geo.json"));
				map.put(BrowncoatVariants.POKERPAWN,
						new Identifier("pvzmod", "geo/pokerpawn.geo.json"));
				map.put(BrowncoatVariants.POKERPAWNHYPNO,
						new Identifier("pvzmod", "geo/pokerpawn.geo.json"));
				map.put(BrowncoatVariants.POKERKNIGHT,
						new Identifier("pvzmod", "geo/pokerknight.geo.json"));
				map.put(BrowncoatVariants.POKERKNIGHTHYPNO,
						new Identifier("pvzmod", "geo/pokerknight.geo.json"));
				map.put(BrowncoatVariants.POKERTOWER,
						new Identifier("pvzmod", "geo/pokertower.geo.json"));
				map.put(BrowncoatVariants.POKERTOWERHYPNO,
						new Identifier("pvzmod", "geo/pokertower.geo.json"));
				map.put(BrowncoatVariants.POKERBISHOP,
						new Identifier("pvzmod", "geo/pokerbishop.geo.json"));
				map.put(BrowncoatVariants.POKERBISHOPHYPNO,
						new Identifier("pvzmod", "geo/pokerbishop.geo.json"));
			});

	public Identifier getModelResource(PeasantEntity object) {
		return LOCATION_MODEL_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public void render(GeoModel model, PokerEntity animatable, float partialTick, RenderLayer type, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (animatable.getRainbow()) {
			float s;
			float t;
			float u;
			int n = animatable.age / 25 + animatable.getId();
			int o = DyeColor.values().length;
			int p = n % o;
			int q = (n + 1) % o;
			float r = ((float) (animatable.age % 25) + alpha) / 25.0F;
			float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
			float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
			s = fs[0] * (1.0F - r) + gs[0] * r;
			t = fs[1] * (1.0F - r) + gs[1] * r;
			u = fs[2] * (1.0F - r) + gs[2] * r;
			super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, 255, packedOverlay, s, t, u, alpha);
		}
		else
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
}
