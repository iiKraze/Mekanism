package mekanism.common.inventory.container.sync;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.network.container.property.InfusionStackPropertyData;
import mekanism.common.network.container.property.LongPropertyData;
import mekanism.common.network.container.property.PropertyData;

/**
 * Version of {@link net.minecraft.util.IntReferenceHolder} for handling infusion stacks
 */
public class SyncableInfusionStack extends SyncableChemicalStack<InfuseType, InfusionStack> {

    public static SyncableInfusionStack create(IChemicalTank<InfuseType, InfusionStack> handler) {
        return new SyncableInfusionStack(handler::getStack, handler::setStack);
    }

    public static SyncableInfusionStack create(Supplier<@NonNull InfusionStack> getter, Consumer<@NonNull InfusionStack> setter) {
        return new SyncableInfusionStack(getter, setter);
    }

    private SyncableInfusionStack(Supplier<@NonNull InfusionStack> getter, Consumer<@NonNull InfusionStack> setter) {
        super(getter, setter);
    }

    @Nonnull
    @Override
    protected InfusionStack getEmptyStack() {
        return InfusionStack.EMPTY;
    }

    @Nonnull
    @Override
    protected InfusionStack createStack(InfusionStack stored, long size) {
        return new InfusionStack(stored, size);
    }

    @Override
    public PropertyData getPropertyData(short property, DirtyType dirtyType) {
        if (dirtyType == DirtyType.SIZE) {
            //If only the size changed, don't bother re-syncing the type
            return new LongPropertyData(property, get().getAmount());
        }
        return new InfusionStackPropertyData(property, get());
    }
}