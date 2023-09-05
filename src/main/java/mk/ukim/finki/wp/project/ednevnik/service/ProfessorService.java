package mk.ukim.finki.wp.project.ednevnik.service;


import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;

import java.util.List;

public interface ProfessorService {
    List<Professor> findAll();

    Professor findById(Long id);

    Professor create(String name, String surname, ProfessorRole professorRole);

    Professor update(Long id, String name, String surname, ProfessorRole professorRole);

    Professor remove(Long id);

    List<Topic> topicsForThisProfSortedByTheirNNSMeetingDate(Professor chosenProf);

    List<Topic> topicsForThisProfessorFilteredBySpecs(Professor chosenProf, String categoryName, String subCategoryName, String studentFullNameId) throws StudentFormatException;
}
