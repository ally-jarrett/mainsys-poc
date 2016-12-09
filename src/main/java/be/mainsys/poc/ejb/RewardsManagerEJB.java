/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.mainsys.poc.ejb;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jbpm.services.ejb.TaskServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.mainsys.poc.jbpm.mainsys_jbpm_demo.EmployeeReward;
import be.mainsys.poc.model.Employee;
import be.mainsys.poc.util.StartupBean;

@Stateless
public class RewardsManagerEJB {

	@PersistenceContext(unitName = "be.mainsys.ds")
	private EntityManager entityManager;
	
	private static Logger logger = LoggerFactory.getLogger(RewardsManagerEJB.class);
	
//    @Inject
//    private CustomIdentityProvider identity;
    
    @EJB
    private ProcessServiceEJBLocal processService;
    
    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Employee> listAllEmployees() {
		return entityManager.createQuery("select * from Employee e").getResultList();
	}
	
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Employee getEmployeeByName(String name) {
		Employee emp = null; 
		try {
			TypedQuery<Employee> tq = entityManager.createQuery("from Employee WHERE name= :name", Employee.class);
			emp = tq.setParameter("name", name).getSingleResult();
		} catch (NoResultException nre){
			logger.error("No Employee found form name: " + name);
		}
		return emp;
	}

	// Returns all Employees with type 'employee'
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Employee> listEmployees() {
		TypedQuery<Employee> tq = entityManager.createQuery("from Employee WHERE type= :employee", Employee.class);
		return tq.setParameter("employee", "employee").getResultList();
	}

	// Returns all Employees with type 'manager'
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Employee> listManagers() {
		TypedQuery<Employee> tq = entityManager.createQuery("from Employee WHERE type= :manager", Employee.class);
		return tq.setParameter("manager", "manager").getResultList();
	}

	// Returns all Managers for Employee
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Employee> listManagersForEmployee(Employee empl) {
		// i.e. itGroup < itGroupManagement
		String groupCriteria = empl.getGroups() + "Management";
		TypedQuery<Employee> tq = entityManager.createQuery("from Employee WHERE groups LIKE CONCAT('%', :criteria, '%')", Employee.class);
		return tq.setParameter("criteria", groupCriteria).getResultList();
	}

	// Should return HR & Finance Users
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Employee> listApprovers() {
		TypedQuery<Employee> tq = entityManager.createQuery("from Employee WHERE type= :approver", Employee.class);
		return tq.setParameter("approver", "approver").getResultList();
	}

	// Should return HR Users or Finance Users
	// SELECT * FROM EMPLOYEE E WHERE E.DEPARTMENT='HR' AND E.TYPE='approver';
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Employee> listApproversByDepartment(String department) {
		TypedQuery<Employee> tq = entityManager
				.createQuery("from Employee WHERE department= :departmentId AND type= :typeId", Employee.class);
		tq.setParameter("departmentId", department);
		return tq.setParameter("typeId", "approver").getResultList();
	}
	
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long createRewardsEmployeeProcess(EmployeeReward employeeReward, Employee employee) throws RemoteException, JMSException {
        long processInstanceId = -1;        
        
        // Since there is no 'logged in' user we'll use the rewards recipient as 
        // the initiator/owner of the process
        String[] groups = employee.getGroups().split(",");
        
        if (groups.length > 0){
        	logger.info("Assigning to Management Group: " + groups[0] + "Management");
        }
        
        try {
	        Map<String, Object> params = new HashMap<String, Object>();
	        
	        // Sets the users Management Group to approve :: 'itGroup' + 'Management'
	        // Its pretty bad but hey its a demo ;)
	        params.put("managerApprovalGroup", groups[0] + "Management");
	        params.put("employeeReward", employeeReward);
	        params.put("initiator", employee.getName());
	        
	        processInstanceId = processService.startProcess(StartupBean.DEPLOYMENT_ID, "be.mainsys.poc.jbpm.mainsys-jbpm-demo.reward-employee", params);
	        logger.info("Process instance " + processInstanceId + " has been successfully started.");
	        
	    } catch (Exception e) {
	    	logger.warn("Unable to start the business process.");
	        throw new EJBException("Error thrown - Unable to start the business process.");
	    }
        return processInstanceId;
        
    }

}
