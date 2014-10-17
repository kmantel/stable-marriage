import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Juggler extends MatchingPairItem<Circuit> implements Applicant<Circuit> {
    private int id;
    private int[] skills;
    private Map<Circuit, Integer> prefs;

    public Juggler(int id, int[] skills) {
        this.id = id;
        this.skills = skills;
        this.prefs = new HashMap<Circuit, Integer>();
    }

    public Integer getPreference(Circuit c) {
        return prefs.get(c);
    }

    public void setPreference(Circuit c, Integer value) {
        prefs.put(c, value);
    }

    public Integer removePreference(Circuit c) {
        return prefs.remove(c);
    }

    public Circuit getMostPreferred() {
        LinkedHashSet<Circuit> most = getKMostPreferred(1, prefs.keySet());
        if (most.size() == 0)
            return null;
        else
            return most.iterator().next();
    }

    public int id() { return id; }
    public int[] skills() { return skills; }
    
    public String toString() {
        return "J" + id;
    }

    public int hashCode() {
        return id;
    }
}