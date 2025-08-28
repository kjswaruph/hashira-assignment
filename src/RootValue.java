public class RootValue {
    int x;
    int base;
    String baseValue;

    RootValue(int x, int base, String baseValue) {
        this.x = x;
        this.base = base;
        this.baseValue = baseValue;
    }

    java.math.BigInteger getDecodedBig() {
        return new java.math.BigInteger(baseValue, base);
    }

    double getDecodedY() {
        // Retained for backward compatibility (quadratic case); may lose precision for large values
        return getDecodedBig().doubleValue();
    }
}
