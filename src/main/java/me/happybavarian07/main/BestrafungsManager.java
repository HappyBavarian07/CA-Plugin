package me.happybavarian07.main;/*
 * @Author HappyBavarian07
 * @Date 13.11.2022 | 17:46
 */

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BestrafungsManager extends BukkitRunnable {
    private final Map<UUID, List<Bestrafung>> bestrafungsListe;
    private final CAPluginMain plugin;
    private final File bestrafungsFile;
    private final FileConfiguration bestrafungsConfig;

    public BestrafungsManager(CAPluginMain plugin, File bestrafungsFile) {
        this.bestrafungsListe = new HashMap<>();
        this.plugin = plugin;
        this.bestrafungsFile = bestrafungsFile;
        this.bestrafungsConfig = YamlConfiguration.loadConfiguration(bestrafungsFile);
        if(!bestrafungsConfig.isConfigurationSection("BestrafungsCount")) bestrafungsConfig.createSection("BestrafungsCount");
        if(!bestrafungsConfig.isConfigurationSection("Bestrafungen")) bestrafungsConfig.createSection("Bestrafungen");
    }

    public boolean hasBestrafungsCount(UUID player, int count) {
        int bestrafungsCount = getBestrafungsCount(player, true);
        if (bestrafungsCount == 0 || count < 0) return false;
        return count >= bestrafungsCount;
    }

    public int getBestrafungsCount(UUID player, boolean returnZeroIfMissing) {
        if (!bestrafungsListe.containsKey(player)) {
            if(!returnZeroIfMissing) return 1;
            return 0;
        }
        return bestrafungsListe.get(player).size();
    }

    public void checkBestrafungen(UUID player) {
        Player playerObject = Bukkit.getPlayer(player);
        if(playerObject == null) return;
        boolean saveAfterGivingTheEffect = false;
        if(bestrafungsListe.get(player) == null) return;
        for(Bestrafung bestrafung : bestrafungsListe.get(player)) {
            if(bestrafung.getExpirationDate() <= System.currentTimeMillis()) {
                bestrafungsListe.get(player).remove(bestrafung);
                if(!playerObject.hasPotionEffect(bestrafung.getPotionEffectType()))
                    playerObject.removePotionEffect(bestrafung.getPotionEffectType());
                saveAfterGivingTheEffect = true;
                continue;
            }
            if(!playerObject.hasPotionEffect(bestrafung.getPotionEffectType())) {
                playerObject.addPotionEffect(new PotionEffect(
                        bestrafung.getPotionEffectType(),
                        999999,
                        bestrafung.getAmplifier(),
                        false,
                        false,
                        false
                ));
            }
        }
        if(saveAfterGivingTheEffect) {
            saveBestrafungen();
        }
    }

    public Bestrafung getBestrafung(UUID player, int position) {
        if (!hasBestrafungsCount(player, position)) return null;
        return bestrafungsListe.get(player).get(position - 1);
    }

    public List<Bestrafung> getBestrafungen(UUID player) {
        if (!bestrafungsListe.containsKey(player)) return new ArrayList<>();
        return bestrafungsListe.get(player);
    }

    public void removeBestrafung(UUID player, int position, boolean saveEdit) {
        if (!hasBestrafungsCount(player, position)) return;
        if (!bestrafungsListe.containsKey(player)) return;
        if (saveEdit) {
            Bestrafung warningFromList = getBestrafung(player, position);
            if(Bukkit.getPlayer(player) != null && Bukkit.getPlayer(player).hasPotionEffect(warningFromList.getPotionEffectType())) {
                Bukkit.getPlayer(player).removePotionEffect(warningFromList.getPotionEffectType());
            }
            removeBestrafungFromConfig(player, warningFromList);
        }
        bestrafungsListe.get(player).remove(position - 1);
    }

    public void addBestrafung(UUID player, Bestrafung bestrafung, boolean saveEdit) {
        if (!bestrafungsListe.containsKey(player)) bestrafungsListe.put(player, new ArrayList<>());
        if (hasBestrafungsCount(player, bestrafung.getBestrafungsCount())) bestrafung.setBestrafungsCount(getBestrafungsCount(player, true) + 1);

        bestrafungsListe.get(player).add(bestrafung);
        if (saveEdit) {
            saveBestrafungToConfig(player, bestrafung);
        }
    }

    private void saveBestrafungToConfig(UUID player, Bestrafung bestrafung) {
        bestrafungsConfig.set("BestrafungsCount." + player.toString(), getBestrafungsCount(player, true));
        String path = "Bestrafungen." + player + "." + bestrafung.getBestrafungsCount() + ".";
        bestrafungsConfig.set(path + "playerUUID", player.toString());
        bestrafungsConfig.set(path + "bestrafungsCount", bestrafung.getBestrafungsCount());
        bestrafungsConfig.set(path + "creationDate", bestrafung.getCreationDate());
        bestrafungsConfig.set(path + "expirationDate", bestrafung.getExpirationDate());
        bestrafungsConfig.set(path + "potionEffectType", bestrafung.getPotionEffectType().getName());
        bestrafungsConfig.set(path + "amplifier", bestrafung.getAmplifier());
        try {
            bestrafungsConfig.save(bestrafungsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO Edit Methods (setReason, setExpirationDate, etc.)

    private void removeBestrafungFromConfig(UUID player, Bestrafung bestrafung) {
        if (bestrafungsConfig.contains("BestrafungsCount." + player.toString()) &&
                bestrafungsConfig.isConfigurationSection("Bestrafungen." + player + "." + bestrafung.getBestrafungsCount())) {
            bestrafungsConfig.set("BestrafungsCount." + player, bestrafungsConfig.getInt("BestrafungsCount." + player) - 1);
            bestrafungsConfig.set("Bestrafungen." + player + "." + bestrafung.getBestrafungsCount(), null);
            try {
                bestrafungsConfig.save(bestrafungsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadBestrafungen() {
        if (!bestrafungsConfig.isConfigurationSection("Bestrafungen")) bestrafungsConfig.createSection("Bestrafungen");

        bestrafungsConfig.getConfigurationSection("Bestrafungen").getKeys(false).forEach(player -> {
            String path = "Bestrafungen." + player;
            if (bestrafungsConfig.isConfigurationSection(path)) {
                for (String count : bestrafungsConfig.getConfigurationSection(path).getKeys(false)) {
                    String tempPath = path + "." + count;
                    Bestrafung warning = new Bestrafung(
                            UUID.fromString(player),
                            bestrafungsConfig.getLong(tempPath + "expirationDate"),
                            bestrafungsConfig.getLong(tempPath + "creationDate"),
                            PotionEffectType.getByName(bestrafungsConfig.getString(tempPath + "potionEffectType", String.valueOf(PotionEffectType.SLOW))),
                            bestrafungsConfig.getInt(tempPath + "bestrafungsCount"),
                            bestrafungsConfig.getInt(tempPath + "amplifier")
                    );
                    if(warning.getExpirationDate() > System.currentTimeMillis()) {
                        addBestrafung(UUID.fromString(player), warning, false);
                    }
                }
                bestrafungsConfig.set("BestrafungsCount." + player, getBestrafungsCount(UUID.fromString(player), true));
            }
        });
        try {
            bestrafungsConfig.save(bestrafungsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.runTaskTimer(plugin, 0L, 2400L);
    }

    public void saveBestrafungen() {
        for(UUID player : bestrafungsListe.keySet()) {
            if(bestrafungsListe.get(player).isEmpty()) continue;
            for(Bestrafung bestrafung : bestrafungsListe.get(player)) {
                if(bestrafung.getExpirationDate() <= System.currentTimeMillis()) continue;
                saveBestrafungToConfig(player, bestrafung);
            }
        }
    }

    public Map<UUID, List<Bestrafung>> getBestrafungsListe() {
        return bestrafungsListe;
    }

    @Override
    public void run() {
        for(UUID player : bestrafungsListe.keySet()) {
            if(bestrafungsListe.get(player).isEmpty()) continue;
            Player playerObject = Bukkit.getPlayer(player);
            if(playerObject == null) continue;
            boolean saveAfterGivingTheEffect = false;
            for(Bestrafung bestrafung : bestrafungsListe.get(player)) {
                if(bestrafung.getExpirationDate() <= System.currentTimeMillis()) {
                    bestrafungsListe.get(player).remove(bestrafung);
                    if(!playerObject.hasPotionEffect(bestrafung.getPotionEffectType()))
                        playerObject.removePotionEffect(bestrafung.getPotionEffectType());
                    saveAfterGivingTheEffect = true;
                    continue;
                }
                if(!playerObject.hasPotionEffect(bestrafung.getPotionEffectType())) {
                    playerObject.addPotionEffect(new PotionEffect(
                            bestrafung.getPotionEffectType(),
                            999999,
                            bestrafung.getAmplifier(),
                            false,
                            false,
                            false
                    ));
                }
            }
            if(saveAfterGivingTheEffect) {
                saveBestrafungen();
            }
        }
    }
}
