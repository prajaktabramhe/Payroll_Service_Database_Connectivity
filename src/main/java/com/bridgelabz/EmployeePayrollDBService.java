package com.bridgelabz;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService
{
    private static PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private PreparedStatement updateEmployeeSalary;
    private PreparedStatement prepareStatement;
    private PreparedStatement employeeSalary;

    EmployeePayrollDBService()
    {

    }
    public static EmployeePayrollDBService getInstance()
    {
        if (employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException
    {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "Admin@123";
        Connection connection;
        System.out.println("Connecting to database:" + jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection is successful!!!!!!" + connection);
        return connection;
    }

    public List<EmployeePayrollData> readData()
    {
        String sql = "SELECT * FROM  employee_payroll; ";
        return this.getEmployeePayrollDataUsingDB(sql);

    }
    public List<EmployeePayrollData> getEmployeePayrollDataForDateRange(LocalDate startDate, LocalDate endDate)
    {
        String sql = String.format("SELECT * FROM  employee_payroll WHERE START BETWEEN '%s' AND '%s';", Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql)
    {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try (Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    int updateEmployeeData(String name, Double basic_pay)
    {
        return this.updateEmployeeDataUsingStatement(name, basic_pay);
    }

    private int updateEmployeeDataUsingStatement(String name, Double  basic_pay)
    {
        String sql = String.format("update employee_payroll set  basic_pay = %.2f where name = '%s';",  basic_pay, name);
        try (Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    int updateEmployeeDataUsingPreparedStatement(String name, Double basic_pay)
    {
        List<EmployeePayrollData> employeePayrollList = null;
        if (this.updateEmployeeSalary == null)
            this.prepareStatementForToUpdateSalary();
        try
        {
            updateEmployeeSalary.setString(2, name);
            updateEmployeeSalary.setDouble(1, basic_pay);
            return updateEmployeeSalary.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name)
    {
        List<EmployeePayrollData> employeePayrollList = null;
        if (this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try
        {
            employeePayrollDataStatement.setString(1, name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
     
        return employeePayrollList;
    }


    public List<EmployeePayrollData> getSalary(String name, Double basic_pay) {
        List<EmployeePayrollData> employeePayrollList = null;
        String sql = "SELECT * FROM  employee_payroll WHERE name = ? AND  basic_pay = ?";
        if (this.employeeSalary == null)
            employeeSalary = this.prepareStatementForEmployeeData(sql);
        try {
            employeeSalary.setString(1, name);
            employeeSalary.setDouble(2, basic_pay);
            ResultSet resultSet = employeeSalary.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
            return employeePayrollList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private PreparedStatement prepareStatementForEmployeeData(String sql) {
        try {
            Connection connection = this.getConnection();
            prepareStatement = connection.prepareStatement(sql);
            return prepareStatement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet)
    {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try
        {
            while (resultSet.next())
            {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double  basic_pay = resultSet.getDouble("basic_pay");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name,  basic_pay, startDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData()
    {
        try
        {
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM  employee_payroll WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    private void prepareStatementForToUpdateSalary()
    {
        try
        {
            Connection connection = this.getConnection();
            String sql = "update  employee_payroll set  basic_pay = ? where name = ?";
            updateEmployeeSalary = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Map<String, Double> readAverageSalaryByGender()
    {
        String sql = "SELECT gendder, AVG(basic_pay) as basic_pay FROM employee_payroll GROUP BY gendder;";
        Map<String, Double> genderAverageSalaryMap = new HashMap<>();
        try (Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next())
            {
                String gendder = resultSet.getString("gendder");
                double basic_pay = resultSet.getDouble("basic_pay");
                genderAverageSalaryMap.put(gendder, basic_pay);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return genderAverageSalaryMap;
    }

}
