package net.ucrafts.ranks;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import de.leonhard.storage.Json;
import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import net.ucrafts.ranks.types.ConfigType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Config
{

    private final FlatFile config;
    // todo: sort by time
    private final HashMap<String, HashMap<String, Object>> ranks = new HashMap<>();

    @Inject
    public Config(@DataDirectory Path dataDirectory)
    {
        List<String> ranks = Arrays.asList("rank1", "rank2", "rank3", "rank4", "rank5");
        int i = 0;

        for (String rank : ranks) {
            i++;
            HashMap<String, Object> data = new HashMap<>();
            data.put("time", 60 * i);
            data.put("title", rank);

            this.ranks.put(rank, data);
        }

        this.config = new Json("config", dataDirectory.toString());
        this.config.setDefault(ConfigType.DB_HOST.getName(), "127.0.0.1");
        this.config.setDefault(ConfigType.DB_PORT.getName(), 3306);
        this.config.setDefault(ConfigType.DB_BASE.getName(), "servers");
        this.config.setDefault(ConfigType.DB_USER.getName(), "user");
        this.config.setDefault(ConfigType.DB_PASS.getName(), "secret");
        this.config.setDefault(ConfigType.DB_MAIN_TABLE.getName(), "playtime");
        this.config.setDefault(ConfigType.DB_DETAIL_TABLE.getName(), "playtime_detail");
        this.config.setDefault(ConfigType.RANKS.getName(), this.ranks);
        this.config.setDefault(ConfigType.PERIOD.getName(), 5);
        this.config.setDefault(ConfigType.TIME_TYPE_SECONDS.getName(), " с.");
        this.config.setDefault(ConfigType.TIME_TYPE_MINUTES.getName(), " м.");
        this.config.setDefault(ConfigType.TIME_TYPE_HOURS.getName(), " ч.");
        this.config.setDefault(ConfigType.MESSAGE_PLAYER_NOT_FOUND.getName(), "Информация о данном игроке отсутствует!");
        this.config.setDefault(ConfigType.MESSAGE_PLAYER_NAME_LINE.getName(), "Статистика игрока: %s");
        this.config.setDefault(ConfigType.MESSAGE_PLAYER_MAIN_TIME_LINE.getName(), "Общее время игры: %s");
        this.config.setDefault(ConfigType.MESSAGE_PLAYER_SESSION_TIME_LINE.getName(), "Текущая сессия: %s");
        this.config.setDefault(ConfigType.MESSAGE_PLAYER_CURRENT_RANK_LINE.getName(), "Текущий ранг: %s");
        this.config.setDefault(ConfigType.MESSAGE_PLAYER_NEXT_RANK_LINE.getName(), "Время до след. ранга: %s");
        this.config.setDefault(ConfigType.MESSAGE_PLAYER_NEXT_RANK_WAIT_LINE.getName(), "ожидайте обновления");
        this.config.setDefault(ConfigType.MESSAGE_PREFIX.getName(), "");
        this.config.setDefault(ConfigType.MESSAGE_RANKUP_TITLE.getName(), "Поздравяем!");
        this.config.setDefault(ConfigType.MESSAGE_RANKUP_SUBTITLE.getName(), "Ваш текущий ранг: %s");
        this.config.setDefault(ConfigType.MESSAGE_RANKUP.getName(), "Поздравляем! Игрок %s получил новый %s!");
        this.config.setDefault(ConfigType.BYPASS_PERMISSION.getName(), "ranks.bypass");

        this.ranks.clear();

        FlatFileSection section = this.config.getSection(ConfigType.RANKS.getName());

        for (String rank : section.singleLayerKeySet()) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("title", this.config.getString(ConfigType.RANKS.getName() + "." + rank + ".title"));
            data.put("time", this.config.getInt(ConfigType.RANKS.getName() + "." + rank + ".time"));

            this.ranks.put(rank, data);
        }
    }

    public FlatFile getConfig()
    {
        return this.config;
    }

    public HashMap<String, HashMap<String, Object>> getRanks()
    {
        return this.ranks;
    }
}
