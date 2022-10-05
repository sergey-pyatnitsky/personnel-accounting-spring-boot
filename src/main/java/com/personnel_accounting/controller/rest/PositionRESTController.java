package com.personnel_accounting.controller.rest;

import com.personnel_accounting.entity.domain.Position;
import com.personnel_accounting.entity.dto.PositionDTO;
import com.personnel_accounting.entity.pagination.DataTablePagingRequest;
import com.personnel_accounting.exception.ExistingDataException;
import com.personnel_accounting.exception.NoSuchDataException;
import com.personnel_accounting.mapper.PositionMapper;
import com.personnel_accounting.service.department.DepartmentService;
import com.personnel_accounting.utils.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/position")
public class PositionRESTController {

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/add")
    public ResponseEntity<?> addPosition(@Valid @RequestBody PositionDTO positionDTO) {
        Position position = departmentService.addPosition(positionMapper.toModal(positionDTO));
        if (position.getId() == null)
            throw new ExistingDataException(
                    messageSource.getMessage("position.alert.add", null, LocaleContextHolder.getLocale()));
        return new ResponseEntity<>(position, HttpStatus.OK);
    }

    @PostMapping("/get_all/search-sorting")
    public ResponseEntity<?> getPositionsWithSearchSorting(@RequestBody DataTablePagingRequest pagingRequest) {
        pagingRequest.setStart(0);
        pagingRequest.setLength(100);
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        departmentService.findAllPositionsWithPaginationAnsSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(positionMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/get_all")
    public ResponseEntity<?> getPositionsWithPagination(@RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromListResponse(
                        departmentService.findAllPositions(
                                        PaginationUtil.getSortFromRequest(pagingRequest)
                                ).stream()
                                .map(positionMapper::toDto).collect(Collectors.toList()),
                        pagingRequest.getDraw()
                )
        );
    }

    @GetMapping("/get_all")
    public ResponseEntity<?> getPositions() {
        List<Position> positions = departmentService.findAllPositions();
        if (positions.size() == 0)
            throw new NoSuchDataException(
                    messageSource.getMessage("position.error.list.empty", null, LocaleContextHolder.getLocale()));
        return new ResponseEntity<>(positions.stream().map(position ->
                positionMapper.toDto(position)).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editPosition(@Valid @RequestBody PositionDTO positionDTO, @PathVariable Long id) {
        positionDTO.setId(id);
        return ResponseEntity.ok(departmentService.editPosition(positionMapper.toModal(positionDTO)));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removePosition(@PathVariable Long id) {
        departmentService.removePositionById(id);
        return ResponseEntity.ok().build();
    }
}
