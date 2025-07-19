/*
 * Decompiled with CFR 0.152.
 */
package mcsushi.dynamicshop.sushidynamicshop.init;

import java.util.logging.Logger;
import mcsushi.dynamicshop.sushidynamicshop.util.PremiumChecker;

public class Initializer {
    private final Logger logger;

    public Initializer(Logger logger) {
        this.logger = logger;
    }

    public void init() {
        this.logger.info("[Initializer] Starting initialization...");
        PremiumChecker.checkPremium(this.logger);
        try {
            Class<?> dbManagerClass = Class.forName("mcsushi.dynamicshop.sushidynamicshop.premium.database.DatabaseManager");
            dbManagerClass.getMethod("init", new Class[0]).invoke(null, new Object[0]);
        }
        catch (ClassNotFoundException e) {
            this.logger.info("[Initializer] Premium Feature not found. Skipping initialization.");
        }
        catch (Exception e) {
            this.logger.warning("[Initializer] Error initializing DatabaseManager: " + e.getMessage());
        }
        try {
            Class<?> premiumInitClass = Class.forName("mcsushi.dynamicshop.sushidynamicshop.init.PremiumInitializer");
            premiumInitClass.getMethod("init", new Class[0]).invoke(null, new Object[0]);
        }
        catch (ClassNotFoundException e) {
            this.logger.info("[Initializer] Premium features not found.");
        }
        catch (Exception e) {
            this.logger.info("[Initializer] Error initializing premium features: " + e.getMessage());
        }
        this.logger.info("[Initializer] Initialization complete.");
    }

    public void shutdown() {
        this.logger.info("[Initializer] Shutting down system...");
        try {
            Class<?> dbManagerClass = Class.forName("mcsushi.dynamicshop.sushidynamicshop.premium.database.DatabaseManager");
            dbManagerClass.getMethod("shutdown", new Class[0]).invoke(null, new Object[0]);
        }
        catch (ClassNotFoundException e) {
            this.logger.info("[Initializer] Premium DatabaseManager not found. Skipping shutdown.");
        }
        catch (Exception e) {
            this.logger.warning("[Initializer] Error during shutdown: " + e.getMessage());
        }
        this.logger.info("[Initializer] Shutdown complete.");
    }
}

