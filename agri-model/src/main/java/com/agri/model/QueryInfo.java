package com.agri.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryInfo implements Serializable {

    long pagesize;

    long pagenum;

    String obscure;
}
