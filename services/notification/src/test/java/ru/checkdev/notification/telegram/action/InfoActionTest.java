package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InfoActionTest {

    @Test
    void whenHandleThenReturnMenuList() {
        List<String> actions = List.of(
                "/start - напечатать список доступных команд",
                "/new - регистрация нового пользователя"
        );
        InfoAction infoAction = new InfoAction(actions);
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        message.setText("/start");

        BotApiMethod<Message> result = infoAction.handle(message);

        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getChatId()).isEqualTo("1");
        assertThat(sendMessage.getText()).contains("Выберите действие:");
        assertThat(sendMessage.getText()).contains("/start");
        assertThat(sendMessage.getText()).contains("/new");
    }
}