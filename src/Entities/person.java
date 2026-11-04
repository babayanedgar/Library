package Entities;

/**
 * Represents a person entity in the library system.
 * This is an abstract class that serves as the base class for other entities like `User`.
 * It includes properties like name, last name, email, and gender.
 */
abstract class Person {
    protected String name; // The first name of the person
    protected String lastName; // The last name of the person
    protected String email; // The email address of the person
    protected String gender; // The gender of the person

    /** Default constructor
     *
     */
    public Person() {}

    /**Parameterized constructor to initialize a person with all details
     *
     * @param name
     * @param lastName
     * @param email
     * @param gender
     */
    public Person(String name, String lastName, String email, String gender) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
    }

    /** Parameterized constructor to initialize a person with name and last name
     *
     * @param name
     * @param lastname
     */
    public Person(String name, String lastname) {
        this.name = name;
        this.lastName = lastname;
    }

    /** Getter for the name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /** Setter for the name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Getter for the last name
     *
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /** Setter for the last name
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** Getter for the email
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    // Setter for the email (validates the email before setting)
    public void setEmail(String email) {
        if (isValidEmail(email)) {
            this.email = email;
        }
    }

    // Getter for the gender
    public String getGender() {
        return gender;
    }

    // Setter for the gender
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Validates the email format.
     *
     * @param email The email to validate.
     * @return true if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        // Check if email is null or empty
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Check if email contains '@' and '.'
        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }

        // Check if '@' comes before '.'
        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');
        if (atIndex > dotIndex) {
            return false;
        }

        // Check if there's at least one character before '@' and after '.'
        return atIndex != 0 && dotIndex != email.length() - 1;
    }
}