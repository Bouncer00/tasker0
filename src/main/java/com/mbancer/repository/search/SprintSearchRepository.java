package com.mbancer.repository.search;

import com.mbancer.domain.Sprint;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SprintSearchRepository extends ElasticsearchRepository<Sprint, Long> {
}
