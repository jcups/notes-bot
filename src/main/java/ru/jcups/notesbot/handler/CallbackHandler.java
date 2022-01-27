package ru.jcups.notesbot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.jcups.notesbot.entity.Category;
import ru.jcups.notesbot.entity.Note;
import ru.jcups.notesbot.model.BotState;
import ru.jcups.notesbot.model.TelegramBot;
import ru.jcups.notesbot.service.CategoryService;
import ru.jcups.notesbot.service.NoteService;
import ru.jcups.notesbot.service.UserService;

import java.util.LinkedList;
import java.util.List;

@Component
public class CallbackHandler {

    private final CategoryService categoryService;
    private final NoteService noteService;
    private final UserService userService;

    public CallbackHandler(CategoryService categoryService, NoteService noteService, UserService userService) {
        this.categoryService = categoryService;
        this.noteService = noteService;
        this.userService = userService;
    }

    public void handle(CallbackQuery callbackQuery, TelegramBot telegramBot) {
        String query = callbackQuery.getData();
        String chatId = callbackQuery.getFrom().getId().toString();
        try {
            if (query.startsWith("category")) {
                String method = query.split(" ")[1];
                long categoryId = Long.parseLong(query.split(" ")[2]);
                switch (method) {
                    case "delete":
                        categoryService.deleteCategory(categoryId);
                        if (!categoryService.isExists(categoryId))
                            telegramBot.execute(SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Успешно удалено").build());
                        else
                            telegramBot.execute(SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Произошла ошибка при удалении категории").build());
                        break;
                    case "rename":
                        telegramBot.execute(SendMessage.builder()
                                .chatId(chatId)
                                .text("Введите новое имя для категории (" +
                                        categoryService.getCategoryById(categoryId).getName() + ")").build());
                        userService.changeBotState(BotState.WAIT_NAME_FOR_CATEGORY_RENAME, callbackQuery.getFrom().getId());
                        userService.setActionById(query, callbackQuery.getFrom().getId());
                        break;
                    case "show":
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        Category category = categoryService.getCategoryById(categoryId);
                        List<List<InlineKeyboardButton>> buttons = new LinkedList<>();
                        if (category.getNotes() != null && !category.getNotes().isEmpty()) {
                            sendMessage.setText(category.getName() + ":");
                            for (Note note : category.getNotes()) {
                                buttons.add(List.of(InlineKeyboardButton.builder()
                                        .text(note.getContent())
                                        .callbackData("note change " + note.getId()).build()));
                            }
                        } else {
                            sendMessage.setText(category.getName() + " - не содержит заметок");
                        }
                        buttons.add(List.of(
                                InlineKeyboardButton.builder().text("Переименовать")
                                        .callbackData("category rename " + category.getId()).build(),
                                InlineKeyboardButton.builder().text("Удалить")
                                        .callbackData("category delete " + category.getId()).build()));
                        sendMessage.setReplyMarkup(
                                InlineKeyboardMarkup.builder().keyboard(buttons).build());
                        telegramBot.execute(sendMessage);
                        break;
                }
            } else if (query.startsWith("note")) {
                String method = query.split(" ")[1];
                long id = Long.parseLong(query.split(" ")[2]);
                switch (method) {
                    case "add":
                        String content = callbackQuery.getMessage().getReplyToMessage().getText();
                        noteService.addNoteToCategory(Note.builder()
                                .content(content).build(), categoryService.getCategoryById(id));
                        telegramBot.execute(SendMessage.builder()
                                .chatId(chatId).text("Успешно добавлено").build());
                        break;
                    case "change":
                        Note note = noteService.getNoteById(id);
                        telegramBot.execute(SendMessage.builder()
                                .chatId(chatId)
                                .text(note.getContent())
                                .replyMarkup(InlineKeyboardMarkup.builder()
                                        .keyboardRow(List.of(
                                                InlineKeyboardButton.builder()
                                                        .text("Изменить")
                                                        .callbackData("note edit " + note.getId()).build(),
                                                InlineKeyboardButton.builder()
                                                        .text("Удалить")
                                                        .callbackData("note delete " + note.getId()).build()
                                        )).build())
                                .build());
                        break;
                    case "edit":
                        telegramBot.execute(SendMessage.builder()
                                .chatId(chatId).text("Введите новое содержимое: ")
                                .build());
                        userService.setActionById(query, callbackQuery.getFrom().getId());
                        userService.changeBotState(BotState.WAIT_NEW_CONTENT_FOR_NOTE, callbackQuery.getFrom().getId());
                        break;
                    case "delete":
                        noteService.deleteNoteById(id);
                        telegramBot.execute(SendMessage.builder()
                                .chatId(chatId).text("Успешно удалено").build());
                        break;
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
