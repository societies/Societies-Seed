package org.societies.tutorial.cal.service.impl;

import org.societies.tutorial.cal.service.api.ICalculator;

public class CalculatorImpl implements ICalculator {

	private int numa;
	private int numb;
	
	public CalculatorImpl(){
		System.out.println(" cal bean started");
	}
	
	
	@Override
	public void setNumber(int a, int b) {
		this.numa=a;
		this.numb=b;

	}

	@Override
	public int getAddition() {
		// TODO Auto-generated method stub
		return numa+numb;
	}

}
