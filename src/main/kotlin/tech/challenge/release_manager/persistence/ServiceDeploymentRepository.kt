package tech.challenge.release_manager.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import tech.challenge.release_manager.persistence.entity.ServiceDeploymentEntity

@Repository
interface ServiceDeploymentRepository : JpaRepository<ServiceDeploymentEntity, Int> {

    fun findTopByIdServiceNameOrderByIdSystemVersionDesc(serviceName: String) : ServiceDeploymentEntity?

    @Query(
        value = """
            select distinct on (service_name) *
            from service_deployment
            where system_version <= :systemVersion
            order by service_name, system_version desc
        """,
        nativeQuery = true)
    fun findServicesAt(@Param("systemVersion") systemVersion: Int): List<ServiceDeploymentEntity>
}