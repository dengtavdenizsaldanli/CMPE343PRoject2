import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a contact in the Contact Management System.
 * <p>
 * This class encapsulates all information about a contact including personal details,
 * contact information, and social media links. All fields are validated upon setting
 * to ensure data integrity.
 * </p>
 * 
 * <p><b>Required Fields:</b> firstName, lastName, phonePrimary, email</p>
 * <p><b>Optional Fields:</b> middleName, nickname, city, phoneSecondary, linkedinUrl, birthDate</p>
 * 
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *   <li>Names: Letters, spaces, apostrophes, Turkish characters (Ç, Ğ, İ, Ö, Ş, Ü)</li>
 *   <li>Phone: 10-15 digits, optional leading +</li>
 *   <li>Email: Valid email format (contains @)</li>
 *   <li>Birth Date: Valid date, not in future, not before 1900</li>
 * </ul>
 * 
 * @author Group 22
 * @version 1.0
 * @since 2024
 */
public class Contact {

    // ==========================================
    // VALIDATION PATTERNS (Static Constants)
    // ==========================================
    
    /**
     * Pattern for validating person names (first, middle, last, nickname).
     * Allows: letters (including Turkish), spaces, apostrophes.
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-zÇçĞğİıÖöŞşÜü' ]+");
    
    /**
     * Pattern for validating city names.
     * Allows: letters (including Turkish), spaces, apostrophes, dots, dashes.
     */
    private static final Pattern CITY_PATTERN = Pattern.compile("[A-Za-zÇçĞğİıÖöŞşÜü'.\\- ]+");
    
    /**
     * Pattern for validating phone numbers.
     * Allows: optional +, followed by 10-15 digits.
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+?[0-9]{10,15}");
    
    /**
     * Pattern for validating email addresses.
     * Basic validation: must contain @ with text before and after.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    /**
     * Date formatter for birth dates (yyyy-MM-dd format).
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ==========================================
    // PRIVATE FIELDS (Encapsulation)
    // ==========================================
    
    private Integer id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String city;  // ← ADDED! (was missing)
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String linkedinUrl;
    private LocalDate birthDate;  // Changed from String to LocalDate for proper validation
    private String birthDateString;  // For database compatibility (stored as String in DB)

    // ==========================================
    // CONSTRUCTORS
    // ==========================================
    
    /**
     * Full constructor with all fields (for loading from database).
     * <p>
     * This constructor is typically used when reading contacts from the database.
     * It does NOT perform validation - assumes data from DB is already valid.
     * </p>
     * 
     * @param id unique contact identifier
     * @param firstName contact's first name (required)
     * @param middleName contact's middle name (optional)
     * @param lastName contact's last name (required)
     * @param nickname contact's nickname (optional)
     * @param city contact's city (optional)
     * @param phonePrimary primary phone number (required)
     * @param phoneSecondary secondary phone number (optional)
     * @param email email address (required)
     * @param linkedinUrl LinkedIn profile URL (optional)
     * @param birthDate birth date in yyyy-MM-dd format (optional)
     */
    public Contact(int id, String firstName, String middleName, String lastName, 
                   String nickname, String city, String phonePrimary, String phoneSecondary,
                   String email, String linkedinUrl, String birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.city = city;
        this.phonePrimary = phonePrimary;
        this.phoneSecondary = phoneSecondary;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
        this.birthDateString = birthDate;
        
        // Parse birth date if available
        if (birthDate != null && !birthDate.isEmpty()) {
            try {
                this.birthDate = LocalDate.parse(birthDate, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                this.birthDate = null;
            }
        }
    }

    /**
     * Constructor for creating new contacts (without ID).
     * <p>
     * This constructor DOES perform validation on all fields.
     * Use this when adding new contacts to ensure data integrity.
     * </p>
     * 
     * @param firstName contact's first name (required)
     * @param middleName contact's middle name (optional)
     * @param lastName contact's last name (required)
     * @param nickname contact's nickname (optional)
     * @param city contact's city (optional)
     * @param phonePrimary primary phone number (required)
     * @param phoneSecondary secondary phone number (optional)
     * @param email email address (required)
     * @param linkedinUrl LinkedIn profile URL (optional)
     * @param birthDate birth date in yyyy-MM-dd format (optional)
     * @throws IllegalArgumentException if any required field is invalid
     */
    public Contact(String firstName, String middleName, String lastName, 
                   String nickname, String city, String phonePrimary, String phoneSecondary,
                   String email, String linkedinUrl, String birthDate) {
        this.id = null;  // Will be set by database
        setFirstName(firstName);
        setMiddleName(middleName);
        setLastName(lastName);
        setNickname(nickname);
        setCity(city);
        setPhonePrimary(phonePrimary);
        setPhoneSecondary(phoneSecondary);
        setEmail(email);
        setLinkedinUrl(linkedinUrl);
        setBirthDate(birthDate);
    }

    // ==========================================
    // GETTERS
    // ==========================================
    
    /** @return the contact's unique ID */
    public Integer getId() { return id; }
    
    /** @return the contact's first name */
    public String getFirstName() { return firstName; }
    
    /** @return the contact's middle name (may be null) */
    public String getMiddleName() { return middleName; }
    
    /** @return the contact's last name */
    public String getLastName() { return lastName; }
    
    /** @return the contact's nickname (may be null) */
    public String getNickname() { return nickname; }
    
    /** @return the contact's city (may be null) */
    public String getCity() { return city; }
    
    /** @return the primary phone number */
    public String getPhonePrimary() { return phonePrimary; }
    
    /** @return the secondary phone number (may be null) */
    public String getPhoneSecondary() { return phoneSecondary; }
    
    /** @return the email address */
    public String getEmail() { return email; }
    
    /** @return the LinkedIn URL (may be null) */
    public String getLinkedinUrl() { return linkedinUrl; }
    
    /** @return the birth date as LocalDate (may be null) */
    public LocalDate getBirthDate() { return birthDate; }
    
    /** @return the birth date as String in yyyy-MM-dd format (may be null) */
    public String getBirthDateString() { return birthDateString; }

    // ==========================================
    // SETTERS WITH VALIDATION
    // ==========================================
    
    /**
     * Sets the contact ID.
     * @param id the unique identifier (typically set by database)
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Sets the first name with validation.
     * @param firstName the first name to set (required, 1-100 chars, letters/apostrophes only)
     * @throws IllegalArgumentException if firstName is invalid
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        String trimmed = firstName.trim();
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("First name cannot exceed 100 characters");
        }
        if (!isValidName(trimmed)) {
            throw new IllegalArgumentException("First name contains invalid characters. Only letters, spaces, and apostrophes allowed.");
        }
        this.firstName = trimmed;
    }

    /**
     * Sets the middle name with validation.
     * @param middleName the middle name to set (optional, max 100 chars)
     * @throws IllegalArgumentException if middleName is invalid
     */
    public void setMiddleName(String middleName) {
        if (middleName == null || middleName.trim().isEmpty()) {
            this.middleName = null;
            return;
        }
        String trimmed = middleName.trim();
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Middle name cannot exceed 100 characters");
        }
        if (!isValidName(trimmed)) {
            throw new IllegalArgumentException("Middle name contains invalid characters");
        }
        this.middleName = trimmed;
    }

    /**
     * Sets the last name with validation.
     * @param lastName the last name to set (required, 1-100 chars)
     * @throws IllegalArgumentException if lastName is invalid
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        String trimmed = lastName.trim();
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Last name cannot exceed 100 characters");
        }
        if (!isValidName(trimmed)) {
            throw new IllegalArgumentException("Last name contains invalid characters");
        }
        this.lastName = trimmed;
    }

    /**
     * Sets the nickname with validation.
     * @param nickname the nickname to set (optional, max 100 chars)
     * @throws IllegalArgumentException if nickname is invalid
     */
    public void setNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            this.nickname = null;
            return;
        }
        String trimmed = nickname.trim();
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Nickname cannot exceed 100 characters");
        }
        if (!isValidName(trimmed)) {
            throw new IllegalArgumentException("Nickname contains invalid characters");
        }
        this.nickname = trimmed;
    }

    /**
     * Sets the city with validation.
     * @param city the city to set (optional, max 100 chars)
     * @throws IllegalArgumentException if city is invalid
     */
    public void setCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            this.city = null;
            return;
        }
        String trimmed = city.trim();
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("City cannot exceed 100 characters");
        }
        if (!isValidCity(trimmed)) {
            throw new IllegalArgumentException("City contains invalid characters");
        }
        this.city = trimmed;
    }

    /**
     * Sets the primary phone with validation.
     * @param phonePrimary the primary phone to set (required, 10-15 digits)
     * @throws IllegalArgumentException if phone is invalid
     */
    public void setPhonePrimary(String phonePrimary) {
        if (phonePrimary == null || phonePrimary.trim().isEmpty()) {
            throw new IllegalArgumentException("Primary phone cannot be empty");
        }
        String trimmed = phonePrimary.trim();
        if (!isValidPhone(trimmed)) {
            throw new IllegalArgumentException("Invalid phone format. Use 10-15 digits, optional leading +");
        }
        this.phonePrimary = trimmed;
    }

    /**
     * Sets the secondary phone with validation.
     * @param phoneSecondary the secondary phone to set (optional, 10-15 digits)
     * @throws IllegalArgumentException if phone is invalid
     */
    public void setPhoneSecondary(String phoneSecondary) {
        if (phoneSecondary == null || phoneSecondary.trim().isEmpty()) {
            this.phoneSecondary = null;
            return;
        }
        String trimmed = phoneSecondary.trim();
        if (!isValidPhone(trimmed)) {
            throw new IllegalArgumentException("Invalid phone format. Use 10-15 digits, optional leading +");
        }
        this.phoneSecondary = trimmed;
    }

    /**
     * Sets the email with validation.
     * @param email the email to set (required, valid email format)
     * @throws IllegalArgumentException if email is invalid
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        String trimmed = email.trim();
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Email cannot exceed 255 characters");
        }
        if (!isValidEmail(trimmed)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = trimmed;
    }

    /**
     * Sets the LinkedIn URL.
     * @param linkedinUrl the LinkedIn URL to set (optional)
     */
    public void setLinkedinUrl(String linkedinUrl) {
        if (linkedinUrl == null || linkedinUrl.trim().isEmpty()) {
            this.linkedinUrl = null;
            return;
        }
        String trimmed = linkedinUrl.trim();
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("LinkedIn URL cannot exceed 255 characters");
        }
        this.linkedinUrl = trimmed;
    }

    /**
     * Sets the birth date with validation.
     * @param birthDateStr the birth date string in yyyy-MM-dd format (optional)
     * @throws IllegalArgumentException if date is invalid or in future
     */
    public void setBirthDate(String birthDateStr) {
        if (birthDateStr == null || birthDateStr.trim().isEmpty()) {
            this.birthDate = null;
            this.birthDateString = null;
            return;
        }
        
        try {
            LocalDate date = LocalDate.parse(birthDateStr.trim(), DATE_FORMATTER);
            
            // Validate: not in future
            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Birth date cannot be in the future");
            }
            
            // Validate: reasonable year (not before 1900)
            if (date.getYear() < 1900) {
                throw new IllegalArgumentException("Birth date cannot be before year 1900");
            }
            
            this.birthDate = date;
            this.birthDateString = birthDateStr.trim();
            
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd (e.g., 1990-05-15)");
        }
    }

    // ==========================================
    // VALIDATION HELPER METHODS (Static)
    // ==========================================
    
    /**
     * Validates a person name (first, middle, last, nickname).
     * @param name the name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validates a city name.
     * @param city the city to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCity(String city) {
        return city != null && CITY_PATTERN.matcher(city).matches();
    }

    /**
     * Validates a phone number.
     * @param phone the phone to validate
     * @return true if valid (10-15 digits, optional +), false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validates an email address.
     * @param email the email to validate
     * @return true if valid email format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates a birth date string.
     * @param dateStr the date string to validate (yyyy-MM-dd)
     * @return true if valid date and not in future, false otherwise
     */
    public static boolean isValidBirthDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            return !date.isAfter(LocalDate.now()) && date.getYear() >= 1900;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // ==========================================
    // BUSINESS LOGIC METHODS
    // ==========================================
    
    /**
     * Calculates the contact's age based on birth date.
     * @return age in years, or -1 if birth date is not set
     */
    public int getAge() {
        if (birthDate == null) {
            return -1;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Checks if contact has a LinkedIn profile.
     * @return true if LinkedIn URL is set and not empty
     */
    public boolean hasLinkedIn() {
        return linkedinUrl != null && !linkedinUrl.trim().isEmpty();
    }

    /**
     * Checks if contact has a middle name.
     * @return true if middle name is set and not empty
     */
    public boolean hasMiddleName() {
        return middleName != null && !middleName.trim().isEmpty();
    }

    /**
     * Checks if contact has a nickname.
     * @return true if nickname is set and not empty
     */
    public boolean hasNickname() {
        return nickname != null && !nickname.trim().isEmpty();
    }

    /**
     * Checks if contact has a secondary phone.
     * @return true if secondary phone is set and not empty
     */
    public boolean hasSecondaryPhone() {
        return phoneSecondary != null && !phoneSecondary.trim().isEmpty();
    }

    /**
     * Gets the full name (first + middle + last).
     * @return full name as single string
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder(firstName);
        if (hasMiddleName()) {
            sb.append(" ").append(middleName);
        }
        sb.append(" ").append(lastName);
        return sb.toString();
    }

    /**
     * Gets a formatted phone number (removes + and spaces).
     * @return formatted primary phone number
     */
    public String getFormattedPhone() {
        return phonePrimary.replace("+", "").replace(" ", "");
    }

    // ==========================================
    // PRINT METHODS (for console display)
    // ==========================================
    
    /**
     * Prints the table header for contact list display.
     * Should be called once before printing multiple contacts.
     */
    public static void printHeader() {
        System.out.printf(
                "%-4s %-14s %-14s %-14s %-14s %-14s %-20s %-20s %-30s %-40s %-14s\n",
                "ID", "First", "Middle", "Last",
                "Nick", "City", "Phone 1", "Phone 2",
                "Email", "LinkedIn", "Birth Date"
        );
        System.out.println(
                "------------------------------------------------------------------------------------------------------------------------------------------"
        );
    }

    /**
     * Prints this contact as a formatted table row.
     * Use after calling printHeader() for proper alignment.
     */
    public void print() {
        System.out.printf(
                "%-4d %-14s %-14s %-14s %-14s %-14s %-20s %-20s %-30s %-40s %-14s\n",
                id != null ? id : 0,
                safe(firstName),
                safe(middleName),
                safe(lastName),
                safe(nickname),
                safe(city),
                safe(phonePrimary),
                safe(phoneSecondary),
                safe(email),
                safe(linkedinUrl),
                safe(birthDateString)
        );
    }

    /**
     * Returns a safe string for display (replaces null with "-").
     * @param val the string value
     * @return the value or "-" if null/empty
     */
    private String safe(String val) {
        return (val == null || val.trim().isEmpty()) ? "-" : val;
    }

    // ==========================================
    // OVERRIDE METHODS (equals, hashCode, toString)
    // ==========================================
    
    /**
     * Compares this contact with another object for equality.
     * Two contacts are equal if they have the same ID.
     * 
     * @param obj the object to compare with
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        return Objects.equals(id, contact.id);
    }

    /**
     * Returns a hash code for this contact.
     * @return hash code based on contact ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this contact.
     * @return string with all contact details
     */
    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + getFullName() + '\'' +
                ", phone='" + phonePrimary + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", age=" + getAge() +
                '}';
    }
}