import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.logging.*;

/********************************************************************************
Produces solutions to stable matching problems, with type V proposing and 
type U accepting proposals.
********************************************************************************/
public class StableMatcher<U extends Program<V>, V extends Applicant<U>> {
    private static final Logger log = Logger.getLogger(StableMatcher.class.getName());

    private List<U> programs;
    private List<V> applicants;
    // during matching, we remove programs from applicants' preference lists, this allows restoration
    private Map<V, Set<AbstractMap.SimpleEntry<U, Integer>>> removedPrefs;

    public StableMatcher(List<U> programs, 
                         List<V> applicants) {
        this.applicants = applicants;
        this.programs = programs;
        this.removedPrefs = new HashMap<V, Set<AbstractMap.SimpleEntry<U, Integer>>>();
    }

    // type U may accept up to maxProgramSize proposals
    public Match<U, V> manyToOneMatch(int maxProgramSize) {
        Map<U, Set<V>> progMatches = new HashMap<U, Set<V>>();
        Map<V, U> appMatches = new HashMap<V, U>();

        Set<V> unmatchedApplicants = new HashSet<V>();

        for (U prog : programs)
            progMatches.put(prog, new HashSet<V>());

        for (V app : applicants)
            unmatchedApplicants.add(app);

        while (!unmatchedApplicants.isEmpty()) {
            log.info(String.format("%d/%d unmatched", unmatchedApplicants.size(), applicants.size()));

            Map<U, Set<V>> proposals = new HashMap<U, Set<V>>();    // proposals of current round
            Set<V> appsToRemove = new HashSet<V>();     // applicants that have been matched or exhausted of options during current round

            for (V app : unmatchedApplicants) {
                U topProg = app.getMostPreferred();

                // if app has no more programs left it wants to match with, remove it from consideration
                if (topProg == null) {
                    appMatches.put(app, null);
                    appsToRemove.add(app);
                }
                // apply to next program
                else {
                    if (!proposals.containsKey(topProg))
                        proposals.put(topProg, new HashSet<V>());
                    proposals.get(topProg).add(app);
                    
                    // remove topProg from app's preference list and save to restore at end
                    if (!removedPrefs.containsKey(app))
                        removedPrefs.put(app, new HashSet<AbstractMap.SimpleEntry<U, Integer>>());
                    removedPrefs.get(app).add(new AbstractMap.SimpleEntry<U, Integer>(topProg, app.getPreference(topProg)));
                    app.removePreference(topProg);
                }
            }
            for (U prog : programs) {
                // available applicants consists of new proposals and proposals tentatively accepted
                // no changes if no new proposals
                Set<V> appsAvailable = proposals.get(prog);

                if (appsAvailable != null) {
                    appsAvailable.addAll(progMatches.get(prog));

                    // prog tentatively accepts up to max of proposals currently available
                    LinkedHashSet<V> curAcceptances = prog.getKMostPreferred(maxProgramSize, appsAvailable);
                    progMatches.put(prog, curAcceptances);


                    // take out of consideration applicants that are matched
                    // add/retain in consideration applicants that are not matched
                    for (V app : appsAvailable) {
                        if (curAcceptances.contains(app)) {
                            appMatches.put(app, prog);
                            appsToRemove.add(app);
                        }
                        else
                            unmatchedApplicants.add(app);   // does not add duplicates
                    }
                }
            }
            for (V app : appsToRemove)
                unmatchedApplicants.remove(app);
        }

        log.info("finished matching");

        // replace programs that were removed from preference lists
        for (V app : applicants) {
            for (AbstractMap.SimpleEntry<U, Integer> e : removedPrefs.get(app)) {
                app.setPreference(e.getKey(), e.getValue());
            }
        }

        return new Match<U, V>(progMatches, appMatches);
    }

    public static <U extends Program<V>, V extends Applicant<U>> boolean isStable(Match<U,V> match, List<U> programs, List<V> applicants, int maxProgramSize) {
        if (match == null || programs == null || applicants == null)
            return false;
        if (programs.size() == 0 || applicants.size() == 0)
            return false;

        for (U prog : programs) {
            for (V app : applicants) {
                // if either is unacceptable to the other, no instability here
                if (prog.getPreference(app) != null && app.getPreference(prog) != null) {
                    Set<V> progMatches = match.getMatches(prog);
                    U matchApp = match.getMatch(app);

                    // app and prog are willing to accept each other, both can, but they are not matched
                    if (matchApp == null && progMatches.size() < maxProgramSize) {
                        log.info(String.format("Blocking pair: %s, %s", app, prog));
                        return false;
                    }
                    else {
                        for (V aMatched : progMatches) {
                            // app prefers prog to its match and prog prefers app to one of its matches
                            if (app.comparePreference(prog, matchApp) > 0 &&
                                prog.comparePreference(app, aMatched) > 0) {
                                log.info(String.format("Blocking pair: %s, %s", app, prog));
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}