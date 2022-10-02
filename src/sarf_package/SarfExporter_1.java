package sarf_package;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sarf.AugmentationFormula;
import sarf.SarfDictionary;
import sarf.SystemConstants;
import sarf.gerund.modifier.trilateral.augmented.standard.TitlateralAugmentedStandardModifier;
import sarf.gerund.modifier.trilateral.unaugmented.meem.TitlateralUnaugmentedMeemModifier;
import sarf.gerund.modifier.trilateral.unaugmented.nomen.TitlateralUnaugmentedNomenModifier;
import sarf.gerund.modifier.trilateral.unaugmented.quality.TitlateralUnaugmentedQualityModifier;
import sarf.gerund.modifier.trilateral.unaugmented.standard.UnaugmentedTrilateralStandardGerundModifier;
//import sarf.gerund.trilateral.augmented.TrilateralAugmentedGerundConjugator;
//import sarf.gerund.trilateral.augmented.nomen.TrilateralAugmentedNomenGerundConjugator;
import sarf.gerund.trilateral.unaugmented.QualityGerundConjugator;
import sarf.gerund.trilateral.unaugmented.TrilateralUnaugmentedGerundConjugator;
import sarf.gerund.trilateral.unaugmented.TrilateralUnaugmentedNomenGerundConjugator;
import sarf.gerund.trilateral.unaugmented.meem.MeemGerundConjugator;
import sarf.kov.KovRulesManager;
import sarf.kov.QuadrilateralKovRule;
import sarf.kov.TrilateralKovRule;
import sarf.noun.TrilateralUnaugmentedNouns;
import sarf.noun.trilateral.augmented.AugmentedTrilateralActiveParticipleConjugator;
import sarf.noun.trilateral.augmented.AugmentedTrilateralPassiveParticipleConjugator;
import sarf.noun.trilateral.unaugmented.UnaugmentedTrilateralActiveParticipleConjugator;
import sarf.noun.trilateral.unaugmented.UnaugmentedTrilateralPassiveParticipleConjugator;
//import sarf.noun.trilateral.unaugmented.assimilate.AssimilateAdjectiveConjugator;
import sarf.noun.trilateral.unaugmented.elative.ElativeNounConjugator;
import sarf.noun.trilateral.unaugmented.exaggeration.NonStandardExaggerationConjugator;
import sarf.noun.trilateral.unaugmented.exaggeration.StandardExaggerationConjugator;
//import sarf.noun.trilateral.unaugmented.instrumental.NonStandardInstrumentalConjugator;
import sarf.noun.trilateral.unaugmented.instrumental.StandardInstrumentalConjugator;
import sarf.noun.trilateral.unaugmented.modifier.activeparticiple.ActiveParticipleModifier;
import sarf.noun.trilateral.unaugmented.modifier.assimilate.AssimilateModifier;
import sarf.noun.trilateral.unaugmented.modifier.elative.ElativeModifier;
import sarf.noun.trilateral.unaugmented.modifier.exaggeration.ExaggerationModifier;
import sarf.noun.trilateral.unaugmented.modifier.instrumental.InstrumentalModifier;
import sarf.noun.trilateral.unaugmented.modifier.passiveparticiple.PassiveParticipleModifier;
import sarf.noun.trilateral.unaugmented.modifier.timeandplace.TimeAndPlaceModifier;
import sarf.verb.quadriliteral.augmented.AugmentedQuadriliteralRoot;
import sarf.verb.quadriliteral.unaugmented.UnaugmentedQuadriliteralRoot;
//import sarf.noun.trilateral.unaugmented.timeandplace.TimeAndPlaceConjugator;
import sarf.verb.trilateral.unaugmented.passive.PassivePastConjugator;
import sarf.verb.trilateral.augmented.AugmentedTrilateralRoot;
import sarf.verb.trilateral.augmented.active.past.AugmentedActivePastConjugator;
import sarf.verb.trilateral.augmented.active.present.AugmentedActivePresentConjugator;
import sarf.verb.trilateral.augmented.imperative.AugmentedImperativeConjugator;
import sarf.verb.trilateral.augmented.modifier.AugmentedTrilateralModifier;
import sarf.verb.trilateral.augmented.passive.past.AugmentedPassivePastConjugator;
import sarf.verb.trilateral.augmented.passive.present.AugmentedPassivePresentConjugator;
import sarf.verb.trilateral.unaugmented.ConjugationResult;
import sarf.verb.trilateral.unaugmented.UnaugmentedImperativeConjugator;
import sarf.verb.trilateral.unaugmented.UnaugmentedTrilateralRoot;
import sarf.verb.trilateral.unaugmented.active.ActivePastConjugator;
import sarf.verb.trilateral.unaugmented.active.ActivePresentConjugator;
import sarf.verb.trilateral.unaugmented.modifier.UnaugmentedTrilateralModifier;
import sarf.verb.trilateral.unaugmented.passive.PassivePresentConjugator;

public class SarfExporter_1 {

    ArrayList results;
    PreparedStatement ps;
    Connection conn;
    ResultSet rs;

    public SarfExporter_1() {

    }

    public ArrayList<Character> diacriticList() {
        ArrayList<Character> marks = new ArrayList();
        marks.add('َ');//فتحة
        marks.add('ِ');//كسرة
        marks.add('ُ');//ضمة
        marks.add('ْ');//سكون
        marks.add('ّ');//شدة
        marks.add('ٌ');//تنوين مضموم
        marks.add('ً');//تنوين مفتوح
        marks.add('ٍ');//تنوين مكسور

        return marks;
    }

    String deDiacritic(String diac) {
        ArrayList<Character> marks = new ArrayList();
        marks.add('َ');//فتحة
        marks.add('ِ');//كسرة
        marks.add('ُ');//ضمة
        marks.add('ْ');//سكون
        marks.add('ّ');//شدة
        marks.add('ٌ');//تنوين مضموم
        marks.add('ً');//تنوين مفتوح
        marks.add('ٍ');//تنوين مكسور

        char[] chars = diac.toCharArray();
        String res = "";
        for (Character ch : chars) {
            if (!marks.contains(ch)) {
                res = res + ch;
            }
        }
        return res;
    }

    List<String> importRoots() throws SAXException, IOException, ParserConfigurationException {
        List<String> rawroots = new ArrayList();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document docimport = db.parse(new File("F:/Master/Thesis/Implementations/SARF_10/ALshmowkh_db/TrilateralRootsNoDuplicate.xml"));
        NodeList rootsNodes = docimport.getElementsByTagName("root");
        for (int i = 0; i < rootsNodes.getLength(); i++) {
            String rot = rootsNodes.item(i).getAttributes().getNamedItem("value").getNodeValue();
            if (!rawroots.contains(rot)) {
                rawroots.add(rot);
            }
        }
        docimport = db.parse(new File("F:/Master/Thesis/Implementations/SARF_10/ALshmowkh_db/QuadRoots.xml"));
        rootsNodes = docimport.getElementsByTagName("root");
        for (int i = 0; i < rootsNodes.getLength(); i++) {
            String rot = rootsNodes.item(i).getAttributes().getNamedItem("value").getNodeValue();
            if (!rawroots.contains(rot)) {
                rawroots.add(rot);
            }
        }
        return rawroots;
    }

    void createNewDB(String name) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + name + ";create=true");

    }

    void getAllDBTables(String dbname) throws SQLException {
//get all tables
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        DatabaseMetaData metaData = conn.getMetaData();
        rs = metaData.getTables(null, null, "%", null);
        while (rs.next()) {
            System.out.println(rs.getString(3));
        }
        //OR
//        ps = conn.prepareStatement("select TABLENAME from SYS.SYSTABLES where TABLETYPE='T'");
//        rs = ps.executeQuery();
//        while (rs.next()) {
//            System.out.println(rs.getString(1));
//        }
    }

    Boolean createNewTable(String name) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String stmt = "CREATE TABLE " + name + " (ID int primary key,root varchar(3) unique)";
        Statement s = conn.createStatement();
        return s.execute(stmt);
    }

    Boolean createRootsTable() throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_10");
        String name = "roots";
        String stmt = "CREATE TABLE " + name + " (root_id int primary key not null,root varchar(4) unique,abstractForm varchar(25))";
        Statement s = conn.createStatement();
        return s.execute(stmt);
    }

    Boolean createVerbsTable() throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String name = "verbs";
        String stmt = "CREATE TABLE " + name + " (verb_id INT primary key,"
                + "verb varchar(20),"
                + "active boolean,"
                + "tense char(1),"
                + "pronoun SMALLINT,"
                + "root_id INT NOT NULL,"
                + "conjugation_no INT NOT NULL,"
                + "FOREIGN KEY (root_id,conjugation_no) REFERENCES conjugations (root_id,conjugation_no))";
        Statement s = conn.createStatement();
        return s.execute(stmt);
    }

    Boolean createVerbsTable2() throws SQLException {
        String name = "verbs";
//        ps = conn.prepareStatement("drop table " + name);
//        ps.execute();
        String stmt = "CREATE TABLE " + name + " (verb_id INT primary key,"
                + "verb varchar(20),"
                + "verb_NonDiac varchar(10),"
                + "active boolean,"
                + "tense char(1),"
                + "pronoun char(3),"
                + "root_id INT NOT NULL,"
                + "conjugation_no INT NOT NULL,"
                + "FOREIGN KEY (root_id,conjugation_no) REFERENCES conjugations (root_id,conjugation_no))";
        Statement s = conn.createStatement();
        return s.execute(stmt);
    }

    Boolean createNounsTable() throws SQLException {

//        Connection conn2 = DriverManager.getConnection("jdbc:derby:" + "sarfDB_02" + ";create=true");
//        conn2.prepareStatement("drop table gerunds").execute();
        String name = "nouns";
        String stmt = "CREATE TABLE " + name + " (noun_id INT primary key,"
                + "noun varchar(20) not null,"
                + "noun_noDiac varchar(10),"
                + "nominal char(1)  CHECK  (nominal in ('S','O','E','I','T','L','A')),"
                + "mood char(1) check (mood in ('N','A','J')),"
                + "number char(1) check (number in ('S','D','P')),"
                + "gender char(1) check  (gender in ('M','F')),"
                + "root_id INT NOT NULL,"
                + "conjugation_no INT NOT NULL)";
//                + "FOREIGN KEY (root_id,conjugation_no) REFERENCES conjugations (root_id,conjugation_no))";
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");

        Statement s = conn.createStatement();
        return s.execute(stmt);
    }

    Boolean deleteTable(String nameTable) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_10");
        String stmt = "DROP table " + nameTable;
        Statement s = conn.createStatement();
        return s.execute(stmt);
    }

    Boolean createConjugationTable() throws SQLException {
        String stmt = "CREATE TABLE conjugations (root_id INT NOT NULL,"
                + "conjugation_no INT NOT NULL,"
                + "transitivity char(1),"
                + "PRIMARY KEY (root_id,conjugation_no),"
                + "FOREIGN KEY (root_id) REFERENCES roots (root_id))";
        Statement s = conn.createStatement();
        return s.execute(stmt);
    }

    void insertionData(int id, String value) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String stmt = "insert into tri_roots3 values (" + id + ",'" + value + "' )";
        ps = conn.prepareStatement(stmt);
        ps.execute();
//        conn.commit();
        pl(id + ": " + value);

    }

    void insertionRoot(int rootId, String root, String abstractForm) throws SQLException {
        String stmt = "insert into roots values (" + rootId + ",'" + root + "','" + abstractForm + "')";
        ps = conn.prepareStatement(stmt);
        ps.execute();
        conn.commit();
//        pl(rootId + " : " + root + " : " + quantity + " : " + abstractForm);

    }

    void insertionRoots() throws SAXException, IOException, ParserConfigurationException, SQLException {
        List<String> roots = importRoots();
        int rootId = 0;
        String abstractForm = "0";
        for (int i = 0; i < roots.size(); i++) {
            String rootIn = roots.get(i);
            if (rootIn.length() == 3) {
                abstractForm = KovRulesManager.getInstance().getTrilateralKovRule(rootIn.charAt(0), rootIn.charAt(1), rootIn.charAt(2)).getDesc();
            }
            if (rootIn.length() == 4) {
                abstractForm = KovRulesManager.getInstance().getQuadrilateralKovRule(rootIn.charAt(0), rootIn.charAt(1), rootIn.charAt(2), rootIn.charAt(3)).getDesc();
            }

            insertionRoot(i + 1, rootIn, abstractForm);

            pl(rootIn + " : " + rootId + " : " + abstractForm);
        }
    }

    void insertionConj(int rootId, int conjugationNo, char transitivity) throws SQLException {
        String stmt = "insert into conjugations  values (" + rootId + "," + conjugationNo + ",'" + transitivity + "')";
        ps = conn.prepareStatement(stmt);
        ps.execute();
        conn.commit();
        pl(rootId + " : " + conjugationNo);
    }

    Boolean deleteTuple(int id) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");

        Statement st = conn.createStatement();
        String stmt = "delete from conjugations";
        return st.execute(stmt);

    }

    Boolean deleteData(String stmt) throws SQLException {

        Statement st = conn.createStatement();
        return st.execute(stmt);

    }

    void selectData(String name) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");

        String stmt = "select * from " + name;
        ps = conn.prepareStatement(stmt);
        rs = ps.executeQuery();

        while (rs.next()) {
//            String value = rs.getString("past");
//            String id = rs.getString("present");
//            pl(id + ": " + value);
            pl(rs.getInt("root_id") + ":" + rs.getInt("conjugation_no") + ":" + rs.getString(3));
        }
    }

    void selectStmt(String stmt, String field) throws SQLException {

        ps = conn.prepareStatement(stmt);
        if (!field.isEmpty()) {
            ps.setString(1, field);
        }
        rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            count++;
//            String first = rs.getString(1);
//            String second = rs.getString(2);
//            String third = rs.getString(3);
////            pl(id + ": " + value);
//            pl(second);
//            pl(first + " : " + second + " : " + third);
        }
        pl(count);
    }

    String getRoot(String name) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String value = "";
        String stmt = "select * from tri_roots2 where root=?";

        ps = conn.prepareStatement(stmt);
        ps.setString(1, name);
        rs = ps.executeQuery();
        System.out.print(name);
        while (rs.next()) {
            value = rs.getString("root");
//            int id = rs.getInt("id");
            pl(": " + value);

        }
        return value;
    }

    void fullConjugations() throws SQLException {
        Map<Integer, String> roots = getRoots();
        for (Map.Entry<Integer, String> entry : roots.entrySet()) {
            fullRootConjs(entry.getKey(), entry.getValue());
//            pl(entry.getKey() + " : " + entry.getValue());

        }
    }

    void fullRootConjs(int rootId, String rootText) throws SQLException {
        if (rootText.length() == 3) {
            this.fullTriConjs(rootId, rootText);
        } else if (rootText.length() == 4) {
            this.fullQuadConjs(rootId, rootText);
        }
    }

    void fullQuadConjs(int rootId, String rootText) throws SQLException {
        QuadrilateralKovRule rule = KovRulesManager.getInstance().getQuadrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2), rootText.charAt(3));
        int kov = rule.getKov();
        int conjNo;
        UnaugmentedQuadriliteralRoot unaugmentedRoot4 = SarfDictionary.getInstance().getUnaugmentedQuadrilateralRoot(rootText);

        if (unaugmentedRoot4 != null) {
            insertionConj(rootId, 1, unaugmentedRoot4.getTransitive().charAt(0));

            //Active verb 
            insertVerbs(rootId, 1, 'P', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePastConjugator.getInstance().createVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PAST_TENSE));
            insertVerbs(rootId, 1, 'N', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createNominativeVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PRESENT_TENSE));
            insertVerbs(rootId, 1, 'A', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createAccusativeVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PRESENT_TENSE));
            insertVerbs(rootId, 1, 'J', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createJussiveVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PRESENT_TENSE));
            insertVerbs(rootId, 1, 'E', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createEmphasizedVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PRESENT_TENSE));
            insertVerbs(rootId, 1, 'I', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.UnaugmentedImperativeConjugator.getInstance().createVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE));
            insertVerbs(rootId, 1, 'M', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.UnaugmentedImperativeConjugator.getInstance().createEmphasizedVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE));
            //Passive verb
            insertVerbs(rootId, 1, 'P', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePastConjugator.getInstance().createVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PAST_TENSE));
            insertVerbs(rootId, 1, 'N', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createNominativeVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PRESENT_TENSE));
            insertVerbs(rootId, 1, 'A', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createAccusativeVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PRESENT_TENSE));
            insertVerbs(rootId, 1, 'J', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createJussiveVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PRESENT_TENSE));
            insertVerbs(rootId, 1, 'E', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createEmphasizedVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PRESENT_TENSE));
        }

        AugmentedQuadriliteralRoot augmentedRoot4 = SarfDictionary.getInstance().getAugmentedQuadrilateralRoot(rootText);
        AugmentationFormula formula;
        Iterator itr;

        if (augmentedRoot4 != null) {

            itr = augmentedRoot4.getAugmentationList().iterator();
            while (itr.hasNext()) {
                formula = (AugmentationFormula) itr.next();
                conjNo = formula.getFormulaNo() + 1;
                insertionConj(rootId, conjNo, formula.getTransitive());

                //active verbs  
                insertVerbs(rootId, conjNo, 'P', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.past.AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PAST_TENSE));
                insertVerbs(rootId, conjNo, 'N', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, conjNo, 'A', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, conjNo, 'J', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, conjNo, 'E', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, conjNo, 'I', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.imperative.AugmentedImperativeConjugator.getInstance().getNotEmphsizedConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE));
                insertVerbs(rootId, conjNo, 'M', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.imperative.AugmentedImperativeConjugator.getInstance().getEmphsizedConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE));
                //passive verbs;
                insertVerbs(rootId, conjNo, 'P', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.past.AugmentedPassivePastConjugator.getInstance().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PAST_TENSE));
                insertVerbs(rootId, conjNo, 'N', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, conjNo, 'A', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, conjNo, 'J', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, conjNo, 'E', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PRESENT_TENSE));
            }
        }
    }

    void fullTriConjs(int rootId, String rootText) throws SQLException {
//        List<String> conjugations = new ArrayList();

        int kov = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2)).getKov();
        int preConj = 0;
        int conjNo;
        List unaugmentedList = SarfDictionary.getInstance().getUnaugmentedTrilateralRoots(rootText);
        Iterator itr1 = unaugmentedList.iterator();
        while (itr1.hasNext()) {
            UnaugmentedTrilateralRoot sarfRoot = (UnaugmentedTrilateralRoot) itr1.next();
            int posConj = Integer.parseInt(sarfRoot.getConjugation());
            if (preConj != posConj) {
                insertionConj(rootId, posConj, sarfRoot.getTransitive().charAt(0));
                //active
                insertVerbs(rootId, posConj, 'P', new RootInfo(sarfRoot, ActivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.PAST_TENSE));
                insertVerbs(rootId, posConj, 'N', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, posConj, 'A', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, posConj, 'J', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, posConj, 'E', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, posConj, 'I', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE));
                insertVerbs(rootId, posConj, 'M', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE));
                //passive
                insertVerbs(rootId, posConj, 'P', new RootInfo(sarfRoot, PassivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, false, SystemConstants.PAST_TENSE));
                insertVerbs(rootId, posConj, 'N', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, posConj, 'A', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, posConj, 'J', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs(rootId, posConj, 'E', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));

                preConj = posConj;
            }
        }

        AugmentedTrilateralRoot augmentedRoot = SarfDictionary.getInstance().getAugmentedTrilateralRoot(rootText);
        if (augmentedRoot != null) {
            itr1 = augmentedRoot.getAugmentationList().iterator();
            while (itr1.hasNext()) {
                AugmentationFormula formula = (AugmentationFormula) itr1.next();
                int formulaNo = formula.getFormulaNo();
                conjNo = formulaNo + 6;
                insertionConj(rootId, conjNo, formula.getTransitive());

                //active verbs
                insertVerbs(rootId, conjNo, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                insertVerbs(rootId, conjNo, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                insertVerbs(rootId, conjNo, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                insertVerbs(rootId, conjNo, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                insertVerbs(rootId, conjNo, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                insertVerbs(rootId, conjNo, 'I', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getNotEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                insertVerbs(rootId, conjNo, 'M', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                // passive verbs
                insertVerbs(rootId, conjNo, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                insertVerbs(rootId, conjNo, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                insertVerbs(rootId, conjNo, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                insertVerbs(rootId, conjNo, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                insertVerbs(rootId, conjNo, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));

            }
        }
    }

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

    Map<Integer, List<Integer>> getConjugations() {
        Map<Integer, List<Integer>> conjsRoots = new HashMap();
        List<Integer> conjs;// = new ArrayList();
        int rootCn = this.getRoots().keySet().size();
//        pl(rootCn);
        try {
            conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
            for (int i = 1; i <= rootCn; i++) {
                int root_id, conj_no;

                String stmt = "select conjugation_no from conjugations where root_id=" + i + "";
                ps = conn.prepareStatement(stmt);
                rs = ps.executeQuery();
                conjs = new ArrayList();
                while (rs.next()) {
                    conjs.add(rs.getInt(1));
                }
//                pl(i + " : " + conjs);
                conjsRoots.put(i, conjs);
            }

        } catch (Exception e) {

        }
        return conjsRoots;
    }

    void insertVerb(int verbID, String verb, String noDiac, Boolean active, char tense, String pronoun, int rootId, int conjNo) throws SQLException {

        String stmt = "insert into verbs  values(" + verbID + ",'" + verb + "','" + noDiac + "','" + active + "','" + tense + "','" + pronoun + "'," + rootId + "," + conjNo + ")";

        ps = conn.prepareStatement(stmt);
        ps.execute();
        conn.commit();
        pl(verb + ": " + rootId);
    }

    void insertVerbs(int rootId, int conjNo, char tense, RootInfo rootInfo) throws SQLException {
        List finalresult = null;

        if (rootInfo.isTrilateral()) {
            if (!rootInfo.isAugmented()) {
                UnaugmentedTrilateralRoot sarfroot = (UnaugmentedTrilateralRoot) rootInfo.getRoot();

                ConjugationResult conjResult = UnaugmentedTrilateralModifier.getInstance().build(sarfroot, rootInfo.getKov(), rootInfo.getList(), rootInfo.getTense(), rootInfo.isActive());
                finalresult = conjResult.getFinalResult();
            } else if (rootInfo.isAugmented()) {
                finalresult = rootInfo.getList();
            }
        } else {

            if (!rootInfo.isAugmented()) {
                UnaugmentedQuadriliteralRoot sarfroot4 = (UnaugmentedQuadriliteralRoot) rootInfo.getRoot();
                sarf.verb.quadriliteral.ConjugationResult conjResult4 = sarf.verb.quadriliteral.modifier.QuadrilateralModifier.getInstance().build(sarfroot4, rootInfo.getKov(), 0, rootInfo.getList(), rootInfo.getTense(), rootInfo.isActive());
                finalresult = conjResult4.getFinalResult();
                conjNo = 1;

            } else if (rootInfo.isAugmented()) {
                finalresult = rootInfo.getList();
            }
        }

        String verb;
        if (finalresult != null) {
            for (int p = 0; p < 13; p++) {
                Object o = finalresult.get(p);
                if (o != null) {
                    verbId++;
                    verb = o.toString();
                    insertVerb(verbId, verb, this.deDiacritic(verb), rootInfo.isActive(), tense, pronouns(p), rootId, conjNo);
                }
            }
        }
    }

    static int verbId = 0;

    int nounID = 571554;

    void importDerivedNouns() throws SQLException {
        //i<=5674
        String rootText = "";
        String formula = "";
        int rootId;
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        TrilateralKovRule rule;// = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
        int kov, conjNo;
        List unaugmentedList;
        List nouns, formulas;
        Iterator itr2;
        AugmentedTrilateralRoot augmentedRoot;
        for (int i = 2511; i <= 5674; i++) {
            rootId = i;
            ps = conn.prepareStatement("select root from roots where root_id=" + rootId + "");
            rs = ps.executeQuery();
            while (rs.next()) {
                rootText = rs.getString(1);
            }
            rule = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
            kov = rule.getKov();
            unaugmentedList = SarfDictionary.getInstance().getUnaugmentedTrilateralRoots(rootText);
            Iterator itr = unaugmentedList.iterator();
            UnaugmentedTrilateralRoot sarfRoot;
            while (itr.hasNext()) {
                sarfRoot = (UnaugmentedTrilateralRoot) itr.next();
                TrilateralUnaugmentedNouns nounsObject = new TrilateralUnaugmentedNouns(sarfRoot);

                formula = "فَاعِل";
                conjNo = Integer.parseInt(sarfRoot.getConjugation());
                insertNouns(ActiveParticipleModifier.getInstance().build(sarfRoot, kov, UnaugmentedTrilateralActiveParticipleConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'S', formula, rootId, conjNo);
                formula = "مَفْعُول";
                insertNouns(PassiveParticipleModifier.getInstance().build(sarfRoot, kov, UnaugmentedTrilateralPassiveParticipleConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'O', formula, rootId, conjNo);

                formulas = StandardExaggerationConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                itr2 = formulas.iterator();
                while (itr2.hasNext()) {
                    formula = itr2.next().toString();
                    insertNouns(ExaggerationModifier.getInstance().build(sarfRoot, kov, StandardExaggerationConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'E', formula, rootId, conjNo);
                }

                formulas = NonStandardExaggerationConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                if (nounsObject.getNonStandardExaggerations() != null && !nounsObject.getNonStandardExaggerations().isEmpty()) {
                    itr2 = formulas.iterator();
                    while (itr2.hasNext()) {
                        formula = itr2.next().toString();
                        insertNouns(ExaggerationModifier.getInstance().build(sarfRoot, kov, StandardExaggerationConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'E', formula, rootId, conjNo);
                    }
                }

                formulas = StandardInstrumentalConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                itr2 = formulas.iterator();
                while (itr2.hasNext()) {
                    formula = itr2.next().toString();
                    insertNouns(InstrumentalModifier.getInstance().build(sarfRoot, kov, StandardInstrumentalConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'I', formula, rootId, conjNo);
                }

                formulas = NonStandardInstrumentalConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                if (nounsObject.getNonStandardInstrumentals() != null && !nounsObject.getNonStandardInstrumentals().isEmpty()) {
                    itr2 = formulas.iterator();
                    while (itr2.hasNext()) {
                        formula = itr2.next().toString();
                        insertNouns(InstrumentalModifier.getInstance().build(sarfRoot, kov, NonStandardInstrumentalConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'I', formula, rootId, conjNo);
                    }
                }
                formulas = TimeAndPlaceConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                if (nounsObject.getTimeAndPlaces() != null && !nounsObject.getTimeAndPlaces().isEmpty()) {
                    itr2 = formulas.iterator();
                    while (itr2.hasNext()) {
                        formula = itr2.next().toString();
                        insertNouns(TimeAndPlaceModifier.getInstance().build(sarfRoot, kov, TimeAndPlaceConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'T', formula, rootId, conjNo);
                    }
                }

                formulas = ElativeNounConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                if (nounsObject.getElatives() != null && !nounsObject.getElatives().isEmpty()) {
                    itr2 = formulas.iterator();
                    while (itr2.hasNext()) {
                        formula = itr2.next().toString();
                        insertNouns(ElativeModifier.getInstance().build(sarfRoot, kov, ElativeNounConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'L', formula, rootId, conjNo);
                    }
                }

                formulas = AssimilateAdjectiveConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                if (nounsObject.getAssimilates() != null && !nounsObject.getAssimilates().isEmpty()) {
                    itr2 = formulas.iterator();
                    while (itr2.hasNext()) {
                        formula = itr2.next().toString();
                        insertNouns(AssimilateModifier.getInstance().build(sarfRoot, kov, AssimilateAdjectiveConjugator.getInstance().createNounList(sarfRoot, formula), formula).getFinalResult(), 'A', formula, rootId, conjNo);
                    }
                }
            }
//            
            augmentedRoot = SarfDictionary.getInstance().getAugmentedTrilateralRoot(rootText);
            if (augmentedRoot != null) {
                itr = augmentedRoot.getAugmentationList().iterator();
                AugmentationFormula formula2;
                int formulaNo;
                while (itr.hasNext()) {
                    formula2 = (AugmentationFormula) itr.next();
                    formulaNo = formula2.getFormulaNo();
                    insertNouns(sarf.noun.trilateral.augmented.modifier.activeparticiple.ActiveParticipleModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedTrilateralActiveParticipleConjugator.getInstance().createNounList(augmentedRoot, formulaNo), null).getFinalResult(), 'S', "فاعل", rootId, formulaNo + 6);
                    insertNouns(sarf.noun.trilateral.augmented.modifier.passiveparticiple.PassiveParticipleModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedTrilateralPassiveParticipleConjugator.getInstance().createNounList(augmentedRoot, formulaNo), null).getFinalResult(), 'O', "مفعول", rootId, formulaNo + 6);
                    insertNouns(sarf.noun.trilateral.augmented.modifier.passiveparticiple.PassiveParticipleModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedTrilateralPassiveParticipleConjugator.getInstance().createTimeAndPlaceNounList(augmentedRoot, formulaNo), null).getFinalResult(), 'T', "الزمان والمكان", rootId, formulaNo + 6);
                }
            }
        }
//            conn.commit();
//        pl(rootText + ": " + rootId);

//        conn.close();
    }

    boolean insertNoun(String noun, String noDiac, char nominal, String formula, char mood, char number, char gender, int rootId, int conjNo) throws SQLException {

//        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String stmt;

//        if (verb.toCharArray().length > 20) {
//            if (verb.contains("/")) {
//                String[] v2 = verb.split("/");
//                stmt = "insert into verbs values(" + vID + ",'" + v2[0] + "','" + active + "','" + tense + "'," + pronoun + "," + rootId + "," + conjNo + ")";
//                ps = conn.prepareStatement(stmt);
//                ps.execute();
//                verbId++;
//                vID=verbId;
//                stmt = "insert into verbs values(" + vID + ",'" + v2[1] + "','" + active + "','" + tense + "'," + pronoun + "," + rootId + "," + conjNo + ")";
//                ps = conn.prepareStatement(stmt);
//                ps.execute();
//                return;
//            }
//        }
        nounID++;
        stmt = "insert into nouns values(" + nounID + ",'" + noun + "','" + noDiac + "','" + nominal + "','" + mood + "','" + number + "','" + gender + "'," + rootId + "," + conjNo + ")";
        ps = conn.prepareStatement(stmt);
        ps.execute();
        conn.commit();
        pl(noun + ": " + rootId);
        return true;
    }

    void insertNouns(List nouns, char nominal, String formula, int rootId, int conjNo) throws SQLException {
//                this.insertNoun(nounId, word, number, nominal, mood, number, gender, rootId, conjNo);

//        pl(!gerunds.get(0).toString().trim().isEmpty() ? "Yes" : "NO");
        boolean done;
        done = (!nouns.get(0).toString().trim().isEmpty()) ? insertNoun(nouns.get(0).toString(), this.deDiacritic(nouns.get(0).toString()), nominal, formula, 'N', 'S', 'M', rootId, conjNo) : false;
        done = (!nouns.get(1).toString().trim().isEmpty()) ? insertNoun(nouns.get(1).toString(), this.deDiacritic(nouns.get(1).toString()), nominal, formula, 'N', 'S', 'F', rootId, conjNo) : false;
        done = (!nouns.get(2).toString().trim().isEmpty()) ? insertNoun(nouns.get(2).toString(), this.deDiacritic(nouns.get(2).toString()), nominal, formula, 'N', 'D', 'M', rootId, conjNo) : false;
        done = (!nouns.get(3).toString().trim().isEmpty()) ? insertNoun(nouns.get(3).toString(), this.deDiacritic(nouns.get(3).toString()), nominal, formula, 'N', 'D', 'F', rootId, conjNo) : false;
        done = (!nouns.get(4).toString().trim().isEmpty()) ? insertNoun(nouns.get(4).toString(), this.deDiacritic(nouns.get(4).toString()), nominal, formula, 'N', 'P', 'M', rootId, conjNo) : false;
        done = (!nouns.get(5).toString().trim().isEmpty()) ? insertNoun(nouns.get(5).toString(), this.deDiacritic(nouns.get(5).toString()), nominal, formula, 'N', 'P', 'F', rootId, conjNo) : false;
        done = (!nouns.get(6).toString().trim().isEmpty()) ? insertNoun(nouns.get(6).toString(), this.deDiacritic(nouns.get(6).toString()), nominal, formula, 'A', 'S', 'M', rootId, conjNo) : false;
        done = (!nouns.get(7).toString().trim().isEmpty()) ? insertNoun(nouns.get(7).toString(), this.deDiacritic(nouns.get(7).toString()), nominal, formula, 'A', 'S', 'F', rootId, conjNo) : false;
        done = (!nouns.get(8).toString().trim().isEmpty()) ? insertNoun(nouns.get(8).toString(), this.deDiacritic(nouns.get(8).toString()), nominal, formula, 'A', 'D', 'M', rootId, conjNo) : false;
        done = (!nouns.get(9).toString().trim().isEmpty()) ? insertNoun(nouns.get(9).toString(), this.deDiacritic(nouns.get(9).toString()), nominal, formula, 'A', 'D', 'F', rootId, conjNo) : false;
        done = (!nouns.get(10).toString().trim().isEmpty()) ? insertNoun(nouns.get(10).toString(), this.deDiacritic(nouns.get(10).toString()), nominal, formula, 'A', 'P', 'M', rootId, conjNo) : false;
        done = (!nouns.get(11).toString().trim().isEmpty()) ? insertNoun(nouns.get(11).toString(), this.deDiacritic(nouns.get(11).toString()), nominal, formula, 'A', 'P', 'F', rootId, conjNo) : false;
        done = (!nouns.get(12).toString().trim().isEmpty()) ? insertNoun(nouns.get(12).toString(), this.deDiacritic(nouns.get(12).toString()), nominal, formula, 'J', 'S', 'M', rootId, conjNo) : false;
        done = (!nouns.get(13).toString().trim().isEmpty()) ? insertNoun(nouns.get(13).toString(), this.deDiacritic(nouns.get(13).toString()), nominal, formula, 'J', 'S', 'F', rootId, conjNo) : false;
        done = (!nouns.get(14).toString().trim().isEmpty()) ? insertNoun(nouns.get(14).toString(), this.deDiacritic(nouns.get(14).toString()), nominal, formula, 'J', 'D', 'M', rootId, conjNo) : false;
        done = (!nouns.get(15).toString().trim().isEmpty()) ? insertNoun(nouns.get(15).toString(), this.deDiacritic(nouns.get(15).toString()), nominal, formula, 'J', 'D', 'F', rootId, conjNo) : false;
        done = (!nouns.get(16).toString().trim().isEmpty()) ? insertNoun(nouns.get(16).toString(), this.deDiacritic(nouns.get(16).toString()), nominal, formula, 'J', 'P', 'M', rootId, conjNo) : false;
        done = (!nouns.get(17).toString().trim().isEmpty()) ? insertNoun(nouns.get(17).toString(), this.deDiacritic(nouns.get(17).toString()), nominal, formula, 'J', 'P', 'F', rootId, conjNo) : false;

//        pl((!gerunds.get(0).toString().trim().isEmpty()) ? gerunds.get(0).toString() + " : " + this.deDiacritic(gerunds.get(0).toString()) + " : " + nominal + " : " + formula + " : " + 'N' + " : " + 'S' + " : " + 'M' + " : " + rootId + " : " + conjNo : "0");
//        insertNoun(gerunds.get(1).toString(), this.deDiacritic(gerunds.get(1).toString()), nominal, formula, 'N', 'S', 'F', rootId, conjNo);
    }

    void showTableSchema(String tablename) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:sarfDB_10");
        ps = conn.prepareStatement("select COLUMNNAME,COLUMNDATATYPE FROM sys.systables t, sys.syscolumns WHERE TABLEID=REFERENCEID and tablename='" + tablename + "'");
//        ps = conn.prepareStatement("select TABLENAME from SYS.SYSTABLES where TABLETYPE='T'");
        rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    boolean createGerundTable() throws SQLException {

//        Connection conn2 = DriverManager.getConnection("jdbc:derby:" + "sarfDB_02" + ";create=true");
//        conn2.prepareStatement("drop table gerunds").execute();
//stmt = "insert into gerunds values(" + gerundId + ",'" + gerund + "','" + noDiac + "','" + nominal + "','" + formula + "','" + mood + "','" + number + "','" + gender + "'," + rootId + "," + conjNo + ")";
        String name = "gerunds";
        String stmt = "CREATE TABLE " + name + " (gerund_id INT primary key,"
                + "gerund varchar(20) not null,"
                + "gerund_noDiac varchar(10),"
                + "nominal char(1)  CHECK  (nominal in ('O','M','N','Q')),"
                + "formula varchar(15),"
                + "mood char(1) check (mood in ('N','A','J')),"
                + "number char(1) check (number in ('S','D','P')),"
                + "gender char(1) check  (gender in ('M','F')),"
                + "root_id INT NOT NULL,"
                + "conjugation_no INT NOT NULL,"
                + "FOREIGN KEY (root_id,conjugation_no) REFERENCES conjugations (root_id,conjugation_no))";
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_10");
//              + "FOREIGN KEY (root_id,conjugation_no) REFERENCES conjugations (root_id,conjugation_no))";

        Statement s = conn.createStatement();
        return s.execute(stmt);
    }
    int gerundID = 0;

    boolean insertGerund(String gerund, String noDiac, char nominal, String formula, char mood, char number, char gender, int rootId, int conjNo) throws SQLException {

        String stmt;

        gerundID++;
        stmt = "insert into gerunds values(" + gerundID + ",'" + gerund + "','" + noDiac + "','" + nominal + "','" + formula + "','" + mood + "','" + number + "','" + gender + "'," + rootId + "," + conjNo + ")";
        ps = conn.prepareStatement(stmt);
        ps.execute();
        pl(gerund + ": " + rootId);
        return true;
    }

    void insertGerunds(List gerunds, char nominal, String formula, int rootId, int conjNo) throws SQLException {

//        pl(gerunds);
        boolean done;
        done = (!gerunds.get(0).toString().trim().isEmpty()) ? insertGerund(gerunds.get(0).toString(), this.deDiacritic(gerunds.get(0).toString()), nominal, formula, 'N', 'S', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(1).toString().trim().isEmpty()) ? insertGerund(gerunds.get(1).toString(), this.deDiacritic(gerunds.get(1).toString()), nominal, formula, 'N', 'S', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(2).toString().trim().isEmpty()) ? insertGerund(gerunds.get(2).toString(), this.deDiacritic(gerunds.get(2).toString()), nominal, formula, 'N', 'D', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(3).toString().trim().isEmpty()) ? insertGerund(gerunds.get(3).toString(), this.deDiacritic(gerunds.get(3).toString()), nominal, formula, 'N', 'D', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(4).toString().trim().isEmpty()) ? insertGerund(gerunds.get(4).toString(), this.deDiacritic(gerunds.get(4).toString()), nominal, formula, 'N', 'P', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(5).toString().trim().isEmpty()) ? insertGerund(gerunds.get(5).toString(), this.deDiacritic(gerunds.get(5).toString()), nominal, formula, 'N', 'P', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(6).toString().trim().isEmpty()) ? insertGerund(gerunds.get(6).toString(), this.deDiacritic(gerunds.get(6).toString()), nominal, formula, 'A', 'S', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(7).toString().trim().isEmpty()) ? insertGerund(gerunds.get(7).toString(), this.deDiacritic(gerunds.get(7).toString()), nominal, formula, 'A', 'S', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(8).toString().trim().isEmpty()) ? insertGerund(gerunds.get(8).toString(), this.deDiacritic(gerunds.get(8).toString()), nominal, formula, 'A', 'D', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(9).toString().trim().isEmpty()) ? insertGerund(gerunds.get(9).toString(), this.deDiacritic(gerunds.get(9).toString()), nominal, formula, 'A', 'D', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(10).toString().trim().isEmpty()) ? insertGerund(gerunds.get(10).toString(), this.deDiacritic(gerunds.get(10).toString()), nominal, formula, 'A', 'P', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(11).toString().trim().isEmpty()) ? insertGerund(gerunds.get(11).toString(), this.deDiacritic(gerunds.get(11).toString()), nominal, formula, 'A', 'P', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(12).toString().trim().isEmpty()) ? insertGerund(gerunds.get(12).toString(), this.deDiacritic(gerunds.get(12).toString()), nominal, formula, 'J', 'S', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(13).toString().trim().isEmpty()) ? insertGerund(gerunds.get(13).toString(), this.deDiacritic(gerunds.get(13).toString()), nominal, formula, 'J', 'S', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(14).toString().trim().isEmpty()) ? insertGerund(gerunds.get(14).toString(), this.deDiacritic(gerunds.get(14).toString()), nominal, formula, 'J', 'D', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(15).toString().trim().isEmpty()) ? insertGerund(gerunds.get(15).toString(), this.deDiacritic(gerunds.get(15).toString()), nominal, formula, 'J', 'D', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(16).toString().trim().isEmpty()) ? insertGerund(gerunds.get(16).toString(), this.deDiacritic(gerunds.get(16).toString()), nominal, formula, 'J', 'P', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(17).toString().trim().isEmpty()) ? insertGerund(gerunds.get(17).toString(), this.deDiacritic(gerunds.get(17).toString()), nominal, formula, 'J', 'P', 'F', rootId, conjNo) : false;

//        pl((!gerunds.get(0).toString().trim().isEmpty()) ? gerunds.get(0).toString() + " : " + this.deDiacritic(gerunds.get(0).toString()) + " : " + nominal + " : " + formula + " : " + 'N' + " : " + 'S' + " : " + 'M' + " : " + rootId + " : " + conjNo : "0");
//        insertNoun(gerunds.get(1).toString(), this.deDiacritic(gerunds.get(1).toString()), nominal, formula, 'N', 'S', 'F', rootId, conjNo);
    }

    void Gerunds() throws SQLException {
        //i<=5674
        String rootText = "لكم";
        String formula = "";
        int rootId;
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        TrilateralKovRule rule;// = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
        int kov, conjNo;
        List unaugmentedList;
        List formulas;
        Iterator itr2;
        AugmentedTrilateralRoot augmentedRoot;
        for (int i = 1; i <= 5674; i++) {
            rootId = i;
            ps = conn.prepareStatement("select root from roots where root_id=" + rootId + "");
            rs = ps.executeQuery();
            while (rs.next()) {
                rootText = rs.getString(1);
            }

            rule = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
            kov = rule.getKov();
            unaugmentedList = SarfDictionary.getInstance().getUnaugmentedTrilateralRoots(rootText);
            Iterator itr = unaugmentedList.iterator();
            UnaugmentedTrilateralRoot sarfRoot;
            while (itr.hasNext()) {
                sarfRoot = (UnaugmentedTrilateralRoot) itr.next();
                conjNo = Integer.parseInt(sarfRoot.getConjugation());
                //original gerund
                formulas = TrilateralUnaugmentedGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                itr2 = formulas.iterator();
                while (itr2.hasNext()) {
                    formula = itr2.next().toString();
                    insertGerunds(UnaugmentedTrilateralStandardGerundModifier.getInstance().build(sarfRoot, kov, TrilateralUnaugmentedGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult(), 'O', formula, rootId, conjNo);
                }
                //meem
                formulas = MeemGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                itr2 = formulas.iterator();
                while (itr2.hasNext()) {
                    formula = itr2.next().toString();
                    insertGerunds(TitlateralUnaugmentedMeemModifier.getInstance().build(sarfRoot, kov, MeemGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult(), 'M', formula, rootId, conjNo);
                }
                //nomen
                formulas = TrilateralUnaugmentedNomenGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                itr2 = formulas.iterator();
                while (itr2.hasNext()) {
                    formula = itr2.next().toString();
                    insertGerunds(TitlateralUnaugmentedNomenModifier.getInstance().build(sarfRoot, kov, TrilateralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult(), 'N', formula, rootId, conjNo);
                }
                //quality
                formula = "فِعْلَة";
                insertGerunds(TitlateralUnaugmentedQualityModifier.getInstance().build(sarfRoot, kov, QualityGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult(), 'Q', formula, rootId, conjNo);
            }

            augmentedRoot = SarfDictionary.getInstance().getAugmentedTrilateralRoot(rootText);
            if (augmentedRoot != null) {
                itr = augmentedRoot.getAugmentationList().iterator();
                AugmentationFormula formula2;
                int formulaNo;
                while (itr.hasNext()) {
                    formula2 = (AugmentationFormula) itr.next();
                    formulaNo = formula2.getFormulaNo();
                    try {
                        insertGerunds(TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, TrilateralAugmentedGerundConjugator.getInstance().createGerundList(augmentedRoot, formulaNo), null).getFinalResult(), 'O', "", rootId, formulaNo + 6);

                    } catch (Exception e) {

                    }
                    insertGerunds(TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, TrilateralAugmentedNomenGerundConjugator.getInstance().createGerundList(augmentedRoot, formulaNo), null).getFinalResult(), 'N', "", rootId, formulaNo + 6);
                    insertGerunds(sarf.noun.trilateral.augmented.modifier.passiveparticiple.PassiveParticipleModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedTrilateralPassiveParticipleConjugator.getInstance().createMeemGerundNounList(augmentedRoot, formulaNo), null).getFinalResult(), 'M', "", rootId, formulaNo + 6);

                }
            }
            conn.commit();
        }
    }

    void Gerunds(GerundInfo gerundInfo) throws SQLException {

        String formula = "";
        int conjNo = 0;
        int kov = gerundInfo.getKov();
        List formulas;
        Iterator itr;
        AugmentedTrilateralRoot augmentedRoot;

        if (gerundInfo.isTrilateral()) {
            if (!gerundInfo.isAugmented()) {
                UnaugmentedTrilateralRoot sarfRoot = (UnaugmentedTrilateralRoot) gerundInfo.getRoot();
                conjNo = gerundInfo.conjNo();
                //original gerund
                formulas = TrilateralUnaugmentedGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                itr = formulas.iterator();
                while (itr.hasNext()) {
                    formula = itr.next().toString();
                    insertGerunds(UnaugmentedTrilateralStandardGerundModifier.getInstance().build(sarfRoot, kov, TrilateralUnaugmentedGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult(), 'O', formula, gerundInfo.rootId(), conjNo);
                }
                //meem
                formulas = MeemGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                itr = formulas.iterator();
                while (itr.hasNext()) {
                    formula = itr.next().toString();
                    insertGerunds(TitlateralUnaugmentedMeemModifier.getInstance().build(sarfRoot, kov, MeemGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult(), 'M', formula, gerundInfo.rootId(), conjNo);
                }
                //nomen
                formulas = TrilateralUnaugmentedNomenGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
                itr = formulas.iterator();
                while (itr.hasNext()) {
                    formula = itr.next().toString();
                    insertGerunds(TitlateralUnaugmentedNomenModifier.getInstance().build(sarfRoot, kov, TrilateralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult(), 'N', formula, gerundInfo.rootId(), conjNo);
                }
                //quality
                formula = "فِعْلَة";
                insertGerunds(TitlateralUnaugmentedQualityModifier.getInstance().build(sarfRoot, kov, QualityGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult(), 'Q', formula, gerundInfo.rootId(), conjNo);

            } else {

                augmentedRoot = (AugmentedTrilateralRoot) gerundInfo.getRoot();
                if (augmentedRoot != null) {
                    itr = augmentedRoot.getAugmentationList().iterator();
                    AugmentationFormula formula2;
                    int formulaNo;
                    while (itr.hasNext()) {
                        formula2 = (AugmentationFormula) itr.next();
                        formulaNo = formula2.getFormulaNo();
                        try {
                            insertGerunds(TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, TrilateralAugmentedGerundConjugator.getInstance().createGerundList(augmentedRoot, formulaNo), null).getFinalResult(), 'O', "", gerundInfo.rootId(), formulaNo + 6);

                        } catch (Exception e) {

                        }
                        insertGerunds(TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, TrilateralAugmentedNomenGerundConjugator.getInstance().createGerundList(augmentedRoot, formulaNo), null).getFinalResult(), 'N', "", gerundInfo.rootId(), formulaNo + 6);
                        insertGerunds(sarf.noun.trilateral.augmented.modifier.passiveparticiple.PassiveParticipleModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedTrilateralPassiveParticipleConjugator.getInstance().createMeemGerundNounList(augmentedRoot, formulaNo), null).getFinalResult(), 'M', "", gerundInfo.rootId(), formulaNo + 6);

                    }
                }
            }
        } else {
            if (!gerundInfo.isAugmented()) {
                UnaugmentedQuadriliteralRoot unaugmentedRoot4 = (UnaugmentedQuadriliteralRoot) gerundInfo.getRoot();

                if (unaugmentedRoot4 != null) {

                    QuadriliteralUnaugmentedGerundConjugator2 qunggc = QuadriliteralUnaugmentedGerundConjugator2.getInstance();
                    formulas = qunggc.createGerundList(unaugmentedRoot4);
                    insertGerunds(formulas, 'N', formula, gerundInfo.rootId(), conjNo);
                    if (unaugmentedRoot4.getC1() == unaugmentedRoot4.getC3() && unaugmentedRoot4.getC2() == unaugmentedRoot4.getC4()) {
                        formulas = qunggc.createGerundListForm2(unaugmentedRoot4);
                        insertGerunds(formulas, 'N', formula, gerundInfo.rootId(), conjNo);
                    }

//            //nomen
//                    sb.append("N").append("(");
//                    gerunds = sarf.gerund.quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedRoot4);
//                    sb.append(insertGerunds(formulas));
//                    sb.append(")").append(",");
//
//                    //meem
//                    sb.append("M").append("(");
//                    gerunds = sarf.noun.quadriliteral.unaugmented.UnaugmentedQuadriliteralPassiveParticipleConjugator.getInstance().createMeemGerundNounList(unaugmentedRoot4);//quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedRoot4);
//                    sb.append(insertGerunds(formulas));
//                    sb.append(")");
//                    sb.append(")");
                }
            } else {
//                AugmentedQuadriliteralRoot augmentedRoot4 = SarfDictionary.getInstance().getAugmentedQuadrilateralRoot(rootText);
//                Iterator itr;
//                if (augmentedRoot4 != null) {
//                    itr = augmentedRoot4.getAugmentationList().iterator();
//                    AugmentationFormula formula;
//                    int formulaNo;
//                    while (itr.hasNext()) {
//                        formula = (AugmentationFormula) itr.next();
//                        formulaNo = formula.getFormulaNo();
//                        sb.append(",");
//                        sb.append("C").append(formulaNo + 1).append("(");
//                        //original
//                        sb.append("O").append("(");
//                        gerunds = QuadriliteralAugmentedGerundConjugator2.getInstance().createGerundList(augmentedRoot4, formulaNo);
//                        gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(augmentedRoot4, formulaNo, kov, gerunds).getFinalResult();
//                        sb.append(insertGerunds(gerunds));
//                        sb.append(")");
//                        //nomen
//                        sb.append(",").append("N").append("(");
//                        gerunds = QuadriliteralAugmentedNomenGerundConjugator2.getInstance().createGerundList(augmentedRoot4, formulaNo);
//                        gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(augmentedRoot4, formulaNo, kov, gerunds).getFinalResult();
//                        sb.append(insertGerunds(gerunds));
//
//                        sb.append(")");
                //meem
//                        sb.append(",").append("M").append("(");
//                        gerunds = sarf.noun.quadriliteral.augmented.AugmentedQuadriliteralActiveParticipleConjugator.getInstance().createNounList(augmentedRoot4, formulaNo);//quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedRoot4);
//                        gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(augmentedRoot4, formulaNo, kov, gerunds).getFinalResult();
//                        sb.append(insertGerunds(gerunds));
//                        sb.append(")");
//                        sb.append(")");
//                    }

//                }
            }

            //------------------- end unaugmentedroot
        }
    }

    String pronouns(int index) {
        String[] pronouns = new String[]{"1ms", "1mp", "2ms", "2fs", "2md", "2mp", "2fp", "3ms", "3fs", "3md", "3md", "3mp", "3fp"};
        return pronouns[index];
    }

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        SarfExporter_1 expr = new SarfExporter_1();
        expr.conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_10");

//        expr.Gerunds();
//        expr.createGerundTable();
//        expr.insertGerund("ياس", "يس", 'O', "فعلا", 'N', 'D', 'M', 1, 1);
//        expr.verbsDiacriting();
//        expr.createNounsTable();
//        expr.insertionRoots();
//        expr.importRootVerbs();
//        expr.importDerivedNouns();
//        expr.createVerbsTable2();
//        expr.insertVerb();
//        expr.insertNoun(1, "كاَُُّتب", "كتب", 'O', 'N', 'P', 'M', 1, 1);
//        stmt= "insert into verbs values(1,'بُؤْسَى','AC','P',1,3,1)";
//        expr.insertVerb("بُؤْسَى", "ياس", true, 'P', 1, 3, 1);
//        expr.createNewDB("sarfDB_10");
//        expr.getAllDBTables("sarfDB_01");
//        expr.createNewTable("tri_roots2");
//        expr.createRootsTable();
//        expr.deleteTable("roots");
//        expr.createConjugationTable();
//        expr.fullConjugations();
//        void insertionConj(int rootId, String past, String present, String transitivity, String triType, int conjugationNo) throws SQLException {
//        expr.insertionConj(1, 1, "ضرب", "يضرب", "متعدي");
//        expr.fullTriConjs(2300, "ضبب");
//        expr.deleteTuple(1);
        expr.deleteData("delete from verbs where root_id >= 5675");
        expr.deleteData("delete from gerunds where root_id >= 5675");
        expr.deleteData("delete from conjugations where root_id >= 5675");
//      
//        expr.selectData("conjugations");
//        expr.selectStmt("select * from verbs where root_id=?", "61");
//        expr.selectStmt("select * from roots","");
//        expr.showTableSchema("roots");
//        pl(expr.getRoots().keySet().size());
        expr.conn.close();
    }

    static void pl(Object o) {
        System.out.println(o);
    }
}
