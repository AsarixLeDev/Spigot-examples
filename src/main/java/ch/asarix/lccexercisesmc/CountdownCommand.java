package ch.asarix.lccexercisesmc;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class CountdownCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Cette commande ne peut être effectuée que par un joueur !");
            return true;
        }
        Player player = (Player) commandSender;
        new BukkitRunnable() {
            int count = 10;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                if (count == 0) {
                    player.sendTitle("Bonne année", "!!!");
                    Location location = player.getLocation();
                    World world = location.getWorld();
                    Random random = new Random();
                    for (int i = 0; i < 10; i++) {
                        Firework fw = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
                        FireworkMeta meta = fw.getFireworkMeta();
                        Color color = Color.fromBGR(random.nextInt(100, 255), random.nextInt(10), random.nextInt(100));
                        meta.clearEffects();
                        meta.addEffect(FireworkEffect.builder().withColor(color).build());
                        fw.setFireworkMeta(meta);
                    }
                    this.cancel();
                } else {
                    player.sendTitle(String.valueOf(count), "");
                    count -= 1;
                }
            }
        }.runTaskTimer(LCCExercisesMC.getPlugin(LCCExercisesMC.class), 0, 20);
        return false;
    }
}
