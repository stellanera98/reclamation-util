package com.accbdd.reclamation_util.bees.effect;

import com.accbdd.complicated_bees.bees.GeneticHelper;
import com.accbdd.complicated_bees.bees.effect.BeeEffect;
import com.accbdd.complicated_bees.bees.gene.GeneProductivity;
import com.accbdd.complicated_bees.bees.gene.enums.EnumProductivity;
import com.accbdd.complicated_bees.util.BlockPosBoxIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ConversionEffect extends BeeEffect {
    private final BlockState convertFrom;
    private final BlockState convertTo;

    public ConversionEffect(BlockState convertFrom, BlockState convertTo) {
        this.convertFrom = convertFrom;
        this.convertTo = convertTo;
    }

    @Override
    public void runEffect(BlockEntity apiary, ItemStack queen, int cycleProgress) {
        if (apiary.getLevel() != null) {
            if (cycleProgress == 0) {
                Level level = apiary.getLevel();
                if (level != null) {
                    BlockPosBoxIterator iterator = this.getBlockIterator(apiary, queen);
                    List<BlockPos> convertibles = new ArrayList<>();

                    while (iterator.hasNext()) {
                        BlockPos pos = iterator.next();
                        BlockState state = level.getBlockState(pos);
                        if (state.equals(convertFrom)) {
                            convertibles.add(pos);
                        }
                    }

                    float chance = 1f / switch ((EnumProductivity) GeneticHelper.getGeneValue(queen, GeneProductivity.ID, true)) {
                        case SLOWEST -> 12f; // 120s
                        case SLOWER -> 10f; // 100s (previous value)
                        case SLOW -> 8f; // 80s
                        case AVERAGE -> 6f; // 60s
                        case FAST -> 4f; // 40s
                        case FASTER -> 2f; // 20s
                        case FASTEST -> 1f; // 10s
                    };
                    if (!convertibles.isEmpty() && level.random.nextFloat() < chance) {
                        BlockPos switchPos = convertibles.get(level.random.nextInt(convertibles.size()));
                        level.setBlockAndUpdate(switchPos, convertTo);
                        level.playSound(null, switchPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS);
                    }
                }
            }
        }
    }
}
