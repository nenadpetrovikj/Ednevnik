package mk.ukim.finki.wp.project.ednevnik.service.implementation;

import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;
import mk.ukim.finki.wp.project.ednevnik.repository.ProfessorRepository;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorServiceImplementation implements ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorServiceImplementation(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    @Override
    public Professor findById(Long id) {
        return professorRepository.findById(id).get();
    }

    @Override
    public Professor create(String name, String surname, ProfessorRole professorRole) {
        return professorRepository.save(new Professor(name, surname, professorRole));
    }

    @Override
    public Professor update(Long id, String name, String surname, ProfessorRole professorRole) {
        Professor professor = findById(id);

        professor.setName(name);
        professor.setSurname(surname);
        professor.setProfessorRole(professorRole);

        return professorRepository.save(professor);
    }

    @Override
    public Professor remove(Long id) {
        Professor professor = findById(id);
        professorRepository.delete(professor);
        return professor;
    }
}
