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
import sarf.verb.quadriliteral.QuadrilateralRoot;
import sarf.verb.quadriliteral.augmented.AugmentedQuadriliteralRoot;
import sarf.verb.quadriliteral.unaugmented.UnaugmentedQuadriliteralRoot;
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
import sarf.verb.trilateral.unaugmented.passive.PassivePastConjugator;
import sarf.verb.trilateral.unaugmented.passive.PassivePresentConjugator;
import static sarf_package.SarfExporter_1.pl;
import static sarf_package.Utiles.conn;
import static sarf_package.Utiles.deDiacritic;
import static sarf_package.Utiles.pronouns;
import static sarf_package.Utiles.ps;
import static sarf_package.Utiles.rs;

public class QuadRoot implements Root {

    String rootText;
    int rootId;

    boolean trilateral;
    int kov;

    static int verbId;
    static int gerundId;

    QuadRoot(int rotId, String rootTxt) {
        rootText = rootTxt;
        rootId = rotId;
        trilateral = rootTxt.length() == 3;
        kov = KovRulesManager.getInstance().getQuadrilateralKov(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2), rootText.charAt(3));
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
        return SarfDictionary.getInstance().getUnaugmentedQuadrilateralRoot(rootText);
    }

    @Override
    public Object augmentedConjs() {
        return SarfDictionary.getInstance().getAugmentedQuadrilateralRoot(rootText);
    }

    @Override
    public boolean insertVG() {

        verbId = getId("verbs", "verb_id");
        gerundId = getId("gerunds", "gerund_id");
        boolean flag = false;
        Object conjs = this.unaugmentedConjs();
        if (conjs != null) {
            flag = insertVG((UnaugmentedQuadriliteralRoot) conjs);
        }
        conjs = this.augmentedConjs();
        if (conjs != null) {
            flag = insertVG((AugmentedQuadriliteralRoot) conjs);
        }

        return flag;
    }

    void insertionConj(int rootId, int conjugationNo, char transitivity) {
        try {
            String stmt = "insert into conjugations  values (" + rootId + "," + conjugationNo + ",'" + transitivity + "')";
            ps = conn.prepareStatement(stmt);
            ps.execute();
            conn.commit();
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

    private boolean insertVG(UnaugmentedQuadriliteralRoot unaugmentedConjs) {
        int conjNo = 1;

        insertionConj(rootId, conjNo, unaugmentedConjs.getTransitive().charAt(0));
        verbConj(conjNo, unaugmentedConjs);
        gerundConj(conjNo, unaugmentedConjs);

        return true;
    }

    private boolean insertVG(AugmentedQuadriliteralRoot augmentedRoot) {
        int conjNo;
        AugmentationFormula formula;
        Iterator itr;
        itr = augmentedRoot.getAugmentationList().iterator();
        while (itr.hasNext()) {
            formula = (AugmentationFormula) itr.next();
            conjNo = formula.getFormulaNo();

            insertionConj(rootId, conjNo + 1, formula.getTransitive());
            verbConj(conjNo, augmentedRoot);
            gerundConj(conjNo, augmentedRoot);

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

    private void verbConj(int conjNo, UnaugmentedQuadriliteralRoot unaugmentedConjs) {

        //active
        insertVerbs(rootId, conjNo, 'P', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.active.ActivePastConjugator.getInstance().createVerbList(unaugmentedConjs), false, false, kov, true, SystemConstants.PAST_TENSE));
        insertVerbs(rootId, conjNo, 'N', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createNominativeVerbList(unaugmentedConjs), false, false, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'A', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createAccusativeVerbList(unaugmentedConjs), false, false, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'J', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createJussiveVerbList(unaugmentedConjs), false, false, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'E', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createEmphasizedVerbList(unaugmentedConjs), false, false, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'I', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.UnaugmentedImperativeConjugator.getInstance().createVerbList(unaugmentedConjs), false, false, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE));
        insertVerbs(rootId, conjNo, 'M', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.UnaugmentedImperativeConjugator.getInstance().createEmphasizedVerbList(unaugmentedConjs), false, false, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE));
        //Passive verb
        insertVerbs(rootId, conjNo, 'P', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.passive.PassivePastConjugator.getInstance().createVerbList(unaugmentedConjs), false, false, kov, false, SystemConstants.PAST_TENSE));
        insertVerbs(rootId, conjNo, 'N', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createNominativeVerbList(unaugmentedConjs), false, false, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'A', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createAccusativeVerbList(unaugmentedConjs), false, false, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'J', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createJussiveVerbList(unaugmentedConjs), false, false, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo, 'E', new RootInfo(unaugmentedConjs, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createEmphasizedVerbList(unaugmentedConjs), false, false, kov, false, SystemConstants.PRESENT_TENSE));

    }

    private void gerundConj(int conjNo, UnaugmentedQuadriliteralRoot unaugmentedConjs) {
        //original gerund
        QuadriliteralUnaugmentedGerundConjugator2 qunggc = QuadriliteralUnaugmentedGerundConjugator2.getInstance();

        List gerunds = qunggc.createGerundList(unaugmentedConjs);
        gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(unaugmentedConjs, conjNo, kov, gerunds).getFinalResult();
        insertGerunds(gerunds, 'O', "فِعْلال", rootId, conjNo);

        if (unaugmentedConjs.getC1() == unaugmentedConjs.getC3() && unaugmentedConjs.getC2() == unaugmentedConjs.getC4()) {
            gerunds = qunggc.createGerundListForm2(unaugmentedConjs);
            gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(unaugmentedConjs, conjNo, kov, gerunds).getFinalResult();

            insertGerunds(gerunds, 'O', "فَعْلَلَة", rootId, conjNo);
        }
        //nomen

        gerunds = sarf.gerund.quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedConjs);
        gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(unaugmentedConjs, conjNo, kov, gerunds).getFinalResult();

        insertGerunds(gerunds, 'N', null, rootId, conjNo);

        //meem
        gerunds = sarf.noun.quadriliteral.unaugmented.UnaugmentedQuadriliteralPassiveParticipleConjugator.getInstance().createMeemGerundNounList(unaugmentedConjs);//quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedConjs);
        gerunds = sarf.noun.quadriliteral.modifier.passiveparticiple.PassiveParticipleModifier.getInstance().build(unaugmentedConjs, conjNo, kov, gerunds).getFinalResult();

        insertGerunds(gerunds, 'M', null, rootId, conjNo);

    }

    private void verbConj(int conjNo, AugmentedQuadriliteralRoot augmentedRoot) {
        insertVerbs(rootId, conjNo + 1, 'P', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.active.past.AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot, conjNo), false, true, kov, true, SystemConstants.PAST_TENSE));
        insertVerbs(rootId, conjNo + 1, 'N', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo + 1, 'A', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo + 1, 'J', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo + 1, 'E', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, true, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo + 1, 'I', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.imperative.AugmentedImperativeConjugator.getInstance().getNotEmphsizedConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE));
        insertVerbs(rootId, conjNo + 1, 'M', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.imperative.AugmentedImperativeConjugator.getInstance().getEmphsizedConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE));
        //passive verbs;
        insertVerbs(rootId, conjNo + 1, 'P', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.passive.past.AugmentedPassivePastConjugator.getInstance().createVerbList(augmentedRoot, conjNo), false, true, kov, false, SystemConstants.PAST_TENSE));
        insertVerbs(rootId, conjNo + 1, 'N', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo + 1, 'A', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo + 1, 'J', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, false, SystemConstants.PRESENT_TENSE));
        insertVerbs(rootId, conjNo + 1, 'E', new RootInfo(augmentedRoot, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, conjNo), false, true, kov, false, SystemConstants.PRESENT_TENSE));

    }

    private void gerundConj(int conjNo, AugmentedQuadriliteralRoot augmentedRoot) {
        List gerunds;
        //original
        gerunds = QuadriliteralAugmentedGerundConjugator2.getInstance().createGerundList(augmentedRoot, conjNo);
        gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(augmentedRoot, conjNo, kov, gerunds).getFinalResult();
        insertGerunds(gerunds, 'O', null, rootId, conjNo + 1);
        //nomen
        gerunds = QuadriliteralAugmentedNomenGerundConjugator2.getInstance().createGerundList(augmentedRoot, conjNo);
        gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(augmentedRoot, conjNo, kov, gerunds).getFinalResult();
        insertGerunds(gerunds, 'N', null, rootId, conjNo + 1);

        //meem
        gerunds = sarf.noun.quadriliteral.augmented.AugmentedQuadriliteralPassiveParticipleConjugator.getInstance().createMeemGerundNounList(augmentedRoot, conjNo);//quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedRoot);
        gerunds = sarf.noun.quadriliteral.modifier.passiveparticiple.PassiveParticipleModifier.getInstance().build(augmentedRoot, conjNo, kov, gerunds).getFinalResult();
        insertGerunds(gerunds, 'M', null, rootId, conjNo + 1);

    }

}
