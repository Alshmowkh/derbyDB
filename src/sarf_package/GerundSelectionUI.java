package sarf_package;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import sarf.ui.*;
import sarf.verb.quadriliteral.augmented.*;
import sarf.verb.quadriliteral.unaugmented.*;
import sarf.verb.trilateral.augmented.*;
import sarf.verb.quadriliteral.QuadrilateralRoot;
import sarf.Action;
import sarf.noun.quadriliteral.augmented.AugmentedQuadriliteralPassiveParticipleConjugator;
import sarf.noun.trilateral.augmented.AugmentedTrilateralPassiveParticipleConjugator;
import sarf.noun.quadriliteral.unaugmented.UnaugmentedQuadriliteralPassiveParticipleConjugator;
import sarf.gerund.trilateral.augmented.TrilateralAugmentedGerundConjugatorListener;
import sarf.gerund.quadrilateral.unaugmented.QuadriliteralUnaugmentedGerundConjugatorListener;
import sarf.verb.trilateral.augmented.modifier.AugmentedTrilateralModifierListener;

/**
 * <p>
 * Title: Sarf Program</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ALEXO</p>
 *
 * @author Haytham Mohtasseb Billah
 * @version 1.0
 */
public class GerundSelectionUI implements TrilateralAugmentedGerundConjugatorListener, QuadriliteralUnaugmentedGerundConjugatorListener, AugmentedTrilateralModifierListener {

    private SelectionInfo selectionInfo;
    public int cashedPatternFormulaNo = -1;
    public int cashedQuadPatternFormulaNo = -1;
    private boolean opened = false;
    Boolean cashedUserResponse = null;

    public GerundSelectionUI() {

        sarf.gerund.trilateral.augmented.TrilateralAugmentedGerundConjugator.getInstance().setListener(this);
        sarf.gerund.trilateral.augmented.TrilateralAugmentedGerundConjugator.getInstance().setAugmentedTrilateralModifierListener(this);
        sarf.gerund.quadrilateral.unaugmented.QuadriliteralUnaugmentedGerundConjugator.getInstance().setListener(this);

     

    }

    public int selectPatternFormNo(int formulaNo) {
        if (cashedPatternFormulaNo != -1) {
            return cashedPatternFormulaNo;
        }
        return 1;
    }

    public int selectFormNo() {
        if (cashedQuadPatternFormulaNo != -1) {
            return cashedQuadPatternFormulaNo;
        }

      return 1;
    }

    //to let the user select when there is two states for the verb: with vocalization and without
    public boolean doSelectVocalization() {
        if (cashedUserResponse != null) {
            return cashedUserResponse.booleanValue();
        }
       
        //it must select one of two states
//        String msg = "لهذا الفعل حالتان : التصحيح والإعلال، اختر إحدى الحالتين";
//        Object[] options = {"التصحيح", "الإعلال"};
//        int optionResult = JOptionPane.showOptionDialog(null, msg, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
//        cashedUserResponse = new Boolean(optionResult == JOptionPane.NO_OPTION);
        return true;
    }

}
