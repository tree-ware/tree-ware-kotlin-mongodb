package org.treeWare.mongoDb

import com.mongodb.client.MongoClients
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestMongoDbTests {
    @BeforeTest
    fun beforeTest() {
        TestMongoDb.start()
    }

    @AfterTest
    fun afterTest() {
        TestMongoDb.stop()
    }

    @Test
    fun `TestMongoDb must be accessible`() {
        val mongo = MongoClients.create(TestMongoDb.uri)
        val databasesNames = mongo.listDatabaseNames().toList()
        // NOTE: a "config" database is in the list occasionally.
        // So the entire list is not asserted to avoid flakiness.
        assertTrue(databasesNames.contains("admin"))
        assertTrue(databasesNames.contains("local"))
    }
}
