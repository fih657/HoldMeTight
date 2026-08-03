package com.ricardthegreat.holdmetight.init;

import com.ricardthegreat.holdmetight.HoldMeTight;
import com.ricardthegreat.holdmetight.blocks.tinyjars.JarBlock;
import com.ricardthegreat.holdmetight.blocks.tinyjars.LiquidJarBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, HoldMeTight.MODID);

    public static final DeferredHolder<Block, JarBlock> TINY_JAR_EMPTY = BLOCKS.register("tiny_jar_empty", 
        () -> new JarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));

    public static final DeferredHolder<Block, LiquidJarBlock> TINY_JAR_FULL = BLOCKS.register("tiny_jar_liquid", 
        () -> new LiquidJarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
}
