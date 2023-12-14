package com.example.gamefusion.Configuration.UtilityClasses;

import com.example.gamefusion.Dto.PaginationInfo;
import java.util.List;

public class PageToListUtil<T> {
    @SuppressWarnings("unchecked")
     public List<T> convert(PaginationInfo paginationInfo) {
        return (List<T>) paginationInfo.getContents();
    }
}
