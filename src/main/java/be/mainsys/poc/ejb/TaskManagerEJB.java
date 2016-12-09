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
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jbpm.services.api.TaskNotFoundException;
import org.jbpm.services.ejb.TaskServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.api.UserTaskServiceEJBLocal;
import org.jbpm.services.task.commands.CompleteTaskCommand;
import org.jbpm.services.task.commands.CompositeCommand;
import org.jbpm.services.task.commands.StartTaskCommand;
import org.kie.api.task.model.TaskSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.mainsys.poc.model.Employee;
import be.mainsys.poc.util.RewardsUserGroupCallback;
import be.mainsys.poc.util.StartupBean;

@Stateless
public class TaskManagerEJB {

	private static Logger logger = LoggerFactory.getLogger(TaskManagerEJB.class);

	@PersistenceContext(unitName = "be.mainsys.ds")
	private EntityManager entityManager;

	@Inject
	RewardsUserGroupCallback rugc;

	@EJB
	private UserTaskServiceEJBLocal userTaskService;

	@EJB
	private RuntimeDataServiceEJBLocal runtimeDataService;

	@EJB
	private TaskServiceEJBLocal taskService;

	private List<TaskSummary> userTasks;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void claimTask(Long taskId, String userId){
		try {
			logger.info("Attempting to claim task with id {} for user {}", taskId, userId);
			userTaskService.claim(taskId, userId);
		} catch (TaskNotFoundException e){
			logger.error("Failed to claim task with error: {}", e.getMessage());
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void releaseTask(Long taskId, String userId){
		try {
			logger.info("Attempting to release task with id {} for user {}", taskId, userId);
			userTaskService.release(taskId, userId);
		} catch (TaskNotFoundException e){
			logger.error("Failed to release task with error: {}", e.getMessage());
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void getTask(Long taskId){
		try {
			logger.info("Attempting to retrieve task with id {}", taskId);
			userTaskService.getTask(taskId);
		} catch (TaskNotFoundException e){
			logger.error("Failed to retrieve task with error: {}", e.getMessage());
		}
	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<TaskSummary> getUserTasks(String userId) {
		logger.info("Obtaining Active User Tasks for " + userId);

		Employee result = null;
		try {
			TypedQuery<Employee> tq = entityManager.createQuery("from Employee WHERE name=?", Employee.class);
			result = tq.setParameter(1, userId).getSingleResult();
		} catch (NoResultException nre) {
			logger.info("Employee not found!");
		}

		if (result == null) {
			logger.info(userId + " not found in employee table.. can only search for tasks assigned by userId	");
			userTasks = runtimeDataService.getTasksAssignedAsPotentialOwner(userId, null);
		} else {
			logger.info(userId + " found in employee table with associated groups..");
			for (String str : result.getGroups().split(",")) {
				logger.info(str);
			}
			userTasks = runtimeDataService.getTasksAssignedAsPotentialOwner(userId,
					Arrays.asList(result.getGroups().split(",")), null);
		}

		if (userTasks != null) {
			logger.info("User Tasks retrieved: " + userTasks.size());
		} else {
			logger.info("User Tasks Empty");
		}
		return userTasks;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void completeTask(String task, String userId) throws RemoteException, JMSException, EJBException {
		long taskId = Long.parseLong(task);
		try {
			@SuppressWarnings("rawtypes")
			CompositeCommand compositeCommand = new CompositeCommand(new CompleteTaskCommand(taskId, userId, null),
					new StartTaskCommand(taskId, userId));

			userTaskService.execute(StartupBean.DEPLOYMENT_ID, compositeCommand);
			logger.info("Task Completed...");
		} catch (Exception e) {
			logger.warn("Task operation failed. With Exception error: " + e.getMessage());
		}

		if (taskId == 3) {
			throw new EJBException("Error thrown to test Task TX Boundary");
		}
	}

}
