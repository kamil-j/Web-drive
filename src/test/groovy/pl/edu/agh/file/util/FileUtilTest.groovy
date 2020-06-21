package pl.edu.agh.file.util

import spock.lang.Specification

class FileUtilTest extends Specification {

    def "should description be valid when empty"() {
        given:
        String description = ""

        expect:
        !FileUtil.isNotValidDescription(description)
    }

    def "should description be valid when null"() {
        given:
        String description = null

        expect:
        !FileUtil.isNotValidDescription(description)
    }

    def "should description be valid when length lower than 20"() {
        given:
        String description = "mnbvcxz"

        expect:
        !FileUtil.isNotValidDescription(description)
    }

    def "should description be valid when length equal to 20"() {
        given:
        String description = "123456789abcdefghijk"

        expect:
        !FileUtil.isNotValidDescription(description)
    }

    def "should description be not valid when length greater than 20"() {
        given:
        String description = "123456789abcdefghijkw"

        expect:
        FileUtil.isNotValidDescription(description)
    }
}
