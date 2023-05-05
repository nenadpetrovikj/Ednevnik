package mk.ukim.finki.wp.project.ednevnik.model.exceptions;

public class NameOrSurnameFieldIsEmptyException extends Exception {

    public NameOrSurnameFieldIsEmptyException() {
        super("Field for name or surname is left blank. Please fill both fields or leave both empty!");
    }
}
