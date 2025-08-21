-- Criação do banco de dados e usuário hack
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'hack')
BEGIN
    CREATE DATABASE hack;
END
GO

USE hack;
GO

IF NOT EXISTS (SELECT name FROM sys.sql_logins WHERE name = N'hack')
BEGIN
    CREATE LOGIN hack WITH PASSWORD = 'Password23';
END
GO

IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = N'hack')
BEGIN
    CREATE USER hack FOR LOGIN hack;
    ALTER ROLE db_owner ADD MEMBER hack;
END
GO

-- Executa o script de produtos
:r /var/opt/mssql/scripts/init-produto.sql

