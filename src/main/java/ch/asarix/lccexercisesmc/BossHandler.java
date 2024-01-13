package ch.asarix.lccexercisesmc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;


// Lorsque le joueur entre la commande /boss dans le chat, un zombie modifié apparaît.
// Si je joueur le tue, il reçoit des récompenses modifiées, définies par la classe BossLootTable.
public class BossHandler implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Cette commande ne peut être effectuée que par un joueur !");
            return true;
        }
        Player player = (Player) commandSender;
        Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
        if (!(entity instanceof Zombie)) {
            commandSender.sendMessage(ChatColor.RED + "Quelque chose s'est mal passé !");
            entity.remove();
            return true;
        }
        Zombie zombie = (Zombie) entity;
        zombie.setGlowing(true);
        zombie.setMaxHealth(100);
        zombie.setHealth(100);
        zombie.setCustomName("Boss");
        zombie.setCustomNameVisible(true);
        zombie.setMetadata("ZombieSpecial", new FixedMetadataValue(LCCExercisesMC.getPlugin(LCCExercisesMC.class), true));
        return true;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Zombie && event.getEntity().hasMetadata("ZombieSpecial")) {
            // Effacer les drops par défaut
            event.getDrops().clear();

            // Ajouter un drop personnalisé pour le zombie spécial
            event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 3));

            // Ajuster l'expérience droppée pour le zombie spécial
            event.setDroppedExp(10);
        }
    }

}
