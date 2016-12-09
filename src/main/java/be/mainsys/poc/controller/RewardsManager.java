package be.mainsys.poc.controller;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.mainsys.poc.ejb.RewardsManagerEJB;
import be.mainsys.poc.jbpm.mainsys_jbpm_demo.EmployeeReward;
import be.mainsys.poc.model.Employee;

@ManagedBean
@ViewScoped
public class RewardsManager implements Serializable {
	
	@Inject
	private RewardsManagerEJB rewardsService;

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(RewardsManager.class);
	
    private String result;
    private EmployeeReward employeeReward;
    private Employee selectedEmployee; 
    
    private String user;

	public void init() {
		logger.info("User: " + user);
		employeeReward = new EmployeeReward();
		employeeReward.setRecipient(user);
	}
    
    public void reset(){
    	result = null;
    }

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<Employee> getEmployees(){
		logger.info("Retrieving Employees");
		return rewardsService.listEmployees();	
	}
	
	public List<Employee> getManagers() {
		logger.info("Retrieving Managers");
		return rewardsService.listManagers();
	}
	
	public List<Employee> getManagersForEmployee(Employee employee){
		logger.info("Retrieving Managers for Employee " + employee.getName());
		return rewardsService.listManagersForEmployee(employee);
	}
	
	public List<Employee> getApprovers(){
		logger.info("Retrieving Approvers");
		return rewardsService.listApprovers();
	}
	
	public List<Employee> getApproversByDepartment(String department){
		logger.info("Retrieving Approvers by Department: " + department);
		return rewardsService.listApproversByDepartment(department);
	}
	
	public Employee getEmployeeByName(String user){
		return rewardsService.getEmployeeByName(user);
	}

	public EmployeeReward getEmployeeReward() {
		return employeeReward;
	}

	public void setEmployeeReward(EmployeeReward employeeReward) {
		this.employeeReward = employeeReward;
	}
	
    public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	
	public void startEmployeeRewards() throws RemoteException, JMSException{
		logger.info("Invoking new Employee Rewards for " + employeeReward.getRecipient());
		Employee employee = this.getEmployeeByName(employeeReward.getRecipient());
		result = Long.toString(rewardsService.createRewardsEmployeeProcess(employeeReward, employee));
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
