package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Dto.WalletDto;
import java.util.List;

public interface WalletService {
    List<WalletDto> findByUser(UserDto userDto);
    Boolean existByUser(Integer userId);

    Boolean existById(Long walletId);

    WalletDto findById(Long id);
    WalletDto save(WalletDto walletDto);

    double getWalletBalance(UserDto userDto);
}
