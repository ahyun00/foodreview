package org.foodreview;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.foodreview.place.Place;
import org.foodreview.place.PlaceMapper;
import org.foodreview.place.PlaceService;
import org.junit.jupiter.api.Test;


public class PlaceServiceTest {
	
	@Test
	void 페이지번호에따른_offset_계산() {
		
		PlaceMapper placeMapper = mock(PlaceMapper.class);
		
		PlaceService placeService = new PlaceService(placeMapper);
		
		placeService.getPlaceList(null, "카페", 3);
		
		verify(placeMapper).getPlaceList(null, "카페", 9, 18);
		
	}
	
	@Test
	void 첫번째_페이지_offset_계산() {
		PlaceMapper placeMapper = mock(PlaceMapper.class);
		PlaceService placeService = new PlaceService(placeMapper);
		placeService.getPlaceList(null, "카페", 1);
		verify(placeMapper).getPlaceList(null, "카페", 9, 0);
	}
	
	@Test
	void 두번째_페이지_offset_계산() {
		PlaceMapper placeMapper = mock(PlaceMapper.class);
		PlaceService placeService = new PlaceService(placeMapper);
		placeService.getPlaceList(null, "카페", 2);
		verify(placeMapper).getPlaceList(null, "카페", 9, 9);
	}
	
	@Test
	void 가게번호로_상세조회() {
		PlaceMapper placeMapper = mock(PlaceMapper.class);
		PlaceService placeService = new PlaceService(placeMapper);
		placeService.getPlace(5);
		verify(placeMapper).getPlace(5);
	}
	
	@Test
	void 가게상세조회_결과반환() {
		PlaceMapper placeMapper = mock(PlaceMapper.class);
		PlaceService placeService = new PlaceService(placeMapper);
		
		Place place = new Place();
		
		when(placeMapper.getPlace(5)).thenReturn(place);
		
		Place result = placeService.getPlace(5);
		
		assertSame(place, result);
		
		
	}

	
}

















