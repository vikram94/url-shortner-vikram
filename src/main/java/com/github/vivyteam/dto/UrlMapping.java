package com.github.vivyteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("url_mappings")
public class UrlMapping {

    @Id
    private Long id;
    private String longUrl;
    private String shortUrl;

}
