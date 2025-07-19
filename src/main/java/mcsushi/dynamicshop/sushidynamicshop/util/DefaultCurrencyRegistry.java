/*
 * Decompiled with CFR 0.152.
 */
package mcsushi.dynamicshop.sushidynamicshop.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mcsushi.dynamicshop.sushidynamicshop.util.CurrencyRegistry;

public class DefaultCurrencyRegistry
implements CurrencyRegistry {
    @Override
    public boolean currencyExists(String currencyId) {
        return false;
    }

    @Override
    public String getPronoun(String currencyId) {
        return "";
    }

    @Override
    public List<String> getAllCurrencies() {
        return new ArrayList<String>();
    }

    @Override
    public List<String> getTop10Players(String currencyId, String pronoun) {
        return new ArrayList<String>();
    }

    @Override
    public String getPlayerBalance(UUID playerUUID, String currencyId, String pronoun) {
        return "\u00a7e" + playerUUID.toString() + " \u00a77has \u00a7a0 \u00a76" + pronoun;
    }

    @Override
    public boolean addCurrency(String currencyId, String pronoun) {
        return false;
    }

    @Override
    public boolean updateCurrency(String currencyId, String newPronoun) {
        return false;
    }

    @Override
    public boolean adjustPlayerBalance(UUID playerUUID, String currencyId, double amount, String action) {
        return false;
    }

    @Override
    public boolean removeCurrency(String currencyId) {
        return false;
    }
}

