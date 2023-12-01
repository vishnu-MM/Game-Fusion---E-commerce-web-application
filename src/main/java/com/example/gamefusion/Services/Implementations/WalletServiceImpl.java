package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.WalletRepository;
import com.example.gamefusion.Repository.UserRepository;
import com.example.gamefusion.Services.WalletService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Entity.Wallet;
import com.example.gamefusion.Dto.WalletDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.User;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final UserRepository userRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public WalletServiceImpl(WalletRepository repository,
                             UserRepository userRepository,
                             EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public List<WalletDto> findByUser(UserDto userDto) {
        if (repository.existsByUser(conversionUtil.dtoToEntity(userDto)))
            return repository.findByUser(conversionUtil.dtoToEntity(userDto))
                    .stream().map(conversionUtil::entityToDto).toList();
        throw new EntityNotFoundException("Wallet with this user not exits");
    }

    @Override
    public Boolean existByUser(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent())
            return repository.existsByUser(user.get());
        throw new EntityNotFoundException("User with this id, not exits");
    }

    @Override
    public Boolean existById(Long walletId) {
        return repository.existsById(walletId);
    }

    @Override
    public WalletDto findById(Long walletId) {
        if (existById(walletId))  repository.findById(walletId) ;
        throw new EntityNotFoundException("Wallet with this id doesnt exist");
    }

    @Override
    public WalletDto save(WalletDto walletDto) {
        return conversionUtil.entityToDto(
            repository.save( conversionUtil.dtoToEntity(walletDto) )
        );
    }

    @Override
    public double getWalletBalance(UserDto userDto) {
        User user = conversionUtil.dtoToEntity(userDto);
        double balance = 0;
        List<Wallet> walletList = new ArrayList<>();
        if(!repository.existsByUser(user))
            return balance;

        walletList = repository.findByUser(user);
        for (Wallet wallet : walletList) {
            if (Objects.equals(wallet.getTransactionType(),"Credit"))
                balance += wallet.getAmount();
            else if (Objects.equals(wallet.getTransactionType(),"Debit"))
                balance -= wallet.getAmount();
        }
        return balance;
    }
}
