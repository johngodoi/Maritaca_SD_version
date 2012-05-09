package br.unifesp.maritaca.business.test.permission;

import static br.unifesp.maritaca.persistence.permission.Accessor.*;
import static br.unifesp.maritaca.persistence.permission.Document.*;
import static br.unifesp.maritaca.persistence.permission.Policy.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import br.unifesp.maritaca.persistence.permission.Accessor;
import br.unifesp.maritaca.persistence.permission.Document;
import br.unifesp.maritaca.persistence.permission.Permission;
import br.unifesp.maritaca.persistence.permission.Policy;
import br.unifesp.maritaca.persistence.permission.Rule;

public class PermissionTest {

	private Rule rules = Rule.getInstance();
	
	@DataProvider(name="rules")
	public Object[][] dataProviderTest() {
		return new Object[][] {
				
				{PRIVATE, FORM, OWNER }, 				{PRIVATE, FORM, LIST }, 				{PRIVATE, FORM, ALL },
				{SHARED_HIERARCHICAL, FORM, OWNER }, 	{SHARED_HIERARCHICAL, FORM, LIST }, 	{SHARED_HIERARCHICAL, FORM, ALL },
				{SHARED_SOCIAL, FORM, OWNER }, 			{SHARED_SOCIAL, FORM, LIST }, 			{SHARED_SOCIAL, FORM, ALL },
				{PUBLIC, FORM, OWNER }, 				{PUBLIC, FORM, LIST }, 					{PUBLIC, FORM, ALL },
				
				{PRIVATE, ANSWER, OWNER }, 				{PRIVATE, ANSWER, LIST }, 				{PRIVATE, ANSWER, ALL },
				{SHARED_HIERARCHICAL, ANSWER, OWNER }, 	{SHARED_HIERARCHICAL, ANSWER, LIST }, 	{SHARED_HIERARCHICAL, ANSWER, ALL },
				{SHARED_SOCIAL, ANSWER, OWNER }, 		{SHARED_SOCIAL, ANSWER, LIST }, 		{SHARED_SOCIAL, ANSWER, ALL },
				{PUBLIC, ANSWER, OWNER }, 				{PUBLIC, ANSWER, LIST }				
		};
	}
	
	@Test(dataProvider="rules")
	public void checkRules(Policy policy, Document document, Accessor accessor) {
		Permission permission = rules.getPermission(policy, document, accessor);
		if(accessor == OWNER) {
			Assert.assertTrue(permission.getRead(),   "Owner should read");
			Assert.assertTrue(permission.getUpdate(), "Owner should update");
			Assert.assertTrue(permission.getDelete(), "Owner should delete");
			Assert.assertTrue(permission.getShare(),  "Owner should share");
		}
		else if(accessor == LIST) {
			if(document == FORM) {
				if(policy == PRIVATE) {
					Assert.assertFalse(permission.getRead(),   "List should not read");
					Assert.assertFalse(permission.getUpdate(), "List should not update");
					Assert.assertFalse(permission.getDelete(), "List should not delete");
					Assert.assertFalse(permission.getShare(),  "List should not share");
				}
				else if(policy == SHARED_HIERARCHICAL || policy == SHARED_SOCIAL) {
					Assert.assertTrue (permission.getRead(),   "List should read");
					Assert.assertFalse(permission.getUpdate(), "List should not update");
					Assert.assertFalse(permission.getDelete(), "List should not delete");
					Assert.assertFalse(permission.getShare(),  "List should not share");
				}				
				else if(policy == PUBLIC) {//?
					Assert.assertTrue(permission.getRead(),    "List should read");
					Assert.assertFalse(permission.getUpdate(), "List should not update");
					Assert.assertFalse(permission.getDelete(), "List should not delete");
					Assert.assertFalse(permission.getShare(),  "List should not share");
				}
			}
			else if(document == ANSWER) {
				if(policy == PRIVATE) {
					Assert.assertFalse(permission.getRead(),   "List should not read");
					Assert.assertFalse(permission.getUpdate(), "List should not update");
					Assert.assertFalse(permission.getDelete(), "List should not delete");
					Assert.assertFalse(permission.getShare(),  "List should not share");
				}
				else if(policy == SHARED_HIERARCHICAL) {
					Assert.assertFalse(permission.getRead(),  "List should not read");//
					Assert.assertTrue(permission.getUpdate(), "List should update");
					Assert.assertTrue(permission.getDelete(), "List should delete");
					Assert.assertTrue(permission.getShare(),  "List should share");
				}
				else if(policy == SHARED_SOCIAL) {
					Assert.assertTrue(permission.getRead(),   "List should read");
					Assert.assertTrue(permission.getUpdate(), "List should update");
					Assert.assertTrue(permission.getDelete(), "List should delete");
					Assert.assertTrue(permission.getShare(),  "List should share");
				}
				else if(policy == PUBLIC) {//?
					Assert.assertTrue(permission.getRead(),   "List should read");
					Assert.assertTrue(permission.getUpdate(), "List should update");
					Assert.assertTrue(permission.getDelete(), "List should delete");
					Assert.assertTrue(permission.getShare(),  "List should share");
				}
			}
		}
		else if(accessor == ALL) {
			if(document == FORM) {
				if(policy == PRIVATE || policy == SHARED_HIERARCHICAL || policy == SHARED_SOCIAL) {
					Assert.assertFalse(permission.getRead(),   "Others should not read");
					Assert.assertFalse(permission.getUpdate(), "Others should not update");
					Assert.assertFalse(permission.getDelete(), "Others should not delete");
					Assert.assertFalse(permission.getShare(),  "Others should not share");
				}
				else if(policy == PUBLIC) {
					Assert.assertTrue (permission.getRead(),   "Others should read");
					Assert.assertFalse(permission.getUpdate(), "Others should not update");
					Assert.assertFalse(permission.getDelete(), "Others should not delete");
					Assert.assertFalse(permission.getShare(),  "Others should not share");
				}
			}
			else if(document == ANSWER) {
				if(policy == PRIVATE || policy == SHARED_HIERARCHICAL || policy == SHARED_SOCIAL) {
					Assert.assertFalse(permission.getRead(),   "Others should not read");
					Assert.assertFalse(permission.getUpdate(), "Others should not update");
					Assert.assertFalse(permission.getDelete(), "Others should not delete");
					Assert.assertFalse(permission.getShare(),  "Others should not share");
				}
				else if(policy == PUBLIC) {
					Assert.assertTrue(permission.getRead(),   "Others should read");
					Assert.assertTrue(permission.getUpdate(), "Others should update");
					Assert.assertTrue(permission.getDelete(), "Others should delete");
					Assert.assertTrue(permission.getShare(),  "Others should share");
				}
			}
		}
	}
}