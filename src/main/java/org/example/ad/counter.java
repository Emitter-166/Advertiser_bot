package org.example.ad;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.example.Main;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class counter extends ListenerAdapter {
    static Database Database = new Database();
    int counter = 0;
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendAds(e.getGuild().getId());
            }
        }, 30_000, 10_000);
    }


    public static void sendAds(String serverId){

        try {
            Arrays.stream(Database.get(serverId, "serverId").get("adIds").toString().split(", ")).forEach(ad -> {

                try {

                    Document doc = Database.get(ad, "adId");
                    long time_sent = Long.parseLong(doc.get("last_sent_on").toString());
                    long repeat_every = Long.parseLong(doc.get("repeat_every").toString());

                    if( System.currentTimeMillis() - time_sent > repeat_every){
                        Database.set(ad, "adId", "last_sent_on", System.currentTimeMillis(), false);

                        TextChannel channel = Main.jda.getTextChannelById(doc.get("channel").toString().replace(" ", ""));
                        String[] text = doc.get("text").toString().split(", ");
                        String[] toSend  = text[(int) Math.floor(Math.random() * text.length)].split("::");

                        System.out.println(Arrays.toString(toSend));
                        System.out.println(toSend.length);
                       for(int i = 0; i < toSend.length; i++){
                           channel.sendMessage(toSend[i]).queue();
                       }

                    }else if(time_sent == 0){
                        Database.set(ad, "adId", "last_sent_on", System.currentTimeMillis(), false);
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
