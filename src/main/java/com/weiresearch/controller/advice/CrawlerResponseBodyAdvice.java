package com.weiresearch.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiresearch.pojo.ApplicationPage;
import com.weiresearch.pojo.ApplicationResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CrawlerResponseBodyAdvice implements ResponseBodyAdvice {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerResponseBodyAdvice.class);

	// 设置需要进行后置处理的接口
	@Override
	public boolean supports(MethodParameter returnType, Class converterType) {

		// 任何接口返回值均需要处理
		return true;
	}

	// 接口后置处理，这里用于统一个格式化响应体
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

		// 抛出异常的接口响应已在下面的全局异常处理方法中进行过格式化，可直接返回
		// 下载文件接口无需格式化，可直接返回
		if (body instanceof ApplicationResponse || body instanceof UrlResource) {
			return body;
		} else {
			if (body instanceof Page) {
				body = new ApplicationPage((Page) body);
			}

			ApplicationResponse ret = new ApplicationResponse(ApplicationResponse.CODE_SUCCESS, body);

			// http://www.voidcn.com/article/p-pzmzleow-bou.html
			if (body instanceof String) {
				ObjectMapper mapper = new ObjectMapper();

				// Convert object to JSON string
				try {
					return mapper.writeValueAsString(ret);
				} catch (JsonProcessingException e) {
					logger.error("JSON parse error", e);

					return null;
				}
			}

			return ret;
		}
	}

	// 全局异常处理方法，将根据异常类型对响应体进行特殊包装，设置相应 code
	// http://blog.csdn.net/liujia120103/article/details/75126124
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public ApplicationResponse handleRuntimeException(HttpServletRequest request, Exception e) throws Exception {
		logger.error("Runtime Exception", e);

		return new ApplicationResponse(ApplicationResponse.CODE_FAIL, e.getMessage());
	}

}
