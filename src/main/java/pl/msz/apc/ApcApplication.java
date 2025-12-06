package pl.msz.apc;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.modulith.Modulithic;

@Modulithic
@SpringBootApplication
public class ApcApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(ApcApplication.class, args);
  }

  @PostConstruct
  public void setTimeZone() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}
