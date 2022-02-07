package com.geekbrains.test;

// POJO constructor(), get, set

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectingResponse {

    private String status;

    private String username;

    private String spoonacularPassword;

    private String hash;

}
