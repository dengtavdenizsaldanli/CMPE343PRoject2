-- DATABASE RESET
DROP DATABASE IF EXISTS cmpe;
CREATE DATABASE cmpe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cmpe;


-- USERS TABLE
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    role ENUM('Tester', 'Junior Developer', 'Senior Developer', 'Manager') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- CONTACTS TABLE (city sütunu dahil)
CREATE TABLE contacts (
    contact_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),
    city VARCHAR(100),
    phone_primary VARCHAR(20) NOT NULL,
    phone_secondary VARCHAR(20),
    email VARCHAR(150) NOT NULL,
    linkedin_url VARCHAR(255),
    birth_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- DEFAULT USERS
INSERT INTO users (username, password_hash, name, surname, role) VALUES
('tt', SHA2('tt', 256), 'Test', 'Tester', 'Tester'),
('jd', SHA2('jd', 256), 'Junior', 'Dev', 'Junior Developer'),
('sd', SHA2('sd', 256), 'Senior', 'Dev', 'Senior Developer'),
('man', SHA2('man', 256), 'Manager', 'Boss', 'Manager');



INSERT INTO contacts (first_name, middle_name, last_name, nickname, city, phone_primary, phone_secondary, email, linkedin_url, birth_date) 
VALUES

-- TURKEY - Istanbul (15 contacts)
('Ahmet', 'Can', 'Yılmaz', 'Ace', 'Istanbul', '+905321234567', '+905421234567', 'ahmet.yilmaz@gmail.com', 'https://linkedin.com/in/ahmetyilmaz', '1995-03-15'),
('Zeynep', NULL, 'Kaya', 'Zey', 'Istanbul', '+905331234568', NULL, 'zeynep.kaya@outlook.com', 'https://linkedin.com/in/zeynepkaya', '1993-07-22'),
('Mehmet', 'Ali', 'Demir', NULL, 'Istanbul', '+905341234569', '+905441234569', 'mehmet.demir@gmail.com', 'https://linkedin.com/in/mehmetdemir', '1992-11-08'),
('Ayşe', 'Nur', 'Şahin', NULL, 'Istanbul', '+905351234570', NULL, 'ayse.sahin@hotmail.com', 'https://linkedin.com/in/aysesahin', '1994-05-30'),
('Emre', 'Can', 'Yıldız', NULL, 'Istanbul', '+905321234577', '+905421234577', 'emre.yildiz@gmail.com', 'https://linkedin.com/in/emreyildiz', '1985-01-14'),
('Elif', NULL, 'Çelik', NULL, 'Istanbul', '+905371234572', NULL, 'elif.celik@gmail.com', 'https://linkedin.com/in/elifcelik', '1998-02-18'),
('Hakan', 'Serkan', 'Öztürk', NULL, 'Istanbul', '+905341234579', NULL, 'hakan.ozturk@yahoo.com', 'https://linkedin.com/in/hakanozturk', '1983-07-05'),
('Selin', 'Defne', 'Kurt', 'Sel', 'Istanbul', '+905391234574', '+905491234574', 'selin.kurt@outlook.com', NULL, '1997-12-03'),
('Burak', 'Emre', 'Arslan', 'Buro', 'Istanbul', '+905381234573', NULL, 'burak.arslan@yahoo.com', NULL, '1999-06-25'),
('Deniz', 'Su', 'Koç', 'Deno', 'Istanbul', '+905301234575', NULL, 'deniz.koc@gmail.com', 'https://linkedin.com/in/denizkoc', '2000-04-10'),
('Leyla', 'Asya', 'Çetin', NULL, 'Istanbul', '+905371234582', NULL, 'leyla.cetin@hotmail.com', 'https://linkedin.com/in/leylacetin', '1975-05-08'),
('Cem', NULL, 'Aydın', NULL, 'Istanbul', '+905301234585', '+905401234585', 'cem.aydin@gmail.com', NULL, '2003-12-25'),
('Gizem', NULL, 'Özkan', 'Gizi', 'Istanbul', '+905331234578', NULL, 'gizem.ozkan@outlook.com', 'https://linkedin.com/in/gizemozkan', '1987-10-28'),
('Murat', NULL, 'Bulut', NULL, 'Istanbul', '+905381234583', NULL, 'murat.bulut@gmail.com', NULL, '1973-09-30'),
('Pınar', 'Ece', 'Yavuz', 'Pino', 'Istanbul', '+905311234586', NULL, 'pinar.yavuz@gmail.com', 'https://linkedin.com/in/pinaryavuz', '1990-06-11'),

-- TURKEY - Ankara (8 contacts)
('Kerem', NULL, 'Polat', NULL, 'Ankara', '+905361234581', NULL, 'kerem.polat@gmail.com', 'https://linkedin.com/in/kerempolat', '1988-11-22'),
('Seda', 'Damla', 'Yılmaz', 'Sedo', 'Ankara', '+905331234588', NULL, 'seda.yilmaz@outlook.com', NULL, '2004-07-15'),
('Can', 'Barış', 'Demir', NULL, 'Ankara', '+905371234525', '+905471234525', 'can.demir@gmail.com', 'https://linkedin.com/in/candemir', '1992-08-12'),
('Fulya', NULL, 'Çelik', NULL, 'Ankara', '+905301234528', NULL, 'fulya.celik@gmail.com', NULL, '1998-11-22'),
('İlker', 'Burak', 'Koç', NULL, 'Ankara', '+905331234531', '+905431234531', 'ilker.koc@gmail.com', 'https://linkedin.com/in/ilkerkoc', '1996-01-25'),
('Özge', 'Damla', 'Karabulut', NULL, 'Ankara', '+905381234536', NULL, 'ozge.karabulut@gmail.com', 'https://linkedin.com/in/ozgekarabulut', '1993-12-08'),
('Tolga', NULL, 'Aslan', NULL, 'Ankara', '+905301234518', NULL, 'tolga.aslan@gmail.com', NULL, '1995-11-08'),
('Yasemin', 'Su', 'Yurt', 'Yas', 'Ankara', '+905331234521', NULL, 'yasemin.yurt@gmail.com', 'https://linkedin.com/in/yaseminyurt', '1999-01-12'),

-- TURKEY - Izmir (5 contacts)
('Mustafa', NULL, 'Özdemir', 'Musta', 'Izmir', '+905361234571', '+905461234571', 'mustafa.ozdemir@gmail.com', 'https://linkedin.com/in/mustafaozdemir', '1991-09-12'),
('İrem', 'Sude', 'Karabulut', NULL, 'Izmir', '+905351234580', NULL, 'irem.karabulut@yahoo.com', 'https://linkedin.com/in/iremkarabulut', '1986-03-19'),
('Barış', 'Mert', 'Yılmaz', NULL, 'Izmir', '+905381234546', '+905481234546', 'baris.yilmaz@gmail.com', 'https://linkedin.com/in/barisyilmaz', '1993-07-12'),
('Ebru', NULL, 'Şahin', 'Ebru', 'Izmir', '+905311234549', NULL, 'ebru.sahin@gmail.com', NULL, '1991-06-22'),
('Kadir', NULL, 'Aydın', NULL, 'Izmir', '+905361234554', NULL, 'kadir.aydin@gmail.com', 'https://linkedin.com/in/kadiraydin', '1990-05-18'),

-- TURKEY - Bursa (2 contacts)
('Tarık', NULL, 'Güler', NULL, 'Bursa', '+905341234589', NULL, 'tarik.guler@yahoo.com', 'https://linkedin.com/in/tarikguler', '2002-11-03'),
('Uğur', NULL, 'Acar', 'Uğur', 'Bursa', '+905351234563', NULL, 'ugur.acar@yahoo.com', NULL, '1989-12-10'),

-- USA - New York (12 contacts)
('John', 'Michael', 'Smith', 'Johnny', 'New York', '+12125551001', '+19175551001', 'john.smith@gmail.com', 'https://linkedin.com/in/johnsmith', '1990-05-12'),
('Emily', 'Rose', 'Johnson', NULL, 'New York', '+12125551002', NULL, 'emily.johnson@outlook.com', 'https://linkedin.com/in/emilyjohnson', '1995-08-23'),
('Michael', NULL, 'Williams', 'Mike', 'New York', '+12125551003', '+19175551003', 'michael.williams@yahoo.com', 'https://linkedin.com/in/michaelwilliams', '1988-02-14'),
('Sarah', 'Jane', 'Brown', NULL, 'New York', '+12125551004', NULL, 'sarah.brown@gmail.com', 'https://linkedin.com/in/sarahbrown', '1992-11-30'),
('David', NULL, 'Jones', 'Dave', 'New York', '+12125551005', NULL, 'david.jones@hotmail.com', NULL, '1985-07-19'),
('Jessica', 'Lynn', 'Garcia', NULL, 'New York', '+12125551006', '+19175551006', 'jessica.garcia@outlook.com', 'https://linkedin.com/in/jessicagarcia', '1998-03-05'),
('Christopher', 'James', 'Martinez', 'Chris', 'New York', '+12125551007', NULL, 'chris.martinez@gmail.com', 'https://linkedin.com/in/chrismartinez', '1991-09-22'),
('Amanda', NULL, 'Rodriguez', NULL, 'New York', '+12125551008', NULL, 'amanda.rodriguez@yahoo.com', 'https://linkedin.com/in/amandarodriguez', '1996-01-15'),
('Matthew', 'Ryan', 'Hernandez', 'Matt', 'New York', '+12125551009', '+19175551009', 'matthew.hernandez@gmail.com', 'https://linkedin.com/in/matthewhernandez', '1989-06-08'),
('Ashley', NULL, 'Lopez', NULL, 'New York', '+12125551010', NULL, 'ashley.lopez@hotmail.com', NULL, '2001-12-20'),
('Daniel', 'Alexander', 'Gonzalez', 'Dan', 'New York', '+12125551011', NULL, 'daniel.gonzalez@outlook.com', 'https://linkedin.com/in/danielgonzalez', '1994-04-17'),
('Jennifer', 'Marie', 'Wilson', 'Jen', 'New York', '+12125551012', '+19175551012', 'jennifer.wilson@gmail.com', 'https://linkedin.com/in/jenniferwilson', '1987-10-25'),

-- USA - Los Angeles (8 contacts)
('Robert', 'Lee', 'Anderson', 'Rob', 'Los Angeles', '+13105552001', '+13235552001', 'robert.anderson@gmail.com', 'https://linkedin.com/in/robertanderson', '1993-07-08'),
('Michelle', NULL, 'Thomas', NULL, 'Los Angeles', '+13105552002', NULL, 'michelle.thomas@yahoo.com', 'https://linkedin.com/in/michellethomas', '1997-02-14'),
('James', 'Patrick', 'Taylor', 'Jim', 'Los Angeles', '+13105552003', NULL, 'james.taylor@outlook.com', NULL, '1986-11-30'),
('Lisa', 'Ann', 'Moore', NULL, 'Los Angeles', '+13105552004', '+13235552004', 'lisa.moore@gmail.com', 'https://linkedin.com/in/lisamoore', '1991-05-22'),
('William', NULL, 'Jackson', 'Bill', 'Los Angeles', '+13105552005', NULL, 'william.jackson@hotmail.com', 'https://linkedin.com/in/williamjackson', '1995-09-16'),
('Angela', 'Rose', 'Martin', 'Angie', 'Los Angeles', '+13105552006', NULL, 'angela.martin@gmail.com', 'https://linkedin.com/in/angelamartin', '1989-03-28'),
('Kevin', NULL, 'Lee', NULL, 'Los Angeles', '+13105552007', '+13235552007', 'kevin.lee@outlook.com', 'https://linkedin.com/in/kevinlee', '1998-12-05'),
('Nicole', 'Marie', 'Perez', NULL, 'Los Angeles', '+13105552008', NULL, 'nicole.perez@yahoo.com', NULL, '2002-06-19'),

-- UK - London (10 contacts)
('Oliver', 'James', 'Davies', 'Ollie', 'London', '+442071234001', '+447700123001', 'oliver.davies@gmail.com', 'https://linkedin.com/in/oliverdavies', '1992-04-15'),
('Sophie', 'Grace', 'Evans', NULL, 'London', '+442071234002', NULL, 'sophie.evans@outlook.com', 'https://linkedin.com/in/sophieevans', '1996-08-22'),
('Harry', NULL, 'Wilson', NULL, 'London', '+442071234003', NULL, 'harry.wilson@yahoo.co.uk', 'https://linkedin.com/in/harrywilson', '1988-01-10'),
('Emma', 'Rose', 'Thomas', NULL, 'London', '+442071234004', '+447700123004', 'emma.thomas@gmail.com', 'https://linkedin.com/in/emmathomas', '1994-11-05'),
('George', 'William', 'Roberts', 'Georgie', 'London', '+442071234005', NULL, 'george.roberts@hotmail.com', NULL, '1990-06-18'),
('Charlotte', NULL, 'Johnson', 'Charlie', 'London', '+442071234006', NULL, 'charlotte.johnson@outlook.com', 'https://linkedin.com/in/charlottejohnson', '1997-03-25'),
('Jack', 'Henry', 'Brown', NULL, 'London', '+442071234007', '+447700123007', 'jack.brown@gmail.com', 'https://linkedin.com/in/jackbrown', '1985-09-12'),
('Amelia', NULL, 'Williams', 'Amy', 'London', '+442071234008', NULL, 'amelia.williams@yahoo.co.uk', 'https://linkedin.com/in/ameliawilliams', '1999-02-28'),
('Thomas', 'Alexander', 'Jones', 'Tom', 'London', '+442071234009', NULL, 'thomas.jones@gmail.com', 'https://linkedin.com/in/thomasjones', '1991-07-20'),
('Isabella', 'Grace', 'Taylor', 'Bella', 'London', '+442071234010', '+447700123010', 'isabella.taylor@outlook.com', NULL, '2003-12-14'),

-- GERMANY - Berlin (8 contacts)
('Lukas', 'Johann', 'Müller', NULL, 'Berlin', '+493012345001', '+491701234001', 'lukas.mueller@gmail.com', 'https://linkedin.com/in/lukasmueller', '1993-05-08'),
('Anna', 'Marie', 'Schmidt', NULL, 'Berlin', '+493012345002', NULL, 'anna.schmidt@web.de', 'https://linkedin.com/in/annaschmidt', '1995-09-15'),
('Felix', NULL, 'Schneider', NULL, 'Berlin', '+493012345003', NULL, 'felix.schneider@gmx.de', 'https://linkedin.com/in/felixschneider', '1989-02-22'),
('Laura', 'Sophie', 'Fischer', NULL, 'Berlin', '+493012345004', '+491701234004', 'laura.fischer@gmail.com', NULL, '1997-06-30'),
('Max', 'Alexander', 'Weber', NULL, 'Berlin', '+493012345005', NULL, 'max.weber@web.de', 'https://linkedin.com/in/maxweber', '1991-11-18'),
('Lena', NULL, 'Wagner', NULL, 'Berlin', '+493012345006', NULL, 'lena.wagner@outlook.com', 'https://linkedin.com/in/lenawagner', '1998-04-05'),
('Paul', 'Thomas', 'Becker', NULL, 'Berlin', '+493012345007', '+491701234007', 'paul.becker@gmx.de', 'https://linkedin.com/in/paulbecker', '1986-08-25'),
('Emma', NULL, 'Koch', NULL, 'Berlin', '+493012345008', NULL, 'emma.koch@gmail.com', NULL, '2001-01-12'),

-- FRANCE - Paris (6 contacts)
('Louis', 'Jean', 'Martin', NULL, 'Paris', '+33145678001', '+33612345001', 'louis.martin@gmail.com', 'https://linkedin.com/in/louismartin', '1992-03-20'),
('Camille', 'Marie', 'Bernard', NULL, 'Paris', '+33145678002', NULL, 'camille.bernard@orange.fr', 'https://linkedin.com/in/camillebernard', '1996-07-14'),
('Hugo', NULL, 'Dubois', NULL, 'Paris', '+33145678003', NULL, 'hugo.dubois@gmail.com', 'https://linkedin.com/in/hugodubois', '1990-11-08'),
('Léa', 'Sophie', 'Thomas', NULL, 'Paris', '+33145678004', '+33612345004', 'lea.thomas@outlook.fr', NULL, '1994-05-25'),
('Gabriel', 'Alexandre', 'Robert', NULL, 'Paris', '+33145678005', NULL, 'gabriel.robert@gmail.com', 'https://linkedin.com/in/gabrielrobert', '1988-09-16'),
('Chloé', NULL, 'Richard', NULL, 'Paris', '+33145678006', NULL, 'chloe.richard@orange.fr', 'https://linkedin.com/in/chloerichard', '1999-02-10'),

-- SPAIN - Madrid (4 contacts)
('Carlos', 'José', 'García', NULL, 'Madrid', '+34912345001', '+34612345001', 'carlos.garcia@gmail.com', 'https://linkedin.com/in/carlosgarcia', '1991-06-12'),
('María', 'Carmen', 'Rodríguez', NULL, 'Madrid', '+34912345002', NULL, 'maria.rodriguez@outlook.es', 'https://linkedin.com/in/mariarodriguez', '1995-10-28'),
('David', NULL, 'Fernández', NULL, 'Madrid', '+34912345003', NULL, 'david.fernandez@yahoo.es', NULL, '1987-03-15'),
('Laura', 'Isabel', 'López', NULL, 'Madrid', '+34912345004', '+34612345004', 'laura.lopez@gmail.com', 'https://linkedin.com/in/lauralopez', '1998-08-22'),

-- ITALY - Rome (4 contacts)
('Marco', 'Antonio', 'Rossi', NULL, 'Rome', '+390612345001', '+393401234001', 'marco.rossi@gmail.com', 'https://linkedin.com/in/marcorossi', '1993-04-18'),
('Giulia', 'Maria', 'Bianchi', NULL, 'Rome', '+390612345002', NULL, 'giulia.bianchi@libero.it', 'https://linkedin.com/in/giuliabianchi', '1997-09-25'),
('Alessandro', NULL, 'Ferrari', 'Alex', 'Rome', '+390612345003', NULL, 'alessandro.ferrari@gmail.com', NULL, '1989-01-08'),
('Francesca', 'Elena', 'Romano', NULL, 'Rome', '+390612345004', '+393401234004', 'francesca.romano@outlook.it', 'https://linkedin.com/in/francescaromano', '1995-06-15'),

-- NETHERLANDS - Amsterdam (3 contacts)
('Lars', 'Johannes', 'De Vries', NULL, 'Amsterdam', '+31201234001', '+31612345001', 'lars.devries@gmail.com', 'https://linkedin.com/in/larsdevries', '1992-05-20'),
('Emma', 'Sophie', 'Van Dijk', NULL, 'Amsterdam', '+31201234002', NULL, 'emma.vandijk@hotmail.nl', 'https://linkedin.com/in/emmavandijk', '1996-11-12'),
('Thomas', NULL, 'Janssen', 'Tom', 'Amsterdam', '+31201234003', NULL, 'thomas.janssen@gmail.com', NULL, '1990-03-08'),

-- CANADA - Toronto (3 contacts)
('Ryan', 'Michael', 'Campbell', NULL, 'Toronto', '+14165551001', '+14165551011', 'ryan.campbell@gmail.com', 'https://linkedin.com/in/ryancampbell', '1994-07-15'),
('Madison', 'Nicole', 'Mitchell', 'Madi', 'Toronto', '+14165551002', NULL, 'madison.mitchell@outlook.com', 'https://linkedin.com/in/madisonmitchell', '1998-02-22'),
('Connor', NULL, 'Stewart', NULL, 'Toronto', '+14165551003', NULL, 'connor.stewart@yahoo.ca', 'https://linkedin.com/in/connorstewart', '1991-10-05'),

-- AUSTRALIA - Sydney (4 contacts)
('Liam', 'James', 'Robinson', NULL, 'Sydney', '+61298765001', '+61412345001', 'liam.robinson@gmail.com', 'https://linkedin.com/in/liamrobinson', '1993-08-18'),
('Olivia', 'Grace', 'Walker', NULL, 'Sydney', '+61298765002', NULL, 'olivia.walker@outlook.com.au', 'https://linkedin.com/in/oliviawalker', '1997-12-25'),
('Noah', NULL, 'White', NULL, 'Sydney', '+61298765003', NULL, 'noah.white@yahoo.com.au', NULL, '1990-04-10'),
('Ava', 'Rose', 'Harris', NULL, 'Sydney', '+61298765004', '+61412345004', 'ava.harris@gmail.com', 'https://linkedin.com/in/avaharris', '1995-09-15');
