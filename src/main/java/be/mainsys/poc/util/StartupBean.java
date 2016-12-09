package be.mainsys.poc.util;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;

import be.mainsys.poc.model.Employee;

@Singleton
@Startup
@TransactionManagement(TransactionManagementType.CONTAINER)
public class StartupBean {

	//public static final String DEPLOYMENT_ID = "org.jbpm.examples:rewards:1.0";
	public static final String DEPLOYMENT_ID = "be.mainsys.poc.jbpm:mainsys-jbpm-demo:1.0";

	@PersistenceContext(unitName = "be.mainsys.ds")
	private EntityManager entityManager;

	@EJB
	DeploymentServiceEJBLocal deploymentService;

	@PostConstruct
	public void init() {
		System.setProperty("org.jbpm.ht.callback", "custom");
		System.setProperty("org.jbpm.ht.custom.callback", "be.mainsys.poc.util.RewardsUserGroupCallback");
		
		//System.clearProperty("org.jbpm.ht.callback");
		//System.clearProperty("org.jbpm.ht.custom.callback");
		
		// Load KJar Deployment
		String[] gav = DEPLOYMENT_ID.split(":");
		DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(gav[0], gav[1], gav[2]);
		deploymentService.deploy(deploymentUnit);
		
		// Load some users into the DB..
		this.persistUsers();
	}

	// Persist some users for the jBPM Process.
	public void persistUsers() {
		entityManager.persist(new Employee("jiri", "IT", "itGroup", "employee"));
		entityManager.persist(new Employee("simon", "Support", "supGroup", "employee"));
		entityManager.persist(new Employee("tom", "Sales", "salGroup", "employee"));
		entityManager.persist(new Employee("manager1", "Technical Practice", "itGroupManagement,supGroupManagement", "manager"));
		entityManager.persist(new Employee("manager2", "Sales Practice", "salGroupManagement", "manager"));
		entityManager.persist(new Employee("hr", "HR", "HR", "approver"));
		entityManager.persist(new Employee("finance", "Sales Practice", "Finance", "approver"));

		// Quickly check the values are in the DB... should really be in a unit test!!

		try {
			TypedQuery<Employee> tq = entityManager.createQuery("from Employee WHERE name=?", Employee.class);
			Employee result = tq.setParameter(1, "manager1").getSingleResult();
			System.out.println(result.toString());

			// List to Query Against
			TypedQuery<Employee> tq1 = entityManager.createQuery("from Employee WHERE type=?", Employee.class);
			List<Employee> employees = tq1.setParameter(1, "employee").getResultList();
			for (Employee emp : employees) {
				System.out.println(emp.toString());
			}
			
	    	String groupCriteria = "itGroupManagement";
			TypedQuery<Employee> tq2 = entityManager.createQuery("from Employee WHERE groups LIKE CONCAT('%', :criteria, '%')", Employee.class);
			employees = tq2.setParameter("criteria", groupCriteria).getResultList();		
			for (Employee emp : employees) {
				System.out.println("Manager: " + emp.toString());
			}

		} catch (NoResultException noresult) {
			// if there is no result
		} catch (NonUniqueResultException notUnique) {
			// if more than one result
		}

	}

}
