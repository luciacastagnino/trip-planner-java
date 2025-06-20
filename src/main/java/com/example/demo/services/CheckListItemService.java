package com.example.demo.services;

import com.example.demo.DTOs.CheckList.Request.CheckListItemCreateDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListItemUpdateDTO;
import com.example.demo.DTOs.Filter.CheckListItemFilterDTO;
import com.example.demo.entities.CheckListEntity;
import com.example.demo.entities.CheckListItemEntity;
import com.example.demo.mappers.CheckListItemMapper;
import com.example.demo.repositories.CheckListItemRepository;
import com.example.demo.repositories.CheckListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CheckListItemService {

    private final CheckListItemRepository itemRepository;
    private final CheckListItemMapper itemMapper;
    private final CheckListRepository checkListRepository;


    public Page<CheckListItemResponseDTO> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(itemMapper::toDTO);
    }

    public Optional<CheckListItemResponseDTO> findById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toDTO);
    }

    public CheckListItemResponseDTO findByIdIfOwned(Long id, Long userId) {
        CheckListItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found."));

        if (!entity.getChecklist().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to view this item.");
        }

        return itemMapper.toDTO(entity);
    }

    public Page<CheckListItemResponseDTO> findByUserId(Long userId, Long checklistId, boolean completed, Pageable pageable) {
        Page<CheckListItemEntity> checkListItem;
        if(checklistId != null){
            checkListItem = itemRepository.findByChecklistIdAndChecklistUserId(checklistId, userId, pageable);
        }
        else if(checklistId != null && completed == false){
            checkListItem = itemRepository.findByChecklistIdAndStatusAndChecklistUserId(checklistId, userId, completed, pageable);
        }
        else if(completed == false){
            checkListItem = itemRepository.findByStatusAndChecklistUserId(completed,userId, pageable);
        }
        else {
            checkListItem = itemRepository.findByChecklistUserId(userId, pageable);
        }
        return checkListItem.map(itemMapper::toDTO);
    }


    public CheckListItemResponseDTO create(CheckListItemCreateDTO dto, Long userId) {

        CheckListItemEntity entity = itemMapper.toEntity(dto);

        CheckListEntity checklist = checkListRepository.findById(dto.getChecklistId())
                .orElseThrow(() -> new NoSuchElementException("Checklist not found."));

        if (!checklist.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to add items at this checklist.");
        }

        entity.setChecklist(checklist);
        entity.setStatus(false);

        return itemMapper.toDTO(itemRepository.save(entity));
    }


    public CheckListItemResponseDTO updateIfOwned(Long id, CheckListItemUpdateDTO dto, Long userId) {
        CheckListItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found."));

        if (!entity.getChecklist().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to modify this item.");
        }

        itemMapper.updateEntityFromDTO(dto, entity);

        if (dto.getChecklistId() != null && !dto.getChecklistId().equals(entity.getChecklist().getId())) {
            CheckListEntity checklist = checkListRepository.findById(dto.getChecklistId())
                    .orElseThrow(() -> new NoSuchElementException("Checklist not found"));

            if (!checklist.getUser().getId().equals(userId)) {
                throw new AccessDeniedException("You do not have permission to assign this item to another checklist.");
            }

            entity.setChecklist(checklist);
        }

        CheckListItemEntity updated = itemRepository.save(entity);
        return itemMapper.toDTO(updated);
    }


    public void deleteIfOwned(Long id, Long userId) {
        CheckListItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found."));

        if (!entity.getChecklist().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this item.");
        }

        itemRepository.delete(entity);
    }


    public Page<CheckListItemResponseDTO> findByChecklistAndStatus(Long checklistId, boolean completed, Pageable pageable) {
        return itemRepository.findByChecklistIdAndStatus(checklistId, completed, pageable)
                .map(itemMapper::toDTO);
    }

    public Page<CheckListItemResponseDTO> findByStatus(boolean completed, Pageable pageable) {
        return itemRepository.findByStatus(completed, pageable)
                .map(itemMapper::toDTO);
    }

    public Page<CheckListItemResponseDTO> findByUserIdWithFilters(Long userId, CheckListItemFilterDTO filters, Pageable pageable) {
        Specification<CheckListItemEntity> spec = Specification.where(
                (root, query, cb) -> cb.equal(root.get("checklist").get("user").get("id"), userId)
        );

        if (filters.getChecklistId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("checklist").get("id"), filters.getChecklistId()));
        }

        if (filters.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), filters.getStatus()));
        }

        Page<CheckListItemEntity> page = itemRepository.findAll(spec, pageable);
        return page.map(itemMapper::toDTO);
    }
}
