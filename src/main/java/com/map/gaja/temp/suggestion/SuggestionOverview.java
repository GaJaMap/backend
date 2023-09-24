package com.map.gaja.temp.suggestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionOverview {
    private Long id;
    private String email;
    private String title;
    private int recommendedCount;
    private LocalDateTime suggestionTime;
}
