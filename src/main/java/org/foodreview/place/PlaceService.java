package org.foodreview.place;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
	
	private final PlaceMapper placeMapper;
	
	// 가게 목록 조회  + 이름 검색 + 카테고리 필터 + 페이징
	public List<Place> getPlaceList(String keyword, String category, int page) {
		
	    // 페이지 번호를 이용해 건너뛸 데이터 개수 계산
	    int offset = ( page -1 ) * PAGE_SIZE;
		
		return placeMapper.getPlaceList(keyword, category, PAGE_SIZE, offset);
	}
	
	
	// 한 페이지에 표시할 맛집 개수
	public static final int PAGE_SIZE = 9;
	
	
	// 가게 상세 조회
	public Place getPlace(Integer pno) {
		return placeMapper.getPlace(pno);
	}
	
	// 검색 및 카테고리 조건에 맞는 전체 맛집 개수 조회
	public int countPlaces(String keyword, String category) {
		return placeMapper.countPlaces(keyword, category);
	}
	
	// 메인 페이지에 표시할 가게 6개 조회
	public List<Place> getHomePlaces() {
		return placeMapper.getPlaceList(null, null, 6, 0);
	}

}













