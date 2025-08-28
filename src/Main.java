import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws Exception {
        // If file paths provided as args, use them; else default to both sample test cases
        String[] files = (args != null && args.length > 0) ? args : new String[]{"testcases/test-case-1.json", "testcases/test-case-2.json"};

        for (String file : files) {
            System.out.println("\n=== Processing: " + file + " ===");
            String json = Files.readString(Paths.get(file));
            List<RootValue> roots = parseRoots(json);
            if (roots.size() < 3) {
                System.out.println("Skipped (needs at least 3 points, found " + roots.size() + ")");
                continue;
            }
            if (roots.size() > 3) {
                System.out.println("Info: Found " + roots.size() + " points, using first 3 for quadratic reconstruction.");
            }
            Coefficient coeff = lagrangeQuadratic(roots);
            System.out.printf("Polynomial: f(x) = %.8f x² + %.8f x + %.8f%n", coeff.a, coeff.b, coeff.c);
            System.out.printf("Secret constant c = %.8f%n", coeff.c);
        }
    }

    private static List<RootValue> parseRoots(String json) {
        List<RootValue> roots = new ArrayList<>();
        // Regex: capture key -> groups of base/value objects (excluding the keys section)
        Pattern pattern = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"value\"\\s*:\\s*\"([^\\\"]+)\"\\s*\\}");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int base = Integer.parseInt(matcher.group(2));
            String val = matcher.group(3);
            roots.add(new RootValue(x, base, val));
        }
        return roots;
    }

    private static Coefficient lagrangeQuadratic(List<RootValue> points) {
        if (points.size() < 3)
            throw new IllegalArgumentException("At least three points needed");

        double[] x = new double[3], y = new double[3];
        for (int i=0; i<3; i++) {
            RootValue p = points.get(i);
            x[i] = p.x;
            y[i] = p.getDecodedY();
        }
        double d0 = (x[0]-x[1])*(x[0]-x[2]);
        double d1 = (x[1]-x[0])*(x[1]-x[2]);
        double d2 = (x[2]-x[0])*(x[2]-x[1]);

        double A0 = 1/d0, B0 = -(x[1]+x[2])/d0, C0 = (x[1]*x[2])/d0;
        double A1 = 1/d1, B1 = -(x[0]+x[2])/d1, C1 = (x[0]*x[2])/d1;
        double A2 = 1/d2, B2 = -(x[0]+x[1])/d2, C2 = (x[0]*x[1])/d2;

        double a = y[0]*A0 + y[1]*A1 + y[2]*A2;
        double b = y[0]*B0 + y[1]*B1 + y[2]*B2;
        double c = y[0]*C0 + y[1]*C1 + y[2]*C2;

        return new Coefficient(a,b,c);
    }
}