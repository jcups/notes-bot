package ru.jcups.notesbot.service;

import org.springframework.stereotype.Service;
import ru.jcups.notesbot.entity.Category;
import ru.jcups.notesbot.entity.Note;
import ru.jcups.notesbot.repo.NoteRepository;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note getNoteById(long noteId) {
        return noteRepository.findById(noteId).orElse(null);
    }

    public void addNoteToCategory(Note note, Category category) {
        note.setCategory(category);
        noteRepository.save(note);
    }

    public void deleteNoteById(long noteId) {
        if (noteRepository.existsById(noteId))
            noteRepository.findById(noteId).ifPresent(noteRepository::delete);
    }

    public void editNote(Note note) {
        noteRepository.save(note);
    }
}
