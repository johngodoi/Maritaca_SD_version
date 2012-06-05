package br.unifesp.maritaca.mobile.model.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;

import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.util.ComponentType;
import br.unifesp.maritaca.mobile.util.Constants;
import br.unifesp.maritaca.mobile.util.UtilConverters;
import br.unifesp.maritaca.mobile.util.XMLFormParser;

public class DateQuestion extends Question {

	private String format;
	private Date min;
	private Date max;
	private DatePicker field;

	public DateQuestion(Integer id, Integer previous, 
			Integer next, String help,
			String label, Boolean required, 
			Element element) {
		
		super(id, previous, next, help, label, required, element);
		
		this.format = XMLFormParser.getDateFormat(XMLFormParser.getTagValue(Constants.DATE_FORMAT, element));
		this.min = UtilConverters.convertStringToDate(element.getAttribute(Constants.MIN), format);
		this.max = UtilConverters.convertStringToDate(element.getAttribute(Constants.MAX), format);
		String  attrDefault = XMLFormParser.getTagValue(Constants.DEFAULT, element);

		super.value = UtilConverters.convertStringToDate(attrDefault, format);
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.DATE;
	}

	@Override
	public Integer getNext() {
		if (clauses.length < 1)
			return next;
		for (int i = 0; i < clauses.length; i++) {
			Date value = this.getValue();
			if (clauses[i].evaluate(value)) {
				return clauses[i].getGoToIndex();
			}
		}
		return next;
	}

	@Override
	public Date getValue() {
		return value != null ? (Date)value : new Date();
	}

	public View getLayout(ControllerActivity activity) {
		field = new DatePicker(activity);	
		Calendar cal = Calendar.getInstance();
		cal.setTime(getValue()!=null?getValue():new Date());
		field.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
		return field;
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void save(View answer) {
		String date = field.getYear()+"-"+(field.getMonth()+1)+"-"+field.getDayOfMonth();
		value = UtilConverters.convertStringToDate(date, Constants.DATE_ISO8601FORMAT);
	}
}