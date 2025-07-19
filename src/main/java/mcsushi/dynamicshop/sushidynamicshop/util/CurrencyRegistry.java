/*
 * Decompiled with CFR 0.152.
 */
package mcsushi.dynamicshop.sushidynamicshop.util;

import java.util.List;
import java.util.UUID;

public interface CurrencyRegistry {
    public boolean currencyExists(String var1);

    public String getPronoun(String var1);

    public List<String> getAllCurrencies();

    public List<String> getTop10Players(String var1, String var2);

    public String getPlayerBalance(UUID var1, String var2, String var3);

    public boolean addCurrency(String var1, String var2);

    public boolean updateCurrency(String var1, String var2);

    public boolean adjustPlayerBalance(UUID var1, String var2, double var3, String var5);

    public boolean removeCurrency(String var1);
}

