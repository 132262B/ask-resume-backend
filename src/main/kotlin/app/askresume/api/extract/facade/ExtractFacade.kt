package app.askresume.api.extract.facade

import app.askresume.api.extract.vo.ExtractedTextResponse
import app.askresume.domain.manager.service.PdfManagerService
import app.askresume.global.annotation.Facade
import app.askresume.global.util.LoggerUtil.logger
import org.springframework.web.multipart.MultipartFile

@Facade
class ExtractFacade(
    private val pdfManagerService: PdfManagerService
) {

    val log = logger()

    fun pdfToText(file: MultipartFile): ExtractedTextResponse {
        return ExtractedTextResponse(
            pdfManagerService.pdfToText(file)
        )
    }
}