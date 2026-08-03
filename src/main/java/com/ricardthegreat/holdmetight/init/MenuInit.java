package com.ricardthegreat.holdmetight.init;

import com.ricardthegreat.holdmetight.HoldMeTight;
import com.ricardthegreat.holdmetight.entities.projectile.RayGunProjectile;
import com.ricardthegreat.holdmetight.entities.projectile.WandProjectile;
import com.ricardthegreat.holdmetight.inventory.HeldEntityInventoryMenu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MenuInit {
    
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, HoldMeTight.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<HeldEntityInventoryMenu>> HELD_PLAYER_MENU =
    MENUS.register("held_player_menu", () -> new MenuType<HeldEntityInventoryMenu>(HeldEntityInventoryMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus bus){
        MENUS.register(bus);
    }
}
