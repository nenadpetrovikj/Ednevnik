package mk.ukim.finki.wp.project.ednevnik.model;

import lombok.Data;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TopicCategory categoryName;

    private String subCategoryName; // type
    private String description;
    private String serialNumber;
    private Boolean isAccepted;
    private String discussion;

    @ManyToOne
    private NNSMeeting nnsMeeting;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Professor professor;

    @ManyToMany
    private List<Professor> professors;

    public Topic(TopicCategory categoryName, String subCategoryName, String description, String serialNumber, Boolean isAccepted, String discussion, NNSMeeting nnsMeeting, Student student, Professor professor, List<Professor> professors) {
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
        this.description = description;
        this.serialNumber = serialNumber;
        this.isAccepted = isAccepted;
        this.discussion = discussion;
        this.nnsMeeting = nnsMeeting;
        this.student = student;
        this.professor = professor;
        this.professors = professors;
    }

    public Topic() {
    }
}
