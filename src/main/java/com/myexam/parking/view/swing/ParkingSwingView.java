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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

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
