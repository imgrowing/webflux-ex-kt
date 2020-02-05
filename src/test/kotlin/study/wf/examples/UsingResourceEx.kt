package study.wf.examples

import reactor.core.publisher.Flux
import reactor.util.Loggers
import study.wf.subscribeAndPrint
import java.util.*

class Connection() : AutoCloseable {

    private val log = Loggers.getLogger(this.javaClass)
    private val random: Random = Random()

    override fun close() {
        log.info("IO Connection closed")
    }

    fun getData(): Iterable<String> =
            if (random.nextInt(10) < 3)
                throw RuntimeException("Communication error")
            else
                listOf("Some", "data")

    companion object {
        fun newConnection() : Connection {
            return Connection()
        }
    }
}

class ImperativeEx {

    companion object {
        private val log = Loggers.getLogger(this.javaClass)

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                Connection.newConnection()
                        .use { conn -> conn.getData().forEach {
                                log.info("Received data: $it")
                            }
                        }
            } catch (e: Exception) {
                log.info("Error: ${e.message}")
            }
        }
        /*
        (정상)
        Received data: Some
        Received data: data
        IO Connection closed

        (에러)
        IO Connection closed
        Error: Communication error
        */
    }
}

class UsingResourceEx {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val ioRequestResults = Flux
                    .using(
                            { Connection.newConnection() },                             // resource supplier
                            { conn -> Flux.fromIterable(conn.getData()) }, // source supplier
                            Connection::close                                           // resource cleanup consumer
                    )

            ioRequestResults.subscribeAndPrint()
            /*
            (정상)
            onNext -> Some
            onNext -> data
            IO Connection closed
            onComplete

            (에러)
            IO Connection closed
            onError -> java.lang.RuntimeException: Communication error
            */
        }
    }
}
