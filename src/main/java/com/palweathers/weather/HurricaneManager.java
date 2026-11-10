package com.palweathers.weather;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

/**
 * Palweathers V2 - Hurricane system.
 *
 * Categories C0 to C5, each with a wind speed range and a surge rate.
 * Storm surge now ALWAYS rises (water goes up), it never lowers the water
 * like the old hurricane code did. The surge speed scales with category.
 */
public class HurricaneManager {

    public enum Category {
        C0(0, 73, 0.03f),
        C1(74, 95, 0.05f),
        C2(96, 110, 0.07f),
        C3(111, 129, 0.09f),
        C4(130, 156, 0.12f),
        C5(157, 400, 0.16f);

        public final int minWind;
        public final int maxWind;
        public final float surgeRate; // blocks of water height gained per in-game minute

        Category(int minWind, int maxWind, float surgeRate) {
            this.minWind = minWind;
            this.maxWind = maxWind;
            this.surgeRate = surgeRate;
        }

        public static Category fromWindSpeed(int windSpeed) {
            for (Category c : values()) {
                if (windSpeed >= c.minWind && windSpeed <= c.maxWind) {
                    return c;
                }
            }
            return windSpeed > C5.maxWind ? C5 : C0;
        }
    }

    // Tracks current surge height (in blocks above normal sea level) per active hurricane center.
    private final Map<BlockPos, Float> surgeHeightByCenter = new HashMap<>();

    /**
     * Call once per weather tick for each active hurricane.
     * Water level only ever goes UP while the hurricane is active; it does not
     * push water back down. Recede it slowly elsewhere once the storm ends.
     */
    public void tickSurge(Level level, BlockPos center, int radius, int windSpeed) {
        Category cat = Category.fromWindSpeed(windSpeed);
        float current = surgeHeightByCenter.getOrDefault(center, 0f);
        current += cat.surgeRate / 1200f; // per tick, 1200 ticks per in-game minute
        surgeHeightByCenter.put(center, current);

        int riseBlocks = Math.round(current);
        if (riseBlocks <= 0) return;

        int seaLevel = level.getSeaLevel();
        for (int dx = -radius; dx <= radius; dx += 4) {
            for (int dz = -radius; dz <= radius; dz += 4) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius) continue;
                BlockPos column = center.offset(dx, 0, dz);
                raiseWaterColumn(level, column, seaLevel, riseBlocks);
            }
        }
    }

    private void raiseWaterColumn(Level level, BlockPos column, int seaLevel, int riseBlocks) {
        int targetY = seaLevel + riseBlocks;
        for (int y = seaLevel; y <= targetY; y++) {
            BlockPos pos = new BlockPos(column.getX(), y, column.getZ());
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) {
                level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            }
            // Solid, non-water blocks are left alone; the surge rises around them,
            // it does not replace terrain.
        }
    }

    /** Slowly lowers surge back to 0 once the hurricane has passed. Call after the storm ends. */
    public void recede(BlockPos center, float amountPerTick) {
        float current = surgeHeightByCenter.getOrDefault(center, 0f);
        current = Math.max(0f, current - amountPerTick);
        if (current <= 0f) {
            surgeHeightByCenter.remove(center);
        } else {
            surgeHeightByCenter.put(center, current);
        }
    }

    public Category getCategory(int windSpeed) {
        return Category.fromWindSpeed(windSpeed);
    }
}
