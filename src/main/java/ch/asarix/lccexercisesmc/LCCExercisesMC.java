package ch.asarix.lccexercisesmc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class LCCExercisesMC extends JavaPlugin implements Listener {

    HashMap<UUID, Integer> deathCounts = new HashMap<>();
    File playerKillsFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Le plugin LCCExercisesMC est en chargement...");
        BossHandler bossHandler = new BossHandler();
        InfiniteBlockHandler infiniteBlockHandler = new InfiniteBlockHandler();
        // Enregistrer les commandes
        Bukkit.getPluginCommand("countdown").setExecutor(new CountdownCommand());
        Bukkit.getPluginCommand("boss").setExecutor(bossHandler);
        Bukkit.getPluginCommand("infiniteblock").setExecutor(infiniteBlockHandler);
        // Enregistrer les listeners, qui détecteront les évènements du jeu
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(bossHandler, this);
        Bukkit.getPluginManager().registerEvents(infiniteBlockHandler, this);
        // Créer le fichier playerkills.yml dans le dossier du plugin
        // qui stockera le nombre de kills qu'a chaque joueur
        File pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        playerKillsFile = new File(pluginFolder, "playerkills.yml");
        if (!playerKillsFile.exists()) {
            try {
                playerKillsFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Le plugin LCCExercisesMC s'éteint...");
    }

    // Quand un joueur en tue un autre, son score est sauvegardé
    // dans le fichier playerkills.yml
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player attacker = event.getEntity().getKiller();
        // Il est possible qu'un joueur meure d'autre chose qu'un autre joueur
        if (attacker == null) return;
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(playerKillsFile);
        int killsCount = configuration.getInt(attacker.getName(), 0);
        configuration.set(attacker.getName(), killsCount + 1);
        try {
            configuration.save(playerKillsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Simple partie de feuille cailloux ciseaux avec l'ordinateur quand
    // un joueur envoie "pierre", "feuille" ou "ciseaux" dans le chat.
    // Les messages d'annonce du gagnant sont envoyés en couleur.
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        String playerPlay = msg.toLowerCase();
        List<String> prsPlays = new ArrayList<>();
        prsPlays.add("pierre");
        prsPlays.add("feuille");
        prsPlays.add("ciseaux");
        if (prsPlays.contains(playerPlay)) {
            Random random = new Random();
            int computerPlayIndex = random.nextInt(prsPlays.size());
            String computerPlay = prsPlays.get(computerPlayIndex);
            msg += "\n[Ordinateur] " + computerPlay;
            if (computerPlay.equals(playerPlay)) {
                Bukkit.broadcastMessage(ChatColor.GRAY + "Egalité !");
            } else {
                boolean playerWon = (computerPlay.equals("feuille") && playerPlay.equals("ciseaux"))
                        || (computerPlay.equals("pierre") && playerPlay.equals("feuille"))
                        || (computerPlay.equals("ciseaux") && playerPlay.equals("pierre"));
                if (playerWon) {
                    msg += "\n" + ChatColor.GREEN + "Le joueur a gagné !";
                } else {
                    msg += "\n" + ChatColor.RED + "L'ordinateur a gagné !";
                }
            }
        }
        event.setMessage(msg);
    }

    // Compteur de morts pour chaque joueur
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();
        int deathNumb = 1;
        if (deathCounts.containsKey(playerUUID)) {
            deathNumb = deathCounts.get(playerUUID) + 1;
        }
        event.setDeathMessage("Le joueur " + player.getName() + " est mort ! Nombre de morts : " + deathNumb);
        deathCounts.put(playerUUID, deathNumb);
    }
}
