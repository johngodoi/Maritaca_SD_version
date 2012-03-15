package br.unifesp.maritaca.access;

import java.util.ArrayList;
import java.util.List;

import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;

public enum Policy {
	PUBLIC(	new FormPermissions(AccessLevel.FULL_ACCESS,  AccessLevel.FULL_ACCESS),
			new FormPermissions(AccessLevel.READ_AND_LIST,AccessLevel.FULL_ACCESS),
			null),
			
	SHARED_HIERARCHICAL(
			new FormPermissions(AccessLevel.NO_ACCESS,     AccessLevel.NO_ACCESS),
			new FormPermissions(AccessLevel.FULL_ACCESS,   AccessLevel.FULL_ACCESS),
			new FormPermissions(AccessLevel.READ_AND_LIST, AccessLevel.WRITE_ONLY)),
			
	SHARED_SOCIAL(
			new FormPermissions(AccessLevel.NO_ACCESS,     AccessLevel.NO_ACCESS),
			new FormPermissions(AccessLevel.FULL_ACCESS,   AccessLevel.FULL_ACCESS),
			new FormPermissions(AccessLevel.READ_AND_LIST, AccessLevel.FULL_ACCESS)),
			
	PRIVATE(new FormPermissions(AccessLevel.FULL_ACCESS, AccessLevel.FULL_ACCESS),
			new FormPermissions(AccessLevel.NO_ACCESS,   AccessLevel.NO_ACCESS),
			null);
	
	private FormPermissions publicPermissions;
	private FormPermissions ownerPermissions;
	private FormPermissions listPermissions;
	
	Policy(FormPermissions allUsersPermissions, FormPermissions ownerPermissions, FormPermissions listPermissions){
		setAllUsersPermissions(allUsersPermissions);
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
			if(p.name().replace(" ", "_").equals(str)){
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

	public static List<FormPermissions> buildPermissions(Policy policy, Group owner, Group allUsers, Group list) {
		List<FormPermissions> permissions = new ArrayList<FormPermissions>();
		
		FormPermissions fpOwner  = policy.getOwnerPermissions();
		FormPermissions fpAllUsr = policy.getAllUsersPermissions();
		FormPermissions fpList   = policy.getListPermissions();						
		
		if(fpOwner!=null){
			fpOwner.setGroup(owner);
			permissions.add(fpOwner);
		}
		if(fpAllUsr!=null){
			fpAllUsr.setGroup(allUsers);
			permissions.add(fpAllUsr);
		}
		if(fpList!=null){
			fpList.setGroup(list);
			permissions.add(fpList);
		}
		return permissions;
	}
}
