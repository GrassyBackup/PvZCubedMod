package io.github.GrassyDev.pvzmod.registry.entity.plants.plantentity.pvz1.night.doomshroom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class DoomIgniteGoal extends Goal {
    private final DoomshroomEntity doom;
    private LivingEntity target;

    public DoomIgniteGoal(DoomshroomEntity doom) {
        this.doom = doom;
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.doom.getTarget();
		if (doom.getShadowPowered()){
			return (this.doom.getFuseSpeed() > 0 || livingEntity != null && this.doom.squaredDistanceTo(livingEntity) < 9D) && !this.doom.getIsAsleep();
		}
		else {
			return (this.doom.getFuseSpeed() > 0 || livingEntity != null && this.doom.squaredDistanceTo(livingEntity) < 25.0D) && !this.doom.getIsAsleep();
		}
    }

    public void start() {
        this.doom.getNavigation().stop();
        this.target = this.doom.getTarget();
    }

    public void stop() {
        this.target = null;
    }

    public void tick() {
		if (doom.getShadowPowered()) {
			if (!doom.getIsAsleep()) {
				if (this.target == null) {
					this.doom.setFuseSpeed(-1);
				} else if (this.doom.squaredDistanceTo(this.target) > 9.0D || this.doom.isInsideWaterOrBubbleColumn()) {
					this.doom.setFuseSpeed(-1);
				} else {
					this.doom.setFuseSpeed(1);
				}
			} else {
				this.doom.setFuseSpeed(-1);
			}
		}
		else {
			if (!doom.getIsAsleep()) {
				if (this.target == null) {
					this.doom.setFuseSpeed(-1);
				} else if (this.doom.squaredDistanceTo(this.target) > 25.0D || this.doom.isInsideWaterOrBubbleColumn()) {
					this.doom.setFuseSpeed(-1);
				} else {
					this.doom.setFuseSpeed(1);
				}
			} else {
				this.doom.setFuseSpeed(-1);
			}
		}
	}
}
