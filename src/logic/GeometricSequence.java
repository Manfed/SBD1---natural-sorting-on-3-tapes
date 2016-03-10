package logic;

import config.Consts;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class GeometricSequence {
	
	private double firstTerm;
	private double multiplier;
	private double sum;
	
	//dane do wypisania w tabeli
	private final DoubleProperty firstTermProperty;
	private final DoubleProperty multiplierProperty;
	
	public GeometricSequence(double firstTerm, double multiplier)
	{
		this.firstTerm = firstTerm;
		this.multiplier = multiplier;
		this.sum = calculateSum(firstTerm, multiplier);
		
		this.multiplierProperty = new SimpleDoubleProperty(multiplier);
		this.firstTermProperty = new SimpleDoubleProperty(firstTerm);
	}
	
	public double getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}
	public double getFirstTerm() {
		return firstTerm;
	}
	public void setFirstTerm(double firstTerm) {
		this.firstTerm = firstTerm;
	}

	public double getSum() {
		return sum;
	}
	
	public DoubleProperty getFirstTermProperty() {
		return firstTermProperty;
	}

	public DoubleProperty getMultiplierProperty() {
		return multiplierProperty;
	}
	
	//obliczanie sumy ciagu geometrycznego
	private double calculateSum(double firstSeqTerm, double seqMultiplier) {
		double sequenceSum = 0;
		if(seqMultiplier != 1){
			sequenceSum = firstSeqTerm * (1 - Math.pow(seqMultiplier, Consts.SEQUENCE_ELEMENT_COUNT))
					/ (1 - seqMultiplier);
		}
		else {
			sequenceSum = Consts.SEQUENCE_ELEMENT_COUNT * firstSeqTerm;
		}
		return sequenceSum;
	}
}
