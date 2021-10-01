import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import oracle.jdbc.driver.*;


public class GUI implements ActionListener {
	
	// Java Swing Elements
	private JFrame frame = null;
	private JPanel queryPanel;
    private JPanel tablePanel;
	private JTable t;
	private DefaultTableModel dtm;
    
	private JButton b = null;
	private static JLabel l = null;
	private JScrollPane jsp;
	
	private JTextField movie_t = null;
	private JTextField title_t = null;
	private JTextField lang_t = null;
	private JTextField comp_t = null;
	private JTextField cntry_t = null;
	private JTextField date_t = null;
	private JTextField rt_t = null;
	
	// Array of information
	protected static Object[][] gathered_rows = {};
	private final Object[] colNames = { "MOVIEID", "TITLE", 
			"LANGUAGE", "PRODUCTION COMPANY",
			"PRODUCTION COUNTRY", "RELEASE DATE", "RUNTIME", "AVG RATING"};   
	
	/*
	 * Constructor, initializes frame
	 * Creates panels
	 * Adds buttons
	 * Displays frame
	 */
	public GUI() {
		// Setup frame
		frame = new JFrame("Movie Search System");
        frame.setSize(1300, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        //SpringLayout sll = new SpringLayout();
       
        
        
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.X_AXIS)); 
        //outerPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        //outerPanel.setBorder(new EmptyBorder(new Insets(150, 200, 150, 200)));
                
        // Create panels
        createQueryPanel();
        createTablePanel();
        
        // Add button
    	addButtonFunction();
    	
    	outerPanel.add(queryPanel);
    	outerPanel.add(tablePanel);
    	frame.add(outerPanel);
    	//frame.pack();
        
        // For testing purposes
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("GUI exited");
            }
        });
       
        frame.setVisible(true);
	}
	
	
	
	/*
	 * Creates the left side of the interface
	 * All elements on a panel
	 */
	private void createQueryPanel() {
		
		queryPanel = new JPanel();
		queryPanel.setSize(500, 500);
		queryPanel.setBackground(Color.LIGHT_GRAY);
		JPanel innerPanel;

		//queryPanel.setBackground(Color.red);
		queryPanel.setLayout(new GridLayout(9,1));
		queryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));		
		
		// Block 1
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        l = new JLabel("<HTML><h1>Movie Search System</h1></HTML>");
        innerPanel.add(l);
        queryPanel.add(innerPanel);
        
        
        // Block 2 movie ID
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        l = new JLabel("Movie ID");
        movie_t = new JTextField(20);
        innerPanel.add(l);
        innerPanel.add(movie_t);
        queryPanel.add(innerPanel);
        
        
        // Block 3 Title
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        l = new JLabel("Title");
        title_t = new JTextField(20);
        innerPanel.add(l);
        innerPanel.add(title_t);
        queryPanel.add(innerPanel);
        
        
        // Block 4 Language
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        l = new JLabel("Language");
        lang_t = new JTextField(20);
        innerPanel.add(l);
        innerPanel.add(lang_t);
        queryPanel.add(innerPanel);     
        
        // Block 5 Company
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        l = new JLabel("Production company");
        comp_t = new JTextField(20);
        innerPanel.add(l);
        innerPanel.add(comp_t);
        queryPanel.add(innerPanel);     
                
        // Block 6 Country
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        l = new JLabel("Production country");
        cntry_t = new JTextField(20);
        innerPanel.add(l);
        innerPanel.add(cntry_t);
        queryPanel.add(innerPanel);  
        
        
        // Block 7 Release Date
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        l = new JLabel("Release Date");
        date_t = new JTextField(20);
        innerPanel.add(l);
        innerPanel.add(date_t);
        queryPanel.add(innerPanel);  
        
        
        // Block 8 Runtime
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        l = new JLabel("Runtime");
        rt_t = new JTextField(20);
        innerPanel.add(l);
        innerPanel.add(rt_t);
        queryPanel.add(innerPanel); 
        
        
        
	    // Search button
        innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        b = new JButton("Search");
    	innerPanel.add(b);
    	queryPanel.add(innerPanel);

    	return;
		
	}
	
	
	/*
	 * Creates the right side of the interface
	 * Displays the table information
	 */
	private void createTablePanel() {
		
		tablePanel = new JPanel();
		tablePanel.setSize(900, 500);
		tablePanel.setBackground(Color.LIGHT_GRAY);

		//tablePanel.setBackground(Color.blue);
		SpringLayout sl = new SpringLayout();
		tablePanel.setLayout(sl);
		
		 // Title
        l = new JLabel("<HTML><h1>Queried Movies</h1></HTML>");
        tablePanel.add(l);
        
        // Table
        t = new JTable();
        dtm = new DefaultTableModel();
        dtm.setColumnIdentifiers(colNames);
        t.setModel(dtm);
        
        jsp = new JScrollPane(t);
        jsp.setPreferredSize(new Dimension(800, 500));
        
        tablePanel.add(jsp);
        
        // Aligns the label
        sl.putConstraint(SpringLayout.HORIZONTAL_CENTER, l, 0, SpringLayout.HORIZONTAL_CENTER, tablePanel);
        
        // Align the table
        sl.putConstraint(SpringLayout.NORTH, jsp,5, SpringLayout.SOUTH, l);
        sl.putConstraint(SpringLayout.HORIZONTAL_CENTER, jsp, 0, SpringLayout.HORIZONTAL_CENTER, tablePanel);
        
		return;
	}
	

	/* 
	 * Adds button to the panel and adds handling
	 * for the Action Event Listener which calls
	 * createOutputGUI() on button push
	 */
	private void addButtonFunction() {
		
		// Adds the event listener to the button
	    b.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    		System.out.println("\nButton pressed!");
	       		
	    		String[] inputs = new String[7];
	    		
	    		// movieid
	    		if (!movie_t.getText().isEmpty()) { inputs[0] = movie_t.getText(); }
	    		else { inputs[0] = null; }
	    		
	    		// title
	    		if (!title_t.getText().isEmpty()) { inputs[1] = title_t.getText(); }
	    		else { inputs[1] = null; }
	    		
	    		// language
	    		if (!lang_t.getText().isEmpty()) { inputs[2] = lang_t.getText(); }
	    		else { inputs[2] = null; }
	    		
	    		// production company
	    		if (!comp_t.getText().isEmpty()) { inputs[3] = comp_t.getText(); }
	    		else { inputs[3] = null; }
	    		
	    		// production country
	    		if (!cntry_t.getText().isEmpty()) { inputs[4] = cntry_t.getText(); }
	    		else { inputs[4] = null; }
	    		
	    		// release date
	    		if (!date_t.getText().isEmpty()) { inputs[5] = date_t.getText(); }
	    		else { inputs[5] = null; }
	    		
	    		// runtime
	    		if (!rt_t.getText().isEmpty()) { inputs[6] = rt_t.getText(); }
	    		else { inputs[6] = null; }
	    		
	    		gathered_rows = Student.processInput(inputs);
	    		
	    		
	    		//t.setModel(dtm);
	            
	    		System.out.print("\n\nFound ");
	    		System.out.print(gathered_rows.length);
	    		System.out.println(" matching results in gathered_rows\n");
	    		
	    		
	    		// Create new table model from data
	    		dtm = new DefaultTableModel();
	    		dtm.setColumnIdentifiers(colNames);
	    		for (int i = 0; i < gathered_rows.length; i++) {
	    			dtm.addRow(gathered_rows[i]);
	    		}
	    		t.setModel(dtm);
	    		
	    		// Auto size columns
	    		t.getColumnModel().getColumn(0).setPreferredWidth(60);
	    		t.getColumnModel().getColumn(1).setPreferredWidth(150);
	    		t.getColumnModel().getColumn(2).setPreferredWidth(40);
	    		t.getColumnModel().getColumn(3).setPreferredWidth(200);
	    		t.getColumnModel().getColumn(4).setPreferredWidth(150);
	    		t.getColumnModel().getColumn(5).setPreferredWidth(50); 
	    		t.getColumnModel().getColumn(6).setPreferredWidth(60);
	    		t.getColumnModel().getColumn(7).setPreferredWidth(50); 
	    		dtm.fireTableDataChanged();
	    	}
	    });
	}
}


