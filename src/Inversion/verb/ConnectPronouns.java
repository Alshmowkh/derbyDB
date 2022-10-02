package Inversion.verb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConnectPronouns extends HashMap<Character, List<String>> {

    private final String[] verbSuffix = {"أ", "ن", "ت", "ت", "ت", "ت", "ت", "ي", "ت", "ي", "ت", "ي", "ي"};

    private final String[] PAST = {"تُ", "نَا", "تَ", "تِ", "تُمَا", "تُمْ", "تُنَّ", "", "تْ", "ا", "تَا", "وا", "نَ", "تُ"};
    private final String[] NOMINATIVE = {"", "", "", "ينَ", "انِ", "ونَ", "نَ", "", "", "انِ", "انِ", "ونَ", "نَ"};
    private final String[] ACCUSATIVE = {"", "", "", "ي", "ا", "وا", "نَ", "", "", "ا", "ا", "وا", "نَ"};

    private final String[] EMPHASIZE = {"نَّ", "نَّ", "نَّ", "نَّ", "انِّ", "نَّ", "نَانِّ", "نَّ", "نَّ", "انِّ", "انِّ", "نَّ", "نَانِّ"};
    private final String[] IMPERITIVE = {"", "", "", "ي", "ا", "وا", "نَ", "", "", "", "", "", ""};
    private final String[] iEMPHASIZE = {"", "", "نَّ", "نَّ", "انِّ", "نَّ", "نَانِّ", "", "", "", "", "", ""};

    public ConnectPronouns() {
        this.put('X', Arrays.asList(verbSuffix));
        this.put('P', Arrays.asList(PAST));
        this.put('N', Arrays.asList(NOMINATIVE));
        this.put('A', Arrays.asList(ACCUSATIVE));
        this.put('J', Arrays.asList(ACCUSATIVE));
        this.put('E', Arrays.asList(EMPHASIZE));
        this.put('I', Arrays.asList(IMPERITIVE));
        this.put('M', Arrays.asList(iEMPHASIZE));

    }

    public static ConnectPronouns getInstance() {
        return new ConnectPronouns();
    }

}
