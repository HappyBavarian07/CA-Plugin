package me.happybavarian07.main;/*
 * @Author HappyBavarian07
 * @Date 03.11.2022 | 13:31
 */

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Prefix {
    private String inGamePrefix;
    private String inGameSuffix;
    private String configName;
    private Material menuMaterial;

    public Prefix(String inGamePrefix, String inGameSuffix, String configName, Material menuMaterial) {
        this.inGamePrefix = inGamePrefix;
        this.inGameSuffix = inGameSuffix;
        this.configName = configName;
        this.menuMaterial = menuMaterial;
    }

    public String getInGamePrefix() {
        return inGamePrefix;
    }

    public void setInGamePrefix(String inGamePrefix) {
        this.inGamePrefix = inGamePrefix;
    }

    public String getInGameSuffix() {
        return inGameSuffix;
    }

    public String getFormat(Player player) {
        return getInGamePrefix() + player.getName() + getInGameSuffix();
    }

    public void setInGameSuffix(String inGameSuffix) {
        this.inGameSuffix = inGameSuffix;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Material getMenuMaterial() {
        return menuMaterial;
    }

    public void setMenuMaterial(Material menuMaterial) {
        this.menuMaterial = menuMaterial;
    }
}
