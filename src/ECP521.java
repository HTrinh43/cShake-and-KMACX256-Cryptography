import java.math.BigInteger;

public class ECP521 {
    private BigInteger myX;
    private BigInteger myY;
    private static BigInteger p = new BigInteger("2").pow(521).subtract(BigInteger.ONE);
    public static BigInteger r = new BigInteger("2").pow(519).subtract(new BigInteger(
            "337554763258501705789107630418782636071" +
                    "904961214051226618635150085779108655765"));
    private static BigInteger d = BigInteger.valueOf(-376014L);
    public ECP521(){
        myX = BigInteger.valueOf(0);
        myY = BigInteger.valueOf(1);
    }

    public ECP521(BigInteger x, boolean yLsb)  {
        this(x, calculateY(x, yLsb));
    }
    public ECP521(BigInteger x, BigInteger y) {
//        if (!isInCurve(x.mod(p),y.mod(p))){
//            throw new Exception("This point is not in the Edwards Curve.");
//        }
//        myX = x.mod(p);
//        myY = y.mod(p);
        if (x == null || y == null) {
            throw new IllegalArgumentException("Not a valid curve point. x: " + x + ", y: " + y + ".");
        }
        x = x.mod(p);
        y = y.mod(p);
        BigInteger left = (x.pow(2)).add(y.pow(2)).mod(p);
        BigInteger right = BigInteger.ONE.add(d.multiply(x.pow(2)
                .multiply(y.pow(2)))).mod(p);
        if (left.compareTo(right) == 0) {
            myX = x;
            myY = y;
        } else {
            throw new IllegalArgumentException("Not a valid curve point. x: " + x + ", y: " + y + ".");
        }
    }

    public BigInteger getR(){
        return r;
    }

    public BigInteger getX() {
        return myX;
    }

    public BigInteger getY() {
        return myY;
    }

    private boolean isInCurve(BigInteger x, BigInteger y){
        BigInteger x2y2 = x.pow(2).add(y.pow(2)).mod(p);
        BigInteger rest = BigInteger.ONE.add(d.multiply(x.pow(2).multiply(y.pow(2)))).mod(p);
        return x2y2.equals(rest);
    }

    public ECP521 add(ECP521 other)  {
//        BigInteger dxy = d.multiply(other.getX()).multiply(other.getY()).multiply(myX).multiply(myY).mod(p);
//        BigInteger numX = myX.multiply(other.getY()).add(myY.multiply(other.getX())).mod(p);
//        BigInteger numY = myY.multiply(other.getY()).subtract(myX.multiply(other.getX())).mod(p);
//        return new ECP521(numX.divide(BigInteger.ONE.add(dxy)), numY.divide(BigInteger.ONE.subtract(dxy)));
        BigInteger num1 = myX.multiply(other.getY()).add(myY.multiply(other.getX())).mod(p);
        BigInteger num2 = myY.multiply(other.getY()).subtract(myX.multiply(other.getX())).mod(p);
        BigInteger mult = d.multiply(myX).multiply(other.getX()).multiply(myY).multiply(other.getY());
        BigInteger den1 = BigInteger.ONE.add(mult).mod(p);
        BigInteger den2 = BigInteger.ONE.subtract(mult).mod(p);
        den1 = den1.modInverse(p);
        den2 = den2.modInverse(p);
        return new ECP521(num1.multiply(den1).mod(p), num2.multiply(den2).mod(p));
    }

    public ECP521 scalarMultiplication(BigInteger s)  {

//        String temp = s.toString(2);
//        int k = temp.length();
//        ECP521 V = new ECP521(myX, myY);
//
//        for (int i = k - 1; i >= 0; i--) {
//            V = V.add(V);
//            char s_i = temp.charAt(i);
//            if (s_i == '1') {
//                V = V.add(this);
//            }
//        }
//        return V;
        if(s.equals(BigInteger.ZERO)) {
            return new ECP521();
        }
        ECP521 v = new ECP521(myX, myY);
        for (int i = s.bitLength() - 2; i >= 0; i--) {
            v = v.add(v);
            if(s.testBit(i)) {
                v = v.add(this);
            }
        }
        return v;
    }

    private static BigInteger calculateY(BigInteger x, boolean yLsb) {
        BigInteger radicand = BigInteger.ONE.subtract(x.modPow(BigInteger.TWO, p));
        radicand = radicand.multiply(BigInteger.ONE.subtract(d.multiply(x.modPow(BigInteger.TWO, p))).modInverse(p));
        return sqrt(radicand, yLsb);
    }

    private static BigInteger sqrt(BigInteger v, boolean lsb) {
        assert (ECP521.p.testBit(0) && ECP521.p.testBit(1)); // p = 3 (mod 4)
        if (v.signum() == 0) {
            return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(ECP521.p.shiftRight(2).add(BigInteger.ONE), ECP521.p);
        if (r.testBit(0) != lsb) {
            r = ECP521.p.subtract(r); // correct the lsb
        }
        return (r.multiply(r).subtract(v).mod(ECP521.p).signum() == 0) ? r : null;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ECP521) {
            ECP521 other = (ECP521) obj;
            return myX.equals(other.getX()) && myY.equals(other.getY());
        }
        return false;
    }
}
