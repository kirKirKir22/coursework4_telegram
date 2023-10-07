package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

    public static NotificationTask parseMessage(String messageText, Long chatId, TelegramBot telegramBot) {

        Pattern MESSAGE_PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = MESSAGE_PATTERN.matcher(messageText);

        if (matcher.find() && matcher.groupCount() == 3) {
            String dateTimeStr = matcher.group(1);
            String notificationText = matcher.group(3);

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                LocalDateTime sendDateTime = LocalDateTime.parse(dateTimeStr, formatter);

                if (sendDateTime.isBefore(LocalDateTime.now())) {
                    telegramBot.execute(new SendMessage(chatId, "Ошибка: Дата и время напоминания в прошлом"));
                    return null;
                }

                return new NotificationTask(chatId, notificationText, sendDateTime);
            } catch (Exception e) {

                telegramBot.execute(new SendMessage(chatId, "Ошибка: Неправильный формат сообщения"));
            }
        }
        telegramBot.execute(new SendMessage(chatId, "Ошибка: Сообщение не соответствует ожидаемому формату." +
                " Пожалуйста, отправьте сообщение в следующем формате: dd.MM.yyyy HH:mm Текст напоминания." +
                " ВРЕМЯ, УКАЗАННОЕ В СООБЩЕНИИ НЕ ДОЛЖНО БЫТЬ В ПРОШЕДШЕМ ВРЕМЕНИ"));
        return null;
    }
}