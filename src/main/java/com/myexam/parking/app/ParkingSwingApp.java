package com.myexam.parking.app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.myexam.parking.app.ParkingSwingApp;
import com.myexam.parking.controller.ParkingController;
import com.myexam.parking.repository.mongo.ParkingTicketMongoRepository;
import com.myexam.parking.repository.mongo.ParkingZoneMongoRepository;
import com.myexam.parking.view.swing.ParkingSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class ParkingSwingApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "parking";

	@Option(names = { "--db-collection-zones" }, description = "Parking zones collection name")
	private String zonesCollection = "parking_zones";

	@Option(names = { "--db-collection-tickets" }, description = "Parking tickets collection name")
	private String ticketsCollection = "parking_tickets";

	public static void main(String[] args) {
		new CommandLine(new ParkingSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient mongoClient = new MongoClient(new ServerAddress(mongoHost, mongoPort));

				ParkingZoneMongoRepository zoneRepository = new ParkingZoneMongoRepository(mongoClient, databaseName,
						zonesCollection);

				ParkingTicketMongoRepository ticketRepository = new ParkingTicketMongoRepository(mongoClient,
						databaseName, ticketsCollection);

				ParkingSwingView parkingView = new ParkingSwingView();

				ParkingController parkingController = new ParkingController(parkingView, zoneRepository,
						ticketRepository);

				parkingView.setParkingController(parkingController);

				parkingView.setVisible(true);

				parkingController.allParkingZones();
				parkingController.allParkingTickets();

			} catch (Exception e) {
				Logger.getLogger(ParkingSwingApp.class.getName()).log(Level.SEVERE,
						"Failed to start Parking application", e);
			}
		});
		return null;
	}
}