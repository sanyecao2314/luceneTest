package gui;

import business.LuceneSearch;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.queryparser.classic.ParseException;

public class MainFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(MainFrame.class.getName());
	
	LuceneSearch ls;
	JTable tbl_result;
	DefaultTableModel dtm;

	public MainFrame(){
		//************ title and location ************//
		super("PRP Search v1.1");
		ls = new LuceneSearch();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0, 0, 1035, 605);
		//************** text box ***************//
		JTextField txtf_serch = new JTextField("Please enter a word or a sentence");
		txtf_serch.addMouseListener(new java.awt.event.MouseListener(){
            public void mouseClicked(java.awt.event.MouseEvent e) {
            	txtf_serch.setText(""); 
            }
            public void mousePressed(java.awt.event.MouseEvent e) {}
            public void mouseReleased(java.awt.event.MouseEvent e) {}
            public void mouseEntered(java.awt.event.MouseEvent e) {}
            public void mouseExited(java.awt.event.MouseEvent e) {}  
        });
		//*********** time span *************//
		JLabel label0 = new JLabel("     Time between");
		JComboBox cbb0=new JComboBox(); 
		cbb0.addItem("2017"); cbb0.addItem("2016");  cbb0.addItem("2015");  cbb0.addItem("2014");
		JLabel label1 = new JLabel("年");
		JComboBox cbb1=new JComboBox();
		cbb1.addItem("01");  cbb1.addItem("02");  cbb1.addItem("03");  cbb1.addItem("04");
		cbb1.addItem("05");  cbb1.addItem("06");  cbb1.addItem("07");  cbb1.addItem("08");
		cbb1.addItem("09");  cbb1.addItem("10");  cbb1.addItem("11");  cbb1.addItem("12");
		JLabel label2 = new JLabel("月");
		JComboBox cbb2=new JComboBox();
		cbb2.addItem("01");  cbb2.addItem("02");  cbb2.addItem("03");  cbb2.addItem("04");
		cbb2.addItem("05");  cbb2.addItem("06");  cbb2.addItem("07");  cbb2.addItem("08");
		cbb2.addItem("09");  cbb2.addItem("10");  cbb2.addItem("11");  cbb2.addItem("12");
		cbb2.addItem("13");  cbb2.addItem("14");  cbb2.addItem("15");  cbb2.addItem("16");
		cbb2.addItem("17");  cbb2.addItem("18");  cbb2.addItem("19");  cbb2.addItem("20");
		cbb2.addItem("21");  cbb2.addItem("22");  cbb2.addItem("23");  cbb2.addItem("24");
		cbb2.addItem("25");  cbb2.addItem("26");  cbb2.addItem("27");  cbb2.addItem("28");
		cbb2.addItem("29");  cbb2.addItem("30");  cbb2.addItem("31"); 
		JLabel label3 = new JLabel("日");
		JLabel label4 = new JLabel("  (小)to(大)  ");
		JComboBox cbb3=new JComboBox(); 
		cbb3.addItem("2017"); cbb3.addItem("2016");  cbb3.addItem("2015");  cbb3.addItem("2014");
		JLabel label5 = new JLabel("年");
		JComboBox cbb4=new JComboBox();
		cbb4.addItem("01");  cbb4.addItem("02");  cbb4.addItem("03");  cbb4.addItem("04");
		cbb4.addItem("05");  cbb4.addItem("06");  cbb4.addItem("07");  cbb4.addItem("08");
		cbb4.addItem("09");  cbb4.addItem("10");  cbb4.addItem("11");  cbb4.addItem("12");
		JLabel label6 = new JLabel("月");
		JComboBox cbb5=new JComboBox();
		cbb5.addItem("01");  cbb5.addItem("02");  cbb5.addItem("03");  cbb5.addItem("04");
		cbb5.addItem("05");  cbb5.addItem("06");  cbb5.addItem("07");  cbb5.addItem("08");
		cbb5.addItem("09");  cbb5.addItem("10");  cbb5.addItem("11");  cbb5.addItem("12");
		cbb5.addItem("13");  cbb5.addItem("14");  cbb5.addItem("15");  cbb5.addItem("16");
		cbb5.addItem("17");  cbb5.addItem("18");  cbb5.addItem("19");  cbb5.addItem("20");
		cbb5.addItem("21");  cbb5.addItem("22");  cbb5.addItem("23");  cbb5.addItem("24");
		cbb5.addItem("25");  cbb5.addItem("26");  cbb5.addItem("27");  cbb5.addItem("28");
		cbb5.addItem("29");  cbb5.addItem("30");  cbb5.addItem("31");
		JLabel label7 = new JLabel("日");
		//************* search button ************//
				JButton btn_serch = new JButton("Search");
				btn_serch.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						logger.info("...Query for: "+txtf_serch.getText());
						String time0 = (String)cbb0.getSelectedItem() + "\\" 
										+ (String)cbb1.getSelectedItem() + "\\"
										+ (String)cbb2.getSelectedItem();
						String time1 = (String)cbb3.getSelectedItem() + "\\" 
								+ (String)cbb4.getSelectedItem() + "\\"
								+ (String)cbb5.getSelectedItem();
						logger.info(time0+" "+time1);
						doQuery(txtf_serch.getText(), time0, time1);		
					}
				});
		//********** open document from table *******//
		tbl_result = new JTable();
		dtm = new DefaultTableModel();
		tbl_result.setModel(dtm);
		tbl_result.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {		
				int row = tbl_result.rowAtPoint(e.getPoint());
			    int col = tbl_result.columnAtPoint(e.getPoint());
			    logger.info(row+" "+col);// for debug use
			    String filename = tbl_result.getValueAt(row, col).toString();
			    String filedate = tbl_result.getValueAt(row, col+1).toString();
			    try {
			    	String p = filedate.substring(0,4)+"/"+filedate.substring(5,7)+"/"
			    			+filedate.substring(8,10)+"/";
					Desktop.getDesktop().open(new File("documents/"+ p +filename));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			public void mouseEntered(java.awt.event.MouseEvent arg0) {}
			public void mouseExited(java.awt.event.MouseEvent arg0) {}
			public void mousePressed(java.awt.event.MouseEvent arg0) {}
			public void mouseReleased(java.awt.event.MouseEvent arg0) {}
		});
		//************* layout setting **********//
		this.setLayout(new FlowLayout(3));
		txtf_serch.setPreferredSize(new Dimension(300, 20));
		btn_serch.setPreferredSize(new Dimension(100, 20));
		JPanel panel = new JPanel();panel.add(txtf_serch);panel.add(btn_serch);
		panel.add(label0);panel.add(cbb0);panel.add(label1);
		panel.add(cbb1);panel.add(label2);panel.add(cbb2);
		panel.add(label3);panel.add(label4);panel.add(cbb3);
		panel.add(label5);panel.add(cbb4);panel.add(label6);
		panel.add(cbb5);panel.add(label7);
		this.add(panel);
		JScrollPane scrollPanel = new JScrollPane();// add 滚动条
		scrollPanel.setPreferredSize(new Dimension(1000, 500));
		scrollPanel.setViewportView(tbl_result);
		this.add(scrollPanel);
	}

	public static void main(String[] args) {
		MainFrame mf = new MainFrame();
		mf.setVisible(true);
	}

	public void doQuery(String str, String t0, String t1){
		try {
			String[][] data = ls.query(str, t0, t1);
			String[] columnNames = {"No.", "File name","Date"};
			DefaultTableModel dtm2 = new DefaultTableModel();
			dtm2.setColumnIdentifiers(columnNames);
			DocsTableModel dtm = new DocsTableModel(data, columnNames);
			tbl_result.setModel(dtm);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();			
		}
	}
	@SuppressWarnings("serial")
	class DocsTableModel extends AbstractTableModel {
		private String[] columnNames;
		private String[][] data;

		public DocsTableModel(String[][] data, String[] columnNames){
			super();
			this.data = data;
			this.columnNames = columnNames;
		}
		public int getColumnCount() {return columnNames.length;}
		public int getRowCount() {return data.length;}
		public String getColumnName(int col) {return columnNames[col];}
		public Object getValueAt(int row, int col) {return data[row][col];}
		//public Class getColumnClass(int c) {
		//	return getValueAt(0, c).getClass();
		//}
	}
}