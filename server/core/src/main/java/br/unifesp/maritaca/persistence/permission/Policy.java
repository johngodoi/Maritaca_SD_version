package br.unifesp.maritaca.persistence.permission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum Policy {
	PRIVATE(0),
	SHARED_HIERARCHICAL(1),
	SHARED_SOCIAL(2),
	PUBLIC(3);
	
	private static final Log log = LogFactory.getLog(Policy.class);
	private Integer idPolicy;
	
	private Policy(Integer idPolicy) {
		this.idPolicy = idPolicy;
	}

	public Integer getIdPolicy() {
		return idPolicy;
	}

	public void setIdPolicy(Integer idPolicy) {
		this.idPolicy = idPolicy;
	}
	
	public static String[] valuesLabels(){
		Policy[] policies = Policy.values();
		String[] labels   = new String[policies.length];
		
		for(int i=0; i<policies.length; i++)
			labels[i] = policies[i].toString();
		
		return labels;
	}
	
	public String toString(){
		return this.name().replace("_", " ").toLowerCase();
	}
	
	public static Policy getPolicyFromString(String str){
		for(Policy p : Policy.values()){
			String policyName = p.toString();
			if(policyName.equals(str)){
				return p;
			}
		}
		String error = "Can't convert string: "+str+" to Policy enum";
		log.error(error);
		throw new IllegalArgumentException(error);
	}
}