package tech.challenge.release_manager.persistence

import org.springframework.data.jpa.repository.JpaRepository
import tech.challenge.release_manager.persistence.entity.ServiceDeploymentEntity

interface ServiceDeploymentRepository : JpaRepository<ServiceDeploymentEntity, Int> {

}