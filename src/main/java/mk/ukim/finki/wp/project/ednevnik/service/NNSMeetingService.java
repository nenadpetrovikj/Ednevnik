package mk.ukim.finki.wp.project.ednevnik.service;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface NNSMeetingService {
    List<NNSMeeting> findAllSortedByDateDesc();
    Page<NNSMeeting> findAllWithPagination(Pageable pageable);

    List<NNSMeeting> findAllHeldBeforeSelectedDateDesc(LocalDate date);

    NNSMeeting findById(Long id);

    NNSMeeting create(String serialCode, LocalDate date);

    NNSMeeting update(Long id, String serialCode, LocalDate date);

    NNSMeeting remove(Long id);

    List<Topic> sortTopicsBySerialNumberForNNSMeeting(Long id);
}
