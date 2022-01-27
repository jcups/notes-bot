package ru.jcups.notesbot.handler.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.jcups.notesbot.entity.Category;
import ru.jcups.notesbot.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

public class MyCategoriesCommand extends BotCommand {

    private final CategoryService categoryService;

    public MyCategoriesCommand(CategoryService categoryService) {
        super("/my_categories","Мои категории");
        this.categoryService = categoryService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        List<Category> categories = categoryService.getCategoriesByUserId(user.getId());
        try {
            absSender.execute(SendMessage.builder()
                    .chatId(chat.getId().toString())
                    .text(categories.size() > 0 ?
                            "Выберите категорию:" :
                            "У вас нет добавленных категорий")
                    .replyMarkup(getCategoriesKeyboard(categories))
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getCategoriesKeyboard(List<Category> categories) {
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for (Category category : categories) {
            keyboardButtons.add(List.of(InlineKeyboardButton.builder()
                    .text(category.getName())
                    .callbackData("category show " + category.getId()).build()));
        }
        return InlineKeyboardMarkup.builder().keyboard(keyboardButtons).build();
    }
}
