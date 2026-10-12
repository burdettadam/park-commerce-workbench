package dev.parkcommerce.catalog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"dev.parkcommerce.catalog","dev.parkcommerce.support"})
public class CatalogApplication { public static void main(String[] args){SpringApplication.run(CatalogApplication.class,args);} }
