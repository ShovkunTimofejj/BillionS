package org.app.billions.data

import org.app.billions.data.DatabaseDriverFactory

class Database(private val databaseDriverFactory: DatabaseDriverFactory) {

    private val database = BillionS.Companion(databaseDriverFactory.createDriver())
    private val dbQuery = database }