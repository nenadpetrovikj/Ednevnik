package mk.ukim.finki.wp.project.ednevnik.service.implementation;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.repository.NNSMeetingRepository;
import mk.ukim.finki.wp.project.ednevnik.service.NNSMeetingService;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class NNSMeetingServiceImplementation implements NNSMeetingService {

    private final NNSMeetingRepository nnsMeetingRepository;

    public NNSMeetingServiceImplementation(NNSMeetingRepository nnsMeetingRepository) {
        this.nnsMeetingRepository = nnsMeetingRepository;
    }

    @Override
    public List<NNSMeeting> findAllSortedByDateDesc() {
        return nnsMeetingRepository.findAllByOrderByDateDesc();
    }

    @Override
    public Page<NNSMeeting> findAllWithPagination(Pageable pageable) {
        return nnsMeetingRepository.findAllByOrderByDateDesc(pageable);
    }

    @Override
    public List<NNSMeeting> findAllHeldBeforeSelectedDateDesc(LocalDate date) {
        List<NNSMeeting> filteredList = findAllSortedByDateDesc();

        filteredList = filteredList.stream()
                .filter(nnsMeeting -> nnsMeeting.getDate().isBefore(date) || nnsMeeting.getDate().isEqual(date)).toList();

        return filteredList;
    }

    @Override
    public NNSMeeting findById(Long id) {
        return nnsMeetingRepository.findById(id).orElseGet(() -> null);
    }

    @Override
    public NNSMeeting create(String serialCode, LocalDate date) {
        return nnsMeetingRepository.save(new NNSMeeting(serialCode, date));
    }

    @Override
    public NNSMeeting update(Long id, String serialCode, LocalDate date) {
        NNSMeeting nnsMeeting = findById(id);
        nnsMeeting.setSerialCode(serialCode);
        nnsMeeting.setDate(date);
        return nnsMeetingRepository.save(nnsMeeting);
    }

    @Override
    public NNSMeeting remove(Long id) {
        NNSMeeting nnsMeeting = findById(id);
        nnsMeetingRepository.delete(nnsMeeting);
        return nnsMeeting;
    }

    @Override
    public List<Topic> sortTopicsBySerialNumberForNNSMeeting(Long id) {
        NNSMeeting nnsMeeting = findById(id);

        List<Topic> topics = nnsMeeting.getTopics();
        topics.sort(Comparator.comparing(Topic::getSerialNumber));
        return topics;
    }
}
