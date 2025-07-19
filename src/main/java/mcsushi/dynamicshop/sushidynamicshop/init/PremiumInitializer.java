/*
 * Decompiled with CFR 0.152.
 */
package mcsushi.dynamicshop.sushidynamicshop.init;

import java.util.logging.Logger;
import mcsushi.dynamicshop.sushidynamicshop.Sushidynamicshop;
import mcsushi.dynamicshop.sushidynamicshop.util.CurrencyRegistry;
import mcsushi.dynamicshop.sushidynamicshop.util.DefaultCurrencyRegistry;

public class PremiumInitializer {
    private static final Logger logger = Sushidynamicshop.getLoggerInstance();
    private static Class<?> fluctuationClass;
    private static CurrencyRegistry currencyRegistry;

    public static void init() {
        logger.info("[PremiumInitializer] Initializing Premium Features...");
        try {
            fluctuationClass = Class.forName("mcsushi.dynamicshop.sushidynamicshop.premium.guidisplay.PriceFluctuationDisplay");
            logger.info("[PremiumInitializer] Price Fluctuation Display loaded successfully.");
        }
        catch (ClassNotFoundException e) {
            logger.info("[PremiumInitializer] Disabled Price Fluctuation Display.");
        }
        try {
            Class<?> registryClass = Class.forName("mcsushi.dynamicshop.sushidynamicshop.premium.customcurrency.CurrencyRegistryImpl");
            currencyRegistry = (CurrencyRegistry)registryClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            logger.info("[PremiumInitializer] Custom Currency loaded successfully.");
        }
        catch (ClassNotFoundException e) {
            logger.info("[PremiumInitializer] Using Default Custom Currency.");
        }
        catch (Exception e) {
            logger.warning("[PremiumInitializer] Error initializing CurrencyRegistry: " + e.getMessage());
        }
    }

    public static String getFluctuationDisplay(double basePrice, double currentPrice) {
        if (fluctuationClass == null) {
            return "N/A";
        }
        try {
            return (String)fluctuationClass.getMethod("getFluctuationDisplay", Double.TYPE, Double.TYPE).invoke(null, basePrice, currentPrice);
        }
        catch (Exception e) {
            logger.warning("[PremiumInitializer] Error invoking getFluctuationDisplay: " + e.getMessage());
            return "N/A";
        }
    }

    public static CurrencyRegistry getCurrencyRegistry() {
        return currencyRegistry;
    }

    static {
        currencyRegistry = new DefaultCurrencyRegistry();
    }
}

