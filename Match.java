import java.util.Map;
import java.util.Set;

/********************************************************************************
Contains many-to-one or one-to-one matching produced by StableMatcher
********************************************************************************/
public class Match<U extends Program<V>, V extends Applicant<U>> {
    private Map<U, Set<V>> programMatches;
    private Map<V, U> applicantMatches;

    public Match(Map<U, Set<V>> programMatches, Map<V, U> applicantMatches) {
        this.programMatches = programMatches;
        this.applicantMatches = applicantMatches;
    }

    // returns null if u is not Matched
    public Set<V> getMatches(U u) {
        return programMatches.get(u);
    }

    // returns null if v is not Matched
    public U getMatch(V v) {
        return applicantMatches.get(v);
    }
}
            