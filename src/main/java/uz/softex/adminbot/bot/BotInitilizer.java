package uz.softex.adminbot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Component
public class BotInitilizer {

    private JobsVNavoiBot jobsVNavoiBot;
    private TelegramBotsApi telegramBotsApi;

    @Autowired
    public BotInitilizer(JobsVNavoiBot jobsVNavoiBot, TelegramBotsApi telegramBotsApi){
        try {
            telegramBotsApi.registerBot(jobsVNavoiBot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

    }
}
