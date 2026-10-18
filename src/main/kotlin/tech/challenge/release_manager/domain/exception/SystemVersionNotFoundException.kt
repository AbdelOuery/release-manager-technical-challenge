package tech.challenge.release_manager.domain.exception

class SystemVersionNotFoundException(systemVersion: Int) :
    RuntimeException("System version $systemVersion does not exist")