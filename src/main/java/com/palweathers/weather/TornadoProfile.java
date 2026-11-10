package com.palweathers.weather;

/**
 * Palweathers V2 - Tornado size/power profile.
 *
 * windSpeed: 45 to 400 mph (matches the EF scale already used in earlier versions)
 * widthMeters: 10 to 1000 (roughly: 120 = small rope, 333 = wedge, 1000 = giant wedge)
 * The hitbox radius in-world is derived from widthMeters, so a bigger tornado
 * actually pulls in and damages a wider area of blocks/entities.
 */
public class TornadoProfile {

    public final int windSpeed;   // mph
    public final int widthMeters; // real-world-ish width
    public final boolean onFire;

    public TornadoProfile(int windSpeed, int widthMeters, boolean onFire) {
        this.windSpeed = clamp(windSpeed, 45, 400);
        this.widthMeters = clamp(widthMeters, 10, 1000);
        // Fire tornado rule: only EFU-EF3 can be on fire. EF4 and EF5 are too
        // violent/cold-core to sustain a fire funnel, so force it off above EF3.
        this.onFire = onFire && getEfRating() <= 3;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    /** EF rating from wind speed, same bands used by earlier Palweathers versions. */
    public int getEfRating() {
        if (windSpeed <= 64) return 0;      // EFU
        if (windSpeed <= 85) return 0;
        if (windSpeed <= 101) return 1;
        if (windSpeed <= 134) return 2;
        if (windSpeed <= 164) return 3;
        if (windSpeed <= 199) return 4;
        return 5;
    }

    /** Hitbox radius in blocks, scaled from width. 120 wide -> ~10 block radius, 1000 wide -> ~40 block radius. */
    public double getHitboxRadius() {
        double normalized = widthMeters / 1000.0;
        return 8.0 + normalized * 32.0;
    }

    /** Whether this tornado is strong enough to rip up roads/terrain, not just grass. */
    public boolean canRipTerrain() {
        return getEfRating() >= 4;
    }

    public String getFunnelColor() {
        int ef = getEfRating();
        if (onFire) return "fire"; // orange/red fire-tornado palette
        return ef <= 2 ? "rope_brown" : "wedge_dark_grey";
    }
}
