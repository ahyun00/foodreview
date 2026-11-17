package org.foodreview.place;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PlaceController {
	
	private final PlaceService placeService;
	
	// 가게 목록 + 이름 검색 + 카테고리 필터 + 페이징
	@GetMapping("/places")
	public String placeList(
			@RequestParam(value= "keyword", required= false) String keyword,
			@RequestParam(value= "category", required= false) String category,
			@RequestParam(value= "page", defaultValue= "1") int page,
			Model model) {
		
		if (page < 1) { page =1;}
		
		// 검색 및 카테고리 조건에 맞는 전체 맛집 개수
		int totalCount = placeService.countPlaces(keyword, category);
		
		// 전체 페이지 수 계산
		int totalPages = (int) Math.ceil(
				(double) totalCount / PlaceService.PAGE_SIZE
		);
		
		// 요청한 페이지가 마지막 페이지보다 크면 마지막 페이지로 변경
		if ( totalPages > 0 && page > totalPages) {
			page = totalPages;
		}
				
		// 검증된 페이지 번호로 맛집 목록 조회
		List<Place> places = placeService.getPlaceList(keyword, category, page);		
		
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPages", totalPages);
		
		model.addAttribute("placeList", places);	
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", category);
		model.addAttribute("page", page);
		
		return "/place/list";
	}
	
	// 가게 상세 조회
	@GetMapping("/places/{pno}")
	public String placeDetail(@PathVariable("pno") Integer pno, Model model) {
		
		Place place = placeService.getPlace(pno);
		
		model.addAttribute("place", place);
		
		return "/place/detail";
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
