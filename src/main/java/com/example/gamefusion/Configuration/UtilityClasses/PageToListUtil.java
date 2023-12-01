package com.example.gamefusion.Configuration.UtilityClasses;

import com.example.gamefusion.Dto.PaginationInfo;
import java.util.List;

public class PageToListUtil<T> {
    public List<T> convert(PaginationInfo paginationInfo) {
        return (List<T>) paginationInfo.getContents();
    }
}
