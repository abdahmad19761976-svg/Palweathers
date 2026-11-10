package com.palweathers.weather;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Palweathers V2 - new sound events.
 * Three new lightning sounds, one per severe-weather context, so a storm,
 * a hurricane and a supercell don't all sound the same.
 */
public class WeatherSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, "palweathers");

    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNING_STORM =
            SOUNDS.register("lightning_storm", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("palweathers", "lightning_storm")));

    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNING_HURRICANE =
            SOUNDS.register("lightning_hurricane", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("palweathers", "lightning_hurricane")));

    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNING_SUPERCELL =
            SOUNDS.register("lightning_supercell", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("palweathers", "lightning_supercell")));

    // Call SOUNDS.register(modEventBus) once in your mod's constructor, same
    // place you already register blocks/entities.
}
