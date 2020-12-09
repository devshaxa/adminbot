package uz.softex.adminbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class AdminbotApplication {

    static {
        ApiContextInitializer.init();
    }
    public static void main(String[] args) {
        SpringApplication.run(AdminbotApplication.class, args);
    }

}
