package com.hmdp.mapper;

import com.hmdp.entity.BlogComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCommentsRepository extends JpaRepository<BlogComments, Long>, JpaSpecificationExecutor<BlogComments> {

}
