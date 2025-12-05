package com.family_tasks.tracker.utils;

import lombok.Data;

import java.util.List;

@Data
public class SliceWrapper<T>  {

    private List<T> content;
    private int number;
    private int size;
    private Boolean last;
}