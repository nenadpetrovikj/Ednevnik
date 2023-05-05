package mk.ukim.finki.wp.project.ednevnik.model;

import lombok.Data;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @Enumerated(EnumType.STRING)
    private ProfessorRole professorRole;

    @OneToMany(mappedBy = "professor")
    private List<Topic> topics;

    @ManyToMany(mappedBy = "professors")
    private List<Topic> topicsWithMultipleProfs;

    public Professor(String name, String surname, ProfessorRole professorRole) {
        this.name = name;
        this.surname = surname;
        this.professorRole = professorRole;
    }

    public Professor() {
    }
}
