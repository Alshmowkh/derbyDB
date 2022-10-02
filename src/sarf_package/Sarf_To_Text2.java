package sarf_package;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
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
import sarf.gerund.trilateral.unaugmented.QualityGerundConjugator;
import sarf.gerund.trilateral.unaugmented.TrilateralUnaugmentedGerundConjugator;
import sarf.gerund.trilateral.unaugmented.TrilateralUnaugmentedNomenGerundConjugator;
import sarf.gerund.trilateral.unaugmented.meem.MeemGerundConjugator;
import sarf.kov.KovRulesManager;
import sarf.kov.QuadrilateralKovRule;
import sarf.kov.TrilateralKovRule;
import sarf.noun.trilateral.augmented.AugmentedTrilateralPassiveParticipleConjugator;
//import sarf.ui.controlpane.GerundSelectionUI;
import sarf.verb.quadriliteral.augmented.AugmentedQuadriliteralRoot;
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
import sarf.verb.trilateral.unaugmented.active.ActivePastConjugator;
import sarf.verb.trilateral.unaugmented.active.ActivePresentConjugator;
import sarf.verb.trilateral.unaugmented.modifier.UnaugmentedTrilateralModifier;
import sarf.verb.trilateral.unaugmented.passive.PassivePastConjugator;
import sarf.verb.trilateral.unaugmented.passive.PassivePresentConjugator;
import static sarf_package.SarfExporter.pl;

public class Sarf_To_Text2 {

    List<String> importRoots() {
        List<String> rawroots = new ArrayList();
        try {
            DocumentBuilderFactory dbf;
            DocumentBuilder db;
            Document docimport;
            NodeList rootsNodes;
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            docimport = db.parse(new File("F:/Master/Thesis/Implementations/SARF_10/ALshmowkh_db/TrilateralRootsNoDuplicate.xml"));
            rootsNodes = docimport.getElementsByTagName("root");
            for (int i = 0; i < rootsNodes.getLength(); i++) {
                String rot = rootsNodes.item(i).getAttributes().getNamedItem("value").getNodeValue();
                rawroots.add(rot);
            }

        } catch (Exception e) {

        }
        return rawroots;
    }

    List<String> import4Roots() {
        List<String> rawroots = new ArrayList();
        try {
            DocumentBuilderFactory dbf;
            DocumentBuilder db;
            Document docimport;
            NodeList rootsNodes;
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            docimport = db.parse(new File("F:/Master/Thesis/Implementations/SARF_10/ALshmowkh_db/QuadRoots.xml"));
            rootsNodes = docimport.getElementsByTagName("root");
            for (int i = 0; i < rootsNodes.getLength(); i++) {
                String rot = rootsNodes.item(i).getAttributes().getNamedItem("value").getNodeValue();
                rawroots.add(rot);
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {

        }
        return rawroots;
    }

    void roots() throws SAXException, IOException, ParserConfigurationException, SQLException {
        List<String> roots = importRoots();
        int rootId = 0;
        String form;
        for (int i = 0; i < roots.size(); i++) {
            String rootIn = roots.get(i);
//            form = KovRulesManager.getInstance().getTrilateralKovRule(rootIn.charAt(0), rootIn.charAt(1), rootIn.charAt(2)).getDesc();
            rootId++;
//            insertionRoot(rootId, rootIn, 'T', form);
            pl(rootIn + " : " + rootId + " : ");
        }

    }

    String getAugTrans(char chr
    ) {
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

    List<String> getTriUnAugConjs(int id, String rootText) throws SQLException {
//        List<String> conjugations = new ArrayList();
        String[] conjs = new String[18];
        Arrays.fill(conjs, "0");

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
                conjs[posConj - 1] = pastRootText + "/" + presentRootText;
//                insertionData(id, posConj, pastRootText, presentRootText, getAugTrans(sarfRoot.getTransitive().charAt(0)));
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
                conjs[formulaNo + 5] = pastRootText + "/" + presentRootText;

//                insertionData(id, formulaNo + 6, pastRootText, presentRootText, getAugTrans(formula.getTransitive()));
//                System.out.println(id + " " + (formulaNo + 6) + " " + pastRootText + " " + presentRootText + " " + getAugTrans(formula.getTransitive()));
            }
//            pl(id + ": " + rootText);
        }
        return Arrays.asList(conjs);
    }

    void rootVerbs(String rootText) throws SQLException {
        //i<=5674

        int rootId = 0;

        TrilateralKovRule rule;// = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
        int kov;
        List unaugmentedList;
        AugmentedTrilateralRoot augmentedRoot;

        rule = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
        kov = rule.getKov();
        unaugmentedList = SarfDictionary.getInstance().getUnaugmentedTrilateralRoots(rootText);
        Iterator itr = unaugmentedList.iterator();
        UnaugmentedTrilateralRoot sarfRoot;
        while (itr.hasNext()) {
            sarfRoot = (UnaugmentedTrilateralRoot) itr.next();
            //active verbs
            rootVerb(rootId, 'P', new RootInfo(sarfRoot, ActivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.PAST_TENSE));
            rootVerb(rootId, 'N', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
            rootVerb(rootId, 'A', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
            rootVerb(rootId, 'J', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
            rootVerb(rootId, 'E', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE));
            rootVerb(rootId, 'I', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE));
            rootVerb(rootId, 'M', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE));
            //passive verbs
            rootVerb(rootId, 'P', new RootInfo(sarfRoot, PassivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, false, SystemConstants.PAST_TENSE));
            rootVerb(rootId, 'N', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
            rootVerb(rootId, 'A', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
            rootVerb(rootId, 'J', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
            rootVerb(rootId, 'E', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE));
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
                rootVerb(rootId, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                rootVerb(rootId, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                rootVerb(rootId, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                rootVerb(rootId, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                rootVerb(rootId, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                rootVerb(rootId, 'I', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getNotEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                rootVerb(rootId, 'M', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""));
                // passive verbs
                rootVerb(rootId, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                rootVerb(rootId, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                rootVerb(rootId, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                rootVerb(rootId, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));
                rootVerb(rootId, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""));

            }
        }
    }

    void ConjuagationsAvarage() throws SQLException, SAXException, IOException, ParserConfigurationException {
        List<String> roots = importRoots();
        Iterator<String> itr = roots.iterator();
        List<String> conjs;
        int max = 0, size = 0, sum = 0;
        String hasmax = "", root;
        while (itr.hasNext()) {
            root = itr.next();
            conjs = getTriUnAugConjs(0, root);
            size = conjs.size();
            sum += size;
            if (size > max) {
                max = size;
                hasmax = root;
            }
        }
        pl("Maximum root conjugations size is : " + max + "   for root :" + hasmax);
        double av = sum / roots.size();
        pl("Avarage: " + av);
    }

    List<String> rootVerb2(int rootId, char tense, RootInfo rootInfo) throws SQLException {
        List finalresult = null;
//        int conjNo = 0;
        if (!rootInfo.isAugmented()) {
            UnaugmentedTrilateralRoot sarfroot = (UnaugmentedTrilateralRoot) rootInfo.getRoot();
//            conjNo = Integer.parseInt(sarfroot.getConjugation());
//            pl(rootInfo.getList());
            ConjugationResult conjResult = UnaugmentedTrilateralModifier.getInstance().build(sarfroot, rootInfo.getKov(), rootInfo.getList(), rootInfo.getTense(), rootInfo.isActive());
            finalresult = conjResult.getFinalResult();
        } else if (rootInfo.isAugmented()) {
            finalresult = rootInfo.getList();
//            conjNo = rootInfo.getKov();
        }

        String[] verbs = new String[13];
        if (finalresult != null) {
            for (int p = 0; p < 13; p++) {

                Object o = finalresult.get(p);
                verbs[p] = o != null ? o.toString() : "0";

            }
        }
        pl(this.flatList(Arrays.asList(verbs)));
        return Arrays.asList(verbs);
    }

    String rootVerb(int rootId, char tense, RootInfo rootInfo) {
        List finalresult = null;
//        int conjNo = 0;
        if (!rootInfo.isAugmented()) {
            UnaugmentedTrilateralRoot sarfroot = (UnaugmentedTrilateralRoot) rootInfo.getRoot();
//            conjNo = Integer.parseInt(sarfroot.getConjugation());
//            pl(rootInfo.getList());
            ConjugationResult conjResult = UnaugmentedTrilateralModifier.getInstance().build(sarfroot, rootInfo.getKov(), rootInfo.getList(), rootInfo.getTense(), rootInfo.isActive());
            finalresult = conjResult.getFinalResult();
        } else if (rootInfo.isAugmented()) {
            finalresult = rootInfo.getList();
//            conjNo = rootInfo.getKov();
        }

        String verb;
        Utiles ut = new Utiles();
        StringBuilder sb = new StringBuilder();
        sb.append(tense).append("(");
        if (finalresult != null) {
            for (int p = 0; p < 13; p++) {
                Object o = finalresult.get(p);
                verb = o != null ? o.toString() : "0";
                sb.append(ut.deDiacritic(verb)).append(",");
            }
            sb.deleteCharAt(sb.length() - 1).append(")");
        }
        return sb.toString();
    }

    String rootVerbs2(String rootText) {
        //i<=5674
        int rootId = 0;
        TrilateralKovRule rule;
        int kov;
        List unaugmentedList;
        AugmentedTrilateralRoot augmentedRoot;
        UnaugmentedTrilateralRoot sarfRoot;
        Iterator itr;
        StringBuilder sb = new StringBuilder();

        rule = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
        kov = rule.getKov();
        unaugmentedList = SarfDictionary.getInstance().getUnaugmentedTrilateralRoots(rootText);
        itr = unaugmentedList.iterator();

        sb.append(rootText).append("(");

        while (itr.hasNext()) {
            sarfRoot = (UnaugmentedTrilateralRoot) itr.next();
            sb.append("C").append(sarfRoot.getConjugation()).append("(");
            sb.append("Av").append("(");
            //active verbs
            sb.append(rootVerb(rootId, 'P', new RootInfo(sarfRoot, ActivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.PAST_TENSE))).append(",");
            
            sb.append(rootVerb(rootId, 'N', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'A', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'J', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'E', new RootInfo(sarfRoot, ActivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'I', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'M', new RootInfo(sarfRoot, UnaugmentedImperativeConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE)));
            sb.append(")").append(",");
            //passive verbs
            sb.append("Pv").append("(");
            sb.append(rootVerb(rootId, 'P', new RootInfo(sarfRoot, PassivePastConjugator.getInstance().createVerbList(sarfRoot), true, false, kov, false, SystemConstants.PAST_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'N', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createNominativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'A', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createAccusativeVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'J', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createJussiveVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(rootVerb(rootId, 'E', new RootInfo(sarfRoot, PassivePresentConjugator.getInstance().createEmphasizedVerbList(sarfRoot), true, false, kov, false, SystemConstants.PRESENT_TENSE)));
            sb.append(")");
            sb.append(")").append(",");
        }
        augmentedRoot = SarfDictionary.getInstance().getAugmentedTrilateralRoot(rootText);
        if (augmentedRoot != null) {
            itr = augmentedRoot.getAugmentationList().iterator();
            AugmentationFormula formula;
            int formulaNo;
            while (itr.hasNext()) {
                formula = (AugmentationFormula) itr.next();
                formulaNo = formula.getFormulaNo();
                sb.append("C").append(formulaNo + 6).append("(");
                //active verbs
                sb.append("Av").append("(");
                sb.append(rootVerb(rootId, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""))).append(",");
                sb.append(rootVerb(rootId, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""))).append(",");
                sb.append(rootVerb(rootId, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""))).append(",");
                sb.append(rootVerb(rootId, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""))).append(",");
                sb.append(rootVerb(rootId, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedActivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""))).append(",");
                sb.append(rootVerb(rootId, 'I', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getNotEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, ""))).append(",");
                sb.append(rootVerb(rootId, 'M', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedImperativeConjugator.getInstance().getEmphsizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.EMPHASIZED_IMPERATIVE_TENSE, true, null).getFinalResult(), true, true, formulaNo + 6, true, "")));
                sb.append(")").append(",");
                // passive verbs
                sb.append("Pv").append("(");
                sb.append(rootVerb(rootId, 'P', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePastConjugator.getInstance().createVerbList(augmentedRoot, formulaNo), SystemConstants.PAST_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""))).append(",");
                sb.append(rootVerb(rootId, 'N', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""))).append(",");
                sb.append(rootVerb(rootId, 'A', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""))).append(",");
                sb.append(rootVerb(rootId, 'J', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, ""))).append(",");
                sb.append(rootVerb(rootId, 'E', new RootInfo(null, AugmentedTrilateralModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedPassivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot, formulaNo), SystemConstants.PRESENT_TENSE, false, null).getFinalResult(), true, true, formulaNo + 6, false, "")));
                sb.append(")");
                sb.append(")").append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        sb.append("\n");
        return sb.toString();
    }

    String flatList(List<String> list
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        list.stream().forEach((s) -> {
            sb.append(s).append(",");
        });
        sb.append(")");
        return sb.toString();
    }

    void exportSARFtext() throws SQLException, IOException {
        List<String> roots = importRoots();
        Iterator<String> itr;
        FileWriter fw = new FileWriter("./dbText/DBverbsDeDiac05.txt");
        String line;
        itr = roots.iterator();
        while (itr.hasNext()) {
            line = this.rootVerbs2(itr.next());
            fw.write(line);
        }
        fw.close();
        pl("have done...");
    }

    boolean insertGerund(String gerund, String noDiac, char nominal, String formula, char mood, char number, char gender, int rootId, int conjNo) throws SQLException {
//        Connection conn2 = DriverManager.getConnection("jdbc:derby:" + "sarfDB_02" + ";create=true");
//        ps = conn2.prepareStatement("delete from gerunds");
//        ps.execute();
//        conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_01");
        String stmt;

        stmt = gerund + "','" + noDiac + "','" + nominal + "','" + formula + "','" + mood + "','" + number + "','" + gender + "'," + rootId + "," + conjNo;

        pl(stmt);
        return true;
    }

    String insertGerunds(List gerunds) {
        StringBuilder sb = new StringBuilder();
        Utiles ut = new Utiles();
        for (int i = 0; i < gerunds.size(); i++) {
            sb.append(!gerunds.get(i).toString().trim().isEmpty() ? ut.deDiacritic(gerunds.get(i).toString()) + "," : "");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    String rootGerunds(String rootText) throws SQLException {
        //i<=5674

        String formula = "";
        int rootId = 0;

        TrilateralKovRule rule;
        int kov, conjNo;
        List unaugmentedList;
        List formulas;
        List gerunds;
        AugmentedTrilateralRoot augmentedRoot;
        StringBuilder sb = new StringBuilder();
        rule = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
        kov = rule.getKov();
        unaugmentedList = SarfDictionary.getInstance().getUnaugmentedTrilateralRoots(rootText);
        Iterator itr = unaugmentedList.iterator();
        UnaugmentedTrilateralRoot sarfRoot;
        Iterator itr2;
        sb.append(rootText).append("(");
        while (itr.hasNext()) {

            sarfRoot = (UnaugmentedTrilateralRoot) itr.next();
            conjNo = Integer.parseInt(sarfRoot.getConjugation());
            sb.append("C").append(conjNo).append("(");
            //original gerund
            sb.append("O").append("(");
            formulas = TrilateralUnaugmentedGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
            itr2 = formulas.iterator();
            while (itr2.hasNext()) {
                formula = itr2.next().toString();
                gerunds = UnaugmentedTrilateralStandardGerundModifier.getInstance().build(sarfRoot, kov, TrilateralUnaugmentedGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult();
                sb.append(insertGerunds(gerunds));
            }

            sb.append(")").append(",");
            //meem
            sb.append("M").append("(");
            formulas = MeemGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
            itr2 = formulas.iterator();
            while (itr2.hasNext()) {
                formula = itr2.next().toString();
                gerunds = TitlateralUnaugmentedMeemModifier.getInstance().build(sarfRoot, kov, MeemGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult();
                sb.append(insertGerunds(gerunds));

            }

            sb.append(")").append(",");
            //nomen
            sb.append("N").append("(");
            formulas = TrilateralUnaugmentedNomenGerundConjugator.getInstance().getAppliedFormulaList(sarfRoot);
            itr2 = formulas.iterator();
            while (itr2.hasNext()) {
                formula = itr2.next().toString();
                gerunds = TitlateralUnaugmentedNomenModifier.getInstance().build(sarfRoot, kov, TrilateralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult();
                sb.append(insertGerunds(gerunds));
            }
            sb.append(")").append(",");
            //quality
            sb.append("Q").append("(");
            formula = "فِعْلَة";
            gerunds = TitlateralUnaugmentedQualityModifier.getInstance().build(sarfRoot, kov, QualityGerundConjugator.getInstance().createGerundList(sarfRoot, formula), formula).getFinalResult();
            sb.append(insertGerunds(gerunds));
            sb.append(")");

            sb.append(")").append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        GerundSelectionUI gs = new GerundSelectionUI();
        TrilateralAugmentedGerundConjugator tagc;
        augmentedRoot = SarfDictionary.getInstance().getAugmentedTrilateralRoot(rootText);
        if (augmentedRoot != null) {
            itr = augmentedRoot.getAugmentationList().iterator();
            AugmentationFormula formula2;
            int formulaNo;
            while (itr.hasNext()) {
                formula2 = (AugmentationFormula) itr.next();
                formulaNo = formula2.getFormulaNo();
                sb.append(",");
                sb.append("C").append(formulaNo + 6).append("(");

                sb.append("O").append("(");
                gs.cashedPatternFormulaNo = 1;
                gs.cashedUserResponse = true;
                tagc = TrilateralAugmentedGerundConjugator.getInstance();
                tagc.setListener(gs);
                tagc.setAugmentedTrilateralModifierListener(gs);
                gerunds = tagc.createGerundList(augmentedRoot, formulaNo);
                gerunds = TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult();
                sb.append(insertGerunds(gerunds));
                sb.deleteCharAt(sb.length() - 1);

                if ((formulaNo == 2 && augmentedRoot.getC3() == 'ء') || (formulaNo == 3 && augmentedRoot.getC1() != 'ي')) {
//                     String msg = "لهذا المصدر وزنان: (تفعيل) و(تفعلة)، اختر أحدهما";2
//                        String msg = "لهذا المصدر وزنان (مفاعلة) و  (فعال) اختر أحدهما";3
                    gs.cashedPatternFormulaNo = 2;
                    tagc = TrilateralAugmentedGerundConjugator.getInstance();
                    tagc.setListener(gs);
                    tagc.setAugmentedTrilateralModifierListener(gs);
                    gerunds = tagc.createGerundList(augmentedRoot, formulaNo);
                    gerunds = TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, gerunds, null).getFinalResult();
                    sb.append(",");
                    sb.append(insertGerunds(gerunds));
                    sb.deleteCharAt(sb.length() - 1);

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
                        sb.append(",");
                        sb.append(insertGerunds(gerunds));
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }

                sb.append(")");
                sb.append(",");

                sb.append("N").append("(");
                gerunds = TitlateralAugmentedStandardModifier.getInstance().build(augmentedRoot, kov, formulaNo, TrilateralAugmentedNomenGerundConjugator.getInstance().createGerundList(augmentedRoot, formulaNo), null).getFinalResult();
                sb.append(insertGerunds(gerunds));
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")").append(",");

                sb.append("M").append("(");
                gerunds = sarf.noun.trilateral.augmented.modifier.passiveparticiple.PassiveParticipleModifier.getInstance().build(augmentedRoot, kov, formulaNo, AugmentedTrilateralPassiveParticipleConjugator.getInstance().createMeemGerundNounList(augmentedRoot, formulaNo), null).getFinalResult();
                sb.append(insertGerunds(gerunds));
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")");
                sb.append(")").append(",");
            }
        }
        sb.append(")");
        sb.append("\n");
        return sb.toString();

    }

    void exportGerunds() throws SQLException, IOException {
        List<String> roots = importRoots();
        String line;
        Iterator<String> itr;

        FileWriter fw = new FileWriter("./dbText/dbgerund02.txt");
        itr = roots.iterator();
        while (itr.hasNext()) {
            line = rootGerunds(itr.next());
            fw.write(line);
        }
        fw.close();
        pl("have done...");
    }

    String root4Verb(int rootId, char tense, RootInfo rootInfo
    ) {
        List finalresult = null;
//        int conjNo = 0;
        if (!rootInfo.isAugmented()) {
            UnaugmentedQuadriliteralRoot sarfroot4 = (UnaugmentedQuadriliteralRoot) rootInfo.getRoot();
//            sarf.verb.quadriliteral.ConjugationResult conjResult4 = sarf.verb.quadriliteral.modifier.QuadrilateralModifier.getInstance().build(sarfroot4, rootInfo.getKov(), rootInfo.getList(), conjugations, rootInfo.getTense(), true);
            sarf.verb.quadriliteral.ConjugationResult conjResult4 = sarf.verb.quadriliteral.modifier.QuadrilateralModifier.getInstance().build(sarfroot4, rootInfo.getKov(), 0, rootInfo.getList(), rootInfo.getTense(), rootInfo.isActive());
            finalresult = conjResult4.getFinalResult();

        } else if (rootInfo.isAugmented()) {
            finalresult = rootInfo.getList();
        }

        String verb;
        Utiles ut = new Utiles();
        StringBuilder sb = new StringBuilder();
        sb.append(tense).append("(");
        if (finalresult != null) {
            for (int p = 0; p < 13; p++) {

                Object o = finalresult.get(p);
                verb = o != null ? o.toString() : "0";
                sb.append(ut.deDiacritic(verb)).append(",");
            }
            sb.deleteCharAt(sb.length() - 1).append(")");
        }
        return sb.toString();
    }

    String root4Verbs(String rootText
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(rootText).append("(");
        QuadrilateralKovRule rule = KovRulesManager.getInstance().getQuadrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2), rootText.charAt(3));
        int kov = rule.getKov();
        sb.append("C1").append("(");
        UnaugmentedQuadriliteralRoot unaugmentedRoot4 = SarfDictionary.getInstance().getUnaugmentedQuadrilateralRoot(rootText);
        //Active verb
        if (unaugmentedRoot4 != null) {
            sb.append("Av").append("(");
            sb.append(root4Verb(0, 'P', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePastConjugator.getInstance().createVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PAST_TENSE))).append(",");
            sb.append(root4Verb(0, 'N', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createNominativeVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(root4Verb(0, 'A', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createAccusativeVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(root4Verb(0, 'J', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createJussiveVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(root4Verb(0, 'E', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.active.ActivePresentConjugator.getInstance().createEmphasizedVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(root4Verb(0, 'I', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.UnaugmentedImperativeConjugator.getInstance().createVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE))).append(",");
            sb.append(root4Verb(0, 'M', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.UnaugmentedImperativeConjugator.getInstance().createEmphasizedVerbList(unaugmentedRoot4), false, false, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE)));
            sb.append(")").append(",");
            //Passive verb
            sb.append("Pv").append("(");
            sb.append(root4Verb(0, 'P', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePastConjugator.getInstance().createVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PAST_TENSE))).append(",");
            sb.append(root4Verb(0, 'N', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createNominativeVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(root4Verb(0, 'A', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createAccusativeVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(root4Verb(0, 'J', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createJussiveVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
            sb.append(root4Verb(0, 'E', new RootInfo(unaugmentedRoot4, sarf.verb.quadriliteral.unaugmented.passive.PassivePresentConjugator.getInstance().createEmphasizedVerbList(unaugmentedRoot4), false, false, kov, false, SystemConstants.PRESENT_TENSE)));
            sb.append(")");
        }
        sb.append(")");

        AugmentedQuadriliteralRoot augmentedRoot4 = SarfDictionary.getInstance().getAugmentedQuadrilateralRoot(rootText);
        AugmentationFormula formula;
        Iterator itr;

        if (augmentedRoot4 != null) {

            itr = augmentedRoot4.getAugmentationList().iterator();
            while (itr.hasNext()) {
                formula = (AugmentationFormula) itr.next();
                sb.append(",");
                sb.append("C").append(formula.getFormulaNo() + 1).append("(");
                //active verbs  
                sb.append("Av").append("(");
                sb.append(root4Verb(0, 'P', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.past.AugmentedActivePastConjugator.getInstance().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PAST_TENSE))).append(",");
                sb.append(root4Verb(0, 'N', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
                sb.append(root4Verb(0, 'A', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
                sb.append(root4Verb(0, 'J', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
                sb.append(root4Verb(0, 'E', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.active.present.AugmentedActivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.PRESENT_TENSE))).append(",");
                sb.append(root4Verb(0, 'I', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.imperative.AugmentedImperativeConjugator.getInstance().getNotEmphsizedConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.NOT_EMPHASIZED_IMPERATIVE_TENSE))).append(",");
                sb.append(root4Verb(0, 'M', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.imperative.AugmentedImperativeConjugator.getInstance().getEmphsizedConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, true, SystemConstants.EMPHASIZED_IMPERATIVE_TENSE)));
                sb.append(")").append(",");
                //passive verbs;
                sb.append("Pv").append("(");
                sb.append(root4Verb(0, 'P', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.past.AugmentedPassivePastConjugator.getInstance().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PAST_TENSE))).append(",");
                sb.append(root4Verb(0, 'N', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getNominativeConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
                sb.append(root4Verb(0, 'A', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getAccusativeConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
                sb.append(root4Verb(0, 'J', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getJussiveConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PRESENT_TENSE))).append(",");
                sb.append(root4Verb(0, 'E', new RootInfo(augmentedRoot4, sarf.verb.quadriliteral.augmented.passive.present.AugmentedPassivePresentConjugator.getInstance().getEmphasizedConjugator().createVerbList(augmentedRoot4, formula.getFormulaNo()), false, true, kov, false, SystemConstants.PRESENT_TENSE)));
                sb.append(")");
                sb.append(")").append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")").append("\n");
        return sb.toString();
    }

    void export4Verbs() throws IOException {
        List<String> roots = importRoots();
        Iterator<String> itr;
        FileWriter fw = new FileWriter("./dbText/DBverbsNoDiac03.txt");
        String line;

        itr = roots.iterator();
        while (itr.hasNext()) {
            line = this.rootVerbs2(itr.next());
            fw.write(line);
        }
        roots = import4Roots();
        itr = roots.iterator();
        while (itr.hasNext()) {
            line = this.root4Verbs(itr.next());
            fw.write(line);
        }

        fw.close();
        pl("have done...");
    }

    String root4Gerunds(String rootText) throws SQLException {
        TrilateralKovRule rule;
        int kov;
        List gerunds;
        StringBuilder sb = new StringBuilder();
        Utiles ut = new Utiles();
        rule = KovRulesManager.getInstance().getTrilateralKovRule(rootText.charAt(0), rootText.charAt(1), rootText.charAt(2));
        kov = rule.getKov();
        UnaugmentedQuadriliteralRoot unaugmentedRoot4 = SarfDictionary.getInstance().getUnaugmentedQuadrilateralRoot(rootText);

        sb.append(rootText).append("(");
        if (unaugmentedRoot4 != null) {

            sb.append("C").append("1").append("(");
            //original gerund
            sb.append("O").append("(");
            QuadriliteralUnaugmentedGerundConjugator2 qunggc = QuadriliteralUnaugmentedGerundConjugator2.getInstance();
            gerunds = qunggc.createGerundList(unaugmentedRoot4);
            sb.append(insertGerunds(gerunds));
            if (unaugmentedRoot4.getC1() == unaugmentedRoot4.getC3() && unaugmentedRoot4.getC2() == unaugmentedRoot4.getC4()) {
                gerunds = qunggc.createGerundListForm2(unaugmentedRoot4);
                sb.append(insertGerunds(gerunds));
            }
            sb.append(")").append(",");

//            //nomen
            sb.append("N").append("(");
            gerunds = sarf.gerund.quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedRoot4);
            sb.append(insertGerunds(gerunds));
            sb.append(")").append(",");

            //meem
            sb.append("M").append("(");
            gerunds = sarf.noun.quadriliteral.unaugmented.UnaugmentedQuadriliteralPassiveParticipleConjugator.getInstance().createMeemGerundNounList(unaugmentedRoot4);//quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedRoot4);
            sb.append(insertGerunds(gerunds));
            sb.append(")");
            sb.append(")");
        }
        //------------------- end unaugmentedroot
        AugmentedQuadriliteralRoot augmentedRoot4 = SarfDictionary.getInstance().getAugmentedQuadrilateralRoot(rootText);
        Iterator itr;
        if (augmentedRoot4 != null) {
            itr = augmentedRoot4.getAugmentationList().iterator();
            AugmentationFormula formula;
            int formulaNo;
            while (itr.hasNext()) {
                formula = (AugmentationFormula) itr.next();
                formulaNo = formula.getFormulaNo();
                sb.append(",");
                sb.append("C").append(formulaNo + 1).append("(");
                //original
                sb.append("O").append("(");
                gerunds = QuadriliteralAugmentedGerundConjugator2.getInstance().createGerundList(augmentedRoot4, formulaNo);
                gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(augmentedRoot4, formulaNo, kov, gerunds).getFinalResult();
                sb.append(insertGerunds(gerunds));
                sb.append(")");
                //nomen
                sb.append(",").append("N").append("(");
                gerunds = QuadriliteralAugmentedNomenGerundConjugator2.getInstance().createGerundList(augmentedRoot4, formulaNo);
                gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(augmentedRoot4, formulaNo, kov, gerunds).getFinalResult();
                sb.append(insertGerunds(gerunds));

                sb.append(")");
                //meem
                sb.append(",").append("M").append("(");
                gerunds = sarf.noun.quadriliteral.augmented.AugmentedQuadriliteralActiveParticipleConjugator.getInstance().createNounList(augmentedRoot4, formulaNo);//quadrilateral.unaugmented.QuadriliteralUnaugmentedNomenGerundConjugator.getInstance().createGerundList(unaugmentedRoot4);
                gerunds = sarf.gerund.modifier.quadrilateral.QuadrilateralStandardModifier.getInstance().build(augmentedRoot4, formulaNo, kov, gerunds).getFinalResult();
                sb.append(insertGerunds(gerunds));
                sb.append(")");
                sb.append(")");
            }

        }
        sb.append(")");
        sb.append("\n");
        return sb.toString();

    }

    void exportGerunds4() throws SQLException, IOException {
        List<String> roots = importRoots();
        String line;
        Iterator<String> itr;

        FileWriter fw = new FileWriter("./dbText/Gerunds02.database");
        itr = roots.iterator();
        while (itr.hasNext()) {
            line = rootGerunds(itr.next());
            fw.write(line);
        }
        roots = import4Roots();
        itr = roots.iterator();
        while (itr.hasNext()) {
            line = root4Gerunds(itr.next());
            fw.write(line);
        }

        fw.close();
//        pl(line);

        pl("have done...");
    }

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, SQLException {
        Sarf_To_Text2 cls = new Sarf_To_Text2();
//        cls.exportGerunds4();
//        cls.export4Verbs();
//        cls.exportSARFtext();
//        cls.exportGerunds();
//        pl(cls.rootVerbs2("خدم"));  
//        pl(cls.root4Gerunds("لعلع"));
        new Utiles().printElements(cls.rootVerbs2("شهد"));
//        new Utiles().printGerund(cls.rootGerunds("خوص"));

    }

    private int checkGerundPattern(AugmentedTrilateralRoot root, int formulaNo) {
// من أجل صيغة مفاعلة وفعال ليختار أحدهما
        int ret = -1;
        boolean form2Applied, form3Applied;
//        return listener.selectPatternFormNo(formulaNo);
        if (formulaNo == 2) {
            //سيتم اختيار أحد القانونين
            if (root.getC3() == 'ء') {
                form3Applied = selectPatternFormNo(2) == 2;
            } //عرض في المؤنث للناقص
            else if (root.getC3() == 'و' || root.getC3() == 'ي') {
                form2Applied = true;
            }
        } else if (formulaNo == 3) {
            if (root.getC1() != 'ي') {
                form2Applied = selectPatternFormNo(3) == 2;
            }
        }
        return ret;
    }

    public int selectPatternFormNo(int formulaNo) {
        int cashedPatternFormulaNo = -1;

        if (cashedPatternFormulaNo != -1) {
            return cashedPatternFormulaNo;
        }

        if (formulaNo == 3) {
            //it must select one of two states
            String msg = "لهذا المصدر وزنان (مفاعلة) و  (فعال) اختر أحدهما";
            Object[] options = {"مفاعلة", "فِعال"};
            int optionResult = JOptionPane.showOptionDialog(null, msg, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            return cashedPatternFormulaNo = (optionResult == JOptionPane.NO_OPTION ? 2 : 1);
        }
        if (formulaNo == 2) {
            //it must select one of two states
            String msg = "لهذا المصدر وزنان: (تفعيل) و(تفعلة)، اختر أحدهما";
            Object[] options = {"تفعيل", "تفعلة"};
            int optionResult = JOptionPane.showOptionDialog(null, msg, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            return cashedPatternFormulaNo = (optionResult == JOptionPane.NO_OPTION ? 2 : 1);
        }
        return -1;
    }
}
