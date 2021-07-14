package net.choco.dropeditor;

import lombok.Getter;
import net.choco.dropeditor.listener.DropEditListener;
import net.choco.dropeditor.manager.FileManager;
import net.choco.dropeditor.utility.Serialization;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Getter
    private FileManager fileManager;

    @Getter
    private HashMap<String, Inventory> drops;

    @Getter
    Random r;

    @Getter
    Boolean deubg;

    @Getter
    Boolean vanillaDrops;

    public Main() {
        this.drops = new HashMap<>();
        this.r = new Random();
        this.deubg = false;
        this.vanillaDrops = true;
    }

    public void onEnable() {
        instance = this;

        this.fileManager = new FileManager(this);
        this.loadAllConfigs();
        this.vanillaDrops = getFileManager().getConfig("config.yml").get().getBoolean("vanillaDrops");

        getServer().getPluginManager().registerEvents(new DropEditListener(), this);
    }

    private void loadAllConfigs() {
        this.fileManager.getConfig("config.yml").copyDefaults(true).save();
        loadFiles();
    }

    private void reloadAllConfigs() {
        FileManager.configs.values().stream().forEach(FileManager.Config::reload);
    }

    public void loadFiles() {
        File folder = new File(this.getDataFolder(), "mobdata");
        if (!folder.exists()) {
            return;
        }
        try {
            Files.walk(Paths.get(folder.getAbsolutePath()), new FileVisitOption[0]).filter(file -> file.getFileName().toString().endsWith(".yml")).collect(Collectors.toList()).forEach(file -> this.loadData(file.getFileName().toString()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData(String type) {
        type = type.split("\\.")[0];
        this.getLogger().log(Level.INFO, "Loading " + type);
        try {
            Inventory newinv = Bukkit.createInventory(null, 54, type);
            newinv.setContents(Serialization.fromBase64(this.mobConfig(0, type, "", "")).getContents());
            this.drops.put(type, newinv);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String mobConfig(int i, String type, Object data, Object payload) {
        File folder = new File(this.getDataFolder(), "mobdata");
        File mobconfig = new File(folder, type + ".yml");
        FileConfiguration configuration = new YamlConfiguration();
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (!mobconfig.exists()) {
                mobconfig.createNewFile();
            }
            configuration.load(mobconfig);
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if (i == 0) {
            return configuration.getString("data");
        }
        if (i == 1) {
            configuration.set(data.toString(), payload);
            try {
                configuration.save(mobconfig);
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return "";
    }
}
