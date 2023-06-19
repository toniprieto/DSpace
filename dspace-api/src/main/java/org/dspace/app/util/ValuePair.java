/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.util;

import java.util.HashMap;

public class ValuePair {

    private String stored;

    private String displayed;

    private HashMap<String, String> displayedTranslations = new HashMap<>();

    public ValuePair(String stored, String displayed) {
        this.stored = stored;
        this.displayed = displayed;
    }

    public String getStored() {
        return stored;
    }

    public void setStored(String stored) {
        this.stored = stored;
    }

    public String getDisplayed() {
        return displayed;
    }

    public void setDisplayed(String displayed) {
        this.displayed = displayed;
    }

    public HashMap<String,String> getDisplayedTranslations() {
        return displayedTranslations;
    }

    public void setDisplayedTranslations(HashMap<String,String> displayedTranslations) {
        this.displayedTranslations = displayedTranslations;
    }

    public String getDisplayedTranslation(String lang) {
        String val = displayedTranslations.get(lang);

        if (val == null) {
            return displayed;
        } else {
            return val;
        }
    }
}
