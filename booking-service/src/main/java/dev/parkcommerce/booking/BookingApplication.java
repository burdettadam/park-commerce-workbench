package dev.parkcommerce.booking;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"dev.parkcommerce.booking","dev.parkcommerce.support"})
public class BookingApplication { public static void main(String[] args){SpringApplication.run(BookingApplication.class,args);} }
