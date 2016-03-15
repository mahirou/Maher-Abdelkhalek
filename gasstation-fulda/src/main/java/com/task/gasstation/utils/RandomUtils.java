package com.task.gasstation.utils;

import java.util.Random;

/**
 * This class contains all the constants of the application as well as custom
 * random number generator methods.
 *
 * @author Maher Abdelkhalek
 */
public class RandomUtils {

	/**
	 * The number of pumps in Fulda gas station.
	 * 
	 */
	public static final int GAS_PUMPS_NUMBER = 3;

	/**
	 * The initial quantity of Diesel gas contained in the Diesel pump.
	 * 
	 */
	public static final double DIESEL_GAS_AMOUNT = 200.5;

	/**
	 * The initial quantity of Regular gas contained in the Regular pump.
	 * 
	 */
	public static final double REGULAR_GAS_AMOUNT = 500;

	/**
	 * The initial quantity of Super gas contained in the Super pump.
	 * 
	 */
	public static final double SUPER_GAS_AMOUNT = 80.3;

	/**
	 * The min random gas amount in Liter that a customer may request.
	 * 
	 */
	public static final double MIN_VALUE_GAS_RANGE = 10;

	/**
	 * The max random gas amount in Liter that a customer may request.
	 * 
	 */
	public static final double MAX_VALUE_GAS_RANGE = 50;

	/**
	 * The average price of one Liter of Diesel gas in Euro.
	 * 
	 */
	public static final double DIESEL_AVERAGE_PRICE_PER_LITER = 1.2;

	/**
	 * The average price of one Liter of Regular gas in Euro.
	 * 
	 */
	public static final double REGULAR_AVERAGE_PRICE_PER_LITER = 1.4;

	/**
	 * The average price of one Liter of Super gas in Euro.
	 * 
	 */
	public static final double SUPER_AVERAGE_PRICE_PER_LITER = 1.7;

	/**
	 * The max deviation that can have the price of one Liter of gas around its
	 * average price.
	 * 
	 */
	public static final double GAS_PRICE_STANDARD_DEVIATION = 0.5;

	/**
	 * The min random time period separating the incoming of two customers to
	 * the same pump.
	 * 
	 */
	public static final int CUSTOMER_INCOMING_MIN_TIME_PERIOD = 3000;

	/**
	 * The max random time period separating the incoming of two customers to
	 * the same pump.
	 */
	public static final int CUSTOMER_INCOMING_MAX_TIME_PERIOD = 7000;

	/**
	 * The min random time period separating two gas prices updates.
	 * 
	 */
	public static final int UPDATE_GAS_PRICE_MIN_TIME_PERIOD = 10000;

	/**
	 * The max random time period separating two gas prices updates.
	 * 
	 */
	public static final int UPDATE_GAS_PRICE_MAX_TIME_PERIOD = 20000;

	/**
	 * This method generates a random double number around a given value.
	 * 
	 * @param average
	 *            the given average value.
	 * @param standardDeviation
	 *            the max deviation of the generated number around the average.
	 * 
	 * @return A random number greater or less than the average with max
	 *         difference the deviation value.
	 */
	public static double generateAroundAverage(double average, double standardDeviation) {
		Random l_r = new Random();
		// Randomize difference
		double l_difference = l_r.nextDouble() * standardDeviation;
		// randomize sign
		boolean l_negative = l_r.nextBoolean();
		if (l_negative) {
			l_difference = -l_difference;
		}
		return Math.floor((average + l_difference) * 100) / 100;
	}

	/**
	 * This method generates a random double number between too given values.
	 * 
	 * @param minValue
	 *            the given min value.
	 * @param maxValue
	 *            the given max value.
	 * 
	 * @return a double value between minValue and maxValue.
	 */
	public static double generateFromRange(double minValue, double maxValue) {
		Random l_r = new Random();
		Double l_val = l_r.nextDouble() * (maxValue - minValue) + minValue;
		return Math.floor(l_val * 100) / 100;
	}

	/**
	 * This method generates a random int number between too given values.
	 * 
	 * @param minValue
	 *            the given min value.
	 * @param maxValue
	 *            the given max value.
	 * 
	 * @return an int value between minValue and maxValue.
	 */
	public static int generateFromRange(int minValue, int maxValue) {
		Random l_r = new Random();
		return l_r.nextInt(maxValue - minValue) + minValue;
	}
}