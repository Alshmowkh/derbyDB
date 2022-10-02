package derbydb;

import com.articulate.sigma.Graph;
import com.articulate.sigma.KB;
import com.articulate.sigma.KBmanager;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import javax.swing.JOptionPane;

public class DerbyDB_main {

    ArrayList results;
    PreparedStatement ps;
    Connection conn;
    ResultSet rs;

    void connectionToDB() throws SQLException, InstantiationException, ClassNotFoundException, IllegalAccessException {
        String wordnet = "F:\\Master\\Thesis\\Tools\\AWN\\awn\\wordnet";
        conn = DriverManager.getConnection("jdbc:derby:" + wordnet);
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        Properties pr = System.getProperties();
        pr.put("derby.storage.pageSize", "32000");
        pr.put("derby.storage.pageCacheSize", "5000");

    }

    void closeDB() throws SQLException {
        conn.close();
//        DriverManager.getConnection("jdbc:derby;shutdown=true");
    }

    ArrayList getTopLevelSynsetsForTree() throws SQLException {
        results = new ArrayList();
        PreparedStatement ps = conn.prepareStatement("SELECT arabicid FROM arabicstarters");
        String s1 = "";
        for (rs = ps.executeQuery(); rs.next(); results.add(s1)) {
            s1 = rs.getString("arabicid");
        }
        return results;
    }

    int tableEntries(String tablename) throws SQLException {
        int E = 0;
        String sql = "Select * from " + tablename + "";
        ps = conn.prepareStatement(sql);
        for (rs = ps.executeQuery(); rs.next();) {
            E++;
        }
        return E;
//    item=126693;  form=16998;  word=226628; link=161705;  
//    starters=340;  has_translation=10414; mappings=97410;
//        conversion1=115424;  authorship=168876;  config=1; 

    }

    ArrayList getColumn_inTable(String tablename, String columnname) throws SQLException {
        results = new ArrayList();
        String s1 = "";
        String sql = "SELECT " + columnname + " FROM " + tablename + "";
        ps = conn.prepareStatement(sql);
        for (rs = ps.executeQuery(); rs.next(); results.add(s1)) {
            s1 = rs.getString(1);
        }

        return results;
    }

    public ArrayList<String> getSynset_noDiacritics(String word) {

        try {
            results = new ArrayList();
            ps = conn.prepareStatement("SELECT synsetid FROM word WHERE value LIKE ?");
            ps.setString(1, word);
            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("synsetid"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return results;
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

    public String vector2csv(ArrayList words) {
        if (words == null || words.size() == 0) {
            return "";
        }
        StringBuffer result = new StringBuffer(((String) words.get(0))
                .replaceAll("_", " "));
        for (int i = 1; i < words.size(); i++) {
            result.append(',');
            result.append(new StringBuffer(((String) words.get(i)).replaceAll(
                    "_", " ")));
        }
        if (result.length() > 140) {
            return result.substring(0, 140) + "...";
        }
        return result.toString();
    }

    void startSigma() throws IOException {
        KB kb;
        Set kbs;
        String kbName = "SUMO";

        KBmanager.getMgr().initializeOnce();
        if (KBmanager.getMgr().getKBnames() == null && KBmanager.getMgr().getKBnames().size() <= 0) {
            JOptionPane.showMessageDialog(null, "No Knowledge Bases Found!", null, JOptionPane.WARNING_MESSAGE);
        } else {
            System.out.println("INFO: KBS found:" + KBmanager.getMgr().getKBnames().size());
            System.out.println("INFO: Got KB names.");

            kb = (KB) KBmanager.getMgr().getKB(kbName);
            kbs = KBmanager.getMgr().getKBnames();

        }
    }

    void addKB(String name, String path) throws IOException {
        KBmanager.getMgr().addKB(name);
//        Set kbs = KBmanager.getMgr().getKBnames();
        KB kb = (KB) KBmanager.getMgr().getKB("SUMO");
        kb.addConstituent(path);
//        System.out.println("Number of Terms is :" + kb.getCountAxioms());
    }

    KB getKB() throws IOException {
        addKB("SUMO", "F:\\Master\\Thesis\\Tools\\AWN\\KBs\\Merge.kif");
        return (KB) KBmanager.getMgr().getKB("SUMO");
    }

    ArrayList<String> getSynsetData(String synsetid) {

        results = new ArrayList<>();
        try {
            ps = conn.prepareStatement("SELECT gloss,pos,pwnid FROM item WHERE itemid = ?");
            ps.setString(1, synsetid);
            rs = ps.executeQuery();
            while (rs.next()) {
                String gloss = rs.getString("gloss");
                results.add(gloss);
                String pos = rs.getString("pos");
                results.add(pos);
                String pwnid = rs.getString("pwnid");
                results.add(pwnid);
            }

        } catch (SQLException e) {
            System.err.println("Error in  getSynsetData statement");
        }
        return results;
    }

    public String getTranslation(String synsetid, String translationLanguage) throws SQLException {
        String result = "";
        switch (translationLanguage) {
            case "Arabic":
                ps = conn.prepareStatement("SELECT link1 FROM link WHERE link2= ? AND type='equivalent'");
                ps.setString(1, synsetid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    result = rs.getString("link1");
                }
                break;
            case "English":
                ps = conn.prepareStatement("SELECT link2 FROM link WHERE link1= ? AND type='equivalent'");
                ps.setString(1, synsetid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    result = rs.getString("link2");
                }
                break;
        }

        return result;
    }

    public String getOntologyID(String pwnid) throws SQLException {
        String result = "";
        ps = conn.prepareStatement("SELECT mappings.sumoid FROM item,mappings WHERE item.pwnid=? AND item.pwnid=mappings.pwnid");
        ps.setString(1, pwnid);
        rs = ps.executeQuery();
        while (rs.next()) {
            result = rs.getString("sumoid");
        }
        return result;
    }

    public static void main(String[] args) throws SQLException, InstantiationException, ClassNotFoundException, IllegalAccessException, IOException {

        DerbyDB_main dbmain = new DerbyDB_main();
        dbmain.connectionToDB();
        //get all tables
//         DatabaseMetaData metaData = dbmain.conn.getMetaData();
//         dbmain.rs=metaData.getTables(null, null, "%", null);
//         while(dbmain.rs.next()){
//             System.out.println(dbmain.rs.getString(3));
//         }
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
        //get translation an item
//        String eng = dbmain.getTranslation("kataba_v1AR", "English");
//        System.out.println(eng);
        //get english synset for an item;
//        ArrayList<String> results = dbmain.getSynsetData("Yemen_n1EN");
//
//        for (String s : results) {
//            System.out.println(s);
//        }
        // get Ontology ID for an pwid number
//        String ontoID=dbmain.getOntologyID(results.get(2));
//        System.out.println(ontoID);
//get an ontology node childern
        ArrayList<String> firstLevelEntities = Graph.createGraph(dbmain.getKB(), "Group", "subclass", 0, 1, "Agent");
        for (String s : firstLevelEntities) {
            System.out.println(s);
        }
//      
//        dbmain.startSigma();
        //-----------------------------------------------------
//        PreparedStatement ps = dbmain.conn.prepareStatement("Select * from word");
//        ResultSet rs2 = ps.executeQuery();
//        ResultSetMetaData meta = rs2.getMetaData();
//        int columns = meta.getColumnCount();
//        for (int i = 1; i <= columns; i++) {
//            System.out.println(meta.getColumnName(i));
//        }
//       // String lastRow=
////                rs2.last();
//                int size=rs2.getRow();
//                rs2.beforeFirst();
//        System.out.println(size);
        //********************************************************
//        PreparedStatement ps;
//        ResultSet rs;
//        String s1 = "";
//        ps = dbmain.conn.prepareStatement("SELECT * FROM word");
//
////        for(rs=ps.executeQuery(); rs.next(); results.add(s1)){
////            s1=rs.getString(1);
////            System.out.println(s1);
////        }
//        rs = ps.executeQuery();
//        for (int i = 0; i < 100; i++) {
//            rs.next();
//            System.out.println(rs.getString(1) + "\t" + rs.getString(2));
//        }
        //*************************************************************
//        ArrayList needs=dbmain.getColumn_inTable("word","value");
//        JOptionPane.showMessageDialog(null, needs.size());
//        for(int i=0;i<needs.size();i++){
//             System.out.println(needs.get(i));
//        }
        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//        System.out.println(dbmain.tableEntries("config"));
//        BuckwalterToArabic b2a = new BuckwalterToArabic();
        //-----------------------------------------------------
        //Find senses JButton
//        ArrayList needs = new ArrayList(dbmain.getSynset_noDiacritics("ك%ت%ب%"));
//        for (int i = 0; i < needs.size(); i++) {
//            System.out.println(needs.get(i).toString() + "\t" + b2a.transliterate(needs.get(i).toString().split("_")[0]));
//        }
        //------------ get synset memebers for specific synsetID;
//        String[] synsetID={"kitaAbap_n1AR","makotuwb_a1AR"};
//        ArrayList synsetvalue = new ArrayList(dbmain.readSynsetMembers(synsetID[0]));
//        for(int i=0;i<synsetvalue.size();i++)
//                           System.out.println(synsetvalue.get(i).toString());

        //------------------------------------------------------
        //buckwalter to arabic
//        String[] buck={"kitaAb","kitaAbap"};
//        String walter=b2a.transliterate(buck[0]);
//        System.out.println(walter);
        //-----------------------------------------------------
        // sigma
        //----------------------------------------------------
//       JOptionPane.showMessageDialog(null, dbmain.conn.getSchema()); 
        dbmain.closeDB();
    }

}
