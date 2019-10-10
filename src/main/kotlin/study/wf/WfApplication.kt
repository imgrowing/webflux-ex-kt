package study.wf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WfApplication

fun main(args: Array<String>) {
    runApplication<WfApplication>(*args)
}
