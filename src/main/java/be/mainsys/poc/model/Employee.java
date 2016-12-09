package be.mainsys.poc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Employee")
public class Employee implements Serializable {
	
	/** Default value included to remove warning. **/
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int id;

	@Column(unique = true, nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String department;
	
	// Could use a ArrayList<String> but its serialized into Byte code 
	// for the demo we'll use a String for readability
	@Column(nullable = false)
	private String groups;
	
	@Column(nullable = false)
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}
	
	public ArrayList<String> getGroupList(){
		if (!this.groups.isEmpty()){
			String[] temp = this.groups.split(";");
			return new ArrayList<String>(Arrays.asList(temp));
		}
		return null;
	}
	
	public Employee(){}
	
	public Employee(String name, String department, String groups, String type) {
		super();
		this.name = name;
		this.department = department;
		this.groups = groups;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", department=" + department + ", groups=" + groups + ", type="
				+ type + "]";
	}


}
