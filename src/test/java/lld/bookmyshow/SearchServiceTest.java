package lld.bookmyshow;

import lld.bookmyshow.model.*;
import lld.bookmyshow.service.*;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchServiceTest {

    private final Movie inception = new Movie("M1", "Inception");
    private final Movie dunkirk = new Movie("M2", "Dunkirk");
    private final Instant time = Instant.parse("2026-01-01T18:00:00Z");

    private final Show inceptionNyc = new Show("S1", inception, "NYC", "AMC", time, List.of(new Seat("A1")));
    private final Show inceptionLa = new Show("S2", inception, "LA", "Regal", time, List.of(new Seat("A1")));
    private final Show dunkirkNyc = new Show("S3", dunkirk, "NYC", "AMC", time, List.of(new Seat("A1")));

    private final SearchService search =
            new SearchService(List.of(inceptionNyc, inceptionLa, dunkirkNyc));

    @Test
    void byMovieReturnsAllCitiesForThatMovie() {
        List<Show> result = search.byMovie("Inception");
        assertEquals(2, result.size());
        assertTrue(result.contains(inceptionNyc) && result.contains(inceptionLa));
    }

    @Test
    void byCityReturnsAllMoviesInThatCity() {
        List<Show> result = search.byCity("NYC");
        assertEquals(2, result.size());
        assertTrue(result.contains(inceptionNyc) && result.contains(dunkirkNyc));
    }

    @Test
    void byMovieAndCityNarrowsToBoth() {
        List<Show> result = search.byMovieAndCity("Inception", "NYC");
        assertEquals(List.of(inceptionNyc), result);
    }
}
