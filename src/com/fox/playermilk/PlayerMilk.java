package com.fox.playermilk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerMilk extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("PlayerMilk active!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerMilk closed!");
    }

    // Quando clicar com botão direito em outro jogador
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {

        if (!(event.getRightClicked() instanceof Player))
            return;

        Player clicker = event.getPlayer();
        Player target = (Player) event.getRightClicked();

        ItemStack item = clicker.getInventory().getItemInMainHand();

        if (item.getType() != Material.BUCKET)
            return;

        event.setCancelled(true);

        // Remove 1 balde vazio
        item.setAmount(item.getAmount() - 1);

        // Cria milk bucket customizado
        ItemStack milk = new ItemStack(Material.MILK_BUCKET);
        ItemMeta meta = milk.getItemMeta();

        meta.setDisplayName(ChatColor.WHITE + "Milk Bucket");
        meta.setLore(List.of(ChatColor.GRAY + target.getName() + "'s milk"));

        milk.setItemMeta(meta);

        // Dá o milk para o jogador
        clicker.getInventory().addItem(milk);

        // Remove 1 ponto de fome do jogador alvo
        int newFood = Math.max(0, target.getFoodLevel() - 4);
        target.setFoodLevel(newFood);

        clicker.sendMessage(ChatColor.GREEN + "You collected milk from " + target.getName());
        target.sendMessage(ChatColor.RED + "Your milk has been collected!");
    }

    // Quando beber o leite
    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {

        ItemStack item = event.getItem();

        if (item.getType() != Material.MILK_BUCKET)
            return;

        if (!item.hasItemMeta())
            return;

        if (!item.getItemMeta().hasLore())
            return;

        List<String> lore = item.getItemMeta().getLore();

        if (lore == null || lore.isEmpty())
            return;

        if (!lore.get(0).contains("'s milk"))
            return;

        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            int newFood = Math.min(20, player.getFoodLevel() + 4);
            player.setFoodLevel(newFood);
        }, 1L);
    }
}
