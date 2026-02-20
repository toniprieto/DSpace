/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.browse;

import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.SearchService;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.SolrSearchCore;
import org.dspace.discovery.indexobject.IndexableItem;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Discovery (Solr) driver implementing ItemCountDAO interface to look up item
 * count information in communities and collections. Caching operations are
 * intentionally not implemented because Solr already is our cache.
 */
public class ItemCountDAOSolr implements ItemCountDAO {
    /**
     * Log4j logger
     */
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(ItemCountDAOSolr.class);

    /**
     * Solr search service
     */
    @Autowired
    protected SearchService searchService;

    @Autowired
    SolrSearchCore solrSearchCore;

    //SolrSearchCore solrSearchCore = DSpaceServicesFactory.getInstance().getServiceManager()
    //    .getServiceByName(SolrSearchCore.class.getName(), SolrSearchCore.class);


    /**
     * Get the count of the items in the given container.
     *
     * @param dso DSpaceObject
     * @return count
     */
    @Override
    public int getCount(DSpaceObject dso) {

        try {
            if (solrSearchCore.getSolr() == null) {
                return 0;
            }

            SolrQuery solrQuery = new SolrQuery();

            String query = "*:*";
            solrQuery.setQuery(query);
            solrQuery.addFilterQuery("search.resourcetype:" + IndexableItem.TYPE);
            solrQuery.addFilterQuery("NOT(discoverable:false)");
            solrQuery.addFilterQuery("NOT(withdrawn:true)");
            if (dso instanceof Collection) {
                solrQuery.addFilterQuery("location.coll:" + dso.getID());
            } else if (dso instanceof Community) {
                solrQuery.addFilterQuery("location.comm:" + dso.getID());
            }
            solrQuery.setRows(0);

            QueryResponse solrQueryResponse = solrSearchCore.getSolr().query(solrQuery,
                solrSearchCore.REQUEST_METHOD);
            if (solrQueryResponse != null) {
                return (int) solrQueryResponse.getResults().getNumFound();
            }
        } catch (Exception e) {
            log.error("Error getting item count: ", e);
        }
        return 0;
    }

    @Override
    public int getCountReadAuthorized(Context context, DSpaceObject dso) {
        DiscoverQuery query = new DiscoverQuery();
        query.addFilterQueries("search.resourcetype:" + IndexableItem.TYPE);    // count only items
        query.addFilterQueries("NOT(discoverable:false)");  // only discoverable
        query.addFilterQueries("withdrawn:false");  // only not withdrawn
        query.addFilterQueries("archived:true");  // only archived
        if (dso instanceof Collection) {
            query.addFilterQueries("location.coll:" + dso.getID());
        } else if (dso instanceof Community) {
            query.addFilterQueries("location.comm:" + dso.getID());
        }
        query.setMaxResults(0);

        DiscoverResult sResponse;
        try {
            sResponse = searchService.search(context, query);
            if (sResponse != null) {
                return (int) sResponse.getTotalSearchResults();
            }
        } catch (SearchServiceException e) {
            log.error("Error getting item count: ", e);
        }
        return 0;
    }
}
