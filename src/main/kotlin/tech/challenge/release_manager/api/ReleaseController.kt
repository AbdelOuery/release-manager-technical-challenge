package tech.challenge.release_manager.api

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tech.challenge.release_manager.api.dto.DeployRequest
import tech.challenge.release_manager.domain.ReleaseService

@RestController
class ReleaseController(private val releaseService: ReleaseService) {

    @PostMapping("deploy")
    fun deploy(@Valid @RequestBody deployRequest: DeployRequest): Int {
        return 1
    }

    @GetMapping("services")
    fun services(@RequestParam("systemVersion") systemVersion: Int): String {
        return "$systemVersion"
    }
}