package info.ivgivanov.util;

import java.util.Calendar;

import org.springframework.beans.factory.FactoryBean;

public class CalendarFactory implements FactoryBean<Calendar> {

	private Calendar instance = Calendar.getInstance();
	
	@Override
	public Calendar getObject() throws Exception {
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return Calendar.class;
	}
	
	public void addDays(int num) {
		
		instance.add(Calendar.DAY_OF_YEAR, num);
	}

}
