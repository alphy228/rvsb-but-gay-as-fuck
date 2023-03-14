package net.voiddustry.redvsblue.util;

import webhook.Webhook;
import webhook.embed.Embed;

import java.awt.*;

import static net.voiddustry.redvsblue.util.Utils.playerCount;

public class WebhookUtils {
    private static void send(String text, Color color) {
        String webhookUrl = Config.getDiscordUrl();
        if (webhookUrl.equals("")) return;

        Webhook webhook = new Webhook(webhookUrl);
        webhook.addEmbed(new Embed()
            .setTitle(text)
            .setColor(color));
        webhook.execute();
    }

    public static void sendPlayerJoinMessage(String name) {
        send(name + " has joined", Color.GREEN);
    }

    public static void sendPlayerLeaveMessage(String name) {
        send(name + " has disconnected from server", Color.RED);
    }

    public static void sendPlayerChatMessage(String text, String name) {
        if (!text.startsWith("/")) {
            send(name + ": " + text, Color.YELLOW);
        }
    }

    public static void sendServerStartMessage() {
        send("Server has been started!", Color.CYAN);
    }

    public static void sendPlayerKillMessage(String killer, String playerName) {
        send(killer + " killed " + playerName, Color.ORANGE);
    }

    public static void sendGameOverMessage() {
        send("Game over", Color.GRAY);
    }

    public static void sendGameStartMessage() {
        send("New game has been started with " + playerCount() + " players", Color.WHITE);
    }
}
