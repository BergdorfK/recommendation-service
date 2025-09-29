package com.starbank.recommendation_service.bot;

import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.model.UserH2;
import com.starbank.recommendation_service.service.RecommendationService;
import com.starbank.recommendation_service.service.UserH2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final RecommendationService recommendationService;
    private final UserH2Service userH2Service;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public TelegramBot(RecommendationService recommendationService, UserH2Service userH2Service) {
        this.recommendationService = recommendationService;
        this.userH2Service = userH2Service;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String command = message.getText();

            if (command.startsWith("/start")) {
                sendText(chatId, "Привет! Я бот, который выдает рекомендации.\n" +
                        "Используйте команду /recommend <username>, чтобы получить рекомендации.");
            } else if (command.startsWith("/recommend")) {
                String[] parts = command.split(" ", 2);
                if (parts.length == 2) {
                    String username = parts[1].trim();
                    handleRecommendCommand(chatId, username);
                } else {
                    sendText(chatId, "Неправильный формат команды. Используйте: /recommend <username>");
                }
            } else {
                sendText(chatId, "Неизвестная команда. Используйте /start или /recommend <username>.");
            }
        }
    }

    private void handleRecommendCommand(String chatId, String username) {
        Optional<UserH2> userH2Opt = userH2Service.findByUsername(username);

        if (userH2Opt.isPresent()) {
            UserH2 userH2 = userH2Opt.get();
            String userIdStr = userH2.getUserId();
            UUID userId;

            try {
                userId = UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                sendText(chatId, "Ошибка: некорректный ID пользователя в базе данных.");
                return;
            }

            RecommendationResponse response = recommendationService.getRecommendationResponse(userId);

            StringBuilder message = new StringBuilder();
            message.append("Здравствуйте ").append(userH2.getFirstName()).append(" ").append(userH2.getLastName()).append("\n");
            message.append("(данные есть в базе).\n\n");
            message.append("Новые продукты для вас:\n");

            List<RecommendationDto> recommendations = response.getRecommendations();
            if (recommendations.isEmpty()) {
                message.append("- Рекомендации отсутствуют.");
            } else {
                for (RecommendationDto rec : recommendations) {
                    message.append("- ").append(rec.getName()).append(": ").append(rec.getText()).append("\n");
                }
            }

            sendText(chatId, message.toString().trim());
        } else {
            sendText(chatId, "Пользователь не найден.");
        }
    }

    private void sendText(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            // Логгировать ошибку
            e.printStackTrace();
        }
    }
}