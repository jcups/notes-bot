package ru.jcups.notesbot.service;

import org.springframework.stereotype.Service;
import ru.jcups.notesbot.entity.User;
import ru.jcups.notesbot.model.BotState;
import ru.jcups.notesbot.repo.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setActionById(String action, long userId) {
        User user = getUserById(userId);
        user.setAction(action);
        userRepository.save(user);
    }

    public String getActionById(long userId) {
        return getUserById(userId).getAction();
    }

    public boolean isExists(long userId) {
        return userRepository.existsById(userId);
    }

    public void changeBotState(BotState botState, long userId) {
        User user = getUserById(userId);
        user.setBotState(botState.name());
        userRepository.save(user);
    }

    public BotState getBotStateById(long userId) {
        User user = getUserById(userId);
        return BotState.valueOf(user.getBotState());
    }

    public void createNewUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
