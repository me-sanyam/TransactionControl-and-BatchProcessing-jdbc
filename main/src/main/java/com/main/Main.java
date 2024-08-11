package com.main;

import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class Main {

    private static final String url = "";
    private static final String username = "";
    private static final String password = "";

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stmt = null;
        Scanner sc = null;

        try {
            conn = DriverManager.getConnection(url, username, password);
            conn.setAutoCommit(false);

            sc = new Scanner(System.in);
            String query = "INSERT INTO emp_details (name, salary, age) VALUES (?, ?, ?);";
            stmt = conn.prepareStatement(query);

            while (true) {
                System.out.println("Enter Name of employee: ");
                String name = sc.next();
                System.out.println("Enter Salary of employee: ");
                double salary = sc.nextDouble();
                System.out.println("Enter Age of employee: ");
                int age = sc.nextInt();

                stmt.setString(1, name);
                stmt.setDouble(2, salary);
                stmt.setInt(3, age);

                stmt.addBatch();
                System.out.println("Do you want to add more employees? YES/NO");
                String opt = sc.next();

                if (opt.equalsIgnoreCase("NO")) {
                    break;
                }
            }

            int[] rowsEffected = stmt.executeBatch();
            boolean allInserted = true;
            for (int rows : rowsEffected) {
                if (rows == 0) {
                    allInserted = false;
                    break;
                }
            }

            if (allInserted) {
                conn.commit();
                System.out.println("-------- Transaction committed successfully --------");
            } else {
                conn.rollback();
                System.out.println("-------- Transaction rolled back due to a failure --------");
            }

        } catch (Exception e) {
            System.out.println("Error in connecting to db: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to an error: " + e.getMessage());
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Error during rollback: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                if (sc != null) sc.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error during resource cleanup: " + e.getMessage());
            }
        }
    }
}
