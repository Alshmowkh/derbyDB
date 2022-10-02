import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

public class DatabaseTest extends JFrame {

  JTextField hostField;

  JTextField queryField;

  QueryTableModel qtm;

  public DatabaseTest() {
    super("Database Test Frame");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(350, 200);

    qtm = new QueryTableModel();
    JTable table = new JTable(qtm);
    JScrollPane scrollpane = new JScrollPane(table);
    JPanel p1 = new JPanel();
    p1.setLayout(new GridLayout(3, 2));
    p1.add(new JLabel("Enter the Host URL: "));
    p1.add(hostField = new JTextField());
    p1.add(new JLabel("Enter your query: "));
    p1.add(queryField = new JTextField());
    p1.add(new JLabel("Click here to send: "));

    JButton jb = new JButton("Search");
    jb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        qtm.setHostURL(hostField.getText().trim());
        qtm.setQuery(queryField.getText().trim());
      }
    });
    p1.add(jb);
    getContentPane().add(p1, BorderLayout.NORTH);
    getContentPane().add(scrollpane, BorderLayout.CENTER);
  }

  public static void main(String args[]) {
    DatabaseTest tt = new DatabaseTest();
    tt.setVisible(true);
  }
}

//QueryTableModel.java
//A basic implementation of the TableModel interface that fills out a Vector of
//String[] structures from a query's result set.
//

class QueryTableModel extends AbstractTableModel {
  Vector cache; // will hold String[] objects . . .

  int colCount;

  String[] headers;

  Connection db;

  Statement statement;

  String currentURL;

  public QueryTableModel() {
    cache = new Vector();
//    new "org.apache.derby.jdbc.EmbeddedDriver";
  }

  public String getColumnName(int i) {
    return headers[i];
  }

  public int getColumnCount() {
    return colCount;
  }

  public int getRowCount() {
    return cache.size();
  }

  public Object getValueAt(int row, int col) {
    return ((String[]) cache.elementAt(row))[col];
  }

  public void setHostURL(String url) {
    if (url.equals(currentURL)) {
      // same database, we can leave the current connection open
      return;
    }
    // Oops . . . new connection required
    closeDB();
    initDB(url);
    currentURL = url;
  }

  // All the real work happens here; in a real application,
  // we'd probably perform the query in a separate thread.
  public void setQuery(String q) {
    cache = new Vector();
    try {
      // Execute the query and store the result set and its metadata
      ResultSet rs = statement.executeQuery(q);
      ResultSetMetaData meta = rs.getMetaData();
      colCount = meta.getColumnCount();

      // Now we must rebuild the headers array with the new column names
      headers = new String[colCount];
      for (int h = 1; h <= colCount; h++) {
        headers[h - 1] = meta.getColumnName(h);
      }

      // and file the cache with the records from our query. This would
      // not be
      // practical if we were expecting a few million records in response
      // to our
      // query, but we aren't, so we can do this.
      while (rs.next()) {
        String[] record = new String[colCount];
        for (int i = 0; i < colCount; i++) {
          record[i] = rs.getString(i + 1);
        }
        cache.addElement(record);
      }
      fireTableChanged(null); // notify everyone that we have a new table.
    } catch (Exception e) {
      cache = new Vector(); // blank it out and keep going.
      e.printStackTrace();
    }
  }

  public void initDB(String url) {
    try {
      db = DriverManager.getConnection(url);
      statement = db.createStatement();
    } catch (Exception e) {
      System.out.println("Could not initialize the database.");
      e.printStackTrace();
    }
  }

  public void closeDB() {
    try {
      if (statement != null) {
        statement.close();
      }
      if (db != null) {
        db.close();
      }
    } catch (Exception e) {
      System.out.println("Could not close the current connection.");
      e.printStackTrace();
    }
  }
}