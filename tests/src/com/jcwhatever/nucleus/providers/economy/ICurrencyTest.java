package com.jcwhatever.nucleus.providers.economy;

import com.jcwhatever.nucleus.providers.economy.ICurrency.CurrencyNoun;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ICurrencyTest {

    private ICurrency _currency;

    public ICurrencyTest(ICurrency currency) {
        _currency = currency;
    }

    public void run() throws Exception {
        testFormat();
        testGetCurrencyName();
        testGetConversionFactor();
        testConvert();
    }

    @Test
    public void testFormat() throws Exception {
        String result = _currency.format(10);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void testGetCurrencyName() throws Exception {

        String result = _currency.getName(CurrencyNoun.SINGULAR);
        Assert.assertNotNull(result);

        result = _currency.getName(CurrencyNoun.SINGULAR);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetConversionFactor() throws Exception {

        // should not throw any exceptions
        _currency.getConversionFactor();
    }

    @Test
    public void testConvert() throws Exception {

        ICurrency other = createDummyCurrency(2.0D);

        double result = _currency.convert(10, other);

        Assert.assertEquals(5 * _currency.getConversionFactor(), result, 0.0D);
    }

    public static ICurrency createDummyCurrency(final double factor) {

        return new ICurrency() {

            @Override
            public String format(double amount) {
                String template = "{0} Dollar";
                return TextUtils.format(template, amount).toString();
            }

            @Override
            public String getName(CurrencyNoun noun) {
                PreCon.notNull(noun);

                switch (noun) {
                    case SINGULAR:
                        return "Dollar";
                    case PLURAL:
                        return "Dollars";
                    default:
                        throw new AssertionError();
                }
            }

            @Override
            public double getConversionFactor() {
                return factor;
            }

            @Override
            public double convert(double amount, ICurrency currency) {
                double base = amount / currency.getConversionFactor();
                return base * factor;
            }
        };
    }
}