package example.jbot.slack;

// Main
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "example.jbot"})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}