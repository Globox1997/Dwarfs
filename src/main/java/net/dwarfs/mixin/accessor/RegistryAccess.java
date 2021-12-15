package net.dwarfs.mixin.accessor;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

@Mixin(Registry.class)
public interface RegistryAccess {

    @Invoker("createRegistryKey")
    static <T> RegistryKey<Registry<T>> callCreateRegistryKey(String registryId) {
        throw new AssertionError("Untransformed accessor!");
    }

    @Invoker("create")
    static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Supplier<T> defaultEntry) {
        throw new AssertionError("Untransformed accessor!");
    }

    // private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Supplier<T> defaultEntry) {
    // return Registry.create(key, defaultId, Lifecycle.experimental(), defaultEntry);
    // }

    // private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Supplier<T> defaultEntry) {
    // return Registry.create(key, defaultId, Lifecycle.experimental(), defaultEntry);
    // }

}
