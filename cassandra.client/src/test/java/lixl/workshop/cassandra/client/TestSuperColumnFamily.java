/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import lixl.workshop.cassandra.client.ColumnFamily_A;
import lixl.workshop.cassandra.client.Key_A;
import lixl.workshop.cassandra.client.SuperColumn_A;

import org.junit.Assert;
import org.junit.Test;

public class TestSuperColumnFamily {

	@ColumnFamily_A(name="MySCF", isSuper=true)
	class DepartmentbyLocation {
		@Key_A(name="deptName")
		public String deptName;
		public Department dept;
	}
	
	@SuperColumn_A
	class Department {
		@Key_A(name="location")
		public String location;
		public int employeeNum;
		public String managerName;
	}
	
	@Test
	public void testSuperColumnFamily() throws NoSuchFieldException, SecurityException {
		Department l_dept = new Department();
		l_dept.employeeNum = 50;
		l_dept.managerName = "Cheng Da";
		
		DepartmentbyLocation l_scf = new DepartmentbyLocation();
		l_scf.deptName = "wpds";
		l_scf.dept = l_dept;
		
		
		Assert.assertTrue(l_scf.getClass().isAnnotationPresent(ColumnFamily_A.class));
		ColumnFamily_A l_cfAnno = l_scf.getClass().getAnnotation(ColumnFamily_A.class);
		Assert.assertTrue(l_cfAnno.isSuper());
		
		Assert.assertTrue(l_scf.getClass().getField("deptName").isAnnotationPresent(Key_A.class));
	}
}
