package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.example.ad.counter;
import org.example.ad.setup;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA jda;
    public static void main(String[] args) throws LoginException, InterruptedException {
         jda = JDABuilder.createLight("MTAwNDg0NzU1MjA4MTk1Mjg3OQ.GizQdu.hb-l53EQc5gwvHCOhPA0p-exyVIw4ksxBwzy9c")
                .addEventListeners(new counter())
                 .addEventListeners(new setup())
                .build().awaitReady();

        jda.getGuildById(tokens.test_id).upsertCommand("help", "help command for kai ad").queue();

        jda.getGuildById(tokens.test_id).upsertCommand("add", "Inserts a new Ad")
                .addOption(OptionType.CHANNEL, "advertisement-channel", "Channel you want to send the ad to")
                .addOption(OptionType.INTEGER, "delay", "how often you want the ad to be sent (in minutes)")
                .addOption(OptionType.INTEGER, "start-delay", "From when you want to start sending the ad (in minutes)")
                .addOption(OptionType.STRING, "ad-name", "Name of the ad")
                .addOption(OptionType.STRING, "contents", "contents of the add, please do /help to see more").queue();

        jda.getGuildById(tokens.test_id).upsertCommand("ad-info", "shows useful information about an ad")
                .addOption(OptionType.STRING, "ad-name", "name of the ad you want to see about").queue();

        jda.getGuildById(tokens.test_id).upsertCommand("remove", "removes an ad")
                .addOption(OptionType.STRING, "ad-name", "name of the ad you want to remove ").queue();

        jda.getGuildById(tokens.test_id).upsertCommand("show-ads", "shows all the active ads").queue();
    }
}