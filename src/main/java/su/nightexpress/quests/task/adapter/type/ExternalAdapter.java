package su.nightexpress.quests.task.adapter.type;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.common.NightKey;
import su.nightexpress.nightcore.util.LowerCase;

public abstract class ExternalAdapter<I, O> extends AbstractAdapter<I, O> {

    protected final String namespace;

    public ExternalAdapter(@NotNull String name, @NotNull String namespace) {
        super(name);
        this.namespace = namespace;
    }

    @NotNull
    public String getNamespace() {
        return this.namespace;
    }

    public boolean isNamespace(@NotNull String name) {
        return this.namespace.equalsIgnoreCase(name);
    }

    @Override
    public boolean canHandle(@NotNull String fullName) {
        return LowerCase.INTERNAL.apply(fullName).startsWith(this.namespace);

        //return NightKey.key(fullName).namespace().equalsIgnoreCase(this.namespace);
    }

    @Override
    @Nullable
    public String getLocalizedName(@NotNull String fullName) {
        int index = fullName.indexOf(NightKey.DELIMITER);
        String namespace = index >= 1 ? fullName.substring(0, index) : NamespacedKey.MINECRAFT;
        String value = index >= 0 ? fullName.substring(index + 1) : fullName;

        if (!this.isNamespace(namespace)) return null;

        I type = this.getTypeByName(value);
        return type == null ? null : this.getLocalizedName(type);
    }

    @NotNull
    public String toFullNameOfType(@NotNull I type) {
        String typeName = /*LowerCase.INTERNAL.apply(*/this.getTypeName(type);
        return this.namespace + NightKey.DELIMITER + typeName;

        //return NightKey.key(this.namespace, typeName).asString();
    }
}
