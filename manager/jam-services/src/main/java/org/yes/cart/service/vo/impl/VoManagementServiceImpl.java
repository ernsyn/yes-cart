/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.*;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoManagementService;

import java.io.IOException;
import java.util.*;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 09:00
 */
public class VoManagementServiceImpl implements VoManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(VoManagementServiceImpl.class);

    private final ManagementService managementService;
    private final DtoShopService shopService;
    private final DtoCategoryService dtoCategoryService;
    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    private static final String LICENSE_ROLE = "ROLE_LICENSEAGREED";

    private static final String DEFAULT =
            "\n\nLicense is not found. This violates condition of use of this software.\n" +
                    "Stop using this software and contact your software provider immediately.\n\n";

    private String licenseText = DEFAULT;

    public VoManagementServiceImpl(final ManagementService managementService,
                                   final DtoShopService shopService,
                                   final DtoCategoryService dtoCategoryService,
                                   final FederationFacade federationFacade,
                                   final VoAssemblySupport voAssemblySupport) {
        this.managementService = managementService;
        this.shopService = shopService;
        this.dtoCategoryService = dtoCategoryService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    @Override
    public VoManager getMyself() throws Exception {
        return getMyselfInternal();
    }

    private VoManager getMyselfInternal() {

        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        final String currentManager = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            return getByEmailInternal(currentManager);
        } catch (Exception exp) {
            LOG.error(exp.getMessage(), exp);
            return null;
        }

    }

    /** {@inheritDoc} */
    @Override
    public VoLicenseAgreement getMyAgreement() throws Exception {
        final VoLicenseAgreement agreement = new VoLicenseAgreement();
        agreement.setText(this.licenseText);
        agreement.setAgreed(this.isAgreedToLicense());
        return agreement;
    }

    /** {@inheritDoc} */
    @Override
    public VoLicenseAgreement acceptMyAgreement() throws Exception {

        final SecurityContext sc = SecurityContextHolder.getContext();
        final String username = sc != null && sc.getAuthentication() != null ? sc.getAuthentication().getName() : null;
        if (StringUtils.isNotBlank(username)) {
            managementService.grantRole(username, LICENSE_ROLE);
        }
        return getMyAgreement();

    }

    /** {@inheritDoc} */
    @Override
    public List<VoManagerInfo> getManagers() throws Exception {
        final List<ManagerDTO> all = managementService.getManagers(null, null, null);
        federationFacade.applyFederationFilter(all, ManagerDTO.class);
        return voAssemblySupport.assembleVos(VoManagerInfo.class, ManagerDTO.class, all);
    }

    /** {@inheritDoc} */
    @Override
    public VoManager getManagerByEmail(String email) throws Exception {
        if (federationFacade.isManageable(email, ManagerDTO.class)) {
            final VoManager voManager = getByEmailInternal(email);
            if (voManager != null) {
                return voManager;
            }
        }
        throw new AccessDeniedException("Access is denied");
    }

    protected VoManager getByEmailInternal(final String email) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ManagerDTO> all = managementService.getManagers(email, null, null);
        if (CollectionUtils.isNotEmpty(all)) {
            final ManagerDTO managerDTO = all.get(0);
            final VoManager voManager = voAssemblySupport.assembleVo(
                    VoManager.class, ManagerDTO.class, new VoManager(), managerDTO);
            final List<VoManagerShop> voManagerShops = new ArrayList<>();
            for (final ShopDTO shop : managementService.getAssignedManagerShops(voManager.getEmail(), false)) {
                final VoManagerShop link = new VoManagerShop();
                link.setManagerId(voManager.getManagerId());
                link.setShopId(shop.getShopId());
                voManagerShops.add(link);
            }
            voManager.setManagerShops(voManagerShops);

            final List<VoManagerRole> voManagerRoles = new ArrayList<>();
            for (final RoleDTO role : managementService.getAssignedManagerRoles(voManager.getEmail())) {
                final VoManagerRole link = new VoManagerRole();
                link.setManagerId(voManager.getManagerId());
                link.setCode(role.getCode());
                voManagerRoles.add(link);
            }
            voManager.setManagerRoles(voManagerRoles);
            final List<VoManagerSupplierCatalog> voManagerSupplierCatalogs = new ArrayList<>();
            for (final String supplierCatalogCode : managerDTO.getProductSupplierCatalogs()) {
                final VoManagerSupplierCatalog link = new VoManagerSupplierCatalog();
                link.setManagerId(voManager.getManagerId());
                link.setCode(supplierCatalogCode);
                voManagerSupplierCatalogs.add(link);
            }
            voManager.setManagerSupplierCatalogs(voManagerSupplierCatalogs);

            final List<VoManagerCategoryCatalog> voManagerCategoryCatalogs = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(managerDTO.getCategoryCatalogs())) {
                final SearchContext filter = new SearchContext(
                        Collections.singletonMap("GUIDs", new ArrayList<>(managerDTO.getCategoryCatalogs())),
                        0,
                        managerDTO.getCategoryCatalogs().size(),
                        "name",
                        false,
                        "GUIDs"
                );
                final SearchResult<CategoryDTO> assigned = dtoCategoryService.findCategories(filter);
                if (!assigned.getItems().isEmpty()) {
                    for (final CategoryDTO category : assigned.getItems()) {
                        final VoManagerCategoryCatalog link = new VoManagerCategoryCatalog();
                        link.setManagerId(voManager.getManagerId());
                        link.setCategoryId(category.getCategoryId());
                        link.setCode(category.getGuid());
                        link.setName(category.getName());
                        voManagerCategoryCatalogs.add(link);
                    }
                }
            }
            voManager.setManagerCategoryCatalogs(voManagerCategoryCatalogs);

            return voManager;
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public VoManager createManager(VoManager voManager) throws Exception {

        if (voManager != null && CollectionUtils.isNotEmpty(voManager.getManagerShops())) {

            checkShopsAndRoles(voManager);

            final ShopDTO shop = shopService.getById(voManager.getManagerShops().get(0).getShopId());
            managementService.addUser(
                    voManager.getEmail(),
                    voManager.getFirstName(),
                    voManager.getLastName(),
                    voManager.getCompanyName1(),
                    voManager.getCompanyName2(),
                    voManager.getCompanyDepartment(),
                    shop.getCode()
            );

            if (voManager.getManagerShops().size() > 1) {
                for (final VoManagerShop otherShop : voManager.getManagerShops().subList(1, voManager.getManagerShops().size())) {
                    final ShopDTO otherShopDTO = shopService.getById(otherShop.getShopId());
                    managementService.grantShop(voManager.getEmail(), otherShopDTO.getCode());
                }
            }

            if (CollectionUtils.isNotEmpty(voManager.getManagerRoles())) {

                for (final VoManagerRole managerRole : voManager.getManagerRoles()) {

                    if ("ROLE_SMADMIN".equals(managerRole.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                        throw new AccessDeniedException("Access is denied");
                    }
                    managementService.grantRole(voManager.getEmail(), managerRole.getCode());

                }
            }

            if (CollectionUtils.isNotEmpty(voManager.getManagerSupplierCatalogs())) {

                for (final VoManagerSupplierCatalog managerSupplierCatalog : voManager.getManagerSupplierCatalogs())  {
                    managementService.grantSupplierCatalog(voManager.getEmail(), managerSupplierCatalog.getCode());
                }

            }

            if (CollectionUtils.isNotEmpty(voManager.getManagerCategoryCatalogs())) {

                for (final VoManagerCategoryCatalog managerCategoryCatalog : voManager.getManagerCategoryCatalogs())  {
                    managementService.grantCategoryCatalog(voManager.getEmail(), managerCategoryCatalog.getCode());
                }

            }

            return getByEmailInternal(voManager.getEmail());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    void checkShopsAndRoles(final VoManager voManager) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        for (final VoManagerShop shop : voManager.getManagerShops()) {
            if (!federationFacade.isShopAccessibleByCurrentManager(shop.getShopId())) {
                throw new AccessDeniedException("Access is denied");
            }
        }

        if (CollectionUtils.isNotEmpty(voManager.getManagerRoles())) {
            final List<RoleDTO> roles = managementService.getRolesList();
            final Set<String> availableRole = new HashSet<>();
            for (final RoleDTO roleDTO : roles) {
                availableRole.add(roleDTO.getCode());
            }
            for (final VoManagerRole roleVo : voManager.getManagerRoles()) {
                if (roleVo.getCode() == null || !availableRole.contains(roleVo.getCode())) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
        }

        if (CollectionUtils.isNotEmpty(voManager.getManagerSupplierCatalogs())) {
            for (final VoManagerSupplierCatalog catalog : voManager.getManagerSupplierCatalogs()) {
                if (!federationFacade.isSupplierCatalogAccessibleByCurrentManager(catalog.getCode())) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
        }

        if (CollectionUtils.isNotEmpty(voManager.getManagerCategoryCatalogs())) {
            for (final VoManagerCategoryCatalog catalog : voManager.getManagerCategoryCatalogs()) {
                if (!federationFacade.isManageable(catalog.getCategoryId(), CategoryDTO.class)) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public VoManager updateManager(VoManager voManager) throws Exception {

        if (voManager != null &&
                (CollectionUtils.isNotEmpty(voManager.getManagerShops()) ||
                        federationFacade.isCurrentUserSystemAdmin())) {

            allowUpdateOnlyBySysAdmin(voManager.getEmail());
            checkShopsAndRoles(voManager);

            managementService.updateUser(
                    voManager.getEmail(),
                    voManager.getFirstName(),
                    voManager.getLastName(),
                    voManager.getCompanyName1(),
                    voManager.getCompanyName2(),
                    voManager.getCompanyDepartment()
            );

            // Shops
            final Set<String> shopsToRevoke = new HashSet<>();
            for (final ShopDTO link : managementService.getAssignedManagerShops(voManager.getEmail(), false)) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    shopsToRevoke.add(link.getCode());
                } // else skip updates for inaccessible shops
            }
            if (CollectionUtils.isNotEmpty(voManager.getManagerShops())) {
                for (final VoManagerShop link : voManager.getManagerShops()) {
                    final ShopDTO shop = shopService.getById(link.getShopId());
                    if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                        if (!shopsToRevoke.contains(shop.getCode())) {
                            managementService.grantShop(voManager.getEmail(), shop.getCode());
                        }
                    } // else skip updates for inaccessible shops
                    shopsToRevoke.remove(shop.getCode()); // Do not revoke, it is still active
                }
            }
            for (final String shopToRevoke : shopsToRevoke) {
                managementService.revokeShop(voManager.getEmail(), shopToRevoke);
            }

            // Roles
            final Set<String> rolesToRevoke = new HashSet<>();
            for (final RoleDTO managerRole : managementService.getAssignedManagerRoles(voManager.getEmail())) {
                if ("ROLE_SMADMIN".equals(managerRole.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                    continue;
                }
                rolesToRevoke.add(managerRole.getCode());
            }
            for (final VoManagerRole managerRole : voManager.getManagerRoles()) {
                if ("ROLE_SMADMIN".equals(managerRole.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                    continue;
                }
                if (!rolesToRevoke.contains(managerRole.getCode())) {
                    managementService.grantRole(voManager.getEmail(), managerRole.getCode());
                }
                rolesToRevoke.remove(managerRole.getCode()); // Do not revoke, it is still active
            }
            for (final String roleToRevoke : rolesToRevoke) {
                managementService.revokeRole(voManager.getEmail(), roleToRevoke);
            }

            // Suppliers
            final Set<String> suppliersToRevoke = new HashSet<>();
            for (final String managerCat : managementService.getAssignedManagerSupplierCatalogs(voManager.getEmail())) {
                if (federationFacade.isSupplierCatalogAccessibleByCurrentManager(managerCat)) {
                    suppliersToRevoke.add(managerCat);
                } // else skip updates for inaccessible suppliers
            }
            for (final VoManagerSupplierCatalog managerCat : voManager.getManagerSupplierCatalogs()) {
                if (federationFacade.isSupplierCatalogAccessibleByCurrentManager(managerCat.getCode())) {
                    if (!suppliersToRevoke.contains(managerCat.getCode())) {
                        managementService.grantSupplierCatalog(voManager.getEmail(), managerCat.getCode());
                    }
                }
                suppliersToRevoke.remove(managerCat.getCode());  // Do not revoke, it is still active
            }
            for (final String supplierToRevoke : suppliersToRevoke) {
                managementService.revokeSupplierCatalog(voManager.getEmail(), supplierToRevoke);
            }

            // Categories
            final Set<String> categoriesToRevoke = new HashSet<>();
            final List<String> codes = managementService.getAssignedManagerCategoryCatalogs(voManager.getEmail());
            if (CollectionUtils.isNotEmpty(codes)) {
                final SearchContext filter = new SearchContext(
                        Collections.singletonMap("GUIDs", new ArrayList<>(codes)),
                        0,
                        codes.size(),
                        "name",
                        false,
                        "GUIDs"
                );
                final SearchResult<CategoryDTO> assigned = dtoCategoryService.findCategories(filter);
                if (!assigned.getItems().isEmpty()) {
                    for (final CategoryDTO category : assigned.getItems()) {
                        if (federationFacade.isManageable(category.getCategoryId(), CategoryDTO.class)) {
                            categoriesToRevoke.add(category.getGuid());
                        } // else skip updates for inaccessible suppliers
                    }
                }
            }
            for (final VoManagerCategoryCatalog managerCat : voManager.getManagerCategoryCatalogs()) {
                if (federationFacade.isManageable(managerCat.getCategoryId(), CategoryDTO.class)) {
                    if (!categoriesToRevoke.contains(managerCat.getCode())) {
                        managementService.grantCategoryCatalog(voManager.getEmail(), managerCat.getCode());
                    }
                }
                categoriesToRevoke.remove(managerCat.getCode());  // Do not revoke, it is still active
            }
            for (final String supplierToRevoke : categoriesToRevoke) {
                managementService.revokeCategoryCatalog(voManager.getEmail(), supplierToRevoke);
            }

            return getByEmailInternal(voManager.getEmail());
            
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public void deleteManager(String email) throws Exception {
        if (federationFacade.isManageable(email, ManagerDTO.class)) {
            managementService.deleteUser(email);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateDashboard(final String email, final String dashboardWidgets) throws Exception {
        if (federationFacade.isManageable(email, ManagerDTO.class)) {
            managementService.updateDashboard(email, dashboardWidgets);
        } else {

            final VoManager myself = getMyselfInternal();
            if (myself != null && email != null && email.equals(myself.getEmail())) {
                managementService.updateDashboard(email, dashboardWidgets);
            } else {
                throw new AccessDeniedException("Access is denied");
            }

        }
    }

    /** {@inheritDoc} */
    @Override
    public void resetPassword(String email) throws Exception {
        if (federationFacade.isManageable(email, ManagerDTO.class)) {
            managementService.resetPassword(email);
        } else {

            final VoManager myself = getMyselfInternal();
            if (myself != null && email != null && email.equals(myself.getEmail())) {
                managementService.resetPassword(email);
            } else {
                throw new AccessDeniedException("Access is denied");
            }

        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateDisabledFlag(String manager, boolean disabled) throws Exception {
        allowUpdateOnlyBySysAdmin(manager);
        if (federationFacade.isManageable(manager, ManagerDTO.class)) {
            if (disabled) {
                managementService.disableAccount(manager);
            } else {
                managementService.enableAccount(manager);
            }
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private void allowUpdateOnlyBySysAdmin(String manager) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<RoleDTO> roles = managementService.getAssignedManagerRoles(manager);
        for (final RoleDTO role : roles) {
            if ("ROLE_SMADMIN".equals(role.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                throw new AccessDeniedException("Access is denied");
            }
        }
    }

    private boolean isAgreedToLicense() {

        if (!DEFAULT.equals(licenseText)) {
            final SecurityContext sc = SecurityContextHolder.getContext();
            final String username = sc != null && sc.getAuthentication() != null ? sc.getAuthentication().getName() : null;
            if (StringUtils.isNotBlank(username)) {
                try {
                    final List<RoleDTO> roles = managementService.getAssignedManagerRoles(username);
                    for (final RoleDTO role : roles) {
                        if (LICENSE_ROLE.equals(role.getCode())) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Unable to retrieve roles for {}", username);
                }
            }
        }

        return false;
    }

    public void setLicenseTextResource(final Resource licenseTextResource) throws IOException {

        licenseText = IOUtils.toString(licenseTextResource.getInputStream(), "UTF-8");

    }



}
