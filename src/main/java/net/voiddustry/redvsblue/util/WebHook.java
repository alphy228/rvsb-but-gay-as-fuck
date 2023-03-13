package net.voiddustry.redvsblue.util;

import java.awt.*;
import java.io.IOException;

import static net.voiddustry.redvsblue.util.Utils.playerCount;

public class WebHook {
    public static void sendWebHook(String text, Color color) {
        Thread thread = new Thread(() -> {
            DiscordWebhook webhook = new DiscordWebhook("");
            webhook.setTts(true);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(text)
                    .setColor(color));
            try {
                webhook.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    public static void playerJoinWebHook(String name) {
        sendWebHook(name + " has joined", Color.GREEN);
    }

    public static void playerLeaveWebHook(String name) {
        DiscordWebhook webhook = new DiscordWebhook("");
        webhook.setTts(true);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(name + " has disconnected from server")
                .setColor(Color.RED));
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void playerSendMessageWebHook(String text, String name) {
        if (!text.startsWith("/")) {
            DiscordWebhook webhook = new DiscordWebhook("");
            webhook.setTts(true);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(name + ": " + text)
                    .setColor(Color.YELLOW));
            try {
                webhook.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void serverStartWebHook() {
        DiscordWebhook webhook = new DiscordWebhook("");
        webhook.setTts(true);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Server has been started!")
                .setColor(Color.CYAN));
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void playerDiedWebHook(String killer, String playerName) {
        DiscordWebhook webhook = new DiscordWebhook("");
        webhook.setTts(true);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(killer + " killed " + playerName)
                .setColor(Color.ORANGE));
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void gameOverWebHook() {
        DiscordWebhook webhook = new DiscordWebhook("");
        webhook.setTts(true);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Game over")
                .setColor(Color.GRAY));
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void newGameWebHook() {
        DiscordWebhook webhook = new DiscordWebhook("");
        webhook.setTts(true);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("New game has been started with " + playerCount() + " players!")
                .setColor(Color.WHITE));
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
