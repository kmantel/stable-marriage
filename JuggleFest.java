import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;
import java.util.logging.*;

public class JuggleFest {
    private static final Logger log = Logger.getLogger(JuggleFest.class.getName());

    public static void main(String[] args) {
        Integer circuitToPrint = null;
        String fName;

        // parse args, ignore desired circuit if malformed
        if (args.length == 1)
            fName = args[0];
        else if (args.length == 2) {
            fName = args[0];
            String c = args[1];
            if (c.length() > 0 && Character.toUpperCase(c.charAt(0)) == 'C') {
                String c1 = c.substring(1);
                if (c1.matches("\\d+"))
                    circuitToPrint = Integer.parseInt(c1);
            }
            else if (c.matches("\\d+"))
                circuitToPrint = Integer.parseInt(c);
        }
        else {
            System.out.println("Usage: java JuggleFest input_file [target_circuit]");
            return;
        }

        log.info("Start");

        List<Circuit> circuits = new ArrayList<Circuit>();
        List<Juggler> jugglers = new ArrayList<Juggler>();

        // read, create circuits and jugglers from input
        try {
            BufferedReader f = new BufferedReader(new FileReader(fName));
            parseInput(f, circuits, jugglers);
        }
        catch (BadInputException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }

        int circuitSize = jugglers.size()/circuits.size();

        // match and verify
        StableMatcher<Circuit, Juggler> matcher = new StableMatcher<Circuit, Juggler>(circuits, jugglers);
        Match<Circuit, Juggler> result = matcher.manyToOneMatch(circuitSize);

        log.info("Verifying solution is stable");
        if (StableMatcher.isStable(result, circuits, jugglers, circuitSize))
            log.info("Stable");
        else
            log.info("Not stable");

        printCircuit(circuitToPrint, circuits, result);
        printYodleEmail(circuitToPrint, circuits, result);
    }

    private static void printCircuit(Integer id, List<Circuit> circuits, Match<Circuit, Juggler> match) {
        if (id != null && id >= 0 && id < circuits.size()) {
            Circuit c = circuits.get(id);
            System.out.printf("%s: %s\n", c, match.getMatches(c));
        }
        else {
            for (Circuit c : circuits)
                System.out.printf("%s: %s\n", c, match.getMatches(c));
        }
    }

    private static void printYodleEmail(Integer id, List<Circuit> circuits, Match<Circuit, Juggler> match) {
        if (id == null || id < 0 || id >= circuits.size())
            return;
        int sum = 0;
        String domain = "@yodle.com";
        Set<Juggler> jugglers = match.getMatches(circuits.get(id));

        for (Juggler j : jugglers)
            sum += j.id();
        System.out.println(sum + domain);
    }

    private static void parseInput(BufferedReader file, List<Circuit> circuits, List<Juggler> jugglers) throws BadInputException, IOException {
        int lineNum = 1;
        String curLine;

        Pattern cPat = Pattern.compile("^C C(?<id>\\d+) (?<skills>H:\\d+ E:\\d+ P:\\d+)$");
        Pattern jPat = Pattern.compile("^J J(?<id>\\d+) (?<skills>H:\\d+ E:\\d+ P:\\d+) (?<prefs>(C\\d+)(,C\\d+)*)$");

        // read circuits first
        while ((curLine = file.readLine()) != null) {
            // one blank line separates circuits from jugglers
            if (curLine.length() == 0)
                break;
            
            Matcher m = cPat.matcher(curLine);
            if (!m.matches())
                throw new BadInputException(String.format("bad format (line %d)\n\'%s\'\n", lineNum, curLine));

            int[] skills = extractIntegerArray(m.group("skills").split(" "));
            Circuit c = new Circuit(Integer.parseInt(m.group("id")), skills);

            circuits.add(c);         
            lineNum++;       
        }
        // read blank line then begin reading jugglers
        if ((curLine = file.readLine()) == null)
            throw new BadInputException(String.format("no jugglers or no blank line before jugglers (line %d)\n", lineNum));

        do {
            if (curLine.length() > 0) {
                Matcher m = jPat.matcher(curLine);
                if (!m.matches())
                    throw new BadInputException(String.format("bad format (line %d)\n\'%s\'\n", lineNum, curLine));

                int[] skills = extractIntegerArray(m.group("skills").split(" "));
                int[] prefs = extractIntegerArray(m.group("prefs").split(","));
                Juggler j = new Juggler(Integer.parseInt(m.group("id")), skills);

                for (int i = 0; i < prefs.length; i++) {
                    Circuit c = circuits.get(prefs[i]);
                    if (c == null)
                        throw new BadInputException(String.format("circuit %d does not exist\n", prefs[i]));
                    j.setPreference(c, prefs.length-1-i);
                }
                
                jugglers.add(j);
                lineNum++;
            }
        }
        while ((curLine = file.readLine()) != null);

        log.info("read all");
    }

    // returns array where each element of s is stripped of nondigit chars then converted to int
    // if an element has no digits, value becomes 0
    private static int[] extractIntegerArray(String[] s) {
        if (s == null)
            return null;
        int[] result = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            String stripped = s[i].replaceAll("\\D", "");
            if (stripped.length() > 0)
                result[i] = Integer.parseInt(stripped);
            else
                result[i] = 0;
        }

        return result;
    }

    static class BadInputException extends Exception {
        BadInputException(String message) {
            super("Bad Input: " + message);
        }
    }
}
