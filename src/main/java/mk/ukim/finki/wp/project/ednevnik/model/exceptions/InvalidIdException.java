package mk.ukim.finki.wp.project.ednevnik.model.exceptions;

public class InvalidIdException extends Exception {
    public InvalidIdException(String mess, Long id) {
        super(mess + " with id: " + id + " does not exist.");
    }
}
