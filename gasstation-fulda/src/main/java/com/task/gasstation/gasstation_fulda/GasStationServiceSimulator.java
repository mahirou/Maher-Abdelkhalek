package com.task.gasstation.gasstation_fulda;

import static com.task.gasstation.utils.RandomUtils.CUSTOMER_INCOMING_MAX_TIME_PERIOD;
import static com.task.gasstation.utils.RandomUtils.CUSTOMER_INCOMING_MIN_TIME_PERIOD;
import static com.task.gasstation.utils.RandomUtils.DIESEL_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.DIESEL_GAS_AMOUNT;
import static com.task.gasstation.utils.RandomUtils.GAS_PRICE_STANDARD_DEVIATION;
import static com.task.gasstation.utils.RandomUtils.GAS_PUMPS_NUMBER;
import static com.task.gasstation.utils.RandomUtils.REGULAR_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.REGULAR_GAS_AMOUNT;
import static com.task.gasstation.utils.RandomUtils.SUPER_AVERAGE_PRICE_PER_LITER;
import static com.task.gasstation.utils.RandomUtils.SUPER_GAS_AMOUNT;
import static com.task.gasstation.utils.RandomUtils.UPDATE_GAS_PRICE_MAX_TIME_PERIOD;
import static com.task.gasstation.utils.RandomUtils.UPDATE_GAS_PRICE_MIN_TIME_PERIOD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;

/**
 * This class is a simulation of the Fulda gas station service processing.
 * 
 * @author Maher Abdelkhalek
 *
 */
public class GasStationServiceSimulator {

	/**
	 * Define the status of the service in the gas station.
	 */
	static volatile boolean stationServiceEnd = false;

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {

		boolean ServiceStarted = false;

		System.out.println("Welcome to Fulda gas station simulator.");
		Thread.sleep(1000);
		System.out.println("The station has " + GAS_PUMPS_NUMBER + " pumps:" + GasType.DIESEL + ", " + GasType.REGULAR
				+ " and " + GasType.SUPER + ".");
		Thread.sleep(1000);
		System.out.println("Loading gas station...");

		// Setting up the gas station
		FuldaGasStation fGasStation = new FuldaGasStation();

		fGasStation.addGasPump(new GasPump(GasType.DIESEL, DIESEL_GAS_AMOUNT));
		fGasStation.addGasPump(new GasPump(GasType.REGULAR, REGULAR_GAS_AMOUNT));
		fGasStation.addGasPump(new GasPump(GasType.SUPER, SUPER_GAS_AMOUNT));

		fGasStation.setPrice(GasType.DIESEL, DIESEL_AVERAGE_PRICE_PER_LITER);
		fGasStation.setPrice(GasType.REGULAR, REGULAR_AVERAGE_PRICE_PER_LITER);
		fGasStation.setPrice(GasType.SUPER, SUPER_AVERAGE_PRICE_PER_LITER);

		// Display Fulda gas station details in the console.
		Thread.sleep(2000);
		System.out.println();
		System.out.println("Gas tanks are fulfilled with:");
		displayGasTankStatus(fGasStation);

		Thread.sleep(2000);
		System.out.println();
		System.out.println("The gas prices list is set with average values as follows:");
		displayGasPricesList(fGasStation);
		Thread.sleep(1000);
		System.out.println("The gas prices list is updated periodically with random periode ["
				+ UPDATE_GAS_PRICE_MIN_TIME_PERIOD / 1000 + "-" + UPDATE_GAS_PRICE_MAX_TIME_PERIOD / 1000
				+ " seconds]");
		System.out.println("with random prices around the average values[(+/-)" + GAS_PRICE_STANDARD_DEVIATION + " €]");

		Thread.sleep(2000);
		System.out.println();
		System.out.println("Each pump has its customer incoming process simulation.");
		Thread.sleep(1000);
		System.out.println("The incoming is periodic with random period [" + CUSTOMER_INCOMING_MIN_TIME_PERIOD / 1000
				+ "-" + CUSTOMER_INCOMING_MAX_TIME_PERIOD / 1000 + " seconds]");

		// Creating a customer incoming thread of each pump.
		CustomerIncoming dieselOperations = new CustomerIncoming(fGasStation, GasType.DIESEL, stationServiceEnd);
		CustomerIncoming regularOperations = new CustomerIncoming(fGasStation, GasType.REGULAR, stationServiceEnd);
		CustomerIncoming superOperations = new CustomerIncoming(fGasStation, GasType.SUPER, stationServiceEnd);

		// Creating a gas price updater thread.
		GasPricesUpdater gasPricesUpdater = new GasPricesUpdater(fGasStation);

		Thread.sleep(2000);

		// Creating a reader from the console
		BufferedReader l_buffer = new BufferedReader(new InputStreamReader(System.in));
		String l_line;
		int l_option = -1;
		System.out.println();
		System.out.println("MENU:");
		// Still listening to the user console entrance until the end of
		// service.
		do {
			// display the application menu options.
			if (!ServiceStarted) {
				System.out.print("[Options: 1 Start Station service | ");
			} else {
				System.out.print("[Options: 2 End Station service | ");
			}
			System.out.print("3 Operations Status | 4 Tanks Status | 5 Prices List Status]:");

			l_line = l_buffer.readLine();
			try {
				l_option = Integer.parseInt(l_line);
				switch (l_option) {
				case 1:
					if (!ServiceStarted) {
						// Starting gas station service.
						ServiceStarted = true;
						System.out.println();
						System.out.println("Starting service...");
						// Starting the 3 incoming customers threads.
						dieselOperations.start();
						regularOperations.start();
						superOperations.start();
						// Starting the gas price updater thread.
						gasPricesUpdater.start();
					} else {
						Thread.sleep(500);
						System.out.println();
						System.out.println("Worng value!");
					}
					break;
				case 2:
					if (ServiceStarted) {
						// Ending the gas station service.
						stationServiceEnd = true;
					} else {
						Thread.sleep(500);
						System.out.println();
						System.out.println("Worng value!");
					}
					break;
				case 3:
					Thread.sleep(300);
					displayGasStationOperationsStatus(fGasStation);
					Thread.sleep(1000);
					break;
				case 4:
					Thread.sleep(300);
					System.out.println();
					System.out.println("Remaining gas amounts:");
					displayGasTankStatus(fGasStation);
					Thread.sleep(1000);
					break;
				case 5:
					Thread.sleep(300);
					System.out.println();
					System.out.println("Current gas prices list:");
					displayGasPricesList(fGasStation);
					Thread.sleep(1000);
					break;
				default:
					Thread.sleep(500);
					System.out.println();
					System.out.println("Worng value!");
					break;
				}
			} catch (NumberFormatException e) {
				Thread.sleep(500);
				System.out.println();
				System.out.println("Worng value!");
			}
			System.out.println();
		} while (!stationServiceEnd);

		// end of the gas station service.

		// waiting until the the launched thread end.
		dieselOperations.join();
		regularOperations.join();
		superOperations.join();
		gasPricesUpdater.join();

		// Display the operation status of Fulda gas station.
		System.out.println();
		System.out.println("End of services.");
		displayGasStationOperationsStatus(fGasStation);
		Thread.sleep(1000);
		System.out.println();
		System.out.println("End of simulation.");
	}

	/**
	 * This method displays the operation status of the gas station like the
	 * revenue, the succeeded and cancelled operations.
	 * 
	 * @param fGasStation
	 * @throws InterruptedException
	 */
	static void displayGasStationOperationsStatus(FuldaGasStation fGasStation) throws InterruptedException {
		System.out.println();
		System.out.println("Fulda Gas station operations status:");
		System.out.println("Revenue: " + String.format("%.2f", fGasStation.getRevenue()) + "€.");
		System.out.println("Number of sales: " + fGasStation.getNumberOfSales() + " successful.");
		System.out.println("Number of cancelled sales due to gas unavailability: "
				+ fGasStation.getNumberOfCancellationsNoGas() + " Cancellation(s).");
		System.out.println("Number of cancelled sales due to expencive gas price: "
				+ fGasStation.getNumberOfCancellationsTooExpensive() + " Cancellation(s).");
	}

	/**
	 * This method displays the pumps status of the gas station.
	 * 
	 * @param fGasStation
	 */
	static void displayGasTankStatus(FuldaGasStation fGasStation) {
		System.out.print(fGasStation.getGasPumps().get(0).getGasType() + ": "
				+ String.format("%.2f", fGasStation.getGasPumps().get(0).getRemainingAmount()) + " L, ");
		System.out.print(fGasStation.getGasPumps().get(1).getGasType() + ": "
				+ String.format("%.2f", fGasStation.getGasPumps().get(1).getRemainingAmount()) + " L and ");
		System.out.println(fGasStation.getGasPumps().get(2).getGasType() + ": "
				+ String.format("%.2f", fGasStation.getGasPumps().get(2).getRemainingAmount()) + " L.");
	}

	/**
	 * This method displays the gas prices list of the gas station.
	 * 
	 * @param fGasStation
	 */
	static void displayGasPricesList(FuldaGasStation fGasStation) {
		System.out.print(GasType.DIESEL + ": " + String.format("%.2f", fGasStation.getPrice(GasType.DIESEL)) + "€/L, ");
		System.out.print(
				GasType.REGULAR + ": " + String.format("%.2f", fGasStation.getPrice(GasType.REGULAR)) + "€/L and ");
		System.out.println(GasType.SUPER + ": " + String.format("%.2f", fGasStation.getPrice(GasType.SUPER)) + "€/L.");
	}
}