package com.github.ffrancoc.foca.db;

import com.github.ffrancoc.foca.model.ConfigurationApp;
import com.github.ffrancoc.foca.model.WindowProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

public class ApplicationSetting {
    private static String DIRECTORY = System.getProperty("user.home")+ File.separator+".FocaGuiDB";
    private static String SETTING_FILE = (DIRECTORY + File.separator + "foca-app.setting");
    private static Gson gson;

    // Funcion para crear directorio si no existe
    private static void createSettingDirectory(){
        File homeDir = new File(DIRECTORY);
        if (!homeDir.exists()){
            homeDir.mkdir();
        }
    }

    // Funcion para comprobar si el archivo de preferencias existe
    public static boolean existsSettings(){
        return new File(SETTING_FILE).exists();
    }

    // Funcion para eliminar el archivo de preferencias
    public static void deleteSettings(){
        File settings = new File(SETTING_FILE);
        if(settings.exists()){
            settings.delete();
        }
    }

    // Funcion para crear el archivo de preferencias por default
    public static void initDefaultSettings(){
        createSettingDirectory();
        WindowProperty windowProperty = new WindowProperty(800, 600, 0, 0);
        ConfigurationApp appConfig = new ConfigurationApp(windowProperty);
        saveSettings(appConfig, false);
    }

    // Funcion para guardar el archivo de preferencias
    public static void saveSettings(ConfigurationApp appConfig, boolean replace){
        gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        String settings = gson.toJson(appConfig);

        if(!replace) {
            try (FileWriter writer = new FileWriter(SETTING_FILE, false)) {
                writer.write(settings);
                writer.flush();
            } catch (IOException e) {
                System.err.println("Error to create setting template file, " + e.getMessage());
            }
        }else{
            deleteSettings();
            saveSettings(appConfig, false);
        }
    }

    // Funcion para abrir el archivo de preferencias
    public static ConfigurationApp openSettings(){
        ConfigurationApp appConfig = null;
        try(Reader reader = Files.newBufferedReader(new File(SETTING_FILE).toPath())){
            gson = new Gson();
            appConfig = gson.fromJson(reader, ConfigurationApp.class);
        } catch (IOException e) {
            System.err.println("Error to open setting file, "+ e.getMessage());

        }
        return appConfig;
    }
}
