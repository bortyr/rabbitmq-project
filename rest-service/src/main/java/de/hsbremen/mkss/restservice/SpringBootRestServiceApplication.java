package de.hsbremen.mkss.restservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootRestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestServiceApplication.class, args);
	}

	// TUTAJ NIBY MOZNA BY BYLO COS WSTAWIC DO TEJ NASZEJ BAZY DANYCH ALE NIE WIEM DO KONCA JAK
	/*
	@Bean
	CommandLineRunner commandLineRunner(oorderRepository repo){
		return args -> {
			repo.save(new oorder(1,"10-10-2022", "dupa"));
		};
	}
	 */

}



