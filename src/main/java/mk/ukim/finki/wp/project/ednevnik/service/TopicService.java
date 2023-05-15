package mk.ukim.finki.wp.project.ednevnik.service;

import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.NameOrSurnameFieldIsEmptyException;

import java.util.List;

public interface TopicService {
    List<Topic> findAll();

    Topic findById(Long id);

    Topic create(TopicCategory categoryName, String subCategoryName, String description, String serialNumber, Boolean isAccepted, String discussion, Long nnsMeetingId, String studentName, String studentSurname, Long professorId, List<Long> professorIds) throws NameOrSurnameFieldIsEmptyException;

    Topic update(Long id, TopicCategory categoryName, String subCategoryName, String description, String serialNumber, Boolean isAccepted, String discussion, Long nnsMeetingId, String studentName, String studentSurname, Long professorId, List<Long> professorIds) throws NameOrSurnameFieldIsEmptyException;

    Topic remove(Long id);
}
