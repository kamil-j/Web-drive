#import.sql FILE - INITIAL DB CONF

#USERS

DELETE FROM users

INSERT INTO users values (1, 'admin_cloud', 'admin@admin', 'admin','$2a$10$FPgMC97v4AYXDPtGhIF/k.heFX54FIKWYXc0BgH5C0xEslhfxwuI6', 'ADMIN', 'admin_surname');
INSERT INTO users values (2, 'user_cloud', 'user@user', 'user', '$2a$10$ZxZi/UqVC6OThDknDYJLJu1KgGi1h51i.yB0yMAMOzZN05ehXclsK', 'USER', 'user_surname');

--DataLoad
INSERT INTO users values (3, '84459550-fcf9-4d09-bf40-44aade93de40', 'adam@wp.pl', 'Adam', '$2a$10$cJ1H2RMG3glTe0V8MLjKdO/mGSOg/Se8gV8gc6qQGbXgCTDAFA1dG', 'USER', 'Kowalski');
INSERT INTO users values (4, 'be0d4924-ab2c-4c47-ad88-3cf13d36cfde', 'mateusz@onet.pl', 'Mateusz', '$2a$10$CUoqmwy3uxjV1h5roYCbK.LQ7wydwLwdGLe3t06mMuJzJAwCCsP0y', 'USER', 'Sieradzki');
INSERT INTO users values (5, '9e0dcb7c-b8bf-424a-a9fd-068ac40445c1', 'kamil@o2.pl', 'Kamil', '$2a$10$RKvbXB1oTEuFtFw8l.b8P.qDN8F25xamDpBthCCY6S8LXa7e/bvdu', 'USER', 'Kowalski');

#FILES_METADATA

DELETE FROM files_metadata

INSERT INTO files_metadata values (1, 'test_file_cloud_name1', 'text/plain', 'description', '2018-06-03 17:29:23', 'test1.txt', '1', 1);
INSERT INTO files_metadata values (2, 'test_file_cloud_name2', 'text/plain', 'description', '2016-09-03 17:30:51', 'test2.txt', '1', 1);
INSERT INTO files_metadata values (3, 'test_file_cloud_name3', 'text/plain', 'description', '2017-09-03 19:19:21', 'test3.txt', '1', 1);
INSERT INTO files_metadata values (4, 'test_file_cloud_name4', 'text/plain', 'description', '2017-06-03 17:39:11', 'test4.txt', '1', 1);
INSERT INTO files_metadata values (5, 'test_file_cloud_name5', 'text/plain', 'description', '2017-05-23 13:59:11', 'test5.txt', '2', 1);
INSERT INTO files_metadata values (6, 'test_file_cloud_name6', 'text/plain', 'description', '2016-05-03 10:39:21', 'testSameName.txt', '3', 1);

INSERT INTO files_metadata values (7, 'test_file_cloud_name7', 'text/plain', 'description', '2015-01-01 17:39:21', 'test3.txt', '5', 2);
INSERT INTO files_metadata values (8, 'test_file_cloud_name8', 'text/plain', 'description', '2017-11-11 23:39:21', 'test4.txt', '4', 2);
INSERT INTO files_metadata values (9, 'test_file_cloud_name9', 'text/plain', 'description', '2017-08-05 08:08:01', 'testSameName.txt', '7', 2);

--DataLoad
INSERT INTO files_metadata values (11, '84459550-fcf9-4d09-bf40-44aade93de40-6bf8b0f0-029a-4956-8f1b-84add7d1b472', 'application/pdf', 'VI Edycja polska', '2017-10-01 13:23:43', 'Eckel B. - Thinking in Java.pdf', '35703890', '3');
INSERT INTO files_metadata values (12, '84459550-fcf9-4d09-bf40-44aade93de40-1bf19a2b-cf81-47cc-b0fe-c62763bdbf34', 'text/plain', 'Programowanie w Java', '2017-10-01 13:25:05', 'Notatki z wykładu 2.txt', '261282', '3');
INSERT INTO files_metadata values (13, '84459550-fcf9-4d09-bf40-44aade93de40-61ed7549-ab86-4f6d-9d7a-e7995391df8f', 'application/pdf', ' Wydanie IX', '2017-10-01 13:26:03', 'C.S. Horstmann & G. Cornell - Java.pdf', '9145258', '3');
INSERT INTO files_metadata values (14, '84459550-fcf9-4d09-bf40-44aade93de40-adda133f-c61a-4092-9050-38445bb20bd9', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'SRS studia', '2017-10-01 13:26:53', 'SRS_Dysk_Internetowy.docx', '29628', '3');
INSERT INTO files_metadata values (15, '84459550-fcf9-4d09-bf40-44aade93de40-02f1fdb7-782d-4bed-8694-eb6fda4cc4df', 'text/plain', 'Programowanie python', '2017-10-01 13:27:47', 'Kod z zajec 3.txt', '522563', '3');
INSERT INTO files_metadata values (16, '84459550-fcf9-4d09-bf40-44aade93de40-1c6ef2d7-597e-4254-88b2-46e6beee5a47', 'application/pdf', 'Rok 2017', '2017-10-01 13:28:39', 'Adam_Kowalski_CV.pdf', '309260', '3');
INSERT INTO files_metadata values (17, 'be0d4924-ab2c-4c47-ad88-3cf13d36cfde-9b5d4519-35d3-44a9-9acf-421cf0d9ca8c', 'application/pdf', 'Podręcznik', '2017-10-01 13:30:20', 'Angielski podstawy.pdf', '35703890', '4');
INSERT INTO files_metadata values (18, 'be0d4924-ab2c-4c47-ad88-3cf13d36cfde-c14cc15c-dac2-4d34-bf19-acf5d2f6b4f6', 'application/pdf', 'CV rok 2017', '2017-10-01 13:30:55', 'Mateusz_Sieradzki_CV.pdf', '309260', '4');
INSERT INTO files_metadata values (19, 'be0d4924-ab2c-4c47-ad88-3cf13d36cfde-b909619f-6fa2-47b2-af58-56e43ac79459', 'text/plain', 'Z dnia 16.09.2017', '2017-10-01 13:32:03', 'Lista_obecnosci.txt', '264180', '4');
INSERT INTO files_metadata values (20, '9e0dcb7c-b8bf-424a-a9fd-068ac40445c1-e1060ee1-daec-4f17-aabd-cbeb5c545967', 'application/pdf', 'Whirlpool D2345', '2017-10-01 13:36:45', 'Instrukcja obslugi zmywarki.pdf', '9145258', '5');
INSERT INTO files_metadata values (21, '9e0dcb7c-b8bf-424a-a9fd-068ac40445c1-5db9430b-f53a-47c8-8e10-251571928cd1', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'Zrobić w tym tyg!', '2017-10-01 13:37:24', 'Dokumenty_z_pracy.docx', '29628', '5');
INSERT INTO files_metadata values (22, '9e0dcb7c-b8bf-424a-a9fd-068ac40445c1-8cc27a3a-63a1-4a3e-9b2b-76b43022aaee', 'text/plain', 'Z dnia 10.09.2017', '2017-10-01 13:38:10', 'Notatki - spotkanie z klientem.txt', '522563', '5');

#SHARED_FILE_METADATA

DELETE FROM shared_files_metadata

INSERT INTO shared_files_metadata values (1, '2018-05-03 17:39:21', 0, 2, 1, 2);
INSERT INTO shared_files_metadata values (2, '2018-08-03 07:39:21', 0, 5, 2, 1);
INSERT INTO shared_files_metadata values (3, '2018-01-01 23:39:21', 0, 3, 1, 2);
INSERT INTO shared_files_metadata values (4, '2020-05-03 17:39:21', 0, 6, 2, 1);

INSERT INTO shared_files_metadata values (5, '2017-02-23 08:39:21', 1, 6, 2, 1);
INSERT INTO shared_files_metadata values (6, '2017-05-03 17:39:21', 1, 2, 1, 2);

INSERT INTO shared_files_metadata values (7, '2019-05-03 19:19:01', 0, 4, 1, 2);
INSERT INTO shared_files_metadata values (8, '2020-05-03 17:39:21', 0, 7, 1, 2);
INSERT INTO shared_files_metadata values (9, '2020-05-03 17:39:21', 0, 8, 1, 2);
INSERT INTO shared_files_metadata values (10, '2020-05-03 17:39:21', 0, 9, 1, 2);

--DataLoad
INSERT INTO shared_files_metadata values (11, '2017-10-08 13:32:35', 0, 17, 4, 3);
INSERT INTO shared_files_metadata values (12, '2017-10-31 13:34:12', 0, 11, 3, 4);
INSERT INTO shared_files_metadata values (13, '2017-10-01 16:34:36', 0, 12, 3, 4);
INSERT INTO shared_files_metadata values (14, '7017-10-01 13:38:26', 0, 22, 5, 3);
INSERT INTO shared_files_metadata values (15, '2017-10-02 13:38:44', 0, 21, 5, 4);
