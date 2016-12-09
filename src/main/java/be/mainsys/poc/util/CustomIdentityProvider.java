package be.mainsys.poc.util;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kie.internal.identity.IdentityProvider;

@ApplicationScoped
public class CustomIdentityProvider implements IdentityProvider {
	
	private String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getRoles() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public boolean hasRole(String arg0) {
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

}
