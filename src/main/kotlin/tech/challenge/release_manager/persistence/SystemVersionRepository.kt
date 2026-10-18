package tech.challenge.release_manager.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import tech.challenge.release_manager.persistence.entity.SystemVersionEntity

@Repository
interface SystemVersionRepository : JpaRepository<SystemVersionEntity, Int> {

    @Query("SELECT MAX(s.id) FROM SystemVersionEntity s")
    fun findCurrentVersion(): Int?

    @Modifying
    @Query("LOCK TABLE system_version IN EXCLUSIVE MODE", nativeQuery = true)
    fun lockTable()
}