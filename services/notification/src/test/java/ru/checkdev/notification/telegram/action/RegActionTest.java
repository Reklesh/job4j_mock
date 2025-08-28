package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RegActionTest {

    @Mock
    private TgAuthCallWebClient authCallWebClient;
    private final String urlSiteAuth = "http://localhost:8080/login";
    private RegAction regAction;
    private Message message;

    @BeforeEach
    void setUp() {
        regAction = new RegAction(authCallWebClient, urlSiteAuth);
        Chat chat = new Chat();
        chat.setId(1L);
        message = new Message();
        message.setChat(chat);
    }

    @Test
    void whenHandleThenReturnEmailPrompt() {
        BotApiMethod<Message> result = regAction.handle(message);

        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getChatId()).isEqualTo("1");
        assertThat(sendMessage.getText()).isEqualTo("Введите email для регистрации:");
    }

    @Test
    void whenCallbackWithInvalidEmailThenReturnErrorMessage() {
        message.setText("invalid-email");

        BotApiMethod<Message> result = regAction.callback(message);

        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getChatId()).isEqualTo("1");
        assertThat(sendMessage.getText()).contains("не корректный");
        assertThat(sendMessage.getText()).contains("/new");
    }

    @Test
    void whenCallbackWithValidEmailAndSuccessRegistrationThenReturnSuccessMessage() {
        message.setText("test@example.com");

        when(authCallWebClient.doPost(eq("/registration"), any()))
                .thenReturn(Mono.just(Map.of("person", Map.of())));

        BotApiMethod<Message> result = regAction.callback(message);

        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getChatId()).isEqualTo("1");
        assertThat(sendMessage.getText()).contains("Вы зарегистрированы:");
        assertThat(sendMessage.getText()).contains("test@example.com");
        assertThat(sendMessage.getText()).contains(urlSiteAuth);
    }

    @Test
    void whenAuthServiceUnavailableThenReturnServiceUnavailableMessage() {
        message.setText("test@example.com");

        when(authCallWebClient.doPost(eq("/registration"), any()))
                .thenReturn(Mono.error(new Throwable("Error")));

        BotApiMethod<Message> result = regAction.callback(message);

        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getChatId()).isEqualTo("1");
        assertThat(sendMessage.getText()).contains("Сервис не доступен попробуйте позже");
        assertThat(sendMessage.getText()).contains("/start");
    }

    @Test
    void whenRegistrationFailsWithErrorThenReturnErrorMessage() {
        message.setText("test@example.com");
        String errorMessage = "Пользователь с почтой test@example.com уже существует.";

        when(authCallWebClient.doPost(eq("/registration"), any()))
                .thenReturn(Mono.just(Map.of("error", errorMessage)));

        BotApiMethod<Message> result = regAction.callback(message);

        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getChatId()).isEqualTo("1");
        assertThat(sendMessage.getText()).contains("Ошибка регистрации:");
        assertThat(sendMessage.getText()).contains(errorMessage);
    }
}