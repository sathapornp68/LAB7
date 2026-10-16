package com.example.lab5_1;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class for converting between Room NoteEntity and OOP Domain Note Models.
 */
public class NoteMapper {

    /**
     * Converts Domain Note model (TextNote or CheckListNote) into Room NoteEntity.
     */
    public static NoteEntity toEntity(Note note) {
        if (note == null) return null;

        String userName = note.getUser() != null ? note.getUser().getName() : "";
        String userEmail = note.getUser() != null ? note.getUser().getEmail() : "";
        String noteType = note.getNoteType();
        String content = "";

        if (note instanceof TextNote) {
            content = ((TextNote) note).getTextContent();
        } else if (note instanceof CheckListNote) {
            content = Converters.fromCheckListItems(((CheckListNote) note).getItems());
        }

        return new NoteEntity(
                note.getTitle(),
                userName,
                userEmail,
                noteType,
                content,
                note.getCreatedAt()
        );
    }

    /**
     * Converts Room NoteEntity into Domain Note model (TextNote or CheckListNote).
     */
    public static Note toDomainModel(NoteEntity entity) {
        if (entity == null) return null;

        User user = new User(entity.getUserName(), entity.getUserEmail());

        if ("CheckListNote".equals(entity.getNoteType())) {
            List<CheckListItem> items = Converters.toCheckListItems(entity.getContent());
            return new CheckListNote(entity.getTitle(), user, items);
        } else {
            return new TextNote(entity.getTitle(), user, entity.getContent());
        }
    }

    /**
     * Converts list of NoteEntity to list of Domain Note models.
     */
    public static List<Note> toDomainModelList(List<NoteEntity> entities) {
        List<Note> notes = new ArrayList<>();
        if (entities != null) {
            for (NoteEntity entity : entities) {
                Note note = toDomainModel(entity);
                if (note != null) {
                    notes.add(note);
                }
            }
        }
        return notes;
    }
}
