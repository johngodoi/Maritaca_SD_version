package br.unifesp.maritaca.core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MaritacaDate extends Date{

	private static final long serialVersionUID = 1L;

	public MaritacaDate(){
		
	}
	
	public MaritacaDate(Date d){
		this.setTime(d.getTime());
	}
	
	@Override
	public String toString(){
		SimpleDateFormat sdf     = new SimpleDateFormat("dd/mm/yy hh:mm a");
		String           dateStr = sdf.format(this);
		return dateStr;
	}

}
