package derbydb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CreateNew {

    ArrayList results;
    PreparedStatement ps;
    Connection conn;
    ResultSet rs;

    void connectionToDB(String nameDB) throws SQLException, InstantiationException, ClassNotFoundException, IllegalAccessException {
        conn = DriverManager.getConnection("jdbc:derby:" + nameDB);
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
//        Properties pr = System.getProperties();
//        pr.put("derby.storage.pageSize", "32000");
//        pr.put("derby.storage.pageCacheSize", "5000");

    }

    void closeDB() throws SQLException {
        conn.close();
//        DriverManager.getConnection("jdbc:derby;shutdown=true");
    }

    ArrayList<String> readSynsetMembers(String synsetid) {

        try {
            results = new ArrayList();
            ps = conn.prepareStatement("SELECT value FROM word WHERE synsetid = ?");
            ps.setString(1, synsetid);
            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("value"));
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return results;
    }

    void createNew(String name) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + name + ";create=true");
//       String stmt="CREATE TABLE TABLE1 (C11 int, C12 int)";
        String stmt = "CREATE TABLE TABLE2 (ID int, name int)";

        Statement s = conn.createStatement();
//       s.execute(stmt);

        s.execute("creAtE TABLE first4 ("
                + "C11 int PRIMARY KEY,"
                + "C12 varchar(12))");
//       ps=conn.prepareStatement(stmt);
//            rs = ps.executeQuery();
    }

    void insert() throws SQLException {
        String stmt = "insert into first2 values (3, 'hello3')";
        ps = conn.prepareStatement(stmt);
        ps.execute();
        conn.commit();

    }

    void select() throws SQLException {
        String stmt = "select * from first2";
        ps = conn.prepareStatement(stmt);
        rs=ps.executeQuery();
        while (rs.next()) {
               pl(rs.getString("c12"));
            }

    }

    public static void main(String[] args) throws SQLException, InstantiationException, ClassNotFoundException, IllegalAccessException, IOException {

        CreateNew dbmain = new CreateNew();
//        dbmain.createNew("firstDB");
        dbmain.connectionToDB("firstDB");
//        dbmain.insert();
        dbmain.select();

//        DatabaseMetaData metaData = dbmain.conn.getMetaData();
//         dbmain.rs=metaData.getTables(null, null, "%", null);
//         while(dbmain.rs.next()){
//             System.out.println(dbmain.rs.getString(3));
//         }
//        DriverManager.getConnection("jdbc:derby:;shutdown=true");
        //------------------------------------------------------------------------------
//        dbmain.closeDB();
    }
    static void pl(Object o){
        System.out.println(o);
    }

}
