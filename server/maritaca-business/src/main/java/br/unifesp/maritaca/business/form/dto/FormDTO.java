package br.unifesp.maritaca.business.form.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.unifesp.maritaca.persistence.permission.Permission;
import br.unifesp.maritaca.persistence.permission.Policy;
import br.unifesp.maritaca.business.base.dto.BaseDTO;

public class FormDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	private UUID key;
	
	private String xml;
	
	private String title;
	
	private String url;
	
	private Policy policy = Policy.PRIVATE;
	
	private String owner; //email
	
	private String creationDate;
	
	private UUID userKey;
	
	private List<UUID> lists;
	
	private Permission permission;
	
	public FormDTO() { }
	
	public FormDTO(UUID key, String title, String owner, String url, String xml,
			String creationDate, Policy policy, Permission permission) {
		super();
		this.key = key;
		this.title = title;
		this.owner = owner;
		this.url = url;
		this.xml = xml;
		this.creationDate = creationDate;
		this.policy = policy;
		this.permission = permission;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public UUID getUserKey() {
		return userKey;
	}
	
	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	public void setUserKey(UUID userKey) {
		this.userKey = userKey;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public Map<String, Policy> getPolicyItems(){
		Map<String, Policy> policyItems = new LinkedHashMap<String, Policy>(); 
		for(Policy p : Policy.values()){
			policyItems.put(p.toString(), p);
		}
		return policyItems;
	}

	public List<UUID> getLists() {
		return lists;
	}

	public void setLists(List<UUID> lists) {
		this.lists = lists;
	}	
}