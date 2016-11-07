package com.mbancer.web.rest.mapper;

import com.mbancer.domain.Board;
import com.mbancer.domain.Project;
import com.mbancer.web.rest.dto.BoardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProjectMapper.class)
public interface BoardMapper {

    @Mapping(source = "project.id", target = "projectId")
    BoardDTO boardToBoardDTO(Board board);

    List<BoardDTO> boardsToBoardDTOs(List<Board> boards);

    @Mapping(source = "projectId", target = "project")
    Board boardDTOToBoard(BoardDTO boardDTO);

    List<Board> boardDTOsToBoards(List<BoardDTO> boardDTOs);

}
