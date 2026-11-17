package org.foodreview;

import java.util.List;

import org.foodreview.place.Place;
import org.foodreview.place.PlaceService;
import org.foodreview.review.Review;
import org.foodreview.review.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final PlaceService placeService;
	private final ReviewService reviewService;
	
	@GetMapping("/")
	public String home(Model model) {
		
		// 메인 페이지에 표시할 가게
		List<Place> places = placeService.getHomePlaces();
		model.addAttribute("homePlaces", places);
		
		// 메인 페이지에 표시할 최신 리뷰
		List<Review> reviews = reviewService.getHomeReviews();
		model.addAttribute("homeReviews", reviews);
		
		return "index";
	}
}
