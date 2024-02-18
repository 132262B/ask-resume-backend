package app.askresume.api.job.mapper

import app.askresume.api.job.vo.JobResponse
import app.askresume.domain.job.dto.JobDto

object JobMapper {

    fun jobResponseListOf(
        jobDtos: List<JobDto>
    ): List<JobResponse> {
        return jobDtos.map { dto ->
            JobResponse(
                id = dto.id,
                name = dto.name
            )
        }
    }

}