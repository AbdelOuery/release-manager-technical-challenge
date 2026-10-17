package tech.challenge.release_manager.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.challenge.release_manager.persistence.entity.ServiceDeploymentEntity

@Repository
interface ServiceDeploymentRepository : JpaRepository<ServiceDeploymentEntity, Int> {

    fun findTopByIdServiceNameOrderByIdSystemVersionDesc(serviceName: String) : ServiceDeploymentEntity?
}