import java.util.*;
import java.sql.*;                   // Step 1) Load JDBC core library
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

public class sqlFunc {
	private static ResultSet rs;
	private static ResultSet newRS; 
	private static Statement statement;
	private static Statement newStatement;
	private static Statement lastStatement;
	public static Connection connTwo;
	public static Connection connThree;
	public static DefaultMutableTreeNode dbTable;
	public static DefaultMutableTreeNode dbItem; 
	private JTree tree;
	public static void select(String table, Connection conn){
		String query = "Select Fields from Desc " + table;
			
		
	}
	public static void showTree(String dbName, Connection conn){
		p.println("Loading Tree...");
		String query = "select table_name from information_schema.columns where table_schema = \""+ dbName +"\" group by table_name";
		DefaultMutableTreeNode dbRoot = new DefaultMutableTreeNode(dbName); 
		
		
		try {
			//p.print(conn);
			statement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
			//statement = conn.createStatement();
			ArrayList<String> model = new ArrayList<String>();
			rs = statement.executeQuery(query);
			int columnCount = rs.getMetaData().getColumnCount();
			int count = 0;
			rs.beforeFirst();
			Map<String, DefaultMutableTreeNode> nodeArray = new HashMap<String, DefaultMutableTreeNode>();
			while(rs.next()){
				String[] row = new String[columnCount];
				for (int i = 1; i <= columnCount; ++i) {
				    row[i - 1] = rs.getString(i); // Or even rs.getObject()
				    dbTable = new DefaultMutableTreeNode(row[i - 1]); 
				    nodeArray.put(row[i - 1], dbTable);
				    nodeArray.forEach((key, val) -> dbRoot.add(val));
				}
			}
	
			Iterator <Map.Entry<String, DefaultMutableTreeNode>> entrySet = nodeArray.entrySet().iterator();
			rs.first();
			connTwo = DriverManager.getConnection(sqlSignInG.getURL(), "root", sqlSignInG.getPass());
			//ArrayList<String> cols = new ArrayList<String>();
			
			while (entrySet.hasNext()){
				Map.Entry<String, DefaultMutableTreeNode> entry = entrySet.next();
				String newStatementQuery = "select column_name from information_schema.columns where table_schema =\"" + dbName +"\" and table_name = \"" + entry.getKey() + "\"";
				newStatement = connTwo.prepareStatement(newStatementQuery, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
				newRS = newStatement.executeQuery(newStatementQuery);
				int ncolumnCount = newRS.getMetaData().getColumnCount();
				
				Map <String, DefaultMutableTreeNode> innerNodeArray = new HashMap<String, DefaultMutableTreeNode>();
				while(newRS.next()){
					String[] nrow = new String[ncolumnCount];
					for (int i = 1; i <= ncolumnCount; ++i) {
					    nrow[i - 1] = newRS.getString(i); // Or even rs.getObject()
					    //innerNodeArray.put(nrow[i - 1], tableItem);
					    dbItem = new DefaultMutableTreeNode(nrow[i -1]);
			    		innerNodeArray.put(entry.getKey(), dbItem);
			    		innerNodeArray.forEach((key, val) -> entry.getValue().add(val));
			    		rs.first();
			    		Iterator <Map.Entry<String, DefaultMutableTreeNode>> lastSet = innerNodeArray.entrySet().iterator();
				    	String lastQuery = "select " + nrow[i - 1] + " from " + entry.getKey();
   					    connThree = DriverManager.getConnection(sqlSignInG.getURL(), "root", sqlSignInG.getPass());
   					    Statement lastStatement = connThree.prepareStatement(lastQuery, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
   					    ResultSet lastRS = lastStatement.executeQuery(lastQuery);
   					    int lastCount  = lastRS.getMetaData().getColumnCount();
   					    DefaultMutableTreeNode item =  new DefaultMutableTreeNode(nrow[i - 1]);
   					    rs.first();
   					    while(lastRS.next()){
    					    for (int x = 1; x <= lastCount; ++x) {
   	    					    Object lastObject = lastRS.getObject(x);
						    	dbItem.add(new DefaultMutableTreeNode(String.valueOf(lastObject)));
					    	}
				    	}
				    	Map <String, DefaultMutableTreeNode> finalNodeArray = new HashMap<String, DefaultMutableTreeNode>();
					}
				}
			}
			JPanel container = new JPanel();
			JTree tree = new JTree(dbRoot);
			JFrame frame = new JFrame();
			final Font currentFont = tree.getFont();
			final Font bigFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 10);
			tree.setFont(bigFont);
			container.add(tree);
			container.setBackground(Color.WHITE);
			JScrollPane jsp = new JScrollPane(container);
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.WHITE));
			frame.setPreferredSize(new Dimension(500, 800));
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.add(jsp);
			frame.setTitle("DB: " + dbName);
			frame.pack();
			frame.setVisible(true);
		}
		catch (SQLException ex){
			JFrame errorWindow = new JFrame();
			JOptionPane.showMessageDialog(errorWindow, ex.toString(), "Error Message", JOptionPane.WARNING_MESSAGE);
			
							
		}
		
	}
	public static void show(String dbName, Connection conn){
		String query = "select table_name from information_schema.columns where table_schema = \""+ dbName +"\" group by table_name";
		try {
			//p.print(conn);
			statement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
			//statement = conn.createStatement();
			ArrayList<String> model = new ArrayList<String>();
			rs = statement.executeQuery(query);
			int columnCount = rs.getMetaData().getColumnCount();
			int count = 0;
			rs.beforeFirst();
			while(rs.next()){
				String[] row = new String[columnCount];
				for (int i = 1; i <= columnCount; ++i) {
				    row[i - 1] = rs.getString(i); // Or even rs.getObject()
				    model.add(row[i - 1]);
				    
				}
			}
			Scanner scan =  new Scanner(System.in);
			JFrame frame = new JFrame();
			frame.setPreferredSize(new Dimension(300, 600));
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			JPanel window = new JPanel(new GridLayout(4,1));
			window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));
			window.setBorder(BorderFactory.createLineBorder(window.getBackground(), 20));
			Map<String, JButton> buttonArray = new HashMap<String, JButton>();
			// Make HashMap of Buttons
			for(int i = 0; i < model.size(); i++){
				JButton b = new JButton(model.get(i));
				buttonArray.put(model.get(i), b);
			}
			Iterator <Map.Entry<String, JButton>> entrySet = buttonArray.entrySet().iterator();
			while(entrySet.hasNext()){
				Map.Entry<String, JButton> entry = entrySet.next();	
				entry.getValue().addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
							try{
								//statement.close();
								rs.first();
								connTwo = DriverManager.getConnection(sqlSignInG.getURL(), "root", sqlSignInG.getPass());
								p.println(entry.getKey());
								p.println("--------");
								String newStatementQuery = "select column_name from information_schema.columns where table_schema =\"" + dbName +"\" and table_name = \"" + entry.getKey() + "\"";
								newStatement = connTwo.prepareStatement(newStatementQuery, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
								newRS = newStatement.executeQuery(newStatementQuery);
								int ncolumnCount = newRS.getMetaData().getColumnCount();
								ArrayList<String> cols = new ArrayList<String>();
								while(newRS.next()){
									String[] nrow = new String[ncolumnCount];
									for (int i = 1; i <= ncolumnCount; ++i) {
									    nrow[i - 1] = newRS.getString(i); // Or even rs.getObject()
									    cols.add(nrow[i - 1]);
 									    //p.println(nrow[i - 1]);
									    
									}	
								}
								p.println("--------");
								
									
							}
							catch(SQLException uh){
								p.println(uh);
							}
						}
				});
				window.add(entry.getValue());
			}
			JScrollPane jsp = new JScrollPane(window);
			frame.add(jsp);
			frame.pack();
			frame.setVisible(true);
			//rs.close();
			
			
			
			
			
		}
		catch (SQLException ex){
			JFrame errorWindow = new JFrame();
			JOptionPane.showMessageDialog(errorWindow, ex.toString(), "Error Message", JOptionPane.WARNING_MESSAGE);
							
		}
		
		
	}
	public static void buildCols(ArrayList<String> aList){
		
		
	}
	public static void main(String[] args){
		
	}
}
