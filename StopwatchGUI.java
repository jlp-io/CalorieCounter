import java.awt.*;					
import java.awt.event.*;			
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class StopwatchGUI extends JFrame implements ActionListener {
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 250;
	private JButton resetTime = new JButton("Reset");
	private JButton deleteEntry = new JButton("Delete Entry");
	private JButton retrieveTime = new JButton("Get Entries");
	private JButton newTime = new JButton("New Entry");
	private JButton newDay = new JButton("New Day");
	private JButton getDays = new JButton("Get Days");
	private JLabel foodLabel = new JLabel("Food Name");
	private JLabel calorieLabel = new JLabel("Calories");
	private JTextField foodField = new JTextField(20);
	private JTextField calorieField = new JTextField(10);
	private JTextArea outputArea = new JTextArea(5,40);
	private Date date1;
	private static String URL = "jdbc:mysql://zwgaqwfn759tj79r.chr7pe7iynqr.eu-west-1.rds.amazonaws.com/igqxgrcy5dw9rxc2";
    private static String USER = "gqk0vs5rc6nfbj0w";
    private static String PASS = "ib45oys91og5m4q5";
    private String SQL;
    private int totalCalories;
    private String date2;
    private String date3;
    
	public static void main(String[] args) {
		StopwatchGUI app = new StopwatchGUI();
		app.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		app.setTitle("Calorie Counter");
		app.createGUI();
		app.setVisible(true);		
	}
	
	public void createGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		add(resetTime);		
		resetTime.addActionListener(this);
		add(retrieveTime);
		retrieveTime.addActionListener(this);
		add(newTime);
		newTime.addActionListener(this);
		add(deleteEntry);
		deleteEntry.addActionListener(this);
		add(newDay);
		newDay.addActionListener(this);
		add(getDays);
		getDays.addActionListener(this);
		add(foodLabel);
		add(foodField);
		add(calorieLabel);
		add(calorieField);
		add(outputArea);
		outputArea.setEditable(false);
	}
	
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == newTime) {
			if (!foodField.getText().equals("") && !calorieField.getText().equals("")) {
				date1 = new Date();
				DateFormat df1 = new SimpleDateFormat("dd HH:mm");
				date2 = df1.format(date1);
				DateFormat df2 = new SimpleDateFormat("dd");
				date3 = df2.format(date1);
				String food = foodField.getText();
				String calories = calorieField.getText();
				outputArea.setText(date2 + " " + food + " " + calories);
				try {
				Connection myConn = DriverManager.getConnection(URL, USER, PASS);
				Statement myStmt = myConn.createStatement();
				SQL = "INSERT INTO food(Time, Food_Name, Calories) VALUES ('" + date2 + "'," + " '" + food + "'," + " '" + calories + "')";
				myStmt.executeUpdate(SQL);
				}catch(Exception ex) {
					outputArea.setText(ex.toString());
				}
				foodField.setText("");
				calorieField.setText("");
			}else{
				outputArea.setText("One of the fields is empty");
			}
		}
		
		if (event.getSource() == resetTime) {
			clear();
		}
		
		if (event.getSource() == retrieveTime) {
			clear();
			totalCalories = 0;
			try {
			Connection myConn = DriverManager.getConnection(URL, USER, PASS);
			Statement myStmt = myConn.createStatement();
			SQL = "SELECT * FROM food";
			ResultSet rs = myStmt.executeQuery(SQL);
			while (rs.next()) {
				outputArea.append(rs.getString("Time") + (" "));
				outputArea.append(rs.getString("Food_Name") + (" "));
				outputArea.append(rs.getInt("Calories") + "\n");
				totalCalories+=rs.getInt("Calories");
			}
			}catch(Exception ex) {
				outputArea.setText(ex.toString());
			}
			outputArea.append("Total Calories: " + totalCalories);
		}		
		
		if (event.getSource() == deleteEntry) {
			if (!foodField.getText().equals("") && !calorieField.getText().equals("")) {
			try {
				String deletedItem = foodField.getText();
				Connection myConn = DriverManager.getConnection(URL, USER, PASS);
				Statement myStmt = myConn.createStatement();
				SQL = "DELETE FROM food WHERE Food_Name = " +  "'" + deletedItem + "'";
				myStmt.executeUpdate(SQL);
				outputArea.setText("Deleted " + deletedItem);
			}catch(Exception ex) {
				outputArea.setText(ex.toString());
			}
			}else{
				outputArea.setText("One of the fields is empty");
			}
		}
		
		if (event.getSource() == newDay) {
			if (totalCalories > 0) {
			try {
			Connection myConn = DriverManager.getConnection(URL, USER, PASS);
			Statement myStmt = myConn.createStatement();
			SQL = "INSERT INTO days(Day, Calories) VALUES ('" + date3 + "'," + " '" + totalCalories + "')";
			myStmt.executeUpdate(SQL);
			totalCalories = 0;
			SQL = "SELECT * FROM days";
			ResultSet rs = myStmt.executeQuery(SQL);
			while (rs.next()) {
				outputArea.append(rs.getInt("Calories") + "\n");
			}
			SQL = "TRUNCATE food";
			myStmt.executeUpdate(SQL);
			}catch(Exception ex) {
				System.out.println(ex.toString());
			}
			}else{
				outputArea.setText("There are no entries currently stored. Only use this button at the beginning of a new day. ");
			}
		}
		
		if (event.getSource() == getDays) {
			clear();
			try {
			Connection myConn = DriverManager.getConnection(URL, USER, PASS);
			Statement myStmt = myConn.createStatement();
			SQL = "SELECT * FROM days";
			ResultSet rs = myStmt.executeQuery(SQL);
			while (rs.next()) {
				outputArea.append(rs.getString("Day") + " ");
				outputArea.append(rs.getInt("Calories") + "\n");
			}
			}catch(Exception ex) {
				outputArea.setText(ex.toString());
			}
		}
	}
	
	public void clear() {
		outputArea.setText("");
		foodField.setText("");
		calorieField.setText("");
	}
}