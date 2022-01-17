package com.example.runnincle.repository.abstraction

import com.example.runnincle.domain.model.Program

interface ProgramRepository {
    suspend fun getAllPrograms(): List<Program>
    suspend fun get(id: Int): Program
}