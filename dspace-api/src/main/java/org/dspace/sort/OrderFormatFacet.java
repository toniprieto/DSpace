/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.sort;

import org.dspace.text.filter.DecomposeDiactritics;
import org.dspace.text.filter.LowerCaseAndTrim;
import org.dspace.text.filter.StripDiacritics;
import org.dspace.text.filter.TextFilter;

/**
 * Standard facet ordering delegate implementation that handles facet value normalization for ordering and filtering.
 *
 * @author Toni Prieto
 */
public class OrderFormatFacet extends AbstractTextFilterOFD {
    {
        filters = new TextFilter[] {new DecomposeDiactritics(),
            new StripDiacritics(),
            new LowerCaseAndTrim()};
    }
}