PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE user (username varchar(60), email varchar(60), password varchar(200), validated boolean, primary key(email));
INSERT INTO "user" VALUES('admin', 'al8587@gmail.com', '1000:2b82e6610c23a6fd8b13827e736adfdc:0f569eb2fa25915ff12bb23a3e89a127576336856a2b27cce0a5f9353b997af57b2deb226977f54963dc52424c016e24d228a564e6f6a5f8562f0880ed66cce1', 'true');
COMMIT;
