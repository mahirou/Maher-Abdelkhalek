package com.task.gasstation.gasstation_fulda;

import static com.task.gasstation.utils.RandomUtils.GAS_PUMPS_NUMBER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

/**
 * This class is an implementation of a the gas station interface.
 * 
 * This class is thread-safe by using several locker objects to optimize
 * performance.
 * 
 * @author Maher Abdelkhalek
 * 
 */
public class FuldaGasStation implements GasStation {

	/**
	 * The object locker of the gasPumpList objects.
	 */
	private Map<GasType, Object> gasPumpLocksList = new HashMap<GasType, Object>(3);

	/**
	 * The object locker of the revenue field.
	 */
	private Object revenueLock = new Object();

	/**
	 * The object locker of the numberOfSales field.
	 */
	private Object numberOfSalesLock = new Object();

	/**
	 * The object locker of the numberOfCancellationsNoGas field.
	 */
	private Object numberOfCancellationsNoGasLock = new Object();

	/**
	 * The object locker of the numberOfCancellationsTooExpensive field.
	 */
	private Object numberOfCancellationsTooExpensiveLock = new Object();

	/**
	 * The object locker of the gasPricesList objects.
	 */
	private Object gasPricesListLock = new Object();

	/**
	 * List of gas pumps of the Fulda station.
	 */
	private List<GasPump> gasPumpsList;

	/**
	 * The total revenue of the Fulda gas station in Euro.
	 */
	private volatile double revenue;

	/**
	 * The total number of sales of the Fulda gas station.
	 */
	private volatile int numberOfSales;

	/**
	 * The cancelled bye gas operations due to lack of gas availability.
	 */
	private volatile int numberOfCancellationsNoGas;

	/**
	 * The cancelled bye gas operations due to expensive gas price.
	 */
	private volatile int numberOfCancellationsTooExpensive;

	/**
	 * The gas prices list of one Liter in Euro for each kind of gas.
	 */
	private volatile Map<GasType, Double> gasPricesList;

	/**
	 * Constructor of the class.
	 */
	public FuldaGasStation() {
		gasPumpsList = new ArrayList<GasPump>(GAS_PUMPS_NUMBER);
		gasPricesList = new Hashtable<GasType, Double>(GAS_PUMPS_NUMBER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.bigpoint.assessment.gasstation.GasStation#addGasPump(net.bigpoint.
	 * assessment.gasstation.GasPump)
	 */
	public synchronized void addGasPump(GasPump pump) {
		gasPumpsList.add(pump);
		gasPumpLocksList.put(pump.getGasType(), new Object());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#getGasPumps()
	 */
	public synchronized List<GasPump> getGasPumps() {
		return gasPumpsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#buyGas(net.bigpoint.
	 * assessment.gasstation.GasType, double, double)
	 */
	public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
			throws NotEnoughGasException, GasTooExpensiveException {

		GasPump l_gasPump = null;
		double l_priceToPayByCustomer = 0;
		double l_unitPrice;
		// Retrieving the gas pump for the given gas type.
		for (GasPump gp : gasPumpsList) {
			if (gp.getGasType().equals(type)) {
				l_gasPump = gp;
				break;
			}
		}
		// Locking the access of each thread wanting to acquire the pump object
		// in question.
		synchronized (gasPumpLocksList.get(type)) {
			// Check of gas amount availability
			if (l_gasPump.getRemainingAmount() >= amountInLiters) {
				// Locking the access to the gasPricesList
				synchronized (gasPricesListLock) {
					l_unitPrice = getPrice(l_gasPump.getGasType());
				}
				// Check of customer gas pricing expectation.
				if (l_unitPrice <= maxPricePerLiter) {
					System.out
							.println(l_gasPump.getGasType() + " pump: Pumping " + amountInLiters + "L in progress...");
					// Launch gas pumping operation.
					l_gasPump.pumpGas(amountInLiters);
					// Calculating the amount that the current customer has to
					// pay.
					l_priceToPayByCustomer = l_unitPrice * amountInLiters;
					// Locking the access to numberOfSales variable.
					synchronized (numberOfSalesLock) {
						numberOfSales++;
					}
					// Locking the access to revenue variable.
					synchronized (revenueLock) {
						revenue += l_priceToPayByCustomer;
					}
				} else {
					// Locking the access to numberOfCancellationsTooExpensive
					// variable.
					synchronized (numberOfCancellationsTooExpensiveLock) {
						numberOfCancellationsTooExpensive++;
					}
					throw new GasTooExpensiveException();
				}
			} else {
				// Locking the access to numberOfCancellationsNoGas variable.
				synchronized (numberOfCancellationsNoGasLock) {
					numberOfCancellationsNoGas++;
				}
				throw new NotEnoughGasException();
			}
		}
		return l_priceToPayByCustomer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#getRevenue()
	 */
	public double getRevenue() {
		synchronized (revenueLock) {
			return revenue;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#getNumberOfSales()
	 */
	public int getNumberOfSales() {
		synchronized (numberOfSalesLock) {
			return numberOfSales;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#
	 * getNumberOfCancellationsNoGas()
	 */
	public int getNumberOfCancellationsNoGas() {
		synchronized (numberOfCancellationsNoGasLock) {
			return numberOfCancellationsNoGas;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#
	 * getNumberOfCancellationsTooExpensive()
	 */
	public int getNumberOfCancellationsTooExpensive() {
		synchronized (numberOfCancellationsTooExpensiveLock) {
			return numberOfCancellationsTooExpensive;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#getPrice(net.bigpoint.
	 * assessment.gasstation.GasType)
	 */
	public double getPrice(GasType type) {
		synchronized (gasPricesListLock) {
			return gasPricesList.get(type);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#setPrice(net.bigpoint.
	 * assessment.gasstation.GasType, double)
	 */
	public void setPrice(GasType type, double price) {
		synchronized (gasPricesListLock) {
			gasPricesList.put(type, price);
		}
	}

	@Override
	public String toString() {
		return "FuldaGasStation [gasPumpLocksList=" + gasPumpLocksList + ", revenueLock=" + revenueLock
				+ ", numberOfSalesLock=" + numberOfSalesLock + ", numberOfCancellationsNoGasLock="
				+ numberOfCancellationsNoGasLock + ", numberOfCancellationsTooExpensiveLock="
				+ numberOfCancellationsTooExpensiveLock + ", gasPricesListLock=" + gasPricesListLock + ", gasPumpsList="
				+ gasPumpsList + ", revenue=" + revenue + ", numberOfSales=" + numberOfSales
				+ ", numberOfCancellationsNoGas=" + numberOfCancellationsNoGas + ", numberOfCancellationsTooExpensive="
				+ numberOfCancellationsTooExpensive + ", gasPricesList=" + gasPricesList + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gasPricesList == null) ? 0 : gasPricesList.hashCode());
		result = prime * result + ((gasPricesListLock == null) ? 0 : gasPricesListLock.hashCode());
		result = prime * result + ((gasPumpLocksList == null) ? 0 : gasPumpLocksList.hashCode());
		result = prime * result + ((gasPumpsList == null) ? 0 : gasPumpsList.hashCode());
		result = prime * result + numberOfCancellationsNoGas;
		result = prime * result
				+ ((numberOfCancellationsNoGasLock == null) ? 0 : numberOfCancellationsNoGasLock.hashCode());
		result = prime * result + numberOfCancellationsTooExpensive;
		result = prime * result + ((numberOfCancellationsTooExpensiveLock == null) ? 0
				: numberOfCancellationsTooExpensiveLock.hashCode());
		result = prime * result + numberOfSales;
		result = prime * result + ((numberOfSalesLock == null) ? 0 : numberOfSalesLock.hashCode());
		long temp;
		temp = Double.doubleToLongBits(revenue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((revenueLock == null) ? 0 : revenueLock.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FuldaGasStation other = (FuldaGasStation) obj;
		if (gasPricesList == null) {
			if (other.gasPricesList != null)
				return false;
		} else if (!gasPricesList.equals(other.gasPricesList))
			return false;
		if (gasPricesListLock == null) {
			if (other.gasPricesListLock != null)
				return false;
		} else if (!gasPricesListLock.equals(other.gasPricesListLock))
			return false;
		if (gasPumpLocksList == null) {
			if (other.gasPumpLocksList != null)
				return false;
		} else if (!gasPumpLocksList.equals(other.gasPumpLocksList))
			return false;
		if (gasPumpsList == null) {
			if (other.gasPumpsList != null)
				return false;
		} else if (!gasPumpsList.equals(other.gasPumpsList))
			return false;
		if (numberOfCancellationsNoGas != other.numberOfCancellationsNoGas)
			return false;
		if (numberOfCancellationsNoGasLock == null) {
			if (other.numberOfCancellationsNoGasLock != null)
				return false;
		} else if (!numberOfCancellationsNoGasLock.equals(other.numberOfCancellationsNoGasLock))
			return false;
		if (numberOfCancellationsTooExpensive != other.numberOfCancellationsTooExpensive)
			return false;
		if (numberOfCancellationsTooExpensiveLock == null) {
			if (other.numberOfCancellationsTooExpensiveLock != null)
				return false;
		} else if (!numberOfCancellationsTooExpensiveLock.equals(other.numberOfCancellationsTooExpensiveLock))
			return false;
		if (numberOfSales != other.numberOfSales)
			return false;
		if (numberOfSalesLock == null) {
			if (other.numberOfSalesLock != null)
				return false;
		} else if (!numberOfSalesLock.equals(other.numberOfSalesLock))
			return false;
		if (Double.doubleToLongBits(revenue) != Double.doubleToLongBits(other.revenue))
			return false;
		if (revenueLock == null) {
			if (other.revenueLock != null)
				return false;
		} else if (!revenueLock.equals(other.revenueLock))
			return false;
		return true;
	}
}