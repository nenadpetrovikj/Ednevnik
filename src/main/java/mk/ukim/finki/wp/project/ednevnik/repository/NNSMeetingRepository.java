package mk.ukim.finki.wp.project.ednevnik.repository;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NNSMeetingRepository extends JpaRepository<NNSMeeting, Long> {
    List<NNSMeeting> findAllByOrderByDateDesc();
}
