package mk.ukim.finki.wp.project.ednevnik.custom;

import org.springframework.stereotype.Component;

@Component
public class StringUtility {

    public int countOccurrences(String input, char target) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == target) {
                count++;
            }
        }
        return count;
    }

}
