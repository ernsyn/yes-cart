/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkjob.product;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkjob.cron.AbstractLastRunDependentProcessorImpl;
import org.yes.cart.cache.CacheBundleHelper;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerLoggerWrapperImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.utils.DateUtils;

import java.time.Instant;
import java.util.List;

/**
 * Processor that scrolls though all modified inventory and re-indexes products so that
 * all latest information is propagated to all nodes.
 *
 * Last time this job runs is stored in system preferences: JOB_PRODINVUP_LR_[NODEID]
 * So that next run we only scan orders that have changed since last job run.
 *
 * User: denispavlov
 * Date: 27/04/2015
 * Time: 15:42
 */
public class ProductInventoryChangedProcessorImpl extends AbstractLastRunDependentProcessorImpl
        implements ProductInventoryChangedProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(ProductInventoryChangedProcessorImpl.class);

    private static final String LAST_RUN_PREF = "JOB_PRODINVUP_LR_";

    private final SkuWarehouseService skuWarehouseService;
    private final ProductService productService;
    private final NodeService nodeService;
    private final CacheBundleHelper productCacheHelper;

    private final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG);

    public ProductInventoryChangedProcessorImpl(final SkuWarehouseService skuWarehouseService,
                                                final ProductService productService,
                                                final NodeService nodeService,
                                                final SystemService systemService,
                                                final CacheBundleHelper productCacheHelper) {
        super(systemService);
        this.skuWarehouseService = skuWarehouseService;
        this.productService = productService;
        this.nodeService = nodeService;
        this.productCacheHelper = productCacheHelper;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    protected String getLastRunPreferenceAttributeName() {
        return LAST_RUN_PREF + getNodeId();
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doRun(final Instant lastRun) {

        final String nodeId = getNodeId();

        if (isLuceneIndexDisabled()) {
            LOG.info("Reindexing products inventory updates on {} ... disabled", nodeId);
            return false;
        }

        if (isFullIndexInProgress()) {
            LOG.info("Reindexing inventory updates on {}, reindexed ALL is already in progress", nodeId);
            return true;
        }

        int batchSize = getBatchSize();

        LOG.info("Check changed orders products to be reindexed on {}, batch {}", nodeId, batchSize);

        List<String> productSkus = skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastRun);

        if (productSkus != null && !productSkus.isEmpty()) {

            // Check again to see if we do not have bulk updates
            int count = productSkus.size();
            int full = getChangeMaxSize();

            boolean runBatch = count < full;

            if (runBatch) {
                try {
                    Thread.sleep(getDeltaCheckDelay());
                } catch (InterruptedException e) {
                    // OK
                }
                productSkus = skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastRun);
                int delta = productSkus.size() - count;
                int maxDelta = getDeltaCheckSize();
                if (delta > maxDelta) {
                    LOG.info("Detected bulking operation ... {} inventory records changed in past {} seconds (max: {}). Postponing check until next run.",
                            delta, getDeltaCheckDelay() / 1000, maxDelta);
                    return false;
                }
            }

            // Check whether we need batch or full
            count = productSkus.size();
            runBatch = count < full;

            listener.notifyPing("Last change detected " + count + ", indexing will run " + (runBatch ? "batch" : "full"));

            LOG.info("Inventory changed for {} since {}", productSkus.size(), DateUtils.formatSDT(lastRun));

            if (runBatch) {
                int fromIndex = 0;
                int toIndex;
                while (fromIndex < productSkus.size()) {

                    if (isFullIndexInProgress()) {
                        LOG.info("Reindexing inventory updates on {}, reindexed ALL is already in progress", nodeId);
                        return true;
                    }

                    toIndex = fromIndex + batchSize > productSkus.size() ? productSkus.size() : fromIndex + batchSize;
                    final List<String> skuBatch = productSkus.subList(fromIndex, toIndex);
                    LOG.info("Reindexing SKU {}  ... so far reindexed {}", skuBatch, fromIndex);

                    self().reindexBatch(skuBatch);

                    fromIndex = toIndex;

                }
                LOG.info("Reindexing inventory updates on {}, reindexed {}", nodeId, count);
            } else {

                if (isFullIndexInProgress()) {
                    LOG.info("Reindexing inventory updates on {}, reindexed ALL is already in progress", nodeId);
                    return true;
                }

                self().reindexBatch(null);
                LOG.info("Reindexing inventory updates on {}, reindexed ALL", nodeId);
            }

            flushCaches();
        }

        LOG.info("Reindexing inventory updates on {} ... completed", nodeId);

        return true;
    }

    protected void flushCaches() {

        productCacheHelper.flushBundleCaches();

    }

    protected boolean isFullIndexInProgress() {
        return productService.getProductsFullTextIndexState().isFullTextSearchReindexInProgress() ||
                productService.getProductsSkuFullTextIndexState().isFullTextSearchReindexInProgress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexBatch(final List<String> skuCodes) {

        if (skuCodes == null) {
            // do full reindex
            productService.reindexProducts(getBatchSize(), true);
            productService.reindexProductsSku(getBatchSize(), true);

        } else {
            for (final String sku : skuCodes) {
                // batch only
                productService.reindexProductSku(sku);
            }
        }

    }

    protected String getNodeId() {
        return nodeService.getCurrentNodeId();
    }

    protected Boolean isLuceneIndexDisabled() {
        return nodeService.getCurrentNode().isFtIndexDisabled();
    }

    protected int getBatchSize() {
        return NumberUtils.toInt(getSystemService().getAttributeValue(AttributeNamesKeys.System.JOB_REINDEX_PRODUCT_BATCH_SIZE), 100);
    }

    protected int getDeltaCheckSize() {
        return NumberUtils.toInt(getSystemService().getAttributeValue(AttributeNamesKeys.System.JOB_PRODUCT_INVENTORY_UPDATE_DELTA), 100);
    }

    protected int getChangeMaxSize() {
        return NumberUtils.toInt(getSystemService().getAttributeValue(AttributeNamesKeys.System.JOB_PRODUCT_INVENTORY_FULL_THRESHOLD), 1000);
    }

    protected long getDeltaCheckDelay() {
        return NumberUtils.toLong(getSystemService().getAttributeValue(AttributeNamesKeys.System.JOB_PRODUCT_INVENTORY_UPDATE_DELTA_DELAY_SECONDS), 15) * 1000L;
    }



    private ProductInventoryChangedProcessorInternal self;

    private ProductInventoryChangedProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public ProductInventoryChangedProcessorInternal getSelf() {
        return null;
    }

}
