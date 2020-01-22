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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueCategoryDTOImpl;
import org.yes.cart.domain.dto.impl.CategoryDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityCategory;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.dto.AttrValueDTOComparator;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCategoryService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCategoryServiceImpl
        extends AbstractDtoServiceImpl<CategoryDTO, CategoryDTOImpl, Category>
        implements DtoCategoryService {

    private static final CategoryRankNameComparator CATEGORY_RANK_NAME_COMPARATOR = new CategoryRankNameComparator();
    private static final AttrValueDTOComparator ATTR_VALUE_DTO_COMPARATOR = new AttrValueDTOComparator();

    private final GenericService<Shop> shopGenericService;
    private final GenericService<ShopCategory> shopCategoryGenericService;
    private final GenericService<ProductType> productTypeService;

    private final GenericService<Attribute> attributeService;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueEntityCategory, Long> attrValueEntityCategoryDao;

    private final Assembler attrValueAssembler;
    private final Assembler shopCategoryAssembler;

    private final ImageService imageService;
    private final FileService fileService;
    private final SystemService systemService;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory             {@link DtoFactory}
     * @param categoryGenericService category     {@link GenericService}
     * @param imageService           {@link ImageService} to manipulate  related images.
     * @param fileService {@link FileService} to manipulate related files
     * @param shopGenericService     shop service
     * @param systemService          system service
     */
    public DtoCategoryServiceImpl(final DtoFactory dtoFactory,
                                  final GenericService<Category> categoryGenericService,
                                  final GenericService<Shop> shopGenericService,
                                  final GenericService<ShopCategory> shopCategoryGenericService,
                                  final GenericService<ProductType> productTypeService,
                                  final DtoAttributeService dtoAttributeService,
                                  final GenericDAO<AttrValueEntityCategory, Long> attrValueEntityCategoryDao,
                                  final ImageService imageService,
                                  final FileService fileService,
                                  final AdaptersRepository adaptersRepository,
                                  final SystemService systemService) {
        super(dtoFactory, categoryGenericService, adaptersRepository);


        this.shopCategoryGenericService = shopCategoryGenericService;
        this.productTypeService = productTypeService;
        this.attrValueEntityCategoryDao = attrValueEntityCategoryDao;
        this.dtoAttributeService = dtoAttributeService;
        this.shopGenericService = shopGenericService;
        this.systemService = systemService;

        this.attributeService = dtoAttributeService.getService();


        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueCategoryDTO.class),
                attributeService.getGenericDao().getEntityFactory().getImplClass(AttrValueCategory.class));

        this.shopCategoryAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(ShopCategoryDTO.class),
                attributeService.getGenericDao().getEntityFactory().getImplClass(ShopCategory.class));

        this.imageService = imageService;
        this.fileService = fileService;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getAllWithAvailabilityFilter(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getAllWithAvailabilityFilter(final boolean withAvailabilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        CategoryService categoryService = (CategoryService) service;
        Category root = categoryService.getRootCategory();
        CategoryDTO rootDTO = getById(root.getCategoryId());
        if (rootDTO != null) {
            loadBranch(rootDTO, withAvailabilityFiltering, Integer.MAX_VALUE, Collections.emptyList());
            return Collections.singletonList(rootDTO);
        }
        return Collections.emptyList();
    }


    private List<CategoryDTO> loadBranch(final CategoryDTO rootDTO,
                                         final boolean withAvailabilityFiltering,
                                         final int expandLevel,
                                         final List<Long> expandNodes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (rootDTO != null) {
            final CategoryService categoryService = (CategoryService) service;
            final List<Category> childCategories = new ArrayList<>();
            if (rootDTO.getLinkToId() != null) {
                childCategories.addAll(categoryService.findChildCategoriesWithAvailability(
                        rootDTO.getLinkToId(),
                        withAvailabilityFiltering));
            }
            childCategories.addAll(categoryService.findChildCategoriesWithAvailability(
                    rootDTO.getCategoryId(),
                    withAvailabilityFiltering));
            childCategories.sort(CATEGORY_RANK_NAME_COMPARATOR);
            final List<CategoryDTO> childCategoriesDTO = new ArrayList<>(childCategories.size());
            fillDTOs(childCategories, childCategoriesDTO);
            rootDTO.setChildren(childCategoriesDTO);
            if (expandLevel > 1 || !expandNodes.isEmpty()) {
                for (CategoryDTO dto : childCategoriesDTO) {
                    if (expandLevel > 1 || expandNodes.contains(dto.getCategoryId())) {
                        dto.setChildren(loadBranch(dto, withAvailabilityFiltering, expandLevel - 1, expandNodes));
                    }
                }
            }
            return childCategoriesDTO;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getBranchById(final long categoryId, final List<Long> expand)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getBranchByIdWithAvailabilityFilter(categoryId, false, expand);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getBranchByIdWithAvailabilityFilter(final long categoryId, final boolean withAvailabilityFiltering, final List<Long> expand)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CategoryDTO> branch = new ArrayList<>();
        CategoryService categoryService = (CategoryService) service;
        final Category branchRoot = categoryId > 0L ? categoryService.getById(categoryId) : categoryService.getRootCategory();
        if (branchRoot != null) {
            CategoryDTO rootDTO = getById(branchRoot.getCategoryId());
            if (rootDTO != null) {
                loadBranch(rootDTO, withAvailabilityFiltering, 1, expand != null ? expand : Collections.emptyList());
            }
            branch.add(rootDTO);
        }
        return branch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assemblyPostProcess(final CategoryDTO dto, final Category entity) {
        if (entity.getLinkToId() != null) {
            final Category link = ((CategoryService)getService()).getById(entity.getLinkToId());
            if (link != null) {
                dto.setLinkToName(getParentName(link) + " > " + link.getName());
            }
        }
        dto.setParentName(getParentName(entity));
        super.assemblyPostProcess(dto, entity);
    }


    protected String getParentName(final Category entity) {
        if (entity.getParentId() > 0L && entity.getParentId() != entity.getCategoryId()) {
            final Category parent = ((CategoryService)getService()).getById(entity.getParentId());
            if (parent != null) {
                final String oneUp = getParentName(parent);
                if (oneUp != null) {
                    return oneUp + " > " + parent.getName();
                }
                return parent.getName();
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void createPostProcess(final CategoryDTO dto, final Category entity) {
        bindDictionaryData(dto, entity);
        ensureBlankUriIsNull(entity);
        ensureZeroLinkIdIsNull(entity);
        super.createPostProcess(dto, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updatePostProcess(final CategoryDTO dto, final Category entity) {
        bindDictionaryData(dto, entity);
        ensureBlankUriIsNull(entity);
        ensureZeroLinkIdIsNull(entity);
        super.updatePostProcess(dto, entity);
    }


    private void ensureBlankUriIsNull(final Seoable entity) {
        if (entity.getSeo() != null && entity.getSeo().getUri() != null && StringUtils.isBlank(entity.getSeo().getUri())) {
            entity.getSeo().setUri(null);
        }
    }

    private void ensureZeroLinkIdIsNull(final Category category) {
        if (category.getLinkToId() != null && category.getLinkToId() <= 0L) {
            category.setLinkToId(null);
        }
    }

    /**
     * Bind data from dictionaries to category.
     *
     * @param instance category dto to collect data from
     * @param category category to set dictionary data to.
     */
    private void bindDictionaryData(final CategoryDTO instance, final Category category) {
        if (instance.getProductTypeId() != null && instance.getProductTypeId() > 0) {
            category.setProductType(productTypeService.findById(instance.getProductTypeId()));
        } else {
            category.setProductType(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(final long id) {
        final Category cat = service.findById(id);
        if (cat != null && !cat.isRoot()) {
            ((ShopCategoryService) shopCategoryGenericService).deleteAll(service.findById(id));
            super.remove(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Category> categories = new ArrayList<>(((ShopService) shopGenericService).findAllByShopId(shopId));
        categories.sort(CATEGORY_RANK_NAME_COMPARATOR);
        final List<CategoryDTO> dtos = new ArrayList<>(categories.size());
        fillDTOs(categories, dtos);
        return dtos;
    }


    private final static char[] PARENT_OR_URI = new char[] { '^', '@' };
    static {
        Arrays.sort(PARENT_OR_URI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResult<CategoryDTO> findCategories(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "categoryIds", "GUIDs");
        final List filterParam = params.get("filter");
        final List categoriesParam = params.get("categoryIds");
        final List guidsParam = params.get("GUIDs");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final CategoryService categoryService = (CategoryService) service;

        final Map<String, List> currentFilter = new HashMap<>();
        if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof String && StringUtils.isNotBlank((String) filterParam.get(0))) {

            final String textFilter = ((String) filterParam.get(0)).trim();

            final Pair<String, String> parentOrUri = ComplexSearchUtils.checkSpecialSearch(textFilter, PARENT_OR_URI);

            if (parentOrUri == null) {

                SearchContext.JoinMode.OR.setMode(currentFilter);
                currentFilter.put("guid", Collections.singletonList(textFilter));
                currentFilter.put("name", Collections.singletonList(textFilter));
                currentFilter.put("seoInternal.uri", Collections.singletonList(textFilter));

            } else {

                if ("@".equals(parentOrUri.getFirst())) {

                    currentFilter.put("seoInternal.uri", Collections.singletonList(parentOrUri.getSecond()));

                } else if ("^".equals(parentOrUri.getFirst())) {

                    final Long parentCatId = categoryService.findCategoryIdByGUID(parentOrUri.getSecond());
                    if (parentCatId != null) {

                        SearchContext.JoinMode.OR.setMode(currentFilter);
                        currentFilter.put("categoryId", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(parentCatId)));
                        currentFilter.put("parentId", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(parentCatId)));

                    }

                }

            }

        }

        // Filter by GUID
        if (CollectionUtils.isNotEmpty(guidsParam)) {
            currentFilter.put("guid", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(guidsParam)));
        }

        // Filter by accessible categoryId
        if (CollectionUtils.isNotEmpty(categoriesParam)) {
            currentFilter.put("categoryIds", categoriesParam);
        }

        final int count = categoryService.findCategoryCount(currentFilter);
        if (count > startIndex) {

            final List<CategoryDTO> entities = new ArrayList<>();
            final List<Category> categories = categoryService.findCategories(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

            fillDTOs(categories, entities);

            return new SearchResult<>(filter, entities, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getByProductId(final long productId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Category> categories = ((CategoryService) service).findByProductId(productId);
        final List<CategoryDTO> dtos = new ArrayList<>(categories.size());
        fillDTOs(categories, dtos);
        return dtos;
    }

    /**
     * Assign category to shop.
     *
     * @param categoryId category id
     * @param shopId     shop id
     * @return {@link ShopCategory}
     */
    @Override
    public ShopCategoryDTO assignToShop(final long categoryId, final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final ShopCategory shopCategory = ((ShopCategoryService) shopCategoryGenericService).assignToShop(categoryId, shopId);
        ShopCategoryDTO dto = dtoFactory.getByIface(ShopCategoryDTO.class);
        shopCategoryAssembler.assembleDto(dto, shopCategory, getAdaptersRepository(), dtoFactory);
        return dto;
    }

    /**
     * Un-assign category from shop.
     *
     * @param categoryId category id
     * @param shopId     shop id
     */
    @Override
    public void unassignFromShop(final long categoryId, final long shopId) {
        ((ShopCategoryService) shopCategoryGenericService).unassignFromShop(categoryId, shopId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUriAvailableForCategory(final String seoUri, final Long categoryId) {

        final Long catId = ((CategoryService) service).findCategoryIdBySeoUri(seoUri);
        return catId == null || catId.equals(categoryId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGuidAvailableForCategory(final String guid, final Long categoryId) {

        final Long catId = ((CategoryService) service).findCategoryIdByGUID(guid);
        return catId == null || catId.equals(categoryId);

    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    @Override
    public Class<CategoryDTO> getDtoIFace() {
        return CategoryDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    @Override
    public Class<CategoryDTOImpl> getDtoImpl() {
        return CategoryDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    @Override
    public Class<Category> getEntityIFace() {
        return Category.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AttrValueCategoryDTO> result = new ArrayList<>();
        final CategoryDTO categoryDTO = getById(entityPk);
        if (categoryDTO != null) {
            result.addAll(categoryDTO.getAttributes());
            final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                    AttributeGroupNames.CATEGORY,
                    getCodes(result));
            for (AttributeDTO attributeDTO : availableAttributeDTOs) {
                AttrValueCategoryDTO attrValueCategoryDTO = getAssemblerDtoFactory().getByIface(AttrValueCategoryDTO.class);
                attrValueCategoryDTO.setAttributeDTO(attributeDTO);
                attrValueCategoryDTO.setCategoryId(entityPk);
                result.add(attrValueCategoryDTO);
            }
            result.sort(ATTR_VALUE_DTO_COMPARATOR);
        }

        return result;
    }

    /**
     * Update attribute value.
     *
     * @param attrValueDTO value to update
     * @return updated value
     */
    @Override
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityCategory valueEntityCategory = attrValueEntityCategoryDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityCategory, getAdaptersRepository(), dtoFactory);
        attrValueEntityCategoryDao.update(valueEntityCategory);
        return attrValueDTO;

    }


    /**
     * Delete attribute value by given pk value.
     *
     * @param attributeValuePk given pk value.
     */
    @Override
    public long deleteAttributeValue(final long attributeValuePk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException{
        final AttrValueEntityCategory valueEntityCategory = attrValueEntityCategoryDao.findById(attributeValuePk);
        final AttributeDTO attributeDTO = dtoAttributeService.findByAttributeCode(valueEntityCategory.getAttributeCode());
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            imageService.deleteImage(valueEntityCategory.getVal(),
                    Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN, systemService.getImageRepositoryDirectory());
        } else if (Etype.FILE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            fileService.deleteFile(valueEntityCategory.getVal(),
                    Constants.CATEGORY_FILE_REPOSITORY_URL_PATTERN, systemService.getFileRepositoryDirectory());
        }

        attrValueEntityCategoryDao.delete(valueEntityCategory);
        return valueEntityCategory.getCategory().getCategoryId();
    }

    /**
     * Create attribute value
     *
     * @param attrValueDTO value to persist
     * @return created value
     */
    @Override
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = attributeService.findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final Category category = service.findById(((AttrValueCategoryDTO) attrValueDTO).getCategoryId());
        if (!multivalue) {
            for (final AttrValueCategory avp : category.getAttributes()) {
                if (avp.getAttributeCode().equals(atr.getCode())) {
                    // this is a duplicate, so need to update
                    attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                    return updateEntityAttributeValue(attrValueDTO);
                }
            }
        }

        AttrValueCategory valueEntityCategory = getPersistenceEntityFactory().getByIface(AttrValueCategory.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityCategory, getAdaptersRepository(), dtoFactory);
        valueEntityCategory.setAttributeCode(atr.getCode());
        valueEntityCategory.setCategory(category);
        valueEntityCategory = attrValueEntityCategoryDao.create((AttrValueEntityCategory) valueEntityCategory);
        attrValueDTO.setAttrvalueId(valueEntityCategory.getAttrvalueId());
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueCategoryDTO dto = new AttrValueCategoryDTOImpl();
        dto.setCategoryId(entityPk);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageRepositoryUrlPattern() {
        return Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileRepositoryUrlPattern() {
        return Constants.CATEGORY_FILE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSysFileRepositoryUrlPattern() {
        return Constants.CATEGORY_SYSFILE_REPOSITORY_URL_PATTERN;
    }

}
