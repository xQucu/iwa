package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.models.Grade;
import com.example.demo.models.User;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent(User student);

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findBySubjectId(Long subjectId);
}
