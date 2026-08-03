package com.ricardthegreat.holdmetight.utils.compat;

import java.lang.reflect.Method;

import com.ricardthegreat.holdmetight.HoldMeTight;

import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import org.joml.Vector3d;

/*
 * Client-side helper that samples a Sable sub-level's own light for an entity standing on it.
 *
 * Sable renders sub-level entities by projecting them out near the sub-level; its entity lighting
 * then samples a small probe box around the entity's GLOBAL position. When that box momentarily
 * misses the sub-level (or the base world is void/dark where the sub-level flies over), the light
 * falls back to the near-empty main world and the entity renders near-black.
 *
 * This helper re-samples the light directly from the sub-level the entity belongs to, in the
 * sub-level's own coordinate space, so the result is independent of where the sub-level is parked
 * globally. It is a stopgap: every Sable access is guarded and any failure silently returns null
 * so the caller simply keeps the value Sable/vanilla already computed. It can be turned off with
 * the client config switch "sableEntityLightingFix" once Sable fixes this itself.
 *
 * The public companion API exposes containment + poses but not the sub-level's Level, so the two
 * bits we need that are internal (getLevel, scaleSkyLight) are reached through reflection on the
 * concrete client sub-level implementation and never fail hard if Sable renames them later.
 */
public final class SablePlotLight {

    private SablePlotLight() {
    }

    /**
     * Samples the containing sub-level's block and sky light around the entity.
     *
     * @param entity the entity being rendered
     * @return {@code {blockLight, skyLight}} computed purely from the sub-level it is on, or
     *         {@code null} if the entity is not on a (client) sub-level, Sable is not present, or
     *         anything inside the sampling failed (in which case the caller keeps its own value).
     */
    public static int[] sample(Entity entity) {
        if (entity == null || !SableCompat.isSableLoaded()) {
            return null;
        }

        try {
            // Query at the entity's GLOBAL position, exactly like Sable's own entity renderer mixin
            // (getAllIntersecting), so this also works for the local player who is standing on a
            // sub-level at real-world coordinates (a plot-grid getContaining() misses those).
            final Vector3d probe = new Vector3d(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
            final BoundingBox3d query = new BoundingBox3d(BlockPos.containing(probe.x, probe.y, probe.z)).expand(2.0);

            int block = 0;
            int sky = 0;
            boolean found = false;

            for (SubLevelAccess subLevel : SableCompanion.INSTANCE.getAllIntersecting(entity.level(), query)) {
                if (!(subLevel instanceof final ClientSubLevelAccess client)) {
                    continue;
                }
                int[] light = sampleSingle(entity, client, probe);
                if (light == null) {
                    continue;
                }
                found = true;
                block = Math.max(block, light[0]);
                sky = Math.max(sky, light[1]);
            }

            return found ? new int[] { block, sky } : null;
        } catch (Throwable t) {
            HoldMeTight.LOGGER.debug("Failed to sample Sable sub-level light for {}, keeping Sable/vanilla value.", entity, t);
            return null;
        }
    }

    private static int[] sampleSingle(Entity entity, ClientSubLevelAccess client, Vector3d globalProbe) {
        try {
            final Level level = levelOf(client);
            if (level == null) {
                return null;
            }
            final Pose3dc renderPose = poseOf(client);
            if (renderPose == null) {
                return null;
            }

            // Global (projected) eye position mapped back into this sub-level's plot space.
            final Vector3d local = new Vector3d();
            renderPose.transformPositionInverse(globalProbe, local);

            int block = 0;
            int sky = 0;
            final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            final int baseX = (int) Math.floor(local.x);
            final int baseY = (int) Math.floor(local.y);
            final int baseZ = (int) Math.floor(local.z);

            // Small max-sampler around the probe so a single dark or unloaded cell cannot black it out.
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = 0; dy <= 2; dy++) {
                        pos.set(baseX + dx, baseY + dy, baseZ + dz);
                        block = Math.max(block, level.getBrightness(LightLayer.BLOCK, pos));
                        sky = Math.max(sky, level.getBrightness(LightLayer.SKY, pos));
                    }
                }
            }

            sky = scaleSkyLight(client, sky);

            return new int[] { block, sky };
        } catch (Throwable t) {
            return null;
        }
    }

    /*
     * getLevel() and scaleSkyLight(int) live on the concrete ClientSubLevel, which is not part of
     * the public companion API, so they are reached reflectively and never fail the caller.
     */

    private static Level levelOf(Object subLevel) {
        try {
            final Method m = subLevel.getClass().getMethod("getLevel");
            return (Level) m.invoke(subLevel);
        } catch (Throwable t) {
            return null;
        }
    }

    private static int scaleSkyLight(Object subLevel, int sky) {
        try {
            final Method m = subLevel.getClass().getMethod("scaleSkyLight", int.class);
            return ((Number) m.invoke(subLevel, sky)).intValue();
        } catch (Throwable t) {
            return sky;
        }
    }

    private static Pose3dc poseOf(ClientSubLevelAccess client) {
        try {
            return client.renderPose();
        } catch (Throwable t) {
            return null;
        }
    }
}