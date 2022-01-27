package ru.jcups.notesbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jcups.notesbot.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
