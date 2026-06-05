package org.example.project

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.project.database.NexoraDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbExists = java.io.File("nexora.db").exists()
        val driver = JdbcSqliteDriver("jdbc:sqlite:nexora.db")
        if (!dbExists) {
            NexoraDatabase.Schema.create(driver)
        }
        return driver
    }
}

actual fun createDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()