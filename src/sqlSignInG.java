import java.util.Scanner;
import java.sql.*;                   
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/******

Program Written and Designed by: Mason Lapine
Version: 0.0.1
Description: Opens a window which allows the user to login and view the database they pick in a tree like structure.
			 You can open multiple databases using this program.
Date of Conception: 02/05/2021
Last Updated: /02/05/2021

*******/ 


public class sqlSignInG {

	final static String DEFAULT_DRIVER =  "com.mysql.cj.jdbc.Driver";
	public static String url = "jdbc:mysql://localhost/";
	public static String DB = "";
	public static String pass = "";
	public static final String username = "root"; //Modify this to change MySQL username
	public static Connection conn;
	public static Connection itDone;
	/********
	Description: Creates a login prompt to view your database (default username is root)
				 and opens the database as a file tree.
	
	********/
	
	public static Connection runGUI(){
		try {
			Class.forName(DEFAULT_DRIVER);
		}
		catch(ClassNotFoundException cex){
			p.println("JDBC Driver Not Found!");
			p.println("Exiting.");
			System.exit(0);
		}
		//p.println("To exit the program and close any database connections please press Ctrl-C.");
		Scanner scan =  new Scanner(System.in);
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(600, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel window = new JPanel(new GridLayout(4,1));
		window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));
		window.setBorder(BorderFactory.createLineBorder(window.getBackground(), 20));
		JLabel lName = new JLabel("Database Name:");
		window.add(lName);
		JTextField tName = new JTextField();
		tName.setFont(new Font("Monospace", Font.PLAIN, 38));
		tName.setForeground(Color.BLACK);
		tName.setBorder(BorderFactory.createLineBorder(window.getBackground(), 20));
		window.add(tName);
		JLabel lPass = new JLabel("Password:");
		window.add(lPass);
		JPasswordField tPass = new JPasswordField();
		tPass.setFont(new Font("Monospace", Font.PLAIN, 38));
		tPass.setForeground(Color.BLACK);
		tPass.setBorder(BorderFactory.createLineBorder(window.getBackground(), 20));
		JButton submit = new JButton("Sign in");
		window.add(tPass);
		window.add(submit);
		frame.add(window);
		frame.pack();
		frame.setVisible(true);
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pass = new String(tPass.getPassword());
				String name = tName.getText();
				DB = name;
				url = url + name;
				url = url + "?serverTimezone=UTC";
				try {
					sqlSignInG.conn = DriverManager.getConnection(url, username, pass); 
					grabConnection();
					//p.println(conn);
					//p.println("Connection Successful");
					JFrame successWindow = new JFrame();
					JOptionPane.showMessageDialog(successWindow, "Connection to " + name + " database successful! \nYou may safely close the login window or keep it open if you want to open another DB.\nYour program will continue to run.", "Success!", JOptionPane.WARNING_MESSAGE);
					sqlFunc.showTree(DB, conn);	
					url = "jdbc:mysql://localhost/"; 
					DB = "";
					pass = "";
				}
				catch (SQLException ex) {
					//p.println("Connection NOT Successful");
					// p.println(ex); <-- Turn this on if you are Debugging
					JFrame failWindow = new JFrame();
					int reply = JOptionPane.showConfirmDialog(failWindow, "Connection to " + name + " database FAILED!  Would you like to see the error message?", "Failure!",JOptionPane.YES_NO_OPTION);
					if(reply == JOptionPane.YES_OPTION){
						JFrame errorWindow = new JFrame();
						JOptionPane.showMessageDialog(errorWindow, ex.toString(), "Error Message", JOptionPane.WARNING_MESSAGE);
						url = "jdbc:mysql://localhost/"; 
						DB = "";
						pass = "";
					}
					else {
						url = "jdbc:mysql://localhost/"; 
						DB = "";
						pass = "";
					}						
				}
			}
		});
		
		//p.println(conn);
		return grabConnection();
	}

	public static String getDB(){
		return DB;
	}
	public static String getPass(){
		return pass;
	}
	public static String getURL(){
		return url;
	} 

	public static void main(String[] args){
		runGUI();
		
	}
	public static Connection grabConnection(){
		//p.println(conn);
		return conn;
		
	}

}

