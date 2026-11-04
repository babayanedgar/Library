-- Library app database setup for Microsoft SQL Server.
-- Matches the connection in src/utilities/dbConnection.java:
--   localhost:1433, database "Library", SQL login root / root.
-- Safe to re-run: existing objects and data are left alone.

IF DB_ID('Library') IS NULL
    CREATE DATABASE Library;
GO

USE Library;
GO

IF OBJECT_ID('dbo.users', 'U') IS NULL
CREATE TABLE dbo.users (
    ID        INT IDENTITY(1,1) CONSTRAINT PK_users PRIMARY KEY,
    Name      NVARCHAR(50)  NOT NULL,
    Lastname  NVARCHAR(50)  NOT NULL,
    Username  NVARCHAR(50)  NOT NULL CONSTRAINT UQ_users_Username UNIQUE,
    Gender    NVARCHAR(10)  NULL,
    Email     NVARCHAR(100) NULL,
    Password  NVARCHAR(64)  NOT NULL,   -- SHA-256 hex, see utilities/HashPass.java
    Position  NVARCHAR(20)  NOT NULL CONSTRAINT CK_users_Position CHECK (Position IN ('CEO', 'User'))
);
GO

IF OBJECT_ID('dbo.books', 'U') IS NULL
CREATE TABLE dbo.books (
    ID        INT IDENTITY(1,1) PRIMARY KEY,
    BookName  NVARCHAR(200) NOT NULL,
    Author    NVARCHAR(100) NULL,
    Publisher NVARCHAR(100) NULL,
    Year      INT           NULL,
    Status    BIT           NOT NULL DEFAULT 1   -- 1 = available, 0 = borrowed
);
GO

-- No foreign keys: the app deletes users and books with a plain DELETE,
-- which would fail if they had borrowing history.
IF OBJECT_ID('dbo.Transactions', 'U') IS NULL
CREATE TABLE dbo.Transactions (
    transactionID INT IDENTITY(1,1) PRIMARY KEY,
    userID        INT           NOT NULL,
    userName      NVARCHAR(50)  NULL,
    bookID        INT           NOT NULL,
    bookName      NVARCHAR(200) NULL,
    borrow_date   DATE          NOT NULL,
    return_date   DATE          NULL,
    isDue         BIT           NOT NULL DEFAULT 0,
    isReturned    BIT           NOT NULL DEFAULT 0
);
GO

-- SQL login used by the app. CHECK_POLICY is off because "root" is too short
-- for the Windows password policy.
IF SUSER_ID('root') IS NULL
    CREATE LOGIN root WITH PASSWORD = 'root', CHECK_POLICY = OFF, DEFAULT_DATABASE = Library;
GO

IF USER_ID('root') IS NULL
    CREATE USER root FOR LOGIN root;
GO
ALTER ROLE db_datareader ADD MEMBER root;
ALTER ROLE db_datawriter ADD MEMBER root;
GO

-- Starting CEO account (only a CEO can register users from inside the app).
-- Log in with admin / admin, then change it by registering your own CEO.
IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE Username = 'admin')
INSERT INTO dbo.users (Name, Lastname, Username, Gender, Email, Password, Position)
VALUES ('Admin', 'User', 'admin', 'Male', 'admin@library.local',
        LOWER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', 'admin'), 2)), 'CEO');
GO
