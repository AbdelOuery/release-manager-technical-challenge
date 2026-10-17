package tech.challenge.release_manager.persistence

import org.springframework.data.jpa.repository.JpaRepository
import tech.challenge.release_manager.persistence.entity.SystemVersionEntity

interface SystemVersionRepository : JpaRepository<SystemVersionEntity, Int> {

}