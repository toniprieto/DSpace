/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.browse;

import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;

/**
 * Interface for data access of cached community and collection item count
 * information
 */
public interface ItemCountDAO {

    /**
     * Get the total number of archived items in the given DSpaceObject container.
     * This method does not take user permissions into account.
     * This method will only succeed if the DSpaceObject is an instance of either a Community or a
     * Collection. Otherwise, it will return 0.
     *
     * @param dso Dspace Object
     * @return count
     */
    int getCount(DSpaceObject dso);

    /**
     * Get the number of items in the given DSpaceObject container that the user in the given context is
     * authorized to read. This method will only succeed if the DSpaceObject is an instance of either
     * a Community or a Collection. Otherwise, it will return 0.
     *
     * @param context DSpace Context
     * @param dso Dspace Object
     * @return count
     */
    int getCountReadAuthorized(Context context, DSpaceObject dso);

}
