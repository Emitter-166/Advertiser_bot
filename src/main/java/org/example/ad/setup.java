package org.example.ad;

import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.awt.*;

public class setup extends ListenerAdapter {
    Database Database = new Database();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e){
        try{
            if(e.getChannel().getType().equals(ChannelType.PRIVATE) || !e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
        } catch (NullPointerException exception){return;}
        switch (e.getName()) {
            case "help":
                e.getInteraction().replyEmbeds(new EmbedBuilder()
                                .setTitle("Help commands for advertiser")
                                .setDescription("`/add` **Inserts a new ad** \n")
                                .appendDescription("`/remove` **remove an ad** \n")
                                .appendDescription("`/show-ads` **See all current ads** \n")
                                .appendDescription("`/ad-info` **see info about a current ad** \n")
                                .addField("About ad content", "" +
                                        "you can add as many contents as you want, each separated by `, ` a coma and a white space. If there is multiple \n" +
                                        "contents, they will be chosen randomly. \n" +
                                        "each content block can have multiple texts that will be sent each one at a time. there can be many texts as you want there \n" +
                                        "to be. texts are separated by `::`. (note that messages can be set to image links too)\n" +
                                        "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                        "**Example:** \n" +
                                        "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                        "`message one::message two::message three, another one::another two::another three`", false)

                                .build())
                        .mentionRepliedUser(false).queue();
                break;


            case "add":
                e.getInteraction().reply("`Inserting new ad...`")
                        .setEphemeral(true)
                        .queue();

                long duration_milliseconds;
                StringBuilder ad_name;
                String content;
                String channelId;
                long start_delay;

                    channelId = e.getInteraction().getOption("advertisement-channel").getAsGuildChannel().getId();
                    duration_milliseconds =   e.getInteraction().getOption("delay").getAsInt() * 60_000L;
                    start_delay = (e.getInteraction().getOption("start-delay").getAsInt() * 60_000L) + System.currentTimeMillis();
                    ad_name = new StringBuilder(e.getInteraction().getOption("ad-name").getAsString());
                    content = e.getInteraction().getOption("contents").getAsString();

                    try {
                    Database.set(e.getGuild().getId(), "serverId", "adIds",   ad_name +", " , true);
                    Database.set(ad_name.toString(), "adId", "channel", channelId, false);
                    Database.set(ad_name.toString(), "adId", "text", content, false);
                    Database.set(ad_name.toString(), "adId", "repeat_every", duration_milliseconds, false);
                    Database.set(ad_name.toString(), "adId", "last_sent_on", start_delay, false);


                    } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                e.getChannel().sendMessage("`ad successfully added! do /ads to see all the ads`").queue();
                break;

            case "show-ads":
                StringBuilder result = new StringBuilder();
                String[] ad_names;
                try {
                    ad_names = Database.get(e.getGuild().getId(), "serverId").get("adIds").toString().split(", ");
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                for(int i = 0; i < ad_names.length; i++){
                    result.append(String.format("`%s.` **%s** \n", i+1, ad_names[i]));
                }
                e.getInteraction().replyEmbeds(new EmbedBuilder()
                        .setTitle("Current ads")
                        .setDescription(result.toString())
                        .build()).mentionRepliedUser(false).queue();
                break;

            case "ad-info":
                Document doc;
                String adName = e.getInteraction().getOption("ad-name").getAsString();

                try {
                    doc = Database.get(adName, "adId");
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                String channel = "<#" + doc.get("channel").toString().replace(" ", "") + ">";
                String content_info = doc.get("text").toString();
                String repeating_time_in_minutes = String.valueOf((Long.parseLong(doc.get("repeat_every").toString()) / 60_000));

                StringBuilder last_sent = new StringBuilder(String.valueOf(Long.parseLong(doc.get("last_sent_on").toString())));
                last_sent.replace(last_sent.length() -4 ,last_sent.length() -1, "");

                e.replyEmbeds(new EmbedBuilder()
                        .setTitle("Ad info")
                        .setDescription(String.format("**Ad name:** `%s` \n", adName))
                        .appendDescription(String.format("**Ad channel:** %s \n", channel))
                        .appendDescription(String.format("**Repeat every:** `%s minutes` \n", repeating_time_in_minutes))
                        .appendDescription(String.format("**Last sent:** <t:%s:R> \n", last_sent))
                        .appendDescription(String.format("**Ad content:** `%s`", content_info))
                        .setColor(Color.WHITE)

                        .build()).queue();
                break;

            case "remove":
                String ad_name_to_remove =  e.getInteraction().getOption("ad-name").getAsString();
                try {
                    Database.set(e.getGuild().getId(), "serverId", "adIds", Database.get(e.getGuild().getId(), "serverId").get("adIds").toString().replace(ad_name_to_remove + ", ", ""), false);
                    Database.collection.deleteOne(Filters.eq("adId", ad_name_to_remove));
                    e.getInteraction().reply("`Successfully removed " + ad_name_to_remove + "`").mentionRepliedUser(false).queue();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;
        }
    }
}