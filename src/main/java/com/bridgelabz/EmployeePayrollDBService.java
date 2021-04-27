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
        String sql = "SELECT * FROM  employee_payrol WHERE name = ? AND  basic_pay = ?";
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
            String sql = "SELECT * FROM  employee_payrol WHERE name = ?";
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
        String sql = "SELECT gender, AVG(basic_pay) as basic_pay FROM employee_payrol GROUP BY gender;";
        Map<String, Double> genderAverageSalaryMap = new HashMap<>();
        try (Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next())
            {
                String gendder = resultSet.getString("gender");
                double basic_pay = resultSet.getDouble("basic_pay");
                genderAverageSalaryMap.put(gendder, basic_pay);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return genderAverageSalaryMap;
    }
    public EmployeePayrollData addEmployeePayrollUC7(String name, double basic_pay, LocalDate start, String gendder)
    {
        int employeeID = -1;
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format("INSERT INTO employee_payrol (name, gender, basic_pay, start)" +
                "VALUES ('%s', '%s', '%s', '%s')", name, gendder, basic_pay, Date.valueOf(start));
        try (Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            int rowAffected  = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeID = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeID, name, basic_pay, start);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return employeePayrollData;
    }
    public EmployeePayrollData addEmployeePayroll(String name, double basic_pay, LocalDate start, String gender)
    {
        int employeeID = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement())
        {
            String sql = String.format("INSERT INTO employee_payrol (name, gender, basic_pay, start)" +
                    "VALUES ('%s', '%s', '%s', '%s')", name, gender, basic_pay, Date.valueOf(start));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1)
            {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeID = resultSet.getInt(1);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try (Statement statement = connection.createStatement())
        {
            double deductions = basic_pay * 0.2;
            double taxablePay = basic_pay - deductions;
            double tax = taxablePay * 0.1;
            double netPay = basic_pay - tax;
            String sql = String.format("INSERT INTO payroll_detils" +
                    "(employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES" +
                    "(%s, %s, %s, %s, %s, %s)", employeeID, basic_pay, deductions, taxablePay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1)
            {
                employeePayrollData = new EmployeePayrollData(employeeID, name, basic_pay, start);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException ex)
            {
                ex.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally {
            if (connection != null)
            {
                try {
                    connection.close();
                } catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                }
            }
        }
        return employeePayrollData;
    }

    public void removeEmployeeFromDB(int empId) {
        String sql = String.format("UPDATE employee_payrollSET is_active = false WHERE id= '%s'", empId);
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public EmployeePayrollData addEmployeePayrollIntoDB(String name, double salary, LocalDate startDate, String gender) {
        int employeeID = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO employee_payrol (name, gender, salary, start)" +
                    "VALUES ('%s', '%s', '%s', '%s')", name, gender, salary, Date.valueOf(startDate));
            int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try (Statement statement = connection.createStatement()) {
            double deduction = salary * 0.2;
            double taxablePay = salary - deduction;
            double tax = taxablePay * 0.1;
            double netPay = salary - tax;
            String sql ="INSERT INTO payroll_detils" +
                    "(employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES" +
                    "(?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, employeeID);
            ps.setDouble(2, salary);
            ps.setDouble(3, deduction);
            ps.setDouble(4, taxablePay);
            ps.setDouble(5, tax);
            ps.setDouble(6, netPay);
            int rowAffected = ps.executeUpdate();
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(employeeID, name, salary, startDate);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exce) {
                    exce.printStackTrace();
                }
            }

        }
        return employeePayrollData;
    }
}

