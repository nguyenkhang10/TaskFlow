package com.example.taskflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.taskflow.domain.Project;
import com.example.taskflow.domain.ProjectStatus;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ProjectMapper {
    public int insert(Project project);
    Optional<Project> findById(@Param("id") Long id);
    List<Project> search(@Param("status") ProjectStatus status,
                         @Param("keyword") String keyword);
    int update(Project project);
    int deleteById(@Param("id") Long id);

}
