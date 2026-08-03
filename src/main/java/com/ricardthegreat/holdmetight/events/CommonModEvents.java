package com.ricardthegreat.holdmetight.events;

import com.ricardthegreat.holdmetight.HoldMeTight;
import com.ricardthegreat.holdmetight.init.RecipeInit;
import com.ricardthegreat.holdmetight.items.CollarItem;
import com.ricardthegreat.holdmetight.items.EntityStandinItem;
import com.ricardthegreat.holdmetight.network.PacketHandler;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(modid = HoldMeTight.MODID)
public class CommonModEvents {
    
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PacketHandler.register(event);
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        RecipeInit.register(event.getBuilder());
    }

    // Curios 9.x only accepts an item into a slot if one of the slot's registered
    // validators (item predicates) passes for that item. We register a predicate
    // keyed by slot-validator id that recognises the carried-mob / carried-player
    // standin items (and the collar) directly, rather than relying on cross-namespace
    // datapack item tags.
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        CuriosApi.registerCurioPredicate(
            ResourceLocation.fromNamespaceAndPath(HoldMeTight.MODID, "carry"),
            slotResult -> slotResult.stack().getItem() instanceof EntityStandinItem);
        CuriosApi.registerCurioPredicate(
            ResourceLocation.fromNamespaceAndPath(HoldMeTight.MODID, "collar"),
            slotResult -> slotResult.stack().getItem() instanceof CollarItem);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        //exists for if i want to generate curios stuff in the future
        /* 
        event.getGenerator().addProvider(
            event.includeServer(), 
            new HMTCurioDataProvider(HoldMeTight.MODID, event.getGenerator().getPackOutput(), event.getExistingFileHelper(), event.getLookupProvider()));
            */
    }
}
