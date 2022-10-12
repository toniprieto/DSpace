/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.authority;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.dspace.app.util.DCInputsReader;
import org.dspace.core.I18nUtil;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**

 */
public class LocaleAuthority implements ChoiceAuthority {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(LocaleAuthority.class);

    /**
     * the name assigned to the specific instance by the PluginService, @see
     * {@link org.dspace.core.NameAwarePlugin}
     **/
    private String authorityName;

    private List<String> values = null;

    protected ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();

    public LocaleAuthority() {
        super();
    }

    @Override
    public boolean storeAuthorityInMetadata() {
        // For backward compatibility language iso fields don't store authority in
        // the metadatavalue
        return false;
    }

    // once-only load of values and labels
    private void init() {
        if (values == null) {
            String[] langs = configurationService
                .getArrayProperty("org.dspace.content.authority.LocaleAuthority.languages");
            values = Arrays.asList(langs);
        }
    }


    @Override
    public Choices getMatches(String query, int start, int limit, String locale) {
        init();

        Locale currentLocale = I18nUtil.getSupportedLocale(locale);
        int dflt = -1;
        int found = 0;
        List<Choice> v = new ArrayList<Choice>();
        for (int i = 0; i < values.size(); ++i) {
            if (found >= start && v.size() < limit) {
                String isoLang = values.get(i);
                Locale aux = new Locale(isoLang);
                String[] parts = isoLang.split("_");
                if (parts.length == 1) {
                    aux = new Locale(parts[0]);
                } else if (parts.length == 2) {
                    aux = new Locale(parts[0], parts[1]);
                } else if (parts.length == 3) {
                    aux = new Locale(parts[0], parts[1], parts[2]);
                }
                String displayValue = isoLang;
                if (StringUtils.isNotBlank(aux.getDisplayName(currentLocale))) {
                    displayValue = StringUtils.capitalize(aux.getDisplayName(currentLocale));
                }
                v.add(new Choice(null, isoLang, displayValue));
                if (isoLang.equalsIgnoreCase(query)) {
                    dflt = i;
                }
            }
            found++;
        }

        Choice[] vArray = new Choice[v.size()];
        return new Choices(v.toArray(vArray), start, found, Choices.CF_AMBIGUOUS, false, dflt);
    }

    @Override
    public Choices getBestMatch(String text, String locale) {
        init();

        Locale searchedLocale = new Locale(text);
        if (StringUtils.isNotBlank(searchedLocale.getLanguage())) {
            Choice[] v = new Choice[1];
            v[0] = new Choice(null, text, searchedLocale.getDisplayName());
            return new Choices(v, 0, v.length, Choices.CF_UNCERTAIN, false, 0);
        } else {
            return new Choices(Choices.CF_NOTFOUND);
        }
    }

    @Override
    public String getLabel(String key, String locale) {

        Locale language = new Locale(key);

        String name = language.getDisplayName(new Locale(locale));

        if (StringUtils.isNotBlank(name) {
            return StringUtils.capitalize(name);
        } else {
            return "UNKNOWN KEY " + key;
        }
    }

    @Override
    public boolean isScrollable() {
        return true;
    }

    @Override
    public String getPluginInstanceName() {
        return authorityName;
    }

    @Override
    public void setPluginInstanceName(String name) {
        this.authorityName = name;
    }
}
