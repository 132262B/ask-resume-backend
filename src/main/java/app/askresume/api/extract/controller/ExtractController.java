package app.askresume.api.extract.controller;

import app.askresume.api.extract.dto.response.ExtractedTextResponse;
import app.askresume.api.extract.service.ExtractService;
import app.askresume.api.extract.validator.ExtractValidator;
import app.askresume.global.model.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Tag(name = "extract", description = "특정 Input을 text로 추출하는 API")
@RestController
@RequestMapping("/api/extract")
@RequiredArgsConstructor
public class ExtractController {

    private final ExtractService extractService;
    private final ExtractValidator extractValidator;


    @Tag(name = "extract")
    @Operation(summary = "이력서를 Text로 전환 API", description = "이력서를 Text로 전환 API")
    @ApiResponses({
            @ApiResponse(responseCode = "500", description = "서버 오류 발생(관리자 문의)"),
            @ApiResponse(responseCode = "500", description = "(F-002) 파일에서 TEXT를 읽는 도중 에러가 발생하였습니다."),
            @ApiResponse(responseCode = "400", description = "(F-001) 허가된 CONTENT_TYPE이 아닙니다.")
    })
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<ExtractedTextResponse>> extractTextFromPdf(@Parameter(name = "resume", description = "이력서PDF파일, maxSize: 3MB, 확장자: pdf)", required = true)
                                                                           @RequestPart("resume") MultipartFile file
    ) throws IOException {
        extractValidator.validateContentType(file.getContentType());
        return ResponseEntity.ok(new ApiResult<>(extractService.pdfToText(file)));
    }

    @Tag(name = "extract")
    @Operation(summary = "Link를 Text로 전환하는 API", description = "Link를 Text로 전환하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "500", description = "서버 오류 발생(관리자 문의)")
    })
    @GetMapping("/link")
    public ResponseEntity<ApiResult<ExtractedTextResponse>> scrapeWebPage(@RequestParam String url) {
        return ResponseEntity.ok(new ApiResult<>(extractService.linkToText(url)));
    }

}
