package sarf_package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sarf.AugmentationFormula;
import sarf.SarfDictionary;
import sarf.SystemConstants;
import sarf.gerund.modifier.trilateral.augmented.standard.TitlateralAugmentedStandardModifier;
import sarf.gerund.modifier.trilateral.unaugmented.meem.TitlateralUnaugmentedMeemModifier;
import sarf.gerund.modifier.trilateral.unaugmented.nomen.TitlateralUnaugmentedNomenModifier;
import sarf.gerund.modifier.trilateral.unaugmented.quality.TitlateralUnaugmentedQualityModifier;
import sarf.gerund.modifier.trilateral.unaugmented.standard.UnaugmentedTrilateralStandardGerundModifier;
import sarf.gerund.trilateral.unaugmented.QualityGerundConjugator;
import sarf.gerund.trilateral.unaugmented.TrilateralUnaugmentedGerundConjugator;
import sarf.gerund.trilateral.unaugmented.TrilateralUnaugmentedNomenGerundConjugator;
import sarf.gerund.trilateral.unaugmented.meem.MeemGerundConjugator;
import sarf.kov.KovRulesManager;
import sarf.noun.trilateral.augmented.AugmentedTrilateralPassiveParticipleConjugator;
import sarf.verb.quadriliteral.unaugmented.UnaugmentedQuadriliteralRoot;
import sarf.verb.trilateral.augmented.AugmentedTrilateralRoot;
import sarf.verb.trilateral.augmented.active.past.AugmentedActivePastConjugator;
import sarf.verb.trilateral.augmented.active.present.AugmentedActivePresentConjugator;
import sarf.verb.trilateral.augmented.imperative.AugmentedImperativeConjugator;
import sarf.verb.trilateral.augmented.modifier.AugmentedTrilateralModifier;
import sarf.verb.trilateral.augmented.modifier.vocalizer.FormulaApplyingChecker;
import sarf.verb.trilateral.augmented.modifier.vocalizer.IFormulaApplyingChecker;
import sarf.verb.trilateral.augmented.passive.past.AugmentedPassivePastConjugator;
import sarf.verb.trilateral.augmented.passive.present.AugmentedPassivePresentConjugator;
import sarf.verb.trilateral.unaugmented.ConjugationResult;
import sarf.verb.trilateral.unaugmented.UnaugmentedImperativeConjugator;
import sarf.verb.trilateral.unaugmented.UnaugmentedTrilateralRoot;
import sarf.verb.trilateral.unaugmented.active.*;
import sarf.verb.trilateral.unaugmented.modifier.UnaugmentedTrilateralModifier;
import sarf.verb.trilateral.unaugmented.passive.*;
import static sarf_package.SarfExporter_1.pl;
import static sarf_package.Utiles.conn;
import static sarf_package.Utiles.deDiacritic;
import static sarf_package.Utiles.pronouns;
import static sarf_package.Utiles.ps;
import static sarf_package.Utiles.rs;

public class TriRoot implements Root {

    String rootText;
    int rootId;

    boolean trilateral;
    int kov;

    static int verbId;
    static int gerundId;

    TriRoot(int rotId, String rootTxt) {
        rootText = rootTxt;
        rootId = rotId;
        trilateral = rootTxt.length() == 3;
        kov = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2)).getKov();

    }

    public boolean openConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_10");
        } catch (SQLException ex) {
            Logger.getLogger(SarfToDerby.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean closeConnection() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SarfToDerby.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean isTrilateral() {
        return trilateral;
    }

    @Override
    public int rootId() {
        return rootId;
    }

    @Override
    public String rootText() {
        return rootText;
    }

    @Override
    public int kov() {
        return kov;
    }

    @Override
    public Object unaugmentedConjs() {
        return SarfDictionary.getInstance().getUnaugmentedTrilateralRoots(rootText);
    }

    @Override
    public Object augmentedConjs() {
        return SarfDictionary.getInstance().getAugmentedTrilateralRoot(rootText);
    }

    @Override
    public boolean insertVG() {

        verbId = getId("verbs", "verb_id");
        gerundId = getId("gerunds", "gerund_id");
        boolean flag = false;
        Object conjs = this.unaugmentedConjs();
        if (conjs != null) {
            flag = insertVG((List) conjs);
        }
        conjs = this.augmentedConjs();
        if (conjs != null) {
            flag = insertVG((AugmentedTrilateralRoot) conjs);
        }

        return flag;
    }

    void insertionConj(int rootId, int conjugationNo, char transitivity) {
        try {
            String stmt = "insert into conjugations  values (" + rootId + "," + conjugationNo + ",'" + transitivity + "')";
            ps = conn.prepareStatement(stmt);
            ps.execute();
//            conn.commit();
        } catch (SQLException ex) {
            pl(rootId + " : " + conjugationNo);
            Logger.getLogger(TriRoot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void insertVerbs(int rootId, int conjNo, char tense, RootInfo rootInfo) {
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
                    insertVerb(verbId, verb, deDiacritic(verb), rootInfo.isActive(), tense, pronouns(p), rootId, conjNo);
                }
            }
        }
    }

    void insertVerb(int verbID, String verb, String noDiac, Boolean active, char tense, String pronoun, int rootId, int conjNo) {

        try {
            String stmt = "insert into verbs  values(" + verbID + ",'" + verb + "','" + noDiac + "','" + active + "','" + tense + "','" + pronoun + "'," + rootId + "," + conjNo + ")";

            ps = conn.prepareStatement(stmt);
            ps.execute();
//            conn.commit();

        } catch (SQLException ex) {
            pl(verb + ": " + rootId);
            Logger.getLogger(TriRoot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    boolean insertGerund(String gerund, String noDiac, char nominal, String formula, char mood, char number, char gender, int rootId, int conjNo) {

        try {
            String stmt;
            gerundId++;

            stmt = "insert into gerunds values(" + gerundId + ",'" + gerund + "','" + noDiac + "','" + nominal + "','" + formula + "','" + mood + "','" + number + "','" + gender + "'," + rootId + "," + conjNo + ")";
            ps = conn.prepareStatement(stmt);
            ps.execute();

            return true;
        } catch (SQLException ex) {
            pl(gerund + ": " + rootId);
            Logger.getLogger(TriRoot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    void insertGerunds(List gerunds, char nominal, String formula, int rootId, int conjNo) {

//        pl(gerunds);
        boolean done;
        done = (!gerunds.get(0).toString().trim().isEmpty()) ? insertGerund(gerunds.get(0).toString(), deDiacritic(gerunds.get(0).toString()), nominal, formula, 'N', 'S', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(1).toString().trim().isEmpty()) ? insertGerund(gerunds.get(1).toString(), deDiacritic(gerunds.get(1).toString()), nominal, formula, 'N', 'S', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(2).toString().trim().isEmpty()) ? insertGerund(gerunds.get(2).toString(), deDiacritic(gerunds.get(2).toString()), nominal, formula, 'N', 'D', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(3).toString().trim().isEmpty()) ? insertGerund(gerunds.get(3).toString(), deDiacritic(gerunds.get(3).toString()), nominal, formula, 'N', 'D', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(4).toString().trim().isEmpty()) ? insertGerund(gerunds.get(4).toString(), deDiacritic(gerunds.get(4).toString()), nominal, formula, 'N', 'P', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(5).toString().trim().isEmpty()) ? insertGerund(gerunds.get(5).toString(), deDiacritic(gerunds.get(5).toString()), nominal, formula, 'N', 'P', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(6).toString().trim().isEmpty()) ? insertGerund(gerunds.get(6).toString(), deDiacritic(gerunds.get(6).toString()), nominal, formula, 'A', 'S', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(7).toString().trim().isEmpty()) ? insertGerund(gerunds.get(7).toString(), deDiacritic(gerunds.get(7).toString()), nominal, formula, 'A', 'S', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(8).toString().trim().isEmpty()) ? insertGerund(gerunds.get(8).toString(), deDiacritic(gerunds.get(8).toString()), nominal, formula, 'A', 'D', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(9).toString().trim().isEmpty()) ? insertGerund(gerunds.get(9).toString(), deDiacritic(gerunds.get(9).toString()), nominal, formula, 'A', 'D', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(10).toString().trim().isEmpty()) ? insertGerund(gerunds.get(10).toString(), deDiacritic(gerunds.get(10).toString()), nominal, formula, 'A', 'P', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(11).toString().trim().isEmpty()) ? insertGerund(gerunds.get(11).toString(), deDiacritic(gerunds.get(11).toString()), nominal, formula, 'A', 'P', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(12).toString().trim().isEmpty()) ? insertGerund(gerunds.get(12).toString(), deDiacritic(gerunds.get(12).toString()), nominal, formula, 'J', 'S', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(13).toString().trim().isEmpty()) ? insertGerund(gerunds.get(13).toString(), deDiacritic(gerunds.get(13).toString()), nominal, formula, 'J', 'S', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(14).toString().trim().isEmpty()) ? insertGerund(gerunds.get(14).toString(), deDiacritic(gerunds.get(14).toString()), nominal, formula, 'J', 'D', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(15).toString().trim().isEmpty()) ? insertGerund(gerunds.get(15).toString(), deDiacritic(gerunds.get(15).toString()), nominal, formula, 'J', 'D', 'F', rootId, conjNo) : false;
        done = (!gerunds.get(16).toString().trim().isEmpty()) ? insertGerund(gerunds.get(16).toString(), deDiacritic(gerunds.get(16).toString()), nominal, formula, 'J', 'P', 'M', rootId, conjNo) : false;
        done = (!gerunds.get(17).toString().trim().isEmpty()) ? insertGerund(gerunds.get(17).toString(), deDiacritic(gerunds.get(17).toString()), nominal, formula, 'J', 'P', 'F', rootId, conjNo) : false;

//        pl((!gerunds.get(0).toString().trim().isEmpty()) ? gerunds.get(0).toString() + " : " + this.deDiacritic(gerunds.get(0).toString()) + " : " + nominal + " : " + formula + " : " + 'N' + " : " + 'S' + " : " + 'M' + " : " + rootId + " : " + conjNo : "0");
//        insertNoun(gerunds.get(1).toString(), this.deDiacritic(gerunds.get(1).toString()), nominal, formula, 'N', 'S', 'F', rootId, conjNo);
    }

    private boolean insertVG(List unaugmentedConjs) {

        Iterator itr = unaugmentedConjs.iterator();

        int preConj = 0;
        while (itr.hasNext()) {
            UnaugmentedTrilateralRoot sarfRoot = (UnaugmentedTrilateralRoot) itr.next();
            int conjNo = Integer.parseInt(sarfRoot.getConjugation());
            if (preConj != conjNo) {

                insertionConj(rootId, conjNo, sarfRoot.getTransitive().charAt(0));
                verbConj(conjNo, sarfRoot);
                gerundConj(conjNo, sarfRoot);
                preConj = conjNo;
            }
        }

        return true;
    }

    private boolean insertVG(AugmentedTrilateralRoot augmentedRoot) {

        Iterator itr = augmentedRoot.getAugmentationList().iterator();
        while (itr.hasNext()) {
            AugmentationFormula formula = (AugmentationFormula) itr.next();
            int formulaNo = formula.getFormulaNo();
            insertionConj(rootId, formulaNo + 6, formula.getTransitive());
            verbConj(formulaNo, augmentedRoot);
            gerundConj(formulaNo, augmentedRoot);
        }

        return true;
    }

    private int getId(String tableName, String colm) {
        try {
            ps = conn.prepareStatement("select max(" + colm + ") as maxID from " + tableName + "");
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt("maxID");
            }
        } catch (SQLException ex) {
            pl("error in count of " + tableName + " id.");
        }
        return 0;
    }

    private void verbConj(int conjNo, UnaugmentedTrilateralRoot sarfRoot) {
        //active
        insertVerbs(rootId, conjNo, 'P', new RootInfo(sarfRoot, ActivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.PAST_TENSE));
        insertVerbs(rootId, conjNo, 'N', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'A', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'J', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'E', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'I', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE));
        insertVerbs(rootId, conjNo, 'M', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE));
        //passive
        insertVerbs(rootId, conjNo, 'P', new RootInfo(sarfRoot, PassivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, false, SystemConstants.PAST_TENSE));
        insertVerbs(rootId, conjNo, 'N', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'A', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'J', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'E', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));

    }

    private void gerundConj(int conjNo, UnaugmentedTrilateralRoot sarfRoot) {
        Iterator itr2;
        String formula;
        List formulas;

        //original
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

    private void verbConj(int formulaNo, AugmentedTrilateralRoot augmentedRoot) {
        //active verbs
        insertVerbs(rootId, formulaNo + 6, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
        insertVerbs(rootId, formulaNo + 6, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
        insertVerbs(rootId, formulaNo + 6, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
        insertVerbs(rootId, formulaNo + 6, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
        insertVerbs(rootId, formulaNo + 6, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
        insertVerbs(rootId, formulaNo + 6, 'I', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getNotEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
        insertVerbs(rootId, formulaNo + 6, 'M', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
        // passive verbs
        insertVerbs(rootId, formulaNo + 6, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
        insertVerbs(rootId, formulaNo + 6, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
        insertVerbs(rootId, formulaNo + 6, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
        insertVerbs(rootId, formulaNo + 6, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
        insertVerbs(rootId, formulaNo + 6, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));

    }

    private void gerundConj(int formulaNo, AugmentedTrilateralRoot augmentedRoot) {
        List gerunds;
        GerundSelectionUI gs = new GerundSelectionUI();
        TrilateralAugmentedGerundConjugator tagc;

        gs.cashedPatternFormulaNo = 1;
        tagc = TrilateralAugmentedGerundConjugator.getInstance();
        tagc.setListener(gs);
        tagc.setAugmentedTrilateralModifierListener(gs);
        gerunds = tagc.createGerundList(augmentedRoot, formulaNo);
        gerunds = TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult();
        insertGerunds(TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult(),
                'O', formulaNo == 2 ? "تفعلة" : formulaNo == 3 ? "مفاعلة" : null, rootId, formulaNo + 6);

        if ((formulaNo == 2 && augmentedRoot.getC3() == 'ء') || (formulaNo == 3 && augmentedRoot.getC1() != 'ي')) {
//                     String msg = "لهذا المصدر وزنان: (تفعيل) و(تفعلة)، اختر أحدهما";2
//                        String msg = "لهذا المصدر وزنان (مفاعلة) و  (فعال) اختر أحدهما";3
            gs.cashedPatternFormulaNo = 2;
            gs.cashedUserResponse = true;
            tagc = TrilateralAugmentedGerundConjugator.getInstance();
            tagc.setListener(gs);
            tagc.setAugmentedTrilateralModifierListener(gs);
            gerunds = tagc.createGerundList(augmentedRoot, formulaNo);
            gerunds = TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult();
            insertGerunds(TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult(),
                    'O', formulaNo == 2 ? "تفعيل" : "فعال", rootId, formulaNo + 6);
        }
        if (formulaNo == 1 || formulaNo == 5 || formulaNo == 9) {
            int result = FormulaApplyingChecker.getInstance().check(augmentedRoot, formulaNo);
            if (result == IFormulaApplyingChecker.TWO_STATE) {
                gs.cashedPatternFormulaNo = 1;
                gs.cashedUserResponse = false;
                tagc = TrilateralAugmentedGerundConjugator.getInstance();
                tagc.setListener(gs);
                tagc.setAugmentedTrilateralModifierListener(gs);
                gerunds = tagc.createGerundList(augmentedRoot, formulaNo);
                gerunds = TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult();
                insertGerunds(TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult(),
                        'O', "علة", rootId, formulaNo + 6);

            }
        }

        insertGerunds(TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult(), 'N', null, rootId, formulaNo + 6);
        insertGerunds(sarf.noun.trilateral.augmented.modifier.passiveparticiple.PassiveParticipleModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedTrilateralPassiveParticipleConjugator.getInstance().createMeemGerundNounList(augmentedRoot, formulaNo), null).getFinalResult(), 'M', null, rootId, formulaNo + 6);

    }

}
