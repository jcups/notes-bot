package ru.jcups.notesbot.model;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.jcups.notesbot.client.TelegramClient;
import ru.jcups.notesbot.config.BotConfig;
import ru.jcups.notesbot.handler.CallbackHandler;
import ru.jcups.notesbot.handler.InlineQueryHandler;
import ru.jcups.notesbot.handler.MessageHandler;
import ru.jcups.notesbot.handler.command.*;
import ru.jcups.notesbot.service.CategoryService;
import ru.jcups.notesbot.service.UserService;


@Component
public class TelegramBot extends TelegramWebhookBot {

    private final BotConfig botConfig;
    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;
    private final InlineQueryHandler inlineQueryHandler;
    private final CommandRegistry commandRegistry;
    private final UserService userService;

    public TelegramBot(BotConfig botConfig, MessageHandler messageHandler,
                       CallbackHandler callbackHandler, InlineQueryHandler inlineQueryHandler,
                       TelegramClient telegramClient, UserService userService,
                       CategoryService categoryService) {
        this.botConfig = botConfig;
        this.messageHandler = messageHandler;
        this.callbackHandler = callbackHandler;
        this.inlineQueryHandler = inlineQueryHandler;
        this.userService = userService;
        if (telegramClient.setWebhook(getBotToken(), getBotPath()).getStatusCode() == HttpStatus.OK)
            System.out.println("successfully set webhookPath");

        this.commandRegistry = new CommandRegistry(true, this::getBotUsername);
        this.commandRegistry.register(new StartCommand());
        this.commandRegistry.register(new AddCategoryCommand(userService));
        this.commandRegistry.register(new MyCategoriesCommand(categoryService));
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                initUser(update.getMessage().getFrom().getId());
                System.out.println("update has message with text: " + update.getMessage().getText());
                if (update.getMessage().isCommand())
                    this.commandRegistry.executeCommand(this, update.getMessage());
                else
                    messageHandler.handle(update.getMessage(), this);
            } else if (update.hasCallbackQuery()) {
                initUser(update.getCallbackQuery().getFrom().getId());
                System.out.println("update has callbackQuery with data: " + update.getCallbackQuery().getData());
                callbackHandler.handle(update.getCallbackQuery(), this);
            } else if (update.hasInlineQuery()) {
                initUser(update.getInlineQuery().getFrom().getId());
                System.out.println("update has inlineQuery with query: " + update.getInlineQuery().getQuery());
                inlineQueryHandler.handle(update.getInlineQuery(), this);
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    public void initUser(long userId) {
        if (!userService.isExists(userId))
            userService.createNewUser(ru.jcups.notesbot.entity.User.builder()
                    .id(userId).botState(BotState.NOTHING.name()).build());
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotPath() {
        return botConfig.getPath();
    }
}
