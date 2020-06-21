#import.sql FILE - TEST INITIAL DB CONF

#USERS

DELETE FROM users

INSERT INTO users values (1, 'admin', 'admin@admin', 'admin','$2a$10$FPgMC97v4AYXDPtGhIF/k.heFX54FIKWYXc0BgH5C0xEslhfxwuI6', 'ADMIN', 'admin');
INSERT INTO users values (2, 'user', 'user@user', 'user', '$2a$10$isS28uaqlVqUBV7yvK2Jqem1vOfhB4RYMjNLyD7JZjhSRJ31cIsQm', 'USER', 'user');
INSERT INTO users values (3, 'test', 'test@test', 'test', '$2a$10$isS28uaqlVqUBV7yvK2Jqem1vOfhB4RYMjNLyD7JZjhSRJ31cIsQm', 'ADMIN', 'test');


#FILES_METADATA

DELETE FROM files_metadata

INSERT INTO files_metadata values (1, 'test_file_cloud_name1', 'text/plain', 'description', '2217-06-03 17:39:21', 'test1.txt', '1', 1);
INSERT INTO files_metadata values (2, 'test_file_cloud_name2', 'text/plain', 'description', '2017-05-03 17:59:11', 'test2.txt', '2', 1);
INSERT INTO files_metadata values (3, 'test_file_cloud_name3', 'text/plain', 'description', '2016-05-03 10:39:21', 'testSameName.txt', '3', 1);

INSERT INTO files_metadata values (4, 'test_file_cloud_name4', 'text/plain', 'description', '2015-01-01 17:39:21', 'test3.txt', '5', 2);
INSERT INTO files_metadata values (5, 'test_file_cloud_name5', 'text/plain', 'description', '2017-11-11 23:39:21', 'test4.txt', '4', 2);
INSERT INTO files_metadata values (6, 'test_file_cloud_name6', 'text/plain', 'description', '2017-08-05 08:08:01', 'testSameName.txt', '7', 2);

INSERT INTO files_metadata values (7, 'test_file_cloud_name7', 'text/plain', 'description', '2017-08-05 08:08:01', 'test5.txt', '7', 3);
INSERT INTO files_metadata values (8, 'test_file_cloud_name8', 'text/plain', 'description', '2017-08-05 08:08:01', 'test6.txt', '7', 3);
INSERT INTO files_metadata values (9, 'test_file_cloud_name9', 'text/plain', 'description', '2017-08-05 08:08:01', 'testSameName.txt', '7', 3);

#SHARED_FILE_METADATA

DELETE FROM shared_files_metadata

INSERT INTO shared_files_metadata values (1, '2025-05-03 17:39:21', 0, 8, 3, 2);
INSERT INTO shared_files_metadata values (2, '2025-05-03 17:39:21', 0, 6, 2, 3);
INSERT INTO shared_files_metadata values (3, '2025-05-03 17:39:21', 0, 5, 2, 3);