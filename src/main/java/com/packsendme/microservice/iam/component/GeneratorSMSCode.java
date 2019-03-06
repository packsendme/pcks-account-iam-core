package com.packsendme.microservice.iam.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class GeneratorSMSCode {
	
	public String generateSMSCode() {
		String codSMS = "";
		// Metemos en una lista los números del 1 al 40.
		List<Integer> numbers = new ArrayList<>(40);
		for (int i=1;i<10;i++){
		   numbers.add(i);
		}
		Random random = new Random();
		for(int n = 1; n<=4;n++) {
			  int randomIndex = random.nextInt(numbers.size());
			  codSMS = randomIndex + codSMS;
			  numbers.remove(randomIndex);
		}
		return codSMS;
	}

}
