package com.smartlearn.repository;

import com.smartlearn.model.Announcement;
import com.smartlearn.model.AnnouncementPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Defines data access for announcements.
 * This interface specializes the shared generic repository for announcement records.
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    @Query("""
            select a from Announcement a
            where exists (
                select e.id from Enrollment e
                where e.student.id = :studentId
                  and e.section.course is not null
                  and (a.targetCourse is null or a.targetCourse.id = e.section.course.id)
                  and (a.targetSection is null or a.targetSection.id = e.section.id)
                  and (a.targetSemester is null or a.targetSemester = e.section.semester)
            )
            order by case
                when a.priority = :high then 0
                when a.priority = :medium then 1
                when a.priority = :low then 2
                else 3
            end,
            a.publishedAt desc
            """)
    List<Announcement> findRelevantByStudentId(@Param("studentId") Long studentId,
                                               @Param("high") AnnouncementPriority high,
                                               @Param("medium") AnnouncementPriority medium,
                                               @Param("low") AnnouncementPriority low);
}
