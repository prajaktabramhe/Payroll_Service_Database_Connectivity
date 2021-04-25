package com.bridgelabz;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EmployeePayrollService
{


    public enum IOService {DB_IO}

    private List<EmployeePayrollData> employeePayrollList;
    private List<EmployeePayrollData> employeePayrollDataList;

    private EmployeePayrollDBService employeePayrollDBService;


    public EmployeePayrollService()
    {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList)
    {
        this();
        this.employeePayrollList = employeePayrollList;
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService)
    {
        if (ioService.equals(IOService.DB_IO))
            this.employeePayrollDataList = new EmployeePayrollDBService().readData();
        return this.employeePayrollDataList;
    }

    public List<EmployeePayrollData> readEmployeePayrollDataForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollDataForDateRange(startDate, endDate);
        return null;
    }


    public boolean checkEmployeePayrollInSyncWithDB(String name)
    {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public void updateEmployeeSalary(String name, Double basic_pay)
    {

        int result = employeePayrollDBService.updateEmployeeData(name, basic_pay);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null)
            EmployeePayrollData.basic_pay = basic_pay;
    }

    public void updateEmployeeSalaryWithPreparedStatement(String name, Double basic_pay)
    {
        int result = employeePayrollDBService.updateEmployeeDataUsingPreparedStatement(name, basic_pay);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null)
            employeePayrollData.basic_pay = basic_pay;
    }

    private EmployeePayrollData getEmployeePayrollData(String name)
    {
        return this.employeePayrollList.stream().filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name)).findFirst().orElse(null);
    }

    public List<EmployeePayrollData> getEmployeeSalary(String name, Double basic_pay)
    {
        List<EmployeePayrollData> employeePayrollData = employeePayrollDBService.getSalary(name, basic_pay);
        return employeePayrollData;
    }

    public List<EmployeePayrollData> readEmployeePayrollDataForDateRange(LocalDate startDate, LocalDate endDate)
    {
        return employeePayrollDBService.getEmployeePayrollDataForDateRange(startDate, endDate);
    }

    //UC11
    public Map<String, Double> averageSalaryByGender()
    {
        return employeePayrollDBService.readAverageSalaryByGender();
    }

    public void addEmployeeAndPayrollData(String name, Double salary, LocalDate startDate, String gender,ArrayList<String> department){
        employeePayrollList.add(
                employeePayrollDBService.addEmployeePayroll(name, salary, startDate, gender, department));
    }
}
