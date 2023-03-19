package net.voiddustry.redvsblue.util;

import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import webhook.Webhook;
import webhook.embed.Embed;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;


import static net.voiddustry.redvsblue.util.Utils.getRandomPlayer;
import static net.voiddustry.redvsblue.util.Utils.playerCount;

public class WebhookUtils {
    private static void send(String text, Color color, String author) {
        if (Objects.equals(author, "")) author = "";
        String webhookUrl = Config.getDiscordUrl();
        if (webhookUrl.equals("")) return;

        Webhook webhook = new Webhook(webhookUrl);
        webhook.addEmbed(new Embed()
            .setTitle(text)
            .setAuthor(author)
            .setColor(color));
        webhook.execute();
    }

    private static void sendBan(String text) {
        String webhookUrl = Config.getDiscordBansUrl();
        if (webhookUrl.equals("")) return;

        Webhook webhook = new Webhook(webhookUrl);
        webhook.addEmbed(new Embed()
                .setTitle(text)
                .setTimestamp(Instant.now().toString())
                .setColor(Color.red));
        webhook.execute();
    }

    public static void sendReport(String reportedName, Player player, String[] text) {
        String webhookUrl = Config.getDiscordReportsUrl();
        String pingRoleID = Config.getDiscordReportsRoleID();
        if (webhookUrl.equals("")) return;

        Webhook webhook = new Webhook(webhookUrl);
        webhook.setContent("<@&" + pingRoleID + ">");
        webhook.addEmbed(new Embed()
                .setTitle("Report on " + reportedName)
                .setDescription("Text: " + text[1] + "\n\nReport by " + player.plainName() + "\nuuid: " + player.uuid())
                .setTimestamp(Instant.now().toString())
                .setColor(Color.ORANGE));
        webhook.execute();
    }

    public static void sendPlayerJoinMessage(String name) {
        send(name + " has joined", Color.GREEN, "");
    }

    public static void sendPlayerLeaveMessage(String name) {
        send(name + " has disconnected from server", Color.RED, "");
    }

    public static void sendPlayerChatMessage(String text, String name) {
        if (!text.startsWith("/")) {
            send(name + ": " + text, Color.YELLOW, "");
        }
    }

    public static void sendServerStartMessage() {
        send("Server has been started!", Color.CYAN, "");
    }

    public static void sendPlayerKillMessage(String killer, String playerName) {
        send(killer + " killed " + playerName, Color.ORANGE, "");
    }

    public static void sendGameOverMessage() {
        send("Game over", Color.GRAY, "");
    }

    public static void sendGameStartMessage() {

        send("New game has been started on map " + Strings.stripColors(Vars.state.map.name()) + " with " + playerCount() + " players", Color.WHITE, "");

    }

    public static void sendGameWinMessage() {
        send("Blue Reached 102 Wave And Won The Game!!!", Color.BLUE, "");
    }

    // Admin.class Events

    public static void sendPlayerBanMessage(Player player) {
        sendBan("Player Banned.\nPlayer name: `" + player.name + "`\n\nPlayer plain name: `" + player.plainName() + "`\n\nPlayer UUID: `" + player.uuid() + "`");
    }
}
