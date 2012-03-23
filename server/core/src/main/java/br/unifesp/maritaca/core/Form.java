package br.unifesp.maritaca.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import br.unifesp.maritaca.access.Policy;
import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@XmlRootElement(name = "form")
@Entity
public class Form implements Comparable<Form> {
	@Id
	private UUID key;

	@Column
	private String xml;

	@Column(indexed = true)
	@Minimal
	private User user;

	@Column
	@Minimal
	private String title;
	
	@Column(indexed=true)
	@Minimal
	private String url;
	
	@Column(indexed = true)
	@Minimal
	private Group sharedlist;
	
	@Column
	@Minimal
	private Policy policy = Policy.PRIVATE;

	@XmlElement(name = "id")
	public UUID getKey() {
		return key;
	}
	
	private Date creationDate;
	
	// 0 means order by name; 1 means order by date
	private int flagToOrder;
	
	public void setKey(UUID key) {
		this.key = key;
		if(key!=null){
			long dl = TimeUUIDUtils.getTimeFromUUID(getKey());
			Date date = new Date();
			date.setTime(dl);
			creationDate = date;
		}
	}

	public void setKey(String ks) {
		setKey(UUID.fromString(ks));
		
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@XmlTransient
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUser(String userId) {
		User f = new User();
		f.setKey(userId);
		setUser(f);
	}
	
	public Date getCreationDate(){
		return creationDate;
	}

	@Override
	public String toString() {
		if (getKey() != null) {
			return getKey().toString();
		}
		return super.toString();
	}

	public String getTitle() {
		if (title == null && getKey() != null) {
			return getKey().toString();
		}
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
	
	@Override
	public int hashCode() {
		if(getKey() == null){
			return super.hashCode();
		}
		return getKey().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Form){
			Form f = (Form)obj;
			if(getKey()!=null && f.getKey()!=null){
				return getKey().equals(f.getKey());
			}
		}
		return false;
	}

	@Override
	public int compareTo(Form form) {
		if (getFlagToOrder() == 0) {
			return getTitle().toLowerCase().compareTo(form.getTitle().toLowerCase());
		} else {
			return getCreationDate().compareTo(form.getCreationDate());
		}
	}

	@XmlTransient
	public int getFlagToOrder() {
		return flagToOrder;
	}
	
	public Map<String,Policy> getPolicyItens(){
		Map<String,Policy> policyItens = new HashMap<String,Policy>(); 
		for(Policy p : Policy.values()){
			policyItens.put(p.toString(), p);
		}		
		return policyItens;
	}

	public void setFlagToOrder(int flagToOrder) {
		this.flagToOrder = flagToOrder;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = Policy.getPolicyFromString(policy);
	}
	
	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	public Group getSharedlist() {
		return sharedlist;
	}

	public void setSharedlist(Group sharedlist) {
		this.sharedlist = sharedlist;
	}
	
	public void setSharedlist(String sharedlist){
		Group g = new Group();
		g.setKey(sharedlist);
		setSharedlist(g);
	}
}
