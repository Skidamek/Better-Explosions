package pl.skidam.betterexplosions.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import pl.skidam.betterexplosions.BetterExplosions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private final Gson gson;
    private final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("better-explosions.json");
    public static ConfigFields fields;

    public Config() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    private void load() {
        try {
            Files.createDirectories(configPath.getParent());
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                fields = gson.fromJson(json, ConfigFields.class);
                checkConfigAndSave();
            } else {
                fields = new ConfigFields();
                checkConfigAndSave();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            String json = gson.toJson(fields);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkConfigAndSave() {
        if (fields.rebuildOneBlockEvery <= 0) {
            fields.rebuildOneBlockEvery = 5; // 5 is default
            BetterExplosions.LOGGER.warn("rebuildOneBlockEvery cannot be less than 1, setting it to {}", fields.rebuildOneBlockEvery);
        }

        if (fields.startRebuildingAfter < 0) {
            fields.startRebuildingAfter = 200; // 200 is default
            BetterExplosions.LOGGER.warn("startRebuildingAfter cannot be less than 0, setting it to {}", fields.startRebuildingAfter);
        }

        save();
    }

    public static class ConfigFields {
        public boolean rebuildCreeperExplosion = true;
        public boolean rebuildTntExplosion = false;
        public boolean rebuildFireballExplosion = true;
        public boolean rebuildWitherSkullExplosion = false;
        public int startRebuildingAfter = 200; // ticks
        public int rebuildOneBlockEvery = 5; // ticks
    }
}
