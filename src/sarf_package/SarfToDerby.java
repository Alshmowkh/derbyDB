package sarf_package;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import sarf.ui.controlpane.GerundSelectionUI;
import static sarf_package.Utiles.closeConnection;
import static sarf_package.Utiles.commit;
import static sarf_package.Utiles.conn;
import static sarf_package.Utiles.openConnection;
import static sarf_package.Utiles.pl;
import static sarf_package.Utiles.ps;
import static sarf_package.Utiles.rs;

public class SarfToDerby {
    
    Map<Integer, String> getRoots() {
        Map<Integer, String> roots = new HashMap();
        try {
            String root;
            int root_id;
            String stmt = "select root_id,root from roots";
            ps = conn.prepareStatement(stmt);
            rs = ps.executeQuery();
            while (rs.next()) {
                root = rs.getString("root");
                root_id = rs.getInt("root_id");
                roots.put(root_id, root);
            }
        } catch (Exception e) {
            
        }
        return roots;
    }
    
    Map<Integer, String> getRoots(int seek) {
        Map<Integer, String> roots = new HashMap();
        try {
            String root;
            int root_id;
            String stmt = "select root_id,root from roots where root_id >=" + seek + "";
            ps = conn.prepareStatement(stmt);
            rs = ps.executeQuery();
            while (rs.next()) {
                root = rs.getString("root");
                root_id = rs.getInt("root_id");
                roots.put(root_id, root);
            }
        } catch (Exception e) {
            
        }
        return roots;
    }
    
    private Root getSarfRoot(int rootId, String rootText) {
        Root root = null;
        if (rootText.length() == 3) {
            root = new TriRoot(rootId, rootText);
        } else if (rootText.length() == 4) {
//            pl("وصلت عملية التفريغ الى الجذر الرباعي.");
            root = new QuadRoot(rootId, rootText);
        }
        
        return root;
    }
    
    void ini() {
        
        openConnection();
        //verbs 1927; //gerund 1114
        //2100
        //startpoint quadroots 5675
        //-----------------------
        Map<Integer, String> roots = getRoots(5675);
//        roots.
//        boolean exported;
        Root root;

        for (Map.Entry<Integer, String> entry : roots.entrySet())
        {
            root = getSarfRoot(entry.getKey(), entry.getValue());

//            root = getSarfRoot(5675, roots.get(5675));
//            pl(roots.get(5675));
            root.insertVG();

//            commit();
//            unaugmentedConjs = root.unaugmentedConjs();
//            augmentedConjs = root.augmentedConjs();
//            exported = exportRoot_VG(root, unaugmentedConjs, augmentedConjs);
            pl(entry.getKey() + " : " + entry.getValue());
        }
        //----------------------
        closeConnection();
    }
    
    public static void main(String[] args) {
        SarfToDerby expr = new SarfToDerby();
        
        expr.ini();
//        expr.test();
//        expr.selectForm();

    }
    
    void selectForm() {
        GerundSelectionUI gs = new GerundSelectionUI();
        
        TrilateralAugmentedGerundConjugator tagc = TrilateralAugmentedGerundConjugator.getInstance();
        tagc.setListener(gs);
        tagc.setAugmentedTrilateralModifierListener(gs);
//        for(int i=1;i<13;i++)
        pl(tagc.selectPatternFormNo(2));
    }
    
    void test() {
        openConnection();
        try {
            rs = conn.prepareStatement("select max(verb_id) from verbs").executeQuery();
//            rs.next();
//            while (rs.next());

            pl(rs.next());
            
        } catch (SQLException ex) {
            pl("errors in ");
        }
        closeConnection();
    }
    
    private void performance() {
//        long start, end;
//
//        openConnection();
//
//        //-----------------------
//        Map<Integer, String> roots = getRoots();
//
////        boolean exported;
//        Root root;
//
////        for (Map.Entry<Integer, String> entry : roots.entrySet()) 
//        {
////            root = getSarfRoot(entry.getKey(), entry.getValue());
//            start = System.nanoTime();
//            root = getSarfRoot(404, "خدم");
//
//            root.insertVG();
//
//            end = System.nanoTime();
//            pl((end - start) / 1000000 + "  millisecond");
//        }
    }
}
