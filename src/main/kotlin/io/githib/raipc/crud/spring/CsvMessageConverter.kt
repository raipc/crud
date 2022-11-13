package io.githib.raipc.crud.spring


import io.githib.raipc.crud.convertrequest.ConvertRequestDto
import io.githib.raipc.crud.util.BeanCsvWriter
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets


@Component
class CsvMessageConverter : AbstractHttpMessageConverter<List<ConvertRequestDto>>(MEDIA_TYPE) {
    private val beanCsvWriter = BeanCsvWriter.create<ConvertRequestDto>(ConvertRequestDto::class)

    override fun supports(clazz: Class<*>): Boolean {
        return List::class.java.isAssignableFrom(clazz)
    }

    override fun readInternal(clazz: Class<out List<ConvertRequestDto>>, inputMessage: HttpInputMessage): List<ConvertRequestDto> {
        throw UnsupportedOperationException()
    }

    override fun writeInternal(response: List<ConvertRequestDto>, output: HttpOutputMessage) {
        output.headers.contentType = MEDIA_TYPE
        output.headers["Content-Disposition"] = "attachment; filename=\"requests.csv\""
        beanCsvWriter.writeBeans(response, output.body)
    }

    companion object {
        val MEDIA_TYPE: MediaType = MediaType("text", "csv", StandardCharsets.UTF_8)
    }
}