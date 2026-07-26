package com.accbdd.reclamation_util.mixins;

import com.rekindled.embers.recipe.BoilingRecipe;
import com.rekindled.embers.recipe.FluidHandlerContext;
import com.rekindled.embers.recipe.FluidIngredient;
import com.rekindled.embers.util.FluidOutput;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BoilingRecipe.class)
public class EmbersBoilerRecipePatch {

    @Final
    @Shadow
    public FluidIngredient input;

    @Final
    @Shadow
    public FluidOutput output;

    /**
     * @author ACCBDD
     * @reason match based on fluid type, not stack
     */
    @Overwrite(remap = false)
    public boolean matches(FluidHandlerContext context, Level level) {
        if (context.fluid.getTanks() == 0)
            return false;

        FluidStack stored = context.fluid.getFluidInTank(0);

        return input.test(stored);
    }

    /**
     * @author ACCBDD
     * @reason match based on fluid type, not stack
     */
    @Overwrite(remap = false)
    public FluidStack process(FluidHandlerContext context, int amount) {
        if (context.fluid.getTanks() == 0)
            return FluidStack.EMPTY;

        FluidStack stored = context.fluid.getFluidInTank(0);

        if (!input.test(stored))
            return FluidStack.EMPTY;

        int required = input.getAmount(stored.getFluid()) * amount;

        int drained = context.fluid.drain(required, IFluidHandler.FluidAction.EXECUTE).getAmount();
        int trueAmount = drained / input.getAmount(stored.getFluid());

        return new FluidStack(output.getStack(), output.getStack().getAmount() * trueAmount);
    }
}
