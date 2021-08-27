package de.zettos.locationsapi;

import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class LocationsAPI extends JavaPlugin {

    private static File file;
    private static FileConfiguration cfg;

    @SneakyThrows
    public static void createFile(String path, String name){
        file = new File(path,name+".yml");
        cfg = YamlConfiguration.loadConfiguration(file);
    }


    @SneakyThrows
    public static void saveLocation(String ID,Location location){
        cfg.set(ID,location);
        saveFile();
    }

    @SneakyThrows
    public static void deleteLocation(String ID){
        cfg.set(ID,null);
        saveFile();
    }

    public static Location getLocation(String ID){
        return (Location) cfg.get(ID);
    }

    @SneakyThrows
    public static void saveFile(){
        cfg.save(file);
    }

    @SneakyThrows
    public static void deleteFile(){
        file.delete();
    }
}
