package mk.ukim.finki.wp.project.ednevnik.service;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.NameOrSurnameFieldIsEmptyException;

import java.util.List;

public interface StudentService {
    List<Student> findAll();

    Student findById(Long id);

    Student create(String name, String surname) throws NameOrSurnameFieldIsEmptyException;

    Student update(Long id, String name, String surname);

    Student remove(Long id);
}
