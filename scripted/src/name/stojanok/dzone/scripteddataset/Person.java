package name.stojanok.dzone.scripteddataset;


public class Person {

	private String firstName;
	
	private String lastName;

	private OtherPersonData otherPersonData;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public OtherPersonData getOtherPersonData() {
		return otherPersonData;
	}

	public void setOtherPersonData(OtherPersonData otherPersonData) {
		this.otherPersonData = otherPersonData;
	}
}
