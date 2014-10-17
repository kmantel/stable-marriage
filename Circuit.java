public class Circuit extends MatchingPairItem<Juggler> implements Program<Juggler> {
    private int id;
    private int[] skills;

    public Circuit(int id, int[] skills) {
        super();
        this.id = id;
        this.skills = skills;
    }

    public Integer getPreference(Juggler j) {
        if (j == null)
            return null;
        return dotProduct(skills, j.skills());
    }

    private static Integer dotProduct(int[] a, int[] b) {
        if (a == null || b == null)
            return null;
        if (a.length != b.length)
            return null;
        int sum = 0;
        for (int i = 0; i < a.length; i++)
            sum += a[i] * b[i];
        return sum;
    }

    public int id() { return id; }
    public int[] skills() { return skills; }

    public String toString() {
        return "C" + id;
    }

    public int hashCode() {
        return id;
    }
}