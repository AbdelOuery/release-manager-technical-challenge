package tech.challenge.release_manager.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.io.Serializable

@Embeddable
data class ServiceDeploymentId(
    @Column(name = "system_version", nullable = false)
    val systemVersion: Int,

    @Column(name = "service_name", nullable = false)
    val serviceName: String,
) : Serializable

@Entity
@Table(name = "service_deployment")
class ServiceDeploymentEntity(
    @EmbeddedId
    val id: ServiceDeploymentId,

    @Column(nullable = false)
    val version: Int
)