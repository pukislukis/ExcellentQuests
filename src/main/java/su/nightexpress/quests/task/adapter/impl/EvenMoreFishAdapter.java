package su.nightexpress.quests.task.adapter.impl;

import com.oheers.fish.api.fishing.items.AbstractFishManager;
import com.oheers.fish.api.fishing.items.IFish;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.quests.task.adapter.type.ExternalAdapter;

public class EvenMoreFishAdapter extends ExternalAdapter<IFish, ItemStack> {

    private static final String DELIMITER = ":";

    public EvenMoreFishAdapter(@NotNull String name) {
        super(name, "evenmorefish");
    }

    @Override
    public boolean canHandle(@NotNull ItemStack itemStack) {
        return getType(itemStack) != null;
    }

    @Override
    @Nullable
    public IFish getTypeByName(@NotNull String name) {
        String[] split = name.split(DELIMITER);
        if (split.length < 2) return null;

        return AbstractFishManager.getInstance().getFish(split[0], split[1]);
    }

    @Override
    @Nullable
    public IFish getType(@NotNull ItemStack itemStack) {
        return AbstractFishManager.getInstance().getFish(itemStack);
    }

    @Override
    @NotNull
    public String getTypeName(@NotNull IFish fish) {
        return fish.getRarity().getId() + DELIMITER + fish.getName();
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull IFish fish) {
        return fish.getName();
    }
}
