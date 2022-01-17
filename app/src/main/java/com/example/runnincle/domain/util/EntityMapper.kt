package com.example.runnincle.domain.util

interface EntityMapper <Entity, DomainModel>{
    fun mapFromEntity(entity: Entity): DomainModel
    fun mapFromDomainModel(domainModel: DomainModel): Entity
}