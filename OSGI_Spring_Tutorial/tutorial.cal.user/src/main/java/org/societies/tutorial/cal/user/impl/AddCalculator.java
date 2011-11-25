package org.societies.tutorial.cal.user.impl;

import javax.annotation.PostConstruct;

import org.societies.tutorial.cal.service.api.ICalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddCalculator {
	
	private ICalculator calService;	
	 
	@Autowired
	public AddCalculator(ICalculator calService){
				this.calService=calService;
//				calService.setNumber(3, 8);		
//				System.out.println(" result of 3+8 = "+calService.getAddition());
	}
	
	@PostConstruct
	public void init(){
		calService.setNumber(3, 8);		
		System.out.println(" Add Result of 3+8 = "+calService.getAddition());
	}

}
