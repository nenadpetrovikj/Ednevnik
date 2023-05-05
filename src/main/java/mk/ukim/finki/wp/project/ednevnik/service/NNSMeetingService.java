package mk.ukim.finki.wp.project.ednevnik.service;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;

import java.time.LocalDate;
import java.util.List;

public interface NNSMeetingService {
    List<NNSMeeting> findAllSortedByDateDesc();

    NNSMeeting findById(Long id);

    NNSMeeting create(String serialCode, LocalDate date);

    NNSMeeting update(Long id, String serialCode, LocalDate date);

    NNSMeeting remove(Long id);
}
