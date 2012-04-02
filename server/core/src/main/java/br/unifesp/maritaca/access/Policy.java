package br.unifesp.maritaca.access;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;

public enum Policy {
	PUBLIC(	new FormPermissions(AccessLevel.READ_AND_LIST,   AccessLevel.FULL_ACCESS),
			new FormPermissions(AccessLevel.FULL_ACCESS,     AccessLevel.FULL_ACCESS),
			null),
			
	SHARED_HIERARCHICAL(
			new FormPermissions(AccessLevel.NO_ACCESS,     AccessLevel.NO_ACCESS),
			new FormPermissions(AccessLevel.FULL_ACCESS,   AccessLevel.FULL_ACCESS),
			new FormPermissions(AccessLevel.READ_AND_LIST, AccessLevel.CREATE_ONLY)),
			
	SHARED_SOCIAL(
			new FormPermissions(AccessLevel.NO_ACCESS,     AccessLevel.NO_ACCESS),
			new FormPermissions(AccessLevel.FULL_ACCESS,   AccessLevel.FULL_ACCESS),
			new FormPermissions(AccessLevel.READ_AND_LIST, AccessLevel.FULL_ACCESS)),
			
	PRIVATE(new FormPermissions(AccessLevel.NO_ACCESS,     AccessLevel.NO_ACCESS),
			new FormPermissions(AccessLevel.FULL_ACCESS,   AccessLevel.FULL_ACCESS),
			null);
	
	private FormPermissions publicPermissions;
	private FormPermissions ownerPermissions;
	private FormPermissions listPermissions;
	
	Policy(FormPermissions publicPermissions, FormPermissions ownerPermissions, FormPermissions listPermissions){
		setAllUsersPermissions(publicPermissions);
		setOwnerPermissions(ownerPermissions);
		setListPermissions(listPermissions);
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
			String policyName = p.name().replace(" ", "_").toLowerCase();
			if(policyName.equals(str)){
				return p;
			}
		}		
		return null;
	}

	public FormPermissions getAllUsersPermissions() {
		return publicPermissions;
	}

	public void setAllUsersPermissions(FormPermissions publicPermissions) {
		this.publicPermissions = publicPermissions;
	}

	public FormPermissions getOwnerPermissions() {
		return ownerPermissions;
	}

	public void setOwnerPermissions(FormPermissions ownerPermissions) {
		this.ownerPermissions = ownerPermissions;
	}

	public FormPermissions getListPermissions() {
		return listPermissions;
	}

	public void setListPermissions(FormPermissions listPermissions) {
		this.listPermissions = listPermissions;
	}
	
	public boolean isUseList(){
		if(getListPermissions()==null){
			return false;
		}else{
			return true;
		}
	}
	
	public FormPermissions buildPublicFormPermission(Form form, MaritacaList list ){
		return buildFormPermission(form, list, getAllUsersPermissions());		
	}
	
	public FormPermissions buildListFormPermission(Form form, MaritacaList list ){
		return buildFormPermission(form, list, getListPermissions());
	}
	
	public FormPermissions buildOwnerFormPermission(Form form, MaritacaList list ){
		return buildFormPermission(form, list, getOwnerPermissions());
	}
	
	private FormPermissions buildFormPermission(Form form, MaritacaList list, FormPermissions fp ){
		AccessLevel answAccessLv = fp.getAnswAccess();
		AccessLevel formAccessLv = fp.getFormAccess();
		
		return new FormPermissions(form,list,formAccessLv,answAccessLv);		
	}
}
