package br.unifesp.maritaca.persistence.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class extends the Date from java.util just changing
 * the way Dates are presented to user when the toString is
 * called.
 * @author tiagobarabasz
 */
public class MaritacaDate extends Date{

	private static final long serialVersionUID = 1L;

	public MaritacaDate(){
		
	}
	
	public MaritacaDate(Date d){
		this.setTime(d.getTime());
	}
	
	public MaritacaDate(Long timeStamp){
		this.setTime(timeStamp);
	}
	
	@Override
	public String toString(){
		SimpleDateFormat sdf     = new SimpleDateFormat("dd/MM/yy hh:mm a");
		String           dateStr = sdf.format(this);
		return dateStr;
	}

}