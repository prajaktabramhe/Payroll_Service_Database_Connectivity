package com.bridgelabz;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService
{

    public enum IOService {DB_IO, REST_IO}


    private List<EmployeePayrollData> employeePayrollList;

    private  final EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService()
    {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList)
    {
        this();
        this.employeePayrollList = new ArrayList<>(employeePayrollList);
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService)
    {
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollList = new EmployeePayrollDBService().readData();
        return this.employeePayrollList;
    }

    public List<EmployeePayrollData> readEmployeePayrollDataForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate)
    {
        if (ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return this.employeePayrollList;
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
    public void updateEmployeeSalaryWithPreparedStatement(String name, Double basic_pay) {
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

    public Map<String, Double> averageSalaryByGender()
    {
        return employeePayrollDBService.readAverageSalaryByGender();
    }
    public void addEmployeeToPayroll(String name, double basic_pay, LocalDate start, String gendder)
    {
        employeePayrollList.add(employeePayrollDBService.addEmployeePayroll(name, basic_pay, start, gendder));
    }

    public void removeEmployee(int empId)
    {
        employeePayrollDBService.removeEmployeeFromDB(empId);
    }


    public void addEmployeeAndPayroll(List<EmployeePayrollData> employeePayrollDataList)
    {
        employeePayrollDataList.forEach(employeePayrollData -> {
            System.out.println("Employee Beingg Added: " + employeePayrollData.name);
            this.addEmployeeAndPayrollData(employeePayrollData.name, employeePayrollData.basic_pay,
                    employeePayrollData.startDate, employeePayrollData.gender);
            System.out.println("Employee Added: "+ employeePayrollData.name);
        });
        System.out.println(this.employeePayrollList);
    }

    private void addEmployeeAndPayrollData(String name, double basic_pay, LocalDate startDate, String gender)
    {
        employeePayrollList.add(employeePayrollDBService.addEmployeePayrollIntoDB(name, basic_pay, startDate, gender));
    }

    public void addEmployeeAndPayrollDataWithThread(List<EmployeePayrollData> employeePayrollDataList)
    {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
                System.out.println("Employee Being Added: " + Thread.currentThread().getName());
                this.addEmployeeAndPayrollData(employeePayrollData.name, employeePayrollData.basic_pay, employeePayrollData.startDate, employeePayrollData.gender);
                employeeAdditionStatus.put(employeeAdditionStatus.hashCode(), true);
                System.out.println("Employee Added: " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.setPriority(10);
            thread.start();
        });
        while (employeeAdditionStatus.containsValue(false)) {
            try {
                Thread.sleep(500000);
            } catch (InterruptedException e) {
            }
        }
        System.out.println(employeePayrollDataList);
    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.DB_IO) || ioService.equals(IOService.REST_IO))
            return employeePayrollList.size();
        return 0;
    }
}