package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Repository.UserRepository;
import com.example.gamefusion.Services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EntityDtoConversionUtil conversionUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public void save(UserDto newUser) {
        User user = new User();
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setPhone(newUser.getPhone());
        user.setUsername(newUser.getUsername());
        user.setRole("USER");
        user.setIsActive(true);
        user.setPassword(
                passwordEncoder.encode(newUser.getPassword())
        );
        userRepository.save(user);
    }

    @Override
    public void update(UserDto userDto) {
        User user = conversionUtil.dtoToEntity(userDto);
        userRepository.save(user);
    }

    @Override
    public UserDto findByUsername(String receiver) {
        User user = userRepository.findByUsername(receiver);
        return conversionUtil.entityToDto(user);
    }

    @Override
    public UserDto findById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent())
            return conversionUtil.entityToDto(user.get());
        else
            throw new EntityNotFoundException("User not found");
    }

    @Override
    public Boolean isExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean isExistsById(Integer id) {
        return userRepository.existsById(id);
    }

    @Override
    public Boolean isBlocked(Integer id) {
        return userRepository.findIsActiveById(id);
    }

    @Override
    public PaginationInfo findAllUsers(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<User> users = userRepository.findUsersByRole("USER",pageable);
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
            userRepository.blockUser(id);
        }
    }

    @Override
    public void unBlock(Integer id) {
        if (isExistsById(id) && !isBlocked(id)) {
            userRepository.unBlockUser(id);
        }
    }
}
