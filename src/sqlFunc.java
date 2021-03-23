import java.util.*;
import java.sql.*; 
import javax.swing.event.*;                  // Step 1) Load JDBC core library
import javax.swing.*;
import javax.swing.tree.*;


//import jdk.internal.org.jline.terminal.MouseEvent;

import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class sqlFunc {
	public static boolean runAgain = false;
	private static ResultSet rs;
	private static ResultSet newRS; 
	private static Statement statement;
	private static Statement newStatement;
	public static DefaultMutableTreeNode node;
	public static boolean selected = false; 
	public static boolean selectedInsert = false; 
	public static JTable thisTable;
	public static Object nodeInfo;
	public static JTree tree;
	public static String passwd = sqlSignInG.getPass();
	public static String url = sqlSignInG.getURL();
	private static Map<String, JTable> Jtables = new HashMap<String, JTable>();
	public static DefaultTableModel table = new DefaultTableModel();
	public static Connection connTwo;
	public static Connection connThree;
	public static int ncolumnCount;
	public static DefaultMutableTreeNode dbTable;
	public static DefaultMutableTreeNode dbItem; 
	public static Map<ArrayList<Object>, Object[]> tableData = new HashMap<ArrayList<Object>, Object[]>();
	public static Object[] smallColumns;
	public static Object[] smallRows;

	public static void showTree(String dbName, Connection conn){
		displayHelpInfo();
		runAgain = false;
		String query = "select table_name from information_schema.columns where table_schema = \""+ dbName +"\" group by table_name";
		DefaultMutableTreeNode dbRoot = new DefaultMutableTreeNode(dbName); 
		try {
			statement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
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
			
			
			while (entrySet.hasNext()){
				Map.Entry<String, DefaultMutableTreeNode> entry = entrySet.next();
				String newStatementQuery = "select column_name from information_schema.columns where table_schema =\"" + dbName +"\" and table_name = \"" + entry.getKey() + "\"";
				newStatement = connTwo.prepareStatement(newStatementQuery, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
				newRS = newStatement.executeQuery(newStatementQuery);
				ncolumnCount = newRS.getMetaData().getColumnCount();
				smallColumns = new Object[ncolumnCount];
				
				
				Map <String, DefaultMutableTreeNode> innerNodeArray = new HashMap<String, DefaultMutableTreeNode>();
				
				while(newRS.next()){
					String[] nrow = new String[ncolumnCount];
					for (int i = 1; i <= ncolumnCount; ++i) {
						smallColumns[i - 1] = newRS.getObject(i);
					    nrow[i - 1] = newRS.getString(i); 
						
					    dbItem = new DefaultMutableTreeNode(nrow[i -1]);
			    		innerNodeArray.put(entry.getKey(), dbItem);
			    		innerNodeArray.forEach((key, val) -> entry.getValue().add(val));
			    		rs.first();
			    		Iterator <Map.Entry<String, DefaultMutableTreeNode>> lastSet = innerNodeArray.entrySet().iterator(); 
				    	String lastQuery = "select " + nrow[i - 1] + " from " + entry.getKey();
						//System.out.println(nrow[i-1]); <-- DEBUG
   					    connThree = DriverManager.getConnection(sqlSignInG.getURL(), "root", sqlSignInG.getPass());
   					    Statement lastStatement = connThree.prepareStatement(lastQuery, ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_UPDATABLE);
   					    ResultSet lastRS = lastStatement.executeQuery(lastQuery);
						
						String tableQuery = "select * from " + entry.getKey();
						Connection connFour = DriverManager.getConnection(sqlSignInG.getURL(),"root", sqlSignInG.getPass());
						Statement tableStat = connFour.prepareStatement(tableQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
						ResultSet tableRS = tableStat.executeQuery(tableQuery);
						JTable tempTable = new JTable(buildTableModel(tableRS));
						Jtables.put(entry.getKey(), tempTable);
   					    int lastCount  = lastRS.getMetaData().getColumnCount();
   					    DefaultMutableTreeNode item =  new DefaultMutableTreeNode(nrow[i - 1]);
   					    rs.first();
						
   					    while(lastRS.next()){
    					    for (int x = 1; x <= lastCount; ++x) {
   	    					    Object lastObject = lastRS.getObject(x);
						    	dbItem.add(new DefaultMutableTreeNode(String.valueOf(lastObject)));								
					    	}
				    	}
				
						
						
				    	
					}
				}

			}
			JPanel container = new JPanel();
			tree = new JTree(dbRoot);
			JFrame frame = new JFrame();
			final Font currentFont = tree.getFont();
			final Font bigFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 10);
			tree.addTreeSelectionListener(new TreeSelectionListener(){

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					nodeInfo = node.getUserObject();
					for (int y = 0; y <  tree.getModel().getChildCount(tree.getModel().getRoot()); y++)
						if (nodeInfo.toString().equals(tree.getModel().getChild(tree.getModel().getRoot(), y).toString())){
							selected = true;			
							selectedInsert = true;
						}
					}
			});
			tree.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {

				}

				@Override
				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub
					if (e.getKeyCode() == 32) {
						
						if (selected == true){
								Object nodeInfoTwo = node.getUserObject();
								JFrame tableWindowFrame = new JFrame();
								tableWindowFrame.setBackground(Color.BLACK);
								table.addRow(tableData.keySet().toArray());
		
								thisTable = Jtables.get(nodeInfo.toString());
								tableWindowFrame.add(new JScrollPane(thisTable));
								tableWindowFrame.setSize(500, 200);
								tableWindowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
								tableWindowFrame.setTitle("Node Table: " + nodeInfoTwo.toString());
								tableWindowFrame.pack();
								tableWindowFrame.setVisible(true);
								selected = false;
							}
						}
						if(e.getKeyCode() == KeyEvent.VK_I){
							//System.out.println("n pressed.");
							if(selectedInsert == true){
							try {
								String getColumnsQuery = "SELECT column_name FROM information_schema.columns WHERE table_schema = '" +dbName + "' AND table_name = '" + nodeInfo.toString() + "'";
								Connection colsConnection = DriverManager.getConnection(url,"root", passwd);
								Statement colsStat = colsConnection.prepareStatement(getColumnsQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
								ResultSet colRS = colsStat.executeQuery(getColumnsQuery);
								ArrayList<String> headerList = new ArrayList<String>();
								int columnCount = colRS.getMetaData().getColumnCount();
								while(colRS.next()){
									for(int l = 1; l <= columnCount; l++){
										headerList.add(colRS.getString(l));
										//System.out.println(colRS.getString(l));
									}
								}
								JPanel insertPanel = new JPanel();
								insertPanel.setPreferredSize(new Dimension(600, 600));
								JFrame insertFrame = new JFrame();

								Map<String, JTextField> insertMap = new HashMap<String, JTextField>();

								for (int i = 0; i < headerList.size(); i++){
									insertMap.put(headerList.get(i), new JTextField(headerList.get(i), 20));

								}
								Iterator <Map.Entry<String, JTextField>> textSet = insertMap.entrySet().iterator(); 
								while(textSet.hasNext()){
									Map.Entry<String, JTextField> entry = textSet.next();

									insertPanel.add(entry.getValue());

									
								}
								JButton insertButton = new JButton("Insert");
								insertPanel.add(insertButton);
								insertFrame.setPreferredSize(new Dimension(600, 200));
								insertFrame.add(insertPanel);
								
								insertFrame.pack();
								insertFrame.setVisible(true);
								insertFrame.setTitle("INSERT");
								insertButton.addActionListener(new ActionListener(){
									@Override
									public void actionPerformed(ActionEvent e){
										Iterator <Map.Entry<String, JTextField>> textSetTwo = insertMap.entrySet().iterator(); 
										ArrayList<String> insertResults = new ArrayList<String>();
										while(textSetTwo.hasNext()){
											Map.Entry<String, JTextField> entryTwo = textSetTwo.next();
											insertResults.add("\"" + entryTwo.getValue().getText()+ "\"");
											
										}
										String insertStringQuery = "INSERT INTO " + nodeInfo.toString() + " VALUES (" + insertResults.toString().replace("[", "").replace("]", "") + ")";
										System.out.println(insertStringQuery);
										try {
																					
											Statement insGO = colsConnection.createStatement();
											insGO.executeUpdate(insertStringQuery);
											JFrame successWindow = new JFrame();
											JOptionPane.showMessageDialog(successWindow, "Added insert info the the table.\n Please close this window and the tree window and log in again.", "Success!", JOptionPane.PLAIN_MESSAGE);
											//sqlFunc.runAgain = true;
										} catch (SQLException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}


									}
							

								});
								//insertPanel.pack();
								
							} catch (SQLException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
							}
							selectedInsert = false;
						}
					}
					
					
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}

			});
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
		//System.out.println(runAgain);
		
	}
	// Stack Overflow Method - Credit: https://stackoverflow.com/questions/10620448/most-simple-code-to-populate-jtable-from-resultset
	public static DefaultTableModel buildTableModel(ResultSet rs)
			throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		// names of columns
		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column));
		}

		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(rs.getObject(columnIndex));
			}
			data.add(vector);
		}

		return new DefaultTableModel(data, columnNames);

	}


	public static void buildCols(ArrayList<String> aList){
		
		
	}
	public static void main(String[] args){
		
	}
	public static void deselectAll(JTree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        TreeSelectionModel m = tree.getSelectionModel();
        if (m != null) {
            m.clearSelection();
        }
    }
	public static void displayHelpInfo(){
		JFrame frame = new JFrame("Help Information");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		frame.setSize(200, 200);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JLabel spacelab = new JLabel("Press '[spacebar]' -> Display the selected table data in a grid-like formation.");
		JLabel Ilab = new JLabel("Press '[i]' -> Insert Data into the selected table.");
		spacelab.setVerticalAlignment(JLabel.TOP);
		panel.add(spacelab);
		panel.add(Ilab);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);


	}
}
