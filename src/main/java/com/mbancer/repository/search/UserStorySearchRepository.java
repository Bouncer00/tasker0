package com.mbancer.repository.search;

import com.mbancer.domain.UserStory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserStorySearchRepository extends ElasticsearchRepository<UserStory, Long> {
}
