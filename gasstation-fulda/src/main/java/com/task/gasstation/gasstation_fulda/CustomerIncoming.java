package com.task.gasstation.gasstation_fulda;

import static com.task.gasstation.utils.RandomUtils.CUSTOMER_INCOMING_MAX_TIME_PERIOD;
import static com.task.gasstation.utils.RandomUtils.CUSTOMER_INCOMING_MIN_TIME_PERIOD;
import static com.task.gasstation.utils.RandomUtils.DIESEL_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.GAS_PRICE_STANDARD_DEVIATION;
import static com.task.gasstation.utils.RandomUtils.MAX_VALUE_GAS_RANGE;
import static com.task.gasstation.utils.RandomUtils.MIN_VALUE_GAS_RANGE;
import static com.task.gasstation.utils.RandomUtils.REGULAR_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.SUPER_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.generateAroundAverage;
import static com.task.gasstation.utils.RandomUtils.generateFromRange;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

/**
 * This class represents the thread handling the customers incoming process of
 * one gas pump in question.
 * 
 * This class contains an inner thread class that handles customers gas buying
 * request.
 * 
 * @author Maher Abdelkhalek
 *
 */
public class CustomerIncoming extends Thread {

	/**
	 * Locker of the bye operation that grants access to the longest-waiting
	 * thread (with fairness policy), so that grants ordered customer service
	 * queue
	 */
	private Lock byeServiceLocker;

	/**
	 * The Fulda gas station object.
	 */
	private FuldaGasStation fGasStation;

	/**
	 * The gas type of the pump in question.
	 */
	private GasType type;

	/**
	 * The gas pump in question.
	 */
	private GasPump gasPump;

	/**
	 * 
	 * @param fGasStation
	 *            the Fulda gas station object.
	 * 
	 * @param type
	 *            the type of gas that the pump in question serves.
	 * 
	 * @param endService
	 *            the status of the service of the Fulda gas station.
	 */
	public CustomerIncoming(FuldaGasStation fGasStation, GasType type, boolean endService) {
		this.fGasStation = fGasStation;
		this.type = type;
		// Retrieving the gas pump for the given gas type.
		for (GasPump gp : fGasStation.getGasPumps()) {
			if (gp.getGasType().equals(type)) {
				gasPump = gp;
				break;
			}
		}
		// Initialization of the bye service locker with the fairness policy
		// option.
		byeServiceLocker = new ReentrantLock(true);
	}

	@Override
	// The run() method of the main thread class that generates customers
	// periodically.
	public void run() {

		// The amount of gas that the customer requests.
		double l_customerGasAmount = 0;
		// The price of gas Liter that the customer expects.
		double l_customerMaxPricePerLiter = 0;
		// The customer order.
		int l_customerIndex = 1;
		// The sale gas operations thread.
		SaleGasOperation customerResquest = null;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
		}
		System.out.println();
		System.out.println(type + " pump is in service...");
		// the customers incoming process still working until the gas station
		// ends the service.
		while (!GasStationServiceSimulator.stationServiceEnd) {
			// Generating customer gas amount randomly.
			l_customerGasAmount = generateFromRange(MIN_VALUE_GAS_RANGE, MAX_VALUE_GAS_RANGE);
			// Generating customer expected gas price randomly depending on the
			// gas type.
			switch (gasPump.getGasType()) {
			case DIESEL:
				l_customerMaxPricePerLiter = generateAroundAverage(DIESEL_AVERAGE_PRICE_PER_LITER,
						GAS_PRICE_STANDARD_DEVIATION);
				break;
			case REGULAR:
				l_customerMaxPricePerLiter = generateAroundAverage(REGULAR_AVERAGE_PRICE_PER_LITER,
						GAS_PRICE_STANDARD_DEVIATION);
				break;
			case SUPER:
				l_customerMaxPricePerLiter = generateAroundAverage(SUPER_AVERAGE_PRICE_PER_LITER,
						GAS_PRICE_STANDARD_DEVIATION);
				break;
			default:
				l_customerMaxPricePerLiter = generateAroundAverage(REGULAR_AVERAGE_PRICE_PER_LITER,
						GAS_PRICE_STANDARD_DEVIATION);
				break;
			}
			// Creating new customer request thread with the specific
			// requirements.
			customerResquest = new SaleGasOperation(l_customerGasAmount, l_customerMaxPricePerLiter, l_customerIndex);
			// Starting the customer request thread.
			customerResquest.start();
			// waiting for the next customer incoming until the sleep time is
			// reached.
			try {
				Thread.sleep(generateFromRange(CUSTOMER_INCOMING_MIN_TIME_PERIOD, CUSTOMER_INCOMING_MAX_TIME_PERIOD));
			} catch (InterruptedException e) {
			}
			// Passing to the next customer.
			l_customerIndex++;
		}
		try {
			// Waiting for the last "request customer" thread, representing the
			// last customer in
			// the pump in question, to end the "incoming customers" thread.
			customerResquest.join();
			System.out.println("End of service in " + type + " pump.");
		} catch (InterruptedException e) {
		}
	}

	/**
	 * This class represents the thread handling the customers requests process
	 * of one gas pump in question.
	 * 
	 * @author Maher Abdelkhalek
	 *
	 */
	class SaleGasOperation extends Thread {

		/**
		 * The amount of gas that the customer requested.
		 */
		double customerGasAmount;

		/**
		 * The price of gas Liter that the customer expected.
		 */
		double customerMaxPricePerLiter;

		/**
		 * The customer number.
		 */
		int customerIndex;

		/**
		 * @param customerGasAmount
		 * @param customerMaxPricePerLiter
		 * @param customerIndex
		 */
		public SaleGasOperation(double customerGasAmount, double customerMaxPricePerLiter, int customerIndex) {
			this.customerGasAmount = customerGasAmount;
			this.customerMaxPricePerLiter = customerMaxPricePerLiter;
			this.customerIndex = customerIndex;
		}

		@Override
		// The run() method of the inner thread class that handles the customers
		// requests.
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e2) {
			}
			System.out.println();
			System.out.println("Customer n°" + customerIndex + " comes requesting " + customerGasAmount + "L of " + type
					+ " gas for up to " + customerMaxPricePerLiter + " €/L.");
			// The customer tries to acquire the lock of the "bye service" to
			// bye gas.
			if (!byeServiceLocker.tryLock()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e2) {
				}
				System.out.println(
						"Customer " + customerIndex + " in " + type + " pump is waiting the previous customer...");
				// The customer still waiting until acquiring the lock.
				byeServiceLocker.lock();
			}
			try {
				// The customer acquires the lock.
				try {
					Thread.sleep(500);
				} catch (InterruptedException e2) {
				}
				System.out.println("Cheaking operation for customer n°" + customerIndex + " in " + type + " pump...");
				// Checking operation with the customer request.
				fGasStation.buyGas(type, customerGasAmount, customerMaxPricePerLiter);
				System.out.println();
				System.out.println(type + " pump: Customer " + customerIndex + " is served.");
				// Sale operation succeeded with the customer.
				try {
					Thread.sleep(500);
				} catch (InterruptedException e2) {
				}
				System.out
						.println(type + " pump: After serving cutomer " + customerIndex + ", The Remaining quantity of "
								+ type + " gas is " + String.format("%.2f", gasPump.getRemainingAmount()) + " L");
			}
			// Sale operation failed with the customer.
			catch (NotEnoughGasException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
				System.out.println("The available " + type + " gas quantity does not cover the customer "
						+ customerIndex + " request (Available:" + String.format("%.2f", gasPump.getRemainingAmount())
						+ "L - Expected:" + customerGasAmount + "L).");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
				}
				System.out.println(type + " pump: Customer " + customerIndex + " left without being served.");
			}
			// Sale operation failed with the customer.
			catch (GasTooExpensiveException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
				System.out.println("The price of " + type + " is actually more than the customer " + customerIndex
						+ " expected (Available:" + fGasStation.getPrice(type) + "€/L - Expected:"
						+ customerMaxPricePerLiter + "€/L).");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
				}
				System.out.println(type + " pump: Customer " + customerIndex + " left without being served.");
			} finally {
				System.out.println(
						"[Options: 2 End Station service | 3 Operations Status | 4 Tanks Status | 5 Prices List Status]:");
				System.out.println();
				// The customer release the lock.
				byeServiceLocker.unlock();
			}
		}
	}
}