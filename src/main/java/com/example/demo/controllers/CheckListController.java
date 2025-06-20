package com.example.demo.controllers;

import com.example.demo.DTOs.CheckList.Request.CheckListCreateDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListUpdateDTO;
import com.example.demo.DTOs.Filter.CheckListFilterDTO;
import com.example.demo.DTOs.GlobalError.ErrorResponseDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.controllers.hateoas.CheckListModelAssembler;
import com.example.demo.exceptions.OwnershipException;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.CheckListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


@Tag(name = "Checklists", description = "Operations related to user travel checklists")
@RestController
@RequestMapping("/checklists")
public class CheckListController {

    private final CheckListService checkListService;
    private final CheckListModelAssembler assembler;
    private final PagedResourcesAssembler<CheckListResponseDTO> pagedResourcesAssembler;

    @Autowired
    public CheckListController(CheckListService checkListService, CheckListModelAssembler assembler, PagedResourcesAssembler<CheckListResponseDTO> pagedResourcesAssembler) {
        this.checkListService = checkListService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(
            summary = "Create a new checklist",
            description = "Creates a new checklist for a specific user and trip.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Checklist data",
                    content = @Content(schema = @Schema(implementation = CheckListCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Checklist successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CheckListResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - User not authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('CREAR_CHECKLIST')")
    @PostMapping
    public ResponseEntity<CheckListResponseDTO> create(
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myUserId = credential.getUser().getId();
        CheckListResponseDTO created = checkListService.create(dto, myUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @Operation(
            summary = "Update a checklist by ID",
            description = "Updates a checklist with new data, including its name, trip and status.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Checklist update data",
                    content = @Content(schema = @Schema(implementation = CheckListUpdateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Checklist updated successfully",
                    content = @Content(schema = @Schema(implementation = CheckListResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Checklist not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('MODIFICAR_CHECKLIST')")
    @PutMapping("/{id}")
    public ResponseEntity<CheckListResponseDTO> update(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListUpdateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();

        CheckListResponseDTO updated = checkListService.update(id, dto, userId);
        return ResponseEntity.ok(updated);
    }


    @Operation(
            summary = "Get a checklist by ID",
            description = "Retrieves a checklist by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Checklist found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CheckListResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Checklist not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('VER_CHECKLIST')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CheckListResponseDTO>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential
    ) {
        Long userId = credential.getUser().getId();

        boolean isAdmin = credential.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        CheckListResponseDTO checklist;

        if (isAdmin) {
            checklist = checkListService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Checklist not found"));
        } else {
            checklist = checkListService.findByIdIfOwned(id, userId);
        }

        return ResponseEntity.ok(assembler.toModel(checklist));
    }


    @Operation(
            summary = "Get all checklists",
            description = "Returns all checklists in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of checklists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CheckListResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - User not authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('VER_TODOS_CHECKLIST')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CheckListResponseDTO>>> getAll(Pageable pageable) {
        Page<CheckListResponseDTO> checklists = checkListService.findAll(pageable);
        PagedModel<EntityModel<CheckListResponseDTO>> model = pagedResourcesAssembler.toModel(checklists, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get all inactive checklists",
            description = "Retrieves a paginated list of all inactive checklists in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Checklists retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CheckListResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('VER_TODOS_CHECKLIST')")
    @GetMapping("/inactive")
    public ResponseEntity<PagedModel<EntityModel<CheckListResponseDTO>>> getAllInactive(Pageable pageable) {
        Page<CheckListResponseDTO> checklists = checkListService.findAllInactive(pageable);
        PagedModel<EntityModel<CheckListResponseDTO>> model = pagedResourcesAssembler.toModel(checklists, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get all checklists by user ID",
            description = "Retrieves all checklists created by the specified user. Supports optional filters."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of checklists for the user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CheckListResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - User not authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User not authorized to access this resource",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or has no checklists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('VER_CHECKLIST_USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedModel<EntityModel<CheckListResponseDTO>>> getByUser(
            @PathVariable Long userId,
            @ModelAttribute CheckListFilterDTO filters,
            @AuthenticationPrincipal CredentialEntity credential,
            Pageable pageable) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
            throw new OwnershipException("You do not have permission to access this resource.");
        }

        Page<CheckListResponseDTO> checklists = checkListService.findByUserIdWithFilters(userId, filters, pageable);
        PagedModel<EntityModel<CheckListResponseDTO>> model = pagedResourcesAssembler.toModel(checklists, assembler);
        return ResponseEntity.ok(model);
    }


    @Operation(
            summary = "Delete a checklist by ID",
            description = "Deletes the specified checklist and all its items."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Checklist deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Checklist not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('ELIMINAR_CHECKLIST')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();
        checkListService.deleteIfOwned(id, userId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Restore a checklist",
            description = "Reactivates a checklist that was previously deleted (soft-deleted) by setting its status to active."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Checklist restored successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Checklist not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('RESTAURAR_CHECKLIST')")
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restore(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();
        checkListService.restoreIfOwned(id, userId);
        return ResponseEntity.noContent().build();
    }
}