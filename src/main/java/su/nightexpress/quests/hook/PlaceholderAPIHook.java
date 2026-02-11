package su.nightexpress.quests.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.battlepass.BattlePassManager;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;
import su.nightexpress.quests.milestone.MilestoneManager;
import su.nightexpress.quests.milestone.data.MilestoneData;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.milestone.definition.MilestoneCategory;
import su.nightexpress.quests.quest.QuestManager;
import su.nightexpress.quests.quest.data.QuestData;
import su.nightexpress.quests.quest.definition.Quest;
import su.nightexpress.quests.user.QuestUser;
import su.nightexpress.quests.util.QuestUtils;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final QuestsPlugin plugin;

    public PlaceholderAPIHook(@NotNull QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "excellentquests";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    @Nullable
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        QuestUser user = plugin.getUserManager().getUserData(player);
        if (user == null) {
            return "";
        }

        // Battle Pass placeholders
        if (params.startsWith("battle_pass_") || params.startsWith("battlepass_")) {
            return parseBattlePassPlaceholder(user, params);
        }

        // Season placeholders
        if (params.startsWith("season_")) {
            return parseSeasonPlaceholder(params);
        }

        // Milestone placeholders
        if (params.startsWith("milestone_")) {
            return parseMilestonePlaceholder(user, params);
        }

        // Quest placeholders
        if (params.startsWith("quest_")) {
            return parseQuestPlaceholder(user, params);
        }

        return null;
    }

    @Nullable
    private String parseBattlePassPlaceholder(@NotNull QuestUser user, @NotNull String params) {
        BattlePassManager manager = plugin.getBattlePassManager();
        if (manager == null) {
            return "";
        }

        BattlePassSeason season = manager.getCurrentSeason();
        if (season == null) {
            return "";
        }

        BattlePassData data = user.getBattlePassData(season);

        // Normalize parameter format (support both battle_pass_ and battlepass_)
        String param = params.replace("battle_pass_", "").replace("battlepass_", "");

        switch (param) {
            case "type":
                return data.isPremium() ? "Premium" : "Free";
            case "level":
                return NumberUtil.format(data.getLevel());
            case "max_level":
            case "maxlevel":
                return NumberUtil.format(data.getMaxLevel());
            case "xp":
                return NumberUtil.format(data.getXP());
            case "xp_max":
            case "xpmax":
                return NumberUtil.format(data.getLevelXP());
            case "xp_to_up":
            case "xptoup":
                return NumberUtil.format(data.getXPToLevelUp());
            case "xp_to_down":
            case "xptodown":
                return NumberUtil.format(data.getXPToLevelDown());
            default:
                return null;
        }
    }

    @Nullable
    private String parseSeasonPlaceholder(@NotNull String params) {
        BattlePassManager manager = plugin.getBattlePassManager();
        if (manager == null) {
            return "";
        }

        BattlePassSeason season = manager.getCurrentSeason();
        if (season == null) {
            return "";
        }

        String param = params.replace("season_", "");

        switch (param) {
            case "name":
                return season.getName();
            case "start_date":
            case "startdate":
                return QuestUtils.formatDateTime(season.getStartDate());
            case "end_date":
            case "enddate":
                return QuestUtils.formatDateTime(season.getEndDate());
            case "expire_date":
            case "expiredate":
                return QuestUtils.formatDateTime(season.getExpireDate());
            case "duration":
                return TimeFormats.formatSince(season.getStartDate(), TimeFormatType.LITERAL);
            case "timeleft":
            case "time_left":
                return TimeFormats.formatDuration(season.getEndDate(), TimeFormatType.LITERAL);
            default:
                return null;
        }
    }

    @Nullable
    private String parseMilestonePlaceholder(@NotNull QuestUser user, @NotNull String params) {
        MilestoneManager manager = plugin.getMilestoneManager();
        if (manager == null) {
            return "";
        }

        String param = params.replace("milestone_", "");

        // Handle milestone category placeholders: milestone_category_<id>_<property>
        if (param.startsWith("category_")) {
            String[] parts = param.substring("category_".length()).split("_", 2);
            if (parts.length < 2) {
                return null;
            }

            String categoryId = parts[0];
            String property = parts[1];
            MilestoneCategory category = manager.getCategoryById(categoryId);
            
            if (category == null) {
                return "";
            }

            switch (property) {
                case "id":
                    return category.getId();
                case "name":
                    return category.getName();
                case "description":
                    return String.join(" ", category.getDescription());
                default:
                    return null;
            }
        }

        // Handle specific milestone placeholders: milestone_<id>_<property>
        String[] parts = param.split("_", 2);
        if (parts.length < 2) {
            return null;
        }

        String milestoneId = parts[0];
        String property = parts[1];
        Milestone milestone = manager.getMilestoneById(milestoneId);
        
        if (milestone == null) {
            return "";
        }

        switch (property) {
            case "id":
                return milestone.getId();
            case "name":
                return milestone.getName();
            case "description":
                return String.join(" ", milestone.getDescription());
            case "level":
            case "levels":
                MilestoneData data = user.getMilestoneData(milestone);
                return data != null ? String.valueOf(data.getLevel()) : "0";
            case "completed":
                return String.valueOf(user.isCompleted(milestone));
            default:
                return null;
        }
    }

    @Nullable
    private String parseQuestPlaceholder(@NotNull QuestUser user, @NotNull String params) {
        QuestManager manager = plugin.getQuestManager();
        if (manager == null) {
            return "";
        }

        String param = params.replace("quest_", "");

        // Handle specific quest placeholders: quest_<id>_<property>
        String[] parts = param.split("_", 2);
        if (parts.length < 2) {
            return null;
        }

        String questId = parts[0];
        String property = parts[1];
        Quest quest = manager.getQuestById(questId);
        
        if (quest == null) {
            return "";
        }

        switch (property) {
            case "id":
                return quest.getId();
            case "name":
                return quest.getName();
            case "description":
                return String.join(" ", quest.getDescription());
            case "active":
            case "isactive":
                // Find quest data by matching questId
                QuestData data = user.getQuestDatas().stream()
                    .filter(qd -> qd.getQuestId().equals(questId))
                    .findFirst()
                    .orElse(null);
                return data != null ? String.valueOf(data.isActive()) : "false";
            case "completed":
            case "iscompleted":
                // Find quest data by matching questId
                QuestData questData = user.getQuestDatas().stream()
                    .filter(qd -> qd.getQuestId().equals(questId))
                    .findFirst()
                    .orElse(null);
                return questData != null ? String.valueOf(questData.isCompleted()) : "false";
            default:
                return null;
        }
    }
}
