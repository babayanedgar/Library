-- One-time migration for databases created before the employees -> users rename.
-- Renames the table, changes the 'Employee' position to 'User', and keeps all data.
-- Safe to re-run: it does nothing once dbo.users exists. New installs only need setup.sql.

USE Library;
GO

SET XACT_ABORT ON;

IF OBJECT_ID('dbo.employees', 'U') IS NOT NULL AND OBJECT_ID('dbo.users', 'U') IS NULL
BEGIN
    BEGIN TRANSACTION;

    -- The old constraints have auto-generated names, so look them up
    DECLARE @check SYSNAME = (SELECT name FROM sys.check_constraints
                              WHERE parent_object_id = OBJECT_ID('dbo.employees'));
    DECLARE @pk SYSNAME = (SELECT name FROM sys.key_constraints
                           WHERE parent_object_id = OBJECT_ID('dbo.employees') AND type = 'PK');
    DECLARE @uq SYSNAME = (SELECT name FROM sys.key_constraints
                           WHERE parent_object_id = OBJECT_ID('dbo.employees') AND type = 'UQ');
    DECLARE @old NVARCHAR(300);

    IF @check IS NOT NULL
    BEGIN
        DECLARE @drop NVARCHAR(400) = N'ALTER TABLE dbo.employees DROP CONSTRAINT ' + QUOTENAME(@check);
        EXEC sp_executesql @drop;
    END

    UPDATE dbo.employees SET Position = 'User' WHERE Position = 'Employee';

    EXEC sp_rename 'dbo.employees', 'users';

    SET @old = N'dbo.' + QUOTENAME(@pk);
    EXEC sp_rename @old, 'PK_users', 'OBJECT';
    SET @old = N'dbo.' + QUOTENAME(@uq);
    EXEC sp_rename @old, 'UQ_users_Username', 'OBJECT';

    EXEC sp_executesql N'ALTER TABLE dbo.users ADD CONSTRAINT CK_users_Position CHECK (Position IN (''CEO'', ''User''))';

    COMMIT;
    PRINT 'Renamed dbo.employees to dbo.users.';
END
ELSE
    PRINT 'Nothing to do.';
GO
