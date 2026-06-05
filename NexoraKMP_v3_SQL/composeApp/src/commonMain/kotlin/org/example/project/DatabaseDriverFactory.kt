package org.example.project

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

expect fun createDriverFactory(): DatabaseDriverFactory