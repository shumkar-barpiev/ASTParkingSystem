package com.myexam.parking.view.swing;

import com.myexam.parking.controller.*;
import com.myexam.parking.model.*;
import com.myexam.parking.view.ParkingView;
import com.myexam.parking.view.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

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

	public ParkingSwingView() {
		setResizable(false);
		setTitle("Parking Management System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 820, 550);

		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPanel);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setForeground(new Color(0, 0, 0));
		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		JPanel errorPanel = new JPanel();
		errorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
		mainPanel.add(errorPanel, BorderLayout.SOUTH);

		errorMessageLabel = new JLabel(" ");
		errorMessageLabel.setName("errorMessageLabel");
		errorMessageLabel.setForeground(java.awt.Color.RED);
		errorMessageLabel.setFont(new java.awt.Font("Lucida Grande", java.awt.Font.BOLD, 12));
		errorPanel.add(errorMessageLabel);

		JPanel parkingPlacePanel = new JPanel(new BorderLayout(0, 0));
		parkingPlacePanel.setName("parkingPlacePanel");
		tabbedPane.addTab("Parking Places", null, parkingPlacePanel, null);

		JPanel parkingZoneFormPanel = new JPanel();
		parkingZoneFormPanel.setName("parkingZoneFormPanel");
		parkingZoneFormPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
		parkingPlacePanel.add(parkingZoneFormPanel, BorderLayout.NORTH);

		JLabel nameLabel = new JLabel("Name");
		nameLabel.setName("nameLabel");
		parkingZoneFormPanel.add(nameLabel);

		nameTextField = new JTextField(10);
		nameTextField.setName("nameTextField");
		parkingZoneFormPanel.add(nameTextField);

		JLabel capacityLabel = new JLabel("Capacity");
		capacityLabel.setName("capacityLabel");
		parkingZoneFormPanel.add(capacityLabel);

		capacityTextField = new JTextField(10);
		capacityTextField.setName("capacityTextField");
		parkingZoneFormPanel.add(capacityTextField);

		JLabel rateLabel = new JLabel("Rate");
		rateLabel.setName("rateLabel");
		parkingZoneFormPanel.add(rateLabel);

		rateTextField = new JTextField(10);
		rateTextField.setName("rateTextField");
		parkingZoneFormPanel.add(rateTextField);

		isAvailableCheckBox = new JCheckBox("Available");
		isAvailableCheckBox.setName("isAvailableCheckBox");
		parkingZoneFormPanel.add(isAvailableCheckBox);

		JButton parkingZoneSaveBtn = new JButton("Save");
		parkingZoneSaveBtn.setName("parkingZoneSaveButton");
		parkingZoneFormPanel.add(parkingZoneSaveBtn);

		JScrollPane parkingZoneTableScrollPane = new JScrollPane();
		parkingZoneTableScrollPane.setName("parkingZoneTableScrollPane");
		parkingPlacePanel.add(parkingZoneTableScrollPane, BorderLayout.CENTER);

		parkingZoneTable = new JTable();
		parkingZoneTable.setName("parkingZoneTable");
		parkingZoneTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Name", "Capacity", "Hourly Rate", "Available", "Actions" }));
		parkingZoneTableScrollPane.setViewportView(parkingZoneTable);

		JPanel parkingTicketPanel = new JPanel(new BorderLayout(0, 0));
		parkingTicketPanel.setName("parkingTicketPanel");
		tabbedPane.addTab("Ticket Management", null, parkingTicketPanel, null);

		JPanel parkingTicketFormPanel = new JPanel(new java.awt.GridBagLayout());
		parkingTicketFormPanel.setName("parkingTicketFormPanel");
		parkingTicketFormPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		parkingTicketPanel.add(parkingTicketFormPanel, BorderLayout.NORTH);

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.insets = new java.awt.Insets(5, 8, 5, 8);
		gbc.anchor = java.awt.GridBagConstraints.WEST;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		JLabel vehiclePlateLabel = new JLabel("Vehicle Plate");
		vehiclePlateLabel.setName("vehiclePlateLabel");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(vehiclePlateLabel, gbc);

		vehiclePlateTextField = new JTextField(12);
		vehiclePlateTextField.setName("vehiclePlateTextField");
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		parkingTicketFormPanel.add(vehiclePlateTextField, gbc);

		JLabel parkingZoneLabel = new JLabel("Zone");
		parkingZoneLabel.setName("parkingZoneLabel");
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(parkingZoneLabel, gbc);

		parkingZoneComboBox = new JComboBox<>();
		parkingZoneComboBox.setName("parkingZoneComboBox");
		parkingZoneComboBox.setPrototypeDisplayValue("XXXXXXXXXXXX");
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 1;
		parkingTicketFormPanel.add(parkingZoneComboBox, gbc);

		JLabel entryTimeLabel = new JLabel("Entry Time");
		entryTimeLabel.setName("entryTimeLabel");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(entryTimeLabel, gbc);

		entryTimeTextField = new JTextField(14);
		entryTimeTextField.setName("entryTimeTextField");
		entryTimeTextField.setToolTipText("e.g. 2026-06-26 09:00");
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		parkingTicketFormPanel.add(entryTimeTextField, gbc);

		JLabel exitTimeLabel = new JLabel("Exit Time");
		exitTimeLabel.setName("exitTimeLabel");
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(exitTimeLabel, gbc);

		exitTimeTextField = new JTextField(14);
		exitTimeTextField.setName("exitTimeTextField");
		exitTimeTextField.setToolTipText("e.g. 2026-06-26 11:00");
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.weightx = 1;
		parkingTicketFormPanel.add(exitTimeTextField, gbc);

		isPaidCheckBox = new JCheckBox("Paid");
		isPaidCheckBox.setName("isPaidCheckBox");
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(isPaidCheckBox, gbc);

		JButton ticketSaveBtn = new JButton("Save");
		ticketSaveBtn.setName("ticketSaveButton");
		gbc.gridx = 5;
		gbc.gridy = 1;
		gbc.weightx = 0;
		parkingTicketFormPanel.add(ticketSaveBtn, gbc);

		JScrollPane parkingTicketTableScrollPane = new JScrollPane();
		parkingTicketTableScrollPane.setName("parkingTicketTableScrollPane");
		parkingTicketPanel.add(parkingTicketTableScrollPane, BorderLayout.CENTER);

		parkingTicketTable = new JTable();
		parkingTicketTable.setName("parkingTicketTable");
		parkingTicketTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "ID", "Vehicle Plate",
				"Zone", "Entry Time", "Exit Time", "Paid", "Total Cost", "Actions" }));
		parkingTicketTableScrollPane.setViewportView(parkingTicketTable);

	}

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
