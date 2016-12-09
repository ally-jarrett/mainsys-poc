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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.kie.internal.query.QueryContext;

import be.mainsys.poc.model.Customer;
import be.mainsys.poc.model.Employee;
import be.mainsys.poc.util.CustomIdentityProvider;
import be.mainsys.poc.util.StartupBean;

@Stateless
public class CustomerManagerEJB {

    @PersistenceContext(unitName = "be.mainsys.ds")
    private EntityManager entityManager;

    @Inject
    private LogMessageManagerEJB logMessageManager;
    
    @EJB
    private ProcessServiceEJBLocal processService;
    
    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;
    
//    @Inject
//    private CustomIdentityProvider identity;
    
    private ArrayList<ProcessInstanceDesc> processInstances;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createCustomer(String name) throws RemoteException, JMSException {
        logMessageManager.logCreateCustomer(name);

        Customer customer = new Customer();
        customer.setName(name);
        entityManager.persist(customer);

        long processInstanceId = -1;        
        
        try {
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("recipient", "jiri");
	        params.put("initiator", "jiri");
	        processInstanceId = processService.startProcess(StartupBean.DEPLOYMENT_ID, "be.mainsys.poc.jbpm.mainsys-jbpm-demo.rewards", params);
	        System.out.println("Process instance " + processInstanceId + " has been successfully started.");
	    } catch (Exception e) {
	        System.out.println("Unable to start the business process.");
	    }
        
        QueryContext queryContext = new QueryContext();
    	queryContext.setCount(10);
    	processInstances = new ArrayList<ProcessInstanceDesc>(runtimeDataService.getProcessInstances(queryContext));
    	for (ProcessInstanceDesc pi : processInstances){
    		System.out.println(pi.toString());
    	}

        if (!nameIsValid(name)|| name.equals("error")) {
            throw new EJBException("Error thrown - Invalid name: customer names should only contain letters & '-'");
        }
    }

    static boolean nameIsValid(String name) {
        return name.matches("[\\p{L}-]+");
    }

    /**
     * List all the customers.
     * 
     * @return
     * @throws NamingException
     * @throws NotSupportedException
     * @throws SystemException
     * @throws SecurityException
     * @throws IllegalStateException
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    @SuppressWarnings("unchecked")
    public List<Customer> listCustomers() {
        return entityManager.createQuery("select c from Customer c").getResultList();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ArrayList<ProcessInstanceDesc> listRunningProcesses(){
        try {
        	QueryContext queryContext = new QueryContext();
        	queryContext.setCount(10);
        	processInstances = new ArrayList<ProcessInstanceDesc>(runtimeDataService.getProcessInstances(queryContext));

        	System.out.println("Retrieved " + processInstances.size() + " processes");

        } catch (Exception e) {
        	System.out.println("Cannot retrieve processes.");

        }
        return processInstances;
    }
}
