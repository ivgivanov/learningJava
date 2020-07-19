package info.ivgivanov.conference.model;

import org.hibernate.validator.constraints.NotEmpty;

public class Registration {
	
	@NotEmpty
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
