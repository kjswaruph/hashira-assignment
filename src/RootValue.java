public class RootValue {
    int x;
    int base;
    String baseValue;

    RootValue(int x, int base, String baseValue) {
        this.x = x;
        this.base = base;
        this.baseValue = baseValue;
    }

    double getDecodedY() {
        // Use BigInteger to safely parse large numbers then convert to double (may lose precision for extremely large values)
        return new java.math.BigInteger(baseValue, base).doubleValue();
    }
}
