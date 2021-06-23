/*

   CitizensBooks
   Copyright (c) 2018 @ Drăghiciu 'nicuch' Nicolae

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package ro.nicuch.citizensbooks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerActions implements Listener {
    private final CitizensBooksPlugin plugin;
    private final CitizensBooksAPI api;

    public PlayerActions(CitizensBooksPlugin plugin) {
        api = (this.plugin = plugin).getAPI();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().substring(1).split(" ")[0];
        if (!this.plugin.getSettings().isString("commands." + command + ".filter_name"))
            return;
        event.setCancelled(true);
        String filterName = this.plugin.getSettings().getString("commands." + command + ".filter_name");
        String permission = this.plugin.getSettings().isString("commands." + command + ".permission") ? this.plugin.getSettings().getString("commands." + command + ".permission") : "none";
        if (!(permission.equalsIgnoreCase("none") || this.api.hasPermission(player, permission)))
            return;
        if (!this.api.hasFilter(filterName)) {
            player.sendMessage(this.plugin.getMessage("lang.no_book_for_filter", ConfigDefaults.no_book_for_filter));
            return;
        }
        ItemStack book = this.api.getFilter(filterName);
        this.api.openBook(event.getPlayer(), this.api.placeholderHook(player, book, null));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (this.plugin.isAuthmeEnabled())
            return;
        if (!this.plugin.getSettings().isItemStack("join_book"))
            return;
        if (this.api.hasPermission(event.getPlayer(), "npcbook.nojoinbook"))
            return;
        Player player = event.getPlayer();
        if (this.plugin.getSettings().isLong("join_book_last_seen_by_players." + player.getUniqueId().toString()))
            if (this.plugin.getSettings().getLong("join_book_last_seen_by_players." + player.getUniqueId().toString(), 0) >= this.plugin.getSettings().getLong("join_book_last_change", 0))
                return;
        this.plugin.getSettings().set("join_book_last_seen_by_players." + player.getUniqueId().toString(), System.currentTimeMillis());
        this.plugin.saveSettings();
        ItemStack book = this.plugin.getSettings().getItemStack("join_book");
        if (book == null)
            return;
        this.api.openBook(event.getPlayer(), this.api.placeholderHook(player, book, null));
    }
}