package net.starkenberg.logging.elastic.web

import net.starkenberg.logging.elastic.schedule.ScheduledMethods
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * @author Brad Starkenberg
 */
@RestController
@RequestMapping("/api")
class MainController(private val service: ScheduledMethods) {

    val indicesThatNeedDeleted: ResponseEntity<List<String>>
        @RequestMapping(method = arrayOf(RequestMethod.GET), path = arrayOf("/indices"))
        get() = ResponseEntity(service.indicesToDelete, HttpStatus.OK)

    @RequestMapping(method = arrayOf(RequestMethod.DELETE), path = arrayOf("/indices"))
    fun deleteOldIndices(): ResponseEntity<String> {
        service.deleteIndices()
        return ResponseEntity(HttpStatus.OK)
    }

    @RequestMapping(method = arrayOf(RequestMethod.DELETE), path = arrayOf("/indices/{index}"))
    fun deleteAnIndex(@PathVariable index: String): ResponseEntity<String> {
        service.deleteAnIndex(index)
        return ResponseEntity(HttpStatus.OK)
    }
}
