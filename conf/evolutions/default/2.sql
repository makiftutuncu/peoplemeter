# --- !Ups
# --- Password = 123456
INSERT INTO accounts(id, email, password)
VALUES (1, "admin@peoplemeter.com", "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413");

INSERT INTO channels (id, name, logo_position) VALUES
(1, 'Trt', 1),
(2, 'Atv', 1),
(3, 'Show Tv', 1),
(4, 'Kanal D', 1),
(5, 'Fox', 1);

INSERT INTO houses (id, device_id, family_name, district, street, building_name, door_number, postal_code, town, city) VALUES
(1, 'a3d6471be6a4459a87d9d70b9fcd754d', 'Hacihalil', 'Sogukkuyu Mah.', 'Girne Bulvari', 'Onur Apt.', '12', '35000', 'Bayrakli', 'Izmir'),
(2, '7b970ec12c3d4b1e8eaf16c9c26c4539', 'Tutuncu', 'Karapinar Mah.', 'B149 Sok.', 'Tutuncu Apt.', '12', '16300', 'Yildirim', 'Bursa');

INSERT INTO people (id, name, birth_date, is_male, house_id, button_number) VALUES
(1, 'Ezgi Hacihalil', '1992-03-13', 0, 1, 1),
(2, 'Mehmet Akif Tutuncu', '1991-03-23', 1, 2, 1);

# --- !Downs
TRUNCATE accounts;
TRUNCATE channels;
TRUNCATE houses;
TRUNCATE people;