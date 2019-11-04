package study.wf.klaxon_ex

import com.beust.klaxon.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

data class Person(val name: String, val age: Int? = null)

data class PersonWithDefault(val name: String, val age: Int = 23)

data class PersonDifferentProperty(
        @Json(name = "name")
        val theName: String,
        val age: Int? = null
)

class ParsePersonJsonTest {
    @Test
    fun `json to Person - deserialize`() {
        var json = """
            {
                "name": "John Smith"
            }
        """.trimIndent()

        println(json)
        var klaxon = Klaxon()

        val person = klaxon.parse<Person>(json)
        assert(person?.name == "John Smith")
        assert(person?.age == null)
        println("person: $person")

        val personWithDefault = klaxon.parse<PersonWithDefault>(json)
        assert(personWithDefault?.name == "John Smith")
        assert(personWithDefault?.age == 23)
        println("personWithDefault: $personWithDefault")
    }

    @Test
    fun `json to Person - deserialize - 다른 프로퍼티이름으로`() {
        var json = """
            {
                "name": "John Smith",
                "age": 21
            }
        """.trimIndent()

        val klaxon = Klaxon()
        val person = klaxon.parse<PersonDifferentProperty>(json)
        assert(person?.theName == "John Smith")
        assert(person?.age == 21)
    }

    data class Ignored(
            val name: String,
            @Json(ignored = true)
            val actualName: String? = null
    )

    @Test
    fun `json to Ignored property`() {
        val json = """
            {
                "name": "John Smith",
                "actualName": "Kill Bill"
            }
        """.trimIndent()

        val ignored = Klaxon().parse<Ignored>(json)
        println("Ignored: $ignored")
        assert(ignored?.name == "John Smith")
        assert(ignored?.actualName == null)
    }

    @Test
    fun `object to json`() {
        val person = Person(name = "John Smith", age = 22)
        val jsonString = Klaxon().toJsonString(person)
        println("jsonString: $jsonString")
        assert(jsonString == """{"age" : 22, "name" : "John Smith"}""")
    }

    @Test
    fun `json to JsonObject`() {
        val jsonString = """
            |{
            |   "cpu": "i7 9999",
            |   "GHz": 4.99,
            |   "ram": "16GB",
            |   "ramSlots": ["1GB", "2GB", "4GB", "8GB"],
            |   "hasWifi": false,
            |   "powerWatts": 500,
            |   "printer": {
            |      "brand": "StarTech",
            |      "colorSupport": false,
            |      "paperSize": "A4"
            |   }
            |}
        """.trimMargin("|")

        val parser = Parser.default()
        val json = parser.parse(StringBuilder(jsonString)) as JsonObject

        assertEquals(json.string("cpu"), "i7 9999")
        assertEquals(json.double("GHz"), 4.99)
        assertEquals(json.string("ram"), "16GB")
        val jsonArray = json.array<String>("ramSlots")
        assertEquals(jsonArray?.size, 4)
        assert((listOf("1GB", "2GB", "4GB", "8GB") == (jsonArray?.value ?: emptyList<String>())))
        assert(setOf("1GB", "2GB", "4GB", "8GB") == jsonArray?.toSet())
    }

    @Test
    fun `make JsonObject from map`() {
        val mutableMap: MutableMap<String, Any?> = mutableMapOf(
                "cpu" to "i7 9999",
                "Ghz" to 4.99,
                "ram" to "16GB"
        )
        val root = JsonObject(mutableMap)
        JsonArray("1", "2")
        root["ramSlots"] = listOf("1GB", "2GB", "4GB", "8GB")
        root["hasWifi"] = false
        println(root.toJsonString(prettyPrint = true))
        println(root.map)
        println(root.toMap())
        println(root.string("cpu"))
        println(root.int("cpuGhz"))
        println(root.safeInt("cpu"))
        println(root.safeInt("cpu", 0))

        /*
        JsonObject 자체로 MutableMap 이다 - jsonObj.map or jsonObj.toMap() 으로 반환 가능. map["key"] 형태의 연산 가능
        값을 put 하는 것은 map 방식과 동일하게 사용해야 하며, 타입을 지정할 수 없다(Any? 사용).
        JSON string 으로 부터 object나 JsonObject로 쉽게 변환할 수 있다.
        JsonObject에서 원하는 타입으로 값을 꺼낼 수 있다. 단, Type Safe 하지 않으므로 ClassCastException이 발생할 수 있다. 확장함수를 정의해서 사용하면 좋을까?
         */
    }

    fun JsonObject.safeInt(fieldName: String, default: Int? = null): Int? {
        return try {
            int(fieldName)
        } catch (e: Exception) {
            default
        }
    }
}
