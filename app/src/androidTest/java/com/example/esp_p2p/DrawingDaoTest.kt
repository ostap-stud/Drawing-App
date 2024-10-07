package com.example.esp_p2p

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.esp_p2p.data.room.AppDatabase
import com.example.esp_p2p.data.room.Drawing
import com.example.esp_p2p.data.room.DrawingDAO
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawingDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var drawingDao: DrawingDAO

    @Before
    fun setupDatabase(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        drawingDao = db.drawingDAO()
    }

    @After
    fun closeDatabase(){
        db.close()
    }

    @Test
    fun insertDrawing_True() = runTest{
        val newDrawing = Drawing(
            title = "New Drawing",
            fieldSize = 16,
            fieldScale = 50f
        )
        drawingDao.insert(newDrawing)
        val receiveChannel = drawingDao.getAllDrawings().produceIn(this)
        assert(receiveChannel.receive().find { it.title == newDrawing.title } != null)
        receiveChannel.cancel()
    }

    @Test
    fun updateDrawing_True() = runTest{
        val drawing = Drawing(
            id = 0,
            title = "Initial Drawing",
            fieldSize = 16,
            fieldScale = 50f
        )
        drawingDao.insert(drawing)

        val updatedDrawing = Drawing(
            id = 0,
            title = "Updated Drawing"
        )

        drawingDao.update(updatedDrawing)
        val result = drawingDao.getDrawingById(updatedDrawing.id!!)

        assert(updatedDrawing.title == result.title)
    }

    @Test
    fun deleteDrawing_True() = runTest{
        val firstDrawing = Drawing(
            id = 0,
            title = "First Drawing",
            fieldSize = 16,
            fieldScale = 50f
        )
        val secondDrawing = Drawing(
            id = 1,
            title = "Second Drawing",
            fieldSize = 16,
            fieldScale = 50f
        )
        drawingDao.insert(firstDrawing)
        drawingDao.insert(secondDrawing)

        drawingDao.delete(firstDrawing)

        val receiveChannel = drawingDao.getAllDrawings().produceIn(this)
        assert(receiveChannel.receive().find { it.title == firstDrawing.title } == null)
        receiveChannel.cancel()
    }
}