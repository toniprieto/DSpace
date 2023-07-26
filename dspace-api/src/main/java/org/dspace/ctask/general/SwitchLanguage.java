/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.ctask.general;

import java.io.IOException;
import java.sql.SQLException;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;

public class SwitchLanguage extends AbstractCurationTask {

    protected int status = Curator.CURATE_UNSET;
    protected String result = null;

    @Override
    public int perform(DSpaceObject dso) throws IOException {

        if (dso instanceof Item) {
            Item item = (Item) dso;

            String lang = itemService.getMetadataFirstValue(item, "dc", "language", "iso", Item.ANY);

            try {

                if (lang == null || "es".equals(lang)) {
                    itemService.clearMetadata(Curator.curationContext(), item, "dc", "language", "iso", Item.ANY);
                    itemService.addMetadata(Curator.curationContext(), item, "dc", "language", "iso", null, "en");
                    setResult("Item " + item.getHandle() + ", dc.language.iso to en");
                } else {
                    itemService.clearMetadata(Curator.curationContext(), item, "dc", "language", "iso", Item.ANY);
                    itemService.addMetadata(Curator.curationContext(), item, "dc", "language", "iso", null, "es");
                    setResult("Item " + item.getHandle() + ", dc.language.iso to es");
                }

                itemService.update(Curator.curationContext(), item);
                status = Curator.CURATE_SUCCESS;
            } catch (SQLException | AuthorizeException e) {
                e.printStackTrace();
            }

            setResult(result);
            report(result);
        }

        return status;
    }


}
