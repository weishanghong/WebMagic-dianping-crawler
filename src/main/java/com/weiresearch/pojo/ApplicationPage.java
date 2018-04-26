package com.weiresearch.pojo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public class ApplicationPage<E> {

	private List<E> content;

	private boolean first;

	private boolean last;

	private int number;

	private int numberOfElements;

	private int size;

	private Sort sort;

	private long totalElements;

	private int totalPages;

	public ApplicationPage(Page<E> page) {
		this.content = page.getContent();
		this.first = page.isFirst();
		this.last = page.isLast();
		this.number = page.getNumber() + 1;
		this.numberOfElements = page.getNumberOfElements();
		this.size = page.getSize();
		this.sort = page.getSort();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
	}

	public List<E> getContent() {
		return content;
	}

	public void setContent(List<E> content) {
		this.content = content;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumberOfElements() {
		return numberOfElements;
	}

	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

}
