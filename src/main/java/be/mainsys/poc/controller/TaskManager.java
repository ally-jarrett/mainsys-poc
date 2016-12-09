package be.mainsys.poc.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.api.task.model.TaskSummary;

import be.mainsys.poc.ejb.TaskManagerEJB;

@ManagedBean
@Named("taskManager")
@RequestScoped
public class TaskManager implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{param.user}")
	private String userName;

    @Inject
    private TaskManagerEJB taskManager;
   
    private List<TaskSummary> userTasks;
    
	public List<TaskSummary> getUserTasks() {
		if (userTasks == null){
			userTasks = taskManager.getUserTasks("jiri");
		}
		return userTasks;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
