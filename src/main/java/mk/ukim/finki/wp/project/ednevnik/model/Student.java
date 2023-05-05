package mk.ukim.finki.wp.project.ednevnik.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @OneToMany(mappedBy = "student")
    private List<Topic> topics;

    public Student(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Student() {
    }
}
