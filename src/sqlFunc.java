import java.util.*;
import java.sql.*; 
import javax.swing.event.*;                  // Step 1) Load JDBC core library
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class sqlFunc {
	private static ResultSet rs;
	private static ResultSet newRS; 
	private static Statement statement;
	private static Statement newStatement;
	private static Statement lastStatement;
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
		p.println("Loading Tree...");
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
			JTree tree = new JTree(dbRoot);
			JFrame frame = new JFrame();
			final Font currentFont = tree.getFont();
			final Font bigFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 10);
			tree.addTreeSelectionListener(new TreeSelectionListener() {
			    public void valueChanged(TreeSelectionEvent e) {
			        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			
			
			    /* retrieve the node that was selected */ 
			        Object nodeInfo = node.getUserObject();
			        for (int y = 0; y <  tree.getModel().getChildCount(tree.getModel().getRoot()); y++)
			        	if (nodeInfo.toString().equals(tree.getModel().getChild(tree.getModel().getRoot(), y).toString())){
			        		tree.addKeyListener(new KeyListener() {
			        		
			        		            @Override
			        		            public void keyTyped(KeyEvent e) {
			        		            }
			        		
			        		            @Override
			        		            public void keyPressed(KeyEvent e) {
			        		                if (e.getKeyCode() == 32) {
			        		                	JFrame tableWindowFrame = new JFrame();
												tableWindowFrame.setBackground(Color.BLACK);
												table.addRow(tableData.keySet().toArray());


			        		                	JTable thisTable = Jtables.get(nodeInfo.toString());
			        		                	tableWindowFrame.add(new JScrollPane(thisTable));
			        		                	tableWindowFrame.setSize(500, 200);
			        		                	tableWindowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			        		                	tableWindowFrame.setTitle("Node Table: " + nodeInfo.toString());
			        		                	tableWindowFrame.pack();
			        		                	tableWindowFrame.setVisible(true);
			        		                    
			        		                }
			        		            }
			        		
			        		            @Override
			        		            public void keyReleased(KeyEvent e) {
			        		            }
			        		        });
			        		
			        		
			        	}
			       
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
}
