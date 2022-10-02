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

public class SarfExporter {

    ArrayList results;
    PreparedStatement ps;
    Connection conn;
    ResultSet rs;

    public SarfExporter() {

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
            rawroots.add(rot);
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
        String stmt = "CREATE TABLE " + name + " (root_id int primary key not null,root varchar(4) unique)";
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
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String name = "verbs2";
//        ps = conn.prepareStatement("drop table " + name);
//        ps.execute();
        String stmt = "CREATE TABLE " + name + " (verb_id INT primary key,"
                + "verb varchar(20),"
                + "verb_NonDiac varchar(10),"
                + "active boolean,"
                + "tense char(1),"
                + "pronoun SMALLINT,"
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
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String stmt = "CREATE TABLE conjugations3 (conj_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,"
                + "root_id INT,"
                + "past varchar(15) NOT NULL,"
                + "present varchar(18) NOT NULL,"
                + "transitivity varchar(15) NOT NULL,"
                + "tri_type varchar(15) NOT NULL,"
                + "conjugation_no INT NOT NULL,"
                + "CONSTRAINT fgnkey2 FOREIGN KEY (root_id) REFERENCES tri_roots3 (id))";
        Statement s = conn.createStatement();
        return s.execute(stmt);
    }

    Boolean createConjugationTable2() throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String stmt = "CREATE TABLE conjugations (root_id INT NOT NULL,"
                + "conjugation_no INT NOT NULL,"
                + "past varchar(15) NOT NULL,"
                + "present varchar(18) NOT NULL,"
                + "transitivity varchar(15) NOT NULL,"
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

    void insertionRoot(int rootId, String root, char quantity, String abstractForm) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String stmt = "insert into roots values (" + rootId + ",'" + root + "','" + quantity + "','" + abstractForm + "' )";
        ps = conn.prepareStatement(stmt);
        ps.execute();
//        conn.commit();
        pl(rootId + " : " + root + " : " + quantity + " : " + abstractForm);

    }

    void insertionTriRoots() throws SAXException, IOException, ParserConfigurationException, SQLException {
        List<String> roots = importRoots();
        int rootId = 0;
        String form;
        for (int i = 0; i < roots.size(); i++) {
            String rootIn = roots.get(i);
            form = KovRulesManager.getInstance().getTrilateralKovRule(rootIn.charAt(0), rootIn.charAt(1), rootIn.charAt(2)).getDesc();
            rootId++;
            insertionRoot(rootId, rootIn, 'T', form);
//            pl(root + " : " + rootId);
        }
    }

    void insertionData(int rootId, int conjugationNo, String past, String present, String transitivity) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
//        String stmt = "insert into conjugations2 (root_id,past,present,transitivity,tri_type,conjugation_no)"
//                + " values (" + rootId + ",'" + past + "','" + present + "','" + transitivity + "','" + triType + "'," + conjugationNo + ")";
        String stmt = "insert into conjugations  values (" + rootId + "," + conjugationNo + ",'" + past + "','" + present + "','" + transitivity + "')";
        ps = conn.prepareStatement(stmt);
        ps.execute();
//        conn.commit();
        pl(rootId + ": " + present);

    }

    Boolean deleteTuple(int id) throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");

        Statement st = conn.createStatement();
        String stmt = "delete from conjugations";
        return st.execute(stmt);

    }

    Boolean deleteData(String stmt) throws SQLException {

        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
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
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");

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
//            pl(count);
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
            getTriUnAugConjs(entry.getKey(), entry.getValue());
        }
    }

    void getTriUnAugConjs(int id, String rootText) throws SQLException {
//        List<String> conjugations = new ArrayList();

        int kov = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2)).getKov();
        int preConj = 0;
        List unaugmentedList = SarfDictionary.getInstance().getUnaugmentedTrilateralRoots(rootText);
        Iterator itr1 = unaugmentedList.iterator();
        while (itr1.hasNext()) {
            UnaugmentedTrilateralRoot sarfRoot = (UnaugmentedTrilateralRoot) itr1.next();
            int posConj = Integer.parseInt(sarfRoot.getConjugation());
            if (preConj != posConj) {
                List pastActive = UnaugmentedTrilateralModifier.getInstance().build(sarfRoot, kov, ActivePastConjugator.getInstance().createVerbList(sarfRoot), SystemConstants.PAST_TENSE, true).getFinalResult();
                List presentActive = UnaugmentedTrilateralModifier.getInstance().build(sarfRoot, kov, ActivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), SystemConstants.PRESENT_TENSE, true).getFinalResult();

                String pastRootText = pastActive.get(7).toString();
                String presentRootText = presentActive.get(7).toString();

                insertionData(id, posConj, pastRootText, presentRootText, getAugTrans(sarfRoot.getTransitive().charAt(0)));
//                System.out.println(id + " " + posConj + " " + pastRootText + " " + presentRootText + " " + getAugTrans(sarfRoot.getTransitive().charAt(0)));
                preConj = posConj;
            }
        }

        AugmentedTrilateralRoot augmentedRoot = SarfDictionary.getInstance().getAugmentedTrilateralRoot(rootText);
        if (augmentedRoot != null) {
            itr1 = augmentedRoot.getAugmentationList().iterator();
            while (itr1.hasNext()) {
                AugmentationFormula formula = (AugmentationFormula) itr1.next();
                int formulaNo = formula.getFormulaNo();

                List pastActive = AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, true, null).getFinalResult();
                List presentActive = AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult();

                String pastRootText = pastActive.get(7).toString();
                String presentRootText = presentActive.get(7).toString();
                insertionData(id, formulaNo + 6, pastRootText, presentRootText, getAugTrans(formula.getTransitive()));
//                System.out.println(id + " " + (formulaNo + 6) + " " + pastRootText + " " + presentRootText + " " + getAugTrans(formula.getTransitive()));

            }
//            pl(id + ": " + rootText);
        }
    }

    String getAugTrans(char chr) {
        switch (chr) {
            case 'ل':
                return "لازم";
            case 'م':
                return "متعدي";
            case 'ك':
                return "لازم و متعدي";
            default:
                return chr + "";
        }
    }

    Map<Integer, String> getRoots() {
        Map<Integer, String> roots = new HashMap();
        try {
            conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
            String root;
            int root_id;
            String stmt = "select * from roots";
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

    void insertVerb(int verbID, String verb, String noDiac, Boolean active, char tense, int pronoun, int rootId, int conjNo) throws SQLException {

//        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String stmt;
//        stmt= "insert into verbs values(1,'بُؤْسَى','AC','P',1,3,1)";

//        if (noDiac.toCharArray().length > 10) {
//            if (noDiac.contains("/")) {
//                String[] v2 = verb.split("/");
//                stmt = "insert into verbs2  values(" + verbID + ",'" + verb + "','" + v2[0] + "','" + active + "','" + tense + "'," + pronoun + "," + rootId + "," + conjNo + ")";
//                ps = conn.prepareStatement(stmt);
//                ps.execute();
//                verbId++;
//                verbID = verbId;
//                stmt = "insert into verbs2  values(" + verbID + ",'" + verb + "','" + v2[1] + "','" + active + "','" + tense + "'," + pronoun + "," + rootId + "," + conjNo + ")";
//                ps = conn.prepareStatement(stmt);
//                ps.execute();
//                return;
//            }
//        }
//        stmt = "insert into verbs values(" + vID + ",'" + verb + "','" + active + "','" + tense + "'," + pronoun + "," + rootId + "," + conjNo + ")";
//        (verb_id,verb,verb_NonDiac,active,tense,pronoun,root_id,conjugation_no)
        stmt = "insert into verbs2  values(" + verbID + ",'" + verb + "','" + noDiac + "','" + active + "','" + tense + "'," + pronoun + "," + rootId + "," + conjNo + ")";

        ps = conn.prepareStatement(stmt);
        ps.execute();
        conn.commit();
        pl(verb + ": " + rootId);
    }

    void insertVerbs2(int rootId, char tense, RootInfo rootInfo) throws SQLException {
        List finalresult = null;
        int conjNo = 0;
        if (!rootInfo.isAugmented()) {
            UnaugmentedTrilateralRoot sarfroot = (UnaugmentedTrilateralRoot) rootInfo.getRoot();
            conjNo = Integer.parseInt(sarfroot.getConjugation());
            ConjugationResult conjResult = UnaugmentedTrilateralModifier.getInstance().build(sarfroot, rootInfo.getKov(), rootInfo.getList(), rootInfo.getTense(), rootInfo.isActive());
            finalresult = conjResult.getFinalResult();
        } else if (rootInfo.isAugmented()) {
            finalresult = rootInfo.getList();
            conjNo = rootInfo.getKov();
        }

        Boolean actPass = rootInfo.isActive();
        int pronoun;
        String verb;
        if (finalresult != null) {
            for (int p = 0; p < 13; p++) {

                pronoun = p + 1;
                Object o = finalresult.get(p);
                if (o != null) {
                    verbId++;
                    verb = o.toString();
//                    insertVerb(verbId, verb, actPass, tense, pronoun, rootId, conjNo);
                }
            }
        }
    }

    static int verbId = 2091658;

    void importRootVerbs() throws SQLException {
        //i<=5674
        String rootText = "";
        int rootId;
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        TrilateralKovRule rule;// = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
        int kov;
        List unaugmentedList;
        AugmentedTrilateralRoot augmentedRoot;
        for (int i = 5674; i < 5674; i++) {
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
                //active verbs
                insertVerbs2(rootId, 'P', new RootInfo(sarfRoot, ActivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.PAST_TENSE));
                insertVerbs2(rootId, 'N', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs2(rootId, 'A', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs2(rootId, 'J', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs2(rootId, 'E', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
                insertVerbs2(rootId, 'I', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE));
                insertVerbs2(rootId, 'M', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE));
                //passive verbs
                insertVerbs2(rootId, 'P', new RootInfo(sarfRoot, PassivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, false, SystemConstants.PAST_TENSE));
                insertVerbs2(rootId, 'N', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs2(rootId, 'A', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs2(rootId, 'J', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
                insertVerbs2(rootId, 'E', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
            }
            augmentedRoot = SarfDictionary.getInstance().getAugmentedTrilateralRoot(rootText);
            if (augmentedRoot != null) {
                itr = augmentedRoot.getAugmentationList().iterator();
                AugmentationFormula formula;
                int formulaNo;
                while (itr.hasNext()) {
                    formula = (AugmentationFormula) itr.next();
                    formulaNo = formula.getFormulaNo();
                    //active verbs
                    insertVerbs2(rootId, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                    insertVerbs2(rootId, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                    insertVerbs2(rootId, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                    insertVerbs2(rootId, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                    insertVerbs2(rootId, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                    insertVerbs2(rootId, 'I', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getNotEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                    insertVerbs2(rootId, 'M', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                    // passive verbs
                    insertVerbs2(rootId, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                    insertVerbs2(rootId, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                    insertVerbs2(rootId, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                    insertVerbs2(rootId, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                    insertVerbs2(rootId, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));

                }
            }
            conn.commit();
            pl(rootText + ": " + rootId);
        }
        conn.close();
    }

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
        ps = conn.prepareStatement("select COLUMNNAME,COLUMNDATATYPE FROM sys.systables t, sys.syscolumns WHERE TABLEID=REFERENCEID and tablename='"+tablename+"'");
//        ps = conn.prepareStatement("select TABLENAME from SYS.SYSTABLES where TABLETYPE='T'");
        rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    void verbsDiacriting() throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        ps = conn.prepareStatement("select * from verbs where  root_id > 4045");
        rs = ps.executeQuery();
//        int verb_ID = 898645;
//(verb,verb_NonDiac,active,tense,pronoun,root_id,conjugation_no)
        while (rs.next()) {
            verbId++;
            String verb = rs.getString("verb");
            String noDiac = this.deDiacritic(verb);
            boolean active = rs.getBoolean("active");
            char tense = rs.getString("tense").charAt(0);
            int pronoun = rs.getInt("pronoun");
            int rootId = rs.getInt("root_Id");
            int conjNo = rs.getInt("conjugation_no");
            insertVerb(verbId, verb, noDiac, active, tense, pronoun, rootId, conjNo);
//            pl(verb + " : " + noDiac + " : " + active + " : " + tense + " : " + pronoun + " : " + rootId + " : " + conjNo);
//            pl("verb id( " + rs.getInt("verb_id") + "-" + verb_ID + "-) : " + verb + " : " + noDiac + " : " + rootId + " : " + conjNo);
        }
//        pl(count);
    }

    Boolean createGerundTable() throws SQLException {

//        Connection conn2 = DriverManager.getConnection("jdbc:derby:" + "sarfDB_02" + ";create=true");
//        conn2.prepareStatement("drop table gerunds").execute();
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
                + "conjugation_no INT NOT NULL)";
//                + "FOREIGN KEY (root_id,conjugation_no) REFERENCES conjugations (root_id,conjugation_no))";
        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");

        Statement s = conn.createStatement();
        return s.execute(stmt);
    }
    int gerundID = 0;

    boolean insertGerund(String gerund, String noDiac, char nominal, String formula, char mood, char number, char gender, int rootId, int conjNo) throws SQLException {
//        Connection conn2 = DriverManager.getConnection("jdbc:derby:" + "sarfDB_02" + ";create=true");
//        ps = conn2.prepareStatement("delete from gerunds");
//        ps.execute();
//        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
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

    void importGerunds() throws SQLException {
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

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        SarfExporter expr = new SarfExporter();
//        expr.importGerunds();
//        expr.createGerundTable();
//        expr.insertGerund("ياس", "يس", 'O', "فعلا", 'N', 'D', 'M', 1, 1);
//        expr.verbsDiacriting();
//        expr.createNounsTable();
//        expr.insertionTriRoots();
//        expr.importRootVerbs();
//        expr.importDerivedNouns();
//        expr.createVerbsTable2();
//        expr.insertVerb();
//            void insertNoun(String noun, char type, char aspect, char person, char gender, int rootId, int conjNo) throws SQLException {
//        expr.insertNoun(1, "كاَُُّتب", "كتب", 'O', 'N', 'P', 'M', 1, 1);
//        stmt= "insert into verbs values(1,'بُؤْسَى','AC','P',1,3,1)";
//        expr.insertVerb("بُؤْسَى", "ياس", true, 'P', 1, 3, 1);
//        expr.createNewDB("sarfDB_10");
//        expr.getAllDBTables("sarfDB_01");
//        expr.createNewTable("tri_roots2");
        expr.createRootsTable();
//        expr.deleteTable("gerunds");
//        expr.createConjugationTable2();
//        expr.fullConjugations();
//        void insertionData(int rootId, String past, String present, String transitivity, String triType, int conjugationNo) throws SQLException {
//        expr.insertionData(1, 1, "ضرب", "يضرب", "متعدي");
//        expr.getTriUnAugConjs(2300, "ضبب");
//        expr.deleteTuple(1);
//        expr.deleteData("delete from verbs2 where root_id=4046");
//        expr.deleteData("delete from gerunds where root_id=1");
//        expr.deleteData("delete from gerunds where root_id=2511");
//        expr.selectData("conjugations");
//        expr.selectStmt("select * from verbs where root_id=?", "61");
//        expr.selectStmt("select * from verbs","");
//        expr.showTableSchema("roots");
//        pl(expr.getRoots().keySet().size());
    }

    static void pl(Object o) {
        System.out.println(o);
    }
}
