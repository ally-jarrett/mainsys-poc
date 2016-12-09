package be.mainsys.poc.controller;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.services.api.model.ProcessInstanceDesc;

import be.mainsys.poc.ejb.CustomerManagerEJB;

@Named("processManager")
@RequestScoped
public class ProcessManager {
	
	// Just Because we currently have methods in this bean...
    @Inject
    private CustomerManagerEJB customerManager;
    
    private List<ProcessInstanceDesc> instances;
    
    // Lazy initialise processInstances as jsf calls multiple times!
    public List<ProcessInstanceDesc> getInstances(){
    	if (instances == null){
    		instances = customerManager.listRunningProcesses();
    	}
    	return instances;
    }

}
