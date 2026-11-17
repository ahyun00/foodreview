package org.foodreview.place;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlaceMapper {
	
	// 가게 목록 조회 + 이름 검색 + 카테고리 필터 + 페이징
	List<Place> getPlaceList(@Param("keyword") String keyword,
											@Param("category") String category,
											@Param("size") int size,
											@Param("offset") int offset				
			);
	
	// 가게 상세 조회
	Place getPlace(@Param("pno") Integer pno);
	
	
	// 검색 및 카테고리 조건에 맞는 전체 맛집 개수 조회
	int countPlaces(
			@Param("keyword") String keyword, 
			@Param("category") String category
	);
	
}
