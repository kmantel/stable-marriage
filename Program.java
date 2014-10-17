import java.util.Set;
import java.util.LinkedHashSet;

/********************************************************************************
Must be implemented to be a program/proposal-receiver of items of type T in the 
college admissions/stable marriage problem using StableMatcher
********************************************************************************/
public interface Program<T> {

    // should return null if t is not listed in preferences (or is not acceptable)
    public Integer getPreference(T t);

    // should return number > 0 if a is preferred to b, 0 if indifferent, number < 0 if b preferred to a
    public int comparePreference(T a, T b);

    // should return linked set of k items ordered descending by preference, from items
    //               linked set of fewer if items too small or not enough entries in preference list
    public LinkedHashSet<T> getKMostPreferred(int k, Set<T> items);
}