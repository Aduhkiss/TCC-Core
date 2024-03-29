package cc.leafed.tcc_core.cmd;

import cc.leafed.tcc_core.TCC_Core;
import cc.leafed.tcc_core.common.ServerData;
import com.thecloudyco.cc.database.CloverDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PortalCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Inventory inv = Bukkit.createInventory(null, (9 * 3), "~ NETWORK PORTAL ~");

        // Pull information from the database to populate the server portal
        List<ServerData> _Servers = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet results = CloverDatabase.query("SELECT * FROM `network_servers`;");
                    while(results.next()) {
                        ServerData sd = new ServerData(results.getString("name"), results.getString("velo_name"), results.getString("lore"),
                                results.getInt("position"), Material.valueOf(results.getString("itemstack")), results.getInt("max_players"), results.getInt("staff_only"));

                        ItemStack item = new ItemStack(sd.getItemStack());
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(sd.getLore());
                        meta.setDisplayName(ChatColor.RESET + sd.getName());
                        item.setItemMeta(meta);

                        inv.setItem(sd.getPosition(), item);
                    }
                } catch(SQLException ex) {
                    // Some wild shit happened while trying to access the SQL database
                }
            }
        }.runTaskAsynchronously(TCC_Core.getCore());

        Player pl = (Player) commandSender;
        pl.openInventory(inv);
        return true;
    }
}
