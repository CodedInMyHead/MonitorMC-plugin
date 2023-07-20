package com.codedinmyhead.monitormc.monitormc.listeners;

import com.codedinmyhead.monitormc.monitormc.monitoring.MetricService;
import com.codedinmyhead.monitormc.monitormc.monitoring.MetricsEnum;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.units.qual.A;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatNumberListener implements Listener {
    @EventHandler
    public void onChat(final AsyncChatEvent event) {
        AtomicInteger sum = new AtomicInteger(0);
        List<Integer> numbersList = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(event.message().toString());

        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group());
            numbersList.add(number);
        }

        numbersList.forEach(e -> sum.set(sum.get() + e));
        MetricService.getInstance().incrementCounter(MetricsEnum.NUMBERS_SAID_COUNT, sum.get(), event.getPlayer().getName());
    }
}
