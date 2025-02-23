package Entities;

/**
 * Represents a book entity in the library system.
 * This class encapsulates the properties of a book, such as its ID, title, author, publisher, year, and status.
 */
public class Books {
    protected int ID; // Unique identifier for the book
    private String Author; // Author of the book
    private String Title; // Title of the book
    private String Publisher; // Publisher of the book
    private int Year; // Year of publication
    private boolean status; // Availability status of the book (true = available, false = unavailable)

    // Default constructor
    public Books() {

    }

    // Parameterized constructor to initialize a book with title, author, publisher, year, and status
    public Books(String Title, String Author, String Publisher, int Year, boolean status) {
        this.Author = Author;
        this.Title = Title;
        this.Publisher = Publisher;
        this.Year = Year;
        this.status = status;
    }

    // Parameterized constructor to initialize a book with ID, title, author, publisher, and year
    public Books(int ID, String title2, String author2, String publisher2, int year2) {
        this.ID = ID;
        this.Author = author2;
        this.Title = title2;
        this.Publisher = publisher2;
        this.Year = year2;

    }

    /** Getter for the author
     *
     * @return Author
     */
    public String getAuthor() {
        return Author;
    }

    /** Setter for the author
     *
     * @param author
     */
    public void setAuthor(String author) {
        Author = author;
    }

    /** Getter for the title
     *
     * @return Title
     */
    public String getTitle() {
        return Title;
    }

    /** Setter for the title
     *
     * @param title
     */
    public void setTitle(String title) {
        Title = title;
    }

    /** Setter for the ID
     *
     * @param id
     */
    public void setID(int id) {
        ID = id;
    }

    /** Getter for the ID
     *
     * @return ID
     */
    public int getID() {
        return ID;
    }

    /** Getter for the publisher
     *
     * @return Publisher
     */
    public String getPublisher() {
        return Publisher;
    }

    /** Setter for the publisher
     *
     * @param publisher
     */
    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    /** Getter for the year
     *
     * @return Year
     */
    public int getYear() {
        return Year;
    }

    /** Setter for the year
     *
     * @param year
     */
    public void setYear(int year) {
        Year = year;
    }

    /** Getter for the status
     *
     * @return status
     */
    public boolean isStatus() {
        return status;
    }

    /** Setter for the status
     *
     * @param status
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /** Overrides the toString method to provide a string representation of the book
     *
     * @return
     */
    public String toString() {
        return "Title: " + Title + ", Author: " + Author + ", Year: " + Year + ", Status: " + status;
    }
}