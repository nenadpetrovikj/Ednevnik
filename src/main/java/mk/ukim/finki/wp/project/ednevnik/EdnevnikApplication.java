package mk.ukim.finki.wp.project.ednevnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class EdnevnikApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdnevnikApplication.class, args);
    }

}
