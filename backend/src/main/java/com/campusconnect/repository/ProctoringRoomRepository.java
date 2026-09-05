package com.campusconnect.repository;

import com.campusconnect.model.ProctoringRoom;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ProctoringRoomRepository {
    private final Map<String, ProctoringRoom> rooms = new ConcurrentHashMap<>();

    public ProctoringRoom save(ProctoringRoom room) {
        rooms.put(room.getId(), room);
        return room;
    }

    public Optional<ProctoringRoom> findById(String id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public void deleteById(String id) {
        rooms.remove(id);
    }

    public boolean existsById(String id) {
        return rooms.containsKey(id);
    }

    public List<ProctoringRoom> findByExamSlotId(String examSlotId) {
        return rooms.values().stream()
                .filter(room -> room.getExamSlotId().equals(examSlotId))
                .collect(Collectors.toList());
    }

    public Collection<ProctoringRoom> findAll() {
        return rooms.values();
    }
}
