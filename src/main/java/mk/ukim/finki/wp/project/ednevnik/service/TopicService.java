package mk.ukim.finki.wp.project.ednevnik.service;

import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;

import java.util.List;
import java.util.Set;

public interface TopicService {
    List<Topic> findAll();

    Topic findById(Long id);

    Topic create(TopicCategory categoryName, String subCategoryName, String description, String serialNumber, Boolean isAccepted, String discussion, Long nnsMeetingId, String studentFullNameId, Long professorId, List<Long> professorIds) throws StudentFormatException;

    Topic update(Long id, TopicCategory categoryName, String subCategoryName, String description, String serialNumber, Boolean isAccepted, String discussion, Long nnsMeetingId, String studentFullNameId, Long professorId, List<Long> professorIds) throws StudentFormatException;

    Set<String> getAllSubCategories();

    Topic remove(Long id);
}
