package com.github.sawied.microservice.sql.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementTest {

	Connection con = null;

	int count = 10000;

	public void preparestatement() throws SQLException {

		PreparedStatement ps = con
				.prepareStatement("INSERT INTO Profile (ID, FirstName, LastName)" + " VALUES (?, ?, ?)");
		java.util.Random r = new java.util.Random();
		for (int i = 0; i < count; i++) {
			ps.setInt(1, i + 1);
			ps.setString(2, Integer.toHexString(r.nextInt(9999)));
			ps.setString(3, Integer.toHexString(r.nextInt(999999)));
			ps.executeUpdate();
		}
		ps.close();

	}
	
	public void statementSql() throws SQLException {

		java.util.Random r = new java.util.Random();
		for (int i = 0; i < count; i++) {
			Statement stat = con.createStatement();
			int p1 = i+1;
			String p2 = Integer.toHexString(r.nextInt(9999));
			String p3 = Integer.toHexString(r.nextInt(999999));
			stat.execute("INSERT INTO Profile (ID, FirstName, LastName) VALUES ("+p1+"," +p2+","+p3+")");
			stat.close();
		}

	}

}
