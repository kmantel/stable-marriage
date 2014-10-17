/********************************************************************************
Must be implemented to be an applicant/proposer to items of type T in the 
college admissions/stable marriage problem using StableMatcher
********************************************************************************/
public interface Applicant<T> {

    // should return null if t is not listed in preferences (or is not acceptable)
    public Integer getPreference(T t);

    // should change preference for t if present, otherwise add it
    public void setPreference(T t, Integer value);

    // should return preference value of t before removal or null if t is not listed in preferences
    public Integer removePreference(T t);

    // should return number > 0 if a is preferred to b, 0 if indifferent, number < 0 if b preferred to a
    public int comparePreference(T a, T b);

    // should return null if no preferences set
    public T getMostPreferred();
}