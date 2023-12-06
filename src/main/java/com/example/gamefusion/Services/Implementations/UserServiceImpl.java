package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Repository.UserRepository;
import com.example.gamefusion.Services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public UserDto save(UserDto newUser) {
        User user = new User();
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setPhone(newUser.getPhone());
        user.setUsername(newUser.getUsername());
        user.setRole("USER");
        user.setIsActive(true);
        user.setReferralCode(UUID.randomUUID());
        user.setPassword(
                passwordEncoder.encode(newUser.getPassword())
        );
        return conversionUtil.entityToDto(repository.save(user));
    }

    @Override
    public User update(UserDto userDto) {
        User user = conversionUtil.dtoToEntity(userDto);
        return repository.save(user);
    }

    @Override
    public UserDto findByUsername(String receiver) {
        if (isExistsByUsername(receiver)) {
            User user = repository.findByUsername(receiver);
            return conversionUtil.entityToDto(user);
        }
        throw new EntityNotFoundException("User with this username is not fount"+receiver);
    }

    @Override
    public UserDto findById(Integer id) {
        Optional<User> user = repository.findById(id);
        if (user.isPresent())
            return conversionUtil.entityToDto(user.get());
        else
            throw new EntityNotFoundException("User not found");
    }

    @Override
    public Boolean isExistsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Boolean isExistsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public Boolean isBlocked(Integer id) {
        return repository.findIsActiveById(id);
    }

    @Override
    public PaginationInfo findAllUsers(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<User> users = repository.findUsersByRole("USER",pageable);
        List<User> listOfUser = users.getContent();
        List<UserDto> contents = listOfUser.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                contents,users.getNumber(),users.getSize(),
                users.getTotalElements(),users.getTotalPages(),
                users.isLast(),users.hasNext()
        );
    }

    @Override
    public void block(Integer id) {
        if (isExistsById(id) && isBlocked(id)) {
            repository.blockUser(id);
        }
    }

    @Override
    public void unBlock(Integer id) {
        if (isExistsById(id) && !isBlocked(id)) {
            repository.unBlockUser(id);
        }
    }

    @Override
    @Transactional
    public void resetPassword(Integer id, String password) {
        if (isExistsById(id)) {
            repository.updatePasswordById( id,
                    passwordEncoder.encode(password)
            );
        }
    }

    @Override
    public Boolean isUserExistsByReferralCode(UUID referral) {
        return repository.existsByReferralCode(referral);
    }

    @Override
    public UserDto findUserByReferralCode(UUID referral) {
        return conversionUtil.entityToDto(repository.findByReferralCode(referral));
    }
}
