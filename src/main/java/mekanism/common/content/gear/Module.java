package mekanism.common.content.gear;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import mekanism.api.Action;
import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.MekanismLang;
import mekanism.common.content.gear.ModuleConfigItem.BooleanData;
import mekanism.common.content.gear.Modules.ModuleData;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StorageUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

public abstract class Module {

    public static final String ENABLED_KEY = "enabled";
    public static final String HANDLE_MODE_CHANGE_KEY = "handleModeChange";

    protected List<ModuleConfigItem<?>> configItems = new ArrayList<>();

    private ModuleData<?> data;
    private ItemStack container;

    private ModuleConfigItem<Boolean> enabled;
    private ModuleConfigItem<Boolean> handleModeChange;
    private ModuleConfigItem<Boolean> renderHUD;

    private int installed = 1;

    public void init(ModuleData<?> data, ItemStack container) {
        this.data = data;
        this.container = container;
    }

    public void init() {
        enabled = addConfigItem(new ModuleConfigItem<>(this, ENABLED_KEY, MekanismLang.MODULE_ENABLED, new BooleanData(), true));
        if (data.handlesModeChange()) {
            handleModeChange = addConfigItem(new ModuleConfigItem<>(this, HANDLE_MODE_CHANGE_KEY, MekanismLang.MODULE_HANDLE_MODE_CHANGE, new BooleanData(), true));
        }
        if (data.rendersHUD()) {
            renderHUD = addConfigItem(new ModuleConfigItem<>(this, "renderHUD", MekanismLang.MODULE_RENDER_HUD, new BooleanData(), true));
        }
    }

    protected <T> ModuleConfigItem<T> addConfigItem(ModuleConfigItem<T> item) {
        configItems.add(item);
        return item;
    }

    public void tick(PlayerEntity player) {
        if (isEnabled()) {
            if (!player.world.isRemote()) {
                tickServer(player);
            }
        }
    }

    protected FloatingLong getContainerEnergy() {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(getContainer(), 0);
        return energyContainer == null ? FloatingLong.ZERO : energyContainer.getEnergy();
    }

    protected void useEnergy(FloatingLong energy) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(getContainer(), 0);
        if (energyContainer != null) {
            energyContainer.extract(energy, Action.EXECUTE, AutomationType.MANUAL);
        }
    }

    protected void tickServer(PlayerEntity player) {}

    public final void read(CompoundNBT nbt) {
        if (nbt.contains(NBTConstants.AMOUNT)) {
            installed = nbt.getInt(NBTConstants.AMOUNT);
        }
        init();
        for (ModuleConfigItem<?> item : configItems) {
            item.read(nbt);
        }
    }

    /**
     * Save this module on the container ItemStack. Will create proper NBT structure if it does not yet exist.
     * @param callback - will run after the NBT data is saved
     */
    public final void save(Consumer<ItemStack> callback) {
        CompoundNBT modulesTag = ItemDataUtils.getCompound(container, NBTConstants.MODULES);
        CompoundNBT nbt = modulesTag.getCompound(data.getName());

        nbt.putInt(NBTConstants.AMOUNT, installed);
        for (ModuleConfigItem<?> item : configItems) {
            item.write(nbt);
        }

        modulesTag.put(data.getName(), nbt);
        ItemDataUtils.setCompound(container, NBTConstants.MODULES, modulesTag);

        if (callback != null) {
            callback.accept(container);
        }
    }

    public String getName() {
        return data.getName();
    }

    public ModuleData<?> getData() {
        return data;
    }

    public int getInstalledCount() {
        return installed;
    }

    public void setInstalledCount(int installed) {
        this.installed = installed;
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setDisabledForce() {
        enabled.getData().set(false);
        save(null);
    }

    protected ItemStack getContainer() {
        return container;
    }

    public List<ModuleConfigItem<?>> getConfigItems() {
        return configItems;
    }

    public void addHUDStrings(List<ITextComponent> list) {}

    public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {}

    public boolean handlesModeChange() {
        return data.handlesModeChange() && handleModeChange.get();
    }

    public void setModeHandlingDisabledForce() {
        if (data.handlesModeChange()) {
            handleModeChange.getData().set(false);
            save(null);
        }
    }

    public boolean renderHUD() {
        return data.rendersHUD() && renderHUD.get();
    }

    public void onAdded(boolean first) {
        for (Module module : Modules.loadAll(getContainer())) {
            if (module.getData() != getData()) {
                // disable other exclusive modules if this is an exclusive module, as this one will now be active
                if (module.getData().isExclusive()) {
                    module.setDisabledForce();
                }
                if (module.handlesModeChange()) {
                    module.setModeHandlingDisabledForce();
                }
            }
        }
    }

    public void onRemoved(boolean last) {}
}
