package mk.ukim.finki.wp.project.ednevnik.repository;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NNSMeetingRepository extends JpaRepository<NNSMeeting, Long> {
    List<NNSMeeting> findAllByOrderByDateDesc();

    Page<NNSMeeting> findAllByOrderByDateDesc(Pageable pageable);


}
