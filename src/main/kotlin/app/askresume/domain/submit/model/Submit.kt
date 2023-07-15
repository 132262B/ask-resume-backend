package app.askresume.domain.submit.model

import app.askresume.domain.common.BaseTimeEntity
import app.askresume.domain.submit.constant.ServiceType
import app.askresume.domain.submit.constant.SubmitStatus
import org.hibernate.annotations.Comment
import org.hibernate.annotations.Where
import javax.persistence.*

@Where(clause = "is_deleted = 'Y'")
@Entity
class Submit(
    title: String,
    serviceType: ServiceType,
    dataCount: Int,
) : BaseTimeEntity() {

    @Comment("제목")
    @Column(nullable = false, length = 50)
    var title: String = title
        protected set

    @Enumerated(EnumType.STRING)
    @Comment("서비스 타입")
    @Column(nullable = false, length = 40)
    var serviceType: ServiceType = serviceType
        protected set

    @Enumerated(EnumType.STRING)
    @Comment("상태")
    @Column(name = "status", nullable = false, length = 30)
    var submitStatus: SubmitStatus = SubmitStatus.WAITING
        protected set

    @Comment("시도 횟수")
    @Column(nullable = false)
    var attempts: Int = 0
        protected set

    @Comment("데이터 개수")
    @Column(nullable = false)
    var dataCount: Int = dataCount
        protected set

    /** 비즈니스 **/

    fun updateStatus(changeStatus: SubmitStatus) {
        this.submitStatus = changeStatus
    }

    fun plusAttempts() {
        this.attempts++
    }
}