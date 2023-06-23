package io.github.GrassyDev.pvzmod.registry.entity.projectileentity.armor;

import com.google.common.collect.Maps;
import io.github.GrassyDev.pvzmod.PvZCubed;
import io.github.GrassyDev.pvzmod.registry.entity.variants.projectiles.MetalHelmetVariants;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

import java.util.Map;

public class MetalHelmetProjEntityRenderer extends GeoProjectilesRenderer {

	public static final Map<MetalHelmetVariants, Identifier> LOCATION_BY_VARIANT =
			Util.make(Maps.newEnumMap(MetalHelmetVariants.class), (map) -> {
				map.put(MetalHelmetVariants.BUCKET,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/browncoat.png"));
				map.put(MetalHelmetVariants.MUMMYBUCKET,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/mummy/mummy.png"));
				map.put(MetalHelmetVariants.PEASANTBUCKET,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/peasant/peasant.png"));
				map.put(MetalHelmetVariants.SCREENDOOR,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/browncoat.png"));
				map.put(MetalHelmetVariants.FOOTBALL,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/football/football.png"));
				map.put(MetalHelmetVariants.BERSERKER,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/football/berserker.png"));
				map.put(MetalHelmetVariants.DEFENSIVEEND,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/gargantuar/defensiveend.png"));
				map.put(MetalHelmetVariants.TRASHCAN,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/browncoat.png"));
				map.put(MetalHelmetVariants.BLASTRONAUT,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/jetpack/blastronaut.png"));
				map.put(MetalHelmetVariants.KNIGHT,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/peasant/peasant.png"));
				map.put(MetalHelmetVariants.MEDALLION,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/dancingzombie/backupdancer.png"));
			});

	public static final Map<MetalHelmetVariants, Identifier> LOCATION_BY_VARIANT_DAMAGED =
			Util.make(Maps.newEnumMap(MetalHelmetVariants.class), (map) -> {
				map.put(MetalHelmetVariants.BUCKET,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/browncoat_geardmg1.png"));
				map.put(MetalHelmetVariants.MUMMYBUCKET,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/mummy/mummy_geardmg1.png"));
				map.put(MetalHelmetVariants.PEASANTBUCKET,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/peasant/peasant_geardmg1.png"));
				map.put(MetalHelmetVariants.SCREENDOOR,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/browncoat_geardmg1.png"));
				map.put(MetalHelmetVariants.FOOTBALL,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/football/football_geardmg1.png"));
				map.put(MetalHelmetVariants.BERSERKER,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/football/berserker_geardmg1.png"));
				map.put(MetalHelmetVariants.DEFENSIVEEND,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/gargantuar/defensiveend.png"));
				map.put(MetalHelmetVariants.TRASHCAN,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/browncoat_geardmg1.png"));
				map.put(MetalHelmetVariants.BLASTRONAUT,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/jetpack/blastronaut_geardmg1.png"));
				map.put(MetalHelmetVariants.KNIGHT,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/browncoat/peasant/peasant_geardmg1.png"));
				map.put(MetalHelmetVariants.MEDALLION,
						new Identifier(PvZCubed.MOD_ID, "textures/entity/dancingzombie/backupdancer_geardmg1.png"));
			});

	public static final Map<MetalHelmetVariants, Identifier> MODEL_LOCATION_BY_VARIANT =
			Util.make(Maps.newEnumMap(MetalHelmetVariants.class), (map) -> {
				map.put(MetalHelmetVariants.BUCKET,
						new Identifier(PvZCubed.MOD_ID, "geo/bucketproj.geo.json"));
				map.put(MetalHelmetVariants.MUMMYBUCKET,
						new Identifier(PvZCubed.MOD_ID, "geo/mummybucketproj.geo.json"));
				map.put(MetalHelmetVariants.PEASANTBUCKET,
						new Identifier(PvZCubed.MOD_ID, "geo/peasantbucketproj.geo.json"));
				map.put(MetalHelmetVariants.SCREENDOOR,
						new Identifier(PvZCubed.MOD_ID, "geo/screendoorproj.geo.json"));
				map.put(MetalHelmetVariants.FOOTBALL,
						new Identifier(PvZCubed.MOD_ID, "geo/footballproj.geo.json"));
				map.put(MetalHelmetVariants.BERSERKER,
						new Identifier(PvZCubed.MOD_ID, "geo/berserkerproj.geo.json"));
				map.put(MetalHelmetVariants.DEFENSIVEEND,
						new Identifier(PvZCubed.MOD_ID, "geo/defensiveendproj.geo.json"));
				map.put(MetalHelmetVariants.TRASHCAN,
						new Identifier(PvZCubed.MOD_ID, "geo/trashcanproj.geo.json"));
				map.put(MetalHelmetVariants.BLASTRONAUT,
						new Identifier(PvZCubed.MOD_ID, "geo/blastronautproj.geo.json"));
				map.put(MetalHelmetVariants.KNIGHT,
						new Identifier(PvZCubed.MOD_ID, "geo/knightproj.geo.json"));
				map.put(MetalHelmetVariants.MEDALLION,
						new Identifier(PvZCubed.MOD_ID, "geo/medallionproj.geo.json"));
			});

	public MetalHelmetProjEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new MetalHelmetProjEntityModel());
		this.shadowRadius = 0.3F; //change 0.7 to the desired shadow size.
	}

	@Override
	public float getWidthScale(Entity animatable) {
		return ((MetalHelmetProjEntity) animatable).magnetized && !((MetalHelmetProjEntity) animatable).keepSize ?
				(float) ((MetalHelmetProjEntity) animatable).reverseAge / ((MetalHelmetProjEntity) animatable).getMaxAge() : 1;
	}

	@Override
	public float getHeightScale(Entity animatable) {
		return ((MetalHelmetProjEntity) animatable).magnetized && !((MetalHelmetProjEntity) animatable).keepSize ?
				(float) ((MetalHelmetProjEntity) animatable).reverseAge / ((MetalHelmetProjEntity) animatable).getMaxAge() : 1;
	}
}
