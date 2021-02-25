import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Polynomial {
    HashMap<Double, Double> equation = new HashMap<>();
    DecimalFormat df = new DecimalFormat("0.#####");

    Polynomial (double[][] values) {
        for (int i = 0; i < values.length; i++) {
            // Exponent is key to k,v pair in Hashmap
            equation.putIfAbsent(values[i][1], values[i][0]);
        }
    }

    Polynomial (Polynomial p) {
        this.equation = p.equation;
    }

    Polynomial (double d) {
        this(new double[][] {{d, 0}});
    }

    Polynomial () {}

    public void mul (Polynomial p) {
        Polynomial n = new Polynomial();
        p.equation.forEach((exp1, coef1) -> {
            this.equation.forEach((exp2, coef2) -> {
                n.add(new double[] {coef1 * coef2, exp1 + exp2});
            });
        });
        this.equation = n.equation;
    }

    public void mul (double d) {
        this.equation.replaceAll((exp, coef) -> coef * d);
    }

    public void add (Polynomial p) {
        p.equation.forEach((exponent, value) ->
                this.equation.merge(exponent, value, (v1, v2) -> v1 + v2));
    }

    public void add (double[] d) {
        double[][] value = {{d[0], d[1]}};  // Convert to double[][]
        this.add(new Polynomial(value));
    }

    public void add(double d) {
        double[][] value = {{d, 0}};        // Convert to double[][] w/ exp 0.
        this.add(new Polynomial(value));
    }

    public void sub(Polynomial p) {
        p.equation.forEach((exponent, value) -> {
            if (this.equation.containsKey(exponent)) {
                this.equation.compute(exponent, (k, v) -> v + value * -1);
            } else {
                this.equation.put(exponent, value * -1);
            }
        });
    }

    public void sub (double[] d) {
        double[][] value = {{d[0], d[1 * -1]}};  // Convert to double[][]
        this.add(new Polynomial(value));
    }

    public void sub(double d) {
        double[][] value = {{d, 0}};        // Convert to double[][] w/ exp 0.
        this.add(new Polynomial(value));
    }


    public String toString() {
        StringBuilder sb = new StringBuilder("f(x) = ");

        // Create string in sorted order of descending exponent of polynomial
        this.equation.entrySet()
                .stream()
                .sorted(Map.Entry.<Double, Double> comparingByKey().reversed())
                .forEach(x -> {
                    if (x.getValue() != 0.0) sb.append(df.format(x.getValue())
                            + (x.getKey() != Math.abs(1) ? "x^"
                            + df.format(x.getKey()) : "x") + " + ");
                });

        // If no values were printed, return polynomial is 0
        if (sb.length() == 7) return "f(x) = 0";

        // Trim off remaining " + " at end of string and return
        sb.setLength(Math.max(sb.length() - 3, 0));

        // Trim off x^0 since it equals 1 for any x and does not matter (unless is it 0^0)
        if (this.equation.containsKey(0.0) && this.equation.get(0.0) != 0)
            sb.setLength(Math.max(sb.length() - 3, 0));

        return sb.toString();
    }

    public double calculateForX(double x) {
        return this.equation.entrySet()
                .stream()
                .mapToDouble(c -> c.getValue() * Math.pow(x, c.getKey()))
                .sum();
    }

    public String getHornersForm() {
        StringBuilder output = new StringBuilder();

        int n = this.equation.size();
        double[][] values = new double[n][2];
        
        Map<Double, Double> map = new TreeMap<>(equation);
        int i = 0;
        for (Map.Entry<Double, Double> term : map.entrySet()) {
            values[i][0] = term.getValue();
            values[i][1] = term.getKey();
            i++;
        }

        boolean isZeroExponent = values[0][1] == 0;
        int firstTerm = isZeroExponent ? 1 : 0;
        if (isZeroExponent) output.append(df.format(values[0][0]));

        double exponents = 0;
        for (int j = firstTerm; j < n; j++) {
            double nextExponent = values[j][1] - exponents;
            String expString = (nextExponent > 1) ? "^" + df.format(nextExponent) : "";
            output.append(" + x" + expString + "(" + df.format(values[j][0]));
            exponents += nextExponent;
        }
        output.append(")".repeat(isZeroExponent ? n-1 : n));
        if (!isZeroExponent) output.delete(0, 3);
        return output.toString();
    }

    public static double[][] createTableOfValuesFromFile(String fileName) {
        double[] xVals, fxVals;
        try {
            File file = new File(fileName);
            BufferedReader  reader = new BufferedReader(new FileReader(file));
            xVals = Arrays
                    .stream(reader.readLine().split(" "))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
            fxVals = Arrays
                    .stream(reader.readLine().split(" "))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        } catch (Exception e) {
            return null;
        }
        return new double[][] {xVals, fxVals};
    }

    public static double getValueFromLagrange (double[] x, double[] fx, double xVal) {
        int n = x.length;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            double product = 1;
            for (int j = 0; j < n; j++) {
                if (j != i) product *= (xVal - x[j]) / (x[i] - x[j]);
            }
            sum += fx[i] * product;
        }
        return sum;
    }

    public static double[][] getDividedDifferenceTable (double[] x, double[] fx) {
        int n = x.length;
        // create f array
        double[][] f = new double[n][];
        f[0] = fx;
        for (int i = 1; i < n; i++) {
            f[i] = new double[n-i];
            for (int j = 0; j < f[i].length; j++) {
                f[i][j] = (f[i-1][j+1] - f[i-1][j]) / (x[i+j] - x[j]);
            }
        }
        return f;
    }

    
    public static void printDividedDifferenceTable(double[] x, double[][] f) {
        // Create Header
        String[] headers = new String[f[0].length + 1];
        headers[0] = "x";
        headers[1] = "f(x)";
        for (int i = 2; i < headers.length; i++) {
            headers[i] = "f" + (i - 1);
        }
        System.out.printf("%-20s".repeat(headers.length), headers);
        System.out.println();
        System.out.println("===================|".repeat(headers.length));

        // Number format for printing values on table
        DecimalFormat df = new DecimalFormat("###,###,###,##0.#####");

        // Initialize the number of terms to print per line, to form the triangle
        int numTermsOnRow = 0;

        for (int i = 0; i < x.length * 2; i++) {
            // Calculate number of terms to print for given row
            if (i < x.length && i % 2 != 0)
                numTermsOnRow++;
            else if (i >= x.length && i % 2 == 0)
                numTermsOnRow--;

            // Print (x, fx) if even row, or put spaces in otherwise
            if(i % 2 == 0) {
                System.out.printf("%-20s%-40s", df.format(x[i/2]), df.format(f[0][i/2]));
            }
            else {
                System.out.printf("%-40s", "");
            }

            // Print each term for given row
            for (int k = 0; k < numTermsOnRow; k++) {
                double nextVal = (i % 2 == 0) ? f[k*2+2][i/2-k-1] : f[k*2+1][i/2-k];
                System.out.printf("%-40s", df.format(nextVal));
            }

            // Blank line for next row
            System.out.print("\n\n");
        }
    }

    public static Polynomial getPolynomialFromTable(double[] x, double[][] f) {
        Polynomial n = new Polynomial();
        double[] xVal = {1, 1};             // equivalent to 1x^1
        for (int i = 0; i < f.length; i++) {
            // Polynomial p is used to store each b term of the interpolated polynomial
            double b = f[i][0];
            Polynomial p = new Polynomial(b);
            for (int j = 0; j < i; j++) {
                double[][] term = {xVal, {x[j] * -1, 0}};
                p.mul(new Polynomial(term));
            }
            n.add(p);
        }
        return n;
    }

    public static String printInterpolatingPolynomialFromTable(double[] x, double[][] f) {
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.#####");

        for (int i = 0; i < f.length; i++) {
            double a = f[i][0];
            // if a is 0, the whole product will equal 0, dont print it.
            if (a == 0) continue;

            // Fix sign for negative a values for readability (rather than "+ -a" -> "- a")
            // If i==0, then it is first term of polynomial and should not be changed
            if (i != 0) {
                String sign = " + ";
                    if (a < 0) {
                        sign = " - ";
                        a *= -1;
                    }
                sb.append(sign);
            }
            sb.append(df.format(a));

            // Get interpolating products of x terms that correspond to given "a" term.
            for (int j = 0; j < i; j++) {
                double xVal = x[j];

                // If xVal is 0, only print "x" instead of "(x - 0)"
                if (xVal == 0) {
                    sb.append("x");
                    continue;
                }

                // // Fix sign for negative xVal (rather than "- -xVal" -> "+ xVal")
                String sign = "- ";
                if (xVal < 0) {
                    sign = "+ ";
                    xVal *= -1;
                }
                String term = "(x " + sign + df.format(xVal) + ")";
                sb.append(term);
            }

        }
        return sb.toString();
    }

    public static void main(String[] args) {
        double[][] vals = createTableOfValuesFromFile("input.txt");

        System.out.println("Print x vals and corresponding fx vals using captured 2D array");
        System.out.println(Arrays.deepToString(createTableOfValuesFromFile("input.txt")));
        System.out.println();



        // Save x and fx in their own array
        double[] x = vals[0];
        double[] fx = vals[1];

        System.out.println("Get interpolated value given data set using Lagrange for x=0.5");
        System.out.println("getValueFromLagrange = " + getValueFromLagrange(x, fx, 0.5));
        System.out.println();

        System.out.println("Save divided difference table to double[][], and print it.");
        double[][] f2 = getDividedDifferenceTable(x, fx);
        System.out.println(Arrays.deepToString(f2));
        System.out.println();

        System.out.println("Print formatted divided difference table using saved double[][]");
        printDividedDifferenceTable(x, f2);

        System.out.println("Interpolating polynomial from table: ");
        String interpolatingPolynomial = printInterpolatingPolynomialFromTable(x, f2);
        System.out.println("\tinterpolatingPolynomial = " + interpolatingPolynomial);
        System.out.println();

        System.out.println("Simplified polynomial from table (Using Polynomial class)");
        Polynomial polynomial = getPolynomialFromTable(x, f2);
        System.out.println("\tpolynomial = " + polynomial);
        System.out.println();

        System.out.println("Same polynomial in Hornor's Form");
        System.out.println("\tpolynomial = " + polynomial.getHornersForm());
        System.out.println();

        System.out.println("Using the following x, fx, interpolate x for 1.3");
        System.out.println("\tArrays.toString(x) = " + Arrays.toString(x));
        System.out.println("\tArrays.toString(fx) = " + Arrays.toString(fx));
        System.out.println("\tgetValueFromLagrange(x, fx, 1.3) = " + getValueFromLagrange(x, fx, 1.3));
        System.out.println();
        System.out.println();

        System.out.println("Demonstration of polynomial class");
        System.out.println("-".repeat(80));
        
        System.out.println("Polynomial class constructor using hashMap data structure.");
        System.out.println("The exponent is the key to the map, allowing fast lookup for math operations");
        System.out.println();

        System.out.println("Create new polynomial using double[][] {{2, 3}, {5, 4}, {9, 2}, {10, 6}}");
        Polynomial p1 = new Polynomial(new double[][] {{2, 3}, {5, 4}, {9, 2}, {10, 6}});
        System.out.println("p1 = " + p1);
        System.out.println();

        System.out.println("Multiply p1 by 4");
        p1.mul(4);
        System.out.println("p1 = " + p1);
        System.out.println();

        System.out.println("Add p1 to polynomial to our interpolating polynomial from the table");
        System.out.println("add " + polynomial);
        p1.add(polynomial);
        System.out.println("p1 = " + p1);
        System.out.println();

        System.out.println("Multiply p1 by " + polynomial);
        p1.mul(polynomial);
        System.out.println("p1 = " + p1);
        System.out.println();

        System.out.println("Subtract p1 from p1 (Expect 0)");
        p1.sub(p1);
        System.out.println("p1 = " + p1);
    }
}

