package mk.ukim.finki.wp.project.ednevnik.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class NNSMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serialCode;
    private LocalDate date;

    @OneToMany(mappedBy = "nnsMeeting")
    private List<Topic> topics;

    public NNSMeeting(String serialCode, LocalDate date) {
        this.serialCode = serialCode;
        this.date = date;
    }

    public NNSMeeting() {
    }
}
