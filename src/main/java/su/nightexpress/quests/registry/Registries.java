package su.nightexpress.quests.registry;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.hook.HookPlugin;
import su.nightexpress.quests.task.adapter.Adapter;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.adapter.impl.CustomCropsAdapter;
import su.nightexpress.quests.task.adapter.impl.CustomFishingAdapter;
import su.nightexpress.quests.task.adapter.impl.EvenMoreFishAdapter;
import su.nightexpress.quests.task.adapter.impl.MythicMobAdapter;
import su.nightexpress.quests.util.QuestUtils;

import java.util.function.Function;

@Deprecated
public class Registries {

    public static final NightRegistry<Adapter<?, ?>>  ADAPTER   = NightRegistry.createOrdered();

    public static void setup(@NotNull QuestsPlugin plugin) {
        registerDefaultAdapters(plugin);
    }

    public static void shutdown() {
        ADAPTER.clear();
    }

    private static void registerDefaultAdapters(@NotNull QuestsPlugin plugin) {
        registerAdapter(Adapter.VANILLA_MOB, AdapterFamily.ENTITY);
        registerAdapter(Adapter.VANILLA_BLOCK, AdapterFamily.BLOCK);
        registerAdapter(Adapter.VANILLA_BLOCK_STATE, AdapterFamily.BLOCK_STATE);
        registerAdapter(Adapter.VANILLA_ITEM, AdapterFamily.ITEM);
        registerAdapter(Adapter.VANILLA_ENCHANTMENT, AdapterFamily.ENCHANTMENT);

        registerExternal(HookPlugin.MYTHIC_MOBS, MythicMobAdapter::new, AdapterFamily.ENTITY);
        registerExternal(HookPlugin.EVEN_MORE_FISH, EvenMoreFishAdapter::new, AdapterFamily.ITEM);
        registerExternal(HookPlugin.CUSTOM_FISHING, CustomFishingAdapter::new, AdapterFamily.ITEM);
        registerExternal(HookPlugin.CUSTOM_CROPS, CustomCropsAdapter::new, AdapterFamily.BLOCK);

        plugin.info("Registered " + ADAPTER.size() + " adapters:");
        ADAPTER.values().forEach(adapter -> plugin.info("  - " + adapter.getName()));
    }

    public static <I, O, E extends Adapter<I, O>> boolean registerExternal(@NotNull String pluginName, @NotNull Function<String, E> function, @NotNull AdapterFamily<O> family) {
        if (!QuestUtils.isIntegrationAvailable(pluginName)) return false;

        return registerAdapter(pluginName, function, family);
    }

    public static <I, O, E extends Adapter<I, O>> boolean registerAdapter(@NotNull String name, @NotNull Function<String, E> function, @NotNull AdapterFamily<O> family) {
        E adapter = function.apply(name);
        return registerAdapter(adapter, family);
    }

    public static <I, O, E extends Adapter<I, O>> boolean registerAdapter(@NotNull E adapter, @NotNull AdapterFamily<O> family) {
        family.addAdapter(adapter);
        ADAPTER.add(adapter.getName(), adapter);
        return true;
    }
}
