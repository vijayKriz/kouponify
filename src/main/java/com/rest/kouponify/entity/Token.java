package com.rest.kouponify.entity;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Token {

    private String token;

}
