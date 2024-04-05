package com.tech.haven.helpers;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageableResponse<T> {

	private List<T> content;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private int pageNumber;
	private boolean last;

}
