package lld.bookmyshow.service;

import lld.bookmyshow.model.*;

import java.util.List;

/**
 * Search/filter over the show catalog. A real system would index this; here a
 * simple in-memory filter is enough to demonstrate the API.
 */
public class SearchService {

    private final List<Show> shows;

    public SearchService(List<Show> shows) {
        this.shows = List.copyOf(shows);
    }

    public List<Show> byMovie(String movieTitle) {
        return shows.stream()
                .filter(s -> s.movie().title().equalsIgnoreCase(movieTitle))
                .toList();
    }

    public List<Show> byCity(String city) {
        return shows.stream()
                .filter(s -> s.city().equalsIgnoreCase(city))
                .toList();
    }

    public List<Show> byMovieAndCity(String movieTitle, String city) {
        return shows.stream()
                .filter(s -> s.movie().title().equalsIgnoreCase(movieTitle))
                .filter(s -> s.city().equalsIgnoreCase(city))
                .toList();
    }
}
