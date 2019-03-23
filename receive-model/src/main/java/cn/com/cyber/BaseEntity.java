package cn.com.cyber;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseEntity {
	private String signature;
	
	private int pageNum;//第几页
	
	private int pageSize;//一页几行
	
	private String column;//排序 字段
	
	private String sort;//desc 降序  asc 升序
	


	@JsonIgnore
	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	@JsonIgnore
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	@JsonIgnore
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	@JsonIgnore
	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}
	@JsonIgnore
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
}