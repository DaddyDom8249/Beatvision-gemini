package com.example.data.local

import com.example.data.model.DemoProjectData
import com.example.data.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(private val projectDao: ProjectDao) {

    fun getSavedProjects(): Flow<List<Project>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getProject(id: String): Project? {
        if (id == DemoProjectData.drainRackHaloProject.id) {
            val fromDb = projectDao.getProjectById(id)?.toDomain()
            return fromDb ?: DemoProjectData.drainRackHaloProject
        }
        return projectDao.getProjectById(id)?.toDomain()
    }

    suspend fun saveProject(project: Project) {
        val updated = project.copy(updatedAt = System.currentTimeMillis())
        projectDao.insertOrUpdate(ProjectEntity.fromDomain(updated))
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteProjectById(id)
    }

    suspend fun loadOrInitDemo(): Project {
        val existing = projectDao.getProjectById(DemoProjectData.drainRackHaloProject.id)
        if (existing == null) {
            val demo = DemoProjectData.drainRackHaloProject
            projectDao.insertOrUpdate(ProjectEntity.fromDomain(demo))
            return demo
        }
        return existing.toDomain()
    }
}
