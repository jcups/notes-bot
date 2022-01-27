package ru.jcups.notesbot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.jcups.notesbot.entity.Category;
import ru.jcups.notesbot.entity.Note;
import ru.jcups.notesbot.model.BotState;
import ru.jcups.notesbot.service.CategoryService;
import ru.jcups.notesbot.service.NoteService;
import ru.jcups.notesbot.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageHandler {

    private final UserService userService;
    private final CategoryService categoryService;
    private final NoteService noteService;

    public MessageHandler(UserService userService, CategoryService categoryService, NoteService noteService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.noteService = noteService;
    }

    public void handle(Message message, AbsSender sender) {
        long userId = message.getFrom().getId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(userId));
        BotState botState = userService.getBotStateById(userId);
        try {
            switch (botState) {
                case WAIT_CATEGORY_NAME:
                    categoryService.addCategoryToUser(Category.builder()
                                    .name(message.getText()).build(),
                            userService.getUserById(userId));
                    userService.changeBotState(BotState.NOTHING, userId);
                    sendMessage.setText("Успешно создано");
                    break;
                case WAIT_NAME_FOR_CATEGORY_RENAME:
                    long categoryId = Long.parseLong(userService.getActionById(userId).split(" ")[2]);
                    Category category = categoryService.getCategoryById(categoryId);
                    categoryService.renameCategory(category.getId(), message.getText());
                    sendMessage.setText("Успешно переименованно");
                    userService.changeBotState(BotState.NOTHING, userId);
                    userService.setActionById("", userId);
                    break;
                case WAIT_NEW_CONTENT_FOR_NOTE:
                    long noteId = Long.parseLong(userService.getActionById(userId).split(" ")[2]);
                    Note note = noteService.getNoteById(noteId);
                    note.setContent(message.getText());
                    noteService.editNote(note);
                    sendMessage.setText("Успешно изменено");
                    userService.changeBotState(BotState.NOTHING, userId);
                    userService.setActionById("", userId);
                    break;
                case NOTHING:
                    sendMessage.setText("Выберите категорию в которую хотите добавить заметку:");
                    sendMessage.setReplyMarkup(getCategoriesKeyboard(
                            categoryService.getCategoriesByUserId(userId)));
                    sendMessage.setReplyToMessageId(message.getMessageId());
                    break;
            }
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getCategoriesKeyboard(List<Category> categories) {
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for (Category category : categories) {
            keyboardButtons.add(List.of(InlineKeyboardButton.builder()
                    .text(category.getName())
                    .callbackData("note add " + category.getId())
                    .build()));
        }
        return InlineKeyboardMarkup.builder().keyboard(keyboardButtons).build();
    }
}
