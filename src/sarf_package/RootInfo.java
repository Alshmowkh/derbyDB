package sarf_package;

import java.util.List;

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
public class RootInfo {

    public boolean trilateral;
    private boolean augmented;
    private boolean active;
    private final int kov;

    private Object root;

    private int augmentationFormulaNo;

    private String formulaText;
    private String verbText;
    private String syntax;
    private final List list;
//    private final String conjugation;
    private final String tense;

    public RootInfo(Object root, List list, boolean trilateral, boolean augmented, int kov, boolean active, String tens) {
        this.root = root;
        this.list = list;
        this.trilateral = trilateral;
        this.augmented = augmented;
        this.kov = kov;
        this.active = active;
        this.tense = tens;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isTrilateral() {
        return trilateral;
    }

    public boolean isAugmented() {
        return augmented;
    }

    public Object getRoot() {
        return root;
    }

    public int getAugmentationFormulaNo() {
        return augmentationFormulaNo;
    }

    public String getFormulaText() {
        return formulaText;
    }

    public String getVerbText() {
        return verbText;
    }

    public int getKov() {
        return kov;
    }

    public List getList() {
        return list;
    }

//    public String getConjugation() {
//        return conjugation;
//    }
    public String getTense() {
        return tense;
    }

    public void setTrilateral(boolean trilateral) {
        this.trilateral = trilateral;
    }

    public void setAugmented(boolean augmented) {
        this.augmented = augmented;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRoot(Object root) {
        this.root = root;
    }

    public void setAugmentationFormulaNo(int augmentationFormulaNo) {
        this.augmentationFormulaNo = augmentationFormulaNo;
    }

    public void setVerbText(String verbText) {
        this.verbText = verbText;
    }

    public void setFormulaText(String formulaText) {
        this.formulaText = formulaText;
    }

}
