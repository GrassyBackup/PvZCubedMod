package io.github.GrassyDev.pvzmod.registry.entity.environment.target.missiletoe;

import io.github.GrassyDev.pvzmod.registry.entity.environment.TileEntity;
import io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.PlantEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public class MissileToeTarget extends TileEntity {

	public boolean hasPlant;
	protected int dragoTick = 360;
	public MissileToeTarget(EntityType<? extends TileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Environment(EnvType.CLIENT)
	public void handleStatus(byte status) {
		if (status != 2 && status != 60){
			super.handleStatus(status);
		}
	}

	private static final TrackedData<Integer> TARGET_ID;

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TARGET_ID, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);
	}

	public void readCustomDataFromNbt(NbtCompound tag) {
		super.readCustomDataFromNbt(tag);
		this.dataTracker.set(TARGET_ID, tag.getInt("TargetID"));
	}

	static {
		TARGET_ID = DataTracker.registerData(MissileToeTarget.class, TrackedDataHandlerRegistry.INTEGER);
	}

	public void setTargetID(int entityId) {
		this.dataTracker.set(TARGET_ID, entityId);
	}
	public int getTargetID(){
		return this.dataTracker.get(TARGET_ID);
	}

	public boolean hasTargetID() {
		return (Integer)this.dataTracker.get(TARGET_ID) != 0;
	}

	@Override
	public void tick() {
		super.tick();
		if (!hasTargetID()){
			this.discard();
		}
		List<PlantEntity> targetList = this.world.getNonSpectatingEntities(PlantEntity.class, this.getBoundingBox().expand(50));
		boolean targetIdBool = false;
		for (PlantEntity plantEntity : targetList){
			if (plantEntity.getId() == getTargetID()){
				targetIdBool = true;
			}
		}
		if (!targetIdBool){
			this.discard();
		}
	}
}
