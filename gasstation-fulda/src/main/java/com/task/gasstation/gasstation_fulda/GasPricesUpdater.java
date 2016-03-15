package com.task.gasstation.gasstation_fulda;

import static com.task.gasstation.utils.RandomUtils.DIESEL_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.GAS_PRICE_STANDARD_DEVIATION;
import static com.task.gasstation.utils.RandomUtils.REGULAR_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.SUPER_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.UPDATE_GAS_PRICE_MAX_TIME_PERIOD;
import static com.task.gasstation.utils.RandomUtils.UPDATE_GAS_PRICE_MIN_TIME_PERIOD;
import static com.task.gasstation.utils.RandomUtils.generateAroundAverage;
import static com.task.gasstation.utils.RandomUtils.generateFromRange;

/**
 * This class represents the thread handling the gas pricing updates.
 * 
 * @author Maher Abdelkhalek
 *
 */
public class GasPricesUpdater extends Thread {

	/**
	 * The Fulda gas station object.
	 */
	private FuldaGasStation fGasStation;

	/**
	 * @param fGasStation
	 */
	public GasPricesUpdater(FuldaGasStation fGasStation) {
		this.fGasStation = fGasStation;
	}

	@Override
	public void run() {
		//the prices updating process still working until the gas station ends the service.
		while (!GasStationServiceSimulator.stationServiceEnd) {
			try {
				// waiting for the next gas prices update operation until the
				// sleep time is reached.
				Thread.sleep(generateFromRange(UPDATE_GAS_PRICE_MIN_TIME_PERIOD, UPDATE_GAS_PRICE_MAX_TIME_PERIOD));
			} catch (InterruptedException e) {
			}
			// generating new price values with random way.
			fGasStation.setPrice(fGasStation.getGasPumps().get(0).getGasType(),
					generateAroundAverage(DIESEL_AVERAGE_PRICE_PER_LITER, GAS_PRICE_STANDARD_DEVIATION));
			fGasStation.setPrice(fGasStation.getGasPumps().get(1).getGasType(),
					generateAroundAverage(REGULAR_AVERAGE_PRICE_PER_LITER, GAS_PRICE_STANDARD_DEVIATION));
			fGasStation.setPrice(fGasStation.getGasPumps().get(2).getGasType(),
					generateAroundAverage(SUPER_AVERAGE_PRICE_PER_LITER, GAS_PRICE_STANDARD_DEVIATION));

			System.out.println();
			System.out.println("The gas prices had new values as follows:");
			GasStationServiceSimulator.displayGasPricesList(fGasStation);
		}
	}
}