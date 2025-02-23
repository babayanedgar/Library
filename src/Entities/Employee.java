/**
 * Copyright (c) 2025. Author - Edgar Babayan
 *
 */

package Entities;

/**
 * Represents an employee entity in the library system.
 * This class extends the `Person` class and includes additional properties like position, username, and password.
 *
 */
public class Employee extends Person {
    protected String position; // The position of the employee (e.g., CEO, Employee)
    protected String password; // The password for the employee's account
    protected String username; // The username for the employee's account

    /**
     *  Default constructor
     *
     */
    public Employee() {
        super();
    }

    /** Parameterized constructor to initialize an employee with all details
     *
     * @param name
     * @param lastName
     * @param username
     * @param email
     * @param position
     * @param gender
     * @param password
     */
    public Employee(String name, String lastName, String username, String email, String position, String gender, String password) {
        super(name, lastName, email, gender); // Calls the superclass constructor
        this.position = position;
        this.password = password;
        this.username = username;
    }

    /** Parameterized constructor to initialize an employee with name, lastname, and username
     *
     * @param name
     * @param lastname
     * @param username
     */
    public Employee(String name, String lastname, String username) {
        super(name, lastname); // Calls the superclass constructor
        this.username = username;
    }

    /** Getter for the position
     *
     * @return position
     */
    public String getPosition() {
        return position;
    }

    /** Setter for the position
     *
     * @param position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /** Getter for the password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /** Setter for the password
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Getter for the username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /** Setter for the username
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /** Getter for the last name (overrides the method in the superclass)
     *
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }
}