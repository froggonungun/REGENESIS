package ru.jgers.regenesis.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MarkOfCainDataProvider implements ICapabilitySerializable<CompoundTag> {
    private final IMarkOfCainData data;
    private final LazyOptional<IMarkOfCainData> lazyOptional;

    public MarkOfCainDataProvider(Player player) {
        this.data = new MarkOfCainData();
        this.lazyOptional = LazyOptional.of(() -> data);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == MarkOfCainCapabilities.CAPABILITY) {
            return this.lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("year", data.getYearCount());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        data.setYearCount(nbt.getInt("year"));
    }
}
