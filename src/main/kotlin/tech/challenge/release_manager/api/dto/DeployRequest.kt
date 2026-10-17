package tech.challenge.release_manager.api.dto

import jakarta.validation.constraints.*

data class DeployRequest(
    @field:NotBlank
    val name: String,

    @field:Positive
    val version: Int
)