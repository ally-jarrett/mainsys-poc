/**
 * Copyright 2015, Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.mainsys.poc.util;

import org.kie.api.task.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.mainsys.poc.model.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public class RewardsUserGroupCallback implements UserGroupCallback {
	
	private static Logger logger = LoggerFactory.getLogger(RewardsUserGroupCallback.class);
	
	List<String> groups = new ArrayList<String>();
	
	public static final String ROLES_QUERY = "select groups from employee where  name= :userId";
	
    @PersistenceContext(unitName = "be.mainsys.ds")
    private EntityManager entityManager;
    
    // Rewards Groups
//    public List<String> getGroups() {
//    	groups.add("PM");
//    	groups.add("HR");
//    	groups.add("Administrators");
//    	groups.add("itGroup");
//    	groups.add("supGroup");
//    	groups.add("salGroup");
//    	groups.add("itGroupManagement");
//    	groups.add("supGroupManagement");
//    	groups.add("salGroupManagement");
//    	groups.add("HR");
//    	groups.add("Finance");
//		return groups;
//	}

	public boolean existsUser(String userId) {
    	logger.info("Determining if User Exists: " + userId);
        return true;
    }

    public boolean existsGroup(String groupId) {
    	logger.info("Determining if Group Exists for : " + groupId);
    	return true;
    }

    public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
    	List<String> roles = new ArrayList<String>();
        logger.info("Obtaining User Groups for : " + userId);
        
        if(userId.equals("manager1")){
        	roles.add("itGroupManagement");
        	roles.add("supGroupManagement");
        	return roles;
        } 
        
        if(userId.equals("manager2")){
        	roles.add("salGroupManagement");
        	return roles;
        } 
        
        
        	// Obtain user groups from DB - Not working atm!
        	TypedQuery<Employee> tq = entityManager.createQuery(ROLES_QUERY, Employee.class);
			Employee result = tq.setParameter("userId", userId).getSingleResult();
			for (String role : result.getGroups().split(",")) {
				roles.add(role);
				logger.info("Adding role: {}",role);
			}	
        
        return roles;
    }
}
