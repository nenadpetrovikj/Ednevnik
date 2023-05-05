package mk.ukim.finki.wp.project.ednevnik.repository;

import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
