package name.stojanok.dzone.scripteddataset;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonDataSet implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Person> person = new ArrayList<Person>();

	public void setList(ArrayList<Person> dataStructures) {
		person = dataStructures;
	}

	public ArrayList<Person> getList() {
		person.add(createPerson("John", "Doe", 33, true));
		person.add(createPerson("Johnny", "Doe", 13, true));
		person.add(createPerson("Lisa", "Doe", 33, false));
		person.add(createPerson("Jenny", "Doe", 13, false));
		return person;
	}
	
	private Person createPerson(String firstName, String lastName, Integer age, boolean gender) {
		Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		OtherPersonData otherPersonData = new OtherPersonData();
		otherPersonData.setAge(age);
		otherPersonData.setGender(gender);
		person.setOtherPersonData(otherPersonData);
		return person;
	}
}
