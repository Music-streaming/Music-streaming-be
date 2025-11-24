package com.example.Music_streaming.album;

import com.example.Music_streaming.album.dto.AlbumDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/{albumId}")
    public AlbumDetailResponse getAlbum(@PathVariable String albumId) {
        return albumService.getAlbum(albumId);
    }
}
