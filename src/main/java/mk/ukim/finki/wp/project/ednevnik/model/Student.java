package mk.ukim.finki.wp.project.ednevnik.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Student {

    @Id
    private Long id; // indeks

    private String name;
    private String surname;

    @OneToMany(mappedBy = "student")
    private List<Topic> topics = new ArrayList<>();

    public Student(String name, String surname, Long id) {
        this.name = name;
        this.surname = surname;
        this.id = id;
    }

    public Student() {
    }
}
