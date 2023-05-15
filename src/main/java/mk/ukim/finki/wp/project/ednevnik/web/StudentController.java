package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
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
    public String showStudentAdd(Model model) {
        String description = "Add student";
        model.addAttribute("description", description);
        return "student-add";
    }

    @GetMapping("/{id}/edit")
    public String showStudentEdit(@PathVariable Long id, Model model) {
        String description = "Edit student";
        Student student = studentService.findById(id);
        model.addAttribute("description", description);
        model.addAttribute(student);
        return "student-add";
    }

    @PostMapping
    public String studentAdd(@RequestParam(required = false) Long id,
                             @RequestParam String name,
                             @RequestParam String surname) {
        if (id != null) {
            studentService.update(id, name, surname);
        } else {
            try {
                studentService.create(name, surname);
            } catch (NameOrSurnameFieldIsEmptyException e) {
                throw new RuntimeException(e);
            }
        }
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
