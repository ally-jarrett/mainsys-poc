package be.mainsys.poc.controller;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.mainsys.poc.ejb.TaskManagerEJB;

@ManagedBean
@ViewScoped
public class Task implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(Task.class);
    
	@Inject
    private TaskManagerEJB taskManager;
	
    private String user;
    private List<TaskSummary> userTasks;
    
	public void init() {
		logger.info("User: " + user);
	    setUserTasks(this.retrieveUserTasks());
	}
	
	public void completeTask(String taskId) throws SecurityException, IllegalStateException, NamingException,
    	NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException, 
    	RemoteException, EJBException, JMSException {
		
		logger.info("Performing task for: " + this.getUser() + " : " + taskId );
		taskManager.completeTask(taskId, this.getUser());
	}
	
	public void claimTask(Long taskId, String userId){
		taskManager.claimTask(taskId, userId);
	}
	
	public void releaseTask(Long taskId, String userId){
		taskManager.releaseTask(taskId, userId);
	}
    
	public List<TaskSummary> retrieveUserTasks() {
		return taskManager.getUserTasks(user);
	}
    
    public String getUser() {
		return user;
	}

    public void setUser(String user) {
        this.user = user;
    }

	public List<TaskSummary> getUserTasks() {
		return userTasks;
	}

	public void setUserTasks(List<TaskSummary> userTasks) {
		this.userTasks = userTasks;
	}

}
