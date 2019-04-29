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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.context.ContextManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.StringUtil;

import net.milkbowl.vault.permission.Permission;

public class PlayerCommands implements TabExecutor {
    private final CitizensBooksPlugin plugin;
    private final CitizensBooksAPI api;

    public PlayerCommands(CitizensBooksPlugin plugin) {
        api = (this.plugin = plugin).getAPI();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        LuckPermsApi luckPerms = this.plugin.getLuckPermissions(); //If LuckPerms not enabled, this will return null
        boolean useLuckPerms = this.plugin.isLuckPermsEnabled(); //So we check if LuckPerms is enabled

        Permission vaultPerms = this.plugin.getVaultPermissions(); //If vault not enabled or luckperms is used, this will return null
        boolean useVault = this.plugin.isVaultEnabled(); //So we check if Vault is hooked
        if (args.length > 0) {
            switch (args[0]) {
                case "about":
                    this.sendAbout(sender);
                    break;
                case "reload":
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.reload")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.reload")) || sender.hasPermission("npcbook.command.reload")) {
                        /*
                         * this.plugin.saveConfig();
                         *
                         * No need to be saved anymore!
                         * If config is edited, when reloaded is
                         * overriten the file, so the edit is lost
                         */
                        this.plugin.reloadSettings();
                        sender.sendMessage(this.plugin.getMessage("lang.config_reloaded", ConfigDefaults.config_reloaded));
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "set":
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.set")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.set")) || sender.hasPermission("npcbook.command.set")) {
                        sender.sendMessage(
                                this.plugin.getMessage("lang.citizens_not_enabled", ConfigDefaults.citizens_not_enabled));
                        break;
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "setjoin":
                    if (!this.isPlayer(sender)) {
                        sender.sendMessage(this.plugin.getMessage("console_cannot_use_command", ConfigDefaults.console_cannot_use_command));
                        break;
                    }
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.setjoin")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.setjoin")) || sender.hasPermission("npcbook.command.setjoin")) {
                        if (this.hasBookInHand((Player) sender)) {
                            this.plugin.getSettings().set("join_book",
                                    this.api.bookToString(this.getBookFromHand((Player) sender)));
                            this.plugin.saveSettings(); //Allways saved
                            sender.sendMessage(this.plugin
                                    .getMessage("lang.set_join_book_successfully", ConfigDefaults.set_join_book_successfully));
                        } else
                            sender.sendMessage(
                                    this.plugin.getMessage("lang.no_book_in_hand", ConfigDefaults.no_book_in_hand));
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "remove":
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.remove")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.remove")) || sender.hasPermission("npcbook.command.remove")) {
                        sender.sendMessage(
                                this.plugin.getMessage("lang.citizens_not_enabled", ConfigDefaults.citizens_not_enabled));
                        break;
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "remjoin":
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.remjoin")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.remjoin")) || sender.hasPermission("npcbook.command.remjoin")) {
                        this.plugin.getSettings().set("join_book", null);
                        this.plugin.saveSettings(); //Allways saved
                        sender.sendMessage(this.plugin
                                .getMessage("lang.remove_join_book_successfully", ConfigDefaults.remove_join_book_successfully));
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "getbook":
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.getbook")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.getbook")) || sender.hasPermission("npcbook.command.getbook")) {
                        sender.sendMessage(
                                this.plugin.getMessage("lang.citizens_not_enabled", ConfigDefaults.citizens_not_enabled));
                        break;
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "openbook":
                    if (!this.isPlayer(sender)) {
                        sender.sendMessage(this.plugin.getMessage("console_cannot_use_command", ConfigDefaults.console_cannot_use_command));
                        break;
                    }
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.openbook")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.openbook")) || sender.hasPermission("npcbook.command.openbook")) {
                        if (this.hasBookInHand((Player) sender)) {
                            this.openBook((Player) sender, this.getBookFromHand((Player) sender));
                        } else
                            sender.sendMessage(
                                    this.plugin.getMessage("lang.no_book_in_hand", ConfigDefaults.no_book_in_hand));
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "setcmd":
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.setcmd")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.setcmd")) || sender.hasPermission("npcbook.command.setcmd")) {
                        if (args.length > 2) {
                            this.plugin.getSettings().set("commands." + args[1], args[2]);
                            this.plugin.saveSettings();
                            sender.sendMessage(this.plugin
                                    .getMessage("lang.set_custom_command_successfully",
                                            ConfigDefaults.set_custom_command_successfully)
                                    .replace("%command%", args[1]).replace("%filter_name%", args[2]));
                        } else
                            sender.sendMessage(this.plugin.getMessage("lang.usage.setcmd", ConfigDefaults.usage_setcmd));
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "remcmd":
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.remcmd")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.remcmd")) || sender.hasPermission("npcbook.command.remcmd")) {
                        if (args.length > 1) {
                            this.plugin.getSettings().set("commands." + args[1], null);
                            this.plugin.saveSettings();
                            sender.sendMessage(this.plugin
                                    .getMessage("lang.remove_custom_command_successfully",
                                            ConfigDefaults.remove_custom_command_successfully)
                                    .replace("%command%", args[1]));
                        } else
                            sender.sendMessage(this.plugin.getMessage("lang.usage.remcmd", ConfigDefaults.usage_remcmd));
                    } else
                        sender.sendMessage(this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                    break;
                case "filter":
                    if (args.length > 1) {
                        switch (args[1]) {
                            case "set":
                                if (!this.isPlayer(sender)) {
                                    sender.sendMessage(this.plugin.getMessage("console_cannot_use_command", ConfigDefaults.console_cannot_use_command));
                                    break;
                                }
                                if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.filter.set")) ||
                                        (useVault && vaultPerms.has(sender, "npcbook.command.filter.set")) || sender.hasPermission("npcbook.command.filter.set")) {
                                    if (args.length > 2) {
                                        if (this.hasBookInHand((Player) sender)) {
                                            this.api.createFilter(args[2], this.getBookFromHand((Player) sender));
                                            sender.sendMessage(
                                                    this.plugin.getMessage("lang.filter_saved", ConfigDefaults.filter_saved)
                                                            .replace("%filter_name%", args[2]));
                                        } else
                                            sender.sendMessage(this.plugin.getMessage("lang.no_book_in_hand",
                                                    ConfigDefaults.no_book_in_hand));
                                    } else
                                        sender.sendMessage(
                                                this.plugin.getMessage("lang.usage.filter.set", ConfigDefaults.usage_filter_set));
                                } else
                                    sender.sendMessage(
                                            this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                                break;
                            case "remove":
                                if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.filter.remove")) ||
                                        (useVault && vaultPerms.has(sender, "npcbook.command.filter.remove")) || sender.hasPermission("npcbook.command.filter.remove")) {
                                    if (args.length > 2) {
                                        this.api.removeFilter(args[2]);
                                        sender.sendMessage(
                                                this.plugin.getMessage("lang.filter_removed", ConfigDefaults.filter_removed)
                                                        .replace("%filter_name%", args[2]));
                                    } else
                                        sender.sendMessage(this.plugin.getMessage("lang.usage.filter.remove",
                                                ConfigDefaults.usage_filter_remove));
                                } else
                                    sender.sendMessage(
                                            this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                                break;
                            case "getbook":
                                if (!this.isPlayer(sender)) {
                                    sender.sendMessage(this.plugin.getMessage("console_cannot_use_command", ConfigDefaults.console_cannot_use_command));
                                    break;
                                }
                                if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.filter.getbook")) ||
                                        (useVault && vaultPerms.has(sender, "npcbook.command.filter.getbook")) || sender.hasPermission("npcbook.command.filter.getbook")) {
                                    if (args.length > 2) {
                                        if (this.api.hasFilter(args[2])) {
                                            ItemStack book = this.api.getFilter(args[2]);
                                            ((Player) sender).getInventory().addItem(book);
                                            sender.sendMessage(
                                                    this.plugin.getMessage("lang.book_recived", ConfigDefaults.book_recived));
                                        } else
                                            sender.sendMessage(this.plugin.getMessage("lang.no_book_for_filter",
                                                    ConfigDefaults.no_book_for_filter));
                                    } else
                                        sender.sendMessage(this.plugin.getMessage("lang.usage.filter.getbook",
                                                ConfigDefaults.usage_filter_getbook));
                                } else
                                    sender.sendMessage(
                                            this.plugin.getMessage("lang.no_permission", ConfigDefaults.no_permission));
                                break;
                            default:
                                this.sendFilterHelp(sender);
                                break;
                        }
                    } else
                        this.sendFilterHelp(sender);
                    break;
                default:
                    if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.help")) ||
                            (useVault && vaultPerms.has(sender, "npcbook.command.help")) || sender.hasPermission("npcbook.command.help"))
                        this.sendHelp(sender);
                    else
                        this.sendAbout(sender);
                    break;
            }
        } else {
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.help")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.help")) || sender.hasPermission("npcbook.command.help"))
                this.sendHelp(sender);
            else
                this.sendAbout(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        LuckPermsApi luckPerms = this.plugin.getLuckPermissions(); //If LuckPerms not enabled, this will return null
        boolean useLuckPerms = this.plugin.isLuckPermsEnabled(); //So we check if LuckPerms is enabled

        Permission vaultPerms = this.plugin.getVaultPermissions(); //If vault not enabled or luckperms is used, this will return null
        boolean useVault = this.plugin.isVaultEnabled(); //So we check if Vault is hooked

        if (args.length == 1) {
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.set")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.set")) || sender.hasPermission("npcbook.command.set"))
                commands.add("set");
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.remove")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.remove")) || sender.hasPermission("npcbook.command.remove"))
                commands.add("remove");
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.setjoin")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.setjoin")) || sender.hasPermission("npcbook.command.setjoin"))
                commands.add("setjoin");
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.remjoin")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.remjoin")) || sender.hasPermission("npcbook.command.remjoin"))
                commands.add("remjoin");
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.getbook")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.getbook")) || sender.hasPermission("npcbook.command.getbook"))
                commands.add("getbook");
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.openbook")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.openbook")) || sender.hasPermission("npcbook.command.openbook"))
                commands.add("openbook");
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.filter")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.filter")) || sender.hasPermission("npcbook.command.filter"))
                commands.add("filter");
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.setcmd")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.setcmd")) || sender.hasPermission("npcbook.command.setcmd"))
                commands.add("setcmd");
            if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.remcmd")) ||
                    (useVault && vaultPerms.has(sender, "npcbook.command.remcmd")) || sender.hasPermission("npcbook.command.remcmd"))
                commands.add("remcmd");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (args[0].equals("filter")) {
                if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.filter.set")) ||
                        (useVault && vaultPerms.has(sender, "npcbook.command.filter.set")) || sender.hasPermission("npcbook.command.filter.set"))
                    commands.add("set");
                if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.filter.remove")) ||
                        (useVault && vaultPerms.has(sender, "npcbook.command.filter.remove")) || sender.hasPermission("npcbook.command.filter.remove"))
                    commands.add("remove");
                if ((useLuckPerms && this.hasLuckPermission(luckPerms.getUser(sender.getName()), "npcbook.command.filter.getbook")) ||
                        (useVault && vaultPerms.has(sender, "npcbook.command.filter.getbook")) || sender.hasPermission("npcbook.command.filter.getbook"))
                    commands.add("getbook");
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }

    private void sendFilterHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "+----------------------+");
        sender.sendMessage("");
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.set", ConfigDefaults.help_filter_set));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.remove", ConfigDefaults.help_filter_remove));
        sender.sendMessage(
                this.plugin.getMessageNoHeader("lang.help.filter.getbook", ConfigDefaults.help_filter_getbook));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "+----------------------+");
    }

    @SuppressWarnings("deprecation")
    private boolean hasBookInHand(Player player) {
        ItemStack item;
        switch (CitizensBooksAPI.version) {
            case "v1_8_R3":
            case "v1_8_R2":
            case "v1_8_R1":
                item = player.getItemInHand();
                break;
            default:
                item = player.getInventory().getItemInMainHand();
                break;
        }
        if (item == null)
            return false;
        return item.getType().equals(Material.WRITTEN_BOOK);
    }

    private boolean isPlayer(CommandSender sender) {
        return (sender instanceof Player);
    }

    @SuppressWarnings("deprecation")
    private ItemStack getBookFromHand(Player player) {
        switch (CitizensBooksAPI.version) {
            case "v1_8_R3":
            case "v1_8_R2":
            case "v1_8_R1":
                return player.getItemInHand();
            default:
                return player.getInventory().getItemInMainHand();
        }
    }

    @SuppressWarnings("deprecation")
    private void openBook(Player player, ItemStack book) {
        BookMeta meta = (BookMeta) book.getItemMeta();
        ItemStack item = new ItemStack(Material.getMaterial("BOOK_AND_QUILL"));
        if (item.getType() == null)
            // 1.13+
            item.setType(Material.getMaterial("WRITABLE_BOOK"));
        if (item != null)
            item.setItemMeta(meta);
        switch (CitizensBooksAPI.version) {
            case "v1_8_R3":
            case "v1_8_R2":
            case "v1_8_R1":
                player.setItemInHand(item);
            default:
                player.getInventory().setItemInMainHand(item);
        }
    }

    private void sendAbout(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "+----------------------+");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "CitizensBooks");
        sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.RED + this.plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GOLD + "Auhtor: " + ChatColor.RED + "nicuch");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "+----------------------+");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "+----------------------+");
        sender.sendMessage("");
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.about", ConfigDefaults.help_about));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.set", ConfigDefaults.help_set));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.remove", ConfigDefaults.help_remove));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.setjoin", ConfigDefaults.help_setjoin));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.remjoin", ConfigDefaults.help_remjoin));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.reload", ConfigDefaults.help_reload));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.getbook", ConfigDefaults.help_getbook));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.openbook", ConfigDefaults.help_openbook));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.setcmd", ConfigDefaults.help_setcmd));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.remcmd", ConfigDefaults.help_remcmd));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.set", ConfigDefaults.help_filter_set));
        sender.sendMessage(this.plugin.getMessageNoHeader("lang.help.filter.remove", ConfigDefaults.help_filter_remove));
        sender.sendMessage(
                this.plugin.getMessageNoHeader("lang.help.filter.getbook", ConfigDefaults.help_filter_getbook));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "+----------------------+");
    }

    private boolean hasLuckPermission(User user, String permission) {
        ContextManager contextManager = this.plugin.getLuckPermissions().getContextManager();
        return user.getCachedData().getPermissionData(contextManager.lookupApplicableContexts(user).orElseGet(contextManager::getStaticContexts)).getPermissionValue(permission).asBoolean();
    }
}