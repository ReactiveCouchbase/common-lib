package org.reactivecouchbase.common.test;

import org.junit.Assert;
import org.junit.Test;
import org.reactivecouchbase.functional.Lens;

public class LensTest {

    public static class Street {
        public final String name;

        public Street(String name) {
            this.name = name;
        }

        public Street withName(String name) {
            return new Street(name);
        }

        @Override
        public String toString() {
            return "Street { " +
                    "name = '" + name + '\'' +
                    " }";
        }
    }

    public static class Address {
        public final Street street;
        public final Integer number;

        public Address(Street street, Integer number) {
            this.street = street;
            this.number = number;
        }

        public Address withStreet(Street s) {
            return new Address(s, number);
        }

        public Address withNumber(Integer n) {
            return new Address(street, n);
        }

        @Override
        public String toString() {
            return "Address { " +
                    "street=" + street +
                    ", number=" + number +
                    " }";
        }
    }

    public static class Company {
        public final String name;
        public final Address address;

        public Company(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public Company withName(String n) {
            return new Company(n, address);
        }

        public Company withAddress(Address a) {
            return new Company(name, a);
        }

        @Override
        public String toString() {
            return "Company { " +
                    "name='" + name + '\'' +
                    ", address=" + address +
                    " }";
        }
    }
    public static class Employee {
        public final String name;
        public final Integer age;
        public final Company company;

        public Employee(String name, Integer age, Company company) {
            this.name = name;
            this.age = age;
            this.company = company;
        }

        public Employee withName(String n) {
            return new Employee(n, age, company);
        }

        public Employee withAge(Integer a) {
            return new Employee(name, a, company);
        }

        public Employee withCompany(Company c) {
            return new Employee(name, age, c);
        }

        @Override
        public String toString() {
            return "Employee { " +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", company=" + company +
                    " }";
        }
    }

    @Test
    public void testLenses() {

        // Basic lenses
        Lens<Street, String> streetNameLens = Lens.of(s -> s.name, Street::withName);

        Lens<Address, Street> addressStreetLens = Lens.of(a -> a.street, Address::withStreet);
        Lens<Address, Integer> addressNumberLens = Lens.of(a -> a.number, Address::withNumber);

        Lens<Company, Address> companyAddressLens = Lens.of(c -> c.address, Company::withAddress);
        Lens<Company, String> companyNameLens = Lens.of(c -> c.name, Company::withName);

        Lens<Employee, Company> employeeCompanyLens = Lens.of(e -> e.company, Employee::withCompany);
        Lens<Employee, Integer> employeeAgeLens = Lens.of(e -> e.age, Employee::withAge);
        Lens<Employee, String> employeeNameLens = Lens.of(e -> e.name, Employee::withName);

        // Lenses composition

        Lens<Employee, String> changeStreetName       = employeeCompanyLens
                .compose(companyAddressLens)
                .compose(addressStreetLens)
                .compose(streetNameLens);
        Lens<Employee, Street> changeAddressStreet    = employeeCompanyLens.compose(companyAddressLens).compose(addressStreetLens);
        Lens<Employee,Integer > changeAddressNumber   = employeeCompanyLens.compose(companyAddressLens).compose(addressNumberLens);
        Lens<Employee, Address> changeCompanyAddress  = employeeCompanyLens.compose(companyAddressLens);
        Lens<Employee, String> changeCompanyName      = employeeCompanyLens.compose(companyNameLens);
        Lens<Employee, Integer> changeEmployeeAge     = employeeAgeLens;
        Lens<Employee, String> changeEmployeeName     = employeeNameLens;
        Lens<Employee, Company> changeEmployeeCompany = employeeCompanyLens;

        // Immutable structure

        Employee employee = new Employee("John Doe", 42, new Company("Unknown Inc.", new Address(new Street("Nowhere Street"), 42)));

        // Mutations through lenses
        employee = changeStreetName.modify(String::toUpperCase).apply(employee);
        Assert.assertEquals("NOWHERE STREET", employee.company.address.street.name);

        employee = changeAddressStreet.modify(ign -> new Street("Baker street")).apply(employee);
        Assert.assertEquals("Baker street", employee.company.address.street.name);

        employee = changeAddressNumber.modify(ign -> 221).apply(employee);
        Assert.assertEquals(Integer.valueOf(221), employee.company.address.number);

        employee = changeCompanyAddress.modify(ign -> new Address(new Street("Elm Street"), 23)).apply(employee);
        Assert.assertEquals("Elm Street", employee.company.address.street.name);
        Assert.assertEquals(Integer.valueOf(23), employee.company.address.number);

        employee = changeCompanyName.modify(ign -> "Dharma Initiative").apply(employee);
        Assert.assertEquals("Dharma Initiative", employee.company.name);

        employee = changeEmployeeAge.modify(ign -> 52).apply(employee);
        Assert.assertEquals(Integer.valueOf(52), employee.age);

        employee = changeEmployeeName.modify(ign -> "Jane Doe").apply(employee);
        Assert.assertEquals("Jane Doe", employee.name);

        employee = changeEmployeeCompany.modify(ign -> new Company("Unknown Inc.", new Address(new Street("Nowhere Street"), 42))).apply(employee);
        Assert.assertEquals("Nowhere Street", employee.company.address.street.name);
        Assert.assertEquals("Unknown Inc.", employee.company.name);
        Assert.assertEquals(Integer.valueOf(42), employee.company.address.number);
        Assert.assertEquals(Integer.valueOf(52), employee.age);
        Assert.assertEquals("Jane Doe", employee.name);
    }
}
