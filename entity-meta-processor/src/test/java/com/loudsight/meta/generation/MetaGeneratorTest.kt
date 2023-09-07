package com.loudsight.meta.generation

//import com.loudsight.meta.MetaRepository
//import com.loudsight.meta.entity.ItemType
//import com.loudsight.meta.entity.SimpleEntity
//import com.loudsight.meta.entity.SimpleEntityMeta
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetaGeneratorTest {

//    companion object {
//        init {
//            MetaRepository.register(SimpleEntityMeta)
//        }
//    }
//
//    @Test
//    fun createUsingNoargs() {
//
//        val meta = MetaRepository.getMeta(SimpleEntity::class)
//
//        val a = meta!!.newInstance()
//        assertTrue(a::class == SimpleEntity::class)
//    }
//
//    @Test
//    fun createSimpleEntityWithAllArgs() {
//
//        val meta = MetaRepository.getMeta(SimpleEntity::class)!!
//        val expected = SimpleEntity()
//        expected.field1 = 5
//        expected.itemType = ItemType.PARENT
//        expected.id = 100L
//        expected.isField4 = true
//        expected.name = "SimpleEntity"
//
//        val objectMap = meta.toMap(expected)
//
//        val actual = meta.newInstance(objectMap)
//        assertTrue(actual::class == SimpleEntity::class)
//        assertEquals(expected, actual, "Expect to be able to create an entity from a map")
//    }
//
//    @Test
//    fun createSimpleEntityWithMissingArgs() {
//
//        val meta = MetaRepository.getMeta(SimpleEntity::class)!!
//        val expected = SimpleEntity()
//        expected.field1 = 5
//        expected.itemType = ItemType.PARENT
//        expected.id = 100L
//        expected.isField4 = true
//        expected.name = null
//
//        val objectMap = meta.toMap(expected)
//        assertFalse(objectMap.containsKey("name"), "Name is null but was included in the objectMap")
//
//        val actual = meta.newInstance(objectMap)
//        assertTrue(actual::class == SimpleEntity::class)
//        assertEquals(expected, actual, "Expect to be able to create an entity from a map")
//    }
//
}