package takeABreak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import takeABreak.service.InitService;

@SpringBootApplication
public class TakeABreakApplication {

    public static void main(String[] args) {

        SpringApplication.run(TakeABreakApplication.class, args);

    }

}
