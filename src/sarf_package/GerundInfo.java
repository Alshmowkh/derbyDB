package sarf_package;

public class GerundInfo {

    private final boolean trilateral;
    private final boolean augmented;
    private final boolean active;
    private final int kov;

    private final Object root;

    private int augmentationFormulaNo;

    private String formulaText;
    private final int rootId;
    private final int conjNo;
//    private final List list;
//    private final String conjugation;
    private final String tense;

    public GerundInfo(int rotId, int conjno, Object root, boolean trilateral, boolean augmented, int kov, boolean active, String tens) {
        this.rootId = rotId;
        this.conjNo = conjno;
        this.root = root;
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

    public int rootId() {
        return rootId;
    }

    public int conjNo() {
        return conjNo;
    }

    public int getKov() {
        return kov;
    }

    public String getTense() {
        return tense;
    }

}
