package dev.parkcommerce.pricing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"dev.parkcommerce.pricing","dev.parkcommerce.support"})
public class PricingApplication { public static void main(String[] args){SpringApplication.run(PricingApplication.class,args);} }
