package com.myexam.parking.view.swing;

import com.myexam.parking.controller.*;
import com.myexam.parking.model.*;
import com.myexam.parking.view.ParkingView;
import com.myexam.parking.view.swing.*;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ParkingSwingView extends JFrame implements ParkingView {
	private JPanel mainPanel;

	private JTextField nameTextField;
	private JTextField capacityTextField;
	private JTextField rateTextField;
	private JCheckBox isAvailableCheckBox;

	private JTextField vehiclePlateTextField;
	private JTextField entryTimeTextField;
	private JTextField exitTimeTextField;
	private JCheckBox isPaidCheckBox;

	private JTable parkingZoneTable;
	private JTable parkingTicketTable;

	private JLabel errorMessageLabel;
	private JComboBox<String> parkingZoneComboBox;

	private java.util.Map<String, ParkingZone> parkingZoneByIdMap = new java.util.HashMap<>();
	private java.util.Map<String, ParkingTicket> parkingTicketByIdMap = new java.util.HashMap<>();

	private ParkingController parkingController;

	@Override
	public void showAllParkingZones(List<ParkingZone> zones) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parkingZoneAdded(ParkingZone zone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parkingZoneRemoved(ParkingZone zone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showError(String message, ParkingZone zone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showAllParkingTickets(List<ParkingTicket> tickets) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parkingTicketAdded(ParkingTicket ticket) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parkingTicketRemoved(ParkingTicket ticket) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showError(String message, ParkingTicket ticket) {
		// TODO Auto-generated method stub

	}

	public void setParkingController(ParkingController parkingController) {
		this.parkingController = parkingController;
	}

}
