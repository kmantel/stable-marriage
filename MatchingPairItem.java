import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/********************************************************************************
Provides some basic functionality for Programs and Applicants
    comparePreference
    getKMostPreferred
********************************************************************************/
public abstract class MatchingPairItem<T> {

    public abstract Integer getPreference(T t);

    public int comparePreference(T a, T b) {
        Integer prefA = getPreference(a);
        Integer prefB = getPreference(b);
        if (prefA == null && prefB == null)
            return 0;
        else if (prefA == null)
            return -1;
        else if (prefB == null)
            return 1;
        else
            return prefA - prefB;
    }

    // used to sort map entries by value, greatest to least
    static class DescendingValueComparator<K, V extends Comparable<V>> implements Comparator<Map.Entry<K, V>> {
        public int compare(Map.Entry<K,V> a, Map.Entry<K,V> b) {
            return (b.getValue().compareTo(a.getValue()));
        }
    }

    public LinkedHashSet<T> getKMostPreferred(int k, Set<T> items) {
        LinkedHashSet<T> result = new LinkedHashSet<T>();
        if (items == null)
            return result;

        Map<T, Integer> itemsPrefs = new HashMap<T, Integer>();
        for (T t : items)
            itemsPrefs.put(t, getPreference(t));

        List<Map.Entry<T, Integer>> sortedPrefs = new ArrayList<Map.Entry<T, Integer>>(itemsPrefs.entrySet());
        Collections.sort(sortedPrefs, new DescendingValueComparator<T, Integer>());

        for (int i = 0; i < k && i < sortedPrefs.size(); i++) {
            Map.Entry<T, Integer> e = sortedPrefs.get(i);
            if (e.getValue() != null)
                result.add(e.getKey());
        }
        return result;
    }   
}