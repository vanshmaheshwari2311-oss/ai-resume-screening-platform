package com.vansh.resume_screening;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatusIgnoreCaseOrderByIdDesc(String status);
}
