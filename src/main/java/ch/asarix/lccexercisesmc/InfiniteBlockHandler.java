package ch.asarix.lccexercisesmc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Random;

// Lorsqu'un joueur effectue la commande /infiniteblock, un bloc aléatoire
// se pose à ses pieds. Lorsqu'il le casse, un autre bloc aléatoire apparaît.
public class InfiniteBlockHandler implements CommandExecutor, Listener {
    Location infiniteBlockLocation = null;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Cette commande ne peut être effectuée que par un joueur !");
            return true;
        }
        Player player = (Player) commandSender;
        infiniteBlockLocation = player.getLocation().getBlock().getLocation();
        infiniteBlockLocation.getBlock().setType(generateNewBlock());
        commandSender.sendMessage("Le bloc infini est apparu aux coordonnées "
                + infiniteBlockLocation.getX() + " " + infiniteBlockLocation.getY() + " " + infiniteBlockLocation.getZ());
        return true;
    }

    private Material generateNewBlock() {
        Random random = new Random();
        Material material;
        do {
            int materialIndex = random.nextInt(Material.values().length);
            material = Material.values()[materialIndex];
        }
        while (!material.isSolid());
        return material;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (infiniteBlockLocation == null) return;
        // Bien faire gaffe au equals() et non ==
        if (event.getBlock().equals(infiniteBlockLocation.getBlock())) {
            Block block = event.getBlock();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(LCCExercisesMC.getPlugin(LCCExercisesMC.class),
                    () -> block.setType(generateNewBlock()), 1L);
        }
    }
}
