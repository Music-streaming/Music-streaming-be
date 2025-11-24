package com.example.Music_streaming.artist;

import com.example.Music_streaming.artist.dto.ArtistAlbumResponse;
import com.example.Music_streaming.artist.dto.ArtistResponse;
import com.example.Music_streaming.artist.dto.ArtistTopTrackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/{artistId}")
    public ArtistResponse getArtist(@PathVariable String artistId) {
        return artistService.getArtist(artistId);
    }

    @GetMapping("/{artistId}/top-tracks")
    public List<ArtistTopTrackResponse> topTracks(@PathVariable String artistId) {
        return artistService.getTopTracks(artistId);
    }

    @GetMapping("/{artistId}/albums")
    public List<ArtistAlbumResponse> albums(@PathVariable String artistId) {
        return artistService.getAlbums(artistId);
    }
}
