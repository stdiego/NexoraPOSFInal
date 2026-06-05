package org.example.project

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.example.project.database.NexoraDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(NexoraDatabase.Schema, NexoraApp.appContext, "nexora.db")
    }
}

actual fun createDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()