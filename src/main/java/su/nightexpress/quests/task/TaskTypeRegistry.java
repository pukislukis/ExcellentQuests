package su.nightexpress.quests.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.quests.task.adapter.AdapterFamily;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TaskTypeRegistry {

    private final Map<String, TaskType<?, ?>> typeByIdMap;

    public TaskTypeRegistry() {
        this.typeByIdMap = new HashMap<>();
    }

    public void clear() {
        this.typeByIdMap.clear();
    }

    public <O, F extends AdapterFamily<O>> boolean registerType(@NotNull String id, @NotNull F adapterFamily, @NotNull Consumer<TaskType<O, F>> consumer) {
        TaskType<O, F> taskType = new TaskType<>(id, adapterFamily);
        this.typeByIdMap.put(taskType.getId(), taskType);

        consumer.accept(taskType);
        return true;
    }

    @Nullable
    public TaskType<?, ?> getTypeById(@NotNull String id) {
        return this.typeByIdMap.get(LowerCase.INTERNAL.apply(id));
    }
}
