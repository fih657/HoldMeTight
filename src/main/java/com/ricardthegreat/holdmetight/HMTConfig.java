package com.ricardthegreat.holdmetight;

import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = HoldMeTight.MODID)
public class HMTConfig {
        public static final ClientConfig CLIENT_CONFIG;
        public static final CommonConfig COMMON_CONFIG;
        public static final ServerConfig SERVER_CONFIG;

        public static final ModConfigSpec clientSpec;
        public static final ModConfigSpec commonSpec;
        public static final ModConfigSpec serverSpec;

        @SubscribeEvent
        public static void onLoad(ModConfigEvent event){
                HoldMeTight.LOGGER.debug("loaded config: " + event.getConfig().getFileName());
        }

        static {
                final Pair<ServerConfig, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
                serverSpec = serverPair.getRight();
                SERVER_CONFIG = serverPair.getLeft();

                final Pair<CommonConfig, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
                commonSpec = commonPair.getRight();
                COMMON_CONFIG = commonPair.getLeft();

                final Pair<ClientConfig, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
                clientSpec = clientPair.getRight();
                CLIENT_CONFIG = clientPair.getLeft();
        }

        public static class ClientConfig {
                private final ModConfigSpec.ConfigValue<Double> scaleMinimum;
                private final ModConfigSpec.ConfigValue<Double> scaleMaximum;
                private final ModConfigSpec.ConfigValue<Double> scaleDefault;
                public final ModConfigSpec.BooleanValue inventoryCanBeAccessed;
                public final ModConfigSpec.BooleanValue trapCarriedPlayer;
                public final ModConfigSpec.BooleanValue canBeTrappedWhileCarried;
                public final ModConfigSpec.BooleanValue sableEntityLightingFix;
                public final ModConfigSpec.BooleanValue sableTinyCrouchFix;

                ClientConfig(ModConfigSpec.Builder builder){
                        builder.comment("ClientSide Config Settings, These mostly exist as preference setting so you dont need to customise them every server you join (currently these do nothing and are just placeholders)");
                        
                        builder.push("Size Preferences");
                        //TODO make these translations
                        this.scaleMinimum = builder.comment("The minimum scale that you can be set to")
                                .define("scaleMinimum", 0d);
                        this.scaleMaximum = builder.comment("The maximum scale that you can be set to")
                                .define("scaleMaximum", Double.MAX_VALUE);
                        this.scaleDefault = builder.comment("Your default scale, what you will be set to when scale is 'reset' such as through a size remotes reset button")
                                .define("scaleDefault", 1d);
                        builder.pop();

                        builder.push("Carry Preferences");
                        this.inventoryCanBeAccessed = builder.comment("If your inventory can be accessed and changed by a player who is carrying you")
                                .define("inventoryCanBeAccessed", true);
                        this.trapCarriedPlayer = builder.comment("Prevent players you are carrying from dismounting by 'shifting'")
                                .define("trapCarriedPlayer", true);
                        this.canBeTrappedWhileCarried = builder.comment("allow yourself to be prevented from dismounting by players carrying you that have the above option enabled")
                                .define("canBeTrappedWhileCarried", true);
                        builder.pop();

                        builder.push("Sable Compatibility");
                        this.sableEntityLightingFix = builder.comment("Workaround for entities on Sable sub-levels rendering dark at certain sub-level positions. Sable's entity lighting falls back to the empty main world when it cannot resolve the sub-level plot for the entity, so we re-sample the sub-level's own light here. Disable this once Sable fixes its own entity lighting so the workaround no longer needs to apply.")
                                .define("sableEntityLightingFix", true);
                        this.sableTinyCrouchFix = builder.comment("Workaround for tiny players (<~1/3 scale) not being able to crouch on Sable sub-levels. Sable's player-fit check for sub-levels builds a collision box of size (width-0.1), which goes negative for tiny players and always reports a collision, so the crouch pose never applies. We report that check as fitting for tiny players on sub-levels. Disable this once Sable fixes it.")
                                .define("sableTinyCrouchFix", true);
                        builder.pop();

                        builder.build();
                }

                public boolean isSableEntityLightingFixEnabled(){
                        return this.sableEntityLightingFix.get();
                }

                public boolean isSableTinyCrouchFixEnabled(){
                        return this.sableTinyCrouchFix.get();
                }

                public float getMinScale(){
                        if (scaleMinimum.get() > scaleMaximum.get()) {
                                HoldMeTight.LOGGER.error("error min scale in client config greater than max scale resetting all scale options to default");
                                resetScaleOptions();
                        }
                        return scaleMinimum.get().floatValue();
                }

                public float getMaxScale(){
                        if (scaleMinimum.get() > scaleMaximum.get()) {
                                HoldMeTight.LOGGER.error("error min scale in client config greater than max scale resetting all scale options to default");
                                resetScaleOptions();
                        }
                        return scaleMaximum.get().floatValue();
                }

                public float getDefaultScale(){
                        if (scaleDefault.get() > scaleMaximum.get()) {
                                HoldMeTight.LOGGER.error("error default scale in client config greater than max scale resetting all scale options to default");
                                resetScaleOptions();
                        }else if (scaleDefault.get() < scaleMinimum.get()) {
                                HoldMeTight.LOGGER.error("error default scale in client config less than min scale resetting all scale options to default");
                                resetScaleOptions();
                        }
                        return scaleDefault.get().floatValue();
                }

                private void resetScaleOptions(){
                        scaleMinimum.set(0d);
                        scaleMaximum.set(Double.MAX_VALUE);
                        scaleDefault.set(1d);
                }
        }

        public static class CommonConfig {
                CommonConfig(ModConfigSpec.Builder builder){

                }
        }

        public static class ServerConfig {

                public final ModConfigSpec.DoubleValue maxHitboxScale;
                private final ModConfigSpec.DoubleValue maxEntityScale;
                public final ModConfigSpec.DoubleValue maxWingsScale;
                public final ModConfigSpec.DoubleValue minParticleScale;
                public final ModConfigSpec.DoubleValue pickupRatioScale;
                public final ModConfigSpec.BooleanValue miningSpeedScaleLink;
                public final ModConfigSpec.BooleanValue damageTakenScaleLink;
                public final ModConfigSpec.BooleanValue dontSlowDownSmallerMovement;
                public final ModConfigSpec.BooleanValue canPickupEntities;
                public final ModConfigSpec.BooleanValue canPickupPlayers;
                public final ModConfigSpec.BooleanValue sableCarryPlaceFix;
                public final ModConfigSpec.BooleanValue changeSoundPitchWithScale;
                private final ModConfigSpec.ConfigValue<Double> soundPitchMin;
                private final ModConfigSpec.ConfigValue<Double> soundPitchMax;
                public final ModConfigSpec.BooleanValue playerChatScale;
                public final ModConfigSpec.DoubleValue maximumMovespeed;
                public final ModConfigSpec.DoubleValue maximumElytraspeed;


                ServerConfig(ModConfigSpec.Builder builder){
                        builder.comment("Serverside Config Settings");
                        
                        builder.push("Scale Limits");
                        //TODO make these translations
                        this.maxHitboxScale = builder.comment("The maximum scale a hitbox can be (8 or lower recommended for performance sake when larger)")
                                .defineInRange("maxHitboxScale", 8.0, 1, 256);
                        this.maxEntityScale = builder.comment("The maximum scale an entity can become, set to 0 for no cap (if it is any other number below 1 it will default to 1)")
                                .defineInRange("maxEntityScale", 0, 0, Double.MAX_VALUE);
                        this.maxWingsScale = builder.comment("the largest someone can be while wearing the paper wings item (an elytra in all ways that matter)")
                                .defineInRange("maxWingsScale", 0.05, 0, 1);
                        this.minParticleScale = builder.comment("the scale an entity should be before ambient particles are disabled on them")
                                .defineInRange("minParticleScale", 0.5, 0, 1);
                        this.pickupRatioScale = builder.comment("how much smaller an entity needs to be before it can be picked up (e.g. at the default of 0.25 someone at 4x can pickup someone at 1x but not any larger than that)")
                                .defineInRange("pickupRatio", 0.25, Double.MIN_VALUE, Double.MAX_VALUE);

                        builder.pop();
                        
                        builder.push("Optional Features");

                        this.miningSpeedScaleLink = builder.comment("should a players mining speed be linked to their scale (faster for larger folk slower for smaller folk)")
                                .define("miningSpeedScaleLink", true);
                        this.damageTakenScaleLink = builder.comment("should the damage a player takes be linked to their scale (less for larger folk more for smaller folk)")
                                .define("damageTakenScaleLink", true);
                        this.dontSlowDownSmallerMovement = builder.comment("if set to true players under 1x will still be able to move as if they were 1x (e.g. jumping 1 block heights moving at full speed etc), players over 1x will still move faster")
                                .define("dontSlowDownSmallerMovement", false);
                        this.canPickupEntities = builder.comment("enable or disable the ability to pickup non player mobs")
                                .define("canPickupMobs", true);
                        this.canPickupPlayers = builder.comment("enable or disable the ability to pickup players")
                                .define("canPickupPlayers", true);

                        this.sableCarryPlaceFix = builder.comment("Workaround for mobs picked up on a Sable sub-level being dropped back at their old spot (or vanishing while still audible) when placed. Sable keeps a plot-local position on entities it has 'stuck' to a sub-level and never refreshes it while the entity is being carried, so on drop the entity is snapped back to where it originally stood every tick. We re-bind the carried entity's plot position to the sub-level cell under its new location. Disable this once Sable fixes it.")
                                .define("sableCarryPlaceFix", true);

                        this.changeSoundPitchWithScale = builder.comment("enable the changing of pitch when an entities size changes")
                                .define("changeSoundPitchWithScale", false);
                        this.soundPitchMin = builder.comment("the range that the pitch can be changed by")
                                .define("soundPitchMin", 0.5d);
                        this.soundPitchMax = builder.define("soundPitchMax", 2d);
                        
                        this.playerChatScale = builder//.comment("should player messages be scaled based on their size (this is not properly tested and could cause many issues use at your own risk)")
                                .comment("this isnt used currently")
                                .define("playerChatScale", false);

                        builder.pop();

                        builder.push("Maximum movespeed");

                        this.maximumMovespeed = builder.comment("the maximum movement speed before it starts giving the 'moved too fast' error. i hightly recommend not increasing this unless you are okay with people potentially moving way too quickly while big (default 100)")
                                .defineInRange("maximumMovespeed", 100, 100, Double.MAX_VALUE);

                        this.maximumElytraspeed = builder.comment("the maximum movement speed when using an elytra before it starts giving the 'moved too fast' error. i hightly recommend not increasing this unless you are okay with people potentially moving way too quickly while big (default 100)")
                                .defineInRange("maximumElytraspeed", 300, 300, Double.MAX_VALUE);

                        builder.pop();
                        builder.build();
                }

                public boolean isSableCarryPlaceFixEnabled(){
                        return this.sableCarryPlaceFix.get();
                }

                public float getMaxEntityScale(){
                        if (this.maxEntityScale.get() <= 0) {
                                return Float.POSITIVE_INFINITY;
                        }else if (this.maxEntityScale.get() < 1) {
                                maxEntityScale.set(1d);;
                        }
                        return maxEntityScale.get().floatValue();
                }

                public Pair<Double,Double> getPitchRange(){
                        if (this.soundPitchMin.get() > this.soundPitchMax.get()) {
                                HoldMeTight.LOGGER.error("Error soundPitchMin value:" + soundPitchMin + "greater than soundPitchMax value:" + soundPitchMax + "resetting to default");
                                soundPitchMin.set(0.5d);
                                soundPitchMax.set(2d);
                        }
                        return Pair.of(soundPitchMin.get(), soundPitchMax.get());
                }
        }
}
