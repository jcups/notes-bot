package ru.jcups.notesbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jcups.notesbot.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
