package tech.challenge.release_manager

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.challenge.release_manager.domain.DeploymentService
import tech.challenge.release_manager.domain.exception.SystemVersionNotFoundException
import tech.challenge.release_manager.persistence.ServiceDeploymentRepository
import tech.challenge.release_manager.persistence.SystemVersionRepository
import tech.challenge.release_manager.persistence.entity.ServiceDeploymentEntity
import tech.challenge.release_manager.persistence.entity.ServiceDeploymentId
import tech.challenge.release_manager.persistence.entity.SystemVersionEntity

class DeploymentServiceTest {
    private val systemVersionRepository = mockk<SystemVersionRepository>(relaxed = true)
    private val serviceDeploymentRepository = mockk<ServiceDeploymentRepository>(relaxed = true)

    private val deploymentService = DeploymentService(systemVersionRepository, serviceDeploymentRepository)

    @BeforeEach
    fun setUp() {
        every { systemVersionRepository.save(any<SystemVersionEntity>()) } answers { firstArg() }
        every { serviceDeploymentRepository.save(any<ServiceDeploymentEntity>()) } answers { firstArg() }
    }

    @Test
    fun `deploy on empty database returns system version 1 and saves it`() {

        // Given
        givenCurrentSystemVersion(null)
        givenNoDeployment("Service 1")

        // When
        val result = deploymentService.deploy("Service A", 1)

        // Then
        assertThat(result).isEqualTo(1)

        verify(exactly = 1) {
            systemVersionRepository.save(match { it.id == 1 })
        }
        verify(exactly = 1) {
            serviceDeploymentRepository.save(
                match { it.version == 1 && it.id.serviceName == "Service A" && it.id.systemVersion == 1 }
            )
        }
    }

    @Test
    fun `deploy with a changed version creates new system version`() {
        givenCurrentSystemVersion(1)
        givenLatestDeployment("Service A", 1, 1)

        val result = deploymentService.deploy("Service A", 2)

        assertThat(result).isEqualTo(2)
        verify(exactly = 1) {
            serviceDeploymentRepository.save(
                match { it.version == 2 && it.id.serviceName == "Service A" && it.id.systemVersion == 2 }
            )
        }
    }

    @Test
    fun `deploy with the same version returns the current system version`() {
        givenCurrentSystemVersion(3)
        givenLatestDeployment("Service B", 2, 1)

        val result = deploymentService.deploy("Service B", 1)

        assertThat(result).isEqualTo(3)
        verify(exactly = 0) { serviceDeploymentRepository.save(any()) }
        verify(exactly = 0) { systemVersionRepository.save(any()) }
    }

    @Test
    fun `deploy with and older version create a new system version`() {
        givenCurrentSystemVersion(3)
        givenLatestDeployment("Service B", 2, 2)

        val result = deploymentService.deploy("Service B", 1)

        assertThat(result).isEqualTo(4)
        verify(exactly = 1) {
            serviceDeploymentRepository.save(
                match { it.version == 1 && it.id.serviceName == "Service B" && it.id.systemVersion == 4 }
            )
        }
    }

    @Test
    fun `getServicesAt() returns services deployed until that point`() {
        givenCurrentSystemVersion(3)
        givenPresentDeploymentsAt(3)

        var result = deploymentService.getServicesAt(3)

        assertThat(result)
            .extracting("name", "version")
            .containsExactlyInAnyOrder(
                tuple("Service A", 2),
                tuple("Service B", 1)
            )
    }

    @Test
    fun `getServicesAt() throws when system version does not exist`() {
        givenCurrentSystemVersion(3)

        AssertionsForClassTypes.assertThatThrownBy {
            deploymentService.getServicesAt(4)
        }.isInstanceOf(SystemVersionNotFoundException::class.java)
    }

    private fun givenCurrentSystemVersion(systemVersion: Int?) {
        every { systemVersionRepository.findCurrentVersion() } returns systemVersion
    }

    private fun givenLatestDeployment(serviceName: String, systemVersion: Int, version: Int) {
        every {
            serviceDeploymentRepository.findTopByIdServiceNameOrderByIdSystemVersionDesc(serviceName)
        } returns ServiceDeploymentEntity(
            ServiceDeploymentId(
                systemVersion,
                serviceName
            ),
            version
        )
    }

    private fun givenPresentDeploymentsAt(systemVersion: Int) {
        every {
            serviceDeploymentRepository.findServicesAt(systemVersion)
        } returns listOf(
            ServiceDeploymentEntity(ServiceDeploymentId(3, "Service A"), 2),
            ServiceDeploymentEntity(ServiceDeploymentId(2, "Service B"), 1)
        )
    }

    private fun givenNoDeployment(serviceName: String) {
        every {
            serviceDeploymentRepository.findTopByIdServiceNameOrderByIdSystemVersionDesc(serviceName)
        } returns null
    }
}