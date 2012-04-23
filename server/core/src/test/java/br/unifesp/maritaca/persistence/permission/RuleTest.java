package br.unifesp.maritaca.persistence.permission;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RuleTest {
	
	private Rule rules; 

	@BeforeClass
	public void load() {
		rules = Rule.getInstance();
	}
	
	@Test
	public void ruleTest() {
		Assert.assertEquals(new Permission(true, true, true, true).toString(), rules.getPermission(Policy.PRIVATE, Document.FORM, Accessor.OWNER).toString(), "This rule is not correct!");
	}	
}