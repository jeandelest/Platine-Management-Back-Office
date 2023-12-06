package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Support;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SupportDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
@RequiredArgsConstructor
public class SupportController {

    private final ModelMapper modelmapper;

    private final SupportService supportService;

    @Operation(summary = "Search for supports, paginated")
    @GetMapping(value = Constants.API_SUPPORTS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SupportPage.class)))
    })
    public ResponseEntity<?> getSupports(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Support> pageSupport = supportService.findAll(pageable);
        List<SupportDto> listSupports = pageSupport.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(new SupportPage(listSupports, pageable, pageSupport.getTotalElements()));
    }

    @Operation(summary = "Search for a support by its id")
    @GetMapping(value = Constants.API_SUPPORTS_ID, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SupportDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<?> getSupport(@PathVariable("id") String id) {
        Support support = supportService.findById(id);
        return ResponseEntity.ok().body(convertToDto(support));

    }

    @Operation(summary = "Update or create a support")
    @PutMapping(value = Constants.API_SUPPORTS_ID, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SupportDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = SupportDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<?> putSupport(@PathVariable("id") String id, @RequestBody SupportDto supportDto) {
        if (!supportDto.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id and support id don't match");
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(supportDto.getId()).toUriString());
        HttpStatus httpStatus;
        Support supportBase;
        try {
            supportBase = supportService.findById(id);
            log.info("Update support with the id {}", supportDto.getId());
            httpStatus = HttpStatus.OK;

        } catch (NotFoundException e) {
            log.info("Create support with the id {}", supportDto.getId());
            httpStatus = HttpStatus.CREATED;
        }


        Support support = supportService.insertOrUpdateSupport(convertToEntity(supportDto));
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToDto(support));
    }

    private SupportDto convertToDto(Support support) {
        return modelmapper.map(support, SupportDto.class);
    }

    private Support convertToEntity(SupportDto supportDto) {
        return modelmapper.map(supportDto, Support.class);
    }

    class SupportPage extends PageImpl<SupportDto> {

        public SupportPage(List<SupportDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
