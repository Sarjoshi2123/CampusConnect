package com.campusconnect.model;

import java.util.Objects;

/**
 * Represents a certification exam conducted on behalf of a partner university.
 */
public class Exam {
    private final String id;
    private final String title;
    private final String partnerUniversity;

    public Exam(String id, String title, String partnerUniversity) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Exam ID cannot be null or empty.");
        }
        this.id = id;
        this.title = title;
        this.partnerUniversity = partnerUniversity;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPartnerUniversity() {
        return partnerUniversity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return Objects.equals(id, exam.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", partnerUniversity='" + partnerUniversity + '\'' +
                '}';
    }
}
