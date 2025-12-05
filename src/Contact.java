import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Pattern;


public class Contact {

    // ==========================================
    // VALIDATION PATTERNS (Static Constants)
    // ==========================================
    
    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-zÇçĞğİıÖöŞşÜü' ]+");
    private static final Pattern CITY_PATTERN = Pattern.compile("[A-Za-zÇçĞğİıÖöŞşÜü'.\\- ]+");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+?[0-9]{10,15}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ==========================================
    // PRIVATE FIELDS (Encapsulation)
    // ==========================================
    
    private Integer id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String city;
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String linkedinUrl;
    private LocalDate birthDate;
    private String birthDateString;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

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

        if (birthDate != null && !birthDate.isEmpty()) {
            try {
                this.birthDate = LocalDate.parse(birthDate, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                this.birthDate = null;
            }
        }
    }

    public Contact(String firstName, String middleName, String lastName, 
                   String nickname, String city, String phonePrimary, String phoneSecondary,
                   String email, String linkedinUrl, String birthDate) {
        this.id = null;
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
    
    public Integer getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getNickname() { return nickname; }
    public String getCity() { return city; }
    public String getPhonePrimary() { return phonePrimary; }
    public String getPhoneSecondary() { return phoneSecondary; }
    public String getEmail() { return email; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getBirthDateString() { return birthDateString; }

    // ==========================================
    // SETTERS WITH VALIDATION
    // ==========================================

    public void setId(Integer id) {
        this.id = id;
    }
    
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

    public void setBirthDate(String birthDateStr) {
        if (birthDateStr == null || birthDateStr.trim().isEmpty()) {
            this.birthDate = null;
            this.birthDateString = null;
            return;
        }
        
        try {
            LocalDate date = LocalDate.parse(birthDateStr.trim(), DATE_FORMATTER);
            
            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Birth date cannot be in the future");
            }
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

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidCity(String city) {
        return city != null && CITY_PATTERN.matcher(city).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

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

    public int getAge() {
        if (birthDate == null) {
            return -1;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public boolean hasLinkedIn() {
        return linkedinUrl != null && !linkedinUrl.trim().isEmpty();
    }

    public boolean hasMiddleName() {
        return middleName != null && !middleName.trim().isEmpty();
    }

    public boolean hasNickname() {
        return nickname != null && !nickname.trim().isEmpty();
    }

    public boolean hasSecondaryPhone() {
        return phoneSecondary != null && !phoneSecondary.trim().isEmpty();
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder(firstName);
        if (hasMiddleName()) {
            sb.append(" ").append(middleName);
        }
        sb.append(" ").append(lastName);
        return sb.toString();
    }

    public String getFormattedPhone() {
        return phonePrimary.replace("+", "").replace(" ", "");
    }

    // ==========================================
    // PRINT METHODS (for console display)
    // ==========================================

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

    private String safe(String val) {
        return (val == null || val.trim().isEmpty()) ? "-" : val;
    }

    // ==========================================
    // OVERRIDE METHODS (equals, hashCode, toString)
    // ==========================================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

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
