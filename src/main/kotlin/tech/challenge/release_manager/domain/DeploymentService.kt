package tech.challenge.release_manager.domain

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import tech.challenge.release_manager.api.dto.ServiceVersion
import tech.challenge.release_manager.domain.exception.SystemVersionNotFoundException
import tech.challenge.release_manager.persistence.ServiceDeploymentRepository
import tech.challenge.release_manager.persistence.SystemVersionRepository
import tech.challenge.release_manager.persistence.entity.ServiceDeploymentEntity
import tech.challenge.release_manager.persistence.entity.ServiceDeploymentId
import tech.challenge.release_manager.persistence.entity.SystemVersionEntity
import java.time.Instant

@Service
class DeploymentService(
    val systemVersionRepository: SystemVersionRepository,
    val serviceDeploymentRepository: ServiceDeploymentRepository
) {

    @Transactional
    fun deploy(serviceName: String, serviceVersion: Int): Int {
        val currentSystemVersion = systemVersionRepository.findCurrentVersion() ?: 0

        // Check if service exists
        val serviceDeployment = serviceDeploymentRepository.findTopByIdServiceNameOrderByIdSystemVersionDesc(serviceName)

        // If it does, nothing happened
        if (serviceDeployment != null && serviceDeployment.version == serviceVersion) {
            return currentSystemVersion
        }

        // A change happened, we increment the system's version and we save the changelog
        val next = currentSystemVersion + 1
        systemVersionRepository.save(SystemVersionEntity(next, Instant.now()))
        serviceDeploymentRepository.save(ServiceDeploymentEntity(ServiceDeploymentId(next, serviceName), serviceVersion))
        return next
    }

    fun getServicesAt(systemVersion: Int): List<ServiceVersion> {
        val currentSystemVersion = systemVersionRepository.findCurrentVersion() ?: 0
        if (currentSystemVersion < 1 || systemVersion > currentSystemVersion) {
            throw SystemVersionNotFoundException(systemVersion)
        }

        return serviceDeploymentRepository.findServicesAt(systemVersion).map {
            ServiceVersion(it.id.serviceName, it.version)
        }
    }
}