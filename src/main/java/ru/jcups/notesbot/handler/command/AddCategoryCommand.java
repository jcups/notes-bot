package ru.jcups.notesbot.handler.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.jcups.notesbot.model.BotState;
import ru.jcups.notesbot.service.UserService;

public class AddCategoryCommand extends BotCommand {

    private final UserService userService;

    public AddCategoryCommand(UserService userService) {
        super("/add_category", "Добавить категорию");
        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        userService.changeBotState(BotState.WAIT_CATEGORY_NAME, user.getId());
        try {
            absSender.execute(SendMessage.builder()
                    .chatId(chat.getId().toString())
                    .text("Введите название категории:").build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
