package com.ricardthegreat.holdmetight.utils.compat;

import com.ricardthegreat.holdmetight.HoldMeTight;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/*
 * Optional runtime compatibility with the Sable mod (modid "sable").
 *
 * HoldMeTight must keep working when Sable is NOT installed. Sable's companion API
 * (dev.ryanhcode.sable.companion) is only available at compile time (compileOnly dependency), so
 * there is no hard runtime dependency on it. Every runtime access to Sable's companion is guarded
 * behind SABLE_PRESENT, so the Sable classes are never touched (and never even resolved) when the
 * mod runs without Sable.
 */
public class SableCompat {

    /** Whether Sable (and therefore its companion implementation) is present at runtime. */
    private static final boolean SABLE_PRESENT = isSablePresent();

    private SableCompat() {
    }

    private static boolean isSablePresent() {
        // Primary signal: the actual Sable mod is loaded by the mod loader.
        try {
            return net.neoforged.fml.ModList.get().isLoaded("sable");
        } catch (Throwable t) {
            // ModList not available (should never happen inside NeoForge); fall through to a
            // raw classpath check so the companion is still used if it is present anyway.
        }

        try {
            Class.forName("dev.ryanhcode.sable.companion.SableCompanion");
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isSableLoaded() {
        return SABLE_PRESENT;
    }

    /**
     * The squared distance between two entities, taking Sable sub-levels into account when Sable
     * is present. Without Sable this is the plain Euclidean (squared) distance.
     */
    public static double distanceSquaredBetween(Entity a, Entity b) {
        if (SABLE_PRESENT) {
            return SableCompanion.INSTANCE.distanceSquaredWithSubLevels(a.level(), a.position(), b.position());
        }
        return a.position().distanceToSqr(b.position());
    }

    /**
     * The squared distance between two positions in a level, taking Sable sub-levels into account
     * when Sable is present. Without Sable this is the plain Euclidean (squared) distance.
     */
    public static double distanceSquaredBetween(Level level, Vec3 a, Vec3 b) {
        if (SABLE_PRESENT) {
            return SableCompanion.INSTANCE.distanceSquaredWithSubLevels(level, a, b);
        }
        return a.distanceToSqr(b);
    }

    /**
     * Whether the given position overlaps a Sable sub-level, using Sable's own physics-ticket
     * lookup (works for real-world positions, e.g. a local player standing on a projected ship).
     * Always false when Sable is not present.
     */
    public static boolean isOnSubLevel(Level level, Vec3 centre, double radius) {
        if (!SABLE_PRESENT || level == null || centre == null) {
            return false;
        }
        try {
            BoundingBox3d query = new BoundingBox3d(
                centre.x - radius, centre.y - radius, centre.z - radius,
                centre.x + radius, centre.y + radius, centre.z + radius
            );
            return SableCompanion.INSTANCE.getAllIntersecting(level, query).iterator().hasNext();
        } catch (Throwable t) {
            return false;
        }
    }

    /** Fully-qualified name of Sable's mixin interface for plot-stuck entities (runtime only). */
    private static final String STICK_EXTENSION_CLASS =
        "dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.EntityStickExtension";

    /** Fully-qualified name of Sable's mixin interface for entity sub-level tracking (runtime only). */
    private static final String MOVEMENT_EXTENSION_CLASS =
        "dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension";

    /**
     * Re-binds a carried entity to the Sable sub-level that currently contains its position, so
     * Sable's per-tick plot-position snap keeps the entity where it was dropped instead of flinging
     * it back to the plot cell it was standing on before being picked up. Returns true if a bind
     * was applied. No-op (returns false) when Sable is absent or the entity/position isn't on a
     * sub-level.
     */
    public static boolean rebindCarriedEntityToSubLevel(Entity entity) {
        if (!SABLE_PRESENT || entity == null) {
            return false;
        }
        try {
            Level level = entity.level();
            Vec3 pos = entity.position();

            // Sable's `getContaining(Level, Position)` is a PLOT-GRID lookup: it only finds the
            // sub-level when `pos` is in the plot's own (plot-local) coordinate space. That is the
            // case for the right-click placement path, where Sable's raycast reports the click in
            // plot-local coordinates. In that case the entity's position already equals its desired
            // plot-local position, so we must NOT inverse-transform it again (doing so would double-
            // transform and push the entity to a fixed offset e.g. a block corner).
            SubLevelAccess plotSubLevel = SableCompanion.INSTANCE.getContaining(level, pos);
            if (plotSubLevel != null) {
                setEntityPlotPosition(entity, pos);
                // Normalize the entity's server-side position back to world coordinates immediately.
                // `dismountTo()` left it at plot-local coordinates; if the entity tracker sends a
                // packet in that transient state, Sable's server tags it `actuallyInSubLevel=true`
                // (the position is in the plot grid) and the client lerps the mob to the plot coords
                // (~20M away, plotPosition=null) - it goes invisible until the world is reloaded.
                entity.setPos(plotSubLevel.logicalPose().transformPosition(pos));
                setEntityTrackingSubLevel(entity, plotSubLevel);
                HoldMeTight.LOGGER.debug("Sable re-bind (plot-local): entity pos {} world {} block {} sub {}", fmt(pos), fmt(entity.position()), block(pos), plotSubLevel.getName());
                return true;
            }

            // Fallback: the position is in main-world coordinates (e.g. a Q-drop near the player).
            // Find a physically-overlapping sub-level and transform into its plot space.
            BoundingBox3d query = new BoundingBox3d(
                pos.x - 1.0, pos.y - 1.0, pos.z - 1.0,
                pos.x + 1.0, pos.y + 1.0, pos.z + 1.0
            );
            for (SubLevelAccess subLevel : SableCompanion.INSTANCE.getAllIntersecting(level, query)) {
                Vec3 plotPosition = subLevel.logicalPose().transformPositionInverse(pos);
                setEntityPlotPosition(entity, plotPosition);
                setEntityTrackingSubLevel(entity, subLevel);
                HoldMeTight.LOGGER.debug("Sable re-bind (world->plot): entity pos {} -> plot pos {}", fmt(pos), fmt(plotPosition));
                return true;
            }

            // The placement point is on no sub-level: the entity has left any sub-level it was on.
            // Clear any stale plot/tracking state (from where it was picked up) so Sable's per-tick
            // `sable$setPosField` snap doesn't teleport it back to the old plot cell.
            clearEntitySubLevelState(entity);
            return false;
        } catch (Throwable t) {
            HoldMeTight.LOGGER.debug("Sable re-bind failed", t);
            return false;
        }
    }

    private static String fmt(Vec3 v) {
        return v == null ? "null" : String.format("(%.4f, %.4f, %.4f)", v.x, v.y, v.z);
    }

    private static String block(Vec3 v) {
        return String.format("[%d, %d, %d]", Mth.floor(v.x), Mth.floor(v.y), Mth.floor(v.z));
    }

    /** Cached reflection handles for Sable's mixin interfaces (resolved once, Sable-runtime only). */
    private static java.lang.reflect.Method SABLE_PLOT_POSITION_SETTER;
    private static java.lang.reflect.Method SABLE_PLOT_POSITION_GETTER;
    private static java.lang.reflect.Method SABLE_TRACKING_SUBLEVEL_SETTER;
    private static boolean SABLE_REFLECTION_ATTEMPTED;

    private static void initSableReflection() {
        if (SABLE_REFLECTION_ATTEMPTED) {
            return;
        }
        SABLE_REFLECTION_ATTEMPTED = true;
        try {
            Class<?> stickExtension = Class.forName(STICK_EXTENSION_CLASS);
            SABLE_PLOT_POSITION_SETTER = stickExtension.getMethod("sable$setPlotPosition", Vec3.class);
            SABLE_PLOT_POSITION_GETTER = stickExtension.getMethod("sable$getPlotPosition");
            Class<?> movementExtension = Class.forName(MOVEMENT_EXTENSION_CLASS);
            Class<?> subLevelClass = Class.forName("dev.ryanhcode.sable.sublevel.SubLevel");
            SABLE_TRACKING_SUBLEVEL_SETTER = movementExtension.getMethod("sable$setTrackingSubLevel", subLevelClass);
        } catch (Throwable t) {
            HoldMeTight.LOGGER.debug("Sable mixin interfaces not resolvable", t);
        }
    }

    /**
     * Sets Sable's plot-local position field on an entity via its STICK_EXTENSION_CLASS mixin
     * interface. The interface ships with the Sable mod (not the companion), so it is reached by
     * reflection and never resolved when Sable is absent.
     */
    private static void setEntityPlotPosition(Entity entity, Vec3 plotPos) {
        initSableReflection();
        if (SABLE_PLOT_POSITION_SETTER == null) {
            return;
        }
        try {
            SABLE_PLOT_POSITION_SETTER.invoke(entity, plotPos);
        } catch (ReflectiveOperationException e) {
            HoldMeTight.LOGGER.debug("Failed to set Sable plot position on {}", entity, e);
        }
    }

    /** Reads Sable's current plot-local position field on an entity, or null if none/unresolvable. */
    private static Vec3 getEntityPlotPosition(Entity entity) {
        initSableReflection();
        if (SABLE_PLOT_POSITION_GETTER == null) {
            return null;
        }
        try {
            return (Vec3) SABLE_PLOT_POSITION_GETTER.invoke(entity);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Sets Sable's tracking-sub-level on an entity via its MOVEMENT_EXTENSION_CLASS mixin
     * interface (reflection, Sable-runtime only). Makes the server-side entity state consistent
     * immediately after a placement so the tracker never emits a plot-local position packet tagged
     * `actuallyInSubLevel`.
     */
    private static void setEntityTrackingSubLevel(Entity entity, Object subLevel) {
        initSableReflection();
        if (SABLE_TRACKING_SUBLEVEL_SETTER == null) {
            return;
        }
        try {
            SABLE_TRACKING_SUBLEVEL_SETTER.invoke(entity, subLevel);
        } catch (ReflectiveOperationException e) {
            HoldMeTight.LOGGER.debug("Failed to set Sable tracking sub-level on {}", entity, e);
        }
    }

    /**
     * Removes all Sable sub-level state (plot position + tracking sub-level) from an entity. Used
     * when a carried entity is placed somewhere that is not on any sub-level, so Sable stops
     * snapping it back to the plot cell it was picked up from. No-op (and silent) when the entity
     * had no sub-level state to clear.
     */
    private static void clearEntitySubLevelState(Entity entity) {
        if (getEntityPlotPosition(entity) == null) {
            return;
        }
        setEntityPlotPosition(entity, null);
        setEntityTrackingSubLevel(entity, null);
        HoldMeTight.LOGGER.debug("Sable re-bind (unbound): entity {} cleared from sub-level", entity);
    }
}
