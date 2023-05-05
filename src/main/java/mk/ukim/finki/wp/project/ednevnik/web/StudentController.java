package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.InvalidIdException;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.NameOrSurnameFieldIsEmptyException;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping
    public String showStudentList(Model model) {
        List<Student> students = studentService.findAll();
        model.addAttribute("students", students);
        return "students-page";
    }

    @GetMapping("/add")
    public String showStudentAdd() {
        return "student-add";
    }

    @GetMapping("/{id}/edit")
    public String showStudentEdit(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);

        model.addAttribute(student);
        return "student-edit";
    }

    @PostMapping
    public String studentAdd(@RequestParam String name,
                             @RequestParam String surname) {
        try {
            studentService.create(name, surname);
        } catch (NameOrSurnameFieldIsEmptyException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/students";
    }

    @PostMapping("/{id}")
    public String studentEdit(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String surname) {
        studentService.update(id, name, surname);
        return "redirect:/students";
    }

//    TODO: causes error due to the relation to topic
//    TODO: possible solutions: notify user to delete related topic first or it student just can't be deleted
//    @PostMapping("/students/{id}/delete")
//    public String studentDelete(@PathVariable Long id) {
//        try {
//            studentService.remove(id);
//        } catch (InvalidIdException e) {
//            throw new RuntimeException(e);
//        }
//        return "redirect:/students";
//    }

}
