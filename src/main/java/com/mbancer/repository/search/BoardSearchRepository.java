package com.mbancer.repository.search;

import com.mbancer.domain.Board;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BoardSearchRepository extends ElasticsearchRepository<Board, Long> {
}
