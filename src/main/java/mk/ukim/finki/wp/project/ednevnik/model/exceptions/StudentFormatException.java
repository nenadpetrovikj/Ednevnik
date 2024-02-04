package mk.ukim.finki.wp.project.ednevnik.model.exceptions;

public class StudentFormatException extends RuntimeException {

    public StudentFormatException() {
        super("Форматот за внесување на студент е грешен! Треба да се внесат името, презимето и индексот, одвоени со празно место!");
    }
}
