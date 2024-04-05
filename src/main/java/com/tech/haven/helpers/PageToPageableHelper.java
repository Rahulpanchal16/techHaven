package com.tech.haven.helpers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

public class PageToPageableHelper {

	public static <U, V> PageableResponse<V> getPageableResponse(Page<U> page, Class<V> type) {
		List<U> entityList = page.getContent();
		List<V> entityDtoList = entityList.stream().map((object) -> new ModelMapper().map(object, type))
				.collect(Collectors.toList());
		PageableResponse<V> pageableResponse = new PageableResponse<>();
		pageableResponse.setContent(entityDtoList);
		pageableResponse.setLast(page.isLast());
		pageableResponse.setPageNumber(page.getNumber());
		pageableResponse.setPageSize(page.getSize());
		pageableResponse.setTotalElements(page.getTotalElements());
		pageableResponse.setTotalPages(page.getTotalPages());

		return pageableResponse;

	}

}
