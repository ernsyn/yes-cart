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

package org.yes.cart.service.dto;

import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * User management service allow
 * to manage users, roles and
 * relationships between them.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ManagementService {

    /**
     * Add new user.
     *
     *
     * @param userId    user email
     * @param firstName first name
     * @param lastName  last name
     * @param company1   company1
     * @param company2   company2
     * @param department department
     * @param shopCode  shop code for this user
     *
     * @throws java.io.UnsupportedEncodingException
     *          in case of bad encoding
     * @throws java.security.NoSuchAlgorithmException
     *          in case of bad algorithm
     */
    void addUser(String userId,
                 String firstName,
                 String lastName,
                 String company1,
                 String company2,
                 String department,
                 String shopCode)
            throws NoSuchAlgorithmException, UnsupportedEncodingException;

    /**
     * Get the list of managers by given filtering criteria.
     *
     * @param emailFilter     optional email filter
     * @param firstNameFilter optional first name filter
     * @param lastNameFilter  optional last name filter
     * @return list of managers dto that match given criteria
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<ManagerDTO> getManagers(final String emailFilter,
                                 final String firstNameFilter,
                                 final String lastNameFilter)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get the roles assigned to manager.
     *
     * @param userId user email
     * @return list of assigned roles
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<RoleDTO> getAssignedManagerRoles(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get the roles available to manager.
     *
     * @param userId user email
     * @return list of available roles
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<RoleDTO> getAvailableManagerRoles(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get the shops assigned to manager
     *
     * @param userId user email
     * @param includeSubs include sub shops
     * @return list of assigned shops
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<ShopDTO> getAssignedManagerShops(String userId, final boolean includeSubs)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get the shops available to manager.
     *
     * @param userId user email
     * @return list of available shops
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<ShopDTO> getAvailableManagerShops(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get the suppliers assigned to manager
     *
     * @param userId user email
     * @return list of supplier codes
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<String> getAssignedManagerSupplierCatalogs(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get the categories assigned to manager
     *
     * @param userId user email
     * @return list of GUIDs
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<String> getAssignedManagerCategoryCatalogs(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get full hierarchy of categories
     *
     * @param userId user email
     * @return list of IDs
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<Long> getAssignedManagerCatalogHierarchy(String userId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Update user names by given user id.
     *
     * @param userId     user email
     * @param firstName  first name
     * @param lastName   last name
     * @param company1   company1
     * @param company2   company2
     * @param department department
     */
    void updateUser(String userId,
                    String firstName,
                    String lastName,
                    String company1,
                    String company2,
                    String department);

    /**
     * Update dashboard to given vo.
     *
     * @param email manager email
     * @param dashboardWidgets dashboard
     */
    void updateDashboard(String email, String dashboardWidgets);

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param userId user email
     */
    void resetPassword(String userId);

    /**
     * Reset password to user chosen one.
     *
     * @param userId user email
     * @param password user chosen password
     * @param lang language for errors
     */
    void updatePassword(String userId, String password, String lang) throws BadCredentialsException;

    /**
     * Delete user by given user id.
     * All {@link org.yes.cart.domain.entity.ManagerRole} related to this user will be deleted.
     *
     * @param userId given user id
     */
    void deleteUser(String userId);

    /**
     * Get all roles.
     *
     * @return List of all roles.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of entity to dto mapping errors
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of config errors
     */
    List<RoleDTO> getRolesList() throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Add new role.
     *
     * @param role        role
     * @param description role description
     */
    void addRole(String role, String description);

    /**
     * Update role description.
     *
     * @param role       given role
     * @param description description
     */
    void updateRole(String role, String description);

    /**
     * Delete role. All {@link org.yes.cart.domain.entity.ManagerRole} related to this role will be deleted.
     *
     * @param role role
     */
    void deleteRole(String role);

    /**
     * Grant given role to user
     *
     * @param userId user id
     * @param role   role
     */
    void grantRole(String userId, String role);

    /**
     * Revoke role from user.
     *
     * @param userId user id
     * @param role   role
     */
    void revokeRole(String userId, String role);

    /**
     * Grant given shop to user
     *
     * @param userId user id
     * @param shopCode  shop
     */
    void grantShop(String userId, String shopCode);

    /**
     * Revoke shop from user.
     *
     * @param userId user id
     * @param shopCode  shop
     */
    void revokeShop(String userId, String shopCode);

    /**
     * Grant given supplier code to user
     *
     * @param userId user id
     * @param catalogCode  catalog
     */
    void grantSupplierCatalog(String userId, String catalogCode);

    /**
     * Revoke supplier code from user.
     *
     * @param userId user id
     * @param catalogCode  catalog
     */
    void revokeSupplierCatalog(String userId, String catalogCode);

    /**
     * Grant given catalog to user
     *
     * @param userId user id
     * @param catalogCode  catalog
     */
    void grantCategoryCatalog(String userId, String catalogCode);

    /**
     * Revoke catalog from user.
     *
     * @param userId user id
     * @param catalogCode  catalog
     */
    void revokeCategoryCatalog(String userId, String catalogCode);

    /**
     * Enable user account.
     *
     * @param userId user id
     */
    void enableAccount(String userId);

    /**
     * Disable user account.
     *
     * @param userId user id
     */
    void disableAccount(String userId);


}
