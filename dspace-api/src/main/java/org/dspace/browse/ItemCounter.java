/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.browse;

import org.apache.logging.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class provides a standard interface to all item counting
 * operations for communities and collections.
 *
 * In the event that the data cache is not being used, this class will return direct
 * real time counts of content.
 */
public class ItemCounter {
    /**
     * Log4j logger
     */
    private static Logger log = org.apache.logging.log4j.LogManager.getLogger(ItemCounter.class);

    @Autowired
    protected ItemService itemService;
    @Autowired
    protected ConfigurationService configurationService;
    @Autowired
    protected ItemCountDAO itemCountDAO;

    /**
     * Construct a new item counter
     */
    protected ItemCounter() {
    }

    /**
     * Get the count of the items in the given container. If the configuration
     * value webui.strengths.show is equal to 'true' this method will return all
     * archived items. If the configuration value webui.strengths.show is equal to
     * 'false' this method will return -1.
     * If the configuration value webui.strengths.rights-aware is set to 'true',
     * this method will return only the items that the user in the given context
     * is authorized to read. If the configuration value webui.strengths.rights-aware
     * is equal to 'false' this method will return all archived items.
     *
     * @param dso DSpaceObject
     * @return count (-1 is returned if count could not be determined or is disabled)
     */
    public int getCount(Context context, DSpaceObject dso) {
        boolean showStrengths = configurationService.getBooleanProperty("webui.strengths.show", false);
        boolean rightsAware = configurationService.getBooleanProperty("webui.strengths.rights-aware", false);
        if (!showStrengths) {
            return -1;
        }

        if (dso instanceof Collection || dso instanceof Community) {
            if (rightsAware) {
                return itemCountDAO.getCountReadAuthorized(context, dso);
            } else {
                return itemCountDAO.getCount(dso);
            }
        }

        return 0;
    }
}
