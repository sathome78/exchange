package me.exrates.service.ethereum;


import java.math.BigDecimal;

/**
 * Created by Maks on 12.03.2018.
 */
public class ExConvert {

    private ExConvert() {
    }

    public static BigDecimal fromWei(String number, ExConvert.Unit unit) {
        return fromWei(new BigDecimal(number), unit);
    }

    public static BigDecimal fromWei(BigDecimal number, ExConvert.Unit unit) {
        return number.divide(unit.getWeiFactor());
    }

    public static BigDecimal toWei(String number, ExConvert.Unit unit) {
        return toWei(new BigDecimal(number), unit);
    }

    public static BigDecimal toWei(BigDecimal number, ExConvert.Unit unit) {
        return number.multiply(unit.getWeiFactor());
    }

    public static enum Unit {
        WEI("wei", 0),
        KWEI("kwei", 3),
        MWEI("mwei", 6),
        AIWEI("aiwei", 8),
        GWEI("gwei", 9),
        TWINKY("twinky", 10),
        SZABO("szabo", 12),
        FINNEY("finney", 15),
        ETHER("ether", 18),
        KETHER("kether", 21),
        METHER("mether", 24),
        GETHER("gether", 27);

        private String name;
        private BigDecimal weiFactor;

        private Unit(String name, int factor) {
            this.name = name;
            this.weiFactor = BigDecimal.TEN.pow(factor);
        }

        public BigDecimal getWeiFactor() {
            return this.weiFactor;
        }

        public String toString() {
            return this.name;
        }

        public static ExConvert.Unit fromString(String name) {
            if(name != null) {
                ExConvert.Unit[] var1 = values();
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    ExConvert.Unit unit = var1[var3];
                    if(name.equalsIgnoreCase(unit.name)) {
                        return unit;
                    }
                }
            }

            return valueOf(name);
        }
    }

}
